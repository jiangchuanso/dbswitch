// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.common.event;

import com.google.common.eventbus.AsyncEventBus;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

public class TaskEventHub {

  private AsyncEventBus asyncEventBus;

  public TaskEventHub(String identifier, int poolSize) {
    this.asyncEventBus = new AsyncEventBus(identifier,
        new ThreadPoolExecutor(
            poolSize,
            poolSize,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            new BasicThreadFactory.Builder()
                .namingPattern(identifier + "-%d")
                .build()
        )
    );
  }

  public void registerSubscriber(EventSubscriber subscriber) {
    asyncEventBus.register(subscriber);
  }

  public void notifyEvent(@Nullable Object... args) {
    String identifier = asyncEventBus.identifier();
    asyncEventBus.post(new ListenedEvent(this, identifier, args));
  }
}
