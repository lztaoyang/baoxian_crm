/**
 * 推广资源报表
 */
var idTmr;
var dimen = 'province';
    var vuePage = new Vue({
        el: '#app',
        data: {
            sourceUrl:"/t/resourceReport",
            // 查询参数
            searchParams: {dimension:'province',startDate:'2018-01-01',endDate:new Date(),province:'',city:'',channel:'',executer:'',sex:'',age:'',timeslot:'',orderBy:'resourceNum'},
            // 分页数据,其中包含分页信息与数据列表
            pageData: {},
            // 时段下拉框
            timeslotoptions:[{value: '00',label: '0点'}, {value: '01',label: '1点'},{value: '02',label: '2点'},{value: '03',label: '3点'},{value: '04',label: '4点'},{value: '05',label: '5点'},{value: '06',label: '6点'},{value: '07',label: '7点'},{value: '08',label: '8点'},{value: '09',label: '9点'},{value: '10',label: '10点'},{value: '11',label: '11点'},{value: '12',label: '12点'},{value: '13',label: '13点'},{value: '14',label: '14点'},{value: '15',label: '15点'},{value: '16',label: '16点'},{value: '17',label: '17点'},{value: '18',label: '18点'},{value: '19',label: '19点'},{value: '20',label: '20点'},{value: '21',label: '21点'},{value: '22',label: '22点'},{value: '23',label: '23点'}],
            //排序规则下拉框
            orderByoptions:[{value:'resourceNum',label:'资源数'},{value:'talkNum',label:'可聊数'},{value:'talkRate',label:'可聊率'},{value:'orderNum',label:'订单数'},{value:'orderRate',label:'订单率'},{value:'customerNum',label:'客户数'},{value:'customerRate',label:'客户率'},{value:'insureMoney',label:'总保费'}],
            // 设置日期快捷方式
            pickerDate :{
                disabledDate(time) {
                  return time.getTime() > Date.now();
                },
                shortcuts: [{
                  text: '今天',
                  onClick(picker) {
                    picker.$emit('pick', new Date().Format("yyyy-MM-dd"));
                  }
                }, {
                  text: '昨天',
                  onClick(picker) {
                    var date = new Date();
                    date.setTime(date.getTime() - 3600 * 1000 * 24);
                    picker.$emit('pick', date.Format("yyyy-MM-dd"));
                  }
                }, {
                  text: '一周前',
                  onClick(picker) {
                    var date = new Date();
                    date.setTime(date.getTime() - 3600 * 1000 * 24 * 7);
                    picker.$emit('pick', date.Format("yyyy-MM-dd"));
                  }
                }]
	      },
        },
        created: function () {
            var self = this;
            self.queryList();
        },
        // 执行方法
        methods: {
    	// 查询
    	  queryList: function () {
                  var params = {
                      asc: false,
                      allocat:"0",
                      pageNum: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalCurrentPage : 1),
                      pageSize: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalPageSize : 9999)
                  };
                  // 将查询条件合并到请求参数中
                  for (var item in this.searchParams) {
                  	if (typeof this.searchParams[item] == 'boolean'){
                          params[item] = this.searchParams[item];
                      }else if (this.searchParams[item] != "") {
                          params[item] = this.searchParams[item];
                      }
                  }
                  this.$http.put(this.sourceUrl+'/list', params)
                          .then(function (response) {
                        	  dimen = this.searchParams.dimension;
                              this.pageData = response.data;
                          });
              },
              dimensionFormat : function(val){//格式化维度数据之渠道，
            	  if(dimen === "channel"){
            		  if (typeof(val) != "undefined"){
                		  var dics = JSON.parse(sessionStorage.getItem("dic"));
                		  var dicCodeName;
                		  for(var dicCode in dics.RESOURCE_CHANNEL1){
                			  if (val == dicCode){
                				  dicCodeName = dics.RESOURCE_CHANNEL1[val];
                                  return dicCodeName;
                              }
                		  }
                      }else{
                          return val;
                      }
            	  }
            	  return val;
              },
              calcTalkRate : function(val,resourceNum){//计算百分比  
            	  if(val == null || val == '' || val == undefined || val == 0){
            		  return 0;
            	  }else if(resourceNum == null || resourceNum == '' || resourceNum == undefined || resourceNum== 0){
            		  return 0;
            	  }else {
            		  return (Math.round(val / resourceNum * 10000) / 100.00 + "%");// 小数点后两位百分比
            	  }
              },
              tableToExcel : function(tableid) { //整个表格拷贝到EXCEL中
          		if(getExplorer() == 'ie') {
        			var curTbl = document.getElementById(tableid);
        			var oXL = new ActiveXObject("Excel.Application");

        			//创建AX对象excel 
        			var oWB = oXL.Workbooks.Add();
        			//获取workbook对象 
        			var xlsheet = oWB.Worksheets(1);
        			//激活当前sheet 
        			var sel = document.body.createTextRange();
        			sel.moveToElementText(curTbl);
        			//把表格中的内容移到TextRange中 
        			sel.select;
        			//全选TextRange中内容 
        			sel.execCommand("Copy");
        			//复制TextRange中内容  
        			xlsheet.Paste();
        			//粘贴到活动的EXCEL中       
        			oXL.Visible = true;
        			//设置excel可见属性
        			oWB.SaveAs;
        			var fname = "";
        			try {
        				fname = oXL.Application.GetSaveAsFilename("Excel.xls", "Excel Spreadsheets (*.xls), *.xls");
        			} catch(e) {
        				print("Nested catch caught " + e);
        			} finally {
        				oWB.SaveAs(fname);

        				oWB.Close(savechanges = false);
        				//xls.visible = false;
        				oXL.Quit();
        				oXL = null;
        				//结束excel进程，退出完成
        				//window.setInterval("Cleanup();",1);
        				idTmr = window.setInterval("Cleanup();", 1);

        			}

        		} else {
        			var date = new Date();
//        			var dateString = date.toLocaleString();//获取日期与时间
        			var dics = JSON.parse(sessionStorage.getItem("dic"));
        			var dimensionval = this.searchParams.dimension;
        			var staDatev = new Date(this.searchParams.startDate);
        			var endDatev = new Date(this.searchParams.endDate);
        			tableExcel(tableid, date.Format("yyyy-MM-dd hh:mm:ss")+"导出"+dics.DIMENSION[dimensionval] +"维度" +staDatev.Format("yyyy-MM-dd") +" - "+endDatev.Format("yyyy-MM-dd") + ".xls","");
        		}
        	},
                dayFormat1 : function (val) {
                	this.searchParams.startDate = val;
                },
                dayFormat2 : function (val) {
                	this.searchParams.endDate = val;
                },
        },
    });

    function getExplorer(){
		var explorer = window.navigator.userAgent;
		//ie 
		if(explorer.indexOf("MSIE") >= 0) {
			return 'ie';
		}
		//firefox 
		else if(explorer.indexOf("Firefox") >= 0) {
			return 'Firefox';
		}
		//Chrome
		else if(explorer.indexOf("Chrome") >= 0) {
			return 'Chrome';
		}
		//Opera
		else if(explorer.indexOf("Opera") >= 0) {
			return 'Opera';
		}
		//Safari
		else if(explorer.indexOf("Safari") >= 0) {
			return 'Safari';
		}
	} 
    
	function Cleanup() {
		window.clearInterval(idTmr);
		CollectGarbage();
	}
	var tableExcel = (function() {
		var uri = 'data:application/vnd.ms-excel;base64,',
			template = '<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40"><head><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>{worksheet}</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]--></head><body><table>{table}</table></body></html>',
			base64 = function(s) {
				return window.btoa(unescape(encodeURIComponent(s)))
			},
			format = function(s, c) {
				return s.replace(/{(\w+)}/g,
					function(m, p) {
						return c[p];
					})
			}
		return function(table,fileName,sheetName) {
			var tableValue;
			if (!table.nodeType) {
				tableValue = document.getElementById(table);
			}
			var tableHtml = tableValue.innerHTML;
			var ctx = {
				worksheet: sheetName || 'Worksheet',
				table: tableHtml
			}
			document.getElementById("dlink").href = uri + base64(format(template, ctx));
            //这里是关键所在,当点击之后,设置a标签的属性,这样就可以更改标签的标题了
            document.getElementById("dlink").download = fileName;
            document.getElementById("dlink").click();
		}
	})()