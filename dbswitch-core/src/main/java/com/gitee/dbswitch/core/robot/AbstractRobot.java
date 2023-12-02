// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.core.robot;

import com.gitee.dbswitch.core.exchange.MemChannel;
import com.gitee.dbswitch.core.task.TaskResult;
import java.util.Optional;

public abstract class AbstractRobot<R extends TaskResult> implements Robot {

  private volatile boolean interrupted = false;
  private MemChannel channel;

  public void setChannel(MemChannel channel) {
    this.channel = channel;
  }

  public MemChannel getChannel() {
    return channel;
  }

  public void interrupt() {
    interrupted = true;
  }

  protected void checkInterrupt() {
    if (interrupted) {
      throw new RuntimeException("task is interrupted");
    }
  }

  public abstract Optional<R> getWorkResult();
}
