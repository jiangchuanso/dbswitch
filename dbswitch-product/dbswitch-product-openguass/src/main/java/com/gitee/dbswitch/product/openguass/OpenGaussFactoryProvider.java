// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.product.openguass;

import com.gitee.dbswitch.annotation.Product;
import com.gitee.dbswitch.common.type.ProductTypeEnum;
import com.gitee.dbswitch.features.ProductFeatures;
import com.gitee.dbswitch.provider.AbstractFactoryProvider;
import com.gitee.dbswitch.provider.meta.MetadataProvider;
import com.gitee.dbswitch.provider.manage.TableManageProvider;
import com.gitee.dbswitch.provider.sync.AutoCastTableDataSynchronizeProvider;
import com.gitee.dbswitch.provider.sync.TableDataSynchronizeProvider;
import com.gitee.dbswitch.provider.write.AutoCastTableDataWriteProvider;
import com.gitee.dbswitch.provider.write.TableDataWriteProvider;
import javax.sql.DataSource;

@Product(ProductTypeEnum.OPENGAUSS)
public class OpenGaussFactoryProvider extends AbstractFactoryProvider {

  public OpenGaussFactoryProvider(DataSource dataSource) {
    super(dataSource);
  }

  @Override
  public ProductFeatures getProductFeatures() {
    return new OpenGaussFeatures();
  }

  @Override
  public MetadataProvider createMetadataQueryProvider() {
    return new OpenGaussMetadataQueryProvider(this);
  }

  @Override
  public TableManageProvider createTableManageProvider() {
    return new OpenGaussTableManageProvider(this);
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
