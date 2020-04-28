/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: CompanyEntity
 * Author:   Administrator
 * Date:     2020-03-06 16:18
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.example.solrDemo.entity;

import org.apache.solr.client.solrj.beans.Field;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author Administrator
 * @create 2020-03-06
 * @since 1.0.0
 */
@Entity
@Table(name = "solr_company")
public class CompanyEntity  extends UuidEntity{

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Field
    private String id;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 公司名称.
     */
    @Column(name = "name")
    @Field
    private String name;

    /** 社会统一编码. */
    @Column(name = "credit_code")
    @Field
    private String creditCode;

    /**
     * 公司名称_高亮显示
     */
    @Field
    private String hilightName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public String getHilightName() {
        return hilightName;
    }

    public void setHilightName(String hilightName) {
        this.hilightName = hilightName;
    }
}