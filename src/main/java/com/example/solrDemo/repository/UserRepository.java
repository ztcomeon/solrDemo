/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: UserRepository
 * Author:   Administrator
 * Date:     2020-03-05 17:40
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.example.solrDemo.repository;

import com.example.solrDemo.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import javax.jws.soap.SOAPBinding;
import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author Administrator
 * @create 2020-03-05
 * @since 1.0.0
 */
@Repository("UserRepository")
public interface UserRepository extends JpaRepository<UserEntity, String>, JpaSpecificationExecutor<UserEntity> {

    List<UserEntity> findByUserName(String name);

}