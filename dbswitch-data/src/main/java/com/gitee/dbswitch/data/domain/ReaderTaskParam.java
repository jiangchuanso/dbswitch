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

import com.gitee.dbswitch.common.entity.CloseableDataSource;
import com.gitee.dbswitch.core.exchange.MemChannel;
import com.gitee.dbswitch.core.task.TaskParam;
import com.gitee.dbswitch.data.config.DbswichPropertiesConfiguration;
import com.gitee.dbswitch.schema.TableDescription;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 读取任务线程的入参
 *
 * @author tang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReaderTaskParam implements TaskParam {

  private MemChannel memChannel;
  private TableDescription tableDescription;
  private DbswichPropertiesConfiguration configuration;
  private CloseableDataSource sourceDataSource;
  private CloseableDataSource targetDataSource;
  private Set<String> targetExistTables;
  private CountDownLatch countDownLatch;
}
