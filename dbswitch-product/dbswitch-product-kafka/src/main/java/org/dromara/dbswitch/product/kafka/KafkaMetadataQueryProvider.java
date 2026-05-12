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
import org.apache.commons.lang3.StringUtils;
import org.dromara.dbswitch.core.provider.ProductFactoryProvider;
import org.dromara.dbswitch.core.provider.meta.AbstractMetadataProvider;
import org.dromara.dbswitch.core.schema.ColumnDescription;
import org.dromara.dbswitch.core.schema.ColumnMetaData;
import org.dromara.dbswitch.core.schema.SourceProperties;
import org.dromara.dbswitch.core.schema.TableDescription;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Kafka元数据查询提供者
 * Kafka作为消息队列，其"表"概念对应Topic
 */
@Slf4j
public class KafkaMetadataQueryProvider extends AbstractMetadataProvider {

  public KafkaMetadataQueryProvider(ProductFactoryProvider factoryProvider) {
    super(factoryProvider);
  }

  @Override
  public List<String> querySchemaList(Connection connection) {
    // Kafka没有schema概念，返回空列表或默认值
    List<String> schemas = new ArrayList<>();
    schemas.add("default");
    return schemas;
  }

  @Override
  public List<TableDescription> queryTableList(Connection connection, String schemaName) {
    // Kafka的"表"对应Topic，这里需要通过JDBC驱动获取Topic列表
    // 由于Kafka JDBC驱动的特殊性，返回空列表或需要从配置中获取
    log.warn("Kafka does not support traditional table listing. Topics should be configured externally.");
    return new ArrayList<>();
  }

  @Override
  public TableDescription queryTableMeta(Connection connection, String schemaName, String tableName) {
    // Kafka的"表"对应Topic
    TableDescription td = new TableDescription();
    td.setSchemaName(schemaName);
    td.setTableName(tableName);
    td.setTableType("TOPIC");
    td.setRemarks("Kafka Topic: " + tableName);
    return td;
  }

  @Override
  public List<String> queryTableColumnName(Connection connection, String schemaName, String tableName) {
    // Kafka消息没有固定的列结构，返回空列表
    log.warn("Kafka topics do not have fixed column structures.");
    return new ArrayList<>();
  }

  @Override
  public List<ColumnDescription> queryTableColumnMeta(Connection connection, String schemaName, String tableName) {
    // Kafka消息没有固定的列结构
    log.warn("Kafka topics do not have fixed column metadata.");
    return new ArrayList<>();
  }

  @Override
  public List<String> queryTablePrimaryKeys(Connection connection, String schemaName, String tableName) {
    // Kafka不支持主键概念
    return new ArrayList<>();
  }

  @Override
  public String getTableDDL(Connection connection, String schemaName, String tableName) {
    // Kafka不支持传统DDL
    return "-- Kafka Topic: " + tableName + "\n-- DDL not applicable for Kafka";
  }

  @Override
  public String getViewDDL(Connection connection, String schemaName, String tableName) {
    // Kafka不支持视图
    return null;
  }

  @Override
  public List<ColumnDescription> querySelectSqlColumnMeta(Connection connection, String sql) {
    // Kafka不支持SQL查询
    log.warn("Kafka does not support SQL queries for metadata extraction.");
    return new ArrayList<>();
  }

  @Override
  public void testQuerySQL(Connection connection, String sql) {
    // Kafka不支持SQL查询测试
    log.warn("Kafka does not support SQL query testing.");
  }

  @Override
  public String getFieldDefinition(ColumnMetaData v, List<String> pks, boolean useAutoInc,
      boolean addCr, boolean withRemarks) {
    // Kafka不需要字段定义
    return "-- Field definition not applicable for Kafka";
  }

  @Override
  public List<String> getTableColumnCommentDefinition(TableDescription td, List<ColumnDescription> cds) {
    return Collections.emptyList();
  }

  @Override
  public void preAppendCreateTableSql(StringBuilder builder) {
    // Kafka不需要建表前缀
  }

  @Override
  public void postAppendCreateTableSql(StringBuilder builder, String tblComment, List<String> primaryKeys,
      SourceProperties tblProperties) {
    // Kafka不需要建表后缀
    builder.append("-- Kafka topic creation is managed externally");
  }
}
