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
            entity: {directorName:'',managerName:'',account:'',extractNum:''},
          //资源分配时部门用户分页数据,其中包含分页信息与数据列表
            key: "id",
            
            remainExtractNum : '',
            
        },
        created: function () {
            var self = this;
            self.readList();
            self.queryUpgradeRemainExtractNum();
        },
        methods: {
            //查询
            readList: function () {
                var params = {
                    asc: false,
                    locked:"0",
                    userGroup:"88002666",
                    orderBy: "user_group,parent_id,extract_num",
                    pageNum: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalCurrentPage : 1),
                    pageSize: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalPageSize : 500)
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
            //修改最大分配数
            changeNum : function (entityId,dingId,agentNo,extractNum) {
            	var self = this;
            	self.entity = {};
            	self.entity.id = entityId;
            	self.entity.dingId = dingId.replace(/(^\s*)|(\s*$)/g, "").replace(/\?/g,"");
            	self.entity.agentNo = agentNo;
            	self.entity.extractNum = extractNum;
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
            queryUpgradeRemainExtractNum : function () {
            	this.$http.put('crm/customer/upgradeRemainExtractNum')
        		.then(function (response) {
        			this.remainExtractNum = response.data.data;
        		});
            }
            
        }
    });