// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.product.greenplum;

import com.gitee.dbswitch.product.postgresql.PostgresMetadataQueryProvider;
import com.gitee.dbswitch.provider.ProductFactoryProvider;
import com.gitee.dbswitch.schema.SourceProperties;
import java.util.HashSet;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

@Slf4j
public class GreenplumMetadataQueryProvider extends PostgresMetadataQueryProvider {

  static {
    systemSchemas.add("pg_aoseg");
    systemSchemas.add("gp_toolkit");
  }

  public GreenplumMetadataQueryProvider(ProductFactoryProvider factoryProvider) {
    super(factoryProvider);
  }

  @Override
  public void postAppendCreateTableSql(StringBuilder builder, String tblComment, List<String> primaryKeys,
      SourceProperties tblProperties) {
    if (CollectionUtils.isEmpty(primaryKeys)) {
      return;
    }
    // 分布键不为空，且需要满足是主键的子集
    if (!CollectionUtils.isEmpty(tblProperties.getDistributedKeys()) && new HashSet<>(primaryKeys).containsAll(
        tblProperties.getDistributedKeys())) {
      getPkOrDkAsString(builder, tblProperties.getDistributedKeys());
    } else {
      getPkOrDkAsString(builder, primaryKeys);
      log.info("using primaryKey as distributed key");
    }
  }

  private void getPkOrDkAsString(StringBuilder builder, List<String> primaryKeys) {
    String pk = getPrimaryKeyAsString(primaryKeys);
    builder.append("\n DISTRIBUTED BY (").append(pk).append(")");
  }

}
