	/**
	*管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"crm/customer",
            //是否显示编辑窗口
            editFormVisible: false,
            //是否显示详情窗口
            detailFormVisible: false,
          //资源分配时部门用户分页数据,其中包含分页信息与数据列表
            deptPageData: {},
            allMenu: {},
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
            //查询参数
            searchParams: {name:'',flowId:'',duty:'',upgradeFlow:'',birthday:'',province:'',city:'',insureNum:'',insureMoney:'',introduceNum:'',labels:'',currentApplyId:'',isHg:'',isService:'',salerId:'',managerId:'',directorId:'',ministerId:'',salerName:'',managerName:'',directorName:'',contracterId:'',contracterName:'',ministerName:'',serverId:'',serverName:'',oldApplyId:'',oldIsHg:'',oldIsService:'',oldServerId:'',oldServerName:'',contracterRemark:'',subordinate:'',enterTime:'',fromInfo:'',startTime:'',endTime:'',isNewClub:''},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            //当前操作的实体（编辑/新增）    
            entity: {name:'',flowId:'',upgradeFlow:'',duty:'',birthday:'',province:'',city:'',insureNum:'',insureMoney:'',introduceNum:'',labels:'',currentApplyId:'',isHg:'',isService:'',salerId:'',managerId:'',directorId:'',ministerId:'',salerName:'',managerName:'',directorName:'',contracterId:'',contracterName:'',ministerName:'',serverId:'',serverName:'',oldApplyId:'',oldIsHg:'',oldIsService:'',oldServerId:'',oldServerName:'',contracterRemark:'',upgradeRemark:'',createTime:''},
            editEntity: {id:'',upgradeRemark:'',upgradeFlow:'',duty:'',sex:'',age:''},
            //签单实体（详情）
            applyEntity: {customerId:'',customerName:'',fuzzyMobile:'',insuranceId:'',amount:'',type:'3',isUpgrade:'1',submitCode:'',submitCodeOld:'',startDate:'',endDate:'',remark:''},
            //市场部服务记录实体（详情）
            salerRecordEntity: {customerId:'',customerName:'',customerMobile:'',serverRecord:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',remark:'',createTime:''},
            //客服服务记录实体（详情）
            serverRecordEntity: {customerId:'',customerName:'',customerMobile:'',serveResult:'',serverRecord:'',serveTime:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',callTime:'',remark:'',createTime:''},
            //投诉实体（详情）
            complainEntity: {customerId:'',customerName:'',type:'',hannel:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',remark:'',createTime:''},
            //退款实体（详情）
            refundEntity: {customerId:'',customerName:'',refundMoney:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',remark:'',createTime:''},

            //表格中选中的行的集合
            selectDatas: [],
            //表单校验规则 api:https://github.com/yiminghe/async-validator
            rules: {
	    		name: [
	    		Vali.utf8mb4Len(false,50)
	    		]
            },
////////////会员资源URL
            allotUrl:"crm/customer/allotUpgrade",
////////////是否显示调配资源窗口
            allotVisible : false,
////////////市场部树
            userTreeData: [],
////////////市场部所有用户
            allUser: [],
////////////资源调配市场部选中行
            allotRow: {id:'',name:''},
////////////申请签单URL        	
            applyUrl:"crm/signApply/modelSignApply",
////////////是否显示申请签单窗口
            signVisible: false,
            applyVisible: false,
////////////申请签单的实体（编辑/新增）
            
//////////////申请签单表单校验规则 
            applyRules: {
            	customerName: [
            	         {required: true, message: '客户姓名不可为空', trigger: 'blur'},
            	],
            	fuzzyMobile: [
            	         {required: true, message: '电话号码不可为空', trigger: 'blur'},
            	         Vali.utf8mb4Len(false,11)
            	],
            	insuranceId: [
            	              {required: true, message: '产品不可为空', trigger: 'blur'},
            	],
            	amount: [
            	              {required: true, message: '签单金额不可为空', trigger: 'blur'},
            	              ],
            	startDate: [
            	              {required: true, message: '服务开始日期不可为空', trigger: 'change'},
  	            ],
  	            endDate: [
                            {required: true, message: '服务结束日期不可为空', trigger: 'change'},
                ],
                submitCode: [
                             {required: true, message: '消息提交码不可为空', trigger: 'blur'},
                          ],
            },
            activeName: 'apply1',
            //通话记录实体
            salerRecordEntity1: {customerId:'',customerName:'',customerMobile:'',serverRecord:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',},
            //父页面的引用
            //topwindow : top.window,
            //父页面方法的引用
            topdial : top.window.dial,
            topdrop : top.window.drop,
            showSaveButton : true,
            callDisable: false,
            isFirstSaveRecord : false,
            //本页面window的引用
            //currentdial : dial,
            
            flowType2 : true,
            flowType5 : true,
            flowType11 : true,
            
        },
        created: function () {
            var self = this;
            self.readList();
        },
        methods: {
            //查询
            readList: function () {
                var params = {
                    desc: true,
                    orderBy: "apply_time",
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
                var upgradeFlow = '';
                if (document.getElementById('upgradeFlow')) {
                	upgradeFlow = document.getElementById('upgradeFlow').value;
                	 params['upgradeFlow'] = upgradeFlow;
                }
                var isWaitUpgrade = '';
                if (document.getElementById('isWaitUpgrade')) {
                	isWaitUpgrade = document.getElementById('isWaitUpgrade').value;
                	params['isWaitUpgrade'] = isWaitUpgrade;
                }
                var isService = '';
                if (document.getElementById('isService')) {
                	isService = document.getElementById('isService').value;
                	params['isService'] = isService;
                }			
                
                
               
                this.$http.put(this.sourceUrl+'/upgrade/list', params)
                        .then(function (response) {
                            this.pageData = response.data;
                        });
            },
            //详情
            readDetail: function (row, event) {
                this.$http.put(this.sourceUrl+'/read/details', {id: row.customer.id})
                        .then(function (response) {
                            this.entity = response.data.data.customer;
                            this.applyEntity = response.data.data.apply;
                            this.salerRecordEntity = response.data.data.salerRecord;
                            this.serverRecordEntity = response.data.data.serverRecord;
                            this.complainEntity = response.data.data.complain;
                            this.refundEntity = response.data.data.refund;
                            this.compensationEntity = response.data.data.compensation;
                            
                            this.editEntity.id = response.data.data.customer.id;
                            this.editEntity.upgradeRemark = response.data.data.customer.upgradeRemark;
                            this.editEntity.duty = response.data.data.customer.duty;
                            this.editEntity.sex = response.data.data.customer.sex;
                            this.editEntity.age = response.data.data.customer.age;
                            
                            this.flowTypeSwitch(this.entity.upgradeFlow);
                            
                            this.detailFormVisible = true;
                        });
            },
            
            flowTypeSwitch : function (flowId) {
            	var self = this;
            	if (flowId == "10000") {
            		self.flowType11 = false;
            		self.flowType5 = true;
            		self.flowType2 = true;
				} else if (flowId == "33333") {//未添加微信
            		self.flowType2 = false;
            		self.flowType5 = true;
            		self.flowType11 = true;
				} else if (flowId == "44444") {
            		self.flowType5 = false;
            		self.flowType2 = true;
            		self.flowType11 = true;
				} else {
					self.flowType5 = true;
            		self.flowType2 = true;
            		self.flowType11 = true;
				}
            },
            //表格记录选择事件
            selectionChange: function (selection) {
                var self = this;
                self.selectDatas = selection;
            },
////////////弹出调配资源窗口
            showAllotDialog: function () {
            	var self = this;
            	if ( null == self.selectDatas || self.selectDatas.length <= 0) {
            		self.$alert('请选择要分配的客户');
				} else {
					self.allotVisible = true;
					self.readAllUser();
				}
            },
////////////获取市场部树
            readAllUser: function () {
                 var self = this;
                 var params = {
                	 asc: false,
                     orderBy: "update_time",
                     pageNum: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalCurrentPage : 1),
                     pageSize: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalPageSize : 10)
                 };
                 this.$http.put('sys/dept/read/list', {"keyword" : '升级',"enable":"1","pageSize":"50"})
                     .then(function (response) {
                         self.allMenu = response.data.data;
                         var tempList = self.convertMenus2Tree(response.data.data);//this.allMenu
                         self.userTreeData = tempList;
                 });
            },
            //资源分配,市场部门节点
            treeNodeClick : function(selected){
            	this.$http.put("sys/user/read/list", {"deptId": selected.id,"enable":"1","locked":"0","pageSize":"100"})
                	.then(function (response) {
                    this.deptPageData = response.data;
                });
            },
            //监听选中的人
            /*userClick : function (selected){
            	this.deptSelectData = selected;
            },*/
            //发送分配请求
            allot : function(){
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
            		self.$alert('请选择要资源接收人');
            	}else{
            		self.$confirm('您确定要调配吗?', '提示', {
            			confirmButtonText: '确定',
            	        cancelButtonText: '取消',
            	        type: 'warning'
                    }).then(function () {//{body: params}
                    	self.$http.post(self.allotUrl, {"ids":params.ids ,"userId":self.allotRow.id})
                    	.then(function (response) {
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
                    /*forFn_convertMenus2TreeItems(menus, menus[j], lev);
                    temp.push(menus[j])*/
                	if (menus[j].parentId == "6") {
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
            dayFormat : function (val) {
            	this.searchParams.enterTime = val;
            },
            dayFormat1 : function (val) {
            	this.searchParams.startTime = val;
            },
            dayFormat2 : function (val) {
            	this.searchParams.endTime = val;
            },
            //抽取20个客户（10000原始客户）
            extractCustomer: function () {
            	var self = this;
            	self.$http.post(self.sourceUrl+'/allotUpgrade/extract')
            	.then(function (response) {
            		var result = response.data.data;
            		self.entity = result;//所有保存，添加此行，以避免重复提交
            		if (self.entity) {
            			self.$notify({
            				title: '成功',
            				message: '抽取20个客户',
            				type: 'success',
            				duration: 2000
            			});
            			self.readList();
					}
            	});
            },
            //修改流程（10000原始客户，20000丢弃客户，30000 已转客服 (旧升级流程  未接客户)，33333未添加微信， 44444已添加微信， 50000未明确客户，66666可聊客户，88888意向客户，99999成交客户）
            changeFlow: function (entityId,entityName,upgradeFlow) {
            	var self = this;
            	if(entityId != null && entityId == ""){
            		self.$alert('无法获取当前行ID');
            	}else{
            		self.$confirm('您确定要移动客户【'+entityName+'】吗？', '提示', {
            			confirmButtonText: '确定',
            			cancelButtonText: '取消',
            			type: 'warning'
            		}).then(function () {
            			self.entity = {};
                    	self.entity.id = entityId;
                    	self.entity.upgradeFlow = upgradeFlow;
                        self.$http.post(self.sourceUrl+'/changeUpgradeFlow', self.entity)
                                .then(function (response) {
                                	var result = response.data.data;
                                    self.entity = result;//所有保存，添加此行，以避免重复提交
                                    self.flowTypeSwitch(self.entity.upgradeFlow);
                                    self.readList();
                                });
            		}).catch(function(){
            			self.$message({
            				type: 'info',
            				message: '取消移动客户'
            			});
            		});
            	}
            },
            //客户分配到客服
            allotServer: function (entityId,entityName) {
            	var self = this;
            	if(entityId != null && entityId == ""){
            		self.$alert('无法获取当前行ID');
            	}else{
            		self.$confirm('您确定要移动客户  【'+entityName+'】 到客服吗？', '提示', {
            			confirmButtonText: '确定',
            			cancelButtonText: '取消',
            			type: 'warning'
            		}).then(function () {
            			self.entity = {};
            			self.entity.id = entityId;
            			self.entity.isService = 0;
            			self.entity.upgradeFlow = 30000;
            			self.$http.post(self.sourceUrl+'/changeServer', self.entity)
            			.then(function (response) {
            				var result = response.data.data;
            				self.entity = result;//所有保存，添加此行，以避免重复提交
            				self.readList();
            				self.detailFormVisible = false;
            			});
            		}).catch(function(){
            			self.$message({
            				type: 'info',
            				message: '取消移动客户'
            			});
            		});
            	}
            },
            //丢弃客户
            discardCustomer : function () {
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
            	if(self.selectDatas.length == 0){
            		self.$alert('请选择客户');
            	}else{
            		self.$confirm('您确定要丢弃【'+self.selectDatas.length+'】个客户吗?', '提示', {
            			confirmButtonText: '确定',
            			cancelButtonText: '取消',
            			type: 'warning'
            		}).then(function () {//{body: params}
            			self.$http.post(self.sourceUrl+'/discardCustomer', {"ids":params.ids,"upgradeFlow":"20000"})
            			.then(function (response) {
            				self.readList();
            			});
            		}).catch(function(){
            			self.$message({
            				type: 'info',
            				message: '取消丢弃'
            			});
            		});
            	}
            },
            //保存
            save: function () {
                var self = this;
                self.$refs["editForm"].validate(function(valid){
                    if (valid) {//校验通过
                        self.$http.post(self.sourceUrl, self.editEntity)
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
////////////弹出申请签单窗口
            showApplyDialog: function (id,name,fuzzyMobile) {
            	var self = this;
            	if (fuzzyMobile && typeof(fuzzyMobile)!="undefined" && fuzzyMobile != 0 && fuzzyMobile != "") {
            		self.applyEntity = {customerId:'',customerName:'',fuzzyMobile:'',insuranceId:'',amount:'',type:'3',isUpgrade:'1',submitCode:'',startDate:'',endDate:''};
            		self.applyEntity.customerId = id;
            		self.applyEntity.customerName = name;
            		self.applyEntity.fuzzyMobile = fuzzyMobile;
            		self.applyVisible = true;
            	} else {
            		self.$alert("请填写电话号码后，再申请签单");
            	}
            },
////////////申请签单
            apply: function () {
                var self = this;
                self.$refs["applyForm"].validate(function(valid){
                    if (valid) {//校验通过
                    	if (self.applyEntity.submitCode && typeof(self.applyEntity.submitCode) != "undefined" && self.applyEntity.submitCode != "") {
							self.$http.post(self.applyUrl, self.applyEntity)
							.then(function (response) {
								self.applyVisible = false;
								self.readList();
							});
						} else {
							self.$confirm('未填写消息提交码，是否继续提交吗?', '提示', {
								confirmButtonText: '确定',
								cancelButtonText: '取消',
								type: 'warning'
							}).then(function () {
								self.$http.post(self.applyUrl, self.applyEntity)
								.then(function (response) {
									self.applyVisible = false;
									self.readList();
								});
							}).catch(function() {
								self.$message({
									type: 'info',
									message: '取消提交'
								});
							});
						}
                    } else {
                        self.$message({
                            message: '请按要求输入内容',
                            type: 'warning'
                        });
                        return false;
                    }
                });

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
                    		self.applyEntity.submitCode = code;
                    	} else {
                    		self.applyEntity.submitCode = "";
                    		self.notify("校验失败","签单消息提交码错误！");
                    	}
                    });
                }
            },
            startTimer : function () {
            	var self = this;
            	var timer = setTimeout( () => {
            		var status = document.getElementById("status").value;
            		//是否循环
            		if (status == "2") {
            			if (self.isFirstSaveRecord == true) {
            				//自动保存通话记录（挂机后）
            				self.firstSaveRecord();
            				clearTimeout(timer);
            				console.log("停止计时");
            				console.log("通话状态：挂机");
            			}
					} else {
						if (status == "1") {
	            			self.salerRecordEntity1.timeLength = parseInt(self.salerRecordEntity1.timeLength) + parseInt(1);
	            			//修改状态为已接通
	            			self.salerRecordEntity1.type = "1";
	            			//
	            			var callFile = document.getElementById("callFile").value;
	            			if (self.isFirstSaveRecord == false && callFile && callFile.length > 5) {
	            				//自动保存通话记录（接通后）
	            				self.firstSaveRecord();
	            				self.isFirstSaveRecord = true;
							}
	            			console.log("通话状态：接通");
	            		} else {
	            			console.log("通话状态：... ...");
	            		}
						//循环
						self.startTimer();
					}
	        	},1000);
            },
          //详情页内拨打电话
            detailsCall : function () {
            	var self = this;
            	//锁定拨打按钮
		    	self.callDisable = true;
            	if (self.entity.mobile && typeof(self.entity.mobile) != "undefined" && self.entity.mobile.length == 11) {
            		var user = JSON.parse(sessionStorage.getItem("user"));
                	if (user.agentNo && typeof(user.agentNo) != "undefined" && user.agentNo != "" && user.agentNo > 0) {
                		self.topdial(self.entity.mobile);
    			    	//客户信息
    			    	self.salerRecordEntity1.customerId = self.entity.id;
    			    	self.salerRecordEntity1.customerName = self.entity.name;
    			    	self.salerRecordEntity1.customerMobile = self.entity.mobile;
    			    	
    			    	document.getElementById("status").value = "";
    			    	document.getElementById("callFile").value = "";
    			    	//开始记录通话时长
    			    	self.salerRecordEntity1.timeLength = 0;
    			    	//初始化通话状态为：未接通
    					self.salerRecordEntity1.type = "0";
    			    	//激活保存按钮
    			    	self.showSaveButton = false;
    			    	//
    			    	self.isFirstSaveRecord = false;
    			    	self.startTimer();
                	} else {
                		//激活拨打按钮
        		    	self.callDisable = false;
                		self.$message.error('无分机号，不可拨打电话、不可保存通话记录');
                	}
				} else {
					//激活拨打按钮
    		    	self.callDisable = false;
					self.$message.error('无手机号码，或手机号码不是11位');
				}
            },
            //详情页保存电话服务记录
            detailsSaveRecord : function () {
            	var self = this;
            	var callFile = document.getElementById("callFile").value;
            	self.salerRecordEntity1.callFile = callFile;
            	console.log("录音文件："+self.salerRecordEntity1.callFile);
				self.$refs["salerRecordForm"].validate(function(valid){
					if (valid) {//校验通过
						self.$http.post("crm/salerRecord", self.salerRecordEntity1)
						.then(function (response) {
							self.showSaveButton = true;
							self.flushSalerRecord();
							//激活拨打按钮
	        		    	self.callDisable = false;
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
            //关闭详情窗事件
            handleClose : function () {
            	var self = this;
            	if (!self.showSaveButton) {
            		/*self.$notify.error({
            				message: '请先保存通话记录',
            	        	offset: 100
            	        });*/
            		self.$message.error('请先保存通话记录');
            	} else {
            		self.detailFormVisible = false;
            	}
            },
          //刷新通话记录
            flushSalerRecord: function () {
            	var self = this;
                this.$http.put('crm/salerRecord/read/list/customer', {customerId: self.entity.id})
                .then(function (response) {
                	self.salerRecordEntity = response.data.data;
                });
            },
            //提示语工具
            notify : function (title,text) {
            	this.$notify.error({
            		title: title,
            		message: text
            	});
            },
            //语音识别
            salerRecordText : function (entityId) {
            	window.open("/business/call/salerRecordTranslated.html?customerId=" + entityId);
            },
            startDateFormat : function (val) {
            	this.applyEntity.startDate = val;
            },
            endDateFormat : function (val) {
            	this.applyEntity.endDate = val;
            },
            isShowServer :function (isService) {
            	if (isService == 5) {
					return true;
				} else {
					return false;
				}
            }
        }
    });