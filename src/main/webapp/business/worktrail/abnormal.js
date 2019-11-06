	/**
	*市场部异常工作轨迹表管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"crm/workTrailAbnormal",
            //是否显示编辑窗口
            editFormVisible: false,
            //是否显示详情窗口
            detailFormVisible: false,
            //查询参数
            searchParams: {cascaderId:'',startTime:'',endTime:'',beforeFlowId:'',afterFlowId:'',},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            options:[],
            tableExl : method1,  
            //当前操作的实体（编辑/新增）
            entity: {salerId:'',managerId:'',directorId:'',ministerId:'',customerId:'',salerRecordId:'',beforeFlowId:'',afterFlowId:'',},
            //表格中选中的行的集合
            selectDatas: [],
            //表单校验规则 api:https://github.com/yiminghe/async-validator
            rules: {}
        },
        created: function () {
            var self = this;
            var firstDate = new Date();
            self.searchParams.startTime = firstDate.Format('yyyy-MM-dd') + " 00:00";
            self.searchParams.endTime = firstDate.Format('yyyy-MM-dd hh:mm');
            self.readList();
            self.initCascader();
        },
        methods: {
            //查询
            readList: function () {
                var params = {
                    asc: false,
                    orderBy: "t.update_time",
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
                this.$http.put(this.sourceUrl+'/read/total', params)
                        .then(function (response) {
                            this.pageData = response.data;
                        });
            },
            initCascader: function(){
            	var self = this;
            	var user = JSON.parse(sessionStorage.getItem("user"));
            	var userGroup = 0;
            	if (user && user != undefined && user != "") {
            		userGroup = user.userGroup;
				}
            	if (userGroup != null && userGroup == 10000000) {
            		self.$http.post('/sys/user/queryCascader')
                    .then(function (response) {
                        self.options = response.data.data;
                    });
            	} else {
            		self.$http.post('/sys/user/queryAuthCascader')
                    .then(function (response) {
                        self.options = response.data.data;
                    });
            	}
            },
            dayFormat1 : function (val) {
            	this.searchParams.startTime = val;
            },
            dayFormat2 : function (val) {
            	this.searchParams.endTime = val;
            },
            // 选择后
            cascaderChange: function(value){
            	var self = this;
            	self.searchParams.cascaderId = value; 
            },
            //表格记录选择事件
            selectionChange: function (selection) {
                var self = this;
                self.selectDatas = selection;
            }
        }
    });