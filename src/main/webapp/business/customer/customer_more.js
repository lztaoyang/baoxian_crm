	/**
	*管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"crm/customer",
            //是否显示详情窗口
            detailFormVisible: false,
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
            //查询参数
            searchParams: {name:'',flowId:'',mobile:'',fuzzyMobile:'',md5Mobile:'',policyholderId:'',duty:'',birthday:'',province:'',city:'',insureNum:'',insureMoney:'',introduceNum:'',labels:'',currentApplyId:'',isHg:'',isService:'',salerId:'',managerId:'',directorId:'',ministerId:'',salerName:'',managerName:'',directorName:'',contracterId:'',contracterName:'',ministerName:'',serverId:'',serverName:'',oldApplyId:'',oldIsHg:'',oldIsService:'',oldServerId:'',oldServerName:'',contracterRemark:'',subordinate:'',enterTime:'',fromInfo:'',startTime:'',endTime:'',isNewClub:''},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            //当前操作的实体（编辑/新增）
            entity: {name:'',flowId:'',fuzzyMobile:'',md5Mobile:'',policyholderId:'',duty:'',birthday:'',province:'',city:'',insureNum:'',insureMoney:'',introduceNum:'',labels:'',currentApplyId:'',isHg:'',isService:'',salerId:'',managerId:'',directorId:'',ministerId:'',salerName:'',managerName:'',directorName:'',contracterId:'',contracterName:'',ministerName:'',serverId:'',serverName:'',oldApplyId:'',oldIsHg:'',oldIsService:'',oldServerId:'',oldServerName:'',contracterRemark:'',remark:'',createTime:''},
            //editEntity: {id:'',remark:''},
            //签单实体（详情）
            applyEntity: {customerId:'',customerName:'',fuzzyMobile:'',insuranceId:'',amount:'',type:'2',submitCode:'',submitCodeOld:'',startDate:'',endDate:''},
            //客服服务记录实体（详情）
            serverRecordEntity: {customerId:'',customerName:'',customerMobile:'',serveResult:'',serverRecord:'',serveTime:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',callTime:'',remark:'',createTime:''},
            //投诉实体（详情）
            complainEntity: {customerId:'',customerName:'',type:'',hannel:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',remark:'',createTime:''},
            //退款实体（详情）
            refundEntity: {customerId:'',customerName:'',refundMoney:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',remark:'',createTime:''},
            //理赔实体（详情）
            compensationEntity: {customerId:'',customerName:'',refundMoney:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',remark:'',createTime:''},
            //市场部服务记录实体（详情）
            salerRecordEntity: {customerId:'',customerName:'',customerMobile:'',serveResult:'',serverRecord:'',serveTime:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',callTime:'',remark:'',createTime:''},
            //市场部服务记录实体（详情）
            salerRecordEntity2: {customerId:'',customerName:'',customerMobile:'',serveResult:'',serverRecord:'',serveTime:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',callTime:'',remark:'',createTime:''},
            //表格中选中的行的集合
            selectDatas: [],
            //表单校验规则 api:https://github.com/yiminghe/async-validator
            rules: {
	    		name: [
	    		Vali.utf8mb4Len(false,50)
	    	],
            },
////////////会员资源URL
            allotUrl:"crm/customer/allotCustomer",
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
            signVisible: false,
            applyVisible: false,
////////////申请签单的实体（编辑/新增）
            
//////////////申请签单表单校验规则 
            applyRules: {
            	customerName: [
            	         {required: true, message: '客户姓名不可为空', trigger: 'blur'},
            	],
            	amount: [
                  	         {required: true, message: '签单金额不可为空', trigger: 'blur'},
                  	],
            	fuzzyMobile: [
            	         {required: true, message: '电话号码不可为空', trigger: 'blur'},
            	         Vali.utf8mb4Len(false,11)
            	],
            	insuranceId: [
            	              {required: true, message: '产品不可为空', trigger: 'change'},
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
          //父页面方法的引用
            topdial : top.window.dial,
            topdrop : top.window.drop,
            showSaveButton : true,
            callDisable: false,
            isFirstSaveRecord : false,
            
          //是否经理以上（或管理员）
            isManager : '',
        },
        created: function () {
            var self = this;
            self.readList();
        },
        methods: {
            //查询
            readList: function () {
                var server = '';
                if (document.getElementById('server')) {
                	server = document.getElementById('server').value;
                }
                if (server == '3') {
                	var params = {
                    		asc: true,
                    		orderBy: "end_date",
                    		pageNum: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalCurrentPage : 1),
                    		pageSize: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalPageSize : 10)
                    };
				} else {
					var params = {
	                		asc: false,
	                		orderBy: "apply_time",
	                		pageNum: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalCurrentPage : 1),
	                		pageSize: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalPageSize : 10)
	                };
				}
                //投顾，产品服务
                var isInterest = '';
                if (document.getElementById('isInterest')) {
                	isInterest = document.getElementById('isInterest').value;
                }
                if (server != '') {
                	params['server'] = server;
				}
                if (isInterest != '') {
                	params['isInterest'] = isInterest;
				}
                
                var flowId = '';
                if (document.getElementById('flowId')) {
                	flowId = document.getElementById('flowId').value;
                }
                if (flowId != '') {
                	params['flowId'] = flowId;
				}
                //将查询条件合并到请求参数中
                for (var item in this.searchParams) {
                	if (typeof this.searchParams[item] == 'boolean'){
                        params[item] = this.searchParams[item];
                    }else if (this.searchParams[item] != "") {
                        params[item] = this.searchParams[item];
                    }
                }
                this.$http.put(this.sourceUrl+'/customerList/list', params)
                        .then(function (response) {
                            this.pageData = response.data;
                        });
                var self = this;
            	var user = JSON.parse(sessionStorage.getItem("user"));
            	var userGroup = 0;
            	if (user && user != undefined && user != "") {
            		userGroup = user.userGroup;
				}
            	if (userGroup != null && (userGroup == 88002002 ||　userGroup == 88002003 ||　userGroup == 88002005 ||　userGroup == 10000000)) {
            		self.isManager = true;
            	}
            },
            //详情
            readDetail: function (row, event) {
                this.$http.put(this.sourceUrl+'/read/details', {id: row.customer.id})
                        .then(function (response) {
                            this.entity = response.data.data.customer;
                            //this.editEntity.id = response.data.data.customer.id;
                            //this.editEntity.remark = response.data.data.customer.remark;
                            this.applyEntity = response.data.data.apply;
                            this.serverRecordEntity = response.data.data.serverRecord;
                            this.complainEntity = response.data.data.complain;
                            this.refundEntity = response.data.data.refund;
                            this.compensationEntity = response.data.data.compensation;
                            this.salerRecordEntity = response.data.data.salerRecord;
                            this.salerRecordEntity2 = response.data.data.salerRecord1;
                            this.detailFormVisible = true;
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
                     orderBy: "update_time",
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
                    if (menus[j].parentId == "2") {
                        forFn_convertMenus2TreeItems(menus, menus[j], lev);
                        //return depts[j];
                        temp.push(menus[j])
                    }
                }
                console.log(temp)
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
            //提示语工具
            notify : function (title,text) {
            	this.$notify.error({
            		title: title,
            		message: text
            	});
            },
          //修改保存
            updateSave : function () {
            	var self = this;
            	var validNumber = true;
            	/*if (self.entity.age == "" || self.entity.age > 200) {
            		validNumber = false;
            		self.$alert("年龄不能为空或者大于200岁");
            	}
            	if (self.entity.birthday == undefined || self.entity.birthday == "") {
            		validNumber = false;
            		self.$alert("请填写出生日期");
            	} else {
            		console.log(self.entity.birthday.toString().length);
            		if (self.entity.birthday.toString().length < 10) {
            			self.$alert("出生日期格式不正确");
					}
            	}*/
            	//保存
            	self.updateSaveResource(validNumber);
            },
            //更新保存
            updateSaveResource : function (validNumber) {
            	var self = this;
            	if (validNumber) {
            		self.entity.submitCode = '';
            		self.entity.upgradeSubmitCode = '';
            		self.entity.qq = '';
            		self.entity.phone = '';
            		self.entity.wechatNo = '';
            		self.entity.fuzzyMobile = '';
            		self.$refs["detailForm"].validate(function(valid){
                		if (valid) {//校验通过
                			self.$http.post(self.sourceUrl, self.entity)
                			.then(function (response) {
                				var result = response.data.data;
                				self.entity = result;//所有保存，添加此行，以避免重复提交
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
////////////弹出申请签单窗口
            showApplyDialog: function (id,name,fuzzyMobile) {
            	var self = this;
            	if (fuzzyMobile && typeof(fuzzyMobile)!="undefined" && fuzzyMobile != 0 && fuzzyMobile != "") {
            		self.applyEntity = {customerId:'',customerName:'',fuzzyMobile:'',insuranceId:'',amount:'',type:'2',submitCode:'',startDate:'',endDate:''};
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
            //刷新通话记录
            flushSalerRecord1: function () {
            	var self = this;
                this.$http.put('crm/salerRecord/read/list/customer/real', {customerId: self.entity.id})
                .then(function (response) {
                	self.salerRecordEntity2 = response.data.data;
                });
            },
            //语音识别
            salerRecordText : function (entityId) {
            	window.open("/business/call/salerRecordTranslated.html?customerId=" + entityId);
            },
            signRowClassName : function (row, index) {
            	/*if (null == row.isRefund || row.isRefund == undefined || row.isRefund == '') {
            		return 'refund-wait-row';
            	}*/
            	if ("1" == row.isRefund) {
                	return 'refund-row';
                }
                return '';
            },
            startDateFormat : function (val) {
            	this.applyEntity.startDate = val;
            },
            endDateFormat : function (val) {
            	this.applyEntity.endDate = val;
            },
            tableRowClassName : function (row, index) {
            	if (null == row.customer.endDate || row.customer.endDate == undefined || row.customer.endDate == '') {
            		return 'info-row';
				} else {
					var date = new Date();
					var startTime = new Date(Date.parse(row.customer.endDate.replace(/-/g,"/"))).getTime();
					var endTime = new Date(Date.parse(date.toString().replace(/-/g,"/"))).getTime();
					var dates = (startTime - endTime)/(1000*60*60*24);
					var expireDay = Math.round(dates);
					if (expireDay == null || expireDay == '' || typeof(expireDay) == "undefined" || expireDay <= 0) {
						return 'info-row';
					} else {
						if (expireDay > 3) {
							return '';
						}
					}
                }
                return 'info-row';
            },
            
            //清空并刷新数据
            flushAndReadList : function () {
            	 var self = this;
            	 self.searchParams = {fromInfo:'',name:'',mobile:'',createtime:'',wechatNo:'',subordinate:'',salerId:'',startTime:'',endTime:''};
         		 self.readList();
            },
            //判断审计/签单
            isUpgradeCustomer : function (type) {
            	if (null != type && type == 1) {
					return false;
				} else {
					return true;
				}
            },
        }
    });