	/**
	*电话录音记录表管理
	*/
    var vuePage = new Vue({
        el: '#app',
        data: {
        	sourceUrl:"aladdinpbx/recordlogs",
            //查询参数
            searchParams: {recId:'',recExtnumber:'',recAgentid:'',recStarttime:'',recEndtime:'',recRecordfile:'',recRecordlength:'',recCallederid:'',recRecordpath:'',recPhonenumber:'',recHangup:'',recCalltype:'',recRingwaittime:'',recArea:'',recAreacode:'',recServerip:'',recTalktype:'',recPbxip:'',},
            //分页数据,其中包含分页信息与数据列表
            pageData: {},
            //当前操作的实体（编辑/新增）
            entity: {recId:'',recExtnumber:'',recAgentid:'',recStarttime:'',recEndtime:'',recRecordfile:'',recRecordlength:'',recCallederid:'',recRecordpath:'',recPhonenumber:'',recHangup:'',recCalltype:'',recRingwaittime:'',recArea:'',recAreacode:'',recServerip:'',recTalktype:'',recPbxip:'',},
            //表格中选中的行的集合
            selectDatas: [],
        },
        created: function () {
            var self = this;
            self.readList();
        },
        methods: {
            //查询
            readList: function () {
                var params = {
                    desc: true,
                    orderBy: "REC_ENDTIME",
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
                this.$http.put(this.sourceUrl+'/read/answerList', params)
                        .then(function (response) {
                            this.pageData = response.data;
                        });
            },
            dayFormat : function (val) {
            	this.searchParams.recStarttime = val;
            },
          //计时器（小时，分钟，秒）/时间格式转换（秒）
            timerFormat : function(timeLength,recRecordpath){
            	if (timeLength == null || timeLength == '' || typeof(timeLength) == "undefined" || timeLength <= 0) {
            		return "一";
            	} else {
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
            		if (timeLength == 1 && recRecordpath == nowDate) {
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
            fptFormat : function (path,file) {
            	if (file == null || file == '' || typeof(file) == "undefined" || file <= 0) {
            		return file;
            	} else {
            		if (file.substring(0,1) == 7) {
            			return "ftp://192.168.20.7/" + path + "/" + file;
            		} else {
            			return "ftp://192.168.0.8/" + path + "/" + file;
            		}
            	}
            },
            ftpDown : function(v,recRecordpath){
            	var ftpUrl = "";
            	if (v == null || v == '' || typeof(v) == "undefined" || v <= 0) {
            		self.$notify({
            	          title: '警告',
            	          message: '录音地址为空',
            	          type: 'warning',
            	          duration: 2000
            	        });
            	} else {
            		ftpUrl = "ftp://172.16.0.7/"+recRecordpath +"/" + v;
            		window.open(ftpUrl);
            	}
            },
        }
    });