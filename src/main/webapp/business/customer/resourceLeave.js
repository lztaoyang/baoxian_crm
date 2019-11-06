/**
	*管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"crm/resourceLeave",
            //查询参数
            searchParams: {fromInfo:'',name:'',mobile:'',createtime:'',wechatNo:'',subordinate:'',salerId:''},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            //资源分配时部门用户分页数据,其中包含分页信息与数据列表
            deptPageData: {},
            //资源分配部门人员被选中的数据
            //deptSelectData: [],
            key: "id",
            //资源分配时部门用户表字段
            tableColumns: [
               {
                   prop: "userName",
                   label: "用户名"
               },
               {
                   prop: "staffNo",
                   label: "工号"
               }],
            //当前操作的实体（编辑/新增）
            entity: {name:'',fromInfo:'',wechatNo:'',mobile:'',fuzzyMobile:'',md5Mobile:'',birthday:'',sex:'',duty:'',remark:''},
            //表格中选中的行的集合
            selectDatas: [],
            //表单校验规则 api:https://github.com/yiminghe/async-validator
////////////是否显示调配资源窗口
            allotVisible : false,
////////////市场部树
            userTreeData: [],
////////////市场部所有用户
            allUser: [],
////////////资源调配市场部选中行
            allotRow: {},
            remainExtractNum : '',
            
              
              //是否经理以上（或管理员）
              isManager : '',
              //是否总经理（或管理员）
              isMinister : '',
              salerMap : [],
              loading: false,
              
              customerDetailTitle : "资源详情（请合理移动资源，认真备注）",
              
              blacklistEntity: {id:'',flowId:'402',oldFlowId:'',blackRemark:''},
              blacklistFormVisible: false,
              addblackParams : {id:'',name:'',mobile:''},
        },
        created: function () {
            var self = this;
            self.readList();
        },
        methods: {
            //查询
            readList: function () {
            	var params = {};
            	var flow = '';
                if (document.getElementById('flow')) {
                	flow = document.getElementById('flow').value;
                }
            	if (flow == 901) {
                	this.$http.put(this.sourceUrl+'/read/remainExtractNum', params)
                		.then(function (response) {
                		this.remainExtractNum = response.data.data;
                	});
                	params = {
                        	asc: false,
                            orderBy: "loss_time",
                            pageNum: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalCurrentPage : 1),
                            pageSize: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalPageSize : 10)
                        };
				} else if (flow == 402) {
					params = {
		                	asc: false,
		                    orderBy: "update_time",
		                    pageNum: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalCurrentPage : 1),
		                    pageSize: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalPageSize : 10)
		                };
				} else {
					params = {
		                	asc: false,
		                    orderBy: "create_time",
		                    pageNum: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalCurrentPage : 1),
		                    pageSize: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalPageSize : 10)
		                };
				}
                params['flowId'] = flow;
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
                var self = this;
            	var user = JSON.parse(sessionStorage.getItem("user"));
            	var userGroup = 0;
            	if (user && user != undefined && user != "") {
            		userGroup = user.userGroup;
				}
            	if (userGroup != null && (userGroup == 88002002 ||　userGroup == 88002003 ||　userGroup == 88002005 ||　userGroup == 10000000)) {
            		self.isManager = true;
            	}
            	if (userGroup != null && (userGroup == 88002005 ||　userGroup == 10000000)) {
            		self.isMinister = true;
            	}
            },
            //表格记录选择事件
            selectionChange: function (selection) {
                var self = this;
                self.selectDatas = selection;
            },
////////////弹出调配资源窗口
            showAllotDialog: function () {
            	var self = this;
            	if ( null == self.selectDatas || self.selectDatas.length <= 0) {
            		self.$alert('请选择要分配的客户');
				} else {
					self.allotVisible = true;
					self.readAllUser();
				}
            },
////////////获取市场部树
            readAllUser: function () {
                 var self = this;
                 var params = {
                	 asc: false,
                     orderBy: "create_time",
                     pageNum: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalCurrentPage : 1),
                     pageSize: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalPageSize : 10)
                 };
                 this.$http.put('sys/dept/read/list', {"keyword" : '市场',"enable":"1","pageSize":"100"})
                     .then(function (response) {
                         self.allMenu = response.data.data;
                         var tempList = self.convertMenus2Tree(response.data.data);//this.allMenu
                         self.userTreeData = tempList;
                 });
            },
          //资源分配,市场部门节点
            treeNodeClick : function(selected){
            	this.$http.put("sys/user/read/list", {"deptId": selected.id,"enable":"1","locked":"0","pageSize":"100"})
                	.then(function (response) {
                    this.deptPageData = response.data;
                });
            },
            //发送分配请求
            allot : function(){
            	var ids = new Array();
                for(var i in this.selectDatas){
                    ids.push(this.selectDatas[i].id);
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
                var self = this;
            	if(self.allotRow.length == 0){
            		self.$alert('请选择要资源接收人');
            	}else{
            		self.$confirm('您确定要调配给['+self.allotRow.name+']吗?', '提示', {
            			confirmButtonText: '确定',
            	        cancelButtonText: '取消',
            	        type: 'warning'
                    }).then(function () {//{body: params}
                    	self.$http.post(self.sourceUrl + '/allot', {"ids":params.ids ,"userId":self.allotRow.id})
                    	.then(function (response) {
                        //self.$alert(self.selectDatas.id);
                        
                    	self.allotVisible = false;
                    	self.readList();
                    	});
                    }).catch(function() {
                    	self.$message({
                            type: 'info',
                            message: '取消分配'
                          });
                    });
            	}
            },
            convertMenus2Tree: function (menus1) {
                var temp = [], lev = 0;
                var menus = clone(menus1);
                var forFn_convertMenus2TreeItems = function (arr, menu, lev) {
                    for (var i in arr) {
                        var item = arr[i];
                        item.label = item.deptName;
                        if (item.parentId == menu.id) {
                            if (typeof(menu.children) == "undefined") {
                                menu.children = [];
                            }
                            menu.children.push(item);
                            item.lev = lev;
                            forFn_convertMenus2TreeItems(arr, item, lev + 1);
                        }
                    }
                };
                for (var j in menus) {
                    if (menus[j].parentId == "2") {
                        forFn_convertMenus2TreeItems(menus, menus[j], lev);
                        //return depts[j];
                        temp.push(menus[j])
                    }
                }
                return temp;
            },
            allotCurrentChange(val) {
                this.allotRow.id = val.id;
                this.allotRow.name = val.userName;
            },
            dayFormat : function (val) {
            	this.searchParams.createtime = val;
            },
            //提示语工具
            notify : function (title,text) {
            	this.$notify.error({
            		title: title,
            		message: text
            	});
            },
            //客户需求格式化
            demandFormat: function(v,p) {
    			if(v == null || v == '' || typeof(v) == "undefined" || v <= 0) {
    				return p;
    			} else {
    				var val = "";
    				try {
    					if (v.indexOf("key") >= 0 && v.indexOf("value") >= 0) {
    						var obj = eval("(" + v + ")");
    						for(var i in obj) {
    							val += obj[i].key + "：" + obj[i].value + "；";
    						}
    						if (p == null || p == '' || typeof(p) == "undefined" || p <= 0) {
    							return val;
    						} else {
    							return p + "，" +val;
    						}
    					}
    				} catch (e) {
    					console.log(v+"解析json异常");
    				}
    				return v;
    			}
    		},
			//到期时间格式化
			retainTimeFormat : function(v) {
				if (v == null || v == '' || typeof(v) == "undefined" || v <= 0) {
					return v;
				} else {
					var date = new Date();
					var startTime = new Date(Date.parse(v.replace(/-/g,"/"))).getTime();
				    var endTime = new Date(Date.parse(date.toString().replace(/-/g,"/"))).getTime();
				    var dates = (startTime - endTime)/(1000*60*60*24);
					return Math.round(dates) + " 天";
				}
			},
			//业务员远程搜索
			selectSaler : function (query) {
            	var self = this;
            	if (query !== '') {
            		self.loading = true;
            		setTimeout(() => {
            			self.loading = false;
            			self.$http.put('sys/user/read/list', {"userName":query,"userGroup":88002001})
                        .then(function (response) {
                            var result = response.data.data;
                            self.salerMap = result.map(item => {
                            	return {label:item.userName,value:item.id};
                            });
                        });
            		}, 200);
            	} else {
            		self.salerMap = [];
            	}
            },
            //添加到黑名单
            moveBlacklist : function (entityId) {
            	var self = this;
            	self.blacklistEntity = {id:'',flowId:'402',oldFlowId:'',blackRemark:''};
            	self.blacklistEntity.id = entityId;
            	self.blacklistEntity.flowId = "402";
            	self.blacklistEntity.oldFlowId = "";
            	self.blacklistEntity.blackRemark = "";
            	self.blacklistFormVisible = true;
            },
            //保存黑名单
            blacklistSave : function () {
            	var self = this;
            	if (self.blacklistEntity.blackRemark) {
            		self.$http.post('crm/resource/moveBlacklist', {"id": self.blacklistEntity.id,"flowId": "402","oldFlowId":self.blacklistEntity.oldFlowId,"blackRemark": self.blacklistEntity.blackRemark})
            		.then(function (response) {
            			var result = response.data.data;
            			//关闭弹窗
            			self.blacklistFormVisible = false;
            			//刷新页面
            			self.readList();
            		});
				} else {
					self.$notify({
	            		title: "提示",
	            		position: 'bottom-right',
	            		duration : 3000,
	            		message: "请填写黑名单说明",
	            		type: 'warning'
	            	});
				}
            },
            //显示黑名单对话框
            showAddBlackDialog : function () {
            	var self = this;
            	self.$http.put('crm/resource/search/list', {"id": self.addblackParams.id,"name": self.addblackParams.name,"mobile": self.addblackParams.mobile})
        		.then(function (response) {
        			var result = response.data.data;
        			console.log(result);
        			if (result) {
        				if (result.length > 1) {
        					self.$notify({
    		            		title: "搜索资源",
    		            		position: 'bottom-right',
    		            		duration : 3000,
    		            		message: "搜索到多个相关资源，请重新搜索",
    		            		type: 'warning'
    		            	});
        				} else if (result.length == 1) {
        					console.log(result);
        					console.log(result[0]);
        					self.blacklistEntity = result[0];
        					/*if (self.blacklistEntity.flowId != "801" && self.blacklistEntity.flowId != "999") {*/
        					if (self.blacklistEntity.flowId != "801" ) {
        						self.blacklistFormVisible = true;
							} else {
								self.blacklistEntity = {id:'',flowId:'402',oldFlowId:'',blackRemark:''};
								self.$notify({
	    		            		title: "搜索资源",
	    		            		position: 'bottom-right',
	    		            		duration : 3000,
	    		            		message: "待审核交资源不可添加黑名单，请重新搜索",
	    		            		type: 'error'
	    		            	});
							}
        				} else {
        					self.$notify({
    		            		title: "搜索资源",
    		            		position: 'bottom-right',
    		            		duration : 3000,
    		            		message: "未知错误，请重新搜索",
    		            		type: 'error'
    		            	});
        				}
					} else {
						self.$notify({
		            		title: "搜索资源",
		            		position: 'bottom-right',
		            		duration : 3000,
		            		message: "未搜索到相关资源，请重新搜索",
		            		type: 'error'
		            	});
					}
        		});
            },
            
            //清空并刷新数据
            flushAndReadList : function () {
            	 var self = this;
            	 self.searchParams = {fromInfo:'',name:'',mobile:'',createtime:'',wechatNo:'',subordinate:'',salerId:'',startTime:'',endTime:''};
         		 self.readList();
            },
        }
    });