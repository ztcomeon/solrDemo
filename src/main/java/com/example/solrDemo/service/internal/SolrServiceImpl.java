package com.example.solrDemo.service.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.solrDemo.entity.CompanyEntity;
import com.example.solrDemo.service.SolrService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;



/**
 * ApacheSolr业务逻辑接口实现.
 * 
 * @author ly
 * @date 2017年12月17日 下午2:10:47
 * @version V1.0
 */
@Service("solrService")
public class SolrServiceImpl implements SolrService {

  private Logger logger = Logger.getLogger(SolrServiceImpl.class);

  /** solr client. **/
  @Autowired
  private SolrClient solrClient;


  // TODO 创建索引，索引字段id，公司名，坐标...
  @Override
  public boolean pushDataIntoSolr(String coreName, Object object) {
    boolean flag = false;
    // 待插入的对象
    List<SolrInputDocument> inputs = new ArrayList<SolrInputDocument>();
    SolrInputDocument input = null;
    if (object instanceof CompanyEntity) {
      /**
       * 基本信息
       */
      CompanyEntity baseInfo = (CompanyEntity) object;
      input = constCompany(baseInfo);
    }
    if (null != inputs) {
      inputs.add(input);
    }
    flag = createIndex(coreName, inputs);
    return flag;
  }

