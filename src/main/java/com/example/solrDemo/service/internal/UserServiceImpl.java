/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: UserServiceImpl
 * Author:   Administrator
 * Date:     2020-03-05 17:41
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.example.solrDemo.service.internal;

import com.example.solrDemo.entity.UserEntity;
import com.example.solrDemo.repository.UserRepository;
import com.example.solrDemo.service.UserService;
import org.apache.solr.client.solrj.SolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author Administrator
 * @create 2020-03-05
 * @since 1.0.0
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;



    @Override
    @Transactional
    public UserEntity create(UserEntity userEntity) {


        return  userRepository.save(userEntity);

    }

    @Override
    public List<UserEntity> findAll() {
        List<UserEntity> users = userRepository.findAll();
        return users;
    }

    @Override
    public List<UserEntity> findById(String id) {
        List<UserEntity> users = (List<UserEntity>) userRepository.findById(id).get();
        return users;
    }

    @Override
    public List<UserEntity> findByName(String name) {
//        List<UserEntity> users = userRepository.findByUserName(name);
//        return users;
        return null;
    }
}