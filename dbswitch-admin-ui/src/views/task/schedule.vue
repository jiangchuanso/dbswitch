<template>
  <div class="schedule-page">
    <el-card class="page-card" shadow="never">
      <!-- 页面标题头部 -->
      <div class="page-header">
        <div class="page-header-left">
          <h2 class="page-title">监控调度</h2>
          <span class="page-desc">查看任务执行状态、调度记录与运行日志</span>
        </div>
      </div>

      <div class="container">
        <!-- 左侧：任务安排列表 -->
        <div class="left-panel">
          <div class="left-header">
            <span class="left-title">
              <i class="el-icon-s-grid"></i>
              任务安排列表
            </span>
            <el-input placeholder="搜索任务"
                      v-model="keyword"
                      :clearable="true"
                      prefix-icon="el-icon-search"
                      size="mini"
                      @change="changeSearchKeyword"
                      class="left-search">
            </el-input>
          </div>
          <div class="navsBox">
            <ul>
              <li v-for="(item,index) in pageTaskAssignments"
                  :key="index"
                  @click="handleChooseClick(item.id,index)"
                  :class="{active:index==isActive}">
                <span class="task-id">#{{item.id}}</span>
                <span class="task-name">{{item.name}}</span>
              </li>
            </ul>
            <el-pagination small
                           layout="sizes, prev, pager, next"
                           @current-change="handleLoadPageTaskAssignments"
                           :current-page="currentTaskAssignmentPage"
                           :page-sizes="[10, 15, 20]"
                           @size-change="handleLoadPageTaskAssignmentsSizeChange"
                           :page-size="currentTaskAssignmentPageSize"
                           :total="pageTaskAssignmentsTotalCount">
            </el-pagination>
          </div>
        </div>

        <!-- 右侧：作业调度记录 -->
        <div class="contentBox">
          <!-- 工具栏 -->
          <div class="content-toolbar" v-if="taskId && taskId>0">
            <div class="toolbar-left">
              <el-tag size="medium" effect="plain" type="info">
                <i class="el-icon-s-flag"></i>
                当前任务：{{taskName}}
                <span class="task-id-tag">#{{taskId}}</span>
              </el-tag>
            </div>
            <div class="toolbar-right">
              <el-button size="mini"
                         type="primary"
                         plain
                         icon="el-icon-refresh"
                         @click="handleClickRefresh"
                         round>刷新</el-button>
            </div>
          </div>

          <!-- 空状态提示 -->
          <div class="content-empty" v-if="!taskId || taskId<=0">
            <i class="el-icon-info"></i>
            <p>请从左侧选择一个任务，查看作业调度记录</p>
          </div>

          <template v-if="taskId && taskId>0">
            <el-table :header-cell-style="{background:'#f5f7fa',color:'#606266',fontWeight:'600'}"
                      :data="jobTableData"
                      size="small"
                      stripe
                      border
                      style="width:100%">
              <template slot="empty">
                <span style="color:#909399;">暂无作业调度记录</span>
              </template>
              <el-table-column property="jobId"
                               label="ID"
                               min-width="60"></el-table-column>
              <el-table-column label="调度方式"
                               min-width="90">
                <template slot-scope="props">
                  <el-tag size="mini"
                          :type="props.row.scheduleMode==='手动执行'?'':'warning'"
                          effect="plain">
                    {{props.row.scheduleMode}}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column property="startTime"
                               label="开始时间"
                               min-width="150"></el-table-column>
              <el-table-column property="finishTime"
                               label="结束时间"
                               min-width="150"></el-table-column>
              <el-table-column property="duration"
                               label="持续时长(s)"
                               min-width="100"></el-table-column>
              <el-table-column label="执行状态"
                               min-width="100">
                <template slot-scope="props">
                  <el-tag size="mini"
                          :type="statusTagType(props.row.jobStatus)"
                          effect="dark">
                    {{statusText(props.row.jobStatus)}}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="日志"
                               min-width="70"
                               align="center">
                <template slot-scope="props">
                  <el-button size="mini"
                             type="text"
                             icon="el-icon-view"
                             @click="handleShowJobLogs(props.row.jobId)">
                    查看
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <div class="pagination-bar">
              <el-pagination @size-change="handleSizeChange"
                             @current-change="handleCurrentChange"
                             :current-page="currentPage"
                             :page-sizes="[5, 10, 20, 40]"
                             :page-size="pageSize"
                             layout="total, sizes, prev, pager, next, jumper"
                             :total="totalCount"></el-pagination>
            </div>
          </template>
        </div>
      </div>
    </el-card>

    <!-- 日志详情对话框 -->
    <el-dialog title="日志详情"
               :visible.sync="dialogShowLogVisible"
               :showClose="false"
               :before-close="handleClose"
               width="700px"
               custom-class="log-dialog">
      <el-alert :title="statusTitle"
                :type="statusAlertType"
                center
                show-icon
                :closable="false"
                style="margin-bottom:16px;">
      </el-alert>
      <el-input type="textarea"
                id="log_textarea_id"
                class="log_textarea_style"
                :rows="20"
                :spellcheck="false"
                v-model="logContent"
                readonly>
      </el-input>
      <div slot="footer"
           class="dialog-footer">
        <el-button size="small"
                   id="butten_cancel_id"
                   type="danger"
                   v-if="status=='1'"
                   @click="handleCancelJob(jobId)">
          终止
        </el-button>
        <el-button size="small"
                   @click="handleCloseLogDialog">关闭</el-button>
      </div>
    </el-dialog>
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
      currentTaskAssignmentPage: 1,
      currentTaskAssignmentPageSize: 10,
      keyword: null,
      pageTaskAssignments: [],
      pageTaskAssignmentsTotalCount: 0,
      taskId: 0,
      taskName: "",
      jobTableData: [],
      jobScheduleTime: '',
      isActive: -1,
      array: [],
      dialogShowLogVisible: false,
      logContent: "",
      jobId: 0,
      baseId: 0,
      status: 0,
      timer: null,
    };
  },
  computed: {
    statusTitle () {
      const map = {
        0: '待执行',
        1: '执行中',
        2: '执行异常',
        3: '执行成功',
        4: '任务取消'
      };
      return map[this.status] || '未知';
    },
    statusAlertType () {
      const map = {
        0: 'info',
        1: 'success',
        2: 'error',
        3: 'success',
        4: 'warning'
      };
      return map[this.status] || 'info';
    }
  },
  methods: {
    statusText (status) {
      const map = {
        '待执行': '待执行',
        '执行中': '执行中',
        '执行异常': '执行异常',
        '执行成功': '执行成功',
        '任务取消': '任务取消'
      };
      return map[status] || status || '未知';
    },
    statusTagType (status) {
      const map = {
        '待执行': 'info',
        '执行中': '',
        '执行异常': 'danger',
        '执行成功': 'success',
        '任务取消': 'warning'
      };
      return map[status] || 'info';
    },
    loadPageTaskAssignments: function () {
      this.$http({
        method: "POST",
        headers: {
          'Content-Type': 'application/json'
        },
        url: "/dbswitch/admin/api/v1/assignment/list",
        data: JSON.stringify({
          searchText: this.keyword,
          page: this.currentTaskAssignmentPage,
          size: this.currentTaskAssignmentPageSize
        })
      }).then(res => {
        if (0 === res.data.code) {
          this.pageTaskAssignments = res.data.data;
          this.pageTaskAssignmentsTotalCount = res.data.pagination.total;
        } else {
          if (res.data.message) {
            alert("初始化任务安排信息失败:" + res.data.message);
          }
        }
      }
      );
    },
    changeSearchKeyword: function () {
      this.currentTaskAssignmentPage = 1;
      this.loadPageTaskAssignments();
    },
    handleLoadPageTaskAssignments: function (currentPage) {
      this.currentTaskAssignmentPage = currentPage;
      this.loadPageTaskAssignments();
    },
    handleLoadPageTaskAssignmentsSizeChange: function (pageSize) {
      this.currentTaskAssignmentPageSize = pageSize;
      this.loadPageTaskAssignments();
    },
    handleClose: function () { },
    handleSizeChange: function (pageSize) {
      this.loading = true;
      this.pageSize = pageSize;
      this.loadJobsData();
    },
    handleCurrentChange: function (currentPage) {
      this.loading = true;
      this.currentPage = currentPage;
      this.loadJobsData();
    },
    loadTaskInfo: function () {
      if (this.taskId && this.taskId > 0) {
        this.$http.get(
          "/dbswitch/admin/api/v1/assignment/info/id/" + this.taskId
        ).then(res => {
          if (0 === res.data.code) {
            this.taskName = res.data.data.name;
          } else {
            if (res.data.message) {
              alert("查询任务详情失败," + res.data.message);
            }
          }
        });
      }
    },
    loadJobsData: function () {
      if (this.taskId && this.taskId > 0) {
        this.$http.get(
          "/dbswitch/admin/api/v1/ops/jobs/list/" + this.currentPage + "/" + this.pageSize + "?id=" + this.taskId
        ).then(res => {
          if (0 === res.data.code) {
            this.currentPage = res.data.pagination.page;
            this.pageSize = res.data.pagination.size;
            this.totalCount = res.data.pagination.total;
            this.jobTableData = res.data.data;
          } else {
            if (res.data.message) {
              alert("查询JOB执行历史纪录失败," + res.data.message);
            }
          }
        });
      }
    },
    handleChooseClick: function (taskId, index) {
      this.isActive = index;
      this.taskId = taskId;
      this.loadTaskInfo();
      this.loadJobsData();
    },
    handleClickRefresh () {
      if (!this.taskId || this.taskId < 0) {
        alert("请先在左侧选择一个任务来");
        return;
      }
      this.loadTaskInfo();
      this.loadJobsData();
    },
    handleCancelJob: function (jobId) {
      this.$http.get(
        "/dbswitch/admin/api/v1/ops/job/cancel?id=" + jobId
      ).then(res => {
        if (0 === res.data.code) {
          // 禁用取消按钮
          var cancelButton = document.getElementById('butten_cancel_id');
          cancelButton.value = "已取消"
          cancelButton.disabled = true;
          this.$message("停止JOB成功");
          this.loadJobsData();
        } else {
          if (res.data.message) {
            alert("JOB停止失败," + res.data.message);
          }
        }
      });
    },
    handleShowJobLogs: function (jobId) {
      this.dialogShowLogVisible = true
      this.jobId = jobId;
      this.$http.get(
        "/dbswitch/admin/api/v1/ops/job/logs/tail?id=" + jobId + "&size=500"
      ).then(res => {
        if (0 === res.data.code) {
          let lists = res.data.data.logs
          this.status = res.data.data.status;
          this.baseId = res.data.data.maxId;
          this.logContent = lists.join("")
          this.scrollMaxheight();
          if (1 === res.data.data.status) {
            this.timer = setInterval(() => {
              this.timerRefreshLogs();
            }, 1000);// 每隔1s定时刷新
          }
        } else {
          if (res.data.message) {
            alert("加载JOB执行日志失败," + res.data.message);
          }
        }
      });
    },
    timerRefreshLogs: function () {
      this.$http.get(
        "/dbswitch/admin/api/v1/ops/job/logs/next?id=" + this.jobId + "&baseId=" + this.baseId
      ).then(res => {
        if (0 === res.data.code) {
          let lists = res.data.data.logs;
          this.logContent = this.logContent + lists.join("");
          this.baseId = res.data.data.maxId;
          this.status = res.data.data.status;
          this.scrollMaxheight();
          if (1 !== res.data.data.status) {
            // 如果不是运行中，则需要关闭定时器
            clearInterval(this.timer);
            this.timer = null;
          }
        }
      });
    },
    scrollMaxheight: function () {
      this.$nextTick(() => {
        setTimeout(() => {
          const textarea = document.getElementById('log_textarea_id');
          textarea.scrollTop = textarea.scrollHeight;
        }, 13)
      })
    },
    handleCloseLogDialog: function () {
      this.dialogShowLogVisible = false;
      clearInterval(this.timer);
      this.timer = null;
    }
  },
  created () {
    this.loadPageTaskAssignments();
    if (this.$route.query.id) {
      this.taskId = this.$route.query.id
      this.loadTaskInfo();
      this.loadJobsData();
    }
  },
  beforeDestroy () {
    if (this.timer) {
      clearInterval(this.timer);
    }
  },
};
</script>

