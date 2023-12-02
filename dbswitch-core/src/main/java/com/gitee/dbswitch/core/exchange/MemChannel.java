// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.core.exchange;

import java.util.concurrent.ConcurrentLinkedQueue;

public class MemChannel extends ConcurrentLinkedQueue<BatchElement> {

  public static MemChannel createNewChannel() {
    return new MemChannel();
  }

  @Override
  public boolean add(BatchElement elem) {
    return super.add(elem);
  }

  @Override
  public BatchElement poll() {
    return super.poll();
  }

}
