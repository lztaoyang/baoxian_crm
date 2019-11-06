	/**
	*推广资源表管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"sys/user",
            //查询参数
            searchParams: {parentId:'',parentId2:'',isAllotResource:''},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            //当前操作的实体（编辑/新增）
            entity: {directorName:'',managerName:'',account:'',isAllotResource:'',allotResourceMax:'',allotResourceNum:'',controlAllotResourceNum:'',rewardResourceNum:'',overRewardResourceNum:''},
            //表格中选中的行的集合
            selectDatas: [],
            //资源分配时部门用户分页数据,其中包含分页信息与数据列表
            key: "id",
            allotResourceMaxVisible : false,
            allotResourceMaxValue : 0,
            
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
         	if (userGroup != null && userGroup == 88002002) {
         		self.isManager = true;
         	}
            self.readList();
            self.queryCarmType();
        },
        methods: {
            //查询
            readList: function () {
                var params = {
                    asc: false,
                    locked:"0",
                    //userGroup2:"88002001,88002002",
                    userGroup:"88002001",
                    orderBy: "user_group,parent_id,is_allot_resource,allot_resource_num",
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
            isAllotFormat : function (isAllotResource) {
            	return isAllotResource=="1"?"正在分配":"暂停分配";
            },
            isAllotPlain : function (isAllotResource) {
            	return isAllotResource=="1"?false:true;
            },
            //修改最大分配数
            changeNum : function (entityId,dingId,agentNo,allotResourceMax,controlAllotResourceNum) {
            	var self = this;
            	if (allotResourceMax >= 1 && allotResourceMax <= 25) {
                	self.entity = {};
                	self.entity.id = entityId;
                	self.entity.dingId = dingId.replace(/(^\s*)|(\s*$)/g, "").replace(/\?/g,"");
                	self.entity.agentNo = agentNo;
                	self.entity.allotResourceMax = allotResourceMax;
                	self.entity.controlAllotResourceNum = controlAllotResourceNum;
                	self.$http.post(self.sourceUrl+'/updateInfo', self.entity)
                	.then(function (response) {
                		var result = response.data.data;
                		self.entity = result;//所有保存，添加此行，以避免重复提交
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
					self.$alert("最大分配数区间：1-25");
				}
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
            disableAllotFormat : function (disableAllotResource) {
            	return disableAllotResource=="1"?"禁止分配":"允许分配";
            },
            disableAllotPlain : function (disableAllotResource) {
            	return disableAllotResource=="1"?false:true;
            },
            //启用停用分配
            disableAllot : function (entityId,disableAllotResource) {
            	var self = this;
            	if (!self.isAdmin) {
            		self.$message({
            			message: '只有管理员才能操作',
            			type: 'warning'
            		});
            		return 0;
            	}
            	self.entity = {};
            	self.entity.id = entityId;
            	self.entity.disableAllotResource = disableAllotResource=="1"?"0":"1";
            	self.$http.post(self.sourceUrl+'/updateInfo', self.entity)
            	.then(function (response) {
            		var result = response.data.data;
            		self.entity = result;//所有保存，添加此行，以避免重复提交
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
            //停止我部门推广分配
            allotResourceStop : function () {
            	var self = this;
            	var self = this;
            	self.$confirm('您确定要批量停止我部门所有推广资源分配吗?', '提示', {
        			confirmButtonText: '确定',
        	        cancelButtonText: '取消',
        	        type: 'warning'
                }).then(function() {
                	self.$http.post(self.sourceUrl+'/allotResource/stopAll')
                    .then(function (response) {
                    	self.readList();
                    });
                }).catch(function() {
                	self.$message({
                        type: 'info',
                        message: '取消批量停止操作'
                      });
                });
            },
            //修改我部门最大分配数
            allotResourceMax : function () {
            	var self = this;
            	//只有经理可用
            	var user = JSON.parse(sessionStorage.getItem("user"));
            	var userGroup = 0;
            	if (user && user != undefined && user != "") {
            		userGroup = user.userGroup;
				}
            	if (userGroup != null && (userGroup == 88002002 || userGroup == 10000000)) {
            		self.allotResourceMaxVisible = true;
            		self.showFree = false;
            		self.showMore = true;
            	} else {
            		self.$message({
						message: '只有经理才能操作',
						type: 'warning'
					});
            	}
            },
            changeAllotResourceMax : function () {
            	var self = this;
            	self.$http.post(self.sourceUrl+'/changeAllotResourceMax', {"allotResourceMaxValue" : self.allotResourceMaxValue})
                .then(function (response) {
                	self.readList();
                	self.allotResourceMaxVisible = false;
                });
            },
          //修改今日奖励资源数
            changeReward : function (entityId,rewardResourceNum) {
            	var self = this;
            	self.entity = {};
            	self.entity.id = entityId;
            	self.entity.rewardResourceNum = rewardResourceNum;
            	self.$http.post(self.sourceUrl+'/updateReward', self.entity)
            	.then(function (response) {
            		var result = response.data.data;
            		self.entity = result;//所有保存，添加此行，以避免重复提交
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
            maxValueChange : function () {
            	var self = this;
            	self.allotResourceMaxValue = parseInt(self.allotResourceMaxValue);
            },
          //是否自动修改
            queryCarmType : function () {
            	var self = this;
            	self.$http.put('sys/properties/queryByKeyString',{"keyString":"task.syncChangeTResourceMax"})
                .then(function (response) {
                	self.carmAuto = response.data.data;
                });
    			if (!self.carmAuto || null == self.carmAuto) {
    				self.$notify.error({
                        title: '错误',
                        message: '查询是否自动修改最大分配数出错'
                      });
    			}
            },
            isAutoPlain : function (v) {
            	return v=="1"?false:true;
            },
          //修改方式
            isAutoClick : function () {
            	var self = this;
            	self.$http.post('sys/properties',{"id":self.carmAuto.id,"valueString":self.carmAuto.valueString==0?"1":"0"})
                .then(function (response) {
                	self.carmAuto = response.data.data;
                });
    			if (!self.carmAuto || null == self.carmAuto) {
    				self.$notify.error({
                        title: '错误',
                        message: '查询是否自动修改最大分配数出错'
                      });
    			}
            },
            isAutoFormat : function (v) {
            	return v=="1"?"自动":"手动";
            },
            
            updateUp : function () {
            	this.$http.put('crm/customer/customerToUpgrade')
    			.then(function (response) {
    			});
            }
            
        }
    });