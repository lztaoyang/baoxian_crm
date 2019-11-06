/**
 *用户管理管理
 */
var vuePage = new Vue({
    el: '#app',
    data: {
        sourceUrl: "sys/user",
        //是否显示编辑窗口
        editFormVisible: false,
        //是否显示详情窗口
        detailFormVisible: false,
        //查询参数
        searchParams: {
            account: '',
            password: '',
            agentNo: '',
            userName: '',
            namePinyin: '',
            sex: '',
            avatar: '',
            userType: '',
            phone: '',
            birthDay: '',
            deptId: [],
            position: '',
            address: '',
            locked: '',
            parentId :'',
            userGroup : '',
          
        },
        //分页数据,其中包含分页信息与数据列表
        pageData: {},
        //当前操作的实体（编辑/新增）
        entity: {
            account: '',
            password: '',
            agentNo: '',
            userName: '',
            namePinyin: '',
            sex: '',
            avatar: '',
            userType: '',
            phone: '',
            birthDay: '',
            entryTime:'',
            education:'',
            deptId: [],
            position: '',
            address: '',
            parentId : '',
            userGroup : '',
            locked: '',
            flag:'0'
        },
        //表格中选中的行的集合
        //表单校验规则 api:https://github.com/yiminghe/async-validator
        rules: {
            
        },
        //部门树
        dept : [{}] ,
        defaultProps: {
            children: 'children',
            label: 'label'
        },
        currentDeptId : '',
    },
    mounted: function () {
        var self = this;
        self.readUser();
    },
    methods: {
        readAllRole: function () {
            var params = {
                asc: false,
                orderBy: "id",
                pageNum: (1),
                pageSize: (1000),
            };

            this.$http.put('sys/role/read/list', params)
                .then(function (response) {
                    this.allRole = response.data.data;
                });
        },
        readUser:function(){
        	 var params = {
        			 enable : "1",
                 	 locked : "0",
                     asc: false,
                     orderBy: "id",
                     pageNum: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalCurrentPage : 1),
                     pageSize: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalPageSize : 10),
                     deptId:''
                 };
        	 
        	//将查询条件合并到请求参数中
             for (var item in this.searchParams) {
                 if (this.searchParams.deptId && this.searchParams.deptId instanceof Array && this.searchParams.deptId.length > 0) {
                     params.deptId = this.searchParams.deptId[this.searchParams.deptId.length - 1]
                 } else if (typeof this.searchParams[item] == 'boolean') {
                     params[item] = this.searchParams[item];
                 } else if (this.searchParams[item] != "") {
                     params[item] = this.searchParams[item];
                 }

             }
        	 params.deptId = this.currentDeptId;

        	  this.$http.put(this.sourceUrl + '/read/JLUser', params)
              .then(function (response) {
                  this.pageData = response.data;
              });
        	
        },
        //详情
        readDetail: function (entityId) {
            this.$http.put(this.sourceUrl + '/read/detail', {id: entityId})
                .then(function (response) {
                    this.entity = response.data.data;
                    this.detailFormVisible = true;
                });
        },
        //弹出其他属性编辑窗口
        showEditDialog: function (entityId) {
            this.$http.put(this.sourceUrl + '/read/detail', {id: entityId})
                .then(function (response) {
                    this.entity = response.data.data;
                    this.editFormVisible = true;
                });
        },
        //保存
        save: function () {
            var self = this;
            self.$refs["editForm"].validate(function (valid) {
                if (valid) {//校验通过
                    self.$http.post(self.sourceUrl, self.entity)
                        .then(function (response) {
                            var result = response.data.data;
                            self.editFormVisible = false;
                            if (self.entity.id) {//修改
                                for (var i = 0; i < self.pageData.data.length; i++) {
                                    if (self.pageData.data[i].id === result.id) {
                                        Vue.set(self.pageData.data, i, result);
                                        break;
                                    }
                                }
                            } else {
                                self.pageData.data.unshift(result);
                            }
                        });
                } else {
                    self.$message({
                        message: '请按要求输入内容',
                        type: 'warning'
                    });
                    return false;
                }
            });
        },
        saveRow: function(row) {
        	var self = this;
        	// 日期转换
        	if (row.birthDay instanceof Date) {
        		row.birthDay = row.birthDay.Format('yyyy-MM-dd');
        	} 
        	if (row.entryTime instanceof Date) {
        		row.entryTime = row.entryTime.Format('yyyy-MM-dd');
        	}
        	self.$http.post(self.sourceUrl, row)
            .then(function (response) {
                var result = response.data.data;
//                self.editFormVisible = false;
//                if (self.entity.id) {//修改
//                    for (var i = 0; i < self.pageData.data.length; i++) {
//                        if (self.pageData.data[i].id === result.id) {
//                            Vue.set(self.pageData.data, i, result);
//                            break;
//                        }
//                    }
//                } else {
//                    self.pageData.data.unshift(result);
//                }
                console.log(result);
            });
        },
    }
});