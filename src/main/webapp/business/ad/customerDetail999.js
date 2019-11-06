	/**
	*管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"ad/cost",
            //是否显示编辑窗口
            editFormVisible: false,
            //是否显示详情窗口
            detailFormVisible: false,
            //查询参数
            searchParams: {startTime:'',endTime:'',signStartTime:'',signEndTime:'',executer:'',fromInfo:'',flowId:'',isSign:'',smsCheck:'',phoneStatus:''},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            //表格中选中的行的集合
            selectDatas: [],
            // 点击列表行显示客户明细
            entity:{},
            // 点击列表行显示客户签单记录
            applys:{},
          //本页面window的引用
            tableExl: method1,
            //表单校验规则 api:https://github.com/yiminghe/async-validator
            rules: {
	    		costTime: [
	    	],
	    		totalCost: [
	    		Vali.double()
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
                    flowId:999,
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
            dayFormat3 : function (val) {
            	this.searchParams.signStartTime = val;
            },
            dayFormat4 : function (val) {
            	this.searchParams.signEndTime = val;
            },
            tableRowClassName : function (row, index) {
                if (row.insureNum > 0) {
						return 'info-row';
				} 
                return '';
            },
            // 双击事件
            accept : function (row, event) {
            	var self = this;
            	self.applys = {};
            	if (row.insureNum && row.insureNum > 0) {
            		var customerId = row.customerId.replace('a','');
            		// 查询客户
            		self.$http.put('/crm/customer/read/details', {id:customerId})
                    .then(function (response) {
                    	self.entity = response.data.data.customer;
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
            },
            
            exportApplyDetail :function () {
            	var str = '';
            	for ( var key in this.searchParams ) {
					if (!this.searchParams[key] == '') {
						str = str + '&' + key + '=' + this.searchParams[key];
					}
				}
            	window.open(encodeURI('/ad/cost/exportApplyDetail?' + str));
            }
        }
    });