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
import org.dromara.dbswitch.core.provider.write.AutoCastTableDataWriteProvider;

@Slf4j
public class DorisTableDataWriteProvider extends AutoCastTableDataWriteProvider {

  private final CloseableDataSource dataSource;
  private final DorisUtils dorisUtils = new DorisUtils();

  public DorisTableDataWriteProvider(ProductFactoryProvider factoryProvider) {
    super(factoryProvider);
    dataSource = (CloseableDataSource) factoryProvider.getDataSource();
  }

  @Override
  public void prepareWrite(String schemaName, String tableName, List<String> fieldNames) {
    super.prepareWrite(schemaName, tableName, fieldNames);
    try {
      dorisUtils.init(schemaName, tableName, dataSource);
    } catch (Exception e) {
      log.warn("Failed to init by DorisUtils#init(),information: {}", e.getMessage());
    }
  }

  @Override
  public long write(List<String> fieldNames, List<Object[]> recordValues) {
    try {
      return dorisUtils.addOrUpdateData(fieldNames, recordValues);
    } catch (Exception e) {
      log.warn("Failed to insertOrUpdate data by DorisUtils#addOrUpdateData(),information: {}", e.getMessage());
      return super.write(fieldNames, recordValues);
    }
  }

}
