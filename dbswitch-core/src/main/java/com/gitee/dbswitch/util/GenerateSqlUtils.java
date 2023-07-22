// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.util;

import com.gitee.dbswitch.common.consts.Constants;
import com.gitee.dbswitch.common.type.ProductTableType;
import com.gitee.dbswitch.common.type.ProductTypeEnum;
import com.gitee.dbswitch.common.util.DDLFormatterUtils;
import com.gitee.dbswitch.provider.meta.MetadataProvider;
import com.gitee.dbswitch.schema.ColumnDescription;
import com.gitee.dbswitch.schema.ColumnMetaData;
import com.gitee.dbswitch.schema.TableDescription;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 * 拼接SQL工具类
 *
 * @author tang
 */
public final class GenerateSqlUtils {

  public static String getDDLCreateTableSQL(
      MetadataProvider provider,
      List<ColumnDescription> fieldNames,
      List<String> primaryKeys,
      String schemaName,
      String tableName,
      boolean autoIncr) {
    return getDDLCreateTableSQL(
        provider,
        fieldNames,
        primaryKeys,
        schemaName,
        tableName,
        false,
        null,
        autoIncr);
  }

  public static String getDDLCreateTableSQL(
      MetadataProvider provider,
      List<ColumnDescription> fieldNames,
      List<String> primaryKeys,
      String schemaName,
      String tableName,
      boolean withRemarks,
      String tableRemarks,
      boolean autoIncr) {
    ProductTypeEnum type = provider.getProductType();
    StringBuilder sb = new StringBuilder();
    List<String> pks = fieldNames.stream()
        .filter((cd) -> primaryKeys.contains(cd.getFieldName()))
        .map((cd) -> cd.getFieldName())
        .collect(Collectors.toList());

    sb.append(Constants.CREATE_TABLE);
    // if(ifNotExist && type!=DatabaseType.ORACLE) {
    // sb.append( Const.IF_NOT_EXISTS );
    // }
    sb.append(provider.getQuotedSchemaTableCombination(schemaName, tableName));
    sb.append("(");

    for (int i = 0; i < fieldNames.size(); i++) {
      if (i > 0) {
        sb.append(", ");
      } else {
        sb.append("  ");
      }

      ColumnMetaData v = fieldNames.get(i).getMetaData();
      sb.append(provider.getFieldDefinition(v, pks, autoIncr, false, withRemarks));
    }

    if (!pks.isEmpty()) {
      String pk = provider.getPrimaryKeyAsString(pks);
      sb.append(", PRIMARY KEY (").append(pk).append(")");
    }

    sb.append(")");
    if (ProductTypeEnum.MYSQL == type) {
      sb.append("ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin");
      if (withRemarks && StringUtils.isNotBlank(tableRemarks)) {
        sb.append(String.format(" COMMENT='%s' ", tableRemarks.replace("'", "\\'")));
      }
    }

    return DDLFormatterUtils.format(sb.toString());
  }

  public static List<String> getDDLCreateTableSQL(
      MetadataProvider provider,
      List<ColumnDescription> fieldNames,
      List<String> primaryKeys,
      String schemaName,
      String tableName,
      String tableRemarks,
      boolean autoIncr) {
    String createTableSql = getDDLCreateTableSQL(provider, fieldNames, primaryKeys, schemaName,
        tableName, true, tableRemarks, autoIncr);
    if (provider.getProductType().noCommentStatement()) {
      return Arrays.asList(createTableSql);
    }

    TableDescription td = new TableDescription();
    td.setSchemaName(schemaName);
    td.setTableName(tableName);
    td.setRemarks(tableRemarks);
    td.setTableType(ProductTableType.TABLE.name());
    List<String> results = provider.getTableColumnCommentDefinition(td, fieldNames);
    results.add(0, createTableSql);
    return results;
  }

  private GenerateSqlUtils() {
    throw new IllegalStateException();
  }

}
