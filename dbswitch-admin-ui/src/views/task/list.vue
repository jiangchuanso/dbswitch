<template>
  <div class="task-list-page">
    <el-card class="list-card">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="page-header-left">
          <h2 class="page-title">迁移任务管理</h2>
          <span class="page-desc">管理数据库异构迁移任务，支持手动与定时调度两种模式</span>
        </div>
        <div class="page-header-right">
          <el-button
            type="primary"
            icon="el-icon-plus"
            @click="handleCreate">新建任务
          </el-button>
        </div>
      </div>

      <!-- 工具栏 -->
      <div class="toolbar">
        <div class="toolbar-left">
          <el-input
            size="small"
            placeholder="输入任务名称搜索"
            v-model="keyword"
            :clearable="true"
            @change="searchByKeyword"
            style="width: 260px">
            <el-button slot="append" icon="el-icon-search" @click="searchByKeyword"></el-button>
          </el-input>
        </div>
        <div class="toolbar-right">
          <el-button
            size="small"
            icon="el-icon-switch-button"
            :disabled="isSelected"
            @click="batchStart()">批量启动
          </el-button>
          <el-button
            size="small"
            icon="el-icon-video-pause"
            :disabled="isSelected"
            @click="batchStop()">批量停用
          </el-button>
          <el-button
            size="small"
            icon="el-icon-download"
            :disabled="isSelected"
            @click="batchExport()">导出
          </el-button>
        </div>
      </div>

      <!-- 数据表格 -->
      <el-table
        :header-cell-style="{background:'#f5f7fa',color:'#606266',fontWeight:'600'}"
        :data="tableData"
        size="small"
        stripe
        border
        @selection-change="handleSelectionChange"
        style="width:100%;margin-top:4px">
        <el-table-column
          type="selection"
          width="42"
          align="center">
        </el-table-column>
        <el-table-column
          label="任务名称"
          :show-overflow-tooltip="true"
          min-width="16%">
          <template slot-scope="scope">
            <span class="task-name" @click="handleDetail(scope.$index, scope.row)">
              <i class="el-icon-document" style="margin-right:4px;color:#409EFF"></i>
              {{ scope.row.name }}
            </span>
          </template>
        </el-table-column>
        <el-table-column
          label="调度模式"
          align="center"
          min-width="8%">
          <template slot-scope="scope">
            <el-tag
              :type="scope.row.scheduleMode === 'MANUAL' ? 'info' : 'primary'"
              size="mini"
              effect="plain">
              {{ scope.row.scheduleMode === 'MANUAL' ? '手动' : '定时' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="源端" min-width="11%">
          <template slot-scope="scope">
            <el-popover trigger="hover" placement="top" width="200">
              <p style="margin:4px 0"><b>数据源类型：</b>{{ scope.row.sourceType }}</p>
              <p style="margin:4px 0"><b>模式名：</b>{{ scope.row.sourceSchema }}</p>
              <div slot="reference">
                <el-tag size="small" effect="plain" type="success" class="schema-tag">
                  <i class="el-icon-connection" style="margin-right:2px"></i>
                  {{ scope.row.sourceSchema }}
                </el-tag>
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column label="目标端" min-width="11%">
          <template slot-scope="scope">
            <el-popover trigger="hover" placement="top" width="200">
              <p style="margin:4px 0"><b>数据源类型：</b>{{ scope.row.targetType }}</p>
              <p style="margin:4px 0"><b>模式名：</b>{{ scope.row.targetSchema }}</p>
              <div slot="reference">
                <el-tag size="small" effect="plain" type="warning" class="schema-tag">
                  <i class="el-icon-connection" style="margin-right:2px"></i>
                  {{ scope.row.targetSchema }}
                </el-tag>
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" min-width="12%">
          <template slot-scope="scope">
            <div class="status-cell">
              <el-tag :type="getStatusTag(scope.row).type" size="mini" effect="light">
                <i :class="getStatusTag(scope.row).icon" style="margin-right:3px"></i>
                {{ getStatusTag(scope.row).text }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          label="调度时间"
          min-width="14%"
          :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <span class="schedule-time">
              {{ scope.row.scheduleTime || '--' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="20%" align="center">
          <template slot-scope="scope">
            <div class="action-cell">
              <!-- 主操作：启动/停止 -->
              <el-tooltip
                :content="scope.row.isPublished ? '停止任务' : '启动任务'"
                placement="top">
                <el-button
                  size="mini"
                  :type="scope.row.isPublished ? 'warning' : 'success'"
                  :icon="scope.row.isPublished ? 'el-icon-video-pause' : 'el-icon-switch-button'"
                  circle
                  @click="scope.row.isPublished ? handleRetireTask(scope.$index, scope.row) : handlePublish(scope.$index, scope.row)">
                </el-button>
              </el-tooltip>

              <!-- 执行（仅已启动任务） -->
              <el-tooltip content="立即执行" placement="top" v-if="scope.row.isPublished">
                <el-button
                  size="mini"
                  type="primary"
                  icon="el-icon-video-play"
                  circle
                  @click="handleRunTask(scope.$index, scope.row)">
                </el-button>
              </el-tooltip>

              <!-- 编辑（仅未启动任务） -->
              <el-tooltip content="编辑任务" placement="top" v-if="!scope.row.isPublished">
                <el-button
                  size="mini"
                  type="primary"
                  icon="el-icon-edit"
                  circle
                  @click="handleUpdate(scope.$index, scope.row)">
                </el-button>
              </el-tooltip>

              <!-- 更多操作下拉菜单 -->
              <el-dropdown trigger="click" @command="handleDropdownCommand($event, scope.$index, scope.row)">
                <el-button size="mini" circle icon="el-icon-more"></el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="detail" icon="el-icon-document">查看详情</el-dropdown-item>
                  <el-dropdown-item command="log" icon="el-icon-notebook-2">调度日志</el-dropdown-item>
                  <el-dropdown-item
                    command="delete"
                    icon="el-icon-delete"
                    v-if="!scope.row.isPublished"
                    style="color:#F56C6C">删除任务</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-bar">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 40, 100]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="totalCount">
        </el-pagination>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  data () {
    return {
      loading: true,
      currentPage: 1,
      pageSize: 10,
      totalCount: 0,
      keyword: null,
      tableData: [],
      isSelected: true,
      idsSelected: []
    };
  },
  methods: {
    getStatusTag (row) {
      if (row.runStatus === '执行中') {
        return { text: '执行中', type: 'primary', icon: 'el-icon-loading' };
      }
      if (row.runStatus === '执行成功') {
        return { text: '执行成功', type: 'success', icon: 'el-icon-circle-check' };
      }
      if (row.runStatus === '执行异常') {
        return { text: '执行异常', type: 'danger', icon: 'el-icon-circle-close' };
      }
      if (row.runStatus === '任务取消') {
        return { text: '已取消', type: 'info', icon: 'el-icon-remove-outline' };
      }
      if (row.runStatus === '待执行') {
        return { text: '待执行', type: 'warning', icon: 'el-icon-time' };
      }
      // 兜底：根据 isPublished 显示任务状态
      if (row.isPublished === true) {
        return { text: '已启动', type: 'success', icon: 'el-icon-switch-button' };
      }
      return { text: '未启动', type: 'info', icon: 'el-icon-video-pause' };
    },
    handleDropdownCommand (command, index, row) {
      if (command === 'detail') {
        this.handleDetail(index, row);
      } else if (command === 'log') {
        this.schedulingLog(index, row);
      } else if (command === 'delete') {
        this.handleDelete(index, row);
      }
    },
    loadData: function () {
      this.$http({
        method: "POST",
        headers: { 'Content-Type': 'application/json' },
        url: "/dbswitch/admin/api/v1/assignment/list",
        data: JSON.stringify({
          searchText: this.keyword,
          page: this.currentPage,
          size: this.pageSize
        })
      }).then(res => {
        if (0 === res.data.code) {
          this.currentPage = res.data.pagination.page;
          this.pageSize = res.data.pagination.size;
          this.totalCount = res.data.pagination.total;
          this.tableData = res.data.data;
        } else {
          this.$message.error("加载任务列表失败：" + res.data.message);
        }
      }, function () {
        console.log("load assignments list failed");
      });
    },
    searchByKeyword: function () {
      this.currentPage = 1;
      this.loadData();
    },
    handleSelectionChange (val) {
      if (val.length > 0) {
        this.isSelected = false;
        this.idsSelected = val.map(item => item.id);
      } else {
        this.isSelected = true;
        this.idsSelected = [];
      }
    },
    batchStart () {
      this.$http({
        method: "POST",
        headers: { 'Content-Type': 'application/json' },
        url: "/dbswitch/admin/api/v1/assignment/deploy?ids=" + this.idsSelected,
      }).then(res => {
        if (0 === res.data.code) {
          this.$message({ message: '任务发布成功!', type: 'success' });
          this.loadData();
        } else {
          if (res.data.message) this.$message.error("任务发布失败," + res.data.message);
        }
      });
    },
    batchStop () {
      this.$http({
        method: "POST",
        headers: { 'Content-Type': 'application/json' },
        url: "/dbswitch/admin/api/v1/assignment/retire?ids=" + this.idsSelected,
      }).then(res => {
        if (0 === res.data.code) {
          this.$message({ message: '下线任务成功!', type: 'success' });
          this.loadData();
        } else {
          if (res.data.message) this.$message.error("下线任务失败," + res.data.message);
        }
      });
    },
    batchExport () {
      this.$http({
        method: "POST",
        headers: { 'Content-Type': 'application/json' },
        url: "/dbswitch/admin/api/v1/assignment/export?ids=" + this.idsSelected,
        responseType: 'blob',
      }).then(res => {
        if (200 === res.status) {
          this.downloadFile(res);
          this.$message({ message: '导出任务成功!', type: 'success' });
          this.loadData();
        } else {
          if (res.data.message) this.$message.error("导出任务失败," + res.data.message);
        }
      });
    },
    downloadFile: function (resp) {
      const headers = resp.headers;
      const contentType = headers['content-type'];
      if (!resp.data) {
        console.error('响应异常：', resp);
        return false;
      } else {
        const blob = new Blob([resp.data], { type: contentType });
        const contentDisposition = resp.headers['content-disposition'];
        let fileName = 'unknown';
        if (contentDisposition) {
          fileName = window.decodeURI(resp.headers['content-disposition'].split('=')[1]);
        }
        this.downFile(blob, fileName);
      }
    },
    downFile: function (blob, fileName) {
      if ('download' in document.createElement('a')) {
        const link = document.createElement('a');
        link.href = window.URL.createObjectURL(blob);
        link.download = fileName;
        link.style.display = 'none';
        document.body.appendChild(link);
        link.click();
        window.URL.revokeObjectURL(link.href);
        document.body.removeChild(link);
      } else {
        window.navigator.msSaveBlob(blob, fileName);
      }
    },
    handleCreate: function () {
      this.$router.push('/task/create');
    },
    handleDetail: function (index, row) {
      this.$router.push({ path: '/task/detail', query: { id: row.id } });
    },
    handleUpdate: function (index, row) {
      this.$router.push({ path: '/task/update', query: { id: row.id } });
    },
    handleDelete: function (index, row) {
      this.$confirm("是否确定删除任务【" + row.name + "】?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.$http.delete("/dbswitch/admin/api/v1/assignment/delete/" + row.id).then(res => {
          if (0 === res.data.code) {
            this.$message({ message: '删除任务成功!', type: 'success' });
            this.loadData();
          } else {
            if (res.data.message) this.$message.error("删除任务失败：" + res.data.message);
          }
        });
      });
    },
    schedulingLog: function (index, row) {
      this.$router.push({ path: "/task/schedule?id=" + row.id });
    },
    handlePublish: function (index, row) {
      this.$http({
        method: "POST",
        headers: { 'Content-Type': 'application/json' },
        url: "/dbswitch/admin/api/v1/assignment/deploy?ids=" + row.id,
      }).then(res => {
        if (0 === res.data.code) {
          this.$message({ message: '任务启动成功!', type: 'success' });
          this.loadData();
        } else {
          if (res.data.message) this.$message.error("任务启动失败," + res.data.message);
        }
      });
    },
    handleRunTask: function (index, row) {
      this.$http({
        method: "POST",
        headers: { 'Content-Type': 'application/json' },
        url: "/dbswitch/admin/api/v1/assignment/run",
        data: JSON.stringify([row.id])
      }).then(res => {
        if (0 === res.data.code) {
          this.$message({ message: '手动执行任务成功!', type: 'success' });
          this.loadData();
        } else {
          if (res.data.message) this.$message.error("手动执行任务失败," + res.data.message);
        }
      });
    },
    handleRetireTask: function (index, row) {
      this.$http({
        method: "POST",
        headers: { 'Content-Type': 'application/json' },
        url: "/dbswitch/admin/api/v1/assignment/retire?ids=" + row.id,
      }).then(res => {
        if (0 === res.data.code) {
          this.$message({ message: '任务已停止!', type: 'success' });
          this.loadData();
        } else {
          if (res.data.message) this.$message.error("停止任务失败," + res.data.message);
        }
      });
    },
    handleSizeChange: function (pageSize) {
      this.loading = true;
      this.pageSize = pageSize;
      this.loadData();
    },
    handleCurrentChange: function (currentPage) {
      this.loading = true;
      this.currentPage = currentPage;
      this.loadData();
    }
  },
  created () {
    this.loadData();
  },
};
</script>

<style scoped>
.task-list-page {
  padding: 4px 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 0 16px;
  margin-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.page-header-left {
  display: flex;
  flex-direction: column;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 4px 0;
  line-height: 1.3;
}

.page-desc {
  font-size: 13px;
  color: #909399;
}

.list-card {
  width: 100%;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.task-name {
  color: #409EFF;
  cursor: pointer;
  font-weight: 500;
  transition: color 0.2s;
}

.task-name:hover {
  color: #66b1ff;
  text-decoration: underline;
}

.schema-tag {
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: inline-block;
}

.status-cell {
  display: flex;
  justify-content: center;
}

.schedule-time {
  color: #606266;
  font-size: 12px;
}

.action-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  flex-wrap: nowrap;
}

.pagination-bar {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

/deep/ .el-table--border .el-table__cell {
  border-right: 0px solid transparent !important;
}

/deep/ .el-card__body {
  padding: 16px;
}
</style>
