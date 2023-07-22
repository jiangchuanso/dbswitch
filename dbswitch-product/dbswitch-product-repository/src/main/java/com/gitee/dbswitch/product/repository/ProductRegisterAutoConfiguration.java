// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.product.repository;

import com.gitee.dbswitch.common.type.ProductTypeEnum;
import com.gitee.dbswitch.product.db2.DB2FactoryProvider;
import com.gitee.dbswitch.product.dm.DmFactoryProvider;
import com.gitee.dbswitch.product.gbase.GbaseFactoryProvider;
import com.gitee.dbswitch.product.hive.HiveFactoryProvider;
import com.gitee.dbswitch.product.kingbase.KingbaseFactoryProvider;
import com.gitee.dbswitch.product.mariadb.MariadbFactoryProvider;
import com.gitee.dbswitch.product.mysql.MysqlFactoryProvider;
import com.gitee.dbswitch.product.oracle.OracleFactoryProvider;
import com.gitee.dbswitch.product.oscar.OscarFactoryProvider;
import com.gitee.dbswitch.product.postgresql.PostgresFactoryProvider;
import com.gitee.dbswitch.product.sqlite.SqliteFactoryProvider;
import com.gitee.dbswitch.product.sqlserver.SqlserverFactoryProvider;
import com.gitee.dbswitch.product.sybase.SybaseFactoryProvider;
import com.gitee.dbswitch.provider.ProductProviderFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnClass(ProductProviderFactory.class)
public class ProductRegisterAutoConfiguration implements InitializingBean {

  @Override
  public void afterPropertiesSet() throws Exception {
    log.info("Register database product now ...");
    ProductProviderFactory.register(ProductTypeEnum.MYSQL, MysqlFactoryProvider.class.getName());
    ProductProviderFactory.register(ProductTypeEnum.ORACLE, OracleFactoryProvider.class.getName());
    ProductProviderFactory.register(ProductTypeEnum.POSTGRESQL, PostgresFactoryProvider.class.getName());
    ProductProviderFactory.register(ProductTypeEnum.SQLSERVER, SqlserverFactoryProvider.class.getName());
    ProductProviderFactory.register(ProductTypeEnum.DB2, DB2FactoryProvider.class.getName());
    ProductProviderFactory.register(ProductTypeEnum.DM, DmFactoryProvider.class.getName());
    ProductProviderFactory.register(ProductTypeEnum.SYBASE, SybaseFactoryProvider.class.getName());
    ProductProviderFactory.register(ProductTypeEnum.KINGBASE, KingbaseFactoryProvider.class.getName());
    ProductProviderFactory.register(ProductTypeEnum.OSCAR, OscarFactoryProvider.class.getName());
    ProductProviderFactory.register(ProductTypeEnum.SQLITE3, SqliteFactoryProvider.class.getName());
    ProductProviderFactory.register(ProductTypeEnum.MARIADB, MariadbFactoryProvider.class.getName());
    ProductProviderFactory.register(ProductTypeEnum.GBASE8A, GbaseFactoryProvider.class.getName());
    ProductProviderFactory.register(ProductTypeEnum.HIVE, HiveFactoryProvider.class.getName());
    log.info("Finish to register database product ");
  }
}
