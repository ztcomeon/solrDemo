/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: UserController
 * Author:   Administrator
 * Date:     2020-03-05 17:42
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.example.solrDemo.controller;

import com.example.solrDemo.controller.model.ResponseCode;
import com.example.solrDemo.controller.model.ResponseModel;
import com.example.solrDemo.entity.UserEntity;
import com.example.solrDemo.service.UserService;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;


/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author Administrator
 * @create 2020-03-05
 * @since 1.0.0
 */
@RestController
public class UserController extends BaseController {

    @Autowired
    UserService userService;

    @Autowired
    private SolrClient client;

    @RequestMapping(value = "/create", method = {RequestMethod.GET})
    public ResponseModel create() {
        try {
            UserEntity u = new UserEntity();
            u.setUserName("aaa");
            u.setUserAge("b");
            userService.create(u);
            SolrInputDocument doc = new SolrInputDocument();
            doc.setField("id", u.getId());
            doc.setField("name", u.getUserName());

            client.add(doc);//如果配置文件中没有指定core，这个方法的第一个参数就需要指定core名称,比如client.add("xjxcc", doc);
            client.commit();//如果配置文件中没有指定core，这个方法的第一个参数就需要指定core名称client.commit("xjxcc");
            return new ResponseModel(new Date().getTime(), null, ResponseCode._200, "");
        } catch (Exception e) {
            return this.buildHttpReslutForException(e);
        }
    }

    @RequestMapping(value = "/findByName", method = {RequestMethod.GET})
    public ResponseModel findByName(String name) {
        try {
            List<UserEntity> userList = userService.findByName(name);

            return new ResponseModel(new Date().getTime(), userList, ResponseCode._200, "");
        } catch (Exception e) {
            return this.buildHttpReslutForException(e);
        }
    }
}