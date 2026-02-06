// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package org.dromara.dbswitch.product.doris;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.dromara.dbswitch.common.entity.CloseableDataSource;
import org.dromara.dbswitch.core.provider.ProductFactoryProvider;
import org.dromara.dbswitch.core.provider.sync.AutoCastTableDataSynchronizeProvider;

@Slf4j
public class DorisTableDataSynchronizer extends AutoCastTableDataSynchronizeProvider {

  private List<String> fieldNames;
  private final CloseableDataSource dataSource;

  private final DorisUtils dorisUtils = new DorisUtils();

  public DorisTableDataSynchronizer(ProductFactoryProvider factoryProvider) {
    super(factoryProvider);
    dataSource = (CloseableDataSource) factoryProvider.getDataSource();
  }

  @Override
  public void prepare(String schemaName, String tableName, List<String> fieldNames, List<String> pks) {
    this.fieldNames = fieldNames;
    super.prepare(schemaName, tableName, fieldNames, pks);
    try {
      dorisUtils.init(schemaName, tableName, dataSource);
    } catch (Exception e) {
      log.warn("Failed to init by DorisUtils#init(),information:: {}", e.getMessage());
    }
  }

  @Override
  public long executeInsert(List<Object[]> recordValues) {
    try {
      return dorisUtils.addOrUpdateData(fieldNames, recordValues);
    } catch (Exception e) {
      log.warn("Failed to addOrUpdateData by DorisUtils#addOrUpdateData(),information:: {}", e.getMessage());
      return super.executeInsert(recordValues);
    }
  }

  @Override
  public long executeUpdate(List<Object[]> recordValues) {
    try {
      return dorisUtils.addOrUpdateData(fieldNames, recordValues);
    } catch (Exception e) {
      log.warn("Failed to addOrUpdateData by DorisUtils#addOrUpdateData(),information:: {}", e.getMessage());
      return super.executeUpdate(recordValues);
    }
  }

}
