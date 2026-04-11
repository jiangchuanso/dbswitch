<template>
  <div class="info-container">
    <!-- 基本信息卡片 -->
    <el-card class="info-base-card" shadow="never">
      <div slot="header" class="card-header">
        <i class="el-icon-document" style="margin-right:6px;color:#409EFF"></i>
        <span>基本信息</span>
      </div>
      <el-row :gutter="16" class="info-row">
        <el-col :span="12">
          <div class="info-item">
            <span class="info-label">任务名称</span>
            <span class="info-value">{{ infoform.name || '--' }}</span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="info-item">
            <span class="info-label">任务类型</span>
            <span class="info-value">普通任务</span>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="16" class="info-row">
        <el-col :span="12">
          <div class="info-item">
            <span class="info-label">集成模式</span>
            <el-tag
              size="small"
              :type="infoform.scheduleMode === 'MANUAL' ? 'info' : 'primary'"
              effect="plain">
              {{ infoform.scheduleMode === 'MANUAL' ? '手动调度' : '定时调度' }}
            </el-tag>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="info-item">
            <span class="info-label">调度计划</span>
            <span class="info-value">
              {{ infoform.scheduleMode === 'MANUAL' ? '--' : (infoform.cronExpression || '--') }}
            </span>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="16" class="info-row" v-if="infoform.description">
        <el-col :span="24">
          <div class="info-item">
            <span class="info-label">任务描述</span>
            <span class="info-value desc-value">{{ infoform.description }}</span>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 源端 + 目标端双栏对比 -->
    <div class="datainfo-row">
      <!-- 源端卡片 -->
      <el-card class="source-card endpoint-card" shadow="never">
        <div class="endpoint-header source-header">
          <div class="endpoint-icon-wrap">
            <el-image
              style="width: 44px; height: 44px; border-radius:4px"
              :src="require('@/assets/icons/' + (infoform.sourceTypeName || 'MySQL') + '.png')">
            </el-image>
          </div>
          <div class="endpoint-title-wrap">
            <div class="endpoint-name">{{ infoform.sourceConnectionName || '--' }}</div>
            <div class="endpoint-badge">
              <el-tag size="mini" type="primary" effect="plain">源端数据源</el-tag>
              <el-tag size="mini" type="info" style="margin-left:4px">{{ infoform.sourceTypeName }}</el-tag>
            </div>
          </div>
        </div>
        <div class="endpoint-body">
          <div class="info-item">
            <span class="info-label">源端 Schema</span>
            <code class="code-value">{{ infoform.sourceSchema || '--' }}</code>
          </div>
          <div class="info-item">
            <span class="info-label">源端表类型</span>
            <el-tag size="mini" :type="infoform.tableType === 'TABLE' ? 'success' : 'warning'" effect="plain">
              {{ infoform.tableType === 'TABLE' ? '物理表' : '视图表' }}
            </el-tag>
          </div>
          <div class="info-item">
            <span class="info-label">表选择方式</span>
            <el-tag size="mini" effect="plain">
              {{ infoform.includeOrExclude === 'INCLUDE' ? '包含表' : '排除表' }}
            </el-tag>
          </div>
          <div class="info-item align-start">
            <span class="info-label">表名列表</span>
            <div class="table-tags-wrap">
              <template v-if="infoform.sourceTables && infoform.sourceTables.length > 0">
                <el-tag
                  v-for="(item, index) in infoform.sourceTables"
                  :key="index"
                  size="mini"
                  type="info"
                  effect="plain"
                  style="margin:2px 4px 2px 0">{{ item }}
                </el-tag>
              </template>
              <span v-else class="info-value-muted">全部表</span>
            </div>
          </div>

          <!-- 增量同步 -->
          <div class="info-item align-start"
               v-if="infoform.incrTableColumns && infoform.incrTableColumns.length > 0">
            <span class="info-label">增量同步配置</span>
            <el-table
              :data="infoform.incrTableColumns"
              size="mini"
              border
              style="width:100%">
              <el-table-column prop="tableName" label="增量同步表名"></el-table-column>
              <el-table-column prop="columnName" label="增量标识字段"></el-table-column>
            </el-table>
          </div>

          <div class="info-item">
            <span class="info-label">前置 SQL</span>
            <span :class="infoform.sourceBeforeSqlScripts ? 'code-value' : 'info-value-muted'">
              {{ infoform.sourceBeforeSqlScripts || '（未配置）' }}
            </span>
          </div>
          <div class="info-item">
            <span class="info-label">后置 SQL</span>
            <span :class="infoform.sourceAfterSqlScripts ? 'code-value' : 'info-value-muted'">
              {{ infoform.sourceAfterSqlScripts || '（未配置）' }}
            </span>
          </div>
        </div>
      </el-card>

      <!-- 目标端卡片 -->
      <el-card class="target-card endpoint-card" shadow="never">
        <div class="endpoint-header target-header">
          <div class="endpoint-icon-wrap">
            <el-image
              style="width: 44px; height: 44px; border-radius:4px"
              :src="require('@/assets/icons/' + (infoform.targetTypeName || 'MySQL') + '.png')">
            </el-image>
          </div>
          <div class="endpoint-title-wrap">
            <div class="endpoint-name">{{ infoform.targetConnectionName || '--' }}</div>
            <div class="endpoint-badge">
              <el-tag size="mini" type="success" effect="plain">目标端数据源</el-tag>
              <el-tag size="mini" type="info" style="margin-left:4px">{{ infoform.targetTypeName }}</el-tag>
            </div>
          </div>
        </div>
        <div class="endpoint-body">
          <div class="info-item">
            <span class="info-label">目标端 Schema</span>
            <code class="code-value">{{ infoform.targetSchema || '--' }}</code>
          </div>
          <div class="info-item">
            <span class="info-label">自动同步模式</span>
            <el-tag size="mini" type="primary" effect="plain">
              <span v-if="infoform.autoSyncMode == 2">目标端建表并同步数据</span>
              <span v-if="infoform.autoSyncMode == 1">目标端只创建物理表</span>
              <span v-if="infoform.autoSyncMode == 0">目标端只同步表里数据</span>
            </el-tag>
          </div>
          <div class="info-item" v-if="infoform.autoSyncMode !== 0">
            <span class="info-label">建表字段自增</span>
            <el-tag
              size="mini"
              :type="infoform.targetAutoIncrement ? 'success' : 'info'"
              effect="plain">
              {{ infoform.targetAutoIncrement ? '是' : '否' }}
            </el-tag>
          </div>
          <div class="info-item" v-if="infoform.autoSyncMode !== 0">
            <span class="info-label">表名转换方法</span>
            <span class="info-value">{{ getNameCaseLabel(infoform.tableNameCase) }}</span>
          </div>
          <div class="info-item" v-if="infoform.autoSyncMode !== 0">
            <span class="info-label">列名转换方法</span>
            <span class="info-value">{{ getNameCaseLabel(infoform.columnNameCase) }}</span>
          </div>
          <div class="info-item" v-if="infoform.autoSyncMode !== 1">
            <span class="info-label">数据批次大小</span>
            <span class="info-value">{{ infoform.batchSize }}</span>
          </div>
          <div class="info-item" v-if="infoform.autoSyncMode !== 1">
            <span class="info-label">通道队列大小</span>
            <span class="info-value">{{ infoform.channelSize }}</span>
          </div>
          <div class="info-item" v-if="infoform.autoSyncMode !== 1">
            <span class="info-label">同步操作方法</span>
            <span class="info-value">{{ getSyncOptionLabel(infoform.targetSyncOption) }}</span>
          </div>
          <div class="info-item" v-if="infoform.autoSyncMode !== 1">
            <span class="info-label">前置 SQL</span>
            <span :class="infoform.targetBeforeSqlScripts ? 'code-value' : 'info-value-muted'">
              {{ infoform.targetBeforeSqlScripts || '（未配置）' }}
            </span>
          </div>
          <div class="info-item" v-if="infoform.autoSyncMode !== 1">
            <span class="info-label">后置 SQL</span>
            <span :class="infoform.targetAfterSqlScripts ? 'code-value' : 'info-value-muted'">
              {{ infoform.targetAfterSqlScripts || '（未配置）' }}
            </span>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 映射规则区域 -->
    <div class="mapper-row">
      <el-card class="mapper-card" shadow="never">
        <div slot="header" class="card-header">
          <i class="el-icon-s-grid" style="margin-right:6px;color:#67C23A"></i>
          <span>表名映射规则</span>
          <el-tag size="mini" type="success" style="margin-left:8px">
            {{ infoform.tableNameMapper && infoform.tableNameMapper.length > 0 ? infoform.tableNameMapper.length + '条' : '无映射' }}
          </el-tag>
        </div>
        <el-table
          :data="infoform.tableNameMapper"
          size="small"
          border
          style="width:100%">
          <template slot="empty">
            <span style="color:#909399">无表名映射规则，目标表名与源表名相同</span>
          </template>
          <el-table-column prop="fromPattern" label="源端表名（正则匹配）" min-width="50%"></el-table-column>
          <el-table-column prop="toValue" label="替换的目标值" min-width="50%"></el-table-column>
        </el-table>
      </el-card>
      <el-card class="mapper-card" shadow="never">
        <div slot="header" class="card-header">
          <i class="el-icon-document-copy" style="margin-right:6px;color:#E6A23C"></i>
          <span>字段名映射规则</span>
          <el-tag size="mini" type="warning" style="margin-left:8px">
            {{ infoform.columnNameMapper && infoform.columnNameMapper.length > 0 ? infoform.columnNameMapper.length + '条' : '无映射' }}
          </el-tag>
        </div>
        <el-table
          :data="infoform.columnNameMapper"
          size="small"
          border
          style="width:100%">
          <template slot="empty">
            <span style="color:#909399">无字段名映射规则，目标字段名与源字段名相同</span>
          </template>
          <el-table-column prop="fromPattern" label="源端字段名（正则匹配）" min-width="50%"></el-table-column>
          <el-table-column prop="toValue" label="替换的目标值" min-width="50%"></el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- 自定义DDL -->
    <el-card
      v-if="infoform.customDdlMap && Object.keys(infoform.customDdlMap).length > 0"
      class="custom-ddl-card"
      shadow="never">
      <div slot="header" class="card-header">
        <i class="el-icon-tickets" style="margin-right:6px;color:#F56C6C"></i>
        <span>自定义建表 DDL</span>
        <el-tag size="mini" type="danger" style="margin-left:8px">
          共 {{ Object.keys(infoform.customDdlMap).length }} 张表
        </el-tag>
      </div>
      <div class="custom-ddl-tags">
        <el-tag
          v-for="(ddl, tableName) in infoform.customDdlMap"
          :key="tableName"
          size="small"
          type="warning"
          style="margin: 2px 4px 2px 0">{{ tableName }}
        </el-tag>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  props: {
    infoform: {
      type: Object,
      default: function () {
        return {
          id: 0,
          name: "--",
          description: "--",
          scheduleMode: "MANUAL",
          cronExpression: "",
          sourceConnectionId: 0,
          sourceTypeName: 'MySQL',
          sourceSchema: "",
          runStatus: "",
          tableType: "TABLE",
          includeOrExclude: "",
          sourceTables: [],
          incrTableColumns: [],
          sourceBeforeSqlScripts: "",
          sourceAfterSqlScripts: "",
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
          targetBeforeSqlScripts: '',
          targetAfterSqlScripts: '',
          customDdlMap: {},
        }
      }
    }
  },
  methods: {
    tableRowClassName ({ row, rowIndex }) {
      if (rowIndex === 1) return 'warning-row';
      else if (rowIndex === 3) return 'success-row';
      return '';
    },
    getNameCaseLabel (val) {
      const map = {
        'NONE': '无转换',
        'UPPER': '转大写',
        'LOWER': '转小写',
        'CAMEL': '下划线转驼峰',
        'SNAKE': '驼峰转下划线'
      };
      return map[val] || val || '--';
    },
    getSyncOptionLabel (val) {
      const map = {
        'ONLY_INSERT': '只同步 INSERT 操作',
        'ONLY_UPDATE': '只同步 UPDATE 操作',
        'INSERT_UPDATE': '同步 INSERT 和 UPDATE',
        'ONLY_DELETE': '只同步 DELETE 操作',
        'UPDATE_DELETE': '同步 UPDATE 和 DELETE',
        'INSERT_UPDATE_DELETE': '执行所有同步操作（INSERT/UPDATE/DELETE）'
      };
      return map[val] || val || '--';
    }
  }
}
</script>

