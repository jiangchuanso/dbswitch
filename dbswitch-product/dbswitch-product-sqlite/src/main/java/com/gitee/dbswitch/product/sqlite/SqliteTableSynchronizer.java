// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.product.sqlite;

import com.gitee.dbswitch.common.util.TypeConvertUtils;
import com.gitee.dbswitch.provider.ProductFactoryProvider;
import com.gitee.dbswitch.provider.sync.DefaultTableDataSynchronizer;
import java.util.List;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class SqliteTableSynchronizer extends DefaultTableDataSynchronizer {

  public SqliteTableSynchronizer(ProductFactoryProvider factoryProvider) {
    super(factoryProvider);
  }

  @Override
  protected TransactionDefinition getDefaultTransactionDefinition() {
    DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
    definition.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
    definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    return definition;
  }

  @Override
  public long executeInsert(List<Object[]> records) {
    records.parallelStream().forEach((Object[] row) -> {
      for (int i = 0; i < row.length; ++i) {
        try {
          row[i] = TypeConvertUtils.castByDetermine(row[i]);
        } catch (Exception e) {
          row[i] = null;
        }
      }
    });

    return super.executeInsert(records);
  }

  @Override
  public long executeUpdate(List<Object[]> records) {
    records.parallelStream().forEach((Object[] row) -> {
      for (int i = 0; i < row.length; ++i) {
        try {
          row[i] = TypeConvertUtils.castByDetermine(row[i]);
        } catch (Exception e) {
          row[i] = null;
        }
      }
    });

    return super.executeUpdate(records);
  }

}
