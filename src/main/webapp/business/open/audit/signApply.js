	/**
	*管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"crm/signApply",
        	customerUrl:"crm/customer",
            //是否显示编辑窗口
            editFormVisible: false,
            //是否显示驳回窗口
            rejectFormVisible: false,
            //是否审核分配窗口
            allotVisible: false,
            //是否显示详情窗口
            detailFormVisible: false,
            //查询参数
            searchParams: {customerId:'',customerName:'',wechatNo:'',mobile:'',fromInfo:'',insureNum:'',insuranceId:'',insuranceName:'',amount:'',salerId:'',managerId:'',directorId:'',ministerId:'',subordinate:'',},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            //当前操作的实体（编辑/新增）
            entity: {customerId:'',customerName:'',wechatNo:'',mobile:'',fromInfo:'',insureNum:'',insuranceId:'',insuranceName:'',amount:'',startDate:'',endDate:'',salerId:'',managerId:'',directorId:'',ministerId:'',salerName:'',managerName:'',directorName:'',ministerName:'',signStatus:'',auditId:'',auditName:'',auditReason:'',auditTime:'',submitCode:'',sex:'',age:'',salerId:''},
            //表格中选中的行的集合
            selectDatas: [],
            //资源分配时部门用户分页数据,其中包含分页信息与数据列表
            deptPageData: {},
            //商务部树
            userTreeData: [],
            key: "id",
          //资源分配时部门用户表字段
            tableColumns: [
               {
                   prop: "userName",
                   label: "用户名"
               },
               {
                   prop: "staffNo",
                   label: "工号"
               }],
            //审核分配商务部选中客服
            allotRow : {},
            //分配URL
            allotUrl : "crm/signApply/audit",
            //驳回URL
            rejectUrl : "crm/signApply/reject",
          //表单校验规则 api:https://github.com/yiminghe/async-validator
            rules: {
            	sex: [
            	{required: true, message: '此项不可为空', trigger: 'blur'},
            ],
            	age: [
            	{required: true, message: '此项不可为空', trigger: 'blur'},
            ],
	    		insuranceId: [
	    		{required: true, message: '此项不可为空', trigger: 'blur'},
	    	],
	    		policyholderIdentifyNumber: [
	    	    Vali.utf8mb4Len(false,18)
	    	],
            },
            rejectRules: {
            	auditReason: [
	    		{required: true, message: '此项不可为空', trigger: 'blur'},
	    	],
            },
            customerEntity: {name:'',flowId:'',policyholderId:'',duty:'',birthday:'',province:'',city:'',insureNum:'',insureMoney:'',introduceNum:'',labels:'',currentApplyId:'',isHg:'',isService:'',salerId:'',managerId:'',directorId:'',ministerId:'',salerName:'',managerName:'',directorName:'',contracterId:'',contracterName:'',ministerName:'',serverId:'',serverName:'',oldApplyId:'',oldIsHg:'',oldIsService:'',oldServerId:'',oldServerName:'',contracterRemark:'',remark:'',createTime:''},
            //签单实体（详情）
            applyEntity: {customerId:'',customerName:'',policyNo:'',belong:'',insuranceId:'',insuranceName:'',introducerId:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',remark:'',createTime:'',resourceType:'',upgraderId:'',isUpgrade:''},
            //市场部服务记录实体（详情）
            salerRecordEntity: {customerId:'',customerName:'',customerMobile:'',serveResult:'',serverRecord:'',serveTime:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',callTime:'',remark:'',createTime:''},
            activeName: 'apply1',
            areaData : {},
            //区域弹窗
            areaVisible: false,
            
            salerMap : [],
            loading: false,
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
                    orderBy: "create_time",
                    pageNum: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalCurrentPage : 1),
                    pageSize: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalPageSize : 500)
                };
                //将查询条件合并到请求参数中
                for (var item in this.searchParams) {
                	if (typeof this.searchParams[item] == 'boolean'){
                        params[item] = this.searchParams[item];
                    }else if (this.searchParams[item] != "") {
                        params[item] = this.searchParams[item];
                    }
                }
                this.$http.put(this.sourceUrl+'/checkPending/audit/contracter/list/nonsort', params)
                        .then(function (response) {
                            this.pageData = response.data;
                        });
            },
            //详情
            /*readDetail: function (entityId) {
                this.$http.put(this.sourceUrl+'/read/detail', {id: entityId})
                        .then(function (response) {
                            this.entity = response.data.data;
                            this.detailFormVisible = true;
                        });
            },*/
            //详情
            readDetail: function (row, event) {
            	if (null != row.insureNum && row.insureNum > 1) {
            		this.$http.put(this.customerUrl+'/read/openDetails', {id: row.customerId})
                    .then(function (response) {
                        this.customerEntity = response.data.data.customer;
                        this.applyEntity = response.data.data.apply;
                        this.salerRecordEntity = response.data.data.salerRecord;
                        this.detailFormVisible = true;
                    });
            		
				} else {
					this.$alert("当前为首次申请签单，无客户详情");
				}
            },
            //弹出编辑窗口
            showEditDialog: function (entityId) {
                this.$http.put(this.sourceUrl+'/read/detail', {id: entityId})
                        .then(function (response) {
                            this.entity = response.data.data;
                            this.entity.salerId = "";
                            this.editFormVisible = true;
                        });
            },
            //保存
            save: function () {
                var self = this;
                self.$refs["editForm"].validate(function(valid){
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
                self.readList();
            },
            //表格记录选择事件
            selectionChange: function (selection) {
                var self = this;
                self.selectDatas = selection;
            },
            //打开审核分配窗口
            showAllotDialog: function () {
            	var self = this;
            	if ( null == self.selectDatas || self.selectDatas.length <= 0) {
            		self.$alert('请选择要分配的客户');
				} else {
					self.allotVisible = true;
					self.readAllUser();
				}
            },
            //获取用户树
            readAllUser: function () {
                 var self = this;
                 var params = {
                	 asc: false,
                     orderBy: "create_time",
                     dealerStatus : "0",
                     pageNum: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalCurrentPage : 1),
                     pageSize: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalPageSize : 10)
                 };
                 this.$http.put('sys/dept/read/list', {"parentId" : 3,"enable":"1","pageSize":"50"})
                     .then(function (response) {
                         self.allMenu = response.data.data;
                         var tempList = self.convertMenus2Tree(response.data.data);//this.allMenu
                         self.userTreeData = tempList;
                 });
            },
          //资源分配,商务部门节点
            treeNodeClick : function(selected){
            	this.$http.put("sys/user/read/list", {"deptId": selected.id,"enable":"1","locked":"0","pageSize":"100"})
                	.then(function (response) {
                    this.deptPageData = response.data;
                });
            },
            convertMenus2Tree: function (menus1) {
                var temp = [], lev = 0;
                var menus = clone(menus1);
                var forFn_convertMenus2TreeItems = function (arr, menu, lev) {
                    for (var i in arr) {
                        var item = arr[i];
                        item.label = item.deptName;
                        if (item.parentId == menu.id) {
                            if (typeof(menu.children) == "undefined") {
                                menu.children = [];
                            }
                            menu.children.push(item);
                            item.lev = lev;
                            forFn_convertMenus2TreeItems(arr, item, lev + 1);
                        }
                    }
                };
                for (var j in menus) {
                    if (menus[j].parentId == "3") {
                        forFn_convertMenus2TreeItems(menus, menus[j], lev);
                        //return depts[j];
                        temp.push(menus[j])
                    }
                }
                return temp;
            },
            //提交审核
            allot : function () {
            	var ids = new Array();
                for(var i in this.selectDatas){
                    ids.push(this.selectDatas[i].id);
                }
            	var params = {};
                if (ids) {
                	//params.userId = this.allotRow.id;
                    //删除单个实体
                    if (ids instanceof Array) {
                        params.ids = ids;
                    } else {//批量删除
                        params.ids = [ids];
                    }
                }
                var self = this;
            	if(self.allotRow.length == 0){
            		self.$alert('请选择要分配合规人员');
            	}else{
            		self.$confirm('您确定要分配给['+self.allotRow.name+']吗？', '提示', {
            			confirmButtonText: '确定',
            	        cancelButtonText: '取消',
            	        type: 'warning'
                    }).then(function () {//{body: params}
                    	self.$http.post(self.allotUrl, {"ids":params.ids ,"userId":self.allotRow.id})
                    	.then(function (response) {
                        //self.$alert(self.selectDatas.id);
                        
                    	self.allotVisible = false;
                    	self.readList();
                    	});
                    }).catch(function(){
                    	self.$message({
                            type: 'info',
                            message: '取消分配'
                          });
                    });
            	}
            },
            allotCurrentChange(val) {
                this.allotRow.id = val.id;
                this.allotRow.name = val.userName;
            },
            startDateFormat : function (val) {
            	this.applyEntity.startDate = val;
            },
            endDateFormat : function (val) {
            	this.applyEntity.endDate = val;
            },
            dayFormat : function (val) {
            	this.entity.auditTime = val;
            },
            dayFormat1 : function (val) {
            	this.entity.insurederBirthday = val;
            },
            //
            showRejectDialog : function(entityId) {
            	this.$http.put(this.sourceUrl+'/read/detail', {id: entityId})
                .then(function (response) {
                    this.entity = response.data.data;
                    this.rejectFormVisible = true;
                });
            },
            //审核驳回
            shbh: function () {
                var self = this;
                self.$refs["rejectForm"].validate(function(valid){
                    if (valid) {//校验通过
                        self.$http.post(self.sourceUrl+'/reject', self.entity)
                                .then(function (response) {
                                    var result = response.data.data;
                                    self.rejectFormVisible = false;
                                    self.readList();
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
          //匹配区域
            fuzzyMatch : function (policyholderRegion) {
            	var self = this;
            	if (policyholderRegion) {//不为空时
                    self.$http.put("bd/area/fuzzyMatch", {"area":policyholderRegion})
                    .then(function (response) {
                    	self.areaData = response.data;
                    	//如果是唯一的，就自动填写
                    	if (self.areaData.total == 1) {
                    		if (self.areaData.data[0].parentId > 0) {
                    			self.entity.policyholderRegion = self.areaData.data[0].parentName + "/" + self.areaData.data[0].province;
                    			self.entity.policyholderProvince = self.areaData.data[0].parentName;
                    			self.entity.policyholderCity = self.areaData.data[0].province;
                    		} else {
                    			self.entity.policyholderRegion = self.areaData.data[0].province;
                    			self.entity.policyholderProvince = self.areaData.data[0].parentName;
                    		}
                    	} else if (self.areaData.total > 1) {
                    		self.areaVisible = true;
                    	}
                    	//如果不唯一，就弹窗提示（选择后再自动填写）
                    });
                }
            },
          //采纳（选择某一个区域）
            accept : function (row, event) {
            	var self = this;
            	if (row.parentId > 0) {
        			self.entity.policyholderRegion = row.parentName + "/" + row.province;
        			self.entity.policyholderProvince = row.parentName;
        			self.entity.policyholderCity = row.province;
        		} else {
        			self.entity.policyholderRegion = row.province;
        			self.entity.policyholderProvince = row.parentName;
        		}
            	self.areaVisible = false;
            },
          //验证签单消息提交码
            checkSubmitCode : function (code) {
            	var self = this;
            	if (code) {//不为空时
                    self.$http.put("crm/signApply/check/submitCode", {"submitCode":code})
                    .then(function (response) {
                    	var verify = response.data;
                    	console.log(verify.data);
                    	//如果是唯一的，就自动填写
                    	if (verify.data == true) {
                    		self.entity.submitCode = code;
                    		self.$notify({
                    			type: 'success',
                        		title: "校验成功",
                        		message: "签单消息提交码验证成功"
                        	});
                    	} else {
                    		self.entity.submitCode = "";
                    		self.notify("校验失败","签单消息提交码错误！");
                    	}
                    });
                }
            },
            //提示语工具
            notify : function (title,text) {
            	this.$notify.error({
            		title: title,
            		message: text
            	});
            },
            selectSaler : function (query) {
            	var self = this;
            	if (query !== '') {
            		self.loading = true;
            		setTimeout(() => {
            			self.loading = false;
            			self.$http.put('sys/user/read/list', {"userName":query,"userGroup":88002001})
                        .then(function (response) {
                            var result = response.data.data;
                            self.salerMap = result.map(item => {
                            	return {label:item.userName,value:item.id};
                            });
                        });
            		}, 200);
            	} else {
            		self.salerMap = [];
            	}
            },
         // 详细页保存（年龄和性别）
            saveDetail1: function () {
                var self = this;
                if (self.customerEntity.age) {//校验通过
                    self.$http.post(self.customerUrl, {id:self.customerEntity.id, age:self.customerEntity.age})
                            .then(function (response) {
                                var result = response.data.data;
                                self.editFormVisible = false;
                                if (self.customerEntity.id) {//修改
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
            },
            // 详细页保存（年龄和性别）
            saveDetail2: function () {
                var self = this;
                if (self.customerEntity.sex) {//校验通过
                    self.$http.post(self.customerUrl, {id:self.customerEntity.id, sex:self.customerEntity.sex})
                            .then(function (response) {
                                var result = response.data.data;
                                self.editFormVisible = false;
                                if (self.customerEntity.id) {//修改
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
            },
        }
    });