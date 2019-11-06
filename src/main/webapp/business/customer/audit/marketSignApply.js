	/**
	*管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"crm/signApply",
        	auditMobileUrl:"crm/auditMobile",
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
                this.$http.put(this.sourceUrl+'/checkPending/market/list/nonsort', params)
                        .then(function (response) {
                            this.pageData = response.data;
                        });
            },
            dayFormat : function (val) {
            	this.searchParams.createTime = val;
            },
          //隐藏手机号码中间4位
            hiddenMobile : function (mobile) {
            	//郑雷贤: {id:50100,deptId:907488434921119744}
            	var user = JSON.parse(sessionStorage.getItem("user"));
            	var deptId = 0;
            	if (user && user != undefined && user != "") {
            		deptId = user.deptId;
				}
            	if (deptId != null && (deptId == 907488434921119744 || deptId == 1501)) {
            		var reg = /^(\d{3})\d{4}(\d{4})$/;
            		return mobile.replace(reg, "$1****$2");
            	} else {
            		return mobile;
            	}
            },
            appliPhone : function (customerName,customerId,fuzzyMobile){
               	var self = this;
            	var msg = '申请查看'+customerName+'的'+fuzzyMobile;
            	self.$confirm(msg+',是否继续', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                  }).then(() => {
                	  self.$http.post(self.auditMobileUrl + '/applyAudit', {customerId:customerId,describe:msg})
                      .then(function (response) {
                    	  if (response.data.httpCode == 200) {
                    		  this.$message({
                                  type: 'success',
                                  message: '申请成功!'
                                });
                    	  } else {
                    		  this.$message({
                                  type: 'warning',
                                  message: '申请失败!'
                                });
                    	  }
                      });
                    
                  }).catch(() => {
                    this.$message({
                      type: 'info',
                      message: '已取消申请'
                    });          
                  });
            },
            
        }
    });