<style scoped>
.schedule-page {
  height: 100%;
  width: 100%;
}

.page-card {
  height: 100%;
  width: 100%;
  overflow: auto;
}

.page-card >>> .el-card__body {
  padding: 0;
}

/* 页面标题头部 */
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;
}

.page-header-left {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

/* 双栏布局 */
.container {
  display: flex;
  height: calc(100% - 62px);
  padding: 0;
}

/* 左侧面板 */
.left-panel {
  width: 280px;
  min-width: 280px;
  border-right: 1px solid #ebeef5;
  display: flex;
  flex-direction: column;
  background: #fafbfc;
}

.left-header {
  padding: 12px 14px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.left-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.left-title i {
  color: #409EFF;
  margin-right: 4px;
}

.left-search {
  width: 100%;
}

/* 左侧任务列表 */
.navsBox {
  flex: 1;
  overflow: auto;
  display: flex;
  flex-direction: column;
}

.navsBox ul {
  margin: 0;
  padding: 0;
  flex: 1;
}

.navsBox ul li {
  list-style: none;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;
  padding: 10px 14px;
  border-bottom: 1px solid #f0f2f5;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: background-color 0.2s;
  font-size: 13px;
  color: #606266;
}

.navsBox ul li:hover {
  background: #ecf5ff;
  color: #409EFF;
}

.navsBox ul li.active {
  background: #409EFF;
  color: #fff;
}

.navsBox ul li.active .task-id {
  color: rgba(255,255,255,0.8);
}

.navsBox ul li .task-id {
  color: #909399;
  font-size: 12px;
  flex-shrink: 0;
}

.navsBox ul li .task-name {
  overflow: hidden;
  text-overflow: ellipsis;
}

.navsBox >>> .el-pagination {
  padding: 8px 10px;
  text-align: center;
}

/* 右侧内容区 */
.contentBox {
  flex: 1;
  padding: 16px 20px;
  overflow: auto;
}

/* 工具栏 */
.content-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.toolbar-left {
  display: flex;
  align-items: center;
}

.task-id-tag {
  color: #909399;
  font-size: 12px;
  margin-left: 4px;
}

.toolbar-right {
  display: flex;
  align-items: center;
}

/* 空状态提示 */
.content-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 0;
  color: #c0c4cc;
}

.content-empty i {
  font-size: 48px;
  margin-bottom: 16px;
}

.content-empty p {
  font-size: 14px;
  margin: 0;
}

/* 分页栏 */
.pagination-bar {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}

/* 日志对话框 */
.log-dialog >>> .el-dialog__header {
  border-bottom: 1px solid #ebeef5;
  padding: 14px 20px;
}

.log-dialog >>> .el-dialog__title {
  font-weight: 600;
  font-size: 16px;
}

.log-dialog >>> .el-dialog__body {
  padding: 20px;
}

.log_textarea_style >>> textarea {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.6;
  background: #1e1e1e;
  color: #d4d4d4;
  border-color: #303030;
  border-radius: 4px;
}

.log_textarea_style >>> textarea:focus {
  border-color: #409EFF;
}

.dialog-footer {
  text-align: right;
}
</style>
