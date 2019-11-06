	/**
	*管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"crm/customerRefund",
        	userUrl:"sys/user",
            //是否显示编辑窗口
            editFormVisible: false,
            //是否显示详情窗口
            detailFormVisible: false,
            refundFormVisible: false,
            //查询参数
            searchParams: {customerId:'',customerName:'',refundMoney:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            //当前操作的实体（编辑/新增）
            entity: {customerId:'',customerName:'',refundMoney:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',nextStemp:''},
            //当前操作的实体（编辑/新增）
            customerEntity: {name:'',flowId:'',policyholderId:'',duty:'',birthday:'',province:'',city:'',insureNum:'',insureMoney:'',introduceNum:'',labels:'',currentApplyId:'',isHg:'',isService:'',salerId:'',managerId:'',directorId:'',ministerId:'',salerName:'',managerName:'',directorName:'',contracterId:'',contracterName:'',ministerName:'',serverId:'',serverName:'',oldApplyId:'',oldIsHg:'',oldIsService:'',oldServerId:'',oldServerName:'',contracterRemark:'',remark:'',createTime:''},
            //签单实体（详情）
            applyEntity: {customerId:'',customerName:'',policyNo:'',belong:'',insuranceId:'',insuranceName:'',introducerId:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',remark:'',createTime:''},
            //服务记录实体（详情）
            recordEntity: {customerId:'',customerName:'',customerMobile:'',serveResult:'',serverRecord:'',serveTime:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',callTime:'',remark:'',createTime:''},
            //保单实体（详情）
            policyEntity: {insuranceProductId:'',insuranceProductName:'',policyNo:'',guaranteeStartTime:'',guaranteeEndTime:'',guaranteeTerm:'',guaranteeLimit:'',policyholderId:'',policyholderName:'',policyholderIdentifyType:'',policyholderIdentifyNumber:'',policyholderMobile:'',policyholderMobile1:'',policyholderEmail:'',insuranceRelationship:'',insurederName:'',insurederIdentifyType:'',insurederIdentifyNumber:'',insurederMobile:'',beneficiaryType:'',effectiveStatus:'',remark:'',createTime:''},
            //投诉实体（详情）
            complainEntity: {customerId:'',customerName:'',type:'',hannel:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',remark:'',createTime:''},
            //退款实体（详情）
            refundEntity: {customerId:'',customerName:'',refundMoney:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',remark:'',createTime:''},
            //理赔实体（详情）
            compensationEntity: {customerId:'',customerName:'',refundMoney:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',remark:'',createTime:''},
            //退保实体（详情）
            refundEntity1: {customerId:'',signApplyId:'',belong:'',companyName:'', customerName:'',dealerResult:'',remark:'',createTime:'',insuranceName:'',applyTime:'',amount:'',policyNo:'',dealerId:'',dealerName:'',updateTime:''},
          //通话记录实体
            serverRecordMobileEntity1: {customerId:'',customerName:'',customerMobile:'',serverRecord:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',},
          //市场部服务记录实体（详情）
            serverRecordMobileEntity: {customerId:'',customerName:'',customerMobile:'',serveResult:'',serverRecord:'',serveTime:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',callTime:'',remark:'',createTime:''},
          //客服服务记录实体（详情）
            serverRecordEntity: {customerId:'',customerName:'',customerMobile:'',serveResult:'',serverRecord:'',serveTime:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',callTime:'',remark:'',createTime:''},
            //父页面方法的引用
            topdial : top.window.dial,
            topdrop : top.window.drop,
            showSaveButton : true,
            callDisable: false,
            activeName: 'record2',
         // 客服人员
            users:[],
            YESORNO:{"0":"否","1":"是"},
            NEXTSTEP:{"1":"重新开发","0":"禁止开发"},
            satisfactions:[{key:"1",lable:"满意"},{key:"2",lable:"一般"},{key:"3",lable:"不满意"}],
            //表单校验规则 api:https://github.com/yiminghe/async-validator
            rules: {
	    		customerId: [
	    		Vali.long()
	    	],
	    		customerName: [
	    		Vali.utf8mb4Len(false,50)
	    	],
	    		refundMoney: [
	    		Vali.double()
	    	],
	    		dealerId: [
	    		Vali.long()
	    	],
	    		dealerName: [
	    		Vali.utf8mb4Len(false,50)
	    	],
	    		dealerStatus: [
	    	],
	    		dealerResult: [
	    		Vali.utf8mb4Len(false,500)
	    	],
	    	dealerName: [
	    	    {required: true, message: '此项不可为空', trigger: 'blur'},
	    	],
	    	refundType: [
	    	    {required: true, message: '此项不可为空', trigger: 'blur'},
	    	],
	    	isSuccess: [
	    	    {required: true, message: '此项不可为空', trigger: 'blur'},
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
                    orderBy: "id",
                    dealerStatus : 0,
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
                this.$http.put(this.sourceUrl+'/read/list', params)
                        .then(function (response) {
                            this.pageData = response.data;
                        });
            },
            //详情
            readDetail: function (customerId) {
                this.$http.put('crm/customer/read/serverDetails', {id: customerId})
                        .then(function (response) {
                            this.customerEntity = response.data.data.customer;
                            this.applyEntity = response.data.data.apply;
                            this.serverRecordEntity = response.data.data.serverRecord;
                            this.serverRecordMobileEntity = response.data.data.serverRecordMobile;
                            this.detailFormVisible = true;
                        });
            },
            readRefund: function (row, event) {
            	var self = this;
                this.$http.put('/crm/customerRefund/read/refund', {id: row.id})
                        .then(function (response) {
                        	self.refundEntity1 = response.data.data;
                        	self.refundEntity1.insuranceName = response.data.data.signApply.insuranceName;
                        	self.refundEntity1.applyTime = response.data.data.signApply.createTime;
                        	self.refundEntity1.amount = response.data.data.signApply.amount;
                        	self.refundEntity1.policyNo = response.data.data.signApply.policyNo;
                        	self.refundFormVisible = true;
                        });
            },
            //弹出编辑窗口
            showEditDialog: function (entityId) {
            	
            	// 客服人员是否加载，如果未加载则先初始化客服数据
            	if (!this.users || this.users.length == 0) {
            		var params = {
            				locked:'0',
            				userGroup3:"true",
                        pageNum: 1,
                        pageSize: 500
                    };
            		this.$http.put(this.userUrl+'/read/user', params)
                            .then(function (response) {
                            	this.users = response.data.data;
                            });
            	}
            	
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
                    	self.entity.dealerStatus = 1;
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
            //处理（0待处理，2驳回，5处理中）
            deal: function (entityId,entityName,status) {
            	var self = this;
            	if(entityId != null && entityId == ""){
            		self.$alert('无法获取当前行ID');
            	}else{
            		self.$confirm('您确定要处理['+entityName+']的退款吗？', '提示', {
            			confirmButtonText: '确定',
            			cancelButtonText: '取消',
            			type: 'warning'
            		}).then(function () {
            			self.entity = {};
                    	self.entity.id = entityId;
                    	self.entity.dealerStatus = status;
                        self.$http.post(self.sourceUrl, self.entity)
                                .then(function (response) {
                                    var result = response.data.data;
                                    self.editFormVisible = false;
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
          //详情页内拨打电话
            detailsCall : function () {
            	var self = this;
            	//锁定拨打按钮
		    	self.callDisable = true;
            	if (self.entity.mobile && typeof(self.entity.mobile) != "undefined" && self.entity.mobile.length == 11) {
            		var user = JSON.parse(sessionStorage.getItem("user"));
                	if (user.agentNo && typeof(user.agentNo) != "undefined" && user.agentNo != "" && user.agentNo > 0) {
                		if (self.entity.province.indexOf("武汉") >= 0 || self.entity.city.indexOf("武汉") >= 0) {
                			//开始拨号
                			self.topdial(self.entity.mobile);
    					} else {
    						//加“0”拨号
                			self.topdial("0" + self.entity.mobile);
    					}
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
                this.$http.put('crm/serverRecordMobile/read/list/customer', {customerId: self.customerEntity.id})
                .then(function (response) {
                	self.serverRecordMobileEntity = response.data.data;
                });
            },
            onChange:function(v){
            	this.users.map((s,index)=>{
                    if(s.id===v){
                      this.entity.dealerName = this.users[index].userName;
                    }
                });
            },
            fmtSatisfaction:function(v) {
            	if (v == 1) {
            		return "满意";
            	} else if (v == 2) {
            		return "一般";
            	} else if (v == 3) {
            		return "不满意";
            	} else {
            		return "";
            	}
            },
            // 是否退保成功
            isSuccessChange:function(v){
            	// 选择否
            	if (v == 0) {
            		this.entity.refundType = "0";
            		this.rules.refundType = [];
            	} else {
            		this.entity.nextStemp = '1';
            		this.rules.refundType = [{required: true, message: '此项不可为空', trigger: 'blur'}];
            	}
            }
        }
    });