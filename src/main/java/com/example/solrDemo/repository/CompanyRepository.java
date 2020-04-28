package com.example.solrDemo.repository;

import com.example.solrDemo.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author Administrator
 * @create 2020-04-28
 * @since 1.0.0
 */
@Repository("companyRepository")
public interface CompanyRepository extends JpaRepository<CompanyEntity, String>, JpaSpecificationExecutor<CompanyEntity> {
    List<CompanyEntity> findByName(String name);

}