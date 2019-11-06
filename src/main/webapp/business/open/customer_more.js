	/**
	*管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"crm/customer",
        	auditMobileUrl:"crm/auditMobile",
            //是否显示详情窗口
            detailFormVisible: false,
            //查询参数
            searchParams: {name:'',flowId:'',mobile:'',fuzzyMobile:'',md5Mobile:'',policyholderId:'',duty:'',birthday:'',province:'',city:'',insureNum:'',insureMoney:'',introduceNum:'',labels:'',currentApplyId:'',isHg:'',isService:'',salerId:'',managerId:'',directorId:'',ministerId:'',salerName:'',managerName:'',directorName:'',contracterId:'',contracterName:'',ministerName:'',serverId:'',serverName:'',oldApplyId:'',oldIsHg:'',oldIsService:'',oldServerId:'',oldServerName:'',contracterRemark:'',subordinate:'',enterTime:'',fromInfo:'',startTime:'',endTime:'',isNewClub:'',insuranceId:''},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            //当前操作的实体（编辑/新增）
            entity: {name:'',flowId:'',policyholderId:'',duty:'',birthday:'',province:'',city:'',insureNum:'',insureMoney:'',introduceNum:'',labels:'',currentApplyId:'',isHg:'',isService:'',salerId:'',managerId:'',directorId:'',ministerId:'',salerName:'',managerName:'',directorName:'',contracterId:'',contracterName:'',ministerName:'',serverId:'',serverName:'',oldApplyId:'',oldIsHg:'',oldIsService:'',oldServerId:'',oldServerName:'',contracterRemark:'',remark:'',createTime:''},
            //签单实体（详情）
            applyEntity: {customerId:'',customerName:'',policyNo:'',belong:'',insuranceId:'',insuranceName:'',introducerId:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',remark:'',submitCodeOld:'',createTime:''},
            //客服服务记录实体（详情）
            serverRecordEntity: {customerId:'',customerName:'',customerMobile:'',serveResult:'',serverRecord:'',serveTime:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',callTime:'',remark:'',createTime:''},
            //市场部服务记录实体（详情）
            serverRecordMobileEntity: {customerId:'',customerName:'',customerMobile:'',serveResult:'',serverRecord:'',serveTime:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',callTime:'',remark:'',createTime:''},
            //表格中选中的行的集合
            selectDatas: [],
            //表单校验规则 api:https://github.com/yiminghe/async-validator
            rules: {
	    		name: [
	    		Vali.utf8mb4Len(false,50)
	    	],
            },
            activeName: 'apply1',
            //通话记录实体
            serverRecordMobileEntity1: {customerId:'',customerName:'',customerMobile:'',serverRecord:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',testTimeLength:'',testUpdateTime:''},
            //通话记录实体
            salerRecordEntity: {customerId:'',customerName:'',customerMobile:'',serverRecord:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',},
            //父页面方法的引用
            topdial : top.window.dial,
            topdrop : top.window.drop,
            showSaveButton : true,
            callDisable: false,
            isFirstSaveRecord : false,
            
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
                    orderBy: "audit_time",
                    pageNum: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalCurrentPage : 1),
                    pageSize: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalPageSize : 10)
                };
                var server = '';
                if (document.getElementById('server')) {
                	server = document.getElementById('server').value;
                }
                //首次多次客服
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
                this.$http.put(this.sourceUrl+'/contracterMore/list', params)
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
                            this.serverRecordEntity = response.data.data.serverRecord;
                            this.serverRecordMobileEntity = response.data.data.serverRecordMobile;
                            this.salerRecordEntity = response.data.data.salerRecord;
                            this.salerRecordEntity2 = response.data.data.salerRecord1;
                            this.detailFormVisible = true;
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
            dayFormat1 : function (val) {
            	this.searchParams.startTime = val;
            },
            dayFormat2 : function (val) {
            	this.searchParams.endTime = val;
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
	            			self.serverRecordMobileEntity.timeLength += 1;
	            			//修改状态为已接通
	            			self.serverRecordMobileEntity.type = "1";
	            			
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
            	self.serverRecordMobileEntity1.callFile = callFile;
            	console.log("录音文件："+self.serverRecordMobileEntity1.callFile);
				self.$refs["serverRecordMobileForm"].validate(function(valid){
					if (valid) {//校验通过
						self.$http.post("crm/serverRecordMobile", self.serverRecordMobileEntity1)
						.then(function (response) {
							self.showSaveButton = true;
							//刷新通话记录
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
            flushSalerRecord1: function () {
            	var self = this;
            	this.$http.put('crm/salerRecord/read/list/customer', {customerId: self.entity.id})
            	.then(function (response) {
            		self.salerRecordEntity1 = response.data.data;
            	});
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
            
            //电话接通后，自动保存一次通话
            firstSaveRecord : function () {
            	var self = this;
            	var callFile = document.getElementById("callFile").value;
            	self.serverRecordMobileEntity1.callFile = callFile;
            	console.log("录音文件："+self.serverRecordMobileEntity1.callFile);
            	self.$http.put("crm/serverRecordMobile/automation", self.serverRecordMobileEntity1)
            	.then(function (response) {
            		var result = response.data.data;
            		self.serverRecordMobileEntity1 = result;
            		//刷新通话记录
            		self.flushAllSalerRecordResource();
            		self.flushSalerRecordResource();
            		self.flushSalerRecord();
            		self.flushAllSalerRecord();
            	});
            	self.$notify({
            		title: "电话接通",
            		position: 'bottom-right',
            		duration : 3000,
            		message: "保存录音成功",
            		type: 'success'
            	});
            },
            appliPhone : function (cusEntity){
               	var self = this;
            	var msg = '申请查看'+cusEntity.name+'的'+cusEntity.fuzzyMobile;
            	self.$confirm(msg+',是否继续', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                  }).then(() => {
                	  self.$http.post(self.auditMobileUrl + '/applyAudit', {customerId:cusEntity.id,describe:msg})
                      .then(function (response) {
                    	  if (response.data.httpCode == 200) {
                    		  this.$message({
                                  type: 'success',
                                  message: '申请成功!'
                                });
                    	  } else {
                    		  this.$message({
                                  type: 'warning',
                                  message: '申请失败!'
                                });
                    	  }
                      });
                    
                  }).catch(() => {
                    this.$message({
                      type: 'info',
                      message: '已取消申请'
                    });          
                  });
            },
        }
    });