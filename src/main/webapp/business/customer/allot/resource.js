/**
	*管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"crm/resource",
        	//是否新增
        	addFormVisible: false,
            //是否显示详情窗口
            detailFormVisible: false,
            //查询参数
            searchParams: {fromInfo:'',name:'',mobile:'',createtime:'',wechatNo:'',qq:'',subordinate:'',salerId:''},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            //资源分配时部门用户分页数据,其中包含分页信息与数据列表
            deptPageData: {},
            //资源分配部门人员被选中的数据
            //deptSelectData: [],
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
            //当前操作的实体（编辑/新增）
            entity: {name:'',fromInfo:'',wechatNo:'',mobile:'',qq:'',fuzzyMobile:'',md5Mobile:'',birthday:'',sex:'',duty:'',remark:'',demand:''},
////////////申请签单的实体（编辑/新增）
            applyEntity: {customerId:'',customerName:'',fuzzyMobile:'',insuranceId:'',amount:'',type:'1',submitCode:'',startDate:'',endDate:''},
            //表格中选中的行的集合
            selectDatas: [],
            //表单校验规则 api:https://github.com/yiminghe/async-validator
            addRules: {
            	demand: [
           	         {required: true, message: '请在备注里面请说明录入资源的来源', trigger: 'blur'},
        	    ]
            },
            editRules: {
	    		mobile: [
	    		Vali.long(),
	    		Vali.utf8mb4Len(false,11)
	    	],
	    	qq: [
	 	    		Vali.long(),
	 	    		Vali.utf8mb4Len(false,11)
	 	    	],
            },
////////////调配资源URL
            allotUrl:"crm/resource/allotTestResource",
////////////是否显示调配资源窗口
            allotVisible : false,
////////////市场部树
            userTreeData: [],
////////////市场部所有用户
            allUser: [],
////////////资源调配市场部选中行
            allotRow: {},
////////////申请签单URL        	
            applyUrl:"crm/signApply/modelSignApply",
////////////是否显示申请签单窗口
            applyVisible: false,
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
            	 submitCode: [
                          {required: true, message: '消息提交码不可为空', trigger: 'blur'},
                  ],
            },
            //
            sameResourcePageData : {},
            sameResourceVisible : false,
            // 正常工作轨迹ID
            workTrailNormalId:'',
            activeName: 'record1',
          //市场部服务记录实体（详情）
            salerRecordEntity: {customerId:'',customerName:'',customerMobile:'',serverRecord:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',remark:'',createTime:''},
          //市场部通话30秒以上服务记录实体（详情）
            salerRecordEntity2: {customerId:'',customerName:'',customerMobile:'',serverRecord:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',remark:'',createTime:''},
            //通话记录实体
            salerRecordEntity1: {id:'',customerId:'',customerName:'',customerMobile:'',serverRecord:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',},
          //父页面方法的引用
            topdial : top.window.dial,
            topdrop : top.window.drop,
            showSaveButton : true,
            callDisable: false,
            isFirstSaveRecord : false,
            //
            flowType2 : true,
            flowType7 : true,
            flowType3 : true,
            flowType5 : true,
            flowType4 : true,
            remainExtractNum : '',
            
          //钉钉闹钟框框
            alarmList : {},
            alarmVisible : false,
            alarmEntity : {id:'',resourceId:'',summary:'',content:'',alarmTime:''},
            pickerOptions1: {
                shortcuts: [{
                  text: '今天',
                  onClick(picker) {
                    picker.$emit('pick', new Date());
                  }
                }, {
                  text: '明天',
                  onClick(picker) {
                    const date = new Date();
                    date.setTime(date.getTime() + 3600 * 1000 * 24);
                    picker.$emit('pick', date);
                  }
                }, {
                  text: '后天',
                  onClick(picker) {
                    const date = new Date();
                    date.setTime(date.getTime() + 3600 * 1000 * 24 * 2);
                    picker.$emit('pick', date);
                  }
                }]
              },
              
              //是否经理以上（或管理员）
              isManager : '',
              //是否总经理（或管理员）
              isMinister : '',
              salerMap : [],
              loading: false,
              
              customerDetailTitle : "资源详情（请合理移动资源，认真备注）",
              
              blacklistEntity: {id:'',flowId:'402',oldFlowId:'',blackRemark:''},
              blacklistFormVisible: false,
              addblackParams : {id:'',name:'',mobile:''},
              //所有资源ID集合
              pageDataIdList : [],
              //当前资源序号
              resourceIndex : "",
              //今日拨打资源数
              todayCallNum : "",
              //当前详情页查询未结束
              querySuccess: false,
              
        },
        created: function () {
            var self = this;
        	var user = JSON.parse(sessionStorage.getItem("user"));
        	var userGroup = 0;
        	if (user && user != undefined && user != "") {
        		userGroup = user.userGroup;
			}
        	if (userGroup != null && (userGroup == 88002002 ||　userGroup == 88002003 ||　userGroup == 88002005 ||　userGroup == 10000000)) {
        		self.isManager = true;
        	}
        	if (userGroup != null && (userGroup == 88002005 ||　userGroup == 10000000)) {
        		self.isMinister = true;
        	}
            self.readList();
        },
        methods: {
            //查询
            readList: function () {
            	var flow = '';
                if (document.getElementById('flow')) {
                	flow = document.getElementById('flow').value;
                }
            	var params = {};
            	params = {
            			asc: false,
            			isAdd:"1",
            			orderBy: "create_time",
            			pageNum: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalCurrentPage : 1),
            			pageSize: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalPageSize : 10)
            	};
            	params['flowId'] = flow;
                //将查询条件合并到请求参数中
                for (var item in this.searchParams) {
                	if (typeof this.searchParams[item] == 'boolean'){
                        params[item] = this.searchParams[item];
                    }else if (this.searchParams[item] != "") {
                        params[item] = this.searchParams[item];
                    }
                }
                this.$http.put(this.sourceUrl+'/read/list', params)
                        .then(function (response) {
                            this.pageData = response.data.data.resourcePage;
                            //把ID集数据存入List，实现上一个、下一个资源切换
                            this.createPageDataList(response.data.data.idList);
                            //今日拨打资源数
                            this.todayCallNum = "今日拨打资源：" + response.data.data.todayCallNum + " 条";
                        });
                
            },
            //弹出新增窗口
            showAddDialog: function () {
                this.addFormVisible = true;
                for (var item in this.entity) {
                    this.entity[item] = "";
                }
            },
            //新增保存
            addSave: function () {
                var self = this;
                self.entity.demand = self.demandSplit();
                self.$refs["addForm"].validate(function(valid){
                    if (valid) {//校验通过
                        self.$http.post(self.sourceUrl, self.entity)
                        .then(function (response) {
                        	var result = response.data.data;
                        	if (result) {
                        		self.entity = result;//所有保存，添加此行，以避免重复提交
                        		self.addFormVisible = false;
                        		self.readList();
							}
                        });
                    } else {
                        self.$message({
                            message: '请按要求输入内容',
                            type: 'warning'
                        });
                    }
                });
                
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
                     orderBy: "create_time",
                     pageNum: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalCurrentPage : 1),
                     pageSize: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalPageSize : 10)
                 };
                 this.$http.put('sys/dept/read/list', {"keyword" : '市场',"enable":"1","pageSize":"100"})
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
            //发送分配请求
            allot : function(){
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
            		self.$alert('请选择要资源接收人');
            	}else{
            		self.$confirm('您确定要调配给['+self.allotRow.name+']吗?', '提示', {
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
                    }).catch(function() {
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
                    if (menus[j].parentId == "2") {
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
////////////弹出申请签单窗口
            showApplyDialog: function (id,name,fuzzyMobile) {
            	var self = this;
            	if (fuzzyMobile && typeof(fuzzyMobile)!="undefined" && fuzzyMobile != 0 && fuzzyMobile != "") {
            		self.applyEntity = {customerId:'',customerName:'',fuzzyMobile:'',insuranceId:'',amount:'',type:'1',submitCode:'',startDate:'',endDate:''};
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
            dayFormat : function (val) {
            	this.searchParams.createtime = val;
            },
            tableRowClassName : function (row, index) {
            	if (null == row.lastCallTime || row.lastCallTime == undefined || row.lastCallTime == '') {
            		return 'info-row';
				} else {
					var date = new Date();
					//var nowDate = date.toLocaleDateString().substring(0,10).replace(/\//g, "-");
					var year = date.getFullYear();
					var month = date.getMonth()+1;
					if (month < 10) {
						month = "0" + month;
					}
					var day = date.getDate();
					if (day < 10) {
						day = "0" + day;
					}
					var nowDate = year+"-"+month+"-"+day;
					
					var lastCallDate = row.lastCallTime.toString().substring(0,10);
					if (nowDate == lastCallDate) {
						return '';
					}
                }
                return 'info-row';
            },
            //提示语工具
            notify : function (title,text) {
            	this.$notify.error({
            		title: title,
            		message: text
            	});
            },
          //详情
            readDetail: function (row, event) {
            	var self = this;
                this.$http.put(self.sourceUrl+'/read/details', {id: row.id})
                        .then(function (response) {
                        	self.entity = response.data.data.resource;
                        	self.entity.lastCallTime = '';
                        	self.salerRecordEntity = response.data.data.salerRecord;
                        	self.salerRecordEntity2 = response.data.data.salerRecord1;
                        	self.flowTypeSwitch(self.entity.flowId);
                        	if (null != self.pageDataIdList && self.pageDataIdList != undefined && self.pageDataIdList != '' && self.pageDataIdList.length > 0) {
            					for (var int = 0; int < self.pageDataIdList.length; int++) {
            						if (self.entity.id == self.pageDataIdList[int]) {
            							self.resourceIndex = int + 1;
            							break;
            						}
            					}
                        	}
                        	self.customerDetailTitle = "第 " + self.resourceIndex + " 条资源详情（请合理移动资源，认真备注）客户ID：" + self.entity.id;
                        	self.workTrailNormalId = '';
                        	self.detailFormVisible = true;
                        });
            },
            flowTypeSwitch : function (flowId) {
            	var self = this;
            	if (flowId == "101") {
            		self.flowType2 = true;
            		self.flowType7 = true;
            		self.flowType3 = true;
            		self.flowType4 = true;
            		self.flowType5 = true;
				} else if (flowId == "201") {
            		self.flowType2 = false;
            		self.flowType7 = true;
            		self.flowType3 = true;
            		self.flowType4 = true;
            		self.flowType5 = true;
				} else if (flowId == "301") {
					self.flowType3 = false;
					self.flowType7 = true;
					self.flowType2 = true;
            		self.flowType4 = true;
            		self.flowType5 = true;
				} else if (flowId == "401") {
					self.flowType4 = false;
					self.flowType7 = true;
					self.flowType2 = true;
            		self.flowType3 = true;
            		self.flowType5 = true;
				} else if (flowId == "501") {
					self.flowType5 = false;
					self.flowType7 = true;
					self.flowType2 = true;
            		self.flowType3 = true;
            		self.flowType4 = true;
				} else if (flowId == "601") {
					self.flowType5 = true;
					self.flowType7 = true;
					self.flowType2 = true;
            		self.flowType3 = true;
            		self.flowType4 = true;
				} else if (flowId == "701") {
					self.flowType7 = false;
					self.flowType2 = true;
            		self.flowType3 = true;
            		self.flowType4 = true;
            		self.flowType5 = true;
				}
            },
          //修改保存
            updateSave : function () {
            	var self = this;
            	var validNumber = true;
            	//保存
            	self.updateSaveResource(validNumber);
            },
            //更新保存
            updateSaveResource : function (validNumber) {
            	var self = this;
            	if (validNumber) {
            		self.$refs["detailForm"].validate(function(valid){
                		if (valid) {//校验通过
                			self.$http.post(self.sourceUrl, self.entity)
                			.then(function (response) {
                				var result = response.data.data;
                				self.entity = result;//所有保存，添加此行，以避免重复提交
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
            	self.workTrailNormalId = -1;
            	//锁定拨打按钮
		    	self.callDisable = true;
		    	self.salerRecordEntity1 = {id:'',customerId:'',customerName:'',customerMobile:'',serverRecord:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:''};
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
				if (self.salerRecordEntity1.serverRecord && typeof(self.salerRecordEntity1.serverRecord) != "undefined" && self.salerRecordEntity1.serverRecord != '') {//服务内容
					self.$http.post("crm/salerRecord", self.salerRecordEntity1)
					.then(function (response) {
						self.showSaveButton = true;
						self.flushSalerRecord();
						self.workTrailNormalId = response.data.data.workTrailNormalId;
						//激活拨打按钮
						self.callDisable = false;
					});
				} else {
					self.$message({
						message: '请认真填写每一次通话内容',
						type: 'warning'
					});
					return false;
				}
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
            		self.readList();
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
            //刷新通话记录
            flushSalerRecord1: function () {
            	var self = this;
                this.$http.put('crm/salerRecord/read/list/customer/real', {customerId: self.entity.id})
                .then(function (response) {
                	self.salerRecordEntity2 = response.data.data;
                });
            },
            //移动流程
            moveResource : function (entityId,flowId) {
            	var self = this;
            	
            	if (self.workTrailNormalId == -1) {
            		self.$message({
						message: '请先保存通话记录',
						type: 'warning'
					});
            		return false;
            	}
            	
            	if (flowId == 901) {
            		self.$confirm('您确定要丢弃这个资源到共享池吗?（限空号、停机、确定不可聊客户）', '提示', {
            			confirmButtonText: '确定',
            	        cancelButtonText: '取消',
            	        type: 'warning'
                    }).then(function() {
                    	self.entity = {};
                    	self.entity.id = entityId;
                    	self.entity.flowId = flowId;
                    	// 设置正常工作ID
                    	self.entity.workTrailNormalId = self.workTrailNormalId;
                    	self.$http.post(self.sourceUrl + '/moveResource', self.entity)
                    	.then(function (response) {
                    		self.nextResource(entityId);
                    		//刷新列表（id集合）
        					for (var int = 0; int < self.pageDataIdList.length; int++) {
        						if (entityId == self.pageDataIdList[int]) {
        							console.log("移除："+int);
        							self.pageDataIdList.splice(int,1);
        							break;
        						}
        					}
        					//如果没有资源了，关闭详情页
        					if (null != self.pageDataIdList && self.pageDataIdList != undefined && self.pageDataIdList != '' && self.pageDataIdList.length > 0) {
							} else {
        						self.detailFormVisible = false;
        						self.readList();
        					}
                    	});
                    }).catch(function() {
                    	self.$message({
                            type: 'info',
                            message: '取消丢弃'
                          });
                    });
				} else {
					self.entity = {};
                	self.entity.id = entityId;
                	self.entity.flowId = flowId;
                	// 设置正常工作ID
                	self.entity.workTrailNormalId = self.workTrailNormalId;
                	self.$http.post(self.sourceUrl + '/moveResource', self.entity)
                	.then(function (response) {
                		var result = response.data.data;
        				self.entity = result;//所有保存，添加此行，以避免重复提交
        				self.flowTypeSwitch(self.entity.flowId);
                	});
				}
            },
            //抽取资源
            extractResource : function () {
            	var self = this;
            	var user = JSON.parse(sessionStorage.getItem("user"));
            	var userGroup = 0;
            	if (user && user != undefined && user != "") {
            		userGroup = user.userGroup;
				}
            	if (userGroup != null && userGroup <= 88002002 &&　userGroup >= 88002001) {
            		self.$confirm('确定要抽取10条共享池资源吗?', '提示', {
    					confirmButtonText: '确定',
    					cancelButtonText: '取消',
    					type: 'warning'
    				}).then(function () {
    					console.log(self.pageData.total);
    					if (self.pageData.total >= 50) {
    						self.$alert("你的抽取资源数大于50个,请先消耗后再抽取");
    					} else {
    						self.$http.post('crm/resourceLeave/extractResource')
    						.then(function (response) {
    							var result = response.data.data;
    							self.$alert("成功抽取资源："+result+"条");
    							self.readList();
    						});
    					}
    				}).catch(function(){
    					self.$message({
    						type: 'info',
    						message: '取消抽取'
    					});
    					
    				});
				} else {
					self.$message({
						message: '只有业务员和经理才能抽取资源',
						type: 'warning'
					});
				}
            	
            },
            //电话接通后，自动保存一次通话
            firstSaveRecord : function () {
            	var self = this;
            	var callFile = document.getElementById("callFile").value;
            	self.salerRecordEntity1.callFile = callFile;
            	console.log("录音文件："+self.salerRecordEntity1.callFile);
            	self.$http.put("crm/salerRecord/automation", self.salerRecordEntity1)
            	.then(function (response) {
            		var result = response.data.data;
            		self.salerRecordEntity1 = result;
            		//刷新通话记录
            		self.flushSalerRecord();
            	});
            	self.$notify({
            		title: "电话接通",
            		position: 'bottom-right',
            		duration : 3000,
            		message: "保存录音成功",
            		type: 'success'
            	});
            },
            demandFormat: function(v,p) {
    			if(v == null || v == '' || typeof(v) == "undefined" || v <= 0) {
    				return p;
    			} else {
    				var val = "";
    				try {
    					if (v.indexOf("key") >= 0 && v.indexOf("value") >= 0) {
    						var obj = eval("(" + v + ")");
    						for(var i in obj) {
    							val += obj[i].key + "：" + obj[i].value + "；";
    						}
    						if (p == null || p == '' || typeof(p) == "undefined" || p <= 0) {
    							return val;
    						} else {
    							return p + "，" +val;
    						}
    					}
    				} catch (e) {
    					console.log(v+"解析json异常");
    				}
    				return v;
    			}
    		},
			stuffFormat : function (v,t) {
				if (v == null || v == '' || typeof(v) == "undefined" || v <= 0) {
					return t + "：无";
				} else {
					if (t == "客户需求") {
						var demand = this.demandFormat(v);
						if (null != demand && demand != '') {
							return t + "：" + demand;
						}
					} else {
						return t + "：" + v;
					}
				}
			},
			//钉钉闹钟列表
			alarm : function () {
				var self = this;
				self.alarmEntity = {id:'',resourceId:'',summary:'',content:'',alarmTime:''};
				self.$http.put("crm/alarm/read/queryList", {"resourceId":self.entity.id})
                .then(function (response) {
                	self.alarmList = response.data.data;
                	self.alarmVisible = true;
                });
			},
			//钉钉闹钟保存
			alarmSave : function () {
				var self = this;
				self.alarmEntity.resourceId = self.entity.id;
				self.alarmEntity.summary = "["+self.entity.name+"："+self.entity.fuzzyMobile+"]";
				if (self.alarmEntity.alarmTime && self.alarmEntity.content && self.alarmEntity.content != '') {
					self.$http.post("crm/alarm", self.alarmEntity)
					.then(function (response) {
						self.alarmVisible = false;
					});
				} else {
					self.$message({
						message: '请认真填写提醒时间和提醒内容',
						type: 'warning'
					});
					return false;
				}
			},
			//钉钉闹钟删除
			alarmDel : function (alarmEntityId) {
				var self = this;
				self.$http.delete("crm/alarm", {body: {"ids":alarmEntityId}})
                .then(function (response) {
    				self.alarm();
                });
			},
			//业务员远程搜索
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
            //语音识别
            salerRecordText : function (entityId) {
            	window.open("/business/call/salerRecordTranslated.html?customerId=" + entityId);
            },
            //添加到黑名单
            moveBlacklist : function (entityId) {
            	var self = this;
            	self.blacklistEntity = {id:'',flowId:'402',oldFlowId:'',blackRemark:''};
            	self.blacklistEntity.id = entityId;
            	self.blacklistEntity.flowId = "402";
            	self.blacklistEntity.oldFlowId = "";
            	self.blacklistEntity.blackRemark = "";
            	self.blacklistFormVisible = true;
            },
            //保存黑名单
            blacklistSave : function () {
            	var self = this;
            	if (self.blacklistEntity.blackRemark) {
            		self.$http.post(self.sourceUrl+'/moveBlacklist', {"id": self.blacklistEntity.id,"flowId": "402","oldFlowId":self.blacklistEntity.oldFlowId,"blackRemark": self.blacklistEntity.blackRemark})
            		.then(function (response) {
            			var result = response.data.data;
            			//关闭弹窗
            			self.blacklistFormVisible = false;
            			//刷新页面
            			self.nextResource(self.blacklistEntity.id);
            			self.readList();
            		});
				} else {
					self.$notify({
	            		title: "提示",
	            		position: 'bottom-right',
	            		duration : 3000,
	            		message: "请填写黑名单说明",
	            		type: 'warning'
	            	});
				}
            },
            //把ID集数据存入List，实现上一个、下一个资源切换
            createPageDataList : function (pageData) {
            	console.log("所有资源数：" + pageData.length);
            	var self = this;
            	self.pageDataIdList = pageData;
            	/*if (null != pageData && pageData != undefined && pageData != '') {
            		self.pageDataIdList = pageData.map(item => {
            			return {resourceId:item.id};
            		});
				}*/
            },
            //上一个资源
            previousResource : function (entityId) {
            	var self = this;
            	self.querySuccess = true;
            	if (!self.showSaveButton) {
            		self.$message.error('请先保存通话记录');
            		return;
            	}
            	if (null != self.pageDataIdList && self.pageDataIdList != undefined && self.pageDataIdList != '' && self.pageDataIdList.length > 0) {
            		var previousIndex = -1;
					for (var int = 0; int < self.pageDataIdList.length; int++) {
						if (entityId == self.pageDataIdList[int]) {
							previousIndex = int;
							self.resourceIndex = int;
							break;
						}
					}
					if (previousIndex != -1) {
						previousIndex = previousIndex - 1;
					}
					if (previousIndex >= 0) {
						self.changeDetail(self.pageDataIdList[previousIndex]);
					} else {
						self.$notify({
					          title: '警告',
					          message: '已到顶部，无上一条资源',
					          type: 'warning',
					          duration: 2000
					        });
						self.querySuccess = false;
					}
				}
            },
            //下一个资源
            nextResource : function (entityId) {
            	var self = this;
            	self.querySuccess = true;
            	if (!self.showSaveButton) {
            		self.$message.error('请先保存通话记录');
            		return;
            	}
            	if (null != self.pageDataIdList && self.pageDataIdList != undefined && self.pageDataIdList != '' && self.pageDataIdList.length > 0) {
            		var nextIndex = -1;
					for (var int = 0; int < self.pageDataIdList.length; int++) {
						if (entityId == self.pageDataIdList[int]) {
							nextIndex = int;
							self.resourceIndex = int + 2;
							break;
						}
					}
					if (nextIndex != -1) {
						nextIndex = nextIndex + 1;
					}
					if (nextIndex < self.pageDataIdList.length) {
						self.changeDetail(self.pageDataIdList[nextIndex]);
					} else {
						self.$notify({
					          title: '警告',
					          message: '已到底部，无下一条资源',
					          type: 'warning',
					          duration: 2000
					        });
						self.querySuccess = false;
					}
				}
            },
            //上一条、下一条资源的详情
            changeDetail: function (entityId) {
            	var self = this;
                this.$http.put(self.sourceUrl+'/read/details', {id: entityId})
                        .then(function (response) {
                        	self.entity = response.data.data.resource;
                        	self.entity.lastCallTime = '';
                        	self.salerRecordEntity = response.data.data.salerRecord;
                        	self.salerRecordEntity2 = response.data.data.salerRecord1;
                        	console.log(self.entity.flowId);
                        	self.flowTypeSwitch(self.entity.flowId);
                        	self.customerDetailTitle = "第 " + self.resourceIndex + " 条资源详情（请合理移动资源，认真备注）客户ID：" + self.entity.id;
                        	self.workTrailNormalId = '';
                        	self.querySuccess = false;
                        });
            },
            startDateFormat : function (val) {
            	this.applyEntity.startDate = val;
            },
            endDateFormat : function (val) {
            	this.applyEntity.endDate = val;
            },
            //客户需求拼接
            demandSplit : function () {
            	var self = this;
            	if (self.entity.demand && self.entity.demand != '') {
              		var d = "[";
      				d += '{"key":"录入需求","value":"'+ self.entity.demand +'"},';
      				return d + "]";
      			} else {
      				return "";
      			}
            },
            
            //清空并刷新数据
            flushAndReadList : function () {
            	 var self = this;
            	 self.searchParams = {fromInfo:'',name:'',mobile:'',createtime:'',wechatNo:'',subordinate:'',salerId:'',startTime:'',endTime:''};
         		 self.readList();
            },
        }
    });