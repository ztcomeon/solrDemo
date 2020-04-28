package com.example.solrDemo.service.internal;

import com.example.solrDemo.entity.CompanyEntity;
import com.example.solrDemo.repository.CompanyRepository;
import com.example.solrDemo.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author Administrator
 * @create 2020-04-28
 * @since 1.0.0
 */
@Service("companyService")
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    CompanyRepository companyRepository;

    @Override
    public CompanyEntity create(CompanyEntity companyEntity) {
        CompanyEntity entity = companyRepository.save(companyEntity);
        return entity;
    }

    @Override
    public CompanyEntity findById(String id) {
        CompanyEntity entity = companyRepository.findById(id).get();
        return entity;
    }

    @Override
    public List<CompanyEntity> findAll() {
        List<CompanyEntity> entities = companyRepository.findAll();
        return entities;
    }

    @Override
    public List<CompanyEntity> findByName(String name) {
        List<CompanyEntity> entities = companyRepository.findByName(name);
        return entities;
    }
}