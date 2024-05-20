// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.product.sr;

import com.gitee.dbswitch.annotation.Product;
import com.gitee.dbswitch.common.type.ProductTypeEnum;
import com.gitee.dbswitch.common.util.DDLFormatterUtils;
import com.gitee.dbswitch.features.ProductFeatures;
import com.gitee.dbswitch.provider.AbstractFactoryProvider;
import com.gitee.dbswitch.provider.meta.MetadataProvider;
import com.gitee.dbswitch.provider.sync.AutoCastTableDataSynchronizeProvider;
import com.gitee.dbswitch.provider.sync.TableDataSynchronizeProvider;
import com.gitee.dbswitch.provider.write.AutoCastTableDataWriteProvider;
import com.gitee.dbswitch.provider.write.TableDataWriteProvider;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Product(ProductTypeEnum.STARROCKS)
public class StarrocksFactoryProvider extends AbstractFactoryProvider {

  public StarrocksFactoryProvider(DataSource dataSource) {
    super(dataSource);
  }

  public ProductFeatures getProductFeatures() {
    return new StarrocksFeatures();
  }

  @Override
  public MetadataProvider createMetadataQueryProvider() {
    return new StarrocksMetadataQueryProvider(this);
  }

  @Override
  public TableDataWriteProvider createTableDataWriteProvider(boolean useInsert) {
    return new AutoCastTableDataWriteProvider(this);
  }

  @Override
  public TableDataSynchronizeProvider createTableDataSynchronizeProvider() {
    return new AutoCastTableDataSynchronizeProvider(this);
  }


}
