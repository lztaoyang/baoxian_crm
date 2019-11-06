	/**
	*管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"crm/customerActualCot",
        	cusLogUrl:"crm/customerCotOperationLog",
        	teacherUrl:"crm/teacherDirectiveCot",
        	teacherLogUrl:"crm/customerActualCot",
        	//是否显示编辑窗口
            editFormVisible: false,
            //是否显示详情窗口
            detailFormVisible: false,
            //客户实盘操作记录窗口
            readCusRecordDetailVisible : false,
            //公司虚盘操作记录窗口
            readTeaRecordDetailVisible : false,
            
            //查询参数
            searchParams: {startDate:'',endDate:'',customerAdviserRecordId:'',directiveId:'',servicerId:'',servicerName:'',customerId:'',customerName:'',stockCode:'',stockName:'',stockSourceType:'',cotNum:'',cotPrice:'',cotMoney:'',currentPrice:'',currentMoney:'',lossPatio:'',lossMoney:'',position:'',indexValue:'',updateDate:'',},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            //当前操作的实体（编辑/新增）
            entity: {customerAdviserRecordId:'',directiveId:'',servicerId:'',servicerName:'',customerId:'',customerName:'',stockCode:'',stockName:'',stockSourceType:'',cotNum:'',cotPrice:'',cotMoney:'',currentPrice:'',currentMoney:'',lossPatio:'',lossMoney:'',position:'',indexValue:'',updateDate:'',},
            //表格中选中的行的集合
            selectDatas: [],
            
            //查询参数
            searchParamsTea: {startDate:'',endDate:'',customerAdviserRecordId:'',directiveId:'',servicerId:'',servicerName:'',customerId:'',customerName:'',stockCode:'',stockName:'',stockSourceType:'',cotNum:'',cotPrice:'',cotMoney:'',currentPrice:'',currentMoney:'',lossPatio:'',lossMoney:'',position:'',indexValue:'',updateDate:'',},
            //分页数据,其中包含分页信息与数据列表
            pageDataTea: {},
            //虚盘操作的实体（编辑/新增）
            teacherEntity: {customerAdviserRecordId:'',servicerId:'',servicerName:'',customerId:'',customerName:'',stockCode:'',stockName:'',stockSourceType:'',cotNum:'',cotPrice:'',cotMoney:'',currentPrice:'',currentMoney:'',lossPatio:'',lossMoney:'',position:'',indexValue:'',updateDate:'',},
            //表单校验规则 api:https://github.com/yiminghe/async-validator
            rules: {
            	stockCode: [
                    	     {required: true, message: '此项不可为空', trigger: 'blur'},
                    	     Vali.utf8mb4Len(false,2000)
                ],
            	cotNum:[
            	     {required: true, message: '此项不可为空', trigger: 'blur'},
            	     Vali.long()
            	],
            	cotprice:[
            		{required: true, message: '此项不可为空', trigger: 'blur'},
            		Vali.double()
            	]
           },
           activeName: 'customerCot',
        },         
        created: function () {
            var self = this;
            self.readList();
            self.readTeacherList();
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
            //虚盘列表查询
            readTeacherList: function () {
            	var params = {
            			asc: false,
            			orderBy: "id",
            			pageNum: (this.$refs.entityTablePageTea ? this.$refs.entityTablePageTea.internalCurrentPage : 1),
            			pageSize: (this.$refs.entityTablePageTea ? this.$refs.entityTablePageTea.internalPageSize : 10)
            	};
            	//将查询条件合并到请求参数中
            	for (var item in this.searchParamsTea) {
            		if (typeof this.searchParamsTea[item] == 'boolean'){
            			params[item] = this.searchParamsTea[item];
            		}else if (this.searchParamsTea[item] != "") {
            			params[item] = this.searchParamsTea[item];
            		}
            	}
            	this.$http.put(this.teacherUrl+'/read/list', params)
            	.then(function (response) {
            		this.pageDataTea = response.data;
            	});
            },
            dayFormat1 : function (val) {
            	this.searchParams.startDate = val;
            },
            dayFormat2 : function (val) {
            	this.searchParams.endDate = val;
            },
            dayFormat3 : function (val) {
            	this.searchParamsTea.startDate = val;
            },
            dayFormat4 : function (val) {
            	this.searchParamsTea.endDate = val;
            },
        }
    });