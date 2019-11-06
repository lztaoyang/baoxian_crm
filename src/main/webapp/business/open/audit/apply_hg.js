	/**
	*管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"crm/signApply",
        	hgUrl : "crm/signApply/hg",
        	customerUrl:"crm/customer",
        	auditMobileUrl:"crm/auditMobile",
            //是否显示编辑窗口
            editFormVisible: false,
            //是否显示详情窗口
            detailFormVisible: false,
            //是否显示详情窗口
            resourceDetailFormVisible: false,
            //是否显示申请查看手机明文窗口
            appliPhoneFormVisible: false,
            auditEntity : {customerId:'',customerName:'',fuzzyMobile:'',describe:'',applicantId:'',applicantName:'',applyReason:'',auditReason:'',auditTime:'',auditId:'',auditName:'',status:'',},
            //查询参数
            searchParams: {customerId:'',customerName:'',wechatNo:'',mobile:'',fromInfo:'',insureNum:'',insuranceId:'',insuranceName:'',policyNo:'',amount:'',amountTerm:'',introducerId:'',salerId:'',managerId:'',directorId:'',ministerId:'',salerName:'',managerName:'',directorName:'',ministerName:'',signStatus:'',auditId:'',auditName:'',auditReason:'',auditTime:'',complianceId:'',complianceName:'',complianceReason:'',complianceTime:'',subordinate:'',},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            //当前操作的实体（编辑/新增）
            entity: {customerId:'',customerName:'',wechatNo:'',mobile:'',fromInfo:'',insureNum:'',insuranceId:'',insuranceName:'',policyNo:'',amount:'',amountTerm:'',introducerId:'',salerId:'',managerId:'',directorId:'',ministerId:'',salerName:'',managerName:'',directorName:'',ministerName:'',signStatus:'',auditId:'',auditName:'',auditReason:'',auditTime:'',complianceId:'',complianceName:'',complianceReason:'',complianceTime:'',},
            policyVisible : false,
            policyEntity1: {insuranceProductId:'',insuranceProductName:'',policyNo:'',guaranteeStartTime:'',guaranteeEndTime:'',guaranteeTerm:'',guaranteeLimit:'',policyholderId:'',policyholderName:'',policyholderIdentifyType:'',policyholderIdentifyNumber:'',policyholderMobile:'',policyholderMobile1:'',policyholderEmail:'',insuranceRelationship:'',insurederName:'',insurederIdentifyType:'',insurederIdentifyNumber:'',insurederMobile:'',beneficiaryType:'',effectiveStatus:'',fromInfo:'',remark:'',commissionPercent:'',commission:'',applyTime:''},
            //表格中选中的行的集合
            selectDatas: [],
            //表单校验规则 api:https://github.com/yiminghe/async-validator
            rules: {
            	complianceReason: [
	    		{required: true, message: '此项不可为空', trigger: 'blur'},
	    	],
            },
            customerEntity: {name:'',submitCode:'',flowId:'',policyholderId:'',duty:'',birthday:'',province:'',city:'',insureNum:'',insureMoney:'',introduceNum:'',labels:'',currentApplyId:'',isHg:'',isService:'',salerId:'',managerId:'',directorId:'',ministerId:'',salerName:'',managerName:'',directorName:'',contracterId:'',contracterName:'',ministerName:'',serverId:'',serverName:'',oldApplyId:'',oldIsHg:'',oldIsService:'',oldServerId:'',oldServerName:'',contracterRemark:'',remark:'',createTime:''},
            //签单实体（详情）
            applyEntity: {customerId:'',submitCode:'',customerName:'',policyNo:'',belong:'',insuranceId:'',insuranceName:'',introducerId:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',remark:'',createTime:''},
            //市场部服务记录实体（详情）
            salerRecordEntity: {customerId:'',customerName:'',customerMobile:'',serveResult:'',serverRecord:'',serveTime:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',callTime:'',remark:'',createTime:''},
            //市场部服务记录实体（详情）
            salerRecordEntity2: {customerId:'',customerName:'',customerMobile:'',serveResult:'',serverRecord:'',serveTime:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',callTime:'',remark:'',createTime:''},
            activeName: 'apply1',
            //首次成交客户待合规双击详情
            resourceEntity : {name:'',flowId:'',submitCode:'',policyholderId:'',duty:'',birthday:'',province:'',city:'',insureNum:'',insureMoney:'',introduceNum:'',labels:'',currentApplyId:'',isHg:'',isService:'',salerId:'',managerId:'',directorId:'',ministerId:'',salerName:'',managerName:'',directorName:'',contracterId:'',contracterName:'',ministerName:'',serverId:'',serverName:'',oldApplyId:'',oldIsHg:'',oldIsService:'',oldServerId:'',oldServerName:'',contracterRemark:'',remark:'',createTime:''},
            
            //通话记录实体
            serverRecordMobileEntity1: {customerId:'',customerName:'',customerMobile:'',serverRecord:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',testTimeLength:'',testUpdateTime:'',},
            //通话记录实体
            serverRecordMobileEntity2: {customerId:'',customerName:'',customerMobile:'',serverRecord:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',testTimeLength:'',testUpdateTime:'',},
            
            //市场部服务通话记录实体（详情）
            serverRecordMobileEntity: {customerId:'',customerName:'',customerMobile:'',serveResult:'',serverRecord:'',serveTime:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',callTime:'',remark:'',createTime:''},
            
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
                    orderBy: "create_time",
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
                this.$http.put(this.sourceUrl+'/checkPending/hg/contracter/list/nonsort', params)
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
            	var self = this;
            	if (null != row.insureNum && row.insureNum > 1) {
            		self.$http.put(self.customerUrl+'/read/openDetails', {id: row.customerId})
                    .then(function (response) {
                        self.customerEntity = response.data.data.customer;
                        self.applyEntity = response.data.data.apply;
                        self.salerRecordEntity = response.data.data.salerRecord1;//市场部电话记录
                        self.salerRecordEntity2 = response.data.data.serverRecordMobile;//客服部电话记录
                        self.detailFormVisible = true;
                    });
				} else {
					//self.$alert("当前为首次申请签单，无客户详情");
					self.$http.put('crm/resource/read/resourceDetails', {id: row.customerId})
                    .then(function (response) {
                        self.resourceEntity = response.data.data.resource;
                        self.salerRecordEntity = response.data.data.salerRecord1;//市场部电话记录
                        self.salerRecordEntity2 = response.data.data.serverRecordMobile;//客服部电话记录
                        self.resourceDetailFormVisible = true;
                    });
				}
            },
            //弹出编辑窗口
            showEditDialog: function (entityId) {
                this.$http.put(this.sourceUrl+'/read/detail', {id: entityId})
                        .then(function (response) {
                            this.entity = response.data.data;
                            this.editFormVisible = true;
                        });
            },
            //合规不通过
            hgbh: function () {
                var self = this;
                self.$refs["editForm"].validate(function(valid){
                    if (valid) {//校验通过
                        self.$http.post(self.sourceUrl+'/hgbh', self.entity)
                                .then(function (response) {
                                    var result = response.data.data;
                                    self.editFormVisible = false;
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
            //表格记录选择事件
            selectionChange: function (selection) {
                var self = this;
                self.selectDatas = selection;
            },
            //合规通过
            dealHg: function (entityId,entityName,isHg) {
            	var self = this;
            	if(entityId != null && entityId == ""){
            		self.$alert('无法获取当前行ID');
            	}else{
            		self.$confirm('您确定要处理合规['+entityName+']吗？', '提示', {
            			confirmButtonText: '确定',
            	        cancelButtonText: '取消',
            	        type: 'warning'
                    }).then(function () {
                    	self.$http.post(self.hgUrl, {"ids":entityId,"isHg":isHg})
                    	.then(function (response) {
                    		self.readList();
                    	});
                    }).catch(function(){
                    	self.$message({
                            type: 'info',
                            message: '取消驳回'
                          });
                    });
            	}
            },
            showPolicyDialog : function (row, event) {
            	var self = this;
            	self.$http.put('bd/policy/read/detail', {"id": row.policyNo})
                .then(function (response) {
                	self.policyEntity1 = response.data.data;  
                	self.policyVisible = true;
                });
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
            // 合规详细页保存（年龄）
            saveDetail3: function () {
            	var self = this;
            	if (self.resourceEntity.age) {//校验通过
            		self.$http.post('crm/resource', {id:self.resourceEntity.id, age:self.resourceEntity.age})
            		.then(function (response) {
            			var result = response.data.data;
            			self.editFormVisible = false;
            			if (self.resourceEntity.id) {//修改
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
            // 合规详细页保存（性别）
            saveDetail4: function () {
            	var self = this;
            	if (self.resourceEntity.sex) {//校验通过
            		self.$http.post('crm/resource', {id:self.resourceEntity.id, sex:self.resourceEntity.sex})
            		.then(function (response) {
            			var result = response.data.data;
            			self.editFormVisible = false;
            			if (self.resourceEntity.id) {//修改
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
							self.serverRecordMobileEntity1.timeLength = parseInt(self.serverRecordMobileEntity1.timeLength) + parseInt(1);
	            			//修改状态为已接通
	            			self.serverRecordMobileEntity1.type = "1";
	            			
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
            
            //resource详情页内拨打电话
            detailsCallRescource : function () {
            	var self = this;
            	//锁定拨打按钮
		    	self.callDisable = true;
            	if (self.resourceEntity.mobile && typeof(self.resourceEntity.mobile) != "undefined" && self.resourceEntity.mobile.length == 11) {
            		var user = JSON.parse(sessionStorage.getItem("user"));
                	if (user.agentNo && typeof(user.agentNo) != "undefined" && user.agentNo != "" && user.agentNo > 0) {
                		self.topdial(self.resourceEntity.mobile);
    			    	//客户信息
    			    	self.serverRecordMobileEntity1.customerId = self.resourceEntity.id;
    			    	self.serverRecordMobileEntity1.customerName = self.resourceEntity.name;
    			    	self.serverRecordMobileEntity1.customerMobile = self.resourceEntity.mobile;
    			    	
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
            //customer详情页内拨打电话
            detailsCall : function () {
            	var self = this;
            	//锁定拨打按钮
            	self.callDisable = true;
            	if (self.customerEntity.mobile && typeof(self.customerEntity.mobile) != "undefined" && self.customerEntity.mobile.length == 11) {
            		var user = JSON.parse(sessionStorage.getItem("user"));
            		if (user.agentNo && typeof(user.agentNo) != "undefined" && user.agentNo != "" && user.agentNo > 0) {
            			self.topdial(self.customerEntity.mobile);
            			//客户信息
            			self.serverRecordMobileEntity1.customerId = self.customerEntity.id;
            			self.serverRecordMobileEntity1.customerName = self.customerEntity.name;
            			self.serverRecordMobileEntity1.customerMobile = self.customerEntity.mobile;
            			
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
            //reource详情页保存电话服务记录
            detailsSaveRecordResource : function () {
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
							self.flushAllSalerRecordResource();
							self.flushSalerRecordResource();
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
            				self.flushSalerRecord();
            				self.flushAllSalerRecord();
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
            		self.resourceDetailFormVisible = false;
            	}
            },
            //刷新客服部通话记录
            flushAllSalerRecord: function () {
            	var self = this;
            	this.$http.put('crm/serverRecordMobile/read/list/customer', {customerId: self.customerEntity.id})
            	.then(function (response) {
            		self.salerRecordEntity2 = response.data.data;
            	});
            },
            //刷新市场部通话记录
            flushSalerRecord: function () {
            	var self = this;
            	this.$http.put('crm/salerRecord/read/list/customer', {customerId: self.customerEntity.id})
            	.then(function (response) {
            		self.salerRecordEntity = response.data.data;
            	});
            },
            //刷新客服部通话记录
            flushAllSalerRecordResource: function () {
            	var self = this;
            	this.$http.put('crm/serverRecordMobile/read/list/customer', {customerId: self.resourceEntity.id})
            	.then(function (response) {
            		self.salerRecordEntity2 = response.data.data;
            	});
            },
            //刷新市场部通话记录
            flushSalerRecordResource: function () {
            	var self = this;
            	this.$http.put('crm/salerRecord/read/list/customer', {customerId: self.resourceEntity.id})
            	.then(function (response) {
            		self.salerRecordEntity = response.data.data;
            	});
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
            		flushAllSalerRecordResource;
            		flushSalerRecordResource;
            		flushSalerRecord;
            		flushAllSalerRecord;
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