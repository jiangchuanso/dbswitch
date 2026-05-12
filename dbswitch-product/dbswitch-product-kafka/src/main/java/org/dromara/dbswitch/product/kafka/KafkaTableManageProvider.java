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
import org.dromara.dbswitch.core.provider.manage.DefaultTableManageProvider;

/**
 * Kafka表管理提供者
 * Kafka作为消息队列，不支持传统的表操作如truncate和drop
 */
@Slf4j
public class KafkaTableManageProvider extends DefaultTableManageProvider {

  public KafkaTableManageProvider(ProductFactoryProvider factoryProvider) {
    super(factoryProvider);
  }

  @Override
  public void truncateTableData(String schemaName, String tableName) {
    // Kafka不支持truncate操作，Topic中的数据通过保留策略自动清理
    log.warn("Kafka does not support truncate operation. Topic data is managed by retention policy.");
  }

  @Override
  public void dropTable(String schemaName, String tableName) {
    // Kafka不支持drop操作，Topic需要通过Kafka管理工具删除
    log.warn("Kafka does not support drop operation. Topics should be managed through Kafka admin tools.");
  }
}
