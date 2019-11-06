	/**
	*推广资源表管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"sys/user",
            //查询参数
            searchParams: {parentId:''},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            //当前操作的实体（编辑/新增）
            entity: {directorName:'',managerName:'',account:'',isAllotClub:'',clubNum:'',allotClubNum:''},
            //表格中选中的行的集合
            selectDatas: [],
            //资源分配时部门用户分页数据,其中包含分页信息与数据列表
            key: "id",
            allotCustomerMaxVisible : false,
            handleAllotCustomerMaxVisible : false,
            allotCustomerMaxValue : 0,
            
            //是否管理员
            isAdmin : false,
            //是否经理
            isManager : false,
            
            showFree:false,
            showMore:true,
            
            //修改最大分配数说明
            carm:"暂无",
            //配置表是否自动修改
            carmAuto:"0",
        },
        created: function () {
        	var self = this;
         	var user = JSON.parse(sessionStorage.getItem("user"));
         	var userGroup = 0;
         	if (user && user != undefined && user != "") {
         		userGroup = user.userGroup;
			}
         	if (userGroup != null && userGroup == 10000000) {
         		self.isAdmin = true;
         		self.$http.put(self.sourceUrl+'/queryCarm',{})
                .then(function (response) {
                	if (response.data.data) {
                		self.carm = response.data.data;
					}
                });
         	}
         	if (userGroup != null && userGroup == 88002667) {
         		self.isManager = true;
         	}
            self.readList();
        },
        methods: {
            //查询所有升级人员
            readList: function () {
                var params = {
                    asc: false,
                    locked:"0",
                    userGroup:"88002666",
                    orderBy: "user_group,parent_id,allot_club_num,club_num,is_allot_resource",
                    pageNum: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalCurrentPage : 1),
                    pageSize: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalPageSize : 1000)
                };
                //将查询条件合并到请求参数中
                for (var item in this.searchParams) {
                	if (typeof this.searchParams[item] == 'boolean'){
                        params[item] = this.searchParams[item];
                    }else if (this.searchParams[item] != "") {
                        params[item] = this.searchParams[item];
                    }
                }
                this.$http.put(this.sourceUrl+'/read/allotResource/list', params)
                	.then(function (response) {
                		this.pageData = response.data;
                });
                
            },
            //表格记录选择事件
            selectionChange: function (selection) {
                var self = this;
                self.selectDatas = selection;
            },
            isAllotFormat : function (isAllotClub) {
            	return isAllotClub=="1"?"正在分配":"暂停分配";
            },
            isAllotPlain : function (isAllotClub) {
            	return isAllotClub=="1"?false:true;
            },
            //启用停用分配
            isAllot : function (entityId,isAllotResource) {
            	var self = this;
            	if (self.isManager &&　isAllotResource == "0") {
            		self.$message({
						message: '经理只能操作关闭，不能开启',
						type: 'warning'
					});
            		return 0;
				}
            	self.entity = {};
            	self.entity.id = entityId;
            	self.entity.isAllotResource = isAllotResource=="1"?"0":"1";
            	self.$http.post(self.sourceUrl+'/updateInfo', self.entity)
                .then(function (response) {
                	var result = response.data.data;
    				self.entity = result;//所有保存，添加此行，以避免重复提交
    				self.readList();
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
            },
          //清空搜索栏并刷新数据
        	flushAndReadList : function () {
        		var self = this;
        		self.searchParams = {parentId:''};
        		self.readList();
        	},
            
        }
    });