/**
 * Created by naxj on 17/12/7.
 */
var vuePage = new Vue({
    el: '#app',
    data: {
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
        /**
         * 加载某录音文件的录音转写(录音识别内容)
         * @param fileName
         */
        updateRecordAsr:function(callFile){
            var self = this;
            var datePrefix = "";
            if (callFile == null || callFile == '' || typeof(callFile) == "undefined" || callFile <= 0) {
            	self.$notify({
            		title: "上传录音文件",
            		position: 'bottom-right',
            		duration : 3000,
            		message: "无录音文件",
            		type: 'warning'
            	});
        	} else {
        		/*datePrefix = callFile.substring(0,8);
        		self.fileName = callFile.substring(8);
        		if (self.fileName.substring(0,1) == 6) {
        			self.filePath = "M:/" + datePrefix + "/" + self.fileName;
        		} else {
        			self.filePath = "N:/" + datePrefix + "/" + self.fileName;
        		}*/
        		//生产环境时正常传递参数,开发环境替换不同的json文件
        		self.$http.post(self.recordAsrDetailUrl, {"callFile":callFile})
        		.then(function (response) {
        			/*self.asrResult = JSON.parse(response.data.data);
                    console.log(self.asrResult);
                    console.log(self.asrResult.result);
                    $('#audio').load();*/
        		});
        	}
        },
        /**
         * 跳转到指定播放进度
         */
        goToAutionTime :function(audioTime){
            var player = $('#audio')[0];
            player.currentTime = audioTime/1000;
            if (player.paused){
                player.play();
            }
        },
        /**
         * 搜索关键字
         * @param keyWord
         */
        searchKeyWord:function(){
            if (this.keyWord==''){
                return;
            }
            var asrList = this.asrResult.result;
            var scrolled = false;
            for(var i=0;i<asrList.length;i++){
                var item = asrList[i];
                if (item.text.indexOf(this.keyWord)!=-1){
                    //只滚动一次
                    if (!scrolled){
                        $('#asrListPanel').animate({
                            scrollTop: $("#asrItem-"+item.begin_time).offset().top-50
                        }, 200);
                    }
                    scrolled = true;
                    //高亮显示
                    item.text = item.text.replace(this.keyWord,"<span class='searched'>"+this.keyWord+"</span>");
                }
            }
        },
        existsCallFile : function (callFile) {
        	if (callFile == null || callFile == '' || typeof(callFile) == "undefined" || callFile <= 0) {
            	return false;
        	} else {
        		return true;
        	}
        },
        showTranslated : function (callFile,translated) {
        	var self = this;
            var datePrefix = "";
            if (callFile == null || callFile == '' || typeof(callFile) == "undefined" || callFile <= 0) {
            	self.$message({
					message: '无录音文件',
					type: 'warning'
				});
        	} else {
        		datePrefix = callFile.substring(0,8);
        		self.fileName = callFile.substring(8);
        		if (self.fileName.substring(0,1) == 6) {
        			//self.filePath = "http://192.168.0.4/m/" + datePrefix + "/" + self.fileName;
        			self.filePath = "ftp://192.168.0.8/" + datePrefix + "/" + self.fileName;
        		} else {
        			self.filePath = "ftp://192.168.10.8/" + datePrefix + "/" + self.fileName;
        		}
        		$('#audio').load();
        	}
            if (translated == null || translated == '' || typeof(translated) == "undefined" || translated <= 0) {
            	self.asrResult = "";
            	self.$message({
					message: '文本内容解析中，请稍后再试',
					type: 'warning'
				});
        	} else {
        		//console.log(translated);
        		var value = translated.replace(/(^\s*)|(\s*$)/g, "").replace(/\?/g,"").replace(/<\/?.+?>/g,"").replace(/[\r\n]/g, "");
        		//self.asrResult = JSON.parse(value);
        		self.asrResult = eval("(" + value + ")");
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