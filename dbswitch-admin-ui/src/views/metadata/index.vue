<template>
  <div>
    <el-card>
      <div class="flex-between">
        <div class="tree-container">
          <el-scrollbar style="height:100%">
            <el-select placeholder="请选择数据源"
                       v-model="dataSourceId"
                       @change="loadTreeData">
              <el-option v-for="(item,index) in connectionList"
                         :key="index"
                         :label="`[${item.id}]${item.name}`"
                         :value="item.id"></el-option>
            </el-select>
            <el-tree ref="metadataTree"
                     empty-text="请选择数据源后查看"
                     :indent=6
                     :data="treeData"
                     :props="props"
                     :load="loadTreeNode"
                     :expand-on-click-node="true"
                     :highlight-current="true"
                     :render-content="renderContent"
                     @node-click="handleNodeClick"
                     lazy>
            </el-tree>
          </el-scrollbar>
        </div>
        <div class="table-container">
          <el-tabs v-model="tabActiveTabName"
                   type="border-card">
            <el-tab-pane label="元数据"
                         name="metadata">
              <el-tag size="medium">当前表：{{currentNode.schemaName}} / {{currentNode.tableName}}</el-tag>
              <el-tabs v-model="metadataActiveTabName">
                <el-tab-pane label="基本信息"
                             name="first">
                  <el-descriptions title="元数据"
                                   size="small"
                                   :column="1"
                                   colon
                                   border>
                    <el-descriptions-item label="表名称">{{tableMeta.tableName}}</el-descriptions-item>
                    <el-descriptions-item label="表类型">{{tableMeta.type}}</el-descriptions-item>
                    <el-descriptions-item label="模式名">{{tableMeta.schemaName}}</el-descriptions-item>
                    <el-descriptions-item label="表注释">
                      <el-input type="textarea"
                                :rows="2"
                                v-model="tableMeta.remarks"
                                auto-complete="off"
                                :readonly=true></el-input>
                    </el-descriptions-item>
                    <el-descriptions-item label="建表DDL">
                      <el-input type="textarea"
                                :rows="16"
                                v-model="tableMeta.createSql"
                                auto-complete="off"
                                :readonly=true></el-input>
                    </el-descriptions-item>
                  </el-descriptions>
                </el-tab-pane>
                <el-tab-pane label="字段信息"
                             name="seconde">
                  <el-table :header-cell-style="{background:'#eef1f6',color:'#606266'}"
                            :data="tableMeta.columns"
                            size="small"
                            border
                            style="width: 100%">
                    <template slot="empty">
                      <span>单击左侧展开"数据源导航树"来查看表的元数据记录</span>
                    </template>
                    <el-table-column prop="fieldName"
                                     min-width="20%"
                                     show-overflow-tooltip
                                     label="名称">
                    </el-table-column>
                    <el-table-column prop="typeName"
                                     min-width="20%"
                                     label="类型">
                    </el-table-column>
                    <el-table-column prop="fieldType"
                                     min-width="7%"
                                     label="jdbcType">
                    </el-table-column>
                    <el-table-column prop="displaySize"
                                     min-width="7%"
                                     label="长度">
                    </el-table-column>
                    <el-table-column prop="precision"
                                     min-width="5%"
                                     label="精度">
                    </el-table-column>
                    <el-table-column prop="scale"
                                     min-width="5%"
                                     label="位数">
                    </el-table-column>
                    <el-table-column prop="isPrimaryKey"
                                     min-width="5%"
                                     label="主键">
                    </el-table-column>
                    <el-table-column prop="isAutoIncrement"
                                     min-width="5%"
                                     label="自增">
                    </el-table-column>
                    <el-table-column prop="isNullable"
                                     min-width="5%"
                                     label="可空">
                    </el-table-column>
                    <el-table-column prop="remarks"
                                     min-width="20%"
                                     show-overflow-tooltip
                                     label="注释">
                    </el-table-column>
                  </el-table>
                </el-tab-pane>
                <el-tab-pane label="索引信息"
                             name="third">
                  <el-table :header-cell-style="{background:'#eef1f6',color:'#606266'}"
                            :data="tableMeta.indexes"
                            size="small"
                            border
                            style="width: 100%">
                    <template slot="empty">
                      <span>单击左侧展开"数据源导航树"来查看表的元数据记录</span>
                    </template>
                    <el-table-column prop="indexType"
                                     min-width="20%"
                                     label="索引类型">
                    </el-table-column>
                    <el-table-column prop="indexName"
                                     min-width="20%"
                                     label="索引名称">
                    </el-table-column>
                    <el-table-column prop="indexFields"
                                     :formatter="formatIndexFields"
                                     show-overflow-tooltip
                                     min-width="60%"
                                     label="索引字段">
                    </el-table-column>
                  </el-table>
                </el-tab-pane>
                <el-tab-pane class="table-container-data-table"
                             label="取样数据"
                             name="fourth">
                  <el-table :header-cell-style="{background:'#eef1f6',color:'#606266'}"
                            :data="sampleData.rows"
                            border>
                    <template slot="empty">
                      <span>单击左侧展开"数据源导航树"来查看表的数据记录</span>
                    </template>
                    <el-table-column v-for="(item,index) in sampleData.columns"
                                     :prop="item"
                                     :label="item"
                                     :key="index"
                                     show-overflow-tooltip>
                    </el-table-column>
                  </el-table>
                </el-tab-pane>
              </el-tabs>
            </el-tab-pane>
            <!-- <el-tab-pane label="SQL在线"
                         name="sqlQuery">
              <multi-sql-editor ref="sqlEditors"></multi-sql-editor>
            </el-tab-pane> -->
          </el-tabs>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import urlencode from "urlencode";
