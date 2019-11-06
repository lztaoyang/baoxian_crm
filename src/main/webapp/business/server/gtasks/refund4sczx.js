	/**
	*管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"crm/customerRefund",
        	userUrl:"sys/user",
            //是否显示详情窗口
            detailFormVisible: false,
            refundFormVisible: false,
            editFormVisible: false,
            //查询参数
            searchParams: {customerId:'',policyNo:'',customerName:'',refundMoney:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',isSuccess:'',refundType:''},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            //当前操作的实体（编辑/新增）
            entity: {customerId:'',customerName:'',refundMoney:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',},
            //当前操作的实体（编辑/新增）
            customerEntity: {name:'',flowId:'',policyholderId:'',duty:'',birthday:'',province:'',city:'',insureNum:'',insureMoney:'',introduceNum:'',labels:'',currentApplyId:'',isHg:'',isService:'',salerId:'',managerId:'',directorId:'',ministerId:'',salerName:'',managerName:'',directorName:'',contracterId:'',contracterName:'',ministerName:'',serverId:'',serverName:'',oldApplyId:'',oldIsHg:'',oldIsService:'',oldServerId:'',oldServerName:'',contracterRemark:'',remark:'',createTime:''},
            //签单实体（详情）
            applyData: {},
            activeName: 'apply1',
            users:[],
            satisfactions:[{key:"1",lable:"满意"},{key:"2",lable:"一般"},{key:"3",lable:"不满意"}],
            //表单校验规则 api:https://github.com/yiminghe/async-validator
            rules: {
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
                    orderBy: "a.update_time",
                    dealerStatus : 1,
                    'sczx':0,
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
                this.$http.put(this.sourceUrl+'/read/list4sczx', params)
                        .then(function (response) {
                            this.pageData = response.data;
                        });
            },
          //详情
            readDetail: function (row, event) {
                this.$http.put('crm/customer/read/openDetails', {id: row.customerId})
                        .then(function (response) {
                            this.customerEntity = response.data.data.customer;
                            this.applyData = response.data.data.apply;
                            this.detailFormVisible = true;
                        });
            },
            tableRowClassName : function (row, index) {
                if ('' == row.policyNo) {
                  return 'info-row';
                }
                return '';
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
            }
        }
    });