  @Override
  public boolean delIndexs(String coreName, List<String> ids) {
    boolean flag = false;
    try {
      solrClient.deleteById(ids);
      UpdateResponse response = solrClient.commit();
      if (response != null && response.getStatus() == 0) {
        flag = true;
      } else {
        flag = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.error(e + "_" + e.getMessage());
    } finally {
      // try {
      // solrClient.close();
      // } catch (IOException e) {
      // e.printStackTrace();
      // logger.error(e + "_" + e.getMessage());
      // }
    }
    return flag;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.economic.system.aggregation.service.SolrService#delAll(java.lang.String)
   */
  @Override
  public boolean delAll(String coreName) {
    boolean flag = false;
    // if (StringUtils.isBlank(coreName)) {
    // coreName = CORE_NAME;
    // }
    try {
      solrClient.deleteByQuery("*:*");
      UpdateResponse response = solrClient.commit();
      if (response != null && response.getStatus() == 0) {
        flag = true;
      } else {
        flag = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.error(e + "_" + e.getMessage());
    }
    return flag;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.economic.system.aggregation.service.SolrService#searchInfo(java.lang.String,
   * java.lang.String, java.lang.String)
   */
  @Override
  public Map<String, Object> searchInfoPage(String keyWord, Map<String, Object> map,
      String orderByFiled, String ascOrDesc, Pageable pageable) {
    List<CompanyEntity> companyPojos = new ArrayList<CompanyEntity>();
    // 搜索条件
    String condition = getSearchCondtion(keyWord, map);
    Map<String, Object> infos =
        getListPage(condition, orderByFiled, ascOrDesc, companyPojos, pageable);
    infos.put("keyWord", keyWord);
    return infos;
  }

  /**
   * 根据solr唯一键id查询记录是否存在<br>
   * ture 存在，false 不存在.
   * 
   * @param id 索引唯一键
   * @return boolean
   */
  private boolean findById(String id) {
    Validate.notBlank(id, "企业id不能为空！");
    SolrQuery query = new SolrQuery();
    query.setQuery("id:" + escapeQueryChars(id));
    QueryResponse response = null;
    try {
      response = solrClient.query(query);
      SolrDocumentList resultList = response.getResults();
      if (resultList != null && resultList.size() > 0) {
        return true;
      } else {
        return false;
      }
    } catch (SolrServerException | IOException e) {
      e.printStackTrace();
      logger.error("查询异常：" + e + "_" + e.getMessage());
      return false;
    }
  }

  /**
   * 企业基本信息-添加索引字段，显示字段 .
   * 
   * @param baseInfo 公司信息
   * @return SolrInputDocument
   */
  private SolrInputDocument constCompany(CompanyEntity baseInfo) {
    SolrInputDocument input = new SolrInputDocument();
    Validate.notBlank(baseInfo.getId(), "企业基本信息id不能为空！");
    input.addField("id", baseInfo.getId());
    boolean isUpdate = false;
    String updateType = "";
    if (findById(baseInfo.getId())) {
      isUpdate = true;
      updateType = "set";
    }
    input.addField("name", updateFiled(isUpdate, updateType, baseInfo.getName()));
    input.addField("hilightName", baseInfo.getName());
//    input.addField("coordinate", baseInfo.getCoordinate());
    input.addField("classType", CompanyEntity.class.getSimpleName());
    return input;
  }

  /**
   * 新增或更新.
   * 
   * @param isUpdate true为更新，false为新增
   * @param updateType 更新类型：原子更新（set，add，inc）
   * @param value 字段值
   * @return 泛型
   */
  @SuppressWarnings("unchecked")
  private <T> T updateFiled(boolean isUpdate, String updateType, String value) {
    if (isUpdate) {
      Map<String, Object> map = new HashMap<String, Object>();
      map.put(updateType, value);
      return (T) map;
    } else {
      return (T) value;
    }
  }

  /**
   * 创建索引.
   * 
   * @param coreName 核心名称
   * @param inputs 需要创建索引的数据集合
   * @return boolean创建成功与否
   */
  private boolean createIndex(String coreName, List<SolrInputDocument> inputs) {
    boolean flag = false;
    try {
      solrClient.add(inputs);
      UpdateResponse response = solrClient.commit();
      if (response != null && response.getStatus() == 0) {
        flag = true;
      } else {
        flag = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.error(e + "_" + e.getMessage());
    }
    return flag;
  }

  /**
   * 从查询到的solr中获取数据.
   * 
   * @param conditon 搜索条件
   * @param orderByFiled 排序字段
   * @param ascOrDesc 升序降序
   * @param list 返回的公司对象
   * @return SolrDocumentList
   */
  @SuppressWarnings("unused")
  private void getListFromSolr(QueryResponse response, SolrDocumentList resultList,
      List<CompanyEntity> list) {
    Map<String, Map<String, List<String>>> resultHigh = response.getHighlighting();
    for (int i = 0; i < resultList.size(); i++) {
      SolrDocument document = resultList.get(i);
      String id = document.get("id").toString();
      String name = document.get("name") == null ? null : document.get("name").toString();
      String hilightName =
          document.get("hilightName") == null ? null : document.get("hilightName").toString();
      if (resultHigh.get(id) != null) {
        if (resultHigh.get(id).get("hilightName") != null) {
          hilightName = resultHigh.get(id).get("hilightName").get(0);
        }
      }
      CompanyEntity baseInfo = new CompanyEntity();
      baseInfo.setId(id);
      baseInfo.setName(name);
      baseInfo.setName(hilightName);
      list.add(baseInfo);
    }
  }

  /**
   * 从查询到的solr中获取数据.
   * 
   * @param conditon 搜索条件
   * @param orderByFiled 排序字段
   * @param ascOrDesc 升序降序
   * @param list 返回的公司对象
   * @return SolrDocumentList
   */
  private List<Map<String, Object>> getListFromSolr(QueryResponse response,
      SolrDocumentList resultList) {
    Map<String, Map<String, List<String>>> resultHigh = response.getHighlighting();
    // 用于返回数据的集合
    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    for (int i = 0; i < resultList.size(); i++) {
      SolrDocument document = resultList.get(i);
      String id = document.get("id").toString();
      String name = document.get("name") == null ? null : document.get("name").toString();
      String hilightName =
          document.get("hilightName") == null ? null : document.get("hilightName").toString();
      String coordinate =
          document.get("coordinate") == null ? null : document.get("coordinate").toString();
      if (resultHigh.get(id) != null) {
        if (resultHigh.get(id).get("hilightName") != null) {
          hilightName = resultHigh.get(id).get("hilightName").get(0);
        }
      }
      Map<String, Object> companyMap = new HashMap<String, Object>();
      // 企业基本信息
      CompanyEntity baseInfo = new CompanyEntity();
      baseInfo.setId(id);
      baseInfo.setName(name);
      baseInfo.setHilightName(hilightName);
//      baseInfo.setCoordinate(coordinate);
      companyMap.put("company", baseInfo);
      // 企业联系人
      list.add(companyMap);
    }
    return list;
  }

  /**
   * 构造搜索条件（包含多个搜索字段）.
   * 
   * @param keyWord 关键字
   * @param map 其它搜索条件
   * @return String
   */
  private String getSearchCondtion(String keyWord, Map<String, Object> map) {
    // solr特殊字符处理
    if (StringUtils.isNotBlank(keyWord)) {
      keyWord = escapeQueryChars(keyWord);
    }
    // 一个字段，多个关键字处理
    StringBuilder condition = conSearchFiled(keyWord, "name");
    if (map != null) {
      for (String key : map.keySet()) {
        if (condition == null) {
          condition = new StringBuilder();
        } else {
          condition.append(" AND ");
        }
        condition.append(" " + key + ":" + escapeQueryChars(map.get(key).toString()));
      }
    }
    if (condition == null) {
      return null;
    } else {
      return condition.toString();
    }
  }

  /**
   * 一个字段 有多个关键字处理.
   * 
   * @param keyWord 搜索关键字
   * @param fileName 字段名
   * @return StringBuilder
   */
  private StringBuilder conSearchFiled(String keyWord, String fileName) {
    StringBuilder condition = null;
    if (StringUtils.isNotBlank(keyWord)) {
      condition = new StringBuilder();
      keyWord = keyWord.replaceAll(" ", "|").replaceAll(",", "|").replaceAll("，", "|");
      String[] words = null;
      if (keyWord.indexOf("|") != -1) {
        words = keyWord.split("\\|");
      }
      int index = 0;
      if (words != null) {
        for (String word : words) {
          if (index == 0) {
            condition.append("(");
          } else {
            condition.append(" OR ");
          }
          condition.append(fileName + ":" + word);
          index++;
          if (index == words.length) {
            condition.append(")");
          }
        }
      } else {
        condition.append(fileName + ":" + keyWord);
      }
    }
    return condition;
  }

  /**
   * 根据搜索条件从solr查询数据.
   * 
   * @param conditon 搜索条件
   * @param orderByFiled 排序字段
   * @param ascOrDesc 升序降序
   * @param list 返回的公司对象
   * @param pageable 分页
   * @return SolrDocumentList
   */
  private Map<String, Object> getListPage(String conditon, String orderByFiled, String ascOrDesc,
      List<CompanyEntity> list, Pageable pageable) {
    Map<String, Object> map = new HashMap<String, Object>();
    Map<String, Object> pageParam = new HashMap<String, Object>();
    SolrQuery query = new SolrQuery();
//    query.setParam("fl", "*,score");
    query.setHighlightSimplePre("<font color='red'>");// 前缀
    query.setHighlightSimplePost("</font>");// 后缀
    query.addHighlightField("hilightName");// 高亮字段：名字
    query.setHighlight(true); // 开启高亮组件
    query.setHighlightFragsize(1000);// 摘要信息的长度。默认值是100，这个长度是出现关键字的位置向前移6个字符，再往后100个字符，取这一段文本。
    query.setHighlightSnippets(10);// 返回高亮摘要的段数，默认值为1
    // 分片
    //query.setFacet(true).setFacetMinCount(1).addFacetField("industryType");// 分片字段-行业类型
    // 当前页，每页大小，总页数，总条数（默认值）
    int currentPage = 0, pageSize = 15, totalPage = 1, total = 0;
    if (pageable != null) {
      currentPage = pageable.getPageNumber();
      pageSize = pageable.getPageSize();
      query.setStart(currentPage * pageSize);
      query.setRows(pageSize);
    }
    logger.info(query.getStart() + "_" + query.getRows());
    pageParam.put("currentPage", currentPage);
    pageParam.put("pageSize", pageSize);
    if (StringUtils.isBlank(conditon)) {
      query.setQuery("*:*");
    } else {
      query.setQuery(conditon);
    }
    if (StringUtils.isBlank(orderByFiled) || StringUtils.isBlank(ascOrDesc)) {
//      query.addSort("score", ORDER.desc);
    } else {
      query.addSort(orderByFiled, ORDER.valueOf(ascOrDesc));
    }
    // 结果
    QueryResponse response = null;
    SolrDocumentList resultList = null;
    List<Map<String, Object>> datas = null;
    try {
      response = solrClient.query(query);
      resultList = response.getResults();
      if (resultList != null && resultList.getNumFound() > 0) {
        // 获取solr里面查询到的数据
        // getListFromSolr(response, resultList, list);
        datas = getListFromSolr(response, resultList);
        // 总条数
        total = Integer.valueOf(String.valueOf(resultList.getNumFound()));
        if (total > pageSize) {
          if (total % pageSize == 0) {
            totalPage = total / pageSize;
          } else {
            totalPage = total / pageSize + 1;
          }
        }
      }
      pageParam.put("total", total);
      pageParam.put("totalPage", totalPage);
//      List<FacetField> facets = response.getFacetFields();// 返回的facet列表
//      Map<String, Object> industryType = new HashMap<String, Object>();
//      if(facets != null && facets.size()>0) {
//        for (FacetField facet : facets) {
//          logger.info("facets field:" + facet.getName());
//          List<Count> counts = facet.getValues();
//          for (Count count : counts) {
//            industryType.put(count.getName(), count.getCount());
//          }
//        }
//      }
//      map.put("industryType", industryType);
      // map.put("companys", list);
      map.put("companys", datas);
      map.put("pageParam", pageParam);
    } catch (Exception e) {
      e.printStackTrace();
      logger.error(e + "_" + e.getMessage());
    }
    return map;
  }

  /**
   * 特殊字符的处理.
   * 
   * @param s 字符串
   * @return String
   */
  private String escapeQueryChars(String s) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      // These characters are part of the query syntax and must be escaped
      if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '(' || c == ')' || c == ':'
          || c == '^' || c == '[' || c == ']' || c == '\"' || c == '{' || c == '}' || c == '~'
          || c == '*' || c == '?' || c == '|' || c == '&' || c == ';' || c == '/'
          || Character.isWhitespace(c)) {
        sb.append('\\');
      }
      sb.append(c);
    }
    return sb.toString();
  }

  @Override
  public void pushDataIntoSolr(String coreName, List<?> list) {
    if (list != null && list.size() > 0) {
      for (Object object : list) {
        this.pushDataIntoSolr(coreName, object);
      }
    }
  }

  @Override
  public Map<String, Object> searchInfoByKeyWord(String keyWord) {
    // solr特殊字符处理
    if (StringUtils.isNotBlank(keyWord)) {
      keyWord = escapeQueryChars(keyWord);
    }
    // 一个字段，多个关键字处理
    StringBuilder conditions = conSearchFiled(keyWord, "name");
    String condition = conditions.toString();
    Map<String, Object> infos = getListByKeyWord(condition);
    infos.put("keyWord", keyWord);
    return infos;
  }

  private Map<String, Object> getListByKeyWord(String conditon) {
    Map<String, Object> map = new HashMap<String, Object>();
    SolrQuery query = new SolrQuery();
    //query.setParam("fl", "*,score");
    query.setHighlightSimplePre("<font color='red'>");// 前缀
    query.setHighlightSimplePost("</font>");// 后缀
    query.addHighlightField("hilightName");// 高亮字段：名字
    query.setHighlight(true); // 开启高亮组件
    query.setHighlightFragsize(1000);// 摘要信息的长度。默认值是100，这个长度是出现关键字的位置向前移6个字符，再往后100个字符，取这一段文本。
    query.setHighlightSnippets(10);// 返回高亮摘要的段数，默认值为1
    // 分片
    // query.setFacet(true).setFacetMinCount(1).addFacetField("industryType");// 分片字段-行业类型
    //logger.info(query.getStart() + "_" + query.getRows());
    if (StringUtils.isBlank(conditon)) {
      query.setQuery("*:*");
    } else {
      query.setQuery(conditon);
    }
    // 结果
    QueryResponse response = null;
    SolrDocumentList resultList = null;
    List<Map<String, Object>> datas = null;
    try {
      response = solrClient.query(query);
      resultList = response.getResults();
      if (resultList != null && resultList.getNumFound() > 0) {
        // 获取solr里面查询到的数据
        // getListFromSolr(response, resultList, list);
        datas = getListFromSolr(response, resultList);
      }
//      List<FacetField> facets = response.getFacetFields();// 返回的facet列表
//      Map<String, Object> industryType = new HashMap<String, Object>();
//      if(facets != null && facets.size()>0) {
//        for (FacetField facet : facets) {
//          logger.info("facets field:" + facet.getName());
//          List<Count> counts = facet.getValues();
//          for (Count count : counts) {
//            industryType.put(count.getName(), count.getCount());
//          }
//        }
//      }
//      map.put("industryType", industryType);
      // map.put("companys", list);
      map.put("companys", datas);
    } catch (Exception e) {
      e.printStackTrace();
      logger.error(e + "_" + e.getMessage());
    }
    return map;
  }

}
