// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.product.sqlserver;

import com.gitee.dbswitch.provider.ProductFactoryProvider;
import com.gitee.dbswitch.provider.operate.DefaultTableOperateProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SqlserverTableOperateProvider extends DefaultTableOperateProvider {

  public SqlserverTableOperateProvider(ProductFactoryProvider factoryProvider) {
    super(factoryProvider);
  }

  @Override
  public void truncateTableData(String schemaName, String tableName) {
    String sql = String.format("TRUNCATE TABLE [%s].[%s] ", schemaName, tableName);
    this.executeSql(sql);
  }

  @Override
  public void dropTable(String schemaName, String tableName) {
    String sql = String.format("DROP TABLE [%s].[%s] ", schemaName, tableName);
    this.executeSql(sql);
  }

}
