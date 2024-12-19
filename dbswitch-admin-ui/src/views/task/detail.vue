<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-click="handleClick">

      <el-tab-pane label="基本信息" name="first">

        <el-card class="box-card">
          <el-row class="row-gutter">
            <el-col :span="2">
              <label class="key-text">任务ID</label>
            </el-col>
            <el-col :span="6">
              <label class="value-text">{{ infoform.id }}</label>
            </el-col>
            <el-col :span="2">
              <label class="key-text">任务名称</label>
            </el-col>
            <el-col :span="6">
              <label class="value-text">{{ infoform.name }}</label>
            </el-col>
            <el-col :span="2">
              <label class="key-text">任务类型</label>
            </el-col>
            <el-col :span="6">
              <label class="value-text">
                普通任务
              </label>
            </el-col>
          </el-row>
          <el-row class="row-gutter">
            <el-col :span="2">
              <label class="key-text">任务状态</label>
            </el-col>
            <el-col :span="6">
              <label class="value-text">
                {{ $route.query.record.runStatus }}
              </label>
            </el-col>
            <el-col :span="2">
              <label class="key-text">运行状态</label>
            </el-col>
            <el-col :span="6">
              <label class="value-text">
                {{ $route.query.record.isPublished === true ? '启动' : '停止' }}
              </label>
            </el-col>
            <el-col :span="2">
              <label class="key-text">集成模式</label>
            </el-col>
            <el-col :span="6">
              <label class="value-text"><span v-if="infoform.scheduleMode == 'MANUAL'">
            手动
          </span>
                <span v-if="infoform.scheduleMode == 'SYSTEM_SCHEDULED'">
            定时
          </span></label>
            </el-col>
          </el-row>
          <el-row class="row-gutter">
            <el-col :span="2">
              <label class="key-text">调度计划</label>
            </el-col>
            <el-col :span="6">
              <label class="value-text">
                <span v-if="infoform.scheduleMode == 'MANUAL'">
                  --
                </span>
                <span v-if="infoform.scheduleMode == 'SYSTEM_SCHEDULED'">
                  {{ infoform.cronExpression }}
                </span>
              </label>
            </el-col>
            <el-col :span="2">
              <label class="key-text">开始调度时间</label>
            </el-col>
            <el-col :span="6">
              <label class="value-text">{{ $route.query.record.scheduleTime }}</label>
            </el-col>
            <el-col :span="2">
              <label class="key-text">创建时间</label>
            </el-col>
            <el-col :span="6">
              <label class="value-text">{{ $route.query.record.createTime }}</label>
            </el-col>
          </el-row>
          <el-row class="row-gutter">
            <el-col :span="2">
              <label class="key-text">描述</label>
            </el-col>
            <el-col :span="6">
              <label class="value-text">{{ infoform.description }}</label>
            </el-col>
            <el-col :span="2">
              <label class="key-text">任务标签</label>
            </el-col>
            <el-col :span="6">
              <label class="value-text">--</label>
            </el-col>
            <el-col :span="8">

            </el-col>
          </el-row>
        </el-card>

        <div class="common-box">
          <div class="datainfo">
            <div class="source">
              <div class="head">
                <div class="head-img">
                  <el-image
                      style="width: 60px; height: 60px"
                      :src="require('@/assets/icons/' + $route.query.record.sourceType +'.png')"
                      :fit="fit"></el-image>
                </div>
                <div class="head-text">
                  <div class="title">{{ infoform.sourceConnectionName }}</div>
                  <div class="sub-title">源端数据源</div>
                </div>
              </div>
              <div class="body">
                <el-row class="row-gutter">
                  <el-col :span="8">
                    <label class="key-text">源端schema</label>
                  </el-col>
                  <el-col :span="16">
                    <label class="value-text">{{ infoform.sourceSchema }}</label>
                  </el-col>
                </el-row>
                <el-row class="row-gutter">
                  <el-col :span="8">
                    <label class="key-text">源端表类型</label>
                  </el-col>
                  <el-col :span="16">
                    <label class="value-text">{{ infoform.tableType }}</label>
                  </el-col>
                </el-row>
                <el-row class="row-gutter">
                  <el-col :span="8">
                    <label class="key-text">源端表选择方式</label>
                  </el-col>
                  <el-col :span="16">
                    <label class="value-text">{{
                        infoform.includeOrExclude === 'INCLUDE' ? '包含表' : '排除表'
                      }}</label>
                  </el-col>
                </el-row>
                <el-row class="row-gutter">
                  <el-col :span="8">
                    <label class="key-text">源端表名列表</label>
                  </el-col>
                  <el-col :span="16">
                    <label class="value-text" v-for="item in infoform.sourceTables"
                           v-bind:key="item">{{ item }}</label>
                  </el-col>
                </el-row>


              </div>
            </div>
            <div class="target">
              <div class="head">
                <div class="head-img">
                  <el-image
                      style="width: 60px; height: 60px"
                      :src="require('@/assets/icons/' + $route.query.record.targetType +'.png')"
                      :fit="fit"></el-image>
                </div>
                <div class="head-text">
                  <div class="title">{{ infoform.targetConnectionName }}</div>
                  <div class="sub-title">目标端数据源</div>
                </div>
              </div>
              <div class="body">
                <el-row class="row-gutter">
                  <el-col :span="8">
                    <label class="key-text">目地端schema</label>
                  </el-col>
                  <el-col :span="16">
                    <label class="value-text">{{ infoform.targetSchema }}</label>
                  </el-col>
                </el-row>
                <el-row class="row-gutter">
                  <el-col :span="8">
                    <label class="key-text">自动同步模式</label>
                  </el-col>
                  <el-col :span="16">
                                <span class="value-text" v-if="infoform.autoSyncMode == 2">
                                  目标端建表并同步数据
                                </span>
                    <span class="value-text" v-if="infoform.autoSyncMode == 1">
                                  目标端只创建物理表
                                </span>
                    <span class="value-text" v-if="infoform.autoSyncMode == 0">
                                  目标端只同步表里数据
                                </span>

                  </el-col>
                </el-row>
                <el-row class="row-gutter">
                  <el-col :span="8">
                    <label class="key-text">建表字段自增</label>
                  </el-col>
                  <el-col :span="16">
                    <label class="value-text"
                           v-if=" infoform.autoSyncMode!==0 ">{{ infoform.targetAutoIncrement }}
                    </label>
                  </el-col>
                </el-row>
                <el-row class="row-gutter">
                  <el-col :span="8">
                    <label class="key-text">表名转换方法</label>
                  </el-col>
                  <el-col :span="16">
                    <label class="value-text" v-if=" infoform.autoSyncMode!==0 ">
                                  <span v-if="infoform.tableNameCase == 'NONE'">
                                    无转换
                                  </span>
                      <span v-if="infoform.tableNameCase == 'UPPER'">
                                    转大写
                                  </span>
                      <span v-if="infoform.tableNameCase == 'LOWER'">
                                    转小写
                                  </span>
                      <span v-if="infoform.tableNameCase == 'CAMEL'">
                                    下划线转驼峰
                                  </span>
                      <span v-if="infoform.tableNameCase == 'SNAKE'">
                                    驼峰转下换线
                                  </span>
                    </label>
                  </el-col>
                </el-row>
                <el-row class="row-gutter">
                  <el-col :span="8">
                    <label class="key-text">列名转换方法</label>
                  </el-col>
                  <el-col :span="16">


                    <label class="value-text" v-if=" infoform.autoSyncMode!==0 ">
          <span v-if="infoform.columnNameCase == 'NONE'">
            无转换
          </span>
                      <span v-if="infoform.columnNameCase == 'UPPER'">
            转大写
          </span>
                      <span v-if="infoform.columnNameCase == 'LOWER'">
            转小写
          </span>
                      <span v-if="infoform.columnNameCase == 'CAMEL'">
            下划线转驼峰
          </span>
                      <span v-if="infoform.columnNameCase == 'SNAKE'">
            驼峰转下换线
          </span>
                    </label>
                  </el-col>
                </el-row>
                <el-row class="row-gutter">
                  <el-col :span="8">
                    <label class="key-text">数据批次大小</label>
                  </el-col>
                  <el-col :span="16">
                    <label class="value-text"
                           v-if=" infoform.autoSyncMode!==1 ">{{ infoform.batchSize }}
                    </label>
                  </el-col>
                </el-row>
                <el-row class="row-gutter">
                  <el-col :span="8">
                    <label class="key-text">通道队列大小</label>
                  </el-col>
                  <el-col :span="16">
                    <label class="value-text"
                           v-if=" infoform.autoSyncMode!==1 ">{{ infoform.channelSize }}
                    </label>
                  </el-col>
                </el-row>
                <el-row class="row-gutter">
                  <el-col :span="8">
                    <label class="key-text">同步操作方法</label>
                  </el-col>
                  <el-col :span="16">
                    <label class="value-text"
                           v-if="infoform.autoSyncMode!==1 ">{{ infoform.targetSyncOption }}
                    </label>
                  </el-col>
                </el-row>
                <el-row class="row-gutter">
                  <el-col :span="8">
                    <label class="key-text">同步前置执行SQL脚本</label>
                  </el-col>
                  <el-col :span="16">
                    <label class="value-text"
                           v-if=" infoform.autoSyncMode!==1 ">
                      <span
                          v-show="!infoform.beforeSqlScripts || infoform.beforeSqlScripts.length==0">[SQL脚本内容为空]</span>
                      <span
                          v-show="infoform.beforeSqlScripts && infoform.beforeSqlScripts.length>0">{{
                          infoform.beforeSqlScripts
                        }}</span>
                    </label>

                  </el-col>
                </el-row>
                <el-row class="row-gutter">
                  <el-col :span="8">
                    <label class="key-text">同步后置执行SQL脚本</label>
                  </el-col>
                  <el-col :span="16">
                    <label class="value-text"
                           v-if=" infoform.autoSyncMode!==1 ">
                      <span
                          v-show="!infoform.afterSqlScripts || infoform.afterSqlScripts.length==0">[SQL脚本内容为空]</span>
                      <span
                          v-show="infoform.afterSqlScripts && infoform.afterSqlScripts.length>0">{{
                          infoform.afterSqlScripts
                        }}</span>
                    </label>

                  </el-col>
                </el-row>

              </div>
            </div>
          </div>

          <div class="mapper">
            <div class="table-left">
              <div>表名映射规则</div>
              <div>
                <el-table
                    :data="infoform.tableNameMapper"
                    style="width: 100%"
                    :row-class-name="tableRowClassName">
                  <el-table-column
                      prop="fromPattern"
                      label="表名匹配的正则名">
                  </el-table-column>
                  <el-table-column
                      prop="toValue"
                      label="替换的目标值">
                  </el-table-column>
                </el-table>
              </div>
            </div>

            <div class="table-right">
              <div>字段名映射规则</div>
              <div>
                <el-table
                    :data="infoform.columnNameMapper"
                    style="width: 100%"
                    :row-class-name="tableRowClassName">
                  <el-table-column
                      prop="fromPattern"
                      label="表名匹配的正则名">
                  </el-table-column>
                  <el-table-column
                      prop="toValue"
                      label="替换的目标值">
                  </el-table-column>
                </el-table>
              </div>

            </div>
          </div>
        </div>

      </el-tab-pane>
      <!--      <el-tab-pane label="调度日志" name="second">调度日志22</el-tab-pane>-->
      <!--      <el-tab-pane label="操作日志" name="third">操作日志33</el-tab-pane>-->
    </el-tabs>
  </div>

