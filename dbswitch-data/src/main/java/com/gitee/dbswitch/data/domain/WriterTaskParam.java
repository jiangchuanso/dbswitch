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

import com.gitee.dbswitch.core.exchange.MemChannel;
import com.gitee.dbswitch.core.robot.RobotReader;
import com.gitee.dbswitch.core.task.TaskParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 写入任务线程的执行结果
 *
 * @author tang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WriterTaskParam implements TaskParam {

  private MemChannel memChannel;
  private RobotReader robotReader;
}
