// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.admin.controller.converter;

import cn.hutool.extra.spring.SpringUtil;
import com.gitee.dbswitch.admin.dao.AssignmentConfigDAO;
import com.gitee.dbswitch.admin.dao.AssignmentJobDAO;
import com.gitee.dbswitch.admin.dao.DatabaseConnectionDAO;
import com.gitee.dbswitch.admin.entity.AssignmentConfigEntity;
import com.gitee.dbswitch.admin.entity.AssignmentJobEntity;
import com.gitee.dbswitch.admin.entity.AssignmentTaskEntity;
import com.gitee.dbswitch.admin.entity.DatabaseConnectionEntity;
import com.gitee.dbswitch.admin.model.response.AssignmentInfoResponse;
import com.gitee.dbswitch.admin.type.JobStatusEnum;
import com.gitee.dbswitch.common.converter.AbstractConverter;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AssignmentInfoConverter extends
    AbstractConverter<AssignmentTaskEntity, AssignmentInfoResponse> {

  private final static String UNKNOWN_CONNECTION_TYPE = "Unknown";
  private AssignmentConfigDAO assignmentConfigDAO = SpringUtil.getBean(AssignmentConfigDAO.class);
  private DatabaseConnectionDAO databaseConnectionDAO = SpringUtil.getBean(DatabaseConnectionDAO.class);
  private AssignmentJobDAO assignmentJobDAO = SpringUtil.getBean(AssignmentJobDAO.class);

  @Override
  public AssignmentInfoResponse convert(AssignmentTaskEntity assignmentTaskEntity) {
    Long taskId = assignmentTaskEntity.getId();
    AssignmentConfigEntity assignmentConfigEntity = assignmentConfigDAO.getByAssignmentTaskId(taskId);
    Long sourceConnectionId = assignmentConfigEntity.getSourceConnectionId();
    Long targetConnectionId = assignmentConfigEntity.getTargetConnectionId();
    List<Long> connectionIds = Lists.newArrayList(sourceConnectionId, targetConnectionId);
    Map<Long, DatabaseConnectionEntity> databaseConnectionMap = databaseConnectionDAO.getByIds(connectionIds)
        .stream().collect(Collectors.toMap(DatabaseConnectionEntity::getId, Function.identity()));

    AssignmentInfoResponse response = new AssignmentInfoResponse();
    response.setId(assignmentTaskEntity.getId());
    response.setName(assignmentTaskEntity.getName());
    response.setDescription(assignmentTaskEntity.getDescription());
    response.setScheduleMode(assignmentTaskEntity.getScheduleMode());
    response.setCronExpression(assignmentTaskEntity.getCronExpression());
    response.setIsPublished(assignmentTaskEntity.getPublished());
    response.setCreateTime(assignmentTaskEntity.getCreateTime());
    response.setUpdateTime(assignmentTaskEntity.getUpdateTime());
    response.setSourceSchema(assignmentConfigEntity.getSourceSchema());
    response.setTargetSchema(assignmentConfigEntity.getTargetSchema());

    // 注意：这里有可能用户已经在数据源列表中将数据源删除了
    if (databaseConnectionMap.containsKey(sourceConnectionId)) {
      response.setSourceType(databaseConnectionMap.get(sourceConnectionId).getType().getName());
    } else {
      response.setSourceType(UNKNOWN_CONNECTION_TYPE);
    }
    if (databaseConnectionMap.containsKey(targetConnectionId)) {
      response.setTargetType(databaseConnectionMap.get(targetConnectionId).getType().getName());
    } else {
      response.setTargetType(UNKNOWN_CONNECTION_TYPE);
    }

    AssignmentJobEntity assignmentJobEntity = assignmentJobDAO.getLatestJobEntity(taskId);
    Integer status = (assignmentJobEntity == null || assignmentJobEntity.getStatus() == null) ?
        JobStatusEnum.INIT.getValue() :
        assignmentJobEntity.getStatus();
    response.setRunStatus(JobStatusEnum.of(status).getName());
    response.setScheduleTime(assignmentJobEntity == null ? null : assignmentJobEntity.getStartTime());

    return response;
  }
}
