/**
 * Created by naxj on 17/12/7.
 */
var vuePage = new Vue({
    el: '#app',
    data: {
    	translatedUrl : "http://asr.lzyunying.com/v2/view?fileName=",
        //通话记录列表接口
        recordListUrl :"crm/salerRecord/read/list/customer/real/newyear",
        //录音识别详情接口
        recordAsrDetailUrl :"crm/salerRecord/asr",
        //通话记录列表
        recordList:[],
        fileName:"",
        filePath:"",
        //当前显示的录音识别详情
        asrResult:{result:[]},
        //被搜索的关键字
        keyWord:'',
        customerName : '',
        
        isMinister : false,
        expandAll : true,
    },
    created: function () {
    	var self = this;
        var customerId = getQueryString("customerId");
        if (customerId == null){
            alert("缺少参数:请传入资源ID");
            return;
        }
        self.loadRecordList(customerId);
    	var user = JSON.parse(sessionStorage.getItem("user"));
    	var userGroup = 0;
    	if (user && user != undefined && user != "") {
    		userGroup = user.userGroup;
		}
    	if (userGroup != null && (userGroup == 88002005 ||　userGroup == 10000000)) {
    		self.isMinister = true;
    	}
    },
    methods: {
        //当前选中行突出显示
        tableRowClassName:function(row, index) {
            if (row.callFile== this.fileName){
                return 'focus-row';
            }else{
                return '';
            }
            return '';
        },
        /**
         * 加载某资源的所有通话记录列表
         * @param resourceId 资源ID
         */
        loadRecordList : function (customerId) {
            var self = this;
            self.$http.put(self.recordListUrl, {"customerId":customerId})
                .then(function (response) {
                    self.recordList = response.data.data;
                    if (self.recordList) {
                    	self.customerName = self.recordList[0].customerName + '-';
                    	//默认显示第一个通话记录
                    	var firstRecord = self.recordList[0];
                    	if (firstRecord != null && firstRecord != '' && typeof(firstRecord) != "undefined") {
                    		self.showTranslated(firstRecord.callFile,firstRecord.translated);
						}
					}
                    document.title = self.customerName + '语音识别-梧桐树保险网';
                });
        },
        showTranslated:function(callFile){
        	var self = this;
            document.getElementById("translatedIframe").src = self.translatedUrl + callFile.substring(8);
            console.log(self.translatedUrl + callFile.substring(8));
        },
        existsCallFile : function (callFile) {
        	if (callFile == null || callFile == '' || typeof(callFile) == "undefined" || callFile <= 0) {
            	return false;
        	} else {
        		return true;
        	}
        },
        
    }
});

function getQueryString(name)
{
    var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if(r!=null)return  unescape(r[2]); return null;
}