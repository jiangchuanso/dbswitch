<template>
  <div>
    <div class="tool">
      <div class="item-button"><i class="iconfont icon-play"></i><span>执行</span></div>
      <div class="item-button"><i class="iconfont icon-play"></i><span>执行选中</span></div>
      <div class="item-button"><i class="iconfont icon-play"></i><span>格式化</span></div>
    </div>
    <ace ref="editor"
         :value="content"
         @init="initEditor"
         :lang="lang"
         :height="height === 0 ? '100%' : height"
         :theme="theme"
         :options="options"
         width="100%"
         v-bind="config">
    </ace>
  </div>
</template>

<script>
import ace from 'vue2-ace-editor'

export default {
  name: "multiSqlEditer",
  components: {
    ace
  },
  data () {
    return {
    }
  },
  props: {
    content: {
      type: String,
      default: ''
    },
    height: {
      type: Number,
      default: 300
    },
    readOnly: {
      type: Boolean,
      default: false
    },
    theme: {
      type: String,
      default: 'monokai'
    },
    lang: {
      type: String,
      default: 'sql'
    },
    config: {
      type: Object,
      default: () => {
        return {
          font_size: 16,
          sql_atom: true
        }
      }
    }
  },
  computed: {
    options () {
      if (this.readOnly) {
        return {
          enableBasicAutocompletion: true,
          enableSnippets: true,
          enableLiveAutocompletion: this.config.sql_atom,
          showPrintMargin: false,
          fontSize: this.config.font_size,
          readOnly: true
        }
      }
      return {
        enableBasicAutocompletion: true,
        enableSnippets: true,
        enableLiveAutocompletion: this.config.sql_atom,
        showPrintMargin: false,
        fontSize: this.config.font_size
      }
    }
  },
  methods: {
    initEditor (editor) {
      require('brace/ext/language_tools')
      // 设置语言
      require('brace/mode/sql')
      require('brace/snippets/sql')
      // 设置主题 按需加载
      require('brace/theme/monokai')
      require('brace/theme/chrome')
      require('brace/theme/crimson_editor')
      // 监听值的变化
      editor.getSession().on('change', val => {
        this.$emit('change', editor.getValue())
      })
    },
  },
  mounted () {
  },
  created () {
  },
}
</script>

<style scoped>
.tool .item-button {
  display: inline-block;
  font-size: 20px;
  color: #009966;
  margin: 0 20px 0 0;
  line-height: 26px;
  cursor: pointer;
}
.tool .item-button span {
  color: #000;
  font-size: 16px;
}
</style>
