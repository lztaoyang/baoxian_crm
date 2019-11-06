var entitySelectComp = Vue.extend({
    template: `
       <span>
            <el-input size="small" v-model="currentValue" v-show="false"></el-input>
            <el-input size="small" v-model="currentValueTxt" readonly>>
                <el-button slot="append" icon="search" @click="dialogVisible=true"></el-button>
            </el-input>
            <el-dialog :title="this.options.selectType=='single'?'双击要选择的记录':'勾选要选择的记录'" :visible.sync="dialogVisible"
                       size="large">
                <el-row>
                    <el-collapse accordion>
                        <el-collapse-item title="查询面板[点击展开]">
                            <el-form :inline="true" :model="searchParams" label-position="right"
                                     style="margin-bottom: 0px">
                                <el-form-item label="中文本" style="font-size: 13px;" :prop="item.prop"
                                              v-for="item in options.searchColumns">
                                    <el-input size="small" v-model="searchParams[item.prop]" :placeholder="item.label"
                                              @keyup.enter.native="readList"></el-input>
                                </el-form-item>
                                <el-form-item>
                                    <el-button size="small" type="info" @click="readList">查询</el-button>
                                </el-form-item>
                            </el-form>
                        </el-collapse-item>
                    </el-collapse>
                </el-row>
                <el-row style="margin: 5px">

                    <el-col :span="16">
                        <el-pagination ref="entityTablePage"
                                       :current-page="pageData.current"
                                       :page-sizes="[10, 50, 100]"
                                       :page-size="pageData.size"
                                       layout="total, sizes, prev, pager, next, jumper"
                                       :total="pageData.iTotalRecords"
                                       @size-change="readList"
                                       @current-change="readList"
                        >
                        </el-pagination>
                    </el-col>
                    <el-col :span="8" class="text-right">
                        <el-button size="small" type="warning" @click="clear">清空</el-button>
                    </el-col>

                </el-row>
                <el-row>
                    <el-table id="12324"
                              :data="pageData.data"
                              ref="entityTable"
                              stripe
                              max-height="450px"
                              :row-key="this.options.key"
                              empty-text="-"
                              append="加载中..."
                              :row-style="{'font-size':'13px'}"
                              style="width: 100%"
                              @selection-change="selectionChange"
                              @row-dblclick="dblclick"
                    >
                        <el-table-column v-if="options.selectType =='multi'"
                                         type="selection"
                                         prop="id"
                        >
                        </el-table-column>
                        <el-table-column v-for="item in options.tableColumns"
                                         :prop="item.prop"
                                         min-width="100"
                                         v-bind:show-overflow-tooltip="true"
                                         :label="item.label"
                        >
                        </el-table-column>
                    </el-table>
                </el-row>
                </el-table>
            </el-dialog>
        </span>
        `,
    data: function () {
        return {
            pageData: {},
            currentValueTxt: '',
            selectDatas: [],
            dialogVisible: false,
        }
    },
    props: {
        options: {},
        value:{type: String},
    },
    computed: {
        searchParams:function () {
            var tempSearchParams = this.options.params;
            for(var i in this.options.searchColumns){
                if (typeof(tempSearchParams[this.options.searchColumns[i].prop])==undefined){
                    tempSearchParams[this.options.searchColumns[i].prop]='';
                }
            }
            return tempSearchParams;
        },
        currentValue: {
            // 动态计算currentValue的值
            get:function() {
                return this.value;
            },
            set:function(val) {
                this.$emit('input', val);
            }
        }
    },
    created: function () {
        var self = this;
        self.readList();
    },
    methods: {
        //查询
        readList: function () {
            var params = {
                asc: false,
                orderBy: "id",
                pageNum: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalCurrentPage : 1),
                pageSize: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalPageSize : 10)
            };
            //将查询条件合并到请求参数中
            for (var item in this.searchParams) {
                if (typeof this.searchParams[item] == 'boolean') {
                    params[item] = this.searchParams[item];
                } else if (this.searchParams[item] != "") {
                    params[item] = this.searchParams[item];
                }
            }
            this.$http.put(this.options.url, params)
                .then(function (response) {
                    this.pageData = response.data;
                });
        },
        //表格记录选择事件
        selectionChange: function (selection) {
            //var self = this;
            //self.selectDatas = selection;
            var temp;
            if (this.options.resultType == "array"){
                temp = [];
            }else{
                temp = "";
            }
            var tempTxt = "";
            if (selection.length > 0)
                for (var i in selection) {
                    if (this.options.resultType == "array"){
                        temp.push(selection);
                    }else{
                        temp += selection[i]["id"] + ","
                    }
                    tempTxt += selection[i][this.options.tableColumns[0].prop] + ",";
                }
            if (this.options.resultType != "array" && temp.endWith(",")) {
                temp = temp.substring(0, temp.length - 1);
            }
            if (tempTxt.endWith(",")) {
                tempTxt = tempTxt.substring(0, tempTxt.length - 1);
            }
            this.$emit('input', temp);
            this.currentValueTxt = tempTxt;
        },
        dblclick:function(row, event){
            if (this.options.selectType=='single'){
                var temp;
                if (this.options.resultType == "array"){
                    temp = [row];
                }else{
                    temp = row["id"];
                }
                var tempTxt = row[this.options.tableColumns[0].prop];
                this.$emit('input', temp);
                this.currentValueTxt = tempTxt;
                this.dialogVisible = false;
            }
        },
        clear:function(){
            this.$emit('input', "");
            this.currentValueTxt = "";
            this.dialogVisible = false;
        }
    }
});
var entityTxtComp = Vue.extend({
    template: `
    <span>{{entityTxt}}</span>
    `,
    data: function () {
        return {entityTxt:''};
    },
    //computed: {
    //    entityTxt: function () {
    //        var self = this;
    //        self.$http.put("sys/user/read/detail", {id:"2"})
    //                .then(function (response) {
    //                    self.entityTxt =response.data.data.userName;
    //                });
    //
    //        }
    //
    //},
    props: ["param","apiUrl","showProperty"],
    mounted:function(){
        var self = this;
        self.$http.put(self.apiUrl, self.param)
            .then(function (response) {
                var result = response.data.data;
                if (result instanceof Object){
                    self.entityTxt =result[self.showProperty];
                }else if (result instanceof Array && result.length>0){
                    self.entityTxt =result[0][self.showProperty];
                }
            });
    }
});
Vue.component('lz-entity-select', entitySelectComp);
Vue.component('lz-entity-txt', entityTxtComp);