package com.example.solrDemo.service;

import com.example.solrDemo.entity.CompanyEntity;

import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author Administrator
 * @create 2020-04-28
 * @since 1.0.0
 */
public interface CompanyService {

    CompanyEntity create(CompanyEntity companyEntity);

    CompanyEntity findById(String id);

    List<CompanyEntity> findAll();

    List<CompanyEntity> findByName(String name);
}