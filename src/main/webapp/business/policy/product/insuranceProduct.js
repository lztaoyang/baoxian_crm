	/**
	*管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"bd/insuranceProduct",
        	//字典-产品名称
        	product : "PRODUCT",
            yesorno1:"YESORNO",
            yesorno2:"YESORNO",
            //是否显示编辑窗口
            editFormVisible: false,
            //是否显示详情窗口
            orderVisible: false,
            //
            orderPageData : {},
            orderEntity : {customerId:'',customerName:'',insuranceId:'',insuranceName:'',amount:'',},
            editOrderFormVisible : false,
            //查询参数
            searchParams: {name:'',code:'',type:'',isPutaway:''},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            //当前操作的实体（编辑/新增）
            entity: {name:'',code:'',type:'',isPutaway:''},
            //表格中选中的行的集合
            selectDatas: [],
            //表单校验规则 api:https://github.com/yiminghe/async-validator
            rules: {
	    		name: [
	    		{required: true, message: '此项不可为空', trigger: 'blur'},
	    		Vali.utf8mb4Len(false,100)
	    	],
	    		code: [
	    		{required: true, message: '此项不可为空', trigger: 'blur'},
	    		Vali.utf8mb4Len(false, 20)
	    	],
	    		type: [
	    		{required: true, message: '此项不可为空', trigger: 'blur'},
	    		//Vali.long()
	    	],
	    		isPutaway: [
	    	    {required: true, message: '此项不可为空', trigger: 'blur'},
	    	    //Vali.long()
	    	],
            },
            insuranceId : "",
            insuranceName : "",
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
            //弹出新增窗口
            showAddDialog: function () {
                this.editFormVisible = true;
                for (var item in this.entity) {
                    this.entity[item] = "";
                }
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
            //删除
            del: function (entityIds) {
                var params = {};
                if (entityIds) {
                    //删除单个实体
                    if (entityIds instanceof Array) {
                        params.ids = entityIds;
                    } else {//批量删除
                        params.ids = [entityIds];
                    }
                }
                var self = this;
                self.$confirm('您确定要删除'+params.ids.length+'条记录吗?, 删除将无法恢复!', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(function () {
                    self.$http.delete(self.sourceUrl, {body: params})
                            .then(function (response) {
                                var tempIndexArray = [];
                                //同步更新界面中的表格
                                for(var j in params.ids){
                                    for(var i in self.pageData.data){
                                        if (self.pageData.data[i].id == params.ids[j]){
                                            self.pageData.data.splice(i,1);
                                            break;
                                        }
                                    }
                                }
                                console.log(response);
                                //self.entity = response.data.data;
                            });
                }).catch(function(){});
            },
            //表格记录选择事件
            selectionChange: function (selection) {
                var self = this;
                self.selectDatas = selection;
            },
            //立即重新统计
            product: function () {
                var self = this;
                self.$http.post(self.sourceUrl+"/product", null)
                .then(function (response) {
                	self.readList();
                });
            },
            tableRowClassName : function (row, index) {
                if ("1" == row.isLongTerm) {
                  return 'info-row';
                }
                return '';
            },
            //详情
            orderList: function (row, event) {
                this.$http.put('crm/signApply/contracter/list', {insuranceId: row.id, signStatus : 5,orderBy : "amount", pageSize : 10000})
                        .then(function (response) {
                            this.orderPageData = response.data;    
                            this.orderVisible = true;
                        });
            },
            //弹出编辑窗口
            showOrderEditDialog: function (entityId) {
                this.$http.put('crm/signApply/read/detail', {id: entityId})
                        .then(function (response) {
                            this.orderEntity = response.data.data;
                            this.editOrderFormVisible = true;
                        });
            },
            //保存订单
            saveOrder: function () {
                var self = this;
                self.$refs["editOrderForm"].validate(function(valid){
                    if (valid) {//校验通过
                        self.$http.post('crm/signApply', self.orderEntity)
                                .then(function (response) {
                                    var result = response.data.data;
                                    self.editOrderFormVisible = false;
                                    if (self.orderEntity.id) {//修改
                                        for (var i = 0; i < self.orderPageData.data.length; i++) {
                                            if (self.orderPageData.data[i].id === result.id) {
                                                Vue.set(self.orderPageData.data, i, result);
                                                break;
                                            }
                                        }
                                    } else {
                                        self.orderPageData.data.unshift(result);
                                    }
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
            
        }
    });