// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.provider.meta;

import cn.hutool.core.text.StrPool;
import com.gitee.dbswitch.common.type.ProductTypeEnum;
import com.gitee.dbswitch.common.util.HivePrepareUtils;
import com.gitee.dbswitch.provider.AbstractCommonProvider;
import com.gitee.dbswitch.provider.ProductFactoryProvider;
import com.gitee.dbswitch.schema.ColumnDescription;
import com.gitee.dbswitch.schema.ColumnMetaData;
import com.gitee.dbswitch.schema.TableDescription;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 数据库元信息抽象基类
 *
 * @author tang
 */
@Slf4j
public abstract class AbstractMetadataProvider
    extends AbstractCommonProvider
    implements MetadataProvider {

  protected String catalogName = null;

  protected AbstractMetadataProvider(ProductFactoryProvider factoryProvider) {
    super(factoryProvider);
  }

  public void setCatalogName(String catalogName) {
    this.catalogName = catalogName;
  }

  @Override
  public List<String> querySchemaList(Connection connection) {
    Set<String> ret = new LinkedHashSet<>();
    try (ResultSet schemas = connection.getMetaData().getSchemas()) {
      while (schemas.next()) {
        ret.add(schemas.getString("TABLE_SCHEM"));
      }
      return new ArrayList<>(ret);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<TableDescription> queryTableList(Connection connection, String schemaName) {
    List<TableDescription> ret = new ArrayList<>();
    Set<String> uniqueSet = new LinkedHashSet<>();
    String[] types = new String[]{"TABLE", "VIEW"};
    try (ResultSet tables = connection.getMetaData()
        .getTables(catalogName, schemaName, "%", types)) {
      while (tables.next()) {
        String tableName = tables.getString("TABLE_NAME");
        if (uniqueSet.contains(tableName)) {
          continue;
        } else {
          uniqueSet.add(tableName);
        }

        TableDescription td = new TableDescription();
        td.setSchemaName(schemaName);
        td.setTableName(tableName);
        td.setRemarks(tables.getString("REMARKS"));
        td.setTableType(tables.getString("TABLE_TYPE").toUpperCase());
        ret.add(td);
      }
      return ret;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public TableDescription queryTableMeta(Connection connection, String schemaName,
      String tableName) {
    return queryTableList(connection, schemaName).stream()
        .filter(one -> tableName.equals(one.getTableName()))
        .findAny().orElse(null);
  }

  @Override
  public List<String> queryTableColumnName(Connection connection, String schemaName,
      String tableName) {
    Set<String> columns = new LinkedHashSet<>();
    try (ResultSet rs = connection.getMetaData()
        .getColumns(catalogName, schemaName, tableName, null)) {
      while (rs.next()) {
        columns.add(rs.getString("COLUMN_NAME"));
      }
      return new ArrayList<>(columns);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<ColumnDescription> queryTableColumnMeta(Connection connection, String schemaName,
      String tableName) {
    String sql = this.getTableFieldsQuerySQL(schemaName, tableName);
    List<ColumnDescription> ret = this.querySelectSqlColumnMeta(connection, sql);

    // 补充一下注释信息
    try (ResultSet columns = connection.getMetaData()
        .getColumns(catalogName, schemaName, tableName, null)) {
      while (columns.next()) {
        String columnName = columns.getString("COLUMN_NAME");
        String remarks = columns.getString("REMARKS");
        for (ColumnDescription cd : ret) {
          if (columnName.equals(cd.getFieldName())) {
            cd.setRemarks(remarks);
          }
        }
      }
      return ret;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<String> queryTablePrimaryKeys(Connection connection, String schemaName,
      String tableName) {
    Set<String> ret = new LinkedHashSet<>();
    try (ResultSet primaryKeys = connection.getMetaData()
        .getPrimaryKeys(catalogName, schemaName, tableName)) {
      while (primaryKeys.next()) {
        ret.add(primaryKeys.getString("COLUMN_NAME"));
      }
      return new ArrayList<>(ret);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getQuotedSchemaTableCombination(String schemaName, String tableName) {
    return getProductType().quoteSchemaTableName(schemaName, tableName);
  }

  @Override
  public String getFieldDefinition(ColumnMetaData v, List<String> pks, boolean useAutoInc,
      boolean addCr, boolean withRemarks) {
    throw new RuntimeException("AbstractDatabase Unimplemented!");
  }

  @Override
  public String getPrimaryKeyAsString(List<String> pks) {
    if (!pks.isEmpty()) {
      ProductTypeEnum productType = getProductType();
      return productType.quoteName(StringUtils.join(pks, productType.quoteName(StrPool.COMMA)));
    }
    return StringUtils.EMPTY;
  }

  @Override
  public List<String> getTableColumnCommentDefinition(TableDescription td,
      List<ColumnDescription> cds) {
    throw new RuntimeException("AbstractDatabase Unimplemented!");
  }

  /**
   * 执行写SQL操作
   *
   * @param sql 写SQL语句
   */
  protected final int executeSql(String sql) {
    if (log.isDebugEnabled()) {
      log.debug("Execute sql :{}", sql);
    }
    try (Connection connection = getDataSource().getConnection();
        Statement st = connection.createStatement()) {
      return st.executeUpdate(sql);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**************************************
   * internal function
   **************************************/

  protected List<ColumnDescription> getSelectSqlColumnMeta(Connection connection, String querySQL) {
    List<ColumnDescription> ret = new ArrayList<>();
    try (Statement st = connection.createStatement()) {
      if (getProductType() == ProductTypeEnum.HIVE) {
        HivePrepareUtils.setResultSetColumnNameNotUnique(connection);
      }

      try (ResultSet rs = st.executeQuery(querySQL)) {
        ResultSetMetaData m = rs.getMetaData();
        int columns = m.getColumnCount();
        for (int i = 1; i <= columns; i++) {
          String name = m.getColumnLabel(i);
          if (null == name) {
            name = m.getColumnName(i);
          }

          ColumnDescription cd = new ColumnDescription();
          cd.setFieldName(name);
          cd.setLabelName(name);
          cd.setFieldType(m.getColumnType(i));
          if (0 != cd.getFieldType()) {
            cd.setFieldTypeName(m.getColumnTypeName(i));
            cd.setFiledTypeClassName(m.getColumnClassName(i));
            cd.setDisplaySize(m.getColumnDisplaySize(i));
            cd.setPrecisionSize(m.getPrecision(i));
            cd.setScaleSize(m.getScale(i));
            cd.setAutoIncrement(m.isAutoIncrement(i));
            cd.setNullable(m.isNullable(i) != ResultSetMetaData.columnNoNulls);
          } else {
            // 处理视图中NULL as fieldName的情况
            cd.setFieldTypeName("CHAR");
            cd.setFiledTypeClassName(String.class.getName());
            cd.setDisplaySize(1);
            cd.setPrecisionSize(1);
            cd.setScaleSize(0);
            cd.setAutoIncrement(false);
            cd.setNullable(true);
          }

          boolean signed = false;
          try {
            signed = m.isSigned(i);
          } catch (Exception ignored) {
            // This JDBC Driver doesn't support the isSigned method
            // nothing more we can do here by catch the exception.
          }
          cd.setSigned(signed);
          cd.setProductType(getProductType());

          ret.add(cd);
        }

        return ret;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

}