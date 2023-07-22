// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.provider;

import com.gitee.dbswitch.common.type.ProductTypeEnum;
import com.gitee.dbswitch.features.ProductFeatures;
import com.gitee.dbswitch.provider.meta.MetadataProvider;
import com.gitee.dbswitch.provider.operate.DefaultTableOperateProvider;
import com.gitee.dbswitch.provider.operate.TableOperateProvider;
import com.gitee.dbswitch.provider.query.DefaultTableDataQueryProvider;
import com.gitee.dbswitch.provider.query.TableDataQueryProvider;
import com.gitee.dbswitch.provider.sync.DefaultTableDataSynchronizer;
import com.gitee.dbswitch.provider.sync.TableDataSynchronizer;
import com.gitee.dbswitch.provider.write.DefaultTableDataWriteProvider;
import com.gitee.dbswitch.provider.write.TableDataWriteProvider;
import javax.sql.DataSource;

public interface ProductFactoryProvider {

  /**
   * 获取数据库类型
   *
   * @return ProductTypeEnum
   */
  ProductTypeEnum getProductType();

  /**
   * 获取数据源
   *
   * @return DataSource
   */
  DataSource getDataSource();

  /**
   * 获取数据库特征
   *
   * @return ProductFeatures
   */
  ProductFeatures getProductFeatures();

  /**
   * 获取元数据查询Provider
   *
   * @return MetadataQueryProvider
   */
  MetadataProvider createMetadataQueryProvider();

  /**
   * 获取表数据查询Provider
   *
   * @return TableDataQueryProvider
   */
  default TableDataQueryProvider createTableDataQueryProvider() {
    return new DefaultTableDataQueryProvider(this);
  }

  /**
   * 获取表批量写入Provider
   *
   * @param useInsert 是否使用insert写入(只对PG有效)
   * @return TableWriteProvider
   */
  default TableDataWriteProvider createTableDataWriteProvider(boolean useInsert) {
    return new DefaultTableDataWriteProvider(this);
  }

  /**
   * 获取表操作Provider
   *
   * @return TableOperateProvider
   */
  default TableOperateProvider createTableOperateProvider() {
    return new DefaultTableOperateProvider(this);
  }

  /**
   * 获取数据同步Provider
   *
   * @return TableDataSynchronizer
   */
  default TableDataSynchronizer createTableDataSynchronizer() {
    return new DefaultTableDataSynchronizer(this);
  }

}
