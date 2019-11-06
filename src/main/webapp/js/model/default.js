	/**
	*管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	logOutUrl:	"/sys/logout",
        	userUrl:	"sys/user/read/current",
        	changePasswordUrl:	"sys/user/update/password",
        	performanceUrl: "crm/signApply/performance/list",
        	user:{},
        	performance : {},
        	//是否显示密码修改窗口
            isBusinessMan:false,
            searchUrl:"crm/customer/search/list",
            searchResourceUrl:"crm/resource/search/list",
            searchVisible : false,
            searchResourceVisible : false,
            searchParams: {wechatNo:'',mobile:'',name:''},
            searchResourceParams: {mobile:'',name:''},
            //全局搜索分页数据,其中包含分页信息与数据列表
            searchPageData: {},
            searchResourcePageData: {},
            userEntity : {id:'',password:'',avatar:'',agentNo:'',phone:'',dingId:''},
            userRules: {
	    		password: [
	    			Vali.utf8mb4Len(false,20)
	    		],
	    		phone: [
	    			//{required: true, message: '此项不可为空', trigger: 'blur'},
	    			Vali.utf8mb4Len(false,20)
	    		]
            },
            existAgentNo: false,
            dingIdPngVisible : false,
        },
        created: function () {
            var self = this;
            self.readUser();
            //self.readList();
        },
        methods: {
        	//系统退出（返回登录页）
        	logout:function(){
                var self = this;
                self.$confirm('您确定是要退出系统吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(function () {
                    self.$http.get(self.logOutUrl)
                            .then(function (response) {
                                window.location.href = '/s/login.html';
                            });
                }).catch(function () {
                });
            },
            //获取个人业绩
            /*readList : function () {
            	var self = this;
                self.$http.put(self.performanceUrl,{"id":"1000"})
                        .then(function (response) {
                            self.performance = response.data.data;
                            //self.performance.week = response.data.data.week;
                            //self.performance.month = response.data.data.month;
                        });
            },*/
            //获取当前用户信息
            readUser:function(){
                var self = this;
                self.$http.put(self.userUrl,{"id":"1000"})
                        .then(function (response) {
                            self.user = response.data.data;
                            if (self.user.agentNo > 0) {
                            	self.existAgentNo = true;
                            }
                            self.userEntity.id = self.user.id;
                            self.userEntity.dingId = self.user.dingId;
                            if (self.user.userGroup == 10000000 
                            		|| self.user.userGroup == 88002001
                            		|| self.user.userGroup == 88002002
                            		|| self.user.userGroup == 88002003
                            		|| self.user.userGroup == 88002005) {
                            	self.isBusinessMan = true;
							}
                            //全局变量【web存储】，已移至index.html页面处理
                            //sessionStorage.setItem("user", JSON.stringify(response.data.data));
                        });
            },
            //修改密码
            change : function () {
                var self = this;
            	if (self.userEntity) {
	                self.$confirm('您确定要修改吗?', '提示', {
	                    confirmButtonText: '确定',
	                    cancelButtonText: '取消',
	                    type: 'warning'
	                }).then(function () {
	                    self.$http.post(self.changePasswordUrl, self.userEntity)
	                          .then(function (response) {
	                          //self.$alert("修改成功");
	                          self.userEntity.password = "已更改";
	                          self.changeTimeout();
	                     });
	                }).catch(function(){});
				} else {
					self.$alert("未知错误");
            	}
        	},
        	changeTimeout : function () {
        		var self = this;
        		setTimeout( () => {
        			self.userEntity.password = "";
        		},5000);
        	},
        	//全局搜索查询
            /*searchDialog : function () {
        		var self = this;
        		self.searchVisible = true;
        	},*/
            searchList: function () {
                var params = {
                	asc: false,
                    orderBy: "enter_time",
                    pageNum: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalCurrentPage : 1),
                    pageSize: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalPageSize : 5)
                };
                //将查询条件合并到请求参数中
                for (var item in this.searchParams) {
                	if (typeof this.searchParams[item] == 'boolean'){
                        params[item] = this.searchParams[item];
                    }else if (this.searchParams[item] != "") {
                        params[item] = this.searchParams[item];
                    }
                }
                this.$http.put(this.searchUrl, params)
                        .then(function (response) {
                            this.searchPageData = response.data;
                            this.searchVisible = true;
                        });
            },
            searchResourceList: function () {
                var params = {
                	asc: false,
                    orderBy: "enter_time",
                    pageNum: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalCurrentPage : 1),
                    pageSize: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalPageSize : 5)
                };
                //将查询条件合并到请求参数中
                for (var item in this.searchResourceParams) {
                	if (typeof this.searchResourceParams[item] == 'boolean'){
                        params[item] = this.searchResourceParams[item];
                    }else if (this.searchResourceParams[item] != "") {
                        params[item] = this.searchResourceParams[item];
                    }
                }
                this.$http.put(this.searchResourceUrl, params)
                        .then(function (response) {
                            this.searchResourcePageData = response.data;
                            this.searchResourceVisible = true;
                        });
            },
            handleAvatarSuccess : function (res, file) {
            	var self = this;
                self.userEntity.avatar = "/upload/" + res.fileName[0];
                self.$http.post(self.changePasswordUrl, self.userEntity)
                .then(function (response) {
                	self.user = response.data.data;
                	self.$notify({
				          title: '上传头像',
				          message: '又是一道亮丽的风景',
				          type: 'success',
				          duration: 3000
				        });
                });
            },
            beforeAvatarUpload : function (file) {
            },
           createRelUrl: function (src) {
               if (window.URL) {
                   return window.URL.createObjectURL(src);
               } else if (window.webkitURL) {
                   return window.webkitURL.createObjectURL(src);
               } else {
                   return "";
               }  
           },
           dingIdPng : function () {
        	   this.dingIdPngVisible = true;
           }
            
        }
    });