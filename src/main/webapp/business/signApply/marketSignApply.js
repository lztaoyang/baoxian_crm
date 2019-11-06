	/**
	*管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"crm/signApply",
            //查询参数
            searchParams: {customerName:'',policyNo:'',insuranceId:'',mobile:'',wechatNo:'',subordinate:'',fromInfo:'',signStatus:'',startTime:'',endTime:''},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            entity : {id:'',submitCode:''},
            //是否显示编辑窗口
            editFormVisible: false,
            //表单校验规则 api:https://github.com/yiminghe/async-validator
            rules: {
            	submitCode: [
	    		{required: true, message: '此项不可为空', trigger: 'blur'},
	    	],
            },
            
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
                this.$http.put(this.sourceUrl+'/market/list', params)
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
            tableRowClassName : function (row, index) {
                if ("2" == row.signStatus || "4" == row.signStatus) {
                  return 'bh-row';
                } else if ("5" == row.signStatus) {
                  return 'success-row';
                }  
                if ( 1 == row.isRefund) {
                	return 'refund-row';
                }
                return '';
            },
            showAddInfoDialog : function (entityId,submitCode) {
            	var self = this;
            	var user = JSON.parse(sessionStorage.getItem("user"));
            	var userGroup = 0;
            	if (user && user != undefined && user != "") {
            		userGroup = user.userGroup;
				}
            	if (submitCode == "javascript:void(0);" || submitCode.indexOf("javascript") >= 0 || userGroup == 10000000 || userGroup == 88002007) {
            		self.$http.put(self.sourceUrl+'/read/detail', {id: entityId})
            		.then(function (response) {
            			this.entity = response.data.data;
            			this.editFormVisible = true;
            		});
				} else {
					self.$alert('当前订单已有消息提交码，不可修改');
				}
            	
            },
          //验证签单消息提交码
            checkSubmitCode : function (code) {
            	var self = this;
            	if (code) {//不为空时
            		code = code.trim();
                    self.$http.put("crm/signApply/check/submitCode", {"submitCode":code})
                    .then(function (response) {
                    	var verify = response.data;
                    	console.log(verify.data);
                    	//如果是唯一的，就自动填写
                    	if (verify.data == true) {
                    		self.entity.submitCode = code;
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
            //保存
            save: function () {
                var self = this;
                //验证消息提交码
                //self.checkSubmitCode(self.entity.submitCode);
                if (self.entity.submitCode != "" && self.entity.submitCode.length > 0) {
                	self.$refs["editForm"].validate(function(valid){
                		if (valid) {//校验通过
                			self.$http.post(self.sourceUrl + "/businessUpdate", self.entity)
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
				}

            },
            
          //清空并刷新数据
            flushAndReadList : function () {
            	 var self = this;
            	 self.searchParams = {fromInfo:'',customerName:'',mobile:'',signStatus:'',wechatNo:'',subordinate:'',salerId:'',startTime:'',endTime:''};
         		 self.readList();
            },
    }
});