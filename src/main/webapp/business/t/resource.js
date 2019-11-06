	/**
	*推广资源表管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"t/resource",
            //查询参数
            //searchParams: {tId:'',name:'',fuzzyMobile:'',content:'',planName:'',planCode:'',sourceUrl:'',refererUrl:'',userAgent:'',executer:'',createrTime:'',channel:'',ip:'',isAllot:'',assigner:'',},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            //当前操作的实体（编辑/新增）
            entity: {tId:'',name:'',md5Mobile:'',rsaMobile:'',fuzzyMobile:'',content:'',planName:'',planCode:'',sourceUrl:'',refererUrl:'',userAgent:'',executer:'',createrTime:'',channel:'',ip:''},
            //表格中选中的行的集合
            selectDatas: [],
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
   ////////////调配资源URL
               allotUrl:"t/resourceAllot/allot",
   ////////////是否显示调配资源窗口
               allotVisible : false,
   ////////////市场部树
               userTreeData: [],
   ////////////市场部所有用户
               allUser: [],
   ////////////资源调配市场部选中行
               allotRow: {},
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
                    allocat:"0",
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
                this.$http.put(this.sourceUrl+'/read/detail', {id: entityId})
                        .then(function (response) {
                            this.entity = response.data.data;
                            this.detailFormVisible = true;
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
                 this.$http.put('sys/dept/read/list', {"keyword" : '市场',"enable":"1","pageSize":"50"})
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
                    	self.$http.post(self.allotUrl, {"ids":params.ids ,"userId":self.allotRow.id})
                    	.then(function (response) {
                        var num = response.data.data;
                        self.$alert("成功分配资源： "+num+" 条");
                    	self.allotVisible = false;
                    	self.readList();
                    	});
                    }).catch(function(){
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
            contentFormat : function (v) {
            	if (v == null || v == '' || typeof(v) == "undefined" || v <= 0) {
            		return "";
            	} else {
            		var obj = eval("("+v+")");
            		var val = "";
            		for(var i in obj){
            			val += obj[i].key + "：" + obj[i].value + "；";
            		}
            		return val;
            	}
            },
            dayFormat1 : function (val) {
            	this.searchParams.startTime = val;
            },
            dayFormat2 : function (val) {
            	this.searchParams.endTime = val;
            },
            dayFormat3 : function (val) {
            	this.searchParams.allotTime1 = val;
            },
            dayFormat4 : function (val) {
            	this.searchParams.allotTime2 = val;
            },
            
        }
    });