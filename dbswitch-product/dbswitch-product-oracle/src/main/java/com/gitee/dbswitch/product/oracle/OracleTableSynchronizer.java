// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.product.oracle;

import com.gitee.dbswitch.common.util.TypeConvertUtils;
import com.gitee.dbswitch.provider.ProductFactoryProvider;
import com.gitee.dbswitch.provider.sync.DefaultTableDataSynchronizer;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.jdbc.core.SqlTypeValue;

public class OracleTableSynchronizer extends DefaultTableDataSynchronizer {

  public OracleTableSynchronizer(ProductFactoryProvider factoryProvider) {
    super(factoryProvider);
  }

  @Override
  public long executeInsert(List<Object[]> records) {
    List<InputStream> iss = new ArrayList<>();
    records.parallelStream().forEach((Object[] row) -> {
      for (int i = 0; i < row.length; ++i) {
        try {
          switch (this.insertArgsType[i]) {
            case Types.CLOB:
            case Types.NCLOB:
              row[i] = Objects.isNull(row[i])
                  ? null
                  : TypeConvertUtils.castToString(row[i]);
              break;
            case Types.BLOB:
              final byte[] bytes = Objects.isNull(row[i])
                  ? null
                  : TypeConvertUtils.castToByteArray(row[i]);
              row[i] = new SqlTypeValue() {
                @Override
                public void setTypeValue(PreparedStatement ps, int paramIndex, int sqlType,
                    String typeName) throws SQLException {
                  if (null != bytes) {
                    InputStream is = new ByteArrayInputStream(bytes);
                    ps.setBlob(paramIndex, is);
                    iss.add(is);
                  } else {
                    ps.setNull(paramIndex, sqlType);
                  }
                }
              };
              break;
            case Types.ROWID:
            case Types.ARRAY:
            case Types.REF:
            case Types.SQLXML:
              row[i] = null;
              break;
            default:
              break;
          }
        } catch (Exception e) {
          row[i] = null;
        }
      }
    });

    try {
      return super.executeInsert(records);
    } finally {
      iss.forEach(is -> {
        try {
          is.close();
        } catch (Exception ignore) {
        }
      });
    }
  }

  @Override
  public long executeUpdate(List<Object[]> records) {
    List<InputStream> iss = new ArrayList<>();
    records.parallelStream().forEach((Object[] row) -> {
      for (int i = 0; i < row.length; ++i) {
        try {
          switch (this.updateArgsType[i]) {
            case Types.CLOB:
            case Types.NCLOB:
              row[i] = Objects.isNull(row[i])
                  ? null
                  : TypeConvertUtils.castToString(row[i]);
              break;
            case Types.BLOB:
              final byte[] bytes = Objects.isNull(row[i])
                  ? null
                  : TypeConvertUtils.castToByteArray(row[i]);
              row[i] = new SqlTypeValue() {
                @Override
                public void setTypeValue(PreparedStatement ps, int paramIndex, int sqlType,
                    String typeName) throws SQLException {
                  if (null != bytes) {
                    InputStream is = new ByteArrayInputStream(bytes);
                    ps.setBlob(paramIndex, is);
                    iss.add(is);
                  } else {
                    ps.setNull(paramIndex, sqlType);
                  }
                }
              };
              break;
            case Types.ROWID:
            case Types.ARRAY:
            case Types.REF:
            case Types.SQLXML:
              row[i] = null;
              break;
            default:
              break;
          }
        } catch (Exception e) {
          row[i] = null;
        }
      }
    });

    try {
      return super.executeUpdate(records);
    } finally {
      iss.forEach(is -> {
        try {
          is.close();
        } catch (Exception ignore) {
        }
      });
    }
  }

}
