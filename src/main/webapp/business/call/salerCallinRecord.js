	/**
	*市场部来电记录表管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"crm/salerCallinRecord",
            //查询参数
            searchParams: {mobile:'',salerId:'',salerName:'',managerId:'',managerName:'',directorId:'',directorName:'',ministerId:'',ministerName:'',callTime:'',isThrough:'',ringLength:'',callLength:'',throughId:'',throughName:'',throughRecord:'',},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            //当前操作的实体（编辑/新增）
            entity: {mobile:'',salerId:'',salerName:'',managerId:'',managerName:'',directorId:'',directorName:'',ministerId:'',ministerName:'',callTime:'',isThrough:'',ringLength:'',callLength:'',throughId:'',throughName:'',throughRecord:'',},
            //表格中选中的行的集合
            selectDatas: [],
            //表单校验规则 api:https://github.com/yiminghe/async-validator
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
                this.$http.put(this.sourceUrl+'/read/list', params)
                        .then(function (response) {
                            this.pageData = response.data;
                        });
            },
            //表格记录选择事件
            selectionChange: function (selection) {
                var self = this;
                self.selectDatas = selection;
            },
            dayFormat : function (val) {
            	this.searchParams.callTime = val;
            },
            tableRowClassName : function (row, index) {
                if ("1" == row.isThrough) {
                  return 'info-row';
                }
                return '';
            },
        }
    });