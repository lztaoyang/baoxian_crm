	/**
	*管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	signApplyUrl:"crm/signApply",
        	sourceUrl:"crm/customer",
        	stopUrl : "crm/customer/stop",
        	refundUrl:"crm/customerRefund",
        	recordUrl:"crm/serverRecord",
        	userUrl:"sys/user",
            refundVisible : false,
            recordVisible : false,
            policyVisible : false,
            stopServerVisible : false,
            suspendServerVisible : false ,
            againServerVisible : false ,
            extendedServerVisible : false ,
            extendedVisible : false ,
            customerCotVisible : false ,
            teacherCotVisible : false ,
            teacherCotAddVisible : false ,
            stockFailureAddVisible :false,
            teacherCotOutVisible : false ,//批量平仓
            teacherHalfCotOutVisible : false ,//批量平半仓
            stockFailureVisible : false ,
            stockCusFailureVisible : false ,
            stockOutFailureVisible : false ,
            fullscreenLoading: false,
			//资源分配时部门用户分页数据,其中包含分页信息与数据列表
            deptPageData: {},
            //客服部树
            userTreeData: [],
            // 客服人员
            users:[],
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
        	//分配客服
        	allotUrl : "crm/customer/allotServer",
			allotVisible : false,
            //分配客服部选中客服
            allotRow : {},
            //是否显示详情窗口
            detailFormVisible: false,
            //查询参数
            searchParams: {stockCode:'',stockName:'',stockNum:'',teacherStockNum:'',names:'', name:'',flowId:'',mobile:'',fuzzyMobile:'',md5Mobile:'',policyholderId:'',duty:'',birthday:'',province:'',city:'',insureNum:'',insureMoney:'',introduceNum:'',labels:'',currentApplyId:'',isHg:'',isService:'',salerId:'',managerId:'',directorId:'',ministerId:'',salerName:'',managerName:'',directorName:'',contracterId:'',contracterName:'',ministerName:'',serverId:'',serverName:'',oldApplyId:'',oldIsHg:'',oldIsService:'',oldServerId:'',oldServerName:'',contracterRemark:'',subordinate:'',enterTime:'',fromInfo:'',startTime:'',endTime:'',isNewClub:'',isInterest:'',insuranceId:'',isImportant:'',},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            //当前操作的实体（编辑/新增）								 
            entity: {id:'',useMoney:'' ,lossMoney:'' ,totalMoney:'',useTeacherMoney:'' ,lossTeacherMoney:'' ,totalTeacherMoney:'', name:'',fromInfo:'',enterTime:'',flowId:'',policyholderId:'',wechatNo:'',qq:'',mobile:'',phone:'',duty:'',birthday:'',province:'',city:'',insureNum:'',insureMoney:'',introduceNum:'',labels:'',currentApplyId:'',isHg:'',isService:'',salerId:'',managerId:'',directorId:'',ministerId:'',salerName:'',managerName:'',directorName:'',contracterId:'',contracterName:'',ministerName:'',serverId:'',serverName:'',oldApplyId:'',oldIsHg:'',oldIsService:'',oldServerId:'',oldServerName:'',contracterRemark:'',remark:'',createTime:'',isImportant:'',},
            //签单实体（详情）
            applyEntity: {customerId:'',customerName:'',policyNo:'',belong:'',insuranceId:'',insuranceName:'',introducerId:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',remark:'',createTime:''},
            //市场部服务记录实体（详情）
            serverRecordMobileEntity: {customerId:'',customerName:'',customerMobile:'',serveResult:'',serverRecord:'',serveTime:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',callTime:'',remark:'',createTime:''},
            //客服服务记录实体（详情）
            serverRecordEntity: {customerId:'',customerName:'',customerMobile:'',serveResult:'',serverRecord:'',serveTime:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',callTime:'',remark:'',createTime:''},
            //服务记录实体（详情）
            recordEntity1: {ids:'',names:'',customerId:'',customerName:'',customerMobile:'',serveResult:'',serverRecord:'',serveTime:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',callTime:'',remark:'',createTime:''},
            //退款实体（详情）
            refundEntity1: {customerId:'',signApplyId:'',customerName:'',refundMoney:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',remark:'',createTime:'',user:''},
            //
            signApplys:[],
            //表格中选中的行的集合
            selectDatas: [],
            //单击行记录ID，NAME
            single: {},
            //表单校验规则 api:https://github.com/yiminghe/async-validator
            rules: {
	    		name: [
	    		       Vali.utf8mb4Len(false,50)
	    		],
            },
            rules1: {
            	refundMoney: [
            	     {required: true, message: '此项不可为空', trigger: 'blur'},
            	],
            	signApplyId:[
            	     {required: true, message: '此项不可为空', trigger: 'blur'},
            	],
            	dealerId:[
            		{required: true, message: '此项不可为空', trigger: 'blur'},
            	]
            },
            rules2: {
            	mobile: [
            		Vali.mobile(false)
            	],
            },
            activeName: 'record2',
          //通话记录实体
            serverRecordMobileEntity1: {customerId:'',customerName:'',customerMobile:'',serverRecord:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',},
          //父页面方法的引用
            topdial : top.window.dial,
            topdrop : top.window.drop,
            showSaveButton : true,
            callDisable: false,
           
            //暂停服务
            suspendEntity : {id:'',startDate:'',endDate:'',serverRemark:''},
            //停止服务
            stopEntity : {id:'',startDate:'',endDate:'',serverRemark:''},
            //重新开始服务
            againStartEntity : {id:'',startDate:'',endDate:'',serverRemark:''},
            //延长服务
            extendedEntity : {id:'',startDate:'',endDate:'',serverRemark:''},
            //批量延长服务
            extendedEntity1 : {ids:'',names:'',startDate:'',endDate:'',serverRemark:''},
            
            //实盘
            customerCotUrl:"crm/customerActualCot",
            customerCotRecordUrl:"crm/customerCotOperationLog",
            customerEditFormVisible: false,
            customerCotRecordVisible: false,
            
            //虚盘
            teacherCotUrl:"crm/teacherDirectiveCot",
            teacherCotRecordUrl:"crm/teacherDirectiveOperationLog",
            teacherEditFormVisible: false,
            teacherCotRecordVisible: false,
            
            //实盘操作的实体（编辑/新增）
            customerCotEntity: {useMoney:'', directionType:'', customerAdviserRecordId:'',directiveId:'',servicerId:'',servicerName:'',customerId:'',customerName:'',stockCode:'',stockName:'',stockSourceType:'',cotNum:'',cotPrice:'',cotMoney:'',currentPrice:'',currentMoney:'',lossPatio:'',lossMoney:'',position:'',indexValue:'',updateDate:'',createTime:'',},
            //实盘操作记录实体
            customerCotRecordEntity : {customerActualCotId:'',stockCode:'',stockName:'',directionType:'',tradeNum:'',tradePrice:'',tradeMoney:'',cotPrice:'',cotMoney:'',thenLossRatio:'',thenLossMoney:'',updateDate:'',},
            //实盘列表查询参数
            searchParams1: {customerAdviserRecordId:'',directiveId:'',servicerId:'',servicerName:'',customerId:'',customerName:'',stockCode:'',stockName:'',stockSourceType:'',cotNum:'',cotPrice:'',cotMoney:'',currentPrice:'',currentMoney:'',lossPatio:'',lossMoney:'',position:'',indexValue:'',updateDate:'',},
            //实盘操作记录查询参数
            searchParams12: {customerAdviserRecordId:'',directiveId:'',servicerId:'',servicerName:'',customerId:'',customerName:'',stockCode:'',stockName:'',stockSourceType:'',cotNum:'',cotPrice:'',cotMoney:'',currentPrice:'',currentMoney:'',lossPatio:'',lossMoney:'',position:'',indexValue:'',updateDate:'',},
            //虚盘操作的实体（编辑/新增）
            teacherCotEntity: {useMoney:'',directionType:'', customerAdviserRecordId:'',directiveId:'',servicerId:'',servicerName:'',customerId:'',customerName:'',stockCode:'',stockName:'',stockSourceType:'',cotNum:'',cotPrice:'',cotMoney:'',currentPrice:'',currentMoney:'',lossPatio:'',lossMoney:'',position:'',indexValue:'',updateDate:'',createTime:'',},
            //虚盘操作记录实体
            teacherCotRecordEntity : {customerActualCotId:'',stockCode:'',stockName:'',directionType:'',tradeNum:'',tradePrice:'',tradeMoney:'',cotPrice:'',cotMoney:'',thenLossRatio:'',thenLossMoney:'',updateDate:'',},
            //虚盘查询参数
            searchParams2: {customerAdviserRecordId:'',directiveId:'',servicerId:'',servicerName:'',customerId:'',customerName:'',stockCode:'',stockName:'',stockSourceType:'',cotNum:'',cotPrice:'',cotMoney:'',currentPrice:'',currentMoney:'',lossPatio:'',lossMoney:'',position:'',indexValue:'',updateDate:'',},
            //虚盘操作记录查询参数
            searchParams3: {customerAdviserRecordId:'',directiveId:'',servicerId:'',servicerName:'',customerId:'',customerName:'',stockCode:'',stockName:'',stockSourceType:'',cotNum:'',cotPrice:'',cotMoney:'',currentPrice:'',currentMoney:'',lossPatio:'',lossMoney:'',position:'',indexValue:'',updateDate:'',},
            
            //实盘批量建仓
            customerCotEntity1: {ids:'',names:'',useMoneys:'', useMoney:'', directionType:'', customerAdviserRecordId:'',directiveId:'',servicerId:'',servicerName:'',customerId:'',customerName:'',stockCode:'',stockName:'',stockSourceType:'',cotNum:'',cotPrice:'',cotMoney:'',currentPrice:'',currentMoney:'',lossPatio:'',lossMoney:'',position:'',indexValue:'',updateDate:'',createTime:'',},
            //虚盘批量建仓
            teacherCotEntity1: {ids:'',names:'',useMoneys:'', useMoney:'', directionType:'', customerAdviserRecordId:'',directiveId:'',servicerId:'',servicerName:'',customerId:'',customerName:'',stockCode:'',stockName:'',stockSourceType:'',cotNum:'',cotPrice:'',cotMoney:'',currentPrice:'',currentMoney:'',lossPatio:'',lossMoney:'',position:'',indexValue:'',updateDate:'',createTime:'',},
            //虚盘批量平仓
            teacherCotOutEntity: {ids:'',names:'',useMoneys:'', useMoney:'', directionType:'', customerAdviserRecordId:'',directiveId:'',servicerId:'',servicerName:'',customerId:'',customerName:'',stockCode:'',stockName:'',stockSourceType:'',cotNum:'',cotPrice:'',cotMoney:'',currentPrice:'',currentMoney:'',lossPatio:'',lossMoney:'',position:'',indexValue:'',updateDate:'',createTime:'',},
            
            //虚盘批量平半仓
            teacherHalfCotOutEntity: {ids:'',names:'',useMoneys:'', useMoney:'', directionType:'', customerAdviserRecordId:'',directiveId:'',servicerId:'',servicerName:'',customerId:'',customerName:'',stockCode:'',stockName:'',stockSourceType:'',cotNum:'',cotPrice:'',cotMoney:'',currentPrice:'',currentMoney:'',lossPatio:'',lossMoney:'',position:'',indexValue:'',updateDate:'',createTime:'',},
            
            //分页数据,其中包含分页信息与数据列表
            pageData1: {},
            pageData12: {},
            pageData2: {},
            pageData3: {},
            rules3: {
            	stockCode: [
             	    {required: true, message: '股票代码不可为空', trigger: 'blur'},
             	],
             	cotNum:[
             	    {required: true, message: '交易数量不可为空', trigger: 'blur'},
             	    {max: 7, message: '长度在 7 个字符', trigger: 'blur' }
             	],
             	cotPrice:[
             		{required: true, message: '交易价格不可为空', trigger: 'blur'},
             	]
            },
            rules4: {
            	stockCode: [
            	        {required: true, message: '股票代码不可为空', trigger: 'blur'},
	            ],
	            position:[
	                    {required: true, message: '仓位不可为空', trigger: 'blur'},
                ],
                cotPrice:[
                        {required: true, message: '交易价格不可为空', trigger: 'blur'},
              	]
            },
            activeName1: 'customerInfo',
            //详情页客户ID
            useMoney : "",//可用资金
            useTeacherMoney : "",//可用资金
            customerId : "",
            customerName : "",
            customerActualCotId : "",
            teacherDirectiveCotId : "",
            
            stockFailureEntity : {ids:'',names:'',},
            stockCusFailureEntity : {ids:'',names:'',},
            stockOutFailureEntity : {ids:'',names:'',},
            stockHalfOutFailureEntity : {ids:'',names:'',},
            
            //是否管理员
            isAdmin : '',
        },
        created: function () {
            var self = this;
        	var user = JSON.parse(sessionStorage.getItem("user"));
        	var userGroup = 0;
        	if (user && user != undefined && user != "") {
        		userGroup = user.userGroup;
			}
        	if (userGroup != null && userGroup == 10000000) {
        		self.isAdmin = true;
        	}
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
                var flowId = '';
                if (document.getElementById('flowId')) {
                	flowId = document.getElementById('flowId').value;
                }
                var isInterest = '';
                if (document.getElementById('isInterest')) {
                	isInterest = document.getElementById('isInterest').value;
                }
                var isImportant = '';
                if (document.getElementById('isImportant')) {
                	isImportant = document.getElementById('isImportant').value;
                }
                if (isImportant != '') {
                	params['isImportant'] = isImportant;
                }
                if (server != '') {
                	params['server'] = server;
				}
                if (flowId != '') {
                	params['flowId'] = flowId;
				}
                if (isInterest != '') {
                	params['isInterest'] = isInterest;
				}
                //将查询条件合并到请求参数中
                for (var item in this.searchParams) {
                	if (typeof this.searchParams[item] == 'boolean'){
                        params[item] = this.searchParams[item];
                    }else if (this.searchParams[item] != "") {
                        params[item] = this.searchParams[item];
                    }
                }
                this.$http.put(this.sourceUrl+'/serverMore/list', params)
                        .then(function (response) {
                            this.pageData = response.data;
                        });
            },
            
            //详情
            readDetail: function (row, event) {
                this.$http.put(this.sourceUrl+'/read/serverDetails', {id: row.customer.id})
                    .then(function (response) {
                        this.entity = response.data.data.customer;
                        this.customerId = row.customer.id;
                        this.customerName = response.data.data.customer.name;
                        this.useMoney = response.data.data.customer.useMoney;
                        this.useTeacherMoney = response.data.data.customer.useTeacherMoney;
                        this.applyEntity = response.data.data.apply;
                        this.serverRecordEntity = response.data.data.serverRecord;
                        this.serverRecordMobileEntity = response.data.data.serverRecordMobile;
                        this.activeName1 = 'customerInfo',
                        this.detailFormVisible = true;
                        this.readTeacherCotList();
                        this.readCustomerCotList();
                    });
            },
            //重点客户
            showImportantServer :function (id) {
            	var self = this;
            	self.entity.id = id;
            	self.entity.isImportant = 1;
            	self.$confirm('您确定要移动客户['+self.entity.name+']吗？', '提示', {
        			confirmButtonText: '确定',
        			cancelButtonText: '取消',
        			type: 'warning'
        		}).then(function () {
                    self.$http.post(self.sourceUrl, self.entity)
                            .then(function (response) {
                            	self.detailFormVisible = false;
                                self.readList();
                            });
        		}).catch(function(){
        			self.$message({
        				type: 'info',
        				message: '取消移动客户'
        			});
        		});
            },
            //投顾客户
            showNoImportantServer :function (id) {
            	var self = this;
            	self.entity.id = id;
            	self.entity.isImportant = 0;
            	self.$confirm('您确定要移动客户['+self.entity.name+']吗？', '提示', {
            		confirmButtonText: '确定',
            		cancelButtonText: '取消',
            		type: 'warning'
            	}).then(function () {
            		self.$http.post(self.sourceUrl, self.entity)
            		.then(function (response) {
            			self.detailFormVisible = false;
            			self.readList();
            		});
            	}).catch(function(){
            		self.$message({
            			type: 'info',
            			message: '取消移动客户'
            		});
            	});
            },
            //保存
            save: function () {
                var self = this;
                self.entity.submitCode = '';
        		self.entity.upgradeSubmitCode = '';
        		self.entity.qq = '';
        		self.entity.phone = '';
        		self.entity.wechatNo = '';
        		self.entity.mobile = '';
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
                				self.readList();
                				//刷新实虚盘可用资金
                				self.useMoney = self.entity.useMoney;
                				self.useTeacherMoney = self.entity.useTeacherMoney;
                				
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
            
            //表格记录选择事件
            selectionChange: function (selection) {
                var self = this;
                self.selectDatas = selection;
            },
            
            showRefundDialog : function () {
            	var self = this;
            	// 客服人员是否加载，如果未加载则先初始化客服数据
            	if (!self.users || self.users.length == 0) {
            		var params = {
            			locked:'0',	
                        userGroup3:"true",
                        pageNum: 1,
                        pageSize: 500
                    };
            		self.$http.put(self.userUrl+'/read/user', params)
                            .then(function (response) {
                            	self.users = response.data.data;
                            });
            	}
        		if (self.selectDatas.length > 0) {
        			self.refundVisible = true;
        		//	self.refundEntity1 = {};
        			 self.refundEntity1={customerId:'',signApplyId:'',customerName:'',refundMoney:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',remark:'',createTime:''};
        			for(var i in this.selectDatas){
        				self.refundEntity1.customerId = self.selectDatas[i].customer.id;
        				self.refundEntity1.customerName = self.selectDatas[i].customer.name;
        			}
        			 this.$http.put(self.signApplyUrl+'/read/list', {customerId: self.refundEntity1.customerId, signStatus:5 })
                     .then(function (response) {
                     	self.signApplys = response.data.data;
                     });
				} else {
					self.$alert("未选择客户");
				}
        	},
        	
        	//详情页申请退款按钮
        	showRefundOneDialog : function (id ,name) {
        		var self = this;
        		// 客服人员是否加载，如果未加载则先初始化客服数据
        		if (!self.users || self.users.length == 0) {
        			var params = {
        					locked:'0',	
        					userGroup3:"true",
        					pageNum: 1,
        					pageSize: 500
        			};
        			self.$http.put(self.userUrl+'/read/user', params)
        			.then(function (response) {
        				self.users = response.data.data;
        			});
        		}
    			self.refundVisible = true;
    			//	self.refundEntity1 = {};
    			self.refundEntity1={customerId:'',signApplyId:'',customerName:'',refundMoney:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',remark:'',createTime:''};
    			self.refundEntity1.customerId = id;
    			self.refundEntity1.customerName = name;
    			this.$http.put(self.signApplyUrl+'/read/list', {customerId: self.refundEntity1.customerId, signStatus:5 })
    			.then(function (response) {
    				self.signApplys = response.data.data;
    			});
    			
        	},
        	
        	//详情页分配服务
        	showRecordOneDialog : function (id ,name) {
        		var self = this;
    			self.recordEntity1 = {ids:'',names:'',customerId:'',customerName:'',customerMobile:'',serveResult:'',serverRecord:'',serveTime:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',callTime:'',remark:'',createTime:''};
				self.recordEntity1.ids = id;
				self.recordEntity1.names = name;
    			self.recordVisible = true;
        	},
        	
        	showRecordDialog : function () {
    			var ids = new Array();
        		var names = new Array();
                for(var i in this.selectDatas){
                    ids.push(this.selectDatas[i].customer.id);
                    names.push(this.selectDatas[i].customer.name);
                }
            	var params = {};
                if (ids) {
                    if (ids instanceof Array) {
                        params.ids = ids;
                        params.names = names;
                    } else {
                        params.ids = [ids];
                        params.names = [names];
                    }
                }
        		var self = this;
        		if (self.selectDatas.length > 0) {
        			self.recordEntity1 = {ids:'',names:'',customerId:'',customerName:'',customerMobile:'',serveResult:'',serverRecord:'',serveTime:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',callTime:'',remark:'',createTime:''};
        			for(var i in this.selectDatas){
        				self.recordEntity1.ids = params.ids;
        				self.recordEntity1.names = params.names;
        			}
        			self.recordVisible = true;
        		} else {
        			self.$alert("未选择客户");
        		}
        	},
        	//申请退款记录
			refund : function() {
				var self = this;
				self.$refs["refundForm"].validate(function(valid){
					if (valid) {//校验通过
						self.$http.post(self.refundUrl, self.refundEntity1)
						.then(function (response) {
							self.refundVisible = false;
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
			//服务记录
			record : function() {
				var self = this;
				self.$refs["recordForm"].validate(function(valid){
					if (valid) {//校验通过
						self.$http.post(self.recordUrl, self.recordEntity1)
						.then(function (response) {
							self.recordVisible = false;
							//self.readList();
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
                            message: '取消分配'
                          });
                    });
            	}
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
            allotCurrentChange : function (val) {
                this.allotRow.id = val.id;
                this.allotRow.name = val.userName;
            },
            dayFormat1 : function (val) {
            	this.searchParams.startTime = val;
            },
            dayFormat2 : function (val) {
            	this.searchParams.endTime = val;
            },
            dayFormat21 : function (val) {
            	this.searchParams12.updateDate = val;
            },
            dayFormat22 : function (val) {
            	this.searchParams3.updateDate = val;
            },
            dayFormat3 : function (val) {
            	this.suspendEntity.startDate = val;
            },
            dayFormat4 : function (val) {
            	this.suspendEntity.endDate = val;
            },
            dayFormat5 : function (val) {
            	this.stopEntity.startDate = val;
            },
            dayFormat6 : function (val) {
            	this.stopEntity.endDate = val;
            },
            dayFormat7 : function (val) {
            	this.againStartEntity.startDate = val;
            },
            dayFormat8 : function (val) {
            	this.againStartEntity.endDate = val;
            },
            dayFormat9 : function (val) {
            	this.extendedEntity.startDate = val;
            },
            dayFormat10 : function (val) {
            	this.extendedEntity.endDate = val;
            },
            dayFormat11 : function (val) {
            	this.extendedEntity1.startDate = val;
            },
            dayFormat12 : function (val) {
            	this.extendedEntity1.endDate = val;
            },
            
            startTimer : function () {
            	var self = this;
            	var timer = setTimeout( () => {
            		var status = document.getElementById("status").value;
            		//是否循环
            		if (status == "2") {
            			clearTimeout(timer);
            			console.log("停止计时");
        				console.log("通话状态：挂机");
					} else {
						if (status == "1") {
	            			self.serverRecordMobileEntity.timeLength += 1;
	            			//修改状态为已接通
	            			self.serverRecordMobileEntity.type = "1";
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
    			    	self.serverRecordMobileEntity1.customerId = self.entity.id;
    			    	self.serverRecordMobileEntity1.customerName = self.entity.name;
    			    	self.serverRecordMobileEntity1.customerMobile = self.entity.mobile;
    			    	
    			    	document.getElementById("status").value = "";
    			    	document.getElementById("callFile").value = "";
    			    	//开始记录通话时长
    			    	self.serverRecordMobileEntity1.timeLength = 0;
    			    	//初始化通话状态为：未接通
    					self.serverRecordMobileEntity1.type = "0";
    			    	//激活保存按钮
    			    	self.showSaveButton = false;
    			    	//
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
            	self.serverRecordMobileEntity1.callFile = callFile;
            	console.log("录音文件："+self.serverRecordMobileEntity1.callFile);
				self.$refs["serverRecordMobileForm"].validate(function(valid){
					if (valid) {//校验通过
						self.$http.post("crm/serverRecordMobile", self.serverRecordMobileEntity1)
						.then(function (response) {
							self.showSaveButton = true;
							//self.flushSalerRecord();
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
            	self.customerId = '';
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
                this.$http.put('crm/serverRecordMobile/read/list/customer', {customerId: self.entity.id})
                .then(function (response) {
                	self.serverRecordMobileEntity = response.data.data;
                });
            },
            //刷新服务记录
            flushSalerRecord: function () {
            	var self = this;
            	this.$http.put('crm/serverRecordMobile/read/list/customer', {customerId: self.entity.id})
            	.then(function (response) {
            		self.serverRecordMobileEntity = response.data.data;
            	});
            },
            onChange:function(v){
            	this.users.map((s,index)=>{
                    if(s.id===v){
                      this.refundEntity1.dealerName = this.users[index].userName;
                    }
                });
            },
            signRowClassName : function (row, index) {
            	if ( 1 == row.isRefund) {
                	return 'refund-row';
                }
                return '';
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
            
    	//弹出暂停服务窗口
        showSuspendServerDialog: function (suspendEntityId) {
        	var self = this;
        	self.$http.post(self.sourceUrl, {id:suspendEntityId})
                    .then(function (response) {
                    	self.suspendEntity = response.data.data;
                    	self.suspendServerVisible = true;
                    });
        },
        
        //弹出停止服务窗口
        showStopServerDialog: function (stopEntityId) {
        	var self = this;
        	self.$http.post(self.sourceUrl, {id:stopEntityId})
        	.then(function (response) {
        		self.stopEntity = response.data.data;
        		self.stopServerVisible = true;
        	});
        },
        
        //重新开始服务窗口
        showAgainServerDialog: function (againStartEntityId) {
        	var self = this;
        	self.$http.post(self.sourceUrl, {id:againStartEntityId})
        	.then(function (response) {
        		self.againStartEntity = response.data.data;
        		self.againServerVisible = true;
        	});
        },
    	
    	//暂停服务保存记录
    	suspendSave : function () {
    		var self = this;
    		var content = "开始时间:"+self.suspendEntity.startDate.substring(0,10) +",结束时间:"+ self.suspendEntity.endDate.substring(0,10) +",暂停原因:" + self.suspendEntity.serverRemark;
    		
    		self.recordEntity1 = {ids:'',names:'',customerId:'',customerName:'',customerMobile:'',serveResult:'',serverRecord:'',serveTime:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',callTime:'',remark:'',createTime:''};
    		self.recordEntity1.ids = self.suspendEntity.id;
    		self.recordEntity1.type = 5;
    		self.recordEntity1.serverRecord = content ;
    		
    		//保存服务记录
    		self.$http.post(self.recordUrl, self.recordEntity1)
			.then(function (response) {
			});
    		
    		//保存服务修改
    		self.$http.post(self.sourceUrl, {'id':self.suspendEntity.id, 'startDate':self.suspendEntity.startDate, 'endDate':self.suspendEntity.endDate ,'flowId':703 })
    		.then(function (response) {
    		});
    		self.suspendServerVisible = false;
    		self.readList();
    		self.detailFormVisible = false;
    	},
    	
    	//停止服务保存记录
    	stopSave : function () {
    		var self = this;
    		var content = "开始时间:"+self.stopEntity.startDate.substring(0,10) +",结束时间:"+ self.stopEntity.endDate.substring(0,10) +",停止原因:" + self.stopEntity.serverRemark;
    		self.recordEntity1 = {ids:'',names:'',customerId:'',customerName:'',customerMobile:'',serveResult:'',serverRecord:'',serveTime:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',callTime:'',remark:'',createTime:''};
    		self.recordEntity1.ids = self.stopEntity.id;
    		self.recordEntity1.type = 5;
    		self.recordEntity1.serverRecord = content ;
    		
    		//保存服务记录
    		self.$http.post(self.recordUrl, self.recordEntity1)
    		.then(function (response) {
    		});
    		
    		//保存服务修改
    		self.$http.post(self.sourceUrl, {'id':self.stopEntity.id, 'startDate':self.stopEntity.startDate, 'endDate':self.stopEntity.endDate ,'flowId':704 })
    		.then(function (response) {
    		});
    		self.stopServerVisible = false;
			self.readList();
    		self.detailFormVisible = false;
    	},
    	
    	//重新开始服务并保存记录
    	againStartSave : function () {
    		var self = this;
    		var content = "开始时间:"+self.againStartEntity.startDate.substring(0,10) +",结束时间:"+ self.againStartEntity.endDate.substring(0,10) +",重新开始服务原因:" + self.againStartEntity.serverRemark;
    		self.recordEntity1 = {ids:'',names:'',customerId:'',customerName:'',customerMobile:'',serveResult:'',serverRecord:'',serveTime:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',callTime:'',remark:'',createTime:''};
    		self.recordEntity1.ids = self.againStartEntity.id;
    		self.recordEntity1.type = 5;
    		self.recordEntity1.serverRecord = content ;
    		
    		//保存服务记录
    		self.$http.post(self.recordUrl, self.recordEntity1)
    		.then(function (response) {
    		});
    		
    		//保存服务修改
    		self.$http.post(self.sourceUrl, {'id':self.againStartEntity.id, 'startDate':self.againStartEntity.startDate, 'endDate':self.againStartEntity.endDate ,'flowId':501 })
    		.then(function (response) {
    		});
    		self.againServerVisible = false;
    		self.readList();
    		self.detailFormVisible = false;
    	},
    	
    	//延长服务窗口
    	showExtendedServerDialog: function (extendedEntityId) {
        	var self = this;
        	self.$http.post(self.sourceUrl, {id:extendedEntityId})
        	.then(function (response) {
        		self.extendedEntity = response.data.data;
        		self.extendedServerVisible = true;
        	});
        },
        //延长服务并保存记录
        extendedSave : function () {
    		var self = this;
    		var content = "开始时间:"+self.extendedEntity.startDate.substring(0,10) +",结束时间:"+ self.extendedEntity.endDate.substring(0,10) +",赠送服务原因:" + self.extendedEntity.serverRemark;
    		self.recordEntity1 = {ids:'',names:'',customerId:'',customerName:'',customerMobile:'',serveResult:'',serverRecord:'',serveTime:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',callTime:'',remark:'',createTime:''};
    		self.recordEntity1.ids = self.extendedEntity.id;
    		self.recordEntity1.type = 5;
    		self.recordEntity1.serverRecord = content ;
    		
    		//保存服务记录
    		self.$http.post(self.recordUrl, self.recordEntity1)
    		.then(function (response) {
    		});
    		
    		//保存服务修改
    		self.$http.post(self.sourceUrl, {'id':self.extendedEntity.id, 'startDate':self.extendedEntity.startDate, 'endDate':self.extendedEntity.endDate ,'flowId':501 })
    		.then(function (response) {
    		});  
    		self.extendedServerVisible = false;
    		self.readList();
    		//self.detailFormVisible = false;
    	},
    	
    	//批量延长服务
    	showExtendedDialog : function () {
			var ids = new Array();
    		var names = new Array();
            for(var i in this.selectDatas){
                ids.push(this.selectDatas[i].customer.id);
                names.push(this.selectDatas[i].customer.name);
            }
        	var params = {};
            if (ids) {
                if (ids instanceof Array) {
                    params.ids = ids;
                    params.names = names;
                } else {
                    params.ids = [ids];
                    params.names = [names];
                }
            }
    		var self = this;
    		
    		if (self.selectDatas.length > 0) {
    			self.extendedEntity1 = {ids:'',names:'',startDate:'',endDate:'',serverRemark:''};
    			for(var i in this.selectDatas){
    				self.extendedEntity1.ids = params.ids;
    				self.extendedEntity1.names = params.names;
    			}
    			self.extendedVisible = true;
    		} else {
    			self.$alert("未选择客户");
    		}
    	},
    	
    	//批量延长服务 保存服务记录
		recordExtended : function() {
			var self = this;
			var content = "开始时间:"+self.extendedEntity1.startDate.substring(0,10) +",结束时间:"+ self.extendedEntity1.endDate.substring(0,10) +",赠送服务原因:" + self.extendedEntity1.serverRemark;
			self.extendedEntity1.type = 5;
			self.extendedEntity1.serverRecord = content ;
			self.$refs["extendedForm"].validate(function(valid){
				//保存服务记录
				self.$http.post(self.recordUrl, self.extendedEntity1)
				.then(function (response) {
					self.extendedVisible = false;
					self.readList();
				});
				
				//保存服务修改
	    		self.$http.post(self.sourceUrl+'/updateMore', {'ids':self.extendedEntity1.ids,'names':self.extendedEntity1.names, 'startDate':self.extendedEntity1.startDate, 'endDate':self.extendedEntity1.endDate ,'flowId':501 })
	    		.then(function (response) {
	    		});
	    		self.readList();
	    		self.extendedVisible = false;
	    		self.detailFormVisible = false;
			});
		},
		
		
		/**
    	 * 批量实盘建仓
    	 */
    	showStockDialog : function () {
    		var ids = new Array();
    		var names = new Array();
    		var useMoneys = new Array();
            for(var i in this.selectDatas){
                ids.push(this.selectDatas[i].customer.id);
                names.push(this.selectDatas[i].customer.name);
                useMoneys.push(this.selectDatas[i].customer.useMoney);
            }
        	var params = {};
            if (ids) {
                if (ids instanceof Array) {
                    params.ids = ids;
                    params.names = names;
                    params.useMoneys = useMoneys;
                } else {
                    params.ids = [ids];
                    params.names = [names];
                    params.useMoneys = [useMoneys];
                }
            }
    		var self = this;
    		
    		if (self.selectDatas.length > 0) {
    			self.customerCotEntity1 = {ids:'',names:'',useMoneys:'', useMoney:'', directionType: '', customerAdviserRecordId:'',directiveId:'',servicerId:'',servicerName:'',customerId:'',customerName:'',stockCode:'',stockName:'',stockSourceType:'',cotNum:'',cotPrice:'',cotMoney:'',currentPrice:'',currentMoney:'',lossPatio:'',lossMoney:'',position:'',indexValue:'',updateDate:'',createTime:'',};
    			for(var i in this.selectDatas){
    				self.customerCotEntity1.ids = params.ids;
    				self.customerCotEntity1.names = params.names;
    				self.customerCotEntity1.useMoneys = params.useMoneys;
    				
    			}
    			self.customerCotVisible = true;
    		} else {
    			self.$alert("未选择客户");
    		}
    	},
    	
    	/**
    	 * 保存批量建仓
    	 */
    	stockCotSave : function () {
    		var self = this;
			self.$refs["stockForm"].validate(function(valid){
				if (valid) {
					
					self.$http.post(self.customerCotUrl+'/addMoreStock', self.customerCotEntity1)
					.then(function (response) {
						self.readList();
						if (response.data.data.ids.length > 0 ||  response.data.data.names.length > 0) {
							self.stockCusFailureEntity.ids = response.data.data.ids;
							self.stockCusFailureEntity.names = response.data.data.names;
							self.stockCusFailureVisible = true;
						}
					});
					self.customerCotVisible = false;
				}
				
			});
			
    	},
    	/**
         * 批量获取当前股票行情
         */
        getStocksCodes : function (stockCode) {
       	 if(stockCode != null && stockCode != '' && stockCode != undefined){
       		 var self = this;
       		 self.$http.put(this.customerCotUrl+'/readPrice', {'stockCode':stockCode})
       		 .then(function (response) {
       			 self.customerCotEntity1.currentPrice = response.data.data.currentPrice;
       			 self.customerCotEntity1.stockName = response.data.data.stockName;
       			 
       		 }).catch(function(){
       			 
       		 });
       	 }
       	 
        },
       
        /**
         * 实盘批量
         */
        getCotsMoney : function () {
       	 var self = this;
       	 var num = self.customerCotEntity1.cotNum;
       	 var price = self.customerCotEntity1.cotPrice;
       	 var currentPrice =  self.customerCotEntity1.currentPrice;
       	 if (num == 0 || price ==0) {
       		 self.$message({
       			 message: '请输入交易数量和成交价格',
       			 type: 'warning'
       		 });
       	 } else if (num > 0 && price > 0) {
       		 self.customerCotEntity1.cotMoney = Math.round((num * price)*100)/100;
       		 self.customerCotEntity1.currentMoney = Math.round((num * currentPrice)*100)/100;
       	 }else {
       		 if (price < 0) {
       			 self.customerCotEntity1.cotPrice = '';
       		 }
       		 if (num < 0) {
       			 self.customerCotEntity1.cotNum = '';
       		 }
       		 self.$message({
       			 message: '请输入交易数量和成交价格',
       			 type: 'warning'
       		 });
       	 }
        },
		
		 //获取用户实盘列表
        readCustomerCotList : function () {
        	var self = this;
        	if (null == self.customerId || self.customerId == '') {
				//提示：未获取当前客户ID
        		self.$message("未获取当前客户ID");
        		return 0;
			}
            var params = {
                asc: false,
                orderBy: "id",
                pageNum: (this.$refs.entityTablePageCus ? this.$refs.entityTablePageCus.internalCurrentPage : 1),
                pageSize: (this.$refs.entityTablePageCus ? this.$refs.entityTablePageCus.internalPageSize : 10)
            };
            //将查询条件合并到请求参数中
            params['customerId'] = self.customerId;
            for (var item in self.searchParams1) {
            	if (typeof self.searchParams1[item] == 'boolean'){
                    params[item] = self.searchParams1[item];
                }else if (self.searchParams1[item] != "") {
                    params[item] = self.searchParams1[item];
                }
            }
            self.$http.put(self.customerCotUrl+'/read/list', params)
                    .then(function (response) {
                    	self.pageData1 = response.data;
                    });
        },
        
        //获取用户实盘操作记录列表
        readCustomerCotRecord : function (row, event) {
        	var self = this;
        	if (null == self.customerId || self.customerId == '') {
        		//提示：未获取当前客户ID
        		self.$message("未获取当前客户ID");
        		return 0;
        	};
    		if (null == row.id || row.id == '') {
    			//提示：未获取当前ID
    			self.$message("未获取当前ID");
    			return 0;
        	};
        	
        	var params = {
        			asc: false,
        			orderBy: "id",
        			pageNum: (this.$refs.entityTablePageCusLog ? this.$refs.entityTablePageCusLog.internalCurrentPage : 1),
        			pageSize: (this.$refs.entityTablePageCusLog ? this.$refs.entityTablePageCusLog.internalPageSize : 10)
        	};
        	
        	//将查询条件合并到请求参数中
        	customerActualCotId = row.id;
        	params['customerActualCotId'] = row.id;
        	for (var item in self.searchParams12) {
        		if (typeof self.searchParams12[item] == 'boolean'){
        			params[item] = self.searchParams12[item];
        		}else if (self.searchParams12[item] != "") {
        			params[item] = self.searchParams12[item];
        		}
        	}
        	self.$http.put(self.customerCotRecordUrl+'/read/list', params)
        	.then(function (response) {
        		self.pageData12= response.data;
        	});
        	this.customerCotRecordVisible = true;
        },

        readCustomerCotRecordList : function () {
        	var self = this;
        	var params = {
        			asc: false,
        			orderBy: "id",
        			pageNum: (this.$refs.entityTablePageCusLog ? this.$refs.entityTablePageCusLog.internalCurrentPage : 1),
        			pageSize: (this.$refs.entityTablePageCusLog ? this.$refs.entityTablePageCusLog.internalPageSize : 10)
        	};
        	//将查询条件合并到请求参数中
        	params['customerActualCotId'] = customerActualCotId;
        	for (var item in self.searchParams12) {
        		if (typeof self.searchParams12[item] == 'boolean'){
        			params[item] = self.searchParams12[item];
        		}else if (self.searchParams12[item] != "") {
        			params[item] = self.searchParams12[item];
        		}
        	}
        	self.$http.put(self.customerCotRecordUrl+'/read/list', params)
        	.then(function (response) {
        		self.pageData12= response.data;
        	});
        },

        //建仓/买入/卖出
        showCustomerCotDialog : function (directionType,id) {
        	var self = this;
        	self.customerCotEntity = {useMoney:'',directionType:'', customerAdviserRecordId:'',directiveId:'',servicerId:'',servicerName:'',customerId:'',customerName:'',stockCode:'',stockName:'',stockSourceType:'',cotNum:'',cotPrice:'',cotMoney:'',currentPrice:'',currentMoney:'',lossPatio:'',lossMoney:'',position:'',indexValue:'',updateDate:'',createTime:'',};
        	if ((directionType == 1 || directionType == 2) && id != null && id != '' && id != undefined) {
        		this.$http.put(this.customerCotUrl+'/read/detail', {'id': id})
                .then(function (response) {
                	self.customerCotEntity.customerId = response.data.data.customerId;
                	self.customerCotEntity.customerName = response.data.data.customerName;
                	self.customerCotEntity.useMoney = self.useMoney;
                	self.customerCotEntity.stockCode = response.data.data.stockCode;
                	self.customerCotEntity.stockName = response.data.data.stockName;
                	self.getStocksCode(response.data.data.stockCode);
                });
			}else if (directionType == 0) {
				self.customerCotEntity.customerId = self.customerId;
	        	self.customerCotEntity.customerName = self.customerName;
	        	self.customerCotEntity.useMoney = self.useMoney;
			}
        	self.customerCotEntity.directionType = directionType;
        	self.customerEditFormVisible = true;
        },
        //保存实盘建仓、买入、卖出信息
        customerCotsave : function () {
        	var self = this;
            self.$refs["customerEditForm"].validate(function(valid){
                if (valid) {//校验通过
                    self.$http.post(self.customerCotUrl, self.customerCotEntity)
                            .then(function (response) {
                                var result = response.data;
                                if (result.httpCode == 200) {
                                	self.customerCotEntity = result.data;
                                    //self.customerEditFormVisible = false;
                                    if (self.customerCotEntity.id) {//修改
                                        for (var i = 0; i < self.pageData1.data.length; i++) {
                                            if (self.pageData1.data[i].id == result.data.id) {
                                                Vue.set(self.pageData1.data, i, result.data);
                                                break;
                                            }
                                        }
                                    } else {
                                        self.pageData1.data.unshift(result.data);
                                    }
                                    self.readList();
                                    self.customerEditFormVisible = false;
                                    const loading = this.$loading({
      	        			          lock: true,
      	        			          text: 'Loading',
      	        			          spinner: 'el-icon-loading',
      	        			          background: 'rgba(0, 0, 0, 0.7)'
      	        			        });
                                    self.flushCustomerDetail(self.customerCotEntity.customerId);
                                    self.readCustomerCotList();
                                    self.useMoney = self.customerCotEntity.useMoney;//更新页面可用资金
                                    setTimeout(function() {
                                    	loading.close();
                                    }, 2000);

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
        
        //刷新客户详情
        flushCustomerDetail: function (customerId) {
        	var self = this;
            this.$http.put(self.sourceUrl+'/read/serverDetails', {id: customerId})
                .then(function (response) {
    				//刷新可用资金
                	self.entity = response.data.data.customer;
                	self.applyEntity = response.data.data.apply;
                	self.useMoney = self.entity.useMoney;
                	self.useTeacherMoney = self.entity.useTeacherMoney;
                	self.serverRecordEntity = response.data.data.serverRecord;
                	self.serverRecordMobileEntity = response.data.data.serverRecordMobile;
                });
        },
        
        //获取公司虚盘列表
        readTeacherCotList : function () {
        	var self = this;
        	if (null == self.customerId || self.customerId == '') {
        		//提示：未获取当前客户ID
        		self.$message("未获取当前客户ID");
        		return 0;
        	}
        	var params = {
        			asc: false,
        			orderBy: "id",
        			pageNum: (this.$refs.entityTablePageTea ? this.$refs.entityTablePageTea.internalCurrentPage : 1),
        			pageSize: (this.$refs.entityTablePageTea ? this.$refs.entityTablePageTea.internalPageSize : 10)
        	};
        	//将查询条件合并到请求参数中
        	params['customerId'] = self.customerId;
        	for (var item in self.searchParams2) {
        		if (typeof self.searchParams2[item] == 'boolean'){
        			params[item] = self.searchParams2[item];
        		}else if (self.searchParams2[item] != "") {
        			params[item] = self.searchParams2[item];
        		}
        	}
        	self.$http.put(self.teacherCotUrl+'/read/list', params)
        	.then(function (response) {
        		self.pageData2 = response.data;
        	});
        },
        
        //获取公司虚盘操作记录列表
        readTeacherCotRecord : function (row, event) {
        	var self = this;
        	if (null == self.customerId || self.customerId == '') {
        		//提示：未获取当前客户ID
        		self.$message("未获取当前客户ID");
        		return 0;
        	};
    		if (null == row.id || row.id == '') {
    			//提示：未获取当前ID
    			self.$message("未获取当前ID");
    			return 0;
        	};
        	var params = {
        			asc: false,
        			orderBy: "id",
        			pageNum: (this.$refs.entityTablePageTeaLog ? this.$refs.entityTablePageTeaLog.internalCurrentPage : 1),
        			pageSize: (this.$refs.entityTablePageTeaLog ? this.$refs.entityTablePageTeaLog.internalPageSize : 10)
        	};
        	//将查询条件合并到请求参数中
        	teacherDirectiveCotId = row.id;
        	params['teacherDirectiveCotId'] = row.id;
        	for (var item in self.searchParams3) {
        		if (typeof self.searchParams3[item] == 'boolean'){
        			params[item] = self.searchParams3[item];
        		}else if (self.searchParams3[item] != "") {
        			params[item] = self.searchParams3[item];
        		}
        	}
        	self.$http.put(self.teacherCotRecordUrl+'/read/list', params)
        	.then(function (response) {
        		self.pageData3= response.data;
        	});
        	this.teacherCotRecordVisible = true;
        },
        
        //查询、刷新虚盘操作记录表
        readTeacherCotRecordList : function () {
        	var self = this;
        	var params = {
        			asc: false,
        			orderBy: "id",
        			pageNum: (this.$refs.entityTablePageTeaLog ? this.$refs.entityTablePageTeaLog.internalCurrentPage : 1),
        			pageSize: (this.$refs.entityTablePageTeaLog ? this.$refs.entityTablePageTeaLog.internalPageSize : 10)
        	};
        	//将查询条件合并到请求参数中
        	params['teacherDirectiveCotId'] = teacherDirectiveCotId;
        	for (var item in self.searchParams3) {
        		if (typeof self.searchParams3[item] == 'boolean'){
        			params[item] = self.searchParams3[item];
        		}else if (self.searchParams3[item] != "") {
        			params[item] = self.searchParams3[item];
        		}
        	}
        	self.$http.put(self.teacherCotRecordUrl+'/read/list', params)
        	.then(function (response) {
        		self.pageData3= response.data;
        	});
        },
        
        //公司虚拟盘建仓/买入/卖出
        showTeacherCotDialog : function (directionType,id) {
        	var self = this;
        	self.teacherCotEntity = {useMoney:'',directionType:'', customerAdviserRecordId:'',directiveId:'',servicerId:'',servicerName:'',customerId:'',customerName:'',stockCode:'',stockName:'',stockSourceType:'',cotNum:'',cotPrice:'',cotMoney:'',currentPrice:'',currentMoney:'',lossPatio:'',lossMoney:'',position:'',indexValue:'',updateDate:'',createTime:'',};
        	if ((directionType == 1 || directionType == 2) && id != null && id != '' && id != undefined) {
        		this.$http.put(this.teacherCotUrl+'/read/detail', {'id': id})
                .then(function (response) {
                	self.teacherCotEntity.customerId = response.data.data.customerId;
                	self.teacherCotEntity.customerName = response.data.data.customerName;
                	self.teacherCotEntity.useMoney = self.useTeacherMoney;
                	self.teacherCotEntity.stockCode = response.data.data.stockCode;
                	self.getTeacherStocksCode(self.teacherCotEntity.stockCode);
                	self.teacherCotEntity.stockName = response.data.data.stockName;
                });
			}else if (directionType == 0) {
				self.teacherCotEntity.customerId = self.customerId;
	        	self.teacherCotEntity.customerName = self.customerName;
	        	self.teacherCotEntity.useMoney = self.useTeacherMoney;
			}
        	self.teacherCotEntity.directionType = directionType;
        	self.teacherEditFormVisible = true;
        },
        
        //保存虚盘建仓信息
        teacherCotsave : function () {
        	var self = this;
        	self.$refs["teacherEditForm"].validate(function(valid){
        		if (valid) {//校验通过
        			self.$http.post(self.teacherCotUrl, self.teacherCotEntity)
        			.then(function (response) {
        				var result = response.data;
        				if (result.httpCode == 200) {
	        				self.teacherCotEntity = result.data;
	        				self.teacherEditFormVisible = false;
	        				if (self.teacherCotEntity.id) {//修改
	        					for (var i = 0; i < self.pageData2.data.length; i++) {
	        						if (self.pageData2.data[i].id == result.data.id) {
	        							Vue.set(self.pageData2.data, i, result.data);
	        							self.readTeacherCotList();
	        							break;
	        						}
	        					}
	        				} else {
	        					self.pageData2.data.unshift(result.data);
	        				}
	        				self.readList();
	        				self.teacherEditFormVisible = false;
	        				const loading = this.$loading({
	        			          lock: true,
	        			          text: 'Loading',
	        			          spinner: 'el-icon-loading',
	        			          background: 'rgba(0, 0, 0, 0.7)'
	        			        });
                            self.flushCustomerDetail(self.customerId);
	        				self.readTeacherCotList();
	        				self.useTeacherMoney = self.teacherCotEntity.useMoney;
	        				setTimeout(function() {
	        					loading.close();
                            }, 2000);
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
		
		 //删除实盘客户信息
        delCustomerCot: function (entityIds) {
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
            self.$confirm('您确定要删除'+params.ids.length+'条记录吗?, 删除将无法恢复!', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(function () {
                self.$http.delete(self.customerCotUrl, {body: params})
                        .then(function (response) {
                            var tempIndexArray = [];
                            //同步更新界面中的表格
                            for(var j in params.ids){
                                for(var i in self.pageData1.data){
                                    if (self.pageData1.data[i].id == params.ids[j]){
                                        self.pageData1.data.splice(i,1);
                                        break;
                                    }
                                }
                            }
                            
                            self.flushCustomerDetail(self.customerId);
                            self.useMoney = self.customerCotEntity.useMoney;//更新页面可用资金
                            const loading = this.$loading({
                                lock: true,
                                text: 'Loading',
                                spinner: 'el-icon-loading',
                                background: 'rgba(0, 0, 0, 0.7)'
                              });
                            self.readCustomerCotList();
                            self.readList();
                            setTimeout(function() {
                                loading.close();
                                }, 2000);
                        });
            }).catch(function(){});
        },
        //删除虚盘客户信息
        delTeacherCot: function (entityIds) {
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
        	self.$confirm('您确定要删除'+params.ids.length+'条记录吗?, 删除将无法恢复!', '提示', {
        		confirmButtonText: '确定',
        		cancelButtonText: '取消',
        		type: 'warning'
        	}).then(function () {
        		self.$http.delete(self.teacherCotUrl, {body: params})
        		.then(function (response) {
        			var tempIndexArray = [];
        			//同步更新界面中的表格
        			for(var j in params.ids){
        				for(var i in self.pageData2.data){
        					if (self.pageData2.data[i].id == params.ids[j]){
        						self.pageData2.data.splice(i,1);
        						break;
        					}
        				}
        			}
        			
        			self.flushCustomerDetail(self.customerId);
        			self.useTeacherMoney = self.teacherCotEntity.useMoney;
        			const loading = this.$loading({
                        lock: true,
                        text: 'Loading',
                        spinner: 'el-icon-loading',
                        background: 'rgba(0, 0, 0, 0.7)'
                      });
        			self.readTeacherCotList();
        			self.readList();
        			setTimeout(function() {
                        loading.close();
                        }, 2000);
        		});
        	}).catch(function(){});
        },
		
        //当持股数量为0时，隐藏删除按钮
        isAble : function (num) {
        	if(num == 0){
        		return false;
        	}else if (num > 0) {
        		return true;
        	}
        	
        },
        
        /**
         * 获取当前股票行情
         */
         getStocksCode : function (stockCode) {
        	 if(stockCode != null && stockCode != '' && stockCode != undefined){
        		 var self = this;
            	 self.$http.put(this.customerCotUrl+'/readPrice', {'stockCode':stockCode})
            	 	.then(function (response) {
	                     self.customerCotEntity.currentPrice = response.data.data.currentPrice;
	                     self.customerCotEntity.stockName = response.data.data.stockName;
         				
                 }).catch(function(){
                	 
                 });
        	 }
        	 
         },
         
        /**
         * 实盘
         */
        getCotMoney : function () {
        	var self = this;
			var num = self.customerCotEntity.cotNum;
			var price = self.customerCotEntity.cotPrice;
			var currentPrice =  self.customerCotEntity.currentPrice;
			if (num == 0 || price ==0) {
				self.$message({
    				message: '请输入交易数量和成交价格',
    				type: 'warning'
    			});
			} else if (num > 0 && price > 0) {
				self.customerCotEntity.cotMoney = Math.round((num * price)*100)/100;
				self.customerCotEntity.currentMoney = Math.round((num * currentPrice)*100)/100;
			}else {
				if (price < 0) {
					self.customerCotEntity.cotPrice = '';
				}
				if (num < 0) {
					self.customerCotEntity.cotNum = '';
				}
				self.$message({
    				message: '请输入交易数量和成交价格',
    				type: 'warning'
    			});
			}
        },
        
        /**
         * 获取虚盘当前股票行情
         */
        getTeacherStocksCode : function (stockCode) {
       	 if(stockCode != null && stockCode != '' && stockCode != undefined){
       		 var self = this;
       		 self.$http.put(this.teacherCotUrl+'/readPrice', {'stockCode':stockCode})
       		 .then(function (response) {
				self.teacherCotEntity.currentPrice = response.data.data.currentPrice;
       			self.teacherCotEntity.stockName = response.data.data.stockName;
       		 }).catch(function(){
       			 
       		 });
       	 }
       	 
        },
        
        /**
		 * 虚盘
		 */
		getTeacherCotMoney : function () {
			var self = this;
			var num = self.teacherCotEntity.cotNum;
			var price = self.teacherCotEntity.cotPrice;
			var currentPrice =  self.teacherCotEntity.currentPrice;
			if (num == 0 || price ==0) {
				self.$message({
    				message: '请输入交易数量和成交价格',
    				type: 'warning'
    			});
			} else if (num > 0 && price > 0) {
				self.teacherCotEntity.cotMoney = Math.round((num * price)*100)/100;
				self.teacherCotEntity.currentMoney =  Math.round((num * currentPrice)*100)/100;
			}else {
				if (price < 0) {
					self.customerCotEntity.cotPrice = '';
				}
				if (num < 0) {
					self.customerCotEntity.cotNum = '';
				}
				self.$message({
					message: '请输入交易数量和成交价格',
					type: 'warning'
				});
			}
		},
        
		
		/**
    	 * 批量虚盘建仓
    	 */
    	showTeacherStockDialog : function () {
    		var ids = new Array();
    		var names = new Array();
    		var useMoneys = new Array();
            for(var i in this.selectDatas){
                ids.push(this.selectDatas[i].customer.id);
                names.push(this.selectDatas[i].customer.name);
                useMoneys.push(this.selectDatas[i].customer.useTeacherMoney);
            }
        	var params = {};
            if (ids) {
                if (ids instanceof Array) {
                    params.ids = ids;
                    params.names = names;
                    params.useMoneys = useMoneys;
                } else {
                    params.ids = [ids];
                    params.names = [names];
                    params.useMoneys = [useMoneys];
                }
            }
    		var self = this;
    		
    		if (self.selectDatas.length > 0) {
    			self.teacherCotEntity1 = {ids:'',names:'',idsStr:'',namesStr:'',useMoneys:'', useMoney:'', directionType:'', customerAdviserRecordId:'',directiveId:'',servicerId:'',servicerName:'',customerId:'',customerName:'',stockCode:'',stockName:'',stockSourceType:'',cotNum:'',cotPrice:'',cotMoney:'',currentPrice:'',currentMoney:'',lossPatio:'',lossMoney:'',position:'',indexValue:'',updateDate:'',createTime:'',};
    			for(var i in this.selectDatas){
    				self.teacherCotEntity1.ids = params.ids;
    				self.teacherCotEntity1.names = params.names;
    				self.teacherCotEntity1.idsStr = String(params.ids);
    				self.teacherCotEntity1.namesStr = String(params.names);
    				self.teacherCotEntity1.useMoneys = params.useMoneys;
    			}
    			self.teacherCotVisible = true;
    		} else {
    			self.$alert("未选择客户");
    		}
    	},
    	
    	/**
    	 * 保存批量建仓
    	 */
    	stockTeacherCotSave : function () {
    		var self = this;
			self.$refs["stockTeacherForm"].validate(function(valid){
				if (valid) {
					self.$http.post(self.teacherCotUrl+'/addMoreStock', self.teacherCotEntity1)
					.then(function (response) {
						self.readList();
						if (response.data.data.ids.length > 0 ||  response.data.data.names.length > 0) {
							self.stockFailureEntity.ids = response.data.data.ids;
							self.stockFailureEntity.names = response.data.data.names;
							self.stockFailureEntity.idsStr = String(response.data.data.ids);
							self.stockFailureEntity.namesStr = String(response.data.data.names);
							self.stockFailureVisible = true;
						}
					});
					self.teacherCotVisible = false;
				}
			});
    	},
    	
    	/**
         * 批量获取当前股票行情
         */
        getTeacherStocksCodes : function (stockCode) {
       	 if(stockCode != null && stockCode != '' && stockCode != undefined){
       		 var self = this;
       		 self.$http.put(this.customerCotUrl+'/readPrice', {'stockCode':stockCode})
       		 .then(function (response) {
       			 self.teacherCotEntity1.currentPrice = response.data.data.currentPrice;
       			 self.teacherCotEntity1.stockName = response.data.data.stockName;
       			 
       		 }).catch(function(){
       			 
       		 });
       	 }
       	 
        },
        /**
         * 批量获取当前股票行情
         */
        getTeacherHalfStocksCodeOut : function (stockCode) {
        	if(stockCode != null && stockCode != '' && stockCode != undefined){
        		var self = this;
        		self.$http.put(this.customerCotUrl+'/readPrice', {'stockCode':stockCode})
        		.then(function (response) {
        			self.teacherHalfCotOutEntity.currentPrice = response.data.data.currentPrice;
        			self.teacherHalfCotOutEntity.stockName = response.data.data.stockName;
        			
        		}).catch(function(){
        			
        		});
        	}
        	
        },
        /**
         * 批量获取当前股票行情
         */
        getTeacherStocksCodeOut : function (stockCode) {
        	if(stockCode != null && stockCode != '' && stockCode != undefined){
        		var self = this;
        		self.$http.put(this.customerCotUrl+'/readPrice', {'stockCode':stockCode})
        		.then(function (response) {
        			self.teacherCotOutEntity.currentPrice = response.data.data.currentPrice;
        			self.teacherCotOutEntity.stockName = response.data.data.stockName;
        			
        		}).catch(function(){
        			
        		});
        	}
        	
        },
        /**
         * 批量获取当前股票行情
         */
        getTeacherStocksCodes : function (stockCode) {
        	if(stockCode != null && stockCode != '' && stockCode != undefined){
        		var self = this;
        		self.$http.put(this.customerCotUrl+'/readPrice', {'stockCode':stockCode})
        		.then(function (response) {
        			self.teacherCotEntity1.currentPrice = response.data.data.currentPrice;
        			self.teacherCotEntity1.stockName = response.data.data.stockName;
        			
        		}).catch(function(){
        			
        		});
        	}
        	
        },
        
        /**
    	 * 批量虚盘平仓仓
    	 */
    	showTeacherStockOutDialog : function () {
    		var ids = new Array();
    		var names = new Array();
    		var useMoneys = new Array();
            for(var i in this.selectDatas){
                ids.push(this.selectDatas[i].customer.id);
                names.push(this.selectDatas[i].customer.name);
                useMoneys.push(this.selectDatas[i].customer.useTeacherMoney);
            }
        	var params = {};
            if (ids) {
                if (ids instanceof Array) {
                    params.ids = ids;
                    params.names = names;
                    params.useMoneys = useMoneys;
                } else {
                    params.ids = [ids];
                    params.names = [names];
                    params.useMoneys = [useMoneys];
                }
            }
    		var self = this;
    		
    		if (self.selectDatas.length > 0) {
    			self.teacherCotOutEntity = {ids:'',names:'',useMoneys:'', useMoney:'', directionType:'', customerAdviserRecordId:'',directiveId:'',servicerId:'',servicerName:'',customerId:'',customerName:'',stockCode:'',stockName:'',stockSourceType:'',cotNum:'',cotPrice:'',cotMoney:'',currentPrice:'',currentMoney:'',lossPatio:'',lossMoney:'',position:'',indexValue:'',updateDate:'',createTime:'',};
    			for(var i in this.selectDatas){
    				self.teacherCotOutEntity.ids = params.ids;
    				self.teacherCotOutEntity.names = params.names;
    				self.teacherCotOutEntity.useMoneys = params.useMoneys;
    				
    			}
    			self.teacherCotOutVisible = true;
    		} else {
    			self.$alert("未选择客户");
    		}
    	},
    	
    	/**
    	 * 保存批量平仓
    	 */
    	stockTeacherCotOut : function () {
    		var self = this;
			self.$refs["stockTeacherOutForm"].validate(function(valid){
				if (valid) {
					self.$http.post(self.teacherCotUrl+'/sellMoreStock', self.teacherCotOutEntity)
					.then(function (response) {
						self.readList();
						if (response.data.data.ids.length > 0 ||  response.data.data.names.length > 0) {
							self.stockOutFailureEntity.ids = response.data.data.ids;
							self.stockOutFailureEntity.names = response.data.data.names;
							self.stockOutFailureVisible = true;
						}
					});
					self.teacherCotOutVisible = false;
				}
			});
    	},
    	
    	/**
    	 * 批量虚盘平半仓
    	 */
    	showTeacherHalfStockOutDialog : function () {
    		var ids = new Array();
    		var names = new Array();
    		var useMoneys = new Array();
    		for(var i in this.selectDatas){
    			ids.push(this.selectDatas[i].customer.id);
    			names.push(this.selectDatas[i].customer.name);
    			useMoneys.push(this.selectDatas[i].customer.useTeacherMoney);
    		}
    		var params = {};
    		if (ids) {
    			if (ids instanceof Array) {
    				params.ids = ids;
    				params.names = names;
    				params.useMoneys = useMoneys;
    			} else {
    				params.ids = [ids];
    				params.names = [names];
    				params.useMoneys = [useMoneys];
    			}
    		}
    		var self = this;
    		
    		if (self.selectDatas.length > 0) {
    			self.teacherHalfCotOutEntity = {ids:'',names:'',useMoneys:'', useMoney:'', directionType:'', customerAdviserRecordId:'',directiveId:'',servicerId:'',servicerName:'',customerId:'',customerName:'',stockCode:'',stockName:'',stockSourceType:'',cotNum:'',cotPrice:'',cotMoney:'',currentPrice:'',currentMoney:'',lossPatio:'',lossMoney:'',position:'',indexValue:'',updateDate:'',createTime:'',};
    			for(var i in this.selectDatas){
    				self.teacherHalfCotOutEntity.ids = params.ids;
    				self.teacherHalfCotOutEntity.names = params.names;
    				self.teacherHalfCotOutEntity.useMoneys = params.useMoneys;
    				
    			}
    			self.teacherHalfCotOutVisible = true;
    		} else {
    			self.$alert("未选择客户");
    		}
    	},
    	
    	/**
    	 * 保存批量平半仓
    	 */
    	stockTeacherHalfCotOut : function () {
    		var self = this;
    		self.$refs["stockTeacherHalfOutForm"].validate(function(valid){
    			if (valid) {
    				self.$http.post(self.teacherCotUrl+'/sellHalfStock', self.teacherHalfCotOutEntity)
    				.then(function (response) {
    					self.readList();
    					if (response.data.data.ids.length > 0 ||  response.data.data.names.length > 0) {
    						self.stockOutFailureEntity.ids = response.data.data.ids;
    						self.stockOutFailureEntity.names = response.data.data.names;
    						self.stockOutFailureVisible = true;
    					}
    				});
    				self.teacherHalfCotOutVisible = false;
				}
    		});
    	},
        
        
        /**
		 * 最大金额建仓
		 */
		all_add : function () {
			var self = this;
			var money = self.customerCotEntity.useMoney;
			var price = self.customerCotEntity.cotPrice;
			var current = self.customerCotEntity.currentPrice;
			if (money > 0 && price > 0) {
				self.customerCotEntity.cotNum = parseInt( money / price);
				self.customerCotEntity.cotMoney = self.customerCotEntity.cotNum * price;
				self.customerCotEntity.currentMoney = self.customerCotEntity.cotNum * current;
			} else {
				self.$message({
    				message: '请输入可用资金和成交价格',
    				type: 'warning'
    			});
			}
		},
		//清空并刷新数据
        flushAndReadList : function () {
        	 var self = this;
        	 self.searchParams = {stockCode:'',stockName:'',stockNum:'',teacherStockNum:'',names:'', name:'',flowId:'',mobile:'',fuzzyMobile:'',md5Mobile:'',policyholderId:'',duty:'',birthday:'',province:'',city:'',insureNum:'',insureMoney:'',introduceNum:'',labels:'',currentApplyId:'',isHg:'',isService:'',salerId:'',managerId:'',directorId:'',ministerId:'',salerName:'',managerName:'',directorName:'',contracterId:'',contracterName:'',ministerName:'',serverId:'',serverName:'',oldApplyId:'',oldIsHg:'',oldIsService:'',oldServerId:'',oldServerName:'',contracterRemark:'',subordinate:'',enterTime:'',fromInfo:'',startTime:'',endTime:'',isNewClub:''};
     		 self.readList();
        },
        updateAllStocks : function () {
        	 this.$http.put(this.sourceUrl+'/updateAllStock')
             .then(function (response) {
                 this.readTeacherCotList();
                 this.readCustomerCotList();
             });
        },
      //校正用户可用资金
        updateUserMoney: function () {
        	var self = this;
        	this.$http.put('crm/customer/updateAllUserMoney')
        	.then(function (response) {
        		self.readList();
            });
        },
        //初始化通话记录
        updateToCustomer: function () {
        	var self = this;
        	this.$http.put('crm/serverRecordMobile/toCustomer')
        	.then(function (response) {
        		self.readList();
        	});
        },
        
        
    	
		/**
    	 * 批量虚盘加仓
    	 */
    	showTeacherStockAddDialog : function () {
    		var ids = new Array();
    		var names = new Array();
    		var useMoneys = new Array();
            for(var i in this.selectDatas){
                ids.push(this.selectDatas[i].customer.id);
                names.push(this.selectDatas[i].customer.name);
                useMoneys.push(this.selectDatas[i].customer.useTeacherMoney);
            }
        	var params = {};
            if (ids) {
                if (ids instanceof Array) {
                    params.ids = ids;
                    params.names = names;
                    params.useMoneys = useMoneys;
                } else {
                    params.ids = [ids];
                    params.names = [names];
                    params.useMoneys = [useMoneys];
                }
            }
    		var self = this;
    		
    		if (self.selectDatas.length > 0) {
    			self.teacherCotEntity1 = {ids:'',names:'',idsStr:'',namesStr:'',useMoneys:'', useMoney:'', directionType:'', customerAdviserRecordId:'',directiveId:'',servicerId:'',servicerName:'',customerId:'',customerName:'',stockCode:'',stockName:'',stockSourceType:'',cotNum:'',cotPrice:'',cotMoney:'',currentPrice:'',currentMoney:'',lossPatio:'',lossMoney:'',position:'',indexValue:'',updateDate:'',createTime:'',};
    			for(var i in this.selectDatas){
    				self.teacherCotEntity1.ids = params.ids;
    				self.teacherCotEntity1.names = params.names;
    				self.teacherCotEntity1.idsStr = String(params.ids);
    				self.teacherCotEntity1.namesStr = String(params.names);
    				self.teacherCotEntity1.useMoneys = params.useMoneys;
    			}
    			self.teacherCotAddVisible = true;
    		} else {
    			self.$alert("未选择客户");
    		}
    	},
    	
    	/**
    	 * 保存批量加仓
    	 */
    	stockTeacherCotAddSave : function () {
    		var self = this;
			self.$refs["stockTeacherAddForm"].validate(function(valid){
				if (valid) {
					self.$http.post(self.teacherCotUrl+'/batchAddStock', self.teacherCotEntity1)
					.then(function (response) {
						self.readList();
						if (response.data.data.ids.length > 0 ||  response.data.data.names.length > 0) {
							self.stockFailureEntity.ids = response.data.data.ids;
							self.stockFailureEntity.names = response.data.data.names;
							self.stockFailureEntity.idsStr = String(response.data.data.ids);
							self.stockFailureEntity.namesStr = String(response.data.data.names);
							self.stockFailureAddVisible = true;
						}
					});
					self.teacherCotAddVisible = false;
				}
			});
    	},
   }
});