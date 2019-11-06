	/**
	*未接电话记录表管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"aladdinpbx/noanswercall",
            //是否显示详情窗口
            detailFormVisible: false,
            //查询参数
            searchParams: {nasStarttime:''},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            //当前操作的实体（编辑/新增）
            //entity: {nasId:'',nasCallederid:'',nasExtnumber:'',nasStarttime:'',nasEndtime:'',nasLength:'',nasDeal:'',nasPhonenumber:'',nasWaitelength:'',nasAgentid:'',nasPbxip:'',nasRinglength:'',},
            //表格中选中的行的集合
            selectDatas: [],
            
            customerVisible: false,
            resourceVisible: false,
            resourceEntity : {name:'',fromInfo:'',wechatNo:'',mobile:'',birthday:'',sex:'',duty:'',remark:''},
            //市场部服务记录实体（详情）
            salerRecordEntity: {customerId:'',customerName:'',customerMobile:'',serverRecord:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',remark:'',createTime:''},
            //通话记录实体
            salerRecordEntity1: {id:'',customerId:'',customerName:'',customerMobile:'',serverRecord:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',},
            
            activeName: 'record1',
            //父页面方法的引用
              topdial : top.window.dial,
              topdrop : top.window.drop,
              showSaveButton : true,
              callDisable: false,
              isFirstSaveRecord : false,
              
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
                
              //签单实体（详情）
                applyEntity: {customerId:'',customerName:'',wechatNo:'',mobile:'',policyNo:'',fromInfo:'',belong:'',insuranceId:'',insuranceName:'',isLongTerm:'',amount:'',amountTerm:'',introducerId:'',salerName:'',managerName:'',directorName:'',ministerName:'',isNewResource:'',isNewClub:'0',policyholderName:'',policyholderIdentifyNumber:'',policyholderAge:'',policyholderSex:'',policyholderRegion:'',policyholderProvince:'',policyholderCity:'',policyholderProfession:'',relation:'',insurederName:'',insurederIdentifyNumber:'',insurederAge:'',insurederSex:'',companyId:'',companyName:'',upgraderId:'',upgradeManagerId:'',upgradeDirectorId:'',upgraderName:'',upgradeManagerName:'',upgradeDirectorName:'',isUpgrade:'',upgradeNum:'',type:'2',isFirst:'0',submitCode:'',insuranceLimit:''},
                //客服服务记录实体（详情）
                serverRecordMobileEntity: {customerId:'',customerName:'',customerMobile:'',serveResult:'',serverRecord:'',serveTime:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',callTime:'',remark:'',createTime:''},
                //退款实体（详情）
                refundEntity: {customerId:'',customerName:'',refundMoney:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',remark:'',createTime:''},
                //理赔实体（详情）
                compensationEntity: {customerId:'',customerName:'',refundMoney:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',remark:'',createTime:''},
                //市场部服务记录实体（详情）
                //salerRecordEntity: {customerId:'',customerName:'',customerMobile:'',serveResult:'',serverRecord:'',serveTime:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',callTime:'',remark:'',createTime:''},

                
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
                    orderBy: "id",
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
                this.$http.put(this.sourceUrl+'/read/list/customer', params)
                        .then(function (response) {
                            this.pageData = response.data;
                        });
            },
            //表格记录选择事件
            selectionChange: function (selection) {
                var self = this;
                self.selectDatas = selection;
            },
            dayFormat : function (val) {
            	this.searchParams.nasStarttime = val;
            },
            isClub : function (val) {
            	if (val == 1) {
					return true;
				} else {
					return false;
				}
            },
            isResource : function (val) {
            	if (val == 0) {
            		return true;
            	} else {
            		return false;
            	}
            },
            showResourceDetails : function (customerId) {
            	var self = this;
                self.$http.put('crm/resource/read/details', {id: customerId})
                        .then(function (response) {
                        	var result = response.data.data;
                        	self.resourceEntity = result.resource;
                        	self.resourceEntity.lastCallTime = '';
                        	self.salerRecordEntity = result.salerRecord;
                        	self.resourceVisible = true;
                        });
            },
            showCustomerDetails : function (customerId) {
            	var self = this;
            	self.$http.put('crm/customer/read/serverDetails', {id: customerId})
                .then(function (response) {
                	var result = response.data.data;
                	self.resourceEntity = result.customer;
                	self.applyEntity = result.apply;
                	self.salerRecordEntity = result.salerRecord;
                	self.serverRecordMobileEntity = result.serverRecordMobile;
                	self.refundEntity = result.refund;
                	self.compensationEntity = result.compensation;
                    self.customerVisible = true;
                });
            },
          //修改资源保存
            updateResourceSave : function () {
            	var self = this;
            	self.$http.post('crm/resource', self.resourceEntity)
    			.then(function (response) {
    				var result = response.data.data;
    				self.resourceEntity = result;//所有保存，添加此行，以避免重复提交
    			});
            },
            //修改会员保存
            updateCustomerSave : function () {
            	var self = this;
            	self.resourceEntity.submitCode = '';
        		self.resourceEntity.upgradeSubmitCode = '';
        		self.resourceEntity.qq = '';
        		self.resourceEntity.phone = '';
        		self.resourceEntity.wechatNo = '';
        		self.resourceEntity.mobile = '';
        		
        		self.$http.post('crm/customer', self.resourceEntity)
    			.then(function (response) {
    				var result = response.data.data;
    				self.resourceEntity = result;//所有保存，添加此行，以避免重复提交
    			});
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
		    	self.salerRecordEntity1 = {id:'',customerId:'',customerName:'',customerMobile:'',serverRecord:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:''};
            	if (self.resourceEntity.mobile && typeof(self.resourceEntity.mobile) != "undefined" && self.resourceEntity.mobile.length == 11) {
            		var user = JSON.parse(sessionStorage.getItem("user"));
                	if (user.agentNo && typeof(user.agentNo) != "undefined" && user.agentNo != "" && user.agentNo > 0) {
                		if (self.resourceEntity.province.indexOf("武汉") >= 0 || self.resourceEntity.city.indexOf("武汉") >= 0) {
                			//开始拨号
                			self.topdial(self.resourceEntity.mobile);
    					} else {
    						//加“0”拨号
                			self.topdial("0" + self.resourceEntity.mobile);
    					}
    			    	//客户信息
    			    	self.salerRecordEntity1.customerId = self.resourceEntity.id;
    			    	self.salerRecordEntity1.customerName = self.resourceEntity.name;
    			    	self.salerRecordEntity1.customerMobile = self.resourceEntity.mobile;
    			    	
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
            		self.resourceVisible = false;
            	}
            },
          //刷新市场通话记录
            flushSalerRecord: function () {
            	var self = this;
                this.$http.put('crm/salerRecord/read/list/customer', {customerId: self.resourceEntity.id})
                .then(function (response) {
                	self.salerRecordEntity = response.data.data;
                });
            },
          //刷新客服通话记录
            flushServerRecord: function () {
            	var self = this;
                this.$http.put('crm/serverRecordMobile/read/list/customer', {customerId: self.resourceEntity.id})
                .then(function (response) {
                	self.serverRecordMobileEntity = response.data.data;
                });
            },
            //移动流程
            moveResource : function (resourceEntityId,flowId) {
            	var self = this;
            	if (flowId == 901) {
            		self.$confirm('您确定要丢弃这个资源到共享池吗?', '提示', {
            			confirmButtonText: '确定',
            	        cancelButtonText: '取消',
            	        type: 'warning'
                    }).then(function() {
                    	self.resourceEntity = {};
                    	self.resourceEntity.id = resourceEntityId;
                    	self.resourceEntity.flowId = flowId;
                    	self.$http.post('crm/resource/moveResource', self.resourceEntity)
                    	.then(function (response) {
                    		var result = response.data.data;
            				self.resourceEntity = result;//所有保存，添加此行，以避免重复提交
            				self.readList();
                    	});
                    }).catch(function() {
                    	self.$message({
                            type: 'info',
                            message: '取消丢弃'
                          });
                    });
				} else {
					self.resourceEntity = {};
                	self.resourceEntity.id = resourceEntityId;
                	self.resourceEntity.flowId = flowId;
                	self.$http.post('crm/resource/moveResource', self.resourceEntity)
                	.then(function (response) {
                		var result = response.data.data;
        				self.resourceEntity = result;//所有保存，添加此行，以避免重复提交
        				self.readList();
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
					return "";
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
            alarm : function () {
				var self = this;
				self.alarmEntity = {id:'',resourceId:'',summary:'',content:'',alarmTime:''};
				self.$http.put("crm/alarm/read/queryList", {"resourceId":self.resourceEntity.id})
                .then(function (response) {
                	self.alarmList = response.data.data;
                	self.alarmVisible = true;
                });
			},
			alarmSave : function () {
				var self = this;
				self.alarmEntity.resourceId = self.resourceEntity.id;
				self.alarmEntity.summary = "["+self.resourceEntity.name+"："+self.resourceEntity.mobile+"]";
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
			alarmDel : function (alarmEntityId) {
				var self = this;
				self.$http.delete("crm/alarm", {body: {"ids":alarmEntityId}})
                .then(function (response) {
    				self.alarm();
                });
			},
			
        }
    });