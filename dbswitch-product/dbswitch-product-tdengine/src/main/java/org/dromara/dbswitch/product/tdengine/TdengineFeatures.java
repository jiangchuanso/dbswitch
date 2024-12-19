// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: Li ZeMin (2413957313@qq.com)
// Date : 2024/12/16
// Location: nanjing , china
/////////////////////////////////////////////////////////////
package org.dromara.dbswitch.product.tdengine;

import org.dromara.dbswitch.core.features.ProductFeatures;

public class TdengineFeatures implements ProductFeatures {

  @Override
  public int convertFetchSize(int fetchSize) {
    return fetchSize;
  }

}
