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
            searchParams: {startTime:'',endTime:'',totalCost:'',},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            //表格中选中的行的集合
            selectDatas: [],
            // 长险件均平均值
            avgLongAmount:'',
            // 短险件均平均值
            avgShortAmount:'',
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
             var firstDate = new Date();
             firstDate.setDate(1); //当月第一天
             self.searchParams.startTime = firstDate.Format('yyyy-MM-dd');
             var endDate = new Date(firstDate);
             endDate.setMonth(firstDate.getMonth()+1);
             endDate.setDate(0);// 当月最后一天
             self.searchParams.endTime = endDate.Format('yyyy-MM-dd');
             self.readList();
        },
        methods: {
            //查询
            readList: function () {
                var params = {
                    asc: false,
                    orderBy: "costTime",
                    pageNum: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalCurrentPage : 1),
                    pageSize: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalPageSize : 31)
                };
                //将查询条件合并到请求参数中
                for (var item in this.searchParams) {
                	if (typeof this.searchParams[item] == 'boolean'){
                        params[item] = this.searchParams[item];
                    }else if (this.searchParams[item] != "") {
                        params[item] = this.searchParams[item];
                    }
                }
                this.$http.put(this.sourceUrl+'/read/reportDay', params)
                        .then(function (response) {
                            this.pageData = response.data;
                           
                            var longAmts = 0;// 长险保费合计
                            var longNums = 0;// 长险单数合计
                            var shortAmts = 0;// 短险保费合计
                            var shortNums = 0;// 短险单数合计
                            // 轮询计算
                            response.data.data.forEach((row, index) => {
                            	
                            	longAmts += add(row['longAmount']);
                            	longNums += add(row['longNums']);
                            	shortAmts += add(row['shortAmount']);
                            	shortNums += add(row['shortNums']);
                            	
                            });
                            
                            function add(v) {
                            	var value = Number(v);
                        		if (!isNaN(value)) {
                        			return v;
                        		} else {
                        			return 0;
                        		}
                            }
                            // 计算长险件均平均值
                            this.avgLongAmount = Math.round((longAmts/longNums) );
                            this.avgShortAmount = Math.round((shortAmts/shortNums));
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
            getSummaries: function (param) {
            	const columns = param.columns;
            	const data = param.data;
                const sums = [];
                var promoteDay = 0;
            	columns.forEach((column, index) => {
            		if (index === 0) {
                    	sums[index] = '合计';
                    	return;
                    }
            		
            		var values = data.map(item => Number(item[column.property]));
                    if (!values.every(value => isNaN(value))) {
                    	sums[index] = values.reduce((prev, curr) => {
                    		var value = Number(curr);
                    		if (!isNaN(value)) {
                    			return Math.round(prev + curr);
                    		} else {
                    			return prev;
                    		}
                    	}, 0);
                    //sums[index] += ' 元';
                  } else {
                    sums[index] = 'N/A';
                  }
            	});
            	
            	// 资源成本平均
                if (null != sums[1] && !isNaN(sums[1]) && !isNaN(sums[2]) && sums[1] > 0 && sums[2] > 0) {
                	sums[3] = Math.round(sums[1] /sums[2] )
				} else {
					sums[3] = "N/A";
				}
                
                sums[8] =  this.avgLongAmount;
                sums[9] =  this.avgShortAmount;
            	
            	return sums;
            },
            dayFormat1 : function (val) {
            	this.searchParams.startTime = val;
            },
            dayFormat2 : function (val) {
            	this.searchParams.endTime = val;
            },
        }
    });