import multiSqlEditor from '@/views/metadata/sqlEditor'

// 参考文章：https://blog.csdn.net/m0_50255772/article/details/109484828
export default {
  components: {
    multiSqlEditor
  },
  data () {
    return {
      props: {
        label: 'label',
        children: 'children',
        disabled: false,
        isLeaf: false
      },
      dataSourceId: null,
      connectionList: [],
      treeData: [],
      currentNode: {
        tableName: '-',
        schemaName: '-'
      },
      tabActiveTabName: 'metadata',
      metadataActiveTabName: 'first',
      tableMeta: {
        tableName: '-',
        schemaName: '-',
        remarks: '',
        type: '-',
        createSql: "",
        primaryKeys: [],
        columns: []
      },
      count: 1,
      sampleData: []
    };
  },
  methods: {
    loadConnections: function () {
      this.connectionList = [];
      this.$http({
        method: "GET",
        headers: {
          'Content-Type': 'application/json'
        },
        url: "/dbswitch/admin/api/v1/connection/list/name",
      }).then(
        res => {
          if (0 === res.data.code) {
            this.connectionList = res.data.data;
          } else {
            if (res.data.message) {
              alert("加载数据失败:" + res.data.message);
              this.connectionList = [];
            }
          }
        }
      );
    },
    loadTreeData: function () {
      if (this.dataSourceId && this.dataSourceId > 0) {
        this.treeData = []
        setTimeout(() => {
          this.$http({
            method: "GET",
            url: "/dbswitch/admin/api/v1/connection/schemas/get/" + this.dataSourceId
          }).then(
            res => {
              if (0 === res.data.code) {
                for (let element of res.data.data) {
                  let obj = new Object();
                  obj['dataSourceId'] = this.dataSourceId;
                  obj['label'] = element;
                  obj['parent'] = this.dataSourceId;
                  obj['value'] = element;
                  obj['hasChild'] = true;
                  obj['type'] = 'DATABASE';
                  this.treeData.push(obj);
                }
              } else {
                alert("加载失败，原因：" + res.data.message);
              }
            }
          );
        }, 500);
      }
    },
    loadTreeNode: function (node, resolve) {
      setTimeout(() => {
        if (node.level === 1) {
          let tableView = [
            {
              'dataSourceId': this.dataSourceId,
              'label': '表',
              'parent': this.dataSourceId,
              'value': node.label,
              'hasChild': true,
              'type': 'TABLE',
            },
            {
              'dataSourceId': this.dataSourceId,
              'label': '视图',
              'parent': this.dataSourceId,
              'value': node.label,
              'hasChild': true,
              'type': 'VIEW',
            }
          ]
          resolve(tableView);
        } else if (node.level === 2) {
          this.loadTablesList(resolve, this.dataSourceId, node.data.value, node.data.type)
        } else {
          resolve([]);
        }
      }, 500);
    },
    loadTablesList: function (resolve, dataSourceId, schema, type) {
      var tableType = 'VIEW' === type ? 'views' : 'tables'
      this.$http({
        method: "GET",
        url: "/dbswitch/admin/api/v1/connection/" + tableType + "/get/" + dataSourceId + "?schema=" + urlencode(schema)
      }).then(
        res => {
          if (0 === res.data.code) {
            let tableList = []
            for (let element of res.data.data) {
              let obj = new Object();
              obj['dataSourceId'] = dataSourceId;
              obj['label'] = element;
              obj['parent'] = dataSourceId;
              obj['value'] = schema;
              obj['hasChild'] = false;
              obj['type'] = type;
              tableList.push(obj);
            }
            return resolve(tableList);
          } else {
            this.$alert("加载失败，原因：" + res.data.message, '数据加载失败');
          }
        }
      );
    },
    renderContent (h, { node, data, store }) {
      // https://www.cnblogs.com/zhoushuang0426/p/11059989.html
      if (node.level === 1) {
        return (
          <div class="custom-tree-node">
            <i class="iconfont icon-shujuku1"></i>
            <el-tooltip class="item" effect="light" placement="left">
              <div slot="content">{node.label}</div>
              <span>{data.label}</span>
            </el-tooltip>
          </div>
        );
      } else if (node.level === 2) {
        var icon_pic = "iconfont icon-shitu_biaoge";
        if (data.type === 'VIEW') {
          icon_pic = "iconfont icon-viewList"
        }
        return (
          <div class="custom-tree-node">
            <i class={icon_pic}></i>
            <span>{data.label}</span>
          </div>
        );
      } else if (node.level === 3) {
        var icon_pic = "iconfont icon-shitu_biaoge";
        if (data.type === 'VIEW') {
          icon_pic = "iconfont icon-viewList"
        }
        return (
          <div class="custom-tree-node">
            <i class={icon_pic}></i>
            <el-tooltip class="item" effect="light" placement="left">
              <div slot="content">{node.label}</div>
              <span>{data.label}</span>
            </el-tooltip>
          </div>
        );
      } else {
        return (
          <div class="custom-tree-node">
            <i class="el-icon-set-up"></i>
            <el-tooltip class="item" effect="light" placement="left">
              <div slot="content">{data.type}</div>
              <span>{data.label}({data.type})</span>
            </el-tooltip>
          </div>
        );
      }
    },
    formatIndexFields (row, column) {
      let list = row.indexFields;
      let fields = list.map(
        item => {
          if (item.ascOrder === null) {
            return item.fieldName;
          } else if (item.ascOrder) {
            return item.fieldName + " ASC";
          } else {
            return item.fieldName + " DESC";
          }
        })
      return fields.join(";");
    },
    handleNodeClick (data) {
      var type = data.type;
      if (type === 'VIEW' || type === 'TABLE') {
        var datasourceId = data.dataSourceId;
        var schema = data.value;
        var table = data.label;
        if (!data.hasChild && datasourceId && schema && table) {
          this.tabActiveTabName = 'metadata';
          this.metadataActiveTabName = 'first';
          this.getTableMeta(datasourceId, schema, table);
          this.getTableData(datasourceId, schema, table);
        }
      }
    },
    clearDataSet () {
      this.tableData = [];
      this.tableMeta = {
        tableName: '-',
        schemaName: '-',
        remarks: '',
        type: '-',
        createSql: "",
        primaryKeys: [],
        columns: []
      },
        this.sampleData = []
    },
    getTableMeta (id, schema, table) {
      this.$http({
        method: "GET",
        url: "/dbswitch/admin/api/v1/metadata/meta/table/" + id + "?schema=" + urlencode(schema) + "&table=" + urlencode(table)
      }).then(
        res => {
          if (0 === res.data.code) {
            //console.log("list4:" + JSON.stringify(res.data.data))
            this.tableMeta = res.data.data;
            this.currentNode.tableName = table;
            this.currentNode.schemaName = schema;
          } else {
            this.$alert("加载失败，原因：" + res.data.message, '数据加载失败');
            this.clearDataSet();
          }
        }
      );
    },
    async getTableData (id, schema, table) {
      this.$http({
        method: "GET",
        url: "/dbswitch/admin/api/v1/metadata/data/table/" + id + "?schema=" + urlencode(schema) + "&table=" + urlencode(table)
      }).then(
        res => {
          if (0 === res.data.code) {
            this.sampleData = res.data.data;
            //console.log(this.sampleData)
          } else {
            this.$alert("加载失败，原因：" + res.data.message, '数据加载失败');
            this.clearDataSet();
          }
        }
      );
    },
    loadColumnList (resolve, id, schema, table) {
      //console.log("id=" + id);
      this.$http({
        method: "GET",
        url: "/dbswitch/admin/api/v1/metadata/columns/" + id + "/1/0?schema=" + urlencode(schema) + "&table=" + urlencode(table)
      }).then(
        res => {
          if (0 === res.data.code) {
            //console.log("list3:" + JSON.stringify(res.data.data))
            res.data.data.forEach(function (element) {
              element['label'] = element.fieldName.name;
              element['parent'] = schema;
              element['value'] = element.fieldName.value;
              element['hasChild'] = false;
              element['children'] = 'child';
            });
            this.tableData = res.data.data;
            return resolve([]);
          } else {
            this.$alert("加载失败，原因：" + res.data.message, '数据加载失败');
            this.clearDataSet();
          }
        }
      );
    },
  },
  created () {
    this.loadConnections();
    this.loadTreeData();
  },
}
</script>

