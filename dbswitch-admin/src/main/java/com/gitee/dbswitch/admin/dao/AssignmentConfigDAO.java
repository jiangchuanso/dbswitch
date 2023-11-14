// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.admin.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gitee.dbswitch.admin.entity.AssignmentConfigEntity;
import com.gitee.dbswitch.admin.mapper.AssignmentConfigMapper;
import javax.annotation.Resource;
import org.springframework.stereotype.Repository;

@Repository
public class AssignmentConfigDAO {

  @Resource
  private AssignmentConfigMapper assignmentConfigMapper;

  public void insert(AssignmentConfigEntity assignmentConfigEntity) {
    assignmentConfigMapper.insert(assignmentConfigEntity);
  }

  public AssignmentConfigEntity getById(Long id) {
    return assignmentConfigMapper.selectById(id);
  }

  public AssignmentConfigEntity getByAssignmentTaskId(Long taskId) {
    QueryWrapper<AssignmentConfigEntity> queryWrapper = new QueryWrapper<>();
    queryWrapper.lambda().eq(AssignmentConfigEntity::getAssignmentId, taskId);
    return assignmentConfigMapper.selectOne(queryWrapper);
  }

  public void updateSelective(AssignmentConfigEntity assignmentConfigEntity) {
    assignmentConfigMapper.updateById(assignmentConfigEntity);
  }

  public void deleteByAssignmentTaskId(Long taskId) {
    QueryWrapper<AssignmentConfigEntity> queryWrapper = new QueryWrapper<>();
    queryWrapper.lambda().eq(AssignmentConfigEntity::getAssignmentId, taskId);
    assignmentConfigMapper.delete(queryWrapper);
  }

}
