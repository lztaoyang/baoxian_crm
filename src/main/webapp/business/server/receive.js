	/**
	*管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"crm/customer",
            //查询参数
            searchParams: {name:'',fromInfo:'',policyholderId:'',duty:'',birthday:'',province:'',city:'',insureNum:'',insureMoney:'',introduceNum:'',labels:'',currentApplyId:'',isHg:'',isService:'',salerId:'',managerId:'',directorId:'',ministerId:'',salerName:'',managerName:'',directorName:'',contracterId:'',contracterName:'',ministerName:'',serverId:'',serverName:'',oldApplyId:'',oldIsHg:'',oldIsService:'',oldServerId:'',oldServerName:'',contracterRemark:'',subordinate:'',enterTime:''},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            //表格中选中的行的集合
            selectDatas: [],
            //单击行记录ID，NAME
            single: {},
            //表单校验规则 api:https://github.com/yiminghe/async-validator
            rules: {}
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
                    orderBy: "apply_time",
                    isService : "0",
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
                this.$http.put(this.sourceUrl+'/allot/list', params)
                        .then(function (response) {
                            this.pageData = response.data;
                        });
            },
            //表格记录选择事件
            selectionChange: function (selection) {
                var self = this;
                self.selectDatas = selection;
            },
            //批量接收
            receiveBat : function () {
            	var self = this;
            	if (self.selectDatas.length > 0) {
            		var ids = new Array();
            		var names = new Array();
            		for(var i in self.selectDatas){
            			ids.push(self.selectDatas[i].customer.id);
            			names.push(self.selectDatas[i].customer.name);
            		}
            		var params = {};
            		if (ids) {
            			//params.userId = this.allotRow.id;
            			//删除单个实体
            			if (ids instanceof Array) {
            				params.ids = ids;
            			} else {//批量删除
            				params.ids = [ids];
            			}
            		}
            		if(params.length == 0){
            			self.$alert('请选择要接收的客户');
            		}else{
            			self.$confirm('您确定要接收客户['+names+']吗？', '提示', {
            				confirmButtonText: '确定',
            				cancelButtonText: '取消',
            				type: 'warning'
            			}).then(function () {
            				self.$http.post(self.sourceUrl+"/receive", {"ids":params.ids})
            				.then(function (response) {
            					self.readList();
            				});
            			}).catch(function(){
            				self.$message({
            					type: 'info',
            					message: '取消分配'
            				});
            			});
            		}
				} else {
					self.$alert("未选择客户");
				}
            },
            //接收
            receive : function (entityId,entityName) {
            	var self = this;
            	if(entityId != null && entityId == ""){
            		self.$alert('无法获取当前行ID');
            	}else{
            		self.$confirm('您确定要接收客户['+entityName+']吗？', '提示', {
            			confirmButtonText: '确定',
            	        cancelButtonText: '取消',
            	        type: 'warning'
                    }).then(function () {
                    	self.$http.post(self.sourceUrl+"/receive", {"ids":entityId})
                    	.then(function (response) {
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
            call : function () {
            	
            },
			
        }
    });