<style scoped>
.el-card {
  width: 100%;
  height: 100%;
  min-height: 200px;
}

.el-message {
  width: 100%;
  height: 100%;
  overflow: auto;
}
.flex-between {
  display: flex;
}
.el-scrollbar .el-scrollbar__wrap {
  overflow-x: hidden;
}
.tree-container .el-tree {
  min-width: 350px;
  position: relative;
  cursor: default;
  color: black;
}
.el-tree-node__content {
  font-size: small;
  height: 16px;
  background-color: blanchedalmond;
}
.custom-tree-node {
  font-size: 16px;
  background-size: 16px;
}

.scroller {
  min-width: 200px;
  max-height: calc(90vh);
}
.el-select {
  display: inline;
}
.tree-container .tree {
  overflow: auto;
}
.table-container {
  width: 100%;
}
.table-container-data-table {
  height: 90%;
  overflow-y: auto;
  overflow-x: hidden;
}

el-tabs--border-card > .el-tabs__header .el-tabs__item {
  margin-left: 8px;
  border: none;
  border-radius: 8px 8px 0 0;
  background-color: #f3f7fe;
  padding: 4px 20px;
  color: #0065d5;
  line-height: 22px;
  height: 30px;
}
.el-tabs--border-card > .el-tabs__header .el-tabs__item.is-active {
  background-color: #0065d5;
  color: #ffffff;
}
</style>
