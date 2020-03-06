package com.example.solrDemo.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

/**
 * ApacheSolr业务逻辑接口.
 * 
 * @author ly
 * @date 2017年12月17日 下午2:07:47
 * @version V1.0
 */
public interface SolrService {

  /**
   * 往solr插入数据（单个对象）.
   * 
   * @param coreName 核心名称（默认为空）
   * @param object 待存储的对象数据
   * @return boolean
   */
  boolean pushDataIntoSolr(String coreName, Object object);

  /**
   * 根据ids集合删除solr中的索引.
   * 
   * @param coreName 核心名称（默认为空）
   * @param ids ids集合
   * @return boolean
   */
  boolean delIndexs(String coreName, List<String> ids);
  
  /**
   * 删除所有数据.
   * 
   * @param coreName 核心名称（默认为空 ）
   * @return boolean
   */
  boolean delAll(String coreName);

  /**
   * 分页搜索查询.
   * 
   * @param keyWord 搜索关键字
   * @param map 其它搜索字段
   * @param orderByFiled 排序字段
   * @param ascOrDesc 顺序倒序
   * @param pageable 分页
   * @return Map<String, Object>
   */
  Map<String, Object> searchInfoPage(String keyWord, Map<String, Object> map,
                                     String orderByFiled, String ascOrDesc, Pageable pageable);
  
  /**
   * 往solr插入数据（集合对象）.
   * 
   * @param coreName 核心名称（默认为空）
   * @param list 待存储的数据
   * @return boolean
   */
  void pushDataIntoSolr(String coreName, List<?> list);
  /**
   * 通过企业名称关键词搜索
   * @param keyWord
   * @return
   */
  Map<String, Object> searchInfoByKeyWord(String keyWord);
}
