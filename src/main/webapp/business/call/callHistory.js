	/**
	*管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"crm/salerRecord",
        	//是否显示编辑窗口
            editFormVisible: false,
            //查询参数
            searchParams: {customerId:'',customerName:'',customerMobile:'',serverRecord:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',agentNo:'',direct:'',managerId:'',startTime:'',endTime:'',startTime2:'',endTime2:'',flowId:''},
            //当前操作的实体（编辑/新增）
            entity: {customerId:'',customerName:'',customerMobile:'',serverRecord:'',timeLength:'',callFile:'',serverId:'',serverName:'',serverMobile:'',type:'',agentNo:'',direct:'',managerId:'',},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            //表格中选中的行的集合
            selectDatas: [],
            rules: {
	    		type: [
	    		Vali.utf8mb4Len(false,20),
	    		{required: true, message: '通话状态不可为空', trigger: 'blur'},
	    	],
            },
            pickerOptions1: {
                shortcuts: [{
                  text: '今天',
                  onClick(picker) {
                    picker.$emit('pick', new Date());
                  }
                }, {
                  text: '昨天',
                  onClick(picker) {
                    const date = new Date();
                    date.setTime(date.getTime() - 3600 * 1000 * 24);
                    picker.$emit('pick', date);
                  }
                }, {
                  text: '前天',
                  onClick(picker) {
                    const date = new Date();
                    date.setTime(date.getTime() - 3600 * 1000 * 24 * 2);
                    picker.$emit('pick', date);
                  }
                }]
              },
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
                    orderBy: "create_time",
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
          //弹出编辑窗口
            readEdit: function (row, event) {
                this.$http.put(this.sourceUrl+'/read/detail', {id: row.id})
                        .then(function (response) {
                            this.entity = response.data.data;
                            this.editFormVisible = true;
                        });
            },
            //保存
            save: function () {
                var self = this;
                var user = JSON.parse(sessionStorage.getItem("user"));
            	var userGroup = 0;
            	if (user && user != undefined && user != "") {
            		userGroup = user.userGroup;
				}
            	if (userGroup != null && userGroup == 10000000) {
            		self.$http.post(self.sourceUrl+'/adminUpdate', self.entity)
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
            			self.readList();
            		});
				} else {
					self.$message({
                        message: '无修改权限，请联系管理员修改',
                        type: 'warning'
                    });
                    return false;
				}
                

            },
          //计时器（小时，分钟，秒）/时间格式转换（秒）
            timerFormat : function(timeLength,callFile){
            	if (timeLength == null || timeLength == '' || typeof(timeLength) == "undefined" || timeLength <= 0) {
            		return "一";
            	} else {
            		var callFileDate = "";
            		if (callFile == null || callFile == '' || typeof(callFile) == "undefined" || callFile.length > 10) {
            			callFileDate = callFile.substring(0,8);
					}
            		var date = new Date();
					//var nowDate = date.toLocaleDateString().substring(0,10).replace(/\//g, "-");
					var year = date.getFullYear();
					var month = date.getMonth()+1;
					if (month < 10) {
						month = "0" + month;
					}
					var day = date.getDate();
					if (day < 10) {
						day = "0" + day;
					}
					var nowDate = year+""+month+""+day;
            		if (timeLength == 1 && callFileDate == nowDate) {
                		return "正在录音";
                	} else {
                		var time = parseInt(timeLength);
                	    if (null != time && "" != time) {
                	        if (time >= 60 && time < 60 * 60) {
                	            time = parseInt(time / 60.0) + "分钟" + 
                	            	parseInt((parseFloat(time / 60.0) - parseInt(time / 60.0)) * 60) + "秒";
                	        }
                	        else if (time >= 60 * 60 && time < 60 * 60 * 24) {
                	            time = parseInt(time / 3600.0) + "小时" + 
                	            	parseInt((parseFloat(time / 3600.0) -parseInt(time / 3600.0)) * 60) + "分钟" +
                	                parseInt((parseFloat((parseFloat(time / 3600.0) - parseInt(time / 3600.0)) * 60) -
                	                parseInt((parseFloat(time / 3600.0) - parseInt(time / 3600.0)) * 60)) * 60) + "秒";
                	        }
                	        else {
                	            time = parseInt(time) + "秒";
                	        }
                	    }
                	    return time;
                	}
            	}
            },
            tableRowClassName : function (row, index) {
                if (row.type == "1") {
                	return 'info-row';
                }
                return '';
            },
            dayFormat1 : function (val) {
            	this.searchParams.startTime = val;
            },
            dayFormat2 : function (val) {
            	this.searchParams.endTime = val;
            },
            dayFormat3 : function (val) {
            	this.searchParams.startTime2 = val;
            },
            dayFormat4 : function (val) {
            	this.searchParams.endTime2 = val;
            },
            
        }
    });