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
import com.gitee.dbswitch.provider.manage.DefaultTableManageProvider;
import com.gitee.dbswitch.provider.manage.TableManageProvider;
import com.gitee.dbswitch.provider.meta.MetadataProvider;
import com.gitee.dbswitch.provider.query.DefaultTableDataQueryProvider;
import com.gitee.dbswitch.provider.query.TableDataQueryProvider;
import com.gitee.dbswitch.provider.sync.DefaultTableDataSynchronizeProvider;
import com.gitee.dbswitch.provider.sync.TableDataSynchronizeProvider;
import com.gitee.dbswitch.provider.transform.MappedTransformProvider;
import com.gitee.dbswitch.provider.transform.RecordTransformProvider;
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
   * 获取记录转换Provider
   *
   * @return RecordTransformProvider
   */
  default RecordTransformProvider createRecordTransformProvider() {
    return new MappedTransformProvider(this);
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
   * @return TableManageProvider
   */
  default TableManageProvider createTableManageProvider() {
    return new DefaultTableManageProvider(this);
  }

  /**
   * 获取数据同步Provider
   *
   * @return TableDataSynchronizeProvider
   */
  default TableDataSynchronizeProvider createTableDataSynchronizeProvider() {
    return new DefaultTableDataSynchronizeProvider(this);
  }

}
