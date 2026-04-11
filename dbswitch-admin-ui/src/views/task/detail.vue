<template>
  <div class="detail-page">
    <!-- 页面头部 -->
    <div class="detail-header">
      <div class="header-left">
        <el-button
          type="text"
          icon="el-icon-arrow-left"
          @click="handleGoBack"
          class="back-btn">返回列表</el-button>
        <span class="header-divider">|</span>
        <span class="task-name-title">{{ infoform.name || '任务详情' }}</span>
        <el-tag
          v-if="infoform.scheduleMode"
          size="small"
          :type="infoform.scheduleMode === 'MANUAL' ? 'info' : 'primary'"
          style="margin-left:10px"
          effect="plain">
          {{ infoform.scheduleMode === 'MANUAL' ? '手动调度' : '定时调度' }}
        </el-tag>
      </div>
      <div class="header-right">
        <el-button
          size="small"
          icon="el-icon-edit"
          type="primary"
          plain
          @click="handleGoEdit">
          编辑任务
        </el-button>
        <el-button
          size="small"
          icon="el-icon-notebook-2"
          @click="handleGoSchedule">
          调度日志
        </el-button>
      </div>
    </div>

    <el-card class="detail-card">
      <div style="margin-top: 8px">
        <commonInfo :infoform="infoform"></commonInfo>
      </div>
    </el-card>
  </div>
</template>

<script>
import commonInfo from '@/views/task/common/info'

export default {
  components: { commonInfo },
  data () {
    return {
      infoform: {
        id: 0,
        name: "",
        description: "",
        scheduleMode: "MANUAL",
        cronExpression: "",
        sourceConnectionId: 0,
        sourceTypeName: 'MySQL',
        sourceSchema: "",
        runStatus: "",
        tableType: "TABLE",
        includeOrExclude: "",
        sourceTables: [],
        tableNameMapper: [],
        columnNameMapper: [],
        tableNameCase: 'NONE',
        columnNameCase: 'NONE',
        targetConnectionId: 0,
        targetTypeName: 'MySQL',
        targetDropTable: true,
        targetOnlyCreate: false,
        autoSyncMode: 2,
        targetSchema: "",
        batchSize: 5000,
        channelSize: 100,
        targetSyncOption: 'INSERT_UPDATE_DELETE',
        beforeSqlScripts: '',
        afterSqlScripts: '',
        customDdlMap: {},
        incrTableColumns: [],
      },
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
          this.infoform = {
            id: detail.id,
            name: detail.name,
            description: detail.description,
            scheduleMode: detail.scheduleMode,
            cronExpression: detail.cronExpression,
            sourceConnectionId: detail.configuration.sourceConnectionId,
            sourceConnectionName: detail.configuration.sourceConnectionName,
            sourceTypeName: detail.configuration.sourceTypeName,
            sourceSchema: detail.configuration.sourceSchema,
            tableType: detail.configuration.tableType,
            includeOrExclude: detail.configuration.includeOrExclude,
            sourceTables: detail.configuration.sourceTables,
            incrTableColumns: detail.configuration.incrTableColumns,
            sourceBeforeSqlScripts: detail.configuration.sourceBeforeSqlScripts,
            sourceAfterSqlScripts: detail.configuration.sourceAfterSqlScripts,
            tableNameMapper: detail.configuration.tableNameMapper,
            columnNameMapper: detail.configuration.columnNameMapper,
            tableNameCase: detail.configuration.tableNameCase,
            columnNameCase: detail.configuration.columnNameCase,
            targetConnectionId: detail.configuration.targetConnectionId,
            targetConnectionName: detail.configuration.targetConnectionName,
            targetTypeName: detail.configuration.targetTypeName,
            targetDropTable: detail.configuration.targetDropTable,
            targetOnlyCreate: detail.configuration.targetOnlyCreate,
            targetAutoIncrement: detail.configuration.targetAutoIncrement,
            autoSyncMode: varAutoSyncMode,
            targetSchema: detail.configuration.targetSchema,
            batchSize: detail.configuration.batchSize,
            channelSize: detail.configuration.channelSize,
            targetSyncOption: detail.configuration.targetSyncOption,
            targetBeforeSqlScripts: detail.configuration.targetBeforeSqlScripts,
            targetAfterSqlScripts: detail.configuration.targetAfterSqlScripts,
            customDdlMap: detail.configuration.customDdlMap || {},
          }
        } else {
          if (res.data.message) {
            this.$message.error("查询任务失败：" + res.data.message);
          }
        }
      });
    },
    handleGoBack () {
      this.$router.push('/task/list');
    },
    handleGoEdit () {
      this.$router.push({ path: '/task/update', query: { id: this.$route.query.id } });
    },
    handleGoSchedule () {
      this.$router.push({ path: '/task/schedule?id=' + this.$route.query.id });
    }
  },
  created () {
    this.loadAssignmentDetail();
  },
}
</script>

<style scoped>
.detail-page {
  padding: 4px 0;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background: #fff;
  border-radius: 4px;
  margin-bottom: 12px;
  box-shadow: 0 1px 4px rgba(0,21,41,.06);
}

.header-left {
  display: flex;
  align-items: center;
}

.back-btn {
  font-size: 14px;
  padding: 0;
}

.header-divider {
  margin: 0 12px;
  color: #dcdfe6;
}

.task-name-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.header-right {
  display: flex;
  gap: 8px;
  align-items: center;
}

.detail-card {
  width: 100%;
  height: 100%;
  overflow: auto;
}
</style>
