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
            searchParams: {startTime:'',endTime:'',executer:'',},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            //表格中选中的行的集合
            selectDatas: [],
            //表单校验规则 api:https://github.com/yiminghe/async-validator
            rules: {}
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
                    orderBy: "amounts",
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
                this.$http.put(this.sourceUrl+'/read/executerTotal', params)
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
            tableRowClassName : function (row, index) {
                if (row.insureNum > 0) {
						return 'info-row';
				} 
                return '';
            },
        }
    });