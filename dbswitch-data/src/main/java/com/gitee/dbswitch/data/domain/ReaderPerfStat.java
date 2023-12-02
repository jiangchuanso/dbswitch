// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.data.domain;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.unit.DataSizeUtil;
import com.gitee.dbswitch.common.entity.PrintablePerfStat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 并发读取统计格式化信息
 *
 * @author tang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReaderPerfStat extends PrintablePerfStat {

  private long total;
  private long failure;
  private long bytes;
  private Throwable throwable;

  @Override
  public String getPrintableString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Total Tables Count: \t" + total + "\n" +
        "Failure Tables count: \t" + failure + "\n" +
        "Total Transfer Size: \t" + DataSizeUtil.format(bytes) + "\n");
    if (null != throwable) {
      sb.append("Stack:" + ExceptionUtil.stacktraceToString(throwable));
    }
    return sb.toString();
  }
}
