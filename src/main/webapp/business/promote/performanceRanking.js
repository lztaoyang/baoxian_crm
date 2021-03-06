	/**
	*合规业绩排名
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"crm/signApply",
            //字典-来源渠道
        	key: "id",
            //查询参数
            searchParams: {startTime:'',endTime:'',fromInfo:''},
            // 列表数据
            data : {},
            // 经理缴费标题
            managerTitle:'',
            // 总监缴费标题
            directorTitle:'',	
            // 总经理缴费标题
            ministerTitle:'',
            // 累计文案
            txt:''
        },
        created: function () {
            var self = this;
            // 默认查询当天
            var date = (new Date()).Format('yyyy-MM-dd');
            self.searchParams.startTime = date;
            self.searchParams.endTime = date;
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
                this.$http.put(this.sourceUrl+'/business/list', params)
	                .then(function (response) {
	                    this.data = response.data.data;
	                    // 整理文案
	                    if (this.data) {
	                    	this.txt = date.replace('-', '年').replace('-', '月').replace('-', '年').replace('-', '月') + '日合计' + this.data.nums + '单，总缴费：' + this.data.amount + '元';
	                    }
	                });
                var date = '';
                if (this.searchParams.startTime == this.searchParams.endTime) {
                	date = this.searchParams.startTime;
                } else {
                	var start = this.searchParams.startTime;
                	if (start && typeof(start) != "undefined" && start != '') {
					} else {
						start = "";
					}
                	var end = this.searchParams.endTime;
                	if (end && typeof(end) != "undefined" && end != '') {
					} else {
						end = "";
					}
                	date = start + ' 至  ' + end;
                }
                this.managerTitle = date.replace('-', '年').replace('-', '月').replace('-', '年').replace('-', '月') + '日经理缴费排名';
                this.directorTitle = date.replace('-', '年').replace('-', '月').replace('-', '年').replace('-', '月') + '日总监缴费排名';
                this.ministerTitle = date.replace('-', '年').replace('-', '月').replace('-', '年').replace('-', '月') + '日总经理缴费排名';
            },
            dayFormat1 : function (val) {
            	this.searchParams.startTime = val;
            },
            dayFormat2 : function (val) {
            	this.searchParams.endTime = val;
            },
        }
    });