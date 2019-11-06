	/**
	*管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"ad/cost",
            //查询参数
            searchParams: {startTime:'',endTime:'',executer:'',fromInfo:'',flowId:'',isSign:'',smsCheck:'',phoneStatus:''},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            //表格中选中的行的集合
            selectDatas: [],
            // 点击列表行显示客户明细
            userAllotDetails: "",
            //本页面window的引用
            tableExl: method1,
            //表单校验规则 api:https://github.com/yiminghe/async-validator
            rules: {
	    		costTime: [
	    	],
	    		totalCost: [
	    		Vali.double()
	    	],
            },
            //是否资源详情窗口
            resourceVisible : false,
            //资源实体
            resourceEntity : {name:'',fromInfo:'',wechatNo:'',mobile:'',birthday:'',sex:'',duty:'',remark:''},
            //是否会员详情窗口
            customerVisible : false,
            //市场部服务记录实体（详情）
            salerRecordEntity: {customerId:'',customerName:'',customerMobile:'',serverRecord:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',remark:'',createTime:''},
            //客服部服务记录实体（详情）
          //市场部服务记录实体（详情）
            serverRecordMobileEntity: {customerId:'',customerName:'',customerMobile:'',serveResult:'',serverRecord:'',serveTime:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',callTime:'',remark:'',createTime:''},
            //客服服务记录实体（详情）
            serverRecordEntity: {customerId:'',customerName:'',customerMobile:'',serveResult:'',serverRecord:'',serveTime:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',callTime:'',remark:'',createTime:''},
            //服务记录实体（详情）
            //订单实体（详情）
            applyEntity: {customerId:'',customerName:'',wechatNo:'',mobile:'',policyNo:'',fromInfo:'',belong:'',insuranceId:'',insuranceName:'',isLongTerm:'',amount:'',amountTerm:'',introducerId:'',salerName:'',managerName:'',directorName:'',ministerName:'',isNewResource:'',isNewClub:'0',policyholderName:'',policyholderIdentifyNumber:'',policyholderAge:'',policyholderSex:'',policyholderRegion:'',policyholderProvince:'',policyholderCity:'',policyholderProfession:'',relation:'',insurederName:'',insurederIdentifyNumber:'',insurederAge:'',insurederSex:'',companyId:'',companyName:'',upgraderId:'',upgradeManagerId:'',upgradeDirectorId:'',upgraderName:'',upgradeManagerName:'',upgradeDirectorName:'',isUpgrade:'',upgradeNum:'',type:'2',isFirst:'0',submitCode:'',insuranceLimit:''},
            //退款实体（详情）
            refundEntity: {customerId:'',customerName:'',refundMoney:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',remark:'',createTime:''},
            //理赔实体（详情）
            compensationEntity: {customerId:'',customerName:'',refundMoney:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',remark:'',createTime:''},
            
            activeName: 'record2',
          
        },
        created: function () {
            var self = this;
            if (document.getElementById('flowId')) {
            	var flowId = document.getElementById('flowId').value;
            	this.searchParams["flowId"] = 999;
            }
            self.readList();
        },
        methods: {
            //查询
            readList: function () {
                var params = {
                    asc: false,
                    orderBy: "costTime",
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
                this.$http.put(this.sourceUrl+'/read/reportCustomerDetail', params)
                        .then(function (response) {
                            this.pageData = response.data;
                        });
                this.$http.put('t/resourceAllot/read/userAllotNum')
					.then(function(response) {
					this.userAllotDetails = response.data.data;
				});
            },
            readList999: function () {
            	this.searchParams["flowId"] = 999;
            	this.readList();
            },
            //表格记录选择事件
            selectionChange: function (selection) {
                var self = this;
                self.selectDatas = selection;
            },
            showDate:function(time) {
            	return time.substr(0,10);
            },
            fmtCostTime:function(val) {
            	this.searchParams.costTime = val;
            },
            fmtTxt:function(v) {
            	if (v == null || v == '' || typeof(v) == "undefined" || v <= 0) {
            		return "一";
            	} else {
            		return v;
            	}
            },
            //四舍五入
            numFormat:function(v){
            	if (v == null || v == '' || typeof(v) == "undefined" || v <= 0) {
            		return "一";
            	} else { 
            		return Math.round(v);
            	}
            },
            dayFormat1 : function (val) {
            	this.searchParams.startTime = val;
            },
            dayFormat2 : function (val) {
            	this.searchParams.endTime = val;
            },
            tableRowClassName : function (row, index) {
                if (row.insureNum > 0) {
						return 'info-row';
				} 
                return '';
            },
            // 双击事件
            /*accept : function (row, event) {
            	var self = this;
            	self.applys = {};
            	if (row.insureNum && row.insureNum > 0) {
            		var customerId = row.customerId.replace('a','');
            		// 查询客户
            		self.$http.put('/crm/customer/read/detail', {id:customerId})
                    .then(function (response) {
                    	self.entity = response.data.data;
                    	// console.log(JSON.stringify(self.entity));
                    	// 查询客户签单记录
                    	if (self.entity) {
                    		var params = {
                                asc: false,
                                orderBy: "audit_time",
                                customerId:customerId,
                                signStatus:5,
                                pageNum: 1,
                                pageSize: 100
                            };
                    		self.$http.put('/crm/signApply/read/list', params)
	                			.then(function (response) {
	                				self.applys = response.data;
	                			});
                    		self.detailFormVisible =  true;
                    	}
                    });
            	}
            },*/
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
			signRowClassName : function (row, index) {
            	if ("1" == row.isRefund) {
                	return 'refund-row';
                }
                return '';
            },
            accept : function (row, event) {
            	var self = this;
            	var customerId = row.customerId.replace('a','');
            	if (row.flowId && row.flowId == 999) {
            		self.showCustomerDetails(customerId);
            	} else {
            		self.showResourceDetails(customerId);
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
                	self.serverRecordMobileEntity = result.serverRecordMobile;
                	self.serverRecordEntity = result.serverRecord;
                	//市场部通话记录
                	self.flushSalerRecord(customerId);
                });
            },
            flushSalerRecord: function (customerId) {
            	var self = this;
                self.$http.put('crm/salerRecord/read/list/customer', {customerId: customerId})
                .then(function (response) {
                	self.salerRecordEntity = response.data.data;
                	self.customerVisible = true;
                });
            },
        }
    });