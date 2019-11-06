	/**
	*管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"crm/signApply",
            //是否显示详情窗口
            detailFormVisible: false,
            //查询参数
            searchParams: {customerId:'',customerName:'',policyNo:'',belong:'',insuranceId:'',insuranceName:'',amountTerm:'',amount:'',introducerId:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',subordinate:'',createTime:''},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            //当前操作的实体（编辑/新增）
            entity: {customerId:'',customerName:'',policyNo:'',belong:'',insuranceId:'',insuranceName:'',amountTerm:'',amount:'',introducerId:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',},
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
                    dealerStatus : "0",
                    isUpgrade : "1",
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
                this.$http.put(this.sourceUrl+'/checkPending/upgrade/list/nonsort', params)
                        .then(function (response) {
                            this.pageData = response.data;
                        });
            },
            dayFormat : function (val) {
            	this.searchParams.createTime = val;
            }
        }
    });