</template>
<script>
export default {

  data() {
    return {
      activeName: 'first',
      infoform: {
        id: 0,
        name: "",
        description: "",
        scheduleMode: "MANUAL",
        cronExpression: "",
        sourceConnectionId: '请选择',
        sourceSchema: "",
        runStatus: "",
        tableType: "TABLE",
        includeOrExclude: "",
        sourceTables: [],
        tableNameMapper: [],
        columnNameMapper: [],
        tableNameCase: 'NONE',
        columnNameCase: 'NONE',
        targetConnectionId: '请选择',
        targetDropTable: true,
        targetOnlyCreate: false,
        autoSyncMode: 2,
        targetSchema: "",
        batchSize: 5000,
        channelSize: 100,
        targetSyncOption: 'INSERT_UPDATE_DELETE',
        beforeSqlScripts: '',
        afterSqlScripts: '',
      },
      sourceConnection: {},
      targetConnection: {},
      sourceConnectionSchemas: [],
      sourceSchemaTables: [],
      targetConnectionSchemas: [],
    }
  },
  methods: {
    loadAssignmentDetail: function () {
      this.$http.get(
          "/dbswitch/admin/api/v1/assignment/detail/id/" + this.$route.query.id
      ).then(res => {
        if (0 === res.data.code) {
          let detail = res.data.data;
          let varAutoSyncMode = 2;
          if (detail.configuration.targetDropTable && detail.configuration.targetOnlyCreate) {
            varAutoSyncMode = 1;
          } else if (!detail.configuration.targetDropTable && !detail.configuration.targetOnlyCreate) {
            varAutoSyncMode = 0;
          } else {
            varAutoSyncMode = 2;
          }
          debugger
          this.infoform = {
            id: detail.id,
            name: detail.name,
            description: detail.description,
            scheduleMode: detail.scheduleMode,
            cronExpression: detail.cronExpression,
            sourceConnectionId: detail.configuration.sourceConnectionId,
            sourceConnectionName: detail.configuration.sourceConnectionName,
            sourceSchema: detail.configuration.sourceSchema,
            tableType: detail.configuration.tableType,
            includeOrExclude: detail.configuration.includeOrExclude,
            sourceTables: detail.configuration.sourceTables,
            tableNameMapper: detail.configuration.tableNameMapper,
            columnNameMapper: detail.configuration.columnNameMapper,
            tableNameCase: detail.configuration.tableNameCase,
            columnNameCase: detail.configuration.columnNameCase,
            targetConnectionId: detail.configuration.targetConnectionId,
            targetConnectionName: detail.configuration.targetConnectionName,
            targetDropTable: detail.configuration.targetDropTable,
            targetOnlyCreate: detail.configuration.targetOnlyCreate,
            targetAutoIncrement: detail.configuration.targetAutoIncrement,
            autoSyncMode: varAutoSyncMode,
            targetSchema: detail.configuration.targetSchema,
            batchSize: detail.configuration.batchSize,
            channelSize: detail.configuration.channelSize,
            targetSyncOption: detail.configuration.targetSyncOption,
            beforeSqlScripts: detail.configuration.beforeSqlScripts,
            afterSqlScripts: detail.configuration.afterSqlScripts,
          }
          this.selectChangedSourceConnection(this.infoform.sourceConnectionId)
          this.selectUpdateChangedSourceSchema(this.infoform.sourceSchema)
          this.selectChangedTargetConnection(this.infoform.targetConnectionId)
        } else {
          if (res.data.message) {
            alert("查询任务失败," + res.data.message);
          }
        }
      });
    },
    selectChangedSourceConnection: function (value) {
      this.sourceConnection = this.connectionNameList.find(
          (item) => {
            return item.id === value;
          });

      this.sourceConnectionSchemas = [];
      this.$http.get(
          "/dbswitch/admin/api/v1/connection/schemas/get/" + value
      ).then(res => {
        if (0 === res.data.code) {
          this.sourceConnectionSchemas = res.data.data;
        } else {
          this.$message.error("查询来源端数据库的Schema失败," + res.data.message);
          this.sourceConnectionSchemas = [];
        }
      });
    },
    selectUpdateChangedSourceSchema: function (value) {
      this.sourceSchemaTables = [];
      if ('TABLE' === this.infoform.tableType) {
        this.$http.get(
            "/dbswitch/admin/api/v1/connection/tables/get/" + this.infoform.sourceConnectionId + "?schema=" + value
        ).then(res => {
          if (0 === res.data.code) {
            this.sourceSchemaTables = res.data.data;
          } else {
            this.$message.error("查询来源端数据库在指定Schema下的物理表列表失败," + res.data.message);
            this.sourceSchemaTables = [];
          }
        });
      } else {
        this.$http.get(
            "/dbswitch/admin/api/v1/connection/views/get/" + this.infoform.sourceConnectionId + "?schema=" + value
        ).then(res => {
          if (0 === res.data.code) {
            this.sourceSchemaTables = res.data.data;
          } else {
            this.$message.error("查询来源端数据库在指定Schema下的视图表列表失败," + res.data.message);
            this.sourceSchemaTables = [];
          }
        });
      }
    },
    selectChangedTargetConnection: function (value) {
      this.targetConnection = this.connectionNameList.find(
          (item) => {
            return item.id === value;
          });

      this.targetConnectionSchemas = [];
      this.$http.get(
          "/dbswitch/admin/api/v1/connection/schemas/get/" + value
      ).then(res => {
        if (0 === res.data.code) {
          this.targetConnectionSchemas = res.data.data;
        } else {
          this.$message.error("查询目的端数据库的Schema失败," + res.data.message);
          this.targetConnectionSchemas = [];
        }
      });
    },
    handleGoBack() {
      this.$router.go(-1);
    },
    handleClick(tab, event) {
      if (tab.index !== 0) {
        this.$message({
          message: '功能暂未开放，敬请期待！',
          center: true
        });
      }
    },
    tableRowClassName({row, rowIndex}) {
      if (rowIndex === 1) {
        return 'warning-row';
      } else if (rowIndex === 3) {
        return 'success-row';
      }
      return '';
    }
  },
  created() {
    this.loadAssignmentDetail();
    console.log(this.$route.query.record)
  },

}
</script>

