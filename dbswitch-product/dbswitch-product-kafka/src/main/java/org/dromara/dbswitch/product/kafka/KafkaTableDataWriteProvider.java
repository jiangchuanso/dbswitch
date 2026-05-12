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
import org.apache.commons.collections4.CollectionUtils;
import org.dromara.dbswitch.core.provider.ProductFactoryProvider;
import org.dromara.dbswitch.core.provider.write.DefaultTableDataWriteProvider;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

/**
 * Kafka表数据写入提供者
 * Kafka作为消息队列，数据写入对应于向Topic发送消息
 */
@Slf4j
public class KafkaTableDataWriteProvider extends DefaultTableDataWriteProvider {

  public KafkaTableDataWriteProvider(ProductFactoryProvider factoryProvider) {
    super(factoryProvider);
  }

  @Override
  public void prepareWrite(String schemaName, String tableName, List<String> fieldNames) {
    // Kafka不需要预处理，但需要设置schema和tableName
    this.columnType = Collections.emptyMap();
    this.schemaName = schemaName;
    this.tableName = tableName;
    
    log.info("Preparing to write to Kafka topic: {}", tableName);
  }

  @Override
  public long write(List<String> fieldNames, List<Object[]> recordValues) {
    if (CollectionUtils.isEmpty(fieldNames) || CollectionUtils.isEmpty(recordValues)) {
      return 0L;
    }
    
    log.info("Writing {} records to Kafka topic: {}", recordValues.size(), tableName);
    
    // 对于Kafka，我们需要将记录转换为消息并发送到Topic
    // 这里使用JDBC驱动提供的机制来发送数据
    try (Connection connection = getDataSource().getConnection()) {
      for (Object[] record : recordValues) {
        // 构建INSERT语句用于Kafka JDBC连接器
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(tableName).append(" (");
        
        for (int i = 0; i < fieldNames.size(); i++) {
          if (i > 0) sb.append(", ");
          sb.append(fieldNames.get(i));
        }
        sb.append(") VALUES (");
        
        for (int i = 0; i < record.length; i++) {
          if (i > 0) sb.append(", ");
          if (record[i] == null) {
            sb.append("NULL");
          } else {
            sb.append("'").append(record[i].toString().replace("'", "''")).append("'");
          }
        }
        sb.append(")");
        
        try (Statement stmt = connection.createStatement()) {
          stmt.executeUpdate(sb.toString());
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to write data to Kafka topic: " + tableName, e);
    }
    
    return recordValues.size();
  }
}
