// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: wjk (wanglv110@163.com)
// Date : 2024/9/29
// Location: wuhan , china
/////////////////////////////////////////////////////////////
package org.dromara.dbswitch.product.doris;

import javax.sql.DataSource;
import org.dromara.dbswitch.common.type.ProductTypeEnum;
import org.dromara.dbswitch.core.annotation.Product;
import org.dromara.dbswitch.core.features.ProductFeatures;
import org.dromara.dbswitch.core.provider.AbstractFactoryProvider;
import org.dromara.dbswitch.core.provider.meta.MetadataProvider;
import org.dromara.dbswitch.core.provider.sync.TableDataSynchronizeProvider;
import org.dromara.dbswitch.core.provider.write.TableDataWriteProvider;

@Product(ProductTypeEnum.DORIS)
public class DorisFactoryProvider extends AbstractFactoryProvider {

  public DorisFactoryProvider(DataSource dataSource) {
    super(dataSource);
  }

  @Override
  public ProductFeatures getProductFeatures() {
    return new DorisFeatures();
  }

  @Override
  public MetadataProvider createMetadataQueryProvider() {
    return new DorisMetadataQueryProvider(this);
  }

  @Override
  public TableDataWriteProvider createTableDataWriteProvider(boolean useInsert) {
    return new DorisTableDataWriteProvider(this);
  }

  @Override
  public TableDataSynchronizeProvider createTableDataSynchronizeProvider() {
    return new DorisTableDataSynchronizer(this);
  }

}
