// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.product.mongodb;

import com.gitee.dbswitch.annotation.Product;
import com.gitee.dbswitch.common.type.ProductTypeEnum;
import com.gitee.dbswitch.features.ProductFeatures;
import com.gitee.dbswitch.provider.AbstractFactoryProvider;
import com.gitee.dbswitch.provider.meta.MetadataProvider;
import com.gitee.dbswitch.provider.operate.TableOperateProvider;
import com.gitee.dbswitch.provider.query.TableDataQueryProvider;
import com.gitee.dbswitch.provider.sync.TableDataSynchronizer;
import com.gitee.dbswitch.provider.write.TableDataWriteProvider;
import javax.sql.DataSource;

@Product(ProductTypeEnum.MONGODB)
public class MongodbFactoryProvider extends AbstractFactoryProvider {

  public MongodbFactoryProvider(DataSource dataSource) {
    super(dataSource);
  }

  public ProductFeatures getProductFeatures() {
    return new MongodbFeatures();
  }

  @Override
  public MetadataProvider createMetadataQueryProvider() {
    return new MongodbMetadataQueryProvider(this);
  }

  @Override
  public TableDataQueryProvider createTableDataQueryProvider() {
    return new MongodbTableDataQueryProvider(this);
  }

  @Override
  public TableDataWriteProvider createTableDataWriteProvider(boolean useInsert) {
    return new MongodbTableDataWriteProvider(this);
  }

  @Override
  public TableOperateProvider createTableOperateProvider() {
    return new MongodbTableOperateProvider(this);
  }

  @Override
  public TableDataSynchronizer createTableDataSynchronizer() {
    return new MongodbTableDataSynchronizer(this);
  }

}
