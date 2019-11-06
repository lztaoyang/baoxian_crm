	/**
	*管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"crm/customerComplain",
        	complainUrl:"crm/customer",
            //是否显示编辑窗口
            editFormVisible: false,
            //是否显示详情窗口
            detailFormVisible: false,
            complainFormVisible: false,
            //查询参数
            searchParams: {customerId:'',customerName:'',type:'',hannel:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            //当前操作的实体（编辑/新增）
            entity: {customerId:'',customerName:'',type:'',hannel:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',},
            //当前操作的实体（编辑/新增）
            customerEntity: {name:'',flowId:'',policyholderId:'',duty:'',birthday:'',province:'',city:'',insureNum:'',insureMoney:'',introduceNum:'',labels:'',currentApplyId:'',isHg:'',isService:'',salerId:'',managerId:'',directorId:'',ministerId:'',salerName:'',managerName:'',directorName:'',contracterId:'',contracterName:'',ministerName:'',serverId:'',serverName:'',oldApplyId:'',oldIsHg:'',oldIsService:'',oldServerId:'',oldServerName:'',contracterRemark:'',remark:'',createTime:''},
            //签单实体（详情）
            applyEntity: {customerId:'',customerName:'',policyNo:'',belong:'',insuranceId:'',insuranceName:'',introducerId:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',remark:'',createTime:''},
            //服务记录实体（详情）
            recordEntity: {customerId:'',customerName:'',customerMobile:'',serveResult:'',serverRecord:'',serveTime:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',callTime:'',remark:'',createTime:''},
            //保单实体（详情）
            policyEntity: {insuranceProductId:'',insuranceProductName:'',policyNo:'',guaranteeStartTime:'',guaranteeEndTime:'',guaranteeTerm:'',guaranteeLimit:'',policyholderId:'',policyholderName:'',policyholderIdentifyType:'',policyholderIdentifyNumber:'',policyholderMobile:'',policyholderMobile1:'',policyholderEmail:'',insuranceRelationship:'',insurederName:'',insurederIdentifyType:'',insurederIdentifyNumber:'',insurederMobile:'',beneficiaryType:'',effectiveStatus:'',remark:'',createTime:''},
            //投诉实体（详情）
            complainEntity: {customerId:'',customerName:'',type:'',hannel:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',remark:'',createTime:''},
            //退款实体（详情）
            refundEntity: {customerId:'',customerName:'',refundMoney:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',remark:'',createTime:''},
            //理赔实体（详情）
            compensationEntity: {customerId:'',customerName:'',refundMoney:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',remark:'',createTime:''},
            //投诉实体（详情）
            complainEntity1: {customerId:'',belong:'',customerName:'',type:'',hannel:'',dealerId:'',dealerName:'',dealerStatus:'',dealerResult:'',remark:'',createTime:''},
            
            //表单校验规则 api:https://github.com/yiminghe/async-validator
            rules: {
	    		customerId: [
	    		Vali.long()
	    	],
	    		customerName: [
	    		Vali.utf8mb4Len(false,50)
	    	],
	    		type: [
	    	],
	    		hannel: [
	    		Vali.utf8mb4Len(false,100)
	    	],
	    		dealerId: [
	    		Vali.long()
	    	],
	    		dealerName: [
	    		Vali.utf8mb4Len(false,50)
	    	],
	    	dealerName: [
	    	 {required: true, message: '此项不可为空', trigger: 'blur'},
	    	],
	    		dealerStatus: [
	    	],
	    		dealerResult: [
	    		Vali.utf8mb4Len(false,500)
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
                    orderBy: "id",
                    dealerStatus : 0,
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
                this.$http.put(this.sourceUrl+'/read/list', params)
                        .then(function (response) {
                            this.pageData = response.data;
                        });
            },
            //详情
            readDetail: function (entityId) {
            	var self = this;
                this.$http.put('/crm/customer/read/details', {id: entityId})
                        .then(function (response) {
                        	self.customerEntity = response.data.data.customer;
                        	self.applyEntity = response.data.data.apply;
                        	self.recordEntity = response.data.data.record;
                        	self.policyEntity = response.data.data.policy;
                        	self.complainEntity = response.data.data.complain;
                        	self.refundEntity = response.data.data.refund;
                        	self.compensationEntity = response.data.data.compensation;
                            
                        	self.detailFormVisible = true;
                        });	
            },
            readComplain: function (row, event) {
                this.$http.put(this.sourceUrl+'/read/complain', {id: row.id})
                        .then(function (response) {
                            this.complainEntity1 = response.data.data;
                            this.complainFormVisible = true;
                        });
            },
            //弹出编辑窗口
            showEditDialog: function (entityId) {
                this.$http.put(this.sourceUrl+'/read/detail', {id: entityId})
                        .then(function (response) {
                            this.entity = response.data.data;
                            this.editFormVisible = true;
                        });
            },
            //保存
            save: function () {
                var self = this;
                self.$refs["editForm"].validate(function(valid){
                    if (valid) {//校验通过
                    	self.entity.dealerStatus = 5;
                        self.$http.post(self.sourceUrl, self.entity)
                                .then(function (response) {
                                    var result = response.data.data;
                                    self.editFormVisible = false;
                                    if (self.entity.id) {//修改
                                        for (var i = 0; i < self.pageData.data.length; i++) {
                                            if (self.pageData.data[i].id === result.id) {
                                                Vue.set(self.pageData.data, i, result);
                                                break;
                                            }
                                        }
                                    } else {
                                        self.pageData.data.unshift(result);
                                    }
                                    self.readList();
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
            //处理（0待处理，2驳回，5处理中）
            deal: function (entityId,entityName,status) {
            	var self = this;
            	if(entityId != null && entityId == ""){
            		self.$alert('无法获取当前行ID');
            	}else{
            		self.$confirm('您确定要处理['+entityName+']的投诉吗？', '提示', {
            			confirmButtonText: '确定',
            			cancelButtonText: '取消',
            			type: 'warning'
            		}).then(function () {
            			self.entity = {};
                    	self.entity.id = entityId;
                    	self.entity.dealerStatus = status;
                        self.$http.post(self.sourceUrl, self.entity)
                                .then(function (response) {
                                    var result = response.data.data;
                                    self.editFormVisible = false;
                                    self.readList();
                                });
            		}).catch(function(){
            			self.$message({
            				type: 'info',
            				message: '取消驳回'
            			});
            		});
            	}
            },
        }
    });