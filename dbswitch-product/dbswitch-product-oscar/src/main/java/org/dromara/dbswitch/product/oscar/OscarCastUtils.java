// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package org.dromara.dbswitch.product.oscar;

import org.dromara.dbswitch.common.util.ObjectCastUtils;
import java.sql.Date;
import java.time.LocalDate;

public final class OscarCastUtils {

  private OscarCastUtils() {
  }

  public static Date castToSqlDate(Object value) {
    if (value == null) {
      return null;
    }
    LocalDate localDate = ObjectCastUtils.castToLocalDate(value);
    return localDate == null ? null : Date.valueOf(localDate);
  }
}
