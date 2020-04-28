package com.example.solrDemo.controller;

import com.example.solrDemo.controller.model.ResponseCode;
import com.example.solrDemo.controller.model.ResponseModel;
import com.example.solrDemo.entity.CompanyEntity;
import com.example.solrDemo.entity.UserEntity;
import com.example.solrDemo.service.CompanyService;
import com.example.solrDemo.service.SolrService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.RandomUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author Administrator
 * @create 2020-04-28
 * @since 1.0.0
 */
@RestController
@RequestMapping("/v1/company")
public class CompanyController extends BaseController {

    @Value("${spring.data.solr.coreName}")
    private String coreName;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private SolrClient client;

    @RequestMapping(value = "/create", method = {RequestMethod.GET})
    public ResponseModel create() {
        try {
            CompanyEntity companyEntity = new CompanyEntity();
            companyEntity.setName("test" + RandomUtils.nextInt());
            companyEntity.setCreditCode("123" + RandomUtils.nextInt());
            companyEntity.setHilightName("高亮" + RandomUtils.nextInt());
            CompanyEntity entity = companyService.create(companyEntity);
            SolrInputDocument doc = new SolrInputDocument();
            doc.setField("id", entity.getId());
            doc.setField("md_name", entity.getName());
            doc.setField("hilightName", entity.getHilightName());
            client.add(doc);
            client.commit();
            return new ResponseModel(new Date().getTime(), entity, ResponseCode._200, "");
        } catch (Exception e) {
            return this.buildHttpReslutForException(e);
        }
    }


    @Autowired
    SolrService solrService;

    @RequestMapping(value = "/findByIdFromSolr", method = {RequestMethod.GET})
    public ResponseModel findByIdFromSolr(String id) {
        try {
//            Map<String, Object> map = solrService.searchInfoByKeyWord("test");
            //根据id查询内容
            SolrDocument solrDocument = client.getById(id);
            //获取filedName
            Collection<String> fieldNames = solrDocument.getFieldNames();
            System.out.println(fieldNames);
            //获取file名和内容
            Map<String, Object> fieldValueMap = solrDocument.getFieldValueMap();
            System.out.println(fieldValueMap);

            //获取childDocuments
            List<SolrDocument> childDocuments = solrDocument.getChildDocuments();
            System.out.println(childDocuments);
            List<Map<String, Object>> list = new ArrayList<>();
            list.add(fieldValueMap);
            return new ResponseModel(new Date().getTime(), list, ResponseCode._200, "");
        } catch (Exception e) {
            return this.buildHttpReslutForException(e);
        }
    }

    @RequestMapping(value = "/insertSolrIndex", method = {RequestMethod.GET})
    public ResponseModel insertSolrIndex(String id) {
        try {
//            可以批量增加或者单个增加，注意实体类中需要
            List<CompanyEntity> all = companyService.findAll();
            client.addBeans(all);
            client.commit();
            return new ResponseModel(new Date().getTime(), null, ResponseCode._200, "");
        } catch (Exception e) {
            return this.buildHttpReslutForException(e);
        }
    }

    @RequestMapping(value = "/deleteSolrIndex", method = {RequestMethod.GET})
    public ResponseModel deleteSolrIndex(String id) {
        try {
//            id正确，接口正常
            List<String> list = new ArrayList<>();
            list.add(id);
//            solrService.delIndexs(coreName, list);
            solrService.delAll(coreName);
            return new ResponseModel(new Date().getTime(), null, ResponseCode._200, "");
        } catch (Exception e) {
            return this.buildHttpReslutForException(e);
        }
    }

    @ApiOperation(value = "查询文档内容-方式一", notes = "复杂查询", httpMethod = "GET")
    @RequestMapping(value = "queryDocument", method = RequestMethod.GET)
    public Object queryDocument(@ApiParam(value = "条件", defaultValue = "*:*") @RequestParam String condition,
                                @ApiParam(value = "core/默 corename 库", defaultValue = "meta_db") @RequestParam String collection,
                                @ApiParam(value = "分页起始 默 0", defaultValue = "0") @RequestParam Integer pageStart,
                                @ApiParam(value = "分页结束 默 10", defaultValue = "10") @RequestParam Integer pageEnd) throws Exception {
        // 创建一个查询条件
        SolrQuery solrQuery = new SolrQuery();
        // 设置查询条件
        solrQuery.setQuery(condition);
        // 设置分页
        solrQuery.setStart(pageStart);
        solrQuery.setRows(pageEnd);
        //排序
//        solrQuery.setSort("id",SolrQuery.ORDER.asc);

        /*// df 代表默认的查询字段
        solrQuery.set("name", "关键字");
        //   指的是你查询完毕之后要返回的字段
        solrQuery.set("name", "id,name");
        //高亮
        //打开开关
        solrQuery.setHighlight(false);
        solrQuery.addHighlightField("name"); // 高亮字段

        //设置前缀
        solrQuery.setHighlightSimplePre("<font color=\"red\">");
        //设置后缀
        solrQuery.setHighlightSimplePost("</font>");*/

        // 执行查询
//        QueryResponse query = client.query(collection,solrQuery);
        QueryResponse query = client.query(solrQuery);
        // 取查询结果
        SolrDocumentList solrDocumentList = query.getResults();

        System.out.println("总记录数：" + solrDocumentList.getNumFound());
        client.commit(collection);
        return solrDocumentList;
    }


    @RequestMapping(value = "/findByQuery", method = {RequestMethod.GET}, produces = "application/json;utf-8")
    public ResponseModel findByQuery() {
        try {
            // 创建一个查询条件
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery("*:*");
            solrQuery.setStart(0);
            solrQuery.setRows(10);
            QueryResponse queryResponse = client.query(solrQuery);
            // 取查询结果
            SolrDocumentList solrDocumentList = queryResponse.getResults();
            List<Map<String, Object>> lists = new ArrayList<>();
            Map<String, Object> fieldValueMap = null;
            for (SolrDocument document : solrDocumentList) {
                fieldValueMap = document.getFieldValueMap();
                Map<String, Object> result = new HashMap<>();
                Object name = fieldValueMap.get("name");
                result.put("name", name);

                lists.add(result);
            }
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//            String s = mapper.writeValueAsString(fieldValueMap);
//            System.out.println(lists);
//            return s;
            return new ResponseModel(new Date().getTime(), lists, ResponseCode._200, "");
        } catch (Exception e) {
            return this.buildHttpReslutForException(e);
//            e.printStackTrace();
//            return "dd";
        }
    }
}