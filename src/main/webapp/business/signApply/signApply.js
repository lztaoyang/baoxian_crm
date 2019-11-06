	/**
	*管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"crm/signApply",
            //查询参数
            searchParams: {customerName:'',policyNo:'',insuranceId:'',insureNum:'',managerId:'',mobile:'',wechatNo:'',subordinate:'',fromInfo:'',signStatus:'',startTime:'',endTime:'',auditTime1:'',auditTime2:'',isNewClub:'',isLongTerm:''},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            
            //附件
            enclosurePath:[],
            //本页面window的引用
            contrastVisible : false,
            contrastData : "",
           
            detailFormVisible : false,
            activeName: 'apply1',
            //当前操作的实体（编辑/新增）
            customerEntity: {name:'',flowId:'',policyholderId:'',duty:'',birthday:'',province:'',city:'',insureNum:'',insureMoney:'',introduceNum:'',labels:'',currentApplyId:'',isHg:'',isService:'',salerId:'',managerId:'',directorId:'',ministerId:'',salerName:'',managerName:'',directorName:'',contracterId:'',contracterName:'',ministerName:'',serverId:'',serverName:'',oldApplyId:'',oldIsHg:'',oldIsService:'',oldServerId:'',oldServerName:'',contracterRemark:'',remark:'',createTime:''},
            //签单实体（详情）
            applyEntity: {customerId:'',customerName:'',policyNo:'',belong:'',insuranceId:'',insuranceName:'',introducerId:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',remark:'',createTime:''},
            //市场部服务记录实体（详情）
            salerRecordEntity: {customerId:'',customerName:'',customerMobile:'',serveResult:'',serverRecord:'',serveTime:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',callTime:'',remark:'',createTime:''},
            //表格中选中的行的集合
            selectDatas: [],
            //表单校验规则 api:https://github.com/yiminghe/async-validator
            rules: {
	    		name: [
	    		Vali.utf8mb4Len(false,50)
	    	],
            },
            
            notServer : true,
          //修改订单服务日期
            changeVisible:false,
            entity : {id:'',customerId:'',startDate:'',endDate:''},
        },
        created: function () {
            var self = this;
            var user = JSON.parse(sessionStorage.getItem("user"));
        	var userGroup = 0;
        	if (user && user != undefined && user != "") {
        		userGroup = user.userGroup;
			}
        	if (userGroup != null && userGroup == 88002008) {
        		self.notServer = false;
        	}
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
                this.$http.put(this.sourceUrl+'/contracter/list', params)
                        .then(function (response) {
                            this.pageData = response.data;
                        });
            },
            dayFormat1 : function (val) {
            	this.searchParams.startTime = val;
            },
            dayFormat2 : function (val) {
            	this.searchParams.endTime = val;
            },
            dayFormat3 : function (val) {
            	this.searchParams.auditTime1 = val;
            },
            dayFormat4 : function (val) {
            	this.searchParams.auditTime2 = val;
            },
            tableRowClassName : function (row, index) {
            	 if ( 1 == row.isRefund) {
                 	return 'refund-row';
                 }
            	if ("2" == row.signStatus || "4" == row.signStatus) {
                  return 'bh-row';
                } else if ("5" == row.signStatus) {
                  return 'success-row';
                }
                return '';
            },
          //详情
            readDetail: function (row, event) {
            	if (row.signStatus == "5") {
            		this.$http.put('crm/customer/read/openDetails', {id: row.customerId})
                    .then(function (response) {
                        this.customerEntity = response.data.data.customer;
                        this.applyEntity = response.data.data.apply;
                        this.salerRecordEntity = response.data.data.salerRecord;
                        
                        this.detailFormVisible = true;
                    });
				} else {
					this.$alert("非签单成功订单，无客户详情");
				}
            },
            
            //修改订单服务日期
            changeServer : function (id){
            	var self = this;
        		this.$http.put('crm/signApply/read/detail', {'id': id})
                .then(function (response) {
                	self.entity = response.data.data;
                	self.changeVisible = true;
                });
            },
            //修改订单服务日期
            changeSave : function (){
            	var self = this;
            	this.$http.post('crm/signApply/changeServer', {'id':self.entity.id,'customerId':self.entity.customerId,'startDate':self.entity.startDate,'endDate':self.entity.endDate})
            	.then(function (response) {
            		self.entity = response.data.data;
            		self.readList();
            		self.changeVisible = false;
            	});
            },
            isAduit : function (signStatus){
            	if( signStatus == 5){
            		return true;
            	}
            	return false;
            }
        }
    });