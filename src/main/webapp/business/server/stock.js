	/**
	*管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"crm/customerActualCot",
        	cusLogUrl:"crm/customerCotOperationLog",
        	teacherCotUrl:"crm/teacherDirectiveCot",
            teacherCotRecordUrl:"crm/teacherDirectiveOperationLog",
        	//是否显示编辑窗口
            editFormVisible: false,
            //是否显示详情窗口
            detailFormVisible: false,
            customerCotVisible: false,
            teacherCotVisible: false,
            //客户实盘操作记录窗口
            readCusRecordDetailVisible : false,
            //公司虚盘操作记录窗口
            readTeaRecordDetailVisible : false,
            customerAddEditFormVisible : false,
            //查询参数
            searchParams: {startDate:'',endDate:'',customerAdviserRecordId:'',directiveId:'',servicerId:'',servicerName:'',customerId:'',customerName:'',stockCode:'',stockName:'',stockSourceType:'',cotNum:'',cotPrice:'',cotMoney:'',currentPrice:'',currentMoney:'',lossPatio:'',lossMoney:'',position:'',indexValue:'',updateDate:'',},
            searchParamsCus: {startDate:'',endDate:'',customerAdviserRecordId:'',directiveId:'',servicerId:'',servicerName:'',customerId:'',customerName:'',stockCode:'',stockName:'',stockSourceType:'',cotNum:'',cotPrice:'',cotMoney:'',currentPrice:'',currentMoney:'',lossPatio:'',lossMoney:'',position:'',indexValue:'',updateDate:'',},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            pageDataCus: {},
            //当前操作的实体（编辑/新增）
            entity: {payCount:'',lossCount:'',customerAdviserRecordId:'',directiveId:'',servicerId:'',servicerName:'',customerId:'',customerName:'',stockCode:'',stockName:'',stockSourceType:'',cotNum:'',cotPrice:'',cotMoney:'',currentPrice:'',currentMoney:'',lossPatio:'',lossMoney:'',position:'',indexValue:'',updateDate:'',},
            customerCotEntity: {useMoney:'', directionType:'', customerAdviserRecordId:'',directiveId:'',servicerId:'',servicerName:'',customerId:'',customerName:'',stockCode:'',stockName:'',stockSourceType:'',cotNum:'',cotPrice:'',cotMoney:'',currentPrice:'',currentMoney:'',lossPatio:'',lossMoney:'',position:'',indexValue:'',updateDate:'',createTime:'',},
            customerCotAddEntity: {useMoney:'', directionType:0, customerAdviserRecordId:'',directiveId:'',servicerId:'',servicerName:'',customerId:'',customerName:'',stockCode:'',stockName:'',stockSourceType:'',cotNum:'',cotPrice:'',cotMoney:'',currentPrice:'',currentMoney:'',lossPatio:'',lossMoney:'',position:'',indexValue:'',updateDate:'',createTime:'',},
            //实盘操作记录实体
            customerCotRecordEntity : {customerActualCotId:'',stockCode:'',stockName:'',directionType:'',tradeNum:'',tradePrice:'',tradeMoney:'',cotPrice:'',cotMoney:'',thenLossRatio:'',thenLossMoney:'',updateDate:'',},
            //表格中选中的行的集合
            selectDatas: [],
            
            //查询参数
            searchParamsTea: {startDate:'',endDate:'',customerAdviserRecordId:'',directiveId:'',servicerId:'',servicerName:'',customerId:'',customerName:'',stockCode:'',stockName:'',stockSourceType:'',cotNum:'',cotPrice:'',cotMoney:'',currentPrice:'',currentMoney:'',lossPatio:'',lossMoney:'',position:'',indexValue:'',updateDate:'',},
            searchParamsTeaCot: {startDate:'',endDate:'',customerAdviserRecordId:'',directiveId:'',servicerId:'',servicerName:'',customerId:'',customerName:'',stockCode:'',stockName:'',stockSourceType:'',cotNum:'',cotPrice:'',cotMoney:'',currentPrice:'',currentMoney:'',lossPatio:'',lossMoney:'',position:'',indexValue:'',updateDate:'',},
            //分页数据,其中包含分页信息与数据列表
            pageDataTea: {},
            pageDataTeaCot: {},
            pageDataTeaCot: {},
            //虚盘操作的实体（编辑/新增）
            teacherEntity: {payCount:'',lossCount:'',customerAdviserRecordId:'',servicerId:'',servicerName:'',customerId:'',customerName:'',stockCode:'',stockName:'',stockSourceType:'',cotNum:'',cotPrice:'',cotMoney:'',currentPrice:'',currentMoney:'',lossPatio:'',lossMoney:'',position:'',indexValue:'',updateDate:'',},
            //虚盘操作记录实体
            teacherCotRecordEntity : {customerActualCotId:'',stockCode:'',stockName:'',directionType:'',tradeNum:'',tradePrice:'',tradeMoney:'',cotPrice:'',cotMoney:'',thenLossRatio:'',thenLossMoney:'',updateDate:'',},
            //表单校验规则 api:https://github.com/yiminghe/async-validator
            rules: {
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
           activeName: 'customerCot',
           stockCode: "",
           useMoney: "",
        },         
        created: function () {
            var self = this;
            self.readList();
            self.readTeacherList();
        },
        methods: {
            //查询
            readList: function () {
                var params = {
                    asc: false,
                    orderBy: "cot_num",
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
                this.$http.put(this.sourceUrl+'/search/list', params)
                        .then(function (response) {
                            this.pageData = response.data;
                        });
            },
            //虚盘列表查询
            readTeacherList: function () {
            	var params = {
            			asc: false,
            			orderBy: "cot_num",
            			pageNum: (this.$refs.entityTablePageTea ? this.$refs.entityTablePageTea.internalCurrentPage : 1),
            			pageSize: (this.$refs.entityTablePageTea ? this.$refs.entityTablePageTea.internalPageSize : 10)
            	};
            	//将查询条件合并到请求参数中
            	for (var item in this.searchParamsTea) {
            		if (typeof this.searchParamsTea[item] == 'boolean'){
            			params[item] = this.searchParamsTea[item];
            		}else if (this.searchParamsTea[item] != "") {
            			params[item] = this.searchParamsTea[item];
            		}
            	}
            	this.$http.put(this.teacherCotUrl+'/search/list', params)
            	.then(function (response) {
            		this.pageDataTea = response.data;
            	});
            },
            
            /**
             * 实盘持股详情
             */
            readDetail : function (row, event ) {
            	var self = this;
            	var params = {
            			asc: false,
            			orderBy: "cot_num",
            			pageNum: (this.$refs.entityTablePageCus ? this.$refs.entityTablePageCus.internalCurrentPage : 1),
            			pageSize: (this.$refs.entityTablePageCus ? this.$refs.entityTablePageCus.internalPageSize : 10)
            	};
            	
            	//将查询条件合并到请求参数中
            	if (row != '' && row != null && row !=undefined) {
            		self.stockCode = row.stockCode;
				}
            	params['stockCode'] = self.stockCode;
            	for (var item in self.searchParamsCus) {
            		if (typeof self.searchParamsCus[item] == 'boolean'){
            			params[item] = self.searchParamsCus[item];
            		}else if (self.searchParamsCus[item] != "") {
            			params[item] = self.searchParamsCus[item];
            		}
            	}
            	self.$http.put(this.sourceUrl+'/read/list', params)
            	.then(function (response) {
            		self.pageDataCus= response.data;
            		self.customerCotVisible = true;
            	});
            },
            /**
             * 刷新实盘持股列表
             */
            readCustomerCotList : function () {
            	var self = this;
            	var params = {
            			asc: false,
            			orderBy: "cot_num",
            			pageNum: (this.$refs.entityTablePageCus ? this.$refs.entityTablePageCus.internalCurrentPage : 1),
            			pageSize: (this.$refs.entityTablePageCus ? this.$refs.entityTablePageCus.internalPageSize : 10)
            	};
            	params['stockCode'] = self.stockCode;
            	for (var item in self.searchParamsCus) {
            		if (typeof self.searchParamsCus[item] == 'boolean'){
            			params[item] = self.searchParamsCus[item];
            		}else if (self.searchParamsCus[item] != "") {
            			params[item] = self.searchParamsCus[item];
            		}
            	}
            	self.$http.put(this.sourceUrl+'/read/list', params)
            	.then(function (response) {
            		self.pageDataCus= response.data;
            	});
            },
            /**
             * 刷新虚盘持股列表
             */
            readTeaList : function () {
            	var self = this;
            	var params = {
            			asc: false,
            			orderBy: "cot_num",
            			pageNum: (this.$refs.entityTablePageTeaCot ? this.$refs.entityTablePageTeaCot.internalCurrentPage : 1),
            			pageSize: (this.$refs.entityTablePageTeaCot ? this.$refs.entityTablePageTeaCot.internalPageSize : 10)
            	};
            	params['stockCode'] = self.stockCode;
            	for (var item in self.searchParamsTeaCot) {
            		if (typeof self.searchParamsTeaCot[item] == 'boolean'){
            			params[item] = self.searchParamsTeaCot[item];
            		}else if (self.searchParamsTeaCot[item] != "") {
            			params[item] = self.searchParamsTeaCot[item];
            		}
            	}
            	self.$http.put(self.teacherCotUrl+'/read/list', params)
            	.then(function (response) {
            		self.pageDataTeaCot= response.data;
            		self.teacherCotVisible = true;
            	});
            },
            
            /**
             * 虚盘持股详情
             */
            readTeaDetail : function (row, event) {
            	var self = this;
            	var params = {
            			asc: false,
            			orderBy: "cot_num",
            			pageNum: (this.$refs.entityTablePageTeaCot ? this.$refs.entityTablePageTeaCot.internalCurrentPage : 1),
            			pageSize: (this.$refs.entityTablePageTeaCot ? this.$refs.entityTablePageTeaCot.internalPageSize : 10)
            	};
            	//将查询条件合并到请求参数中
            	if (row != '' && row != null && row !=undefined) {
            		self.stockCode = row.stockCode;
				}
            	console.log("teacher======"+self.stockCode);
            	params['stockCode'] = self.stockCode;
            	for (var item in self.searchParamsTeaCot) {
            		if (typeof self.searchParamsTeaCot[item] == 'boolean'){
            			params[item] = self.searchParamsTeaCot[item];
            		}else if (self.searchParamsTeaCot[item] != "") {
            			params[item] = self.searchParamsTeaCot[item];
            		}
            	}
            	self.$http.put(self.teacherCotUrl+'/read/list', params)
            	.then(function (response) {
            		self.pageDataTeaCot= response.data;
            		self.teacherCotVisible = true;
            	});
            },
            /**
             * 实盘建仓
             */
            addStock : function (row, event) {
            	var self = this;
            	var user = JSON.parse(sessionStorage.getItem("user"));
            	var userGroup = 0;
            	if (user && user != undefined && user != "") {
            		userGroup = user.userGroup;
				}
            	if (userGroup != 88002012) {//指导老师没有操作权限
            		self.getCustomerUseMoney(row.customerId);
    				self.customerCotAddEntity.customerId = row.customerId;
    	        	self.customerCotAddEntity.customerName = row.customerName;
    	        	self.customerCotAddEntity.stockCode = row.stockCode;
    	        	self.getStocksCode(row.stockCode);
                	self.customerCotAddEntity.directionType == 0;
                	self.customerAddEditFormVisible = true;
				} 
            	
            },
            /**
             * 实盘
             */
            getCotMoney : function () {
            	var self = this;
    			var num = self.customerCotAddEntity.cotNum;
    			var price = self.customerCotAddEntity.cotPrice;
    			var currentPrice =  self.customerCotAddEntity.currentPrice;
    			if (num == 0 || price ==0) {
    				self.$message({
        				message: '请输入交易数量和成交价格',
        				type: 'warning'
        			});
    			} else if (num > 0 && price > 0) {
    				self.customerCotAddEntity.cotMoney = Math.round((num * price)*100)/100;
    				self.customerCotAddEntity.currentMoney = Math.round((num * currentPrice)*100)/100;
    			}else {
    				if (price < 0) {
    					self.customerCotAddEntity.cotPrice = '';
    				}
    				if (num < 0) {
    					self.customerCotAddEntity.cotNum = '';
    				}
    				self.$message({
        				message: '请输入交易数量和成交价格',
        				type: 'warning'
        			});
    			}
            },
          //保存实盘建仓、买入、卖出信息
            customerCotAddsave : function () {
            	var self = this;
                self.$refs["customerAddEditForm"].validate(function(valid){
                    if (valid) {//校验通过
                        self.$http.post(self.sourceUrl, self.customerCotAddEntity)
                                .then(function (response) {
                                	self.customerAddEditFormVisible = false;
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
            
            
            /**
             * 获取当前股票行情
             */
            getStocksCode : function (stockCode) {
            	if(stockCode != null && stockCode != '' && stockCode != undefined){
            		var self = this;
            		self.$http.put(this.sourceUrl+'/readPrice', {'stockCode':stockCode})
            		.then(function (response) {
            			self.customerCotAddEntity.currentPrice = response.data.data.currentPrice;
            			self.customerCotAddEntity.stockName = response.data.data.stockName;
            		}).catch(function(){
            			
            		});
            	}
            	
            },
            
            /**
             * 获取当前客户实盘可用资金
             */
             getCustomerUseMoney : function (customerId) {
            	 var self = this;
            	 if(customerId != null && customerId != '' && customerId != undefined){
                	 self.$http.put('crm/customer/read/serverDetails', {'id':customerId})
                	 	.then(function (response) {
                	 		self.customerCotAddEntity.useMoney = response.data.data.customer.useMoney;
                     }).catch(function(){
                    	 
                     });
            	 }
            	 
             },
            dayFormat1 : function (val) {
            	this.searchParams.startDate = val;
            },
            dayFormat2 : function (val) {
            	this.searchParams.endDate = val;
            },
            dayFormat3 : function (val) {
            	this.searchParamsTea.startDate = val;
            },
            dayFormat4 : function (val) {
            	this.searchParamsTea.endDate = val;
            },
        }
    });