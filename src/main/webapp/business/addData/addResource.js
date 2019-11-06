var vuePage = new Vue({
    el: '#app',
    data: {
    	sourceUrl:"crm/resource",
        //是否显示编辑窗口
        editFormVisible: false,
        //查询参数
        searchParams: {id:'',fromInfo:'',name:'',mobile:'',createtime:'',wechatNo:'',subordinate:'',salerId:''},
        //分页数据,其中包含分页信息与数据列表
        pageData: {},
        //当前操作的实体（编辑/新增）
        entity: {name:'',fromInfo:'',wechatNo:'',mobile:'',fuzzyMobile:'',md5Mobile:'',birthday:'',sex:'',duty:'',remark:'',salerId:'',idAdd:''},
        //表单校验规则 api:https://github.com/yiminghe/async-validator
        rules: {
    		name: [
    			{required: true, message: '客户姓名不可为空', trigger: 'blur'},
    		],
    		mobile: [
    				{required: true, message: '客户手机号不可为空', trigger: 'blur'},
    		],
    		fromInfo: [
     				{required: true, message: '来源渠道不可为空', trigger: 'blur'},
     		],
     		salerId: [
     				{required: true, message: '分配业务员不可为空', trigger: 'blur'},
     		],
     		planCode: [
     				{required: true, message: '推广专题不可为空', trigger: 'blur'},
     		]
        },
        
        salerMap : [],
        loading: false,
        
        demand : {insurederAge:'',policyholderSex:'',insuranceLimit:'',amountTerm:''},
        
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
                isAdd: "1",
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
                    	this.pageData = response.data.data.resourcePage;
                    });
        },
        //弹出新增窗口
        showAddDialog: function () {
            for (var item in this.entity) {
                this.entity[item] = "";
            }
            for (var item in this.demand) {
                this.demand[item] = "";
            }
            this.editFormVisible = true;
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
                	self.entity.isAdd = 1;
                	//客户需求拼接
                	self.entity.demand = self.demandSplit(self.demand);
                	if (self.entity.demand == '[]') {
                		self.entity.demand = '';
					}
                    self.$http.post(self.sourceUrl + '/addTestResource', self.entity)
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

        },
        dayFormat : function (val) {
        	this.searchParams.createtime = val;
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
        //客户需求拼接
        demandSplit : function (demand) {
        	var d = "[";
        	if (demand.insurederAge && demand.insurederAge != '') {
				d += '{"key":"客户年龄","value":"'+ demand.insurederAge +'岁"},';
	        	if (demand.policyholderSex && demand.policyholderSex != '') {
	        		if (demand.policyholderSex == '0') {
	        			var sex = "未知"; 
	        		}
	        		if (demand.policyholderSex == '1') {
	        			var sex = "男"; 
	        		}
	        		if (demand.policyholderSex == '2') {
	        			sex = "女"; 
					}
					d += '{"key":"性别","value":"'+ sex +'"},';
				}
			}
        	return d + "]";
        }
        
    }
});