// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.common.type;

public enum TableIndexEnum {

  NORMAL("普通索引"),
  UNIQUE("唯一索引"),
  ;

  private String description;

  TableIndexEnum(String description) {
    this.description = description;
  }

  public boolean isUnique() {
    return UNIQUE == this;
  }

}
