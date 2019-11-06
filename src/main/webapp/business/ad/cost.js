	/**
	*管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"ad/cost",
        	channelUrl:"ad/channelCost",
        	// 渠道dic
        	dicUrl:"sys/dic",
            //是否显示编辑窗口
            editFormVisible1: false,
            editFormVisible2: false,
            //是否显示详情窗口
            detailFormVisible: false,
            //查询参数
            searchParams: {costTime:''},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            //当前操作的实体（编辑/新增）
            entity: {costTime:'',channel:'',executer:'',cost:'',costId:'',oldCost:''},
            // 费用明细
            channels:{},
            //表格中选中的行的集合
            selectDatas: [],
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
                this.editFormVisible1 = true;
                for (var item in this.entity) {
                    this.entity[item] = "";
                }
            },
            //弹出编辑窗口
            showEditDialog: function (row, event) {
            	var entityId = row.id;
            	var params = {
                        asc: false,
                        orderBy: "id",
                        costId: entityId,
                        pageNum: 1,
                        pageSize: 100
                    };
                this.$http.put(this.channelUrl+'/read/list', params)
                    .then(function (response) {
                        this.channels = response.data.data;
                        for(var i in this.channels) {
                        	this.channels[i].oldCost = this.channels[i].cost;
                        	this.channels[i].cost = Math.round(this.channels[i].cost);
                        }
                        this.editFormVisible2 = true;
                });
            },
          //保存
            save: function () {
                var self = this;
                self.$refs["editForm"].validate(function(valid){
                    if (valid) {//校验通过
                    	
                    	if (self.entity.costTime instanceof Date) {
                    		self.entity.costTime = self.entity.costTime.Format('yyyy-MM-dd');
                    	} 
                    	
                        self.$http.post(self.channelUrl, self.entity)
                                .then(function (response) {
                                    var result = response.data.data;
                                    self.editFormVisible1 = false;
                                    if (parseFloat(self.entity.cost) != parseFloat(result.totalCost)){//修改
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
            saveRow: function(row) {
            	var self = this;
            	self.$http.post(self.channelUrl, row)
                .then(function (response) {
                	row.oldCost = row.cost;
                    var result = response.data.data;
                    for (var i = 0; i < self.pageData.data.length; i++) {
                        if (self.pageData.data[i].id === result.id) {
                            Vue.set(self.pageData.data, i, result);
                            break;
                        }
                    }
                });
            },
            //删除
            delRow: function (id, costId) {
                var params = {id:id};
                var self = this;
                self.$confirm('您确定要删除这条记录吗?, 删除将无法恢复!', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(function () {
                    self.$http.delete(self.channelUrl, {body: params})
                            .then(function (response) {
                            	var result = response.data.data;
                            	if (result) {
                            		// 主表更新
                            		for (var i = 0; i < self.pageData.data.length; i++) {
                                        if (self.pageData.data[i].id === result.id) {
                                            Vue.set(self.pageData.data, i, result);
                                            break;
                                        }
                                    }
                            		// 明细列表更新
                            		for(var i in self.channels){
                                        if (self.channels[i].id == id){
                                            self.channels.splice(i,1);
                                            break;
                                        }
                                    }
                            		
                            	} else {
                                    for(var i in self.pageData.data){
                                        if (self.pageData.data[i].id == costId){
                                            self.pageData.data.splice(i,1);
                                            break;
                                        }
                                    }
                                    self.editFormVisible2 = false;
                            	}
                            });
                }).catch(function(){});
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
            }
        }
    });