<style scoped>
.info-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding-bottom: 24px;
}

/* 基本信息卡片 */
.info-base-card {
  width: 100%;
}

.card-header {
  display: flex;
  align-items: center;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.info-row {
  margin-bottom: 4px;
}

.info-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f5f7fa;
  min-height: 36px;
}

.info-item.align-start {
  align-items: flex-start;
  padding: 10px 0;
}

.info-label {
  min-width: 120px;
  color: #606266;
  font-size: 13px;
  flex-shrink: 0;
}

.info-value {
  color: #303133;
  font-size: 13px;
  word-break: break-all;
}

.desc-value {
  color: #606266;
  font-style: italic;
}

.info-value-muted {
  color: #c0c4cc;
  font-size: 13px;
}

.code-value {
  font-family: 'Courier New', monospace;
  background: #f4f4f5;
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 12px;
  color: #303133;
  word-break: break-all;
}

/* 双栏区域 */
.datainfo-row {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.endpoint-card {
  flex: 1;
  min-width: 0;
}

.source-card {
  border-top: 3px solid #409EFF;
}

.target-card {
  border-top: 3px solid #67C23A;
}

.endpoint-header {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-radius: 4px 4px 0 0;
  margin: -20px -20px 0;
}

.source-header {
  background: linear-gradient(135deg, #e8f4ff 0%, #f0f9ff 100%);
}

.target-header {
  background: linear-gradient(135deg, #e8f8e8 0%, #f0fff0 100%);
}

.endpoint-icon-wrap {
  margin-right: 12px;
}

.endpoint-title-wrap {
  flex: 1;
}

.endpoint-name {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.endpoint-badge {
  display: flex;
  align-items: center;
}

.endpoint-body {
  padding: 16px 0 8px;
}

.table-tags-wrap {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

/* 映射规则区域 */
.mapper-row {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.mapper-card {
  flex: 1;
  min-width: 0;
}

/* 自定义DDL */
.custom-ddl-card {
  width: 100%;
}

.custom-ddl-tags {
  line-height: 1.8;
}

/deep/ .el-card__header {
  padding: 10px 16px;
  background: #fafafa;
}

/deep/ .el-card__body {
  padding: 16px;
}

/deep/ .el-table .el-table__cell {
  padding: 6px 0;
}
</style>
