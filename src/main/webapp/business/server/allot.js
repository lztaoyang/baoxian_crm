	/**
	*管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"crm/customer",
        	//分配客服
        	allotUrl : "crm/customer/allotServer",
			//确认接收
			receiveUrl : "crm/customer/receive",
			allotVisible : false,
			//资源分配时部门用户分页数据,其中包含分页信息与数据列表
            deptPageData: {},
            //客服部树
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
            //分配客服部选中客服
            allotRow : {},
            //是否显示编辑窗口
            editFormVisible: false,
            //是否显示详情窗口
            detailFormVisible: false,
            //查询参数
            searchParams: {name:'',fromInfo:'',insuranceId:'',policyholderId:'',duty:'',birthday:'',province:'',city:'',insureNum:'',insureMoney:'',introduceNum:'',labels:'',currentApplyId:'',isHg:'',isService:'',salerId:'',managerId:'',directorId:'',ministerId:'',salerName:'',managerName:'',directorName:'',contracterId:'',contracterName:'',ministerName:'',serverId:'',serverName:'',oldApplyId:'',oldIsHg:'',oldIsService:'',oldServerId:'',oldServerName:'',contracterRemark:'',subordinate:'',enterTime:''},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            //当前操作的实体（编辑/新增）
            entity: {name:'',flowId:'',policyholderId:'',duty:'',birthday:'',province:'',city:'',insureNum:'',insureMoney:'',introduceNum:'',labels:'',currentApplyId:'',isHg:'',isService:'',salerId:'',managerId:'',directorId:'',ministerId:'',salerName:'',managerName:'',directorName:'',contracterId:'',contracterName:'',ministerName:'',serverId:'',serverName:'',oldApplyId:'',oldIsHg:'',oldIsService:'',oldServerId:'',oldServerName:'',},
            //表格中选中的行的集合
            selectDatas: [],
            //表单校验规则 api:https://github.com/yiminghe/async-validator
            rules: {
	    		name: [
	    		Vali.utf8mb4Len(false,50)
	    	],
	    		flowId: [
	    	],
	    		policyholderId: [
	    		Vali.long()
	    	],
	    		duty: [
	    		Vali.utf8mb4Len(false,50)
	    	],
	    		birthday: [],
	    		province: [
	    		Vali.utf8mb4Len(false,50)
	    	],
	    		city: [
	    		Vali.utf8mb4Len(false,50)
	    	],
	    		insureNum: [
	    	],
	    		insureMoney: [
	    		Vali.double()
	    	],
	    		introduceNum: [
	    	],
	    		labels: [
	    		Vali.utf8mb4Len(false,500)
	    	],
	    		currentApplyId: [
	    		Vali.long()
	    	],
	    		isHg: [
	    	],
	    		isService: [
	    	],
	    		salerId: [
	    		Vali.long()
	    	],
	    		managerId: [
	    		Vali.long()
	    	],
	    		directorId: [
	    		Vali.long()
	    	],
	    		ministerId: [
	    		Vali.long()
	    	],
	    		salerName: [
	    		Vali.utf8mb4Len(false,50)
	    	],
	    		managerName: [
	    		Vali.utf8mb4Len(false,50)
	    	],
	    		directorName: [
	    		Vali.utf8mb4Len(false,50)
	    	],
	    		contracterId: [
	    		Vali.long()
	    	],
	    		contracterName: [
	    		Vali.utf8mb4Len(false,50)
	    	],
	    		ministerName: [
	    		Vali.utf8mb4Len(false,50)
	    	],
	    		serverId: [
	    		Vali.long()
	    	],
	    		serverName: [
	    		Vali.utf8mb4Len(false,50)
	    	],
	    		oldApplyId: [
	    		Vali.long()
	    	],
	    		oldIsHg: [
	    	],
	    		oldIsService: [
	    	],
	    		oldServerId: [
	    		Vali.long()
	    	],
	    		oldServerName: [
	    		Vali.utf8mb4Len(false,50)
	    	],
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
                    orderBy: "apply_time",
                    isService : "0",
                    pageNum: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalCurrentPage : 1),
                    pageSize: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalPageSize : 10)
                };
                //将查询条件合并到请求参数中
                for (var item in this.searchParams) {
                	if (typeof this.searchParams[item] == 'boolean'){
                        params[item] = this.searchParams[item];
                    }else if (this.searchParams[item] != "") {
                        params[item] = this.searchParams[item];
                    }
                }
                this.$http.put(this.sourceUrl+'/allot/list', params)
                        .then(function (response) {
                            this.pageData = response.data;
                        });
            },
            //详情
            readDetail: function (entityId) {
                this.$http.put(this.sourceUrl+'/read/details', {id: entityId})
                        .then(function (response) {
                            this.entity = response.data.data.customer;
                            this.detailFormVisible = true;
                        });
            },
            //弹出编辑窗口
            showEditDialog: function (entityId) {
                this.$http.put(this.sourceUrl+'/read/detail', {id: entityId})
                        .then(function (response) {
                            this.entity = response.data.data;
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

            },
            showAllotDialog : function () {
            	var self = this;
        		if (self.selectDatas.length > 0) {
        			self.allotVisible = true;
        			self.readAllUser();
				} else {
					self.$alert("未选择客户");
				}
        		
            },
            //分配客服
            allot : function () {
            	var ids = new Array();
                for(var i in this.selectDatas){
                    ids.push(this.selectDatas[i].customer.id);
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
            		self.$alert('请选择要分配客服人员');
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
                            message: '取消批量接收'
                          });
                    });
            	}
            },
            //电话服务
            call: function (mobile) {
            	//1.调用 PBX CTI 接口
            	//2.弹出电话服务弹窗（弹窗里面有挂断电话的按钮）
            },
            //表格记录选择事件
            selectionChange: function (selection) {
                var self = this;
                self.selectDatas = selection;
            },
          //获取用户树
            readAllUser: function () {
                 var self = this;
                 var params = {
                	 asc: false,
                     orderBy: "id",
                     pageNum: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalCurrentPage : 1),
                     pageSize: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalPageSize : 10)
                 };
                 this.$http.put('sys/dept/read/list', {"keyword" : '客服',"enable":"1","pageSize":"50"})
                     .then(function (response) {
                         self.allMenu = response.data.data;
                         var tempList = self.convertMenus2Tree(response.data.data);//this.allMenu
                         self.userTreeData = tempList;
                 });
            },
          //资源分配,客服部门节点
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
                	if (menus[j].parentId == "4") {
                		forFn_convertMenus2TreeItems(menus, menus[j], lev);
                    	//return depts[j];
                    	temp.push(menus[j])
                    }
                }
                return temp;
            },
            allotCurrentChange(val) {
                this.allotRow.id = val.id;
                this.allotRow.name = val.userName;
            },
            //接收
            receive : function (entityId,entityName) {
            	var self = this;
            	if(entityId != null && entityId == ""){
            		self.$alert('无法获取当前行ID');
            	}else{
            		self.$confirm('您确定要接收客户['+entityName+']吗？', '提示', {
            			confirmButtonText: '确定',
            	        cancelButtonText: '取消',
            	        type: 'warning'
                    }).then(function () {
                    	self.$http.post(self.receiveUrl, {"ids":entityId})
                    	.then(function (response) {
                    		self.readList();
                    	});
                    }).catch(function(){
                    	self.$message({
                            type: 'info',
                            message: '取消接收'
                          });
                    });
            	}
            },
          //暂不服务客户
            changeService: function (entityId) {
            	var self = this;
    			self.entity = {};
    			self.entity.id = entityId;
    			self.entity.isService = 5;
    			self.$http.post(self.sourceUrl+'/changeServer', self.entity)
    			.then(function (response) {
    				var result = response.data.data;
    				self.entity = result;//所有保存，添加此行，以避免重复提交
    				self.readList();
    			});
            },
            /**
             * 批量暂不服务
             */
            changeServices : function () {
            	var self = this;
        		if (self.selectDatas.length > 0) {
        			for(var i in this.selectDatas){
                        var customerId = this.selectDatas[i].customer.id;
                        var upgradeFlow = this.selectDatas[i].customer.upgradeFlow;
                        var productId = this.selectDatas[i].apply.insuranceId;;
                        if (productId == 1042965257223069696 || productId == 1042965446226796544) {
                        	if (upgradeFlow != 30000) {
                        		this.changeService(customerId,upgradeFlow);
							}
						}
                    }
        			self.readList();
				} else {
					self.$alert("未选择客户");
				}
            },
            /*
             * 1042965257223069696 短线掘金
             * 1042965446226796544 大咖掌舵
             */
            isProduct : function (productId,upgradeFlow) {
            	if (productId == 1042965257223069696 || productId == 1042965446226796544) {
            		if (upgradeFlow != null && upgradeFlow !='' && upgradeFlow != undefined && upgradeFlow == 30000) {
            			return false;
					} else {
						return true;
					}
				} else {
					return false;
				}
            },
        }
    });