<style scoped>
.app-container {
  background-color: white;
  height: 40px;
}

/deep/ .el-tabs {
  padding-left: 24px;
}

/deep/ .el-tabs__content {
  width: 98.5%;
}

/deep/ .el-tabs__nav-wrap:after {
  width: 270px;
}

.box-card {
  width: 100%;
}

.row-gutter {
  margin-bottom: 12px;
}

.common-box {
  margin-top: 16px;
  width: 100%;
  display: flex;
  flex-direction: column;
  row-gap: 16px;
  padding-bottom: 24px;

  .datainfo {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    gap: 24px;

    .source {
      flex: 1;
      border-radius: 4px;
    }

    .target {
      flex: 1;
      border-radius: 4px;
    }

    .head {
      display: flex;
      height: 72px;
      align-items: center;
      background-color: #F7FBFF;
      width: 100%;
      justify-content: space-between;

      .head-img {
        width: 40px;
        padding-left: 24px;
      }

      .head-text {
        flex-grow: 1;
        padding-left: 48px;

        .title {
          font-size: 14px;
          color: #0051FF;
          font-weight: bold;
        }

        .sub-title {
          margin-top: 4px;
          font-size: 12px;
          color: #7D7D7D;
        }
      }
    }

    .body {
      background-color: white;
      padding: 24px;
      min-height: 328px;
    }
  }

  .mapper {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    gap: 24px;

    .table-left {
      background-color: white;
      width: 100%;
      padding: 24px;
    }

    .table-right {
      background-color: white;
      width: 100%;
      padding: 24px;
    }
  }
}
</style>
<style>
.viewer-container {
  background-color: white;
  padding: 0px !important;
}

.key-text {
  color: #7D7D7D;
  font-size: 14px;
}

.value-text {
  margin-left: 8px;
  font-size: 12px;
  color: #000000;
}

.el-table .warning-row {
  background: oldlace;
}

.el-table .success-row {
  background: #f0f9eb;
}
</style>