// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.product.kingbase;

import com.gitee.dbswitch.common.util.ObjectCastUtils;
import com.gitee.dbswitch.provider.ProductFactoryProvider;
import com.gitee.dbswitch.provider.write.DefaultTableDataWriteProvider;
import java.util.List;

public class KingbaseTableWriteProvider extends DefaultTableDataWriteProvider {

  public KingbaseTableWriteProvider(ProductFactoryProvider factoryProvider) {
    super(factoryProvider);
  }

  @Override
  public long write(List<String> fieldNames, List<Object[]> recordValues) {
    recordValues.forEach((Object[] row) -> {
      for (int i = 0; i < row.length; ++i) {
        try {
          row[i] = ObjectCastUtils.castByDetermine(row[i]);
        } catch (Exception e) {
          row[i] = null;
        }
      }
    });

    return super.write(fieldNames, recordValues);
  }

}
