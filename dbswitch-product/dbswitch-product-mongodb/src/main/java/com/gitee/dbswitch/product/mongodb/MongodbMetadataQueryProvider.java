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

import com.gitee.dbswitch.provider.ProductFactoryProvider;
import com.gitee.dbswitch.provider.meta.AbstractMetadataProvider;
import com.gitee.dbswitch.schema.ColumnDescription;
import com.gitee.dbswitch.schema.ColumnMetaData;
import com.gitee.dbswitch.schema.IndexDescription;
import com.gitee.dbswitch.schema.TableDescription;
import com.google.common.collect.Sets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;

@Slf4j
public class MongodbMetadataQueryProvider extends AbstractMetadataProvider {

  private static final Set<String> systemSchemas = Sets.newHashSet("admin", "config", "local");

  public MongodbMetadataQueryProvider(ProductFactoryProvider factoryProvider) {
    super(factoryProvider);
  }

  @Override
  public List<String> querySchemaList(Connection connection) {
    List<String> catalogs = new ArrayList<String>();
    try (ResultSet rs = connection.getMetaData().getCatalogs()) {
      while (rs.next()) {
        String name = rs.getString("TABLE_CAT");
        if (!systemSchemas.contains(name)) {
          catalogs.add(name);
        }
      }
      return catalogs;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<TableDescription> queryTableList(Connection connection, String schemaName) {
    List<TableDescription> ret = new ArrayList<>();
    try (ResultSet rs = connection.getMetaData().getTables(schemaName, null, null, null)) {
      while (rs.next()) {
        String tableName = rs.getString("TABLE_NAME");
        TableDescription td = new TableDescription();
        td.setSchemaName(schemaName);
        td.setTableName(tableName);
        td.setRemarks(rs.getString("REMARKS"));
        td.setTableType(rs.getString("TABLE_TYPE").toUpperCase());
        ret.add(td);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return ret;
  }

  @Override
  public TableDescription queryTableMeta(Connection connection, String schemaName, String tableName) {
    try (ResultSet rs = connection.getMetaData()
        .getTables(schemaName, null, tableName, new String[]{"TABLE"})) {
      if (rs.next()) {
        TableDescription td = new TableDescription();
        td.setSchemaName(schemaName);
        td.setTableName(tableName);
        td.setRemarks(rs.getString("REMARKS"));
        td.setTableType(rs.getString("TABLE_TYPE").toUpperCase());
        return td;
      }
      return null;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public String getTableDDL(Connection connection, String schemaName, String tableName) {
    return null;
  }

  public String getViewDDL(Connection connection, String schemaName, String tableName) {
    return null;
  }

  public List<String> queryTableColumnName(Connection connection, String schemaName, String tableName) {
    List<String> ret = new ArrayList<>();
    try (ResultSet rs = connection.getMetaData().getColumns(schemaName, null, tableName, null)) {
      while (rs.next()) {
        ret.add(rs.getString("COLUMN_NAME"));
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return ret;
  }

  public List<ColumnDescription> queryTableColumnMeta(Connection connection, String schemaName,
      String tableName) {
    List<ColumnDescription> ret = new ArrayList<>();
    try (ResultSet rs = connection.getMetaData().getColumns(schemaName, null, tableName, null)) {
      ResultSetMetaData m = rs.getMetaData();
      while (rs.next()) {
        ColumnDescription cd = new ColumnDescription();
        cd.setFieldName(rs.getString("COLUMN_NAME"));
        cd.setLabelName(rs.getString("COLUMN_NAME"));
        cd.setFieldType(NumberUtils.toInt(rs.getString("DATA_TYPE")));
        cd.setFieldTypeName(rs.getString("TYPE_NAME"));
        cd.setFiledTypeClassName(rs.getString("DATA_TYPE"));
        cd.setDisplaySize(0);
        cd.setPrecisionSize(0);
        cd.setScaleSize(0);
        cd.setAutoIncrement(false);
        cd.setNullable(!"_id".equals(cd.getFieldName()));
        cd.setProductType(getProductType());
        ret.add(cd);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return ret;
  }

  public List<ColumnDescription> querySelectSqlColumnMeta(Connection connection, String sql) {
    return Collections.emptyList();
  }

  public List<String> queryTablePrimaryKeys(Connection connection, String schemaName, String tableName) {
    List<String> ret = new ArrayList<>();
    try (ResultSet rs = connection.getMetaData().getPrimaryKeys(schemaName, null, tableName)) {
      while (rs.next()) {
        ret.add(rs.getString("COLUMN_NAME"));
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return ret;
  }

  public List<IndexDescription> queryTableIndexes(Connection connection, String schemaName, String tableName) {
    return Collections.emptyList();
  }

  public void testQuerySQL(Connection connection, String sql) {

  }

  public String getQuotedSchemaTableCombination(String schemaName, String tableName) {
    return String.format("%s.%s", schemaName, tableName);
  }

  public String getFieldDefinition(ColumnMetaData v, List<String> pks, boolean useAutoInc, boolean addCr,
      boolean withRemarks) {
    return null;
  }

  public String getPrimaryKeyAsString(List<String> pks) {
    return null;
  }

  public List<String> getTableColumnCommentDefinition(TableDescription td, List<ColumnDescription> cds) {
    return Collections.emptyList();
  }

}
