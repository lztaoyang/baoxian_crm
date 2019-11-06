	/**
	 * 管理
	 */
	var d = new Date();  
	var dateTime = d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate() + ' ' + d.getHours() + ':' + d.getMinutes() + ':' + d.getSeconds(); 
	var data = dateTime.substring(0, 10);
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"t/resourceMobileExport",
            // 是否显示编辑窗口
            editFormVisible: false,
            // 是否显示详情窗口
            detailFormVisible: false,
            // 查询参数
            searchParams: {channel:'',flowId:['301','501','999'],startDate:'2018-01-01',endDate:data},
            //流程下拉数据
            flowOptions: [{value: '101',label: '推广客户'},{value: '201',label: '未添加客户'},{value: '301',label: '可聊客户'}, {value: '501',label: '意向客户'}, {value: '999',label: '成交客户'},],
            // 分页数据,其中包含分页信息与数据列表
            pageData: {},
         // 设置日期快捷方式
            pickerDate :{
	                disabledDate(time) {
	                  return time.getTime() > Date.now();
	                },
	                shortcuts: [{
	                  text: '今天',
	                  onClick(picker) {
	                    picker.$emit('pick', new Date());
	                  }
	                }, {
	                  text: '昨天',
	                  onClick(picker) {
	                    var date = new Date();
	                    date.setTime(date.getTime() - 3600 * 1000 * 24);
	                    picker.$emit('pick', date);
	                  }
	                }, {
	                  text: '一周前',
	                  onClick(picker) {
	                    var date = new Date();
	                    date.setTime(date.getTime() - 3600 * 1000 * 24 * 7);
	                    picker.$emit('pick', date);
	                  }
	                }]
	      },
        },
        methods: {
            // 查询
            readList: function () {
                var params = {
                    asc: false,
                    click:"query",
                    orderBy: "id",
                    pageNum: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalCurrentPage : 1),
                    pageSize: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalPageSize : 10)
                };
                // 将查询条件合并到请求参数中
                for (var item in this.searchParams) {
                	if (typeof this.searchParams[item] == 'boolean'){
                        params[item] = this.searchParams[item];
                    }else if (this.searchParams[item] != "") {
                        params[item] = this.searchParams[item];
                    }
                }
                
              var flowid = params.flowId;
              var flowids = '';
              if(flowid != null && flowid != '' && flowid != undefined){
              	params.flowId = flowids;
              	for(var id in flowid) {
              		flowids += flowid[id] +',';
                  }
              	params.flowId = flowids.substring(0,flowids.length-1);
              }
              
              /*var chan = params.channel;
              if(chan == null || chan == '' || chan == undefined){
            	  this.$message({
                      showClose: false,
                      message: '推广渠道不能为空',
                      duration:2000,
                      type: 'error'
                    });
              }else{*/
            	  this.$http.post(this.sourceUrl+'/queryMobileNum', params)
                  .then(function (response) {
                      this.pageData = response.data;
                  });
             // }
            },
            //导出
            exportExcel: function () {
                    // 将查询条件合并到请求参数中
                var pars = '';
                    for (var item in this.searchParams) {
                    	if (typeof this.searchParams[item] == 'boolean'){
                    		pars += "&"+item+"="+this.searchParams[item];
                        }else if (this.searchParams[item] != "") {
                        	pars += "&"+item+"="+this.searchParams[item];
                        }
                    }
                    /*var chan = this.searchParams.channel;
                    if(chan == null || chan == '' || chan == undefined){
                  	  this.$message({
                            showClose: false,
                            message: '推广渠道不能为空',
                            duration:2000,
                            type: 'error'
                          });
                    }else{*/
                    	var str = pars.substring(1,pars.length);
                    	window.open(encodeURI('/t/resourceMobileExport/exportExcel?'+str));
                    //}
                },
                dayFormat1 : function (val) {
                	var slef = this;
                	slef.searchParams.startDate = val;
                },
                dayFormat2 : function (val) {
                	var slef = this;
                	slef.searchParams.endDate = val;
                },
        },
    });