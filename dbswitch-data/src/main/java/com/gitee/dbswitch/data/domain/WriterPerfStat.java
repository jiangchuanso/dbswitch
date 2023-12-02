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
import com.gitee.dbswitch.common.entity.PrintablePerfStat;

/**
 * 并发写入统计格式化信息
 *
 * @author tang
 */
public class WriterPerfStat extends PrintablePerfStat {

  private long duration;
  private Throwable throwable;

  public WriterPerfStat(long duration, Throwable throwable) {
    this.duration = duration;
    this.throwable = throwable;
  }

  @Override
  public String getPrintableString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Total Writer Duration: \t" + (duration / 1000.0) + " s \n");
    if (null != throwable) {
      sb.append("Stack:" + ExceptionUtil.stacktraceToString(throwable));
    }
    return sb.toString();
  }
}
