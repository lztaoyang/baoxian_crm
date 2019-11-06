/**
 *管理
 */

var dimen = 'channel';
var d = new Date();  
var dateTime = d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate() + ' ' + d.getHours() + ':' + d.getMinutes() + ':' + d.getSeconds(); 
var data = dateTime.substring(0, 10);
var vuePage = new Vue({
	el: '#app',
	data: {
		sourceUrl: "crm/customercount",
		//是否显示编辑窗口
		editFormVisible: false,
		//是否显示详情窗口
		detailFormVisible: false,
		//查询参数
		searchParams: {dimension:'channel',province:'',city:'',channel:'',executer:'',sex:'',age:'',timeslot:'',startDate:'2018-01-01',endDate:data},
		/*// 时段下拉框
        timeslotoptions:[{value: '00',label: '0点'}, {value: '01',label: '1点'},{value: '02',label: '2点'},{value: '03',label: '3点'},{value: '04',label: '4点'},{value: '05',label: '5点'},{value: '06',label: '6点'},{value: '07',label: '7点'},{value: '08',label: '8点'},{value: '09',label: '9点'},{value: '10',label: '10点'},{value: '11',label: '11点'},{value: '12',label: '12点'},{value: '13',label: '13点'},{value: '14',label: '14点'},{value: '15',label: '15点'},{value: '16',label: '16点'},{value: '17',label: '17点'},{value: '18',label: '18点'},{value: '19',label: '19点'},{value: '20',label: '20点'},{value: '21',label: '21点'},{value: '22',label: '22点'},{value: '23',label: '23点'}],
		//维度下拉
        dimensionoptions:[{value: 'channel',label: '渠道'},{value: 'executer',label: '执行人'},{value: 'timeslot',label: '时段'},{value: 'plan',label: '广告'},{value: 'age',label: '年龄'},{value: 'province',label: '省份'},{value: 'city',label: '城市'},{value: 'sex',label: '性别'},],
        */
		//分页数据,其中包含分页信息与数据列表
		pageData: {},
		//表格中选中的行的集合
		selectDatas: [],
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
	created: function () {
        var self = this;
        self.readList();
    },
	methods: {
		//查询
		readList: function() {
			var params = {
				asc: false,
				orderBy: "id",
				pageNum: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalCurrentPage : 1),
				pageSize: (this.$refs.entityTablePage ? this.$refs.entityTablePage.internalPageSize : 10)
			};
			
			
			//将查询条件合并到请求参数中
			for(var item in this.searchParams) {
				if(typeof this.searchParams[item] == 'boolean') {
					params[item] = this.searchParams[item];
				} else if(this.searchParams[item] != "") {
					params[item] = this.searchParams[item];
				}
			}
			this.$http.put(this.sourceUrl + '/read/list', params)
				.then(function(response) {
					dimen = this.searchParams.dimension;
					this.pageData = response.data;
				});
		},
		dimensionFormat : function(val){//格式化维度数据之渠道，
      	  var dics = JSON.parse(sessionStorage.getItem("dic"));
      	  
    	  if(dimen === "channel"){//渠道
    		  if(val == null || val == ''){
    			  return '非推广资源';
    		  }else if (typeof(val) != "undefined"){
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
    	  }else if(dimen === "executer"){//执行人
    		  if(val == null || val == ''){
    			  return '非推广资源';
    		  }else if (typeof(val) != "undefined"){
        		  var dicCodeName;
        		  for(var dicCode in dics.RESOURCE_OP1){
        			  if (val == dicCode){
        				  dicCodeName = dics.RESOURCE_OP1[val];
                          return dicCodeName;
                      }
        		  }
              }else{
                	return val;
              }
    	  }else if(dimen === "timeslot"){//时段
    		  if(val == null || val == ''){
    			  return '非推广资源';
    		  }else{
                	return val;
              }
    	  }else if(dimen === "plan"){//广告
    		  if(val == null || val == ''){
    			  return '非推广资源';
    		  }else{
                	return val;
              }
    	  }else if(dimen === "sex"){//执行人
    		  if (typeof(val) != "undefined"){
        		  var dicCodeName;
        		  for(var dicCode in dics.SEX){
        			  if (val == dicCode){
        				  dicCodeName = dics.SEX[val];
                          return dicCodeName;
                      }
        		  }
              }else{
                	return val;
              }
    	  }
    	  return val;
      },
      dayFormat1 : function (val) {
    	  this.searchParams.startDate = val;
      },
      dayFormat2 : function (val) {
    	  this.searchParams.endDate = val;
      },
      
      //客户统计报表历史数据处理
      synHistorySignApply : function () {
    	  this.$http.get(this.sourceUrl + '/synHistorySignApply')
      		.then(function (response) {
      });
      }
	}
});