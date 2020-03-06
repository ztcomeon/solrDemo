/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: UserService
 * Author:   Administrator
 * Date:     2020-03-05 17:41
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.example.solrDemo.service;

import com.example.solrDemo.entity.UserEntity;

import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author Administrator
 * @create 2020-03-05
 * @since 1.0.0
 */
public interface UserService {

    UserEntity create(UserEntity userEntity);

    List<UserEntity> findAll();

    List<UserEntity> findById(String id);

    List<UserEntity> findByName(String name);
}