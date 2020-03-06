/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: UserEntity
 * Author:   Administrator
 * Date:     2020-03-05 17:37
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.example.solrDemo.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author Administrator
 * @create 2020-03-05
 * @since 1.0.0
 */
@Entity
@Table(name = "solr_user")
public class UserEntity extends UuidEntity {

    @Column(name = "user_name")
    private String userName;

    @Column(name = "user_age")
    private String userAge;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAge() {
        return userAge;
    }

    public void setUserAge(String userAge) {
        this.userAge = userAge;
    }
}