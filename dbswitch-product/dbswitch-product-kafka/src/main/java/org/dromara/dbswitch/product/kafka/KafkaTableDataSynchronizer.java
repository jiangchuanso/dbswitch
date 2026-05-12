// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package org.dromara.dbswitch.product.kafka;

import lombok.extern.slf4j.Slf4j;
import org.dromara.dbswitch.core.provider.ProductFactoryProvider;
import org.dromara.dbswitch.core.provider.sync.DefaultTableDataSynchronizeProvider;

import java.util.Collections;
import java.util.List;

/**
 * Kafka表数据同步提供者
 * Kafka作为消息队列，主要支持数据插入操作（发送消息）
 * 不支持传统的update和delete操作
 */
@Slf4j
public class KafkaTableDataSynchronizer extends DefaultTableDataSynchronizeProvider {

  public KafkaTableDataSynchronizer(ProductFactoryProvider factoryProvider) {
    super(factoryProvider);
  }

  @Override
  public void prepare(String schemaName, String tableName, List<String> fieldNames, List<String> pks) {
    // Kafka不需要主键概念，但为了兼容性仍然调用父类方法
    log.info("Preparing Kafka data synchronizer for topic: {}", tableName);
    
    // 初始化基本字段，但不依赖传统数据库的元数据
    this.columnType = Collections.emptyMap();
    this.fieldOrders = fieldNames;
    this.pksOrders = pks != null ? pks : Collections.emptyList();
    
    // 为Kafka构建INSERT语句模板
    this.insertStatementSql = getInsertPrepareStatementSql(schemaName, tableName, fieldNames);
    
    // Kafka不支持UPDATE和DELETE，设置为null或空
    this.updateStatementSql = null;
    this.deleteStatementSql = null;
    
    // 设置参数类型数组
    this.insertArgsType = new int[fieldNames.size()];
    // 对于Kafka，所有字段都视为字符串类型
    for (int i = 0; i < fieldNames.size(); i++) {
      insertArgsType[i] = java.sql.Types.VARCHAR;
    }
    
    this.updateArgsType = new int[0];
    this.deleteArgsType = new int[0];
  }

  @Override
  public long executeInsert(List<Object[]> records) {
    if (records == null || records.isEmpty()) {
      return 0;
    }
    
    log.info("Executing insert of {} records to Kafka topic", records.size());
    
    // 使用父类的插入逻辑，通过JDBC驱动向Kafka发送消息
    try {
      return super.executeInsert(records);
    } catch (Exception e) {
      log.error("Failed to insert records to Kafka", e);
      throw new RuntimeException("Failed to send messages to Kafka topic", e);
    }
  }

  @Override
  public long executeUpdate(List<Object[]> records) {
    // Kafka不支持更新操作
    log.warn("Kafka does not support update operations. Records will be ignored.");
    return 0;
  }

  @Override
  public long executeDelete(List<Object[]> records) {
    // Kafka不支持删除操作
    log.warn("Kafka does not support delete operations. Records will be ignored.");
    return 0;
  }
}
