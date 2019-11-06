/**
 *用户管理管理
 */
var vuePage = new Vue({
    el: '#app',
    data: {
        sourceUrl: "sys/user",
        //是否显示编辑窗口
        editFormVisible: false,
        otherEditFormVisible: false,
        //是否显示详情窗口
        detailFormVisible: false,
        roleFormVisible:false,
        passwordFormItemShow:true,
        currDeptId : '',
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
            locked: '0',
            parentId :'',
            userGroup : '',
            allotResourceNum :''
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
            education:'',
            deptId: [],
            position: '',
            address: '',
            parentId : '',
            userGroup : '',
            locked: '',
            flag:'0',
            allotResourceNum :'',
            staffNo : '',
            dingId : '',
        },
        userStauts: [{
		value: '0',
		label: '在职'
	}, {
		value: '1',
		label: '离职'
	}, ],
        //表格中选中的行的集合
        selectDatas: [],
        //表单校验规则 api:https://github.com/yiminghe/async-validator
        allRole:[],
        selectedRoles:[],
        rules: {
            account: [
                {required: true, message: '此项不可为空', trigger: 'blur'},
                Vali.utf8mb4Len(false, 50)
            ],
            password: [
                { required: true, message: '此项不可为空', trigger: 'blur' },
                Vali.utf8mb4Len(false, 50)
            ],
            userName: [
                { required: true, message: '此项不可为空', trigger: 'blur' },
                Vali.utf8mb4Len(false, 50)
            ],
            agentNo: [
                  Vali.int()
            ],
            sex: [
                Vali.int()
            ],
            education: [
              ],
            userType: [
                Vali.int()
            ],
            phone: [
                Vali.utf8mb4Len(false, 50)
            ],
            birthDay: [],
            deptId: [
                { required: true, message: '此项不可为空', trigger: 'blur' },
                Vali.long()
            ],
            position: [
                Vali.utf8mb4Len(false, 64)
            ],
            address: [
                Vali.utf8mb4Len(false, 256)
            ],
            parentId: [
                {required: true, message: '此项不可为空', trigger: 'blur'},
                Vali.utf8mb4Len(false, 20),
                Vali.long()
            ],
            userGroup: [
                {required: true, message: '此项不可为空', trigger: 'blur'},
                Vali.utf8mb4Len(false, 20),
                Vali.long()
            ],
            locked: [],
        },
        //部门树
        dept : [],
        defaultProps: {
            children: 'children',
            label: 'label'
        },
        currentDeptId : '',
        currentPositionId:'',
        
        seniorData : [],
    },
    mounted: function () {
        var self = this;
        self.readAllRole()
        //self.readList();
        self.loadNode();
        self.getSenior();
    },
    methods: {
        readAllRole: function () {
            var params = {
                asc: false,
                orderBy: "id",
                pageNum: (1),
                pageSize: (1000)
            };

            this.$http.put('sys/role/read/list', params)
                .then(function (response) {
                    this.allRole = response.data.data;
                });
        },
        //查询
        readList: function () {
            var params = {
                asc: false,
                orderBy: "id",
                pageNum: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalCurrentPage : 1),
                pageSize: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalPageSize : 50)
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
//            params.deptId = this.currDeptId;
//            console.log(this.currDeptId);
            
            this.$http.put(this.sourceUrl + '/read/list', params)
                .then(function (response) {
                    this.pageData = response.data;
                });
        },
        //
        readUser:function(){
        	 var params = {
                     asc: false,
                     orderBy: "id",
                     pageNum: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalCurrentPage : 1),
                     pageSize: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalPageSize : 50),
                     deptId:''
                 };
        	 params.deptId = this.currentDeptId;

           	 if(this.currentDeptId==''){
        		 this.$http.put(this.sourceUrl+'/read/list', params)
                 	 	.then(function (response) {
                          this.pageData = response.data;
                      });
        	 }
           	 
           	for (var item in this.searchParams) {
                 	if (typeof this.searchParams[item] == 'boolean'){
                         params[item] = this.searchParams[item];
                     }else if (this.searchParams[item] != "") {
                         params[item] = this.searchParams[item];
                     }
                 }
           	 
        	  this.$http.put(this.sourceUrl + '/read/user', params)
                      .then(function (response) {
                          this.pageData = response.data;
                      });
        	
        },
        //弹出新增窗口
        showAddDialog: function () {
            this.editFormVisible = true;
            for (var item in this.entity) {
                this.entity[item] = "";
                this.entity.flag = '0';
                this.passwordFormItemShow = true;
            }
            this.entity.deptId =this.currentDeptId;
            this.entity.parentId =this.currentPositionId;
            if(this.currentDeptId==''){
            	this.entity.deptId= '1';
            }
         
        },
        //弹出系统属性编辑窗口
        showEditDialog: function (entityId) {
            this.$http.put(this.sourceUrl + '/read/detail', {id: entityId})
                .then(function (response) {
                    this.entity = response.data.data;
                    this.editFormVisible = true;
                    this.passwordFormItemShow = false;
                });
            this.flag = 0;
        },
        //弹出其他属性编辑窗口
        showOtherEditDialog: function (entityId) {
            this.$http.put(this.sourceUrl + '/read/detail', {id: entityId})
                .then(function (response) {
                    this.entity = response.data.data;
                    this.otherEditFormVisible = true;
                });
        },
        //弹出角色窗口
        showRoleDialog: function (entityId) {
            var self = this;

            this.$http.put('sys/user/read/role', {userId: entityId})
                .then(function (response) {
                    self.entity.id = entityId;
                    self.roleFormVisible = true;
                    self.selectedRoles = [];
                    for(var i in response.data.data){
                        self.selectedRoles.push(response.data.data[i].roleId);
                    }
                });
        },
        saveRoles:function(){
            var self = this;
            var params = [];
            for(var i in self.selectedRoles){
                var userRole = {
                    "userId":self.entity.id,
                    "roleId":self.selectedRoles[i]
                };
                params.push(userRole);
            }
            self.$http.post('sys/user/update/role', params)
                .then(function (response) {
                    self.roleFormVisible = false;
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
                            self.otherEditFormVisible = false;
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
                            
                            if (self.entity.position == 1) {
                            	self.currentPositionId = result.id;
                            	//刷新上级字典
                            	self.getSenior();
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
        otherSave: function () {
        	var self = this;
        	self.$refs["otherEditForm"].validate(function (valid) {
        		if (valid) {//校验通过
        			self.$http.post(self.sourceUrl, self.entity)
        			.then(function (response) {
        				var result = response.data.data;
        				self.editFormVisible = false;
        				self.otherEditFormVisible = false;
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
        				
        				if (self.entity.position == 1) {
        					self.currentPositionId = result.id;
        					//刷新上级字典
        					self.getSenior();
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
        //删除
        del: function (entityIds) {
            var params = {};
            if (entityIds) {
                //删除单个实体
                if (entityIds instanceof Array) {
                    params.ids = entityIds;
                } else {//批量删除
                    params.ids = [entityIds];
                }
            }
            var self = this;
            self.$confirm('您确定要删除' + params.ids.length + '条记录吗?, 删除将无法恢复!', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(function () {
                self.$http.delete(self.sourceUrl, {body: params})
                    .then(function (response) {
                        var tempIndexArray = [];
                        //同步更新界面中的表格
                        for (var j in params.ids) {
                            for (var i in self.pageData.data) {
                                if (self.pageData.data[i].id == params.ids[j]) {
                                    self.pageData.data.splice(i, 1);
                                    break;
                                }
                            }
                        }
                        //self.entity = response.data.data;
                    });
            }).catch(function () {
            });
        },
        //批量删险
        delBatch: function () {
            var self = this;
            if (self.selectDatas.length == 0) {
                this.$alert('请选择要删除的记录', '提示', {
                    confirmButtonText: '确定',
                    callback: function (action) {
                        //if ("confirm"==action){
                    }
                });
            } else {
                var ids = new Array();
                for (var i in self.selectDatas) {
                    ids.push(self.selectDatas[i].id);
                }
                self.del(ids);
            }

        },
        //表格记录选择事件
        selectionChange: function (selection) {
            var self = this;
            self.selectDatas = selection;
            console.log(selection)
        },
        //检测用户名是否可用
        isExist : function (account,id) {
        	var self = this;
        	if (account) {//不为空时
        		
                self.$http.post(self.sourceUrl+"/isExist", {"account":account,"id":(id>0?id:null)})
                .then(function (response) {
                     /*var result = response.data.isexist;
                     if (result != null && result == 1) {
						self.$alert("该用户名已注册！");
                     } else {
                    	self.$alert("该用户名可用");
                     }*/
                });
            }
        },
        //查询下属部门员工
        handleNodeClick:function(node){
        	this.getUserByDeptId(node);
            },
        getUserByDeptId:function(node){
        	var params = {
                    asc: false,
                    orderBy: "id",
                    pageNum: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalCurrentPage : 1),
                    pageSize: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalPageSize : 50),
                    deptId: ""
                };
        	this.currDeptId = node.id;
        	params.deptId = node.id;
        	
        	for (var item in this.searchParams) {
                	if (typeof this.searchParams[item] == 'boolean'){
                        params[item] = this.searchParams[item];
                    }else if (this.searchParams[item] != "") {
                        params[item] = this.searchParams[item];
                    }
                }
        	
                //this.$http.put('sys/user/read/user', {deptId: node.id})
                this.$http.put('sys/user/read/user', params)
                           .then(function (response) {
                        	   this.pageData = response.data;
                        	   this.currentDeptId = node.id;
                        	   //当前部门负责人（position_ = 1）
                        	   for (var i in this.pageData.data) {
                                   if (this.pageData.data[i].position == 1) {
                                	   this.currentPositionId = this.pageData.data[i].id;
                                       break;
                                   }
                               }
                      });
         },
        //得到树
        loadNode:function(){
			this.$http.put('sys/dept/read/tree', {parentId: 0})
				.then(function (response) {
					var result = response.data.data.children;
					this.dept = result;
				});
        },
        //
        getSenior : function () {
        	var self = this;
        	self.$http.get('sys/dic/read/all')
			.then(function (response) {
				var result = response.data.data;
				if (null != result) {
					keys = [];
		            values = [];
		            for (var property in result.SENIOR) {
		            	keys.push(property);
		            }
		            for (var property in result.SENIOR) {
		            	values.push(result.SENIOR[property]); 
		            }
		            for (var j = 0; j < values.length ; j++) {
		            	self.seniorData.push({id:keys[j],name:values[j]});
		            }
					//全局变量【web存储】
                    sessionStorage.setItem("dic", JSON.stringify(response.data.data));
				}
			});
        }
        
    }
});