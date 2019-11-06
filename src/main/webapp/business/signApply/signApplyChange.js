	/**
	*管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"crm/signApply",
        	//是否显示编辑窗口
            editFormVisible: false,
            //查询参数
            searchParams: {customerName:'',policyNo:'',insuranceId:'',mobile:'',wechatNo:'',subordinate:'',fromInfo:'',signStatus:'',startTime:'',endTime:'',auditTime1:'',auditTime2:'',isNewClub:'',isLongTerm:''},
            //订单实体
            entity : {},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            //表格中选中的行的集合
            selectDatas: [],
            rules: {
	    		name: [
	    		       	{required: true, message: '此项不可为空', trigger: 'blur'},
	    				Vali.utf8mb4Len(false,50)
	    		],
	    		policyNo: [
		   		   	   	{required: true, message: '此项不可为空', trigger: 'blur'},
	   	    	],
	   	    		insuranceId: [
	   		   		   	{required: true, message: '此项不可为空', trigger: 'blur'},
	   	    	],
	   	    		amount: [
	 	   		   	    {required: true, message: '此项不可为空', trigger: 'blur'},
	   	    	],
	   	    		amountTerm: [
	   		   		   	{required: true, message: '此项不可为空', trigger: 'blur'},
	   	    	],
	   	    		auditTime: [
	   	    		    {required: true, message: '此项不可为空', trigger: 'blur'},
	   	    	],

	   	    		policyholderName: [
	   	    	   		{required: true, message: '此项不可为空', trigger: 'blur'},
	   	    	],
	   	    		policyholderIdentifyNumber: [
	   	    		  	{required: true, message: '此项不可为空', trigger: 'blur'},
	   	    	],
	   	    		policyholderRegion: [
	   	    		   	{required: true, message: '此项不可为空', trigger: 'blur'},
	   	    	],
	   	    		policyholderProfession: [
	   	    			{required: true, message: '此项不可为空', trigger: 'blur'},
	   	    	],
	   	    		relation: [
	   		   	    	{required: true, message: '此项不可为空', trigger: 'blur'},
	   	    	],
	   	    		insurederName: [
	   	    		   	{required: true, message: '此项不可为空', trigger: 'blur'},
	   	    	],
	   	    		insurederIdentifyNumber: [
	   	    		   	{required: true, message: '此项不可为空', trigger: 'blur'},
	   	    	],
	   	    		companyId: [
	   	    		    {required: true, message: '此项不可为空', trigger: 'blur'},
	   	    	],
	   	    		insuranceLimit: [
	   	    		   	{required: true, message: '此项不可为空', trigger: 'blur'},
	   	    	],
	   	    		sex: [
	  	   	    		{required: true, message: '此项不可为空', trigger: 'blur'},
	   	    	],
	   	    		age: [
	  	   	    		{required: true, message: '此项不可为空', trigger: 'blur'},
	   	    	],
	   	    		/*word : [
	   	    		{required: true, message: '此项不可为空', trigger: 'blur'},
	    		],*/
	    		//
	    		isAdmin : false,
            },
        },
        created: function () {
            var self = this;
            self.readList();
            self.adminPermission();
        },
        methods: {
            //查询
            readList: function () {
                var params = {
                    asc: false,
                    orderBy: "id",
                    signStatus : "5",
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
            adminPermission : function () {
            	var self = this;
            	//只有经理可用
            	var user = JSON.parse(sessionStorage.getItem("user"));
            	var userGroup = 0;
            	if (user && user != undefined && user != "") {
            		userGroup = user.userGroup;
				}
            	if (userGroup != null && userGroup == 10000000) {
            		self.isAdmin = true;
            	}
            },
            dayFormat : function (val) {
            	this.entity.auditTime = val;
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
          //表格记录选择事件
            selectionChange: function (selection) {
                var self = this;
                self.selectDatas = selection;
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
                    	if (self.entity.word && self.entity.word > 0) {
                    		self.$http.post(self.sourceUrl + '/contracter/change', self.entity)
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
	                            message: '请向管理员索要口令',
	                            type: 'error'
	                        });
						}
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
            	console.log(entityIds);
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
                	console.log(params);
                    self.$http.post(self.sourceUrl + '/contracter/del', {"ids": params.ids})
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
            //批量删险
            delBatch: function () {
                var self = this;
                if (self.selectDatas.length == 0) {
                    this.$alert('请选择要删除的记录', '提示', {
                        confirmButtonText: '确定',
                        callback: function(action) {
                            //if ("confirm"==action){
                        }
                    });
                }else{
                    var ids = new Array();
                    for(var i in self.selectDatas){
                        ids.push(self.selectDatas[i].id);
                    }
                    self.del(ids);
                }

            },
            //验证签单消息提交码
            checkSubmitCode : function (code) {
            	var self = this;
            	if (code) {//不为空时
                    self.$http.put(self.sourceUrl + '/check/submitCode', {"submitCode":code})
                    .then(function (response) {
                    	var verify = response.data;
                    	console.log(verify.data);
                    	//如果是唯一的，就自动填写
                    	if (verify.data == true) {
                    		self.entity.submitCode = code;
                    		self.$notify({
                    			type: 'success',
                        		title: "校验成功",
                        		message: "签单消息提交码验证成功"
                        	});
                    	} else {
                    		self.entity.submitCode = "";
                    		self.notify("校验失败","签单消息提交码错误！");
                    	}
                    });
                }
            },
            //提示语工具
            notify : function (title,text) {
            	this.$notify.error({
            		title: title,
            		message: text
            	});
            },
            //获取口令
            getWord : function () {
            	var self = this;
            	self.$http.put(self.sourceUrl + '/contracter/word')
            	.then(function (response) {
            		//
            	});
            }
        }
    });