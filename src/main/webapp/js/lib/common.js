var vueSetAttribute = function(key,value){
    if (typeof(Vue.prototype.lz)=="undefined"){
        Vue.prototype.lz = {};
    }
    Vue.prototype.lz[key]=value;
}
var vueGetAttribute = function(key){
    if (typeof(Vue.prototype.lz)=="undefined"){
        return;
    }
    return Vue.prototype.lz[key];
}
String.prototype.startWith = function (str) {
    var reg = new RegExp("^" + str);
    return reg.test(this);
}

String.prototype.endWith = function (str) {
    var reg = new RegExp(str + "$");
    return reg.test(this);
}
//字符串utf8长度,汉字双字节
String.prototype.utf8len = function () {
    var len = 0;
    for (var i = 0; i < this.length; i++) {
        if (this.charCodeAt(i) > 127 || this.charCodeAt(i) == 94) {
            len += 2;
        } else {
            len++;
        }
    }
    return len;
}
//字符串utf8mb4长度,汉字三字节
String.prototype.utf8mb4len = function () {
    var len = 0;
    for (var i = 0; i < this.length; i++) {
        if (this.charCodeAt(i) > 127 || this.charCodeAt(i) == 94) {
            len += 3;
        } else {
            len++;
        }
    }
    return len;
}
String.prototype.format = function (args) {
    var result = this;
    if (arguments.length > 0) {
        if (arguments.length == 1 && typeof (args) == "object") {
            for (var key in args) {
                if (args[key] != undefined) {
                    var reg = new RegExp("({" + key + "})", "g");
                    result = result.replace(reg, args[key]);
                }
            }
        }
        else {
            for (var i = 0; i < arguments.length; i++) {
                if (arguments[i] != undefined) {
                    var reg = new RegExp("({)" + i + "(})", "g");
                    result = result.replace(reg, arguments[i]);
                }
            }
        }
    }
    return result;
}
//对Date的扩展，将 Date 转化为指定格式的String
//月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符， 
//年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字) 
//例子： 
//(new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423 
//(new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18 
Date.prototype.Format = function (fmt) { //author: meizz 
    var o = {
        "M+": this.getMonth() + 1, //月份 
        "d+": this.getDate(), //日 
        "h+": this.getHours(), //小时 
        "m+": this.getMinutes(), //分 
        "s+": this.getSeconds(), //秒 
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
        "S": this.getMilliseconds() //毫秒 
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}

var _win = window;
if (top){
    _win = top.window;
}
Vue.http.options.root = '';
Vue.http.interceptors.push(function (request, next) {
    if (this instanceof Vue) {
        if (request.body){
            if (typeof(request.body['createBy'])!="undefined"){
                delete request.body.createBy;
            }
            if (typeof(request.body['createTime'])!="undefined"){
                delete request.body.createTime;
            }
            if (typeof(request.body['updateBy'])!="undefined"){
                delete request.body.updateBy;
            }
            if (typeof(request.body['updateTime'])!="undefined"){
                delete request.body.updateTime;
            }
        }
        if (request.method == "PUT") {//查询方法使用小的提示消息
            var loadinMsgTip = this.$message({
                message: '加载中...',
                duration: 0,
                iconClass: "el-icon-loading",
                type: "error"
            });
        } else {//更新方法使用模态提示
            var loadingInstance = this.$loading({fullscreen: true})
        }

        next(function (response) {
            if (request.method == "PUT") {
                loadinMsgTip.close();
            } else {
                loadingInstance.close();
            }
            if (response.ok) {
                if (response.data.httpCode === 200) {
                    if (request.method != "PUT") {//查询结果不显示反馈消息
                        this.$message({
                            message: response.data.msg,
                            type: 'success',
                            showClose: true
                        });
                    }
                } else if (response.data.httpCode === 401 
                		|| response.data.httpCode === 405 
                		|| response.data.httpCode === 0) {//未登录
//                    if (request.url.startWith("/sys/") || request.url.startWith("sys/")) {
                        _win.location.href = "/s/login.html";
//                    }
                    //console.log(request);
                } else if (typeof(response.data.httpCode)=="undefined"){
                	
                }else {
                    this.$message({
                        message: response.data.msg,
                        type: 'error',
                        showClose: true,
                        duration: 0
                    })
                }
            } else {
                this.$message({
                    message: response.statusText + "[HTTP status:" + response.status + "]",
                    type: 'error',
                    showClose: true,
                    duration: 0
                })
            }
            return response;
        });
    }else{
        next(function (response) {
            if (response.ok) {
                if (response.data.httpCode === 200) {
                    return response;
                }else if (response.data.httpCode === 401) {//未登录
//                    if (request.url.startWith("/sys/") || request.url.startWith("sys/")) {
                        _win.location.href = "/s/login.html";
//                    }
                }else{
                    //alert(response.data.msg + "[code:" + response.data.httpCode + "]");
                	_win.location.href = "/s/login.html";
                }
            }else if(response.status==404){
                /*alert("404");
                return;*/
            	_win.location.href = "/s/login.html";
            }else{
                /*alert(response.status+"\n\n"+response.text());
                return;*/
            	_win.location.href = "/s/login.html";
            }

        });
    }
});


var clone = function (obj) {
    var o;
    if (typeof obj == "object") {
        if (obj === null) {
            o = null;
        } else {
            if (obj instanceof Array) {
                o = [];
                for (var i = 0, len = obj.length; i < len; i++) {
                    o.push(clone(obj[i]));
                }
            } else {
                o = {};
                for (var j in obj) {
                    o[j] = clone(obj[j]);
                }
            }
        }
    } else {
        o = obj;
    }
    return o;
};
Vue.use(VueHtml5Editor, {
    // 全局组件名称，使用new VueHtml5Editor(options)时该选项无效
    // global component name
    name: "vue-html5-editor",
    // 是否显示模块名称，开启的话会在工具栏的图标后台直接显示名称
    // if set true,will append module name to toolbar after icon
    showModuleName: false,
    // 自定义各个图标的class，默认使用的是font-awesome提供的图标
    // custom icon class of built-in modules,default using font-awesome
    icons: {
        text: "fa fa-pencil",
        color: "fa fa-paint-brush",
        font: "fa fa-font",
        align: "fa fa-align-justify",
        list: "fa fa-list",
        link: "fa fa-chain",
        unlink: "fa fa-chain-broken",
        tabulation: "fa fa-table",
        image: "fa fa-file-image-o",
        hr: "fa fa-minus",
        eraser: "fa fa-eraser",
        undo: "fa-undo fa",
        "full-screen": "fa fa-arrows-alt",
        info: "fa fa-info",
    },
    // 配置图片模块
    // config image module
    image: {
        // 文件最大体积，单位字节  max file size
        sizeLimit: 512 * 1024,
        // 上传参数,默认把图片转为base64而不上传
        // upload config,default null and convert image to base64
        upload: {
            url: null,
            headers: {},
            params: {},
            fieldName: {}
        },
        // 压缩参数,默认使用localResizeIMG进行压缩,设置为null禁止压缩
        // compression config,default resize image by localResizeIMG (https://github.com/think2011/localResizeIMG)
        // set null to disable compression
        compress: {
            width: 1600,
            height: 1600,
            quality: 80
        },
        // 响应数据处理,最终返回图片链接
        // handle response data，return image url
        uploadHandler: function (responseText) {
            //default accept json data like  {ok:false,msg:"unexpected"} or {ok:true,data:"image url"}
            var json = JSON.parse(responseText)
            if (!json.ok) {
                alert(json.msg)
            } else {
                return json.data
            }
        }
    },
    // 语言，内建的有英文（en-us）和中文（zh-cn）
    //default en-us, en-us and zh-cn are built-in
    language: "zh-cn",
    // 自定义语言
    i18n: {
        //specify your language here
        "zh-cn": {
            "align": "对齐方式",
            "image": "图片",
            "list": "列表",
            "link": "链接",
            "unlink": "去除链接",
            "table": "表格",
            "font": "文字",
            "full screen": "全屏",
            "text": "排版",
            "eraser": "格式清除",
            "info": "关于",
            "color": "颜色",
            "please enter a url": "请输入地址",
            "create link": "创建链接",
            "bold": "加粗",
            "italic": "倾斜",
            "underline": "下划线",
            "strike through": "删除线",
            "subscript": "上标",
            "superscript": "下标",
            "heading": "标题",
            "font name": "字体",
            "font size": "文字大小",
            "left justify": "左对齐",
            "center justify": "居中",
            "right justify": "右对齐",
            "ordered list": "有序列表",
            "unordered list": "无序列表",
            "fore color": "前景色",
            "background color": "背景色",
            "row count": "行数",
            "column count": "列数",
            "save": "确定",
            "upload": "上传",
            "progress": "进度",
            "unknown": "未知",
            "please wait": "请稍等",
            "error": "错误",
            "abort": "中断",
            "reset": "重置"
        }
    },
    // 隐藏不想要显示出来的模块
    // the modules you don't want
    hiddenModules: [],
    // 自定义要显示的模块，并控制顺序
    // keep only the modules you want and customize the order.
    // can be used with hiddenModules together
    visibleModules: [
        "text",
        "color",
        "font",
        "align",
        "list",
        "link",
        "unlink",
        "tabulation",
        "image",
        "hr",
        "eraser",
        "undo",
        "full-screen",
        "info",
    ],
    // 扩展模块，具体可以参考examples或查看源码
    // extended modules
    modules: {
        //omit,reference to source code of build-in modules
    }
});

var Vali = {
    msg: {
        req: "此为必填项",
        min: "数字必须大于{0}",
        max: "数字必须小于{0}",
        len: "最多可输入{0}个字符",
        int: "请输入整数",
        float: "请输入数字",

    },
    required: function (required, v, b) {
        if (required && (!v || v == "")) {
            b(new Error("此为必填项"));
        } else {
            return true;
        }
    },
    ut8Len: function (required, len) {
        var self = this;
        return {
            validator: function (r, v, b) {
                if (required && (!v || v == "")) {
                    b(new Error(self.msg.req));
                } else if (!v || v == "") {
                    b();
                } else if (v.utf8len() > len) {
                    b(new Error(self.msg.len.format(len)));
                } else {
                    b();
                }
            }
        };
    },
    utf8mb4Len: function (required, len) {
        var self = this;
        return {
            validator: function (r, v, b) {
                if (required && (!v || v == "")) {
                    b(new Error(self.msg.req));
                } else if (!v ||v == "") {
                    b();
                } else if (v.utf8mb4len() > len) {
                    b(new Error(self.msg.len.format(len)));
                } else {
                    b();
                }
            }
        };
    },
    int: function (required, min, max) {
        var self = this;
        return {
            validator: function (r, v, b) {
                if (typeof(min) == "undefined") {
                    min = 0;
                }
                if (typeof(max) == "undefined") {
                    max = 2147483647;
                }
                if (required && (!v || v == "")) {
                    b(new Error(self.msg.req));
                } else if (!v || v == "") {
                    b();
                } else if (isNaN(Number.parseInt(v))) {
                    b(new Error(self.msg.int));
                } else if (Number.parseInt(v) < Number.parseInt(min)) {
                    b(new Error(self.msg.min.format(min)));
                } else if (Number.parseInt(v) > Number.parseInt(max)) {
                    b(new Error(self.msg.max.format(max)));
                } else {
                    b();
                }
            }
        }
    },
    long: function (required, min, max) {
        var self = this;
        return {
            validator: function (r, v, b) {
                if (typeof(min) == "undefined") {
                    min = 0;
                }
                if (required && (!v || v == "")) {
                    b(new Error(self.msg.req));
                } else if (!v || v == "") {
                    b();
                } else if (!/^[0-9]*$/.test(v)) {
                    new Error(self.msg.int)
                } else if (isNaN(Number.parseInt(v))) {
                    b(new Error(self.msg.int));
                } else if (typeof(max) == "undefined" && v.length > 19) {
                    b(new Error("不能超过19个数字"));
                } else if (Number.parseInt(v) < Number.parseInt(min)) {
                    b(new Error(self.msg.min.format(min)));
                } else if (max && Number.parseInt(v) > Number.parseInt(max)) {
                    b(new Error(self.msg.max.format(max)));
                } else {
                    b();
                }
            }
        }
    },
    float: function (required, min, max) {
        var self = this;
        return {
            validator: function (r, v, b) {
                if (typeof(min) == "undefined") {
                    min = 0;
                }
                if (required && (!v || v == "")) {
                    b(new Error(self.msg.req));
                } else if (!v || v == "") {
                    b();
                } else if (!/^[0-9]+\.?[0-9]*$/.test(v)) {
                    b(new Error(self.msg.float));
                } else if (isNaN(Number.parseFloat(v))) {
                    b(new Error(self.msg.float));
                } else if (typeof(max) == "undefined" && v.length > 54) {
                    b(new Error("不能超过54个数字"));
                } else if (Number.parseFloat(v) < Number.parseFloat(min)) {
                    b(new Error(self.msg.min.format(min)));
                } else if (max && Number.parseFloat(v) > Number.parseFloat(max)) {
                    b(new Error(self.msg.max.format(max)));
                } else {
                    b();
                }
            }
        }
    },
    double: function (required, min, max) {
        var self = this;
        return {
            validator: function (r, v, b) {
                if (typeof(min) == "undefined") {
                    min = 0;
                }
                if (required && (!v || v == "")) {
                    b(new Error(self.msg.req));
                } else if (!v || v == "") {
                    b();
                } else if (!/^[0-9]+\.?[0-9]*$/.test(v)) {
                    b(new Error(self.msg.float));
                } else if (isNaN(Number.parseFloat(v))) {
                    b(new Error(self.msg.float));
                } else if (typeof(max) == "undefined" && v.length > 414) {
                    b(new Error("不能超过414个数字"));
                } else if (Number.parseFloat(v) < Number.parseFloat(min)) {
                    b(new Error(self.msg.min.format(min)));
                } else if (max && Number.parseFloat(v) > Number.parseFloat(max)) {
                    b(new Error(self.msg.max.format(max)));
                } else {
                    b();
                }
            }
        }
    },
    mobile:  function (required) {
    	var self = this;
        return {
            validator: function (r, v, b) {
                if (required && (!v || v == "")) {
                    b(new Error(self.msg.req));
                } else if (!v || v == "") {
                    b();
                } else if (!/^1[345789][0-9]{9}$/.test(v)) {
                    b(new Error("请输入正确的手机号码"));
                } else {
                    b();
                }
            }
        }
    }
};

//转换布尔值为文字
Vue.prototype.convertBoolean =function(v){
    if (typeof v == 'boolean'){
        if ( v == true){
            return "是"
        }else if ( v == false){
            return "否";
        }
    }else if (typeof v == 'string'){
        if ( v == "true" || v == "1"){
            return "是"
        }else if ( v == "false" || v == "0"){
            return "否";
        }
    }else{
        return "否"
    }
};
//转换状态值为文字
Vue.prototype.convertStatus =function(v){
	if (v == null || v == '') {
		return "未知";
	}
	if (typeof v == 'string'){
		if ( v == "0"){
			return "待处理";
		}else if ( v == "1"){
			return "已处理";
		}else if ( v == "2"){
			return "驳回";
		}else if ( v == "5"){
			return "处理中";
		}
	}else{
		return "";
	}
};
//转换状态值为文字
Vue.prototype.convertEffective =function(v){
	if (v == null || v == '') {
		return "未知";
	}
	if (typeof v == 'string'){
		if ( v == "0"){
			return "未生效";
		}else if ( v == "1"){
			return "生效";
		}else if ( v == "-1"){
			return "失效";
		}
	}else{
		return "";
	}
};
//转换用户ID为用户姓名
Vue.prototype.convertUser =function(id){
	return "暂未启用";
};
//截取时间格式（月-日）
Vue.prototype.monthDayFormat =function(v){
	if (v == null || v == '') {
		return "";
	} else if (typeof v == 'string') {
		return v.substring(5,10);
	} else{
		return "";
	}
};
//截取时间格式（年-月-日）
Vue.prototype.birthdayFormat =function(v){
	if (v == null || v == '') {
		return "";
	} else if (typeof v == 'string') {
		return v.substring(0,10);
	} else{
		return "";
	}
};
//截取时间格式（年-月-日 时 分）
Vue.prototype.minuteFormat =function(v){
	if (v == null || v == '') {
		return "";
	} else if (typeof v == 'string') {
		return v.substring(0,16);
	} else{
		return "";
	}
};
//截取时间格式（月-日 时 分 秒）
Vue.prototype.monthFormat =function(v){
	if (v == null || v == '') {
		return "";
	} else if (typeof v == 'string') {
		return v.substring(5,19);
	} else{
		return "";
	}
};
//截取时间格式（月-日 时 分）2018-02-25 19:11改为03月05日 19:39
Vue.prototype.monthMinuteFormat =function(v){
	if (v == null || v == '') {
		return "";
	} else if (typeof v == 'string') {
		//return v.substring(5,16);
		return v.substring(5,7) + '月' + v.substring(8,10) + '日 ' + v.substring(11,16);
	} else{
		return "";
	}
};
Vue.prototype.hideFormat =function(v){
	if (v == null || v == '') {
		return "";
	} else if (typeof v == 'string') {
		if (v.length > 3 && v.length < 7) {
			var hide = "";
			for (var i = 0 ; i < v.length - 3 ; i++) {
				hide += "*";
			}
			return v.substring(0,3) + hide;
		} else if (v.length >= 7 && v.length <= 12) {
			var hide = "";
			for (var i = 0 ; i < v.length - 5 ; i++) {
				hide += "*";
			}
			return v.substring(0,5) + hide;
		} else if (v.length > 12) {
			var hide = "";
			for (var i = 0 ; i < v.length - 8 ; i++) {
				hide += "*";
			}
			return v.substring(0,8) + hide;
		}
	} else{
		return "";
	}
};

Vue.prototype.mobileFormat =function(v){
	if (v == null || v == '') {
		return "";
	} else {
		var reg = /^(\d{3})\d{4}(\d{4})$/;
		return v.replace(reg, "$1****$2");
	}
}

//四舍五入（单位元）
Vue.prototype.roundFormat =function(v){
	if (v == null || v == '' || typeof(v) == "undefined" || v <= 0) {
		return "一";
	} else {
		return Math.round(v) + "元";
	}
};
//四舍五入（保留两位小数）
Vue.prototype.decimalsFormat =function(v){
	if (v == null || v == '' || typeof(v) == "undefined" || v <= 0) {
		return "一";
	} else {
		return v.toFixed(2);
	}
};
//空值默认（-）
Vue.prototype.nullFormat =function(v){
	if (v == null || v == '' || typeof(v) == "undefined" || v <= 0) {
		return "一";
	} else {
		return v + "个";
	}
};
//添加百分号（%）
Vue.prototype.rateFormat =function(v){
	if (v == null || v == '' || typeof(v) == "undefined" || v <= 0) {
		return "一";
	} else {
		return v + "%";
	}
};
//添加年
Vue.prototype.yearFormat =function(v){
	if (v == null || v == '' || typeof(v) == "undefined" || v <= 0) {
		return v;
	} else {
		return v + " 年";
	}
};
//添加天
Vue.prototype.daydayFormat =function(v){
	if (v == null || v == '' || typeof(v) == "undefined" || v <= 0) {
		return v;
	} else {
		return v + " 天";
	}
};
//添加元
Vue.prototype.rmbFormat =function(v){
	if (v == null || v == '' || typeof(v) == "undefined" || v <= 0) {
		return v;
	} else {
		return v + " 元";
	}
};
//添加元
Vue.prototype.rmbwFormat =function(v){
	if (v == null || v == '' || typeof(v) == "undefined" || v <= 0) {
		return v;
	} else {
		return v + " 元";
	}
};
//添加岁
Vue.prototype.ageFormat =function(v){
	if (v == null || v == '' || typeof(v) == "undefined" || v <= 0) {
		return v;
	} else {
		return v + " 岁";
	}
};
//是否存在录音文件
Vue.prototype.recordExist =function(v){
	if (v == null || v == '' || typeof(v) == "undefined" || v <= 0) {
		return "一";
	} else {
		return "下载录音";
	}
};
//拼接ftp
Vue.prototype.ftpDown =function(v){
	var ftpUrl = "";
	if (v == null || v == '' || typeof(v) == "undefined" || v <= 0) {
		self.$notify({
	          title: '警告',
	          message: '录音地址为空',
	          type: 'warning',
	          duration: 2000
	        });
	} else {
		v1 = v.substring(0,8);
		v2 = v.substring(8);
		ftpUrl = "ftp://172.16.0.7/" + v1 + "/" + v2;
		window.open(ftpUrl);
	}
};
Vue.prototype.isThough =function(v){
	if (v == null || v == '' || typeof(v) == "undefined" || v <= 0) {
		return false;
	} else if (v == 1){
		return true;
	}
};
//计时器（小时，分钟，秒）/时间格式转换（秒）
Vue.prototype.timerFormat =function(v){
	if (v == null || v == '' || typeof(v) == "undefined" || v <= 0) {
		return "";
	} else {
		var time = parseInt(v);
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
	        else if (time >= 60 * 60 * 24 && time < 60 * 60 * 24 * 30) {
	        	var day = Math.floor( time/ (24*3600) ); // Math.floor()向下取整 
	        	var hour = Math.floor( (time - day*24*3600) / 3600);
	        	var minute = Math.floor( (time - day*24*3600 - hour*3600) /60 );
	        	var second = time - day*24*3600 - hour*3600 - minute*60;
	        	time = day + "天" + hour + "时" + minute + "分" + second + "秒";
	        }
	        /*else if (time >= 60 * 60 * 24 && time < 60 * 60 * 24 * 30) {
	        	time = parseInt(time / 86400.0) + "天" + 
	        	parseInt((parseFloat(time / 86400.0) - parseInt(time / 86400.0)) * 24) + "小时" + 
	        	parseInt((parseFloat(time / 3600.0) -parseInt(time / 3600.0)) * 60) + "分钟" +
	        	parseInt((parseFloat((parseFloat(time / 3600.0) - parseInt(time / 3600.0)) * 60) -
	        			parseInt((parseFloat(time / 3600.0) - parseInt(time / 3600.0)) * 60)) * 60) + "秒";
	        }*/
	        else {
	            time = parseInt(time) + "秒";
	        }
	    }
	    return time;
	}
};

//年月日转年龄
Vue.prototype.ageFormat2 =function(v){
	if (v == null || v == '' || typeof(v) == "undefined" || v <= 0) {
		return "一";
	} else {
		var returnAge;
	    var strBirthdayArr=v.toString().substring(0,10).split("-");
	    var birthYear = strBirthdayArr[0];
	    var birthMonth = strBirthdayArr[1];
	    var birthDay = strBirthdayArr[2];
	    var d = new Date();
	    var nowYear = d.getFullYear();
	    var nowMonth = d.getMonth() + 1;
	    var nowDay = d.getDate();
	    
	    if(nowYear == birthYear) {
	        returnAge = "今年出生";
	    } else {
	        var ageDiff = nowYear - birthYear ; //年之差
	        if(ageDiff > 0) {
	            /*if(nowMonth == birthMonth) {
	                var dayDiff = nowDay - birthDay;//日之差
	                if(dayDiff < 0) {
	                    returnAge = ageDiff - 1;
	                } else {
	                    returnAge = ageDiff ;
	                }
	            } else {
	                var monthDiff = nowMonth - birthMonth;//月之差
	                if(monthDiff < 0) {
	                    returnAge = ageDiff - 1;
	                } else {
	                    returnAge = ageDiff ;
	                }
	            }*/
	        	returnAge = ageDiff ;
	        } else {
	            returnAge = "穿越时空";
	        }
	    }
	    return returnAge;//返回周岁年龄
	}
};
//是否存在消息提交码
Vue.prototype.weChatChatExist =function(v){
	if (v == null || v == '' || typeof(v) == "undefined" || v <= 0) {
		return "一";
	} else if (v == 'javascript:void(0);') {
		return "一";
	} else if (v.length == 11 || v.length == 12) {
		return "消息码成功";
	} else {
		return "查看聊天";
	}
};
//距离到期天数
Vue.prototype.retainTimeFormat = function(v) {
	if (v == null || v == '' || typeof(v) == "undefined" || v <= 0) {
		return v;
	} else {
		var date = new Date();
		var startTime = new Date(Date.parse(v.replace(/-/g,"/"))).getTime();
	    var endTime = new Date(Date.parse(date.toString().replace(/-/g,"/"))).getTime();
	    var dates = (startTime - endTime)/(1000*60*60*24);
		return Math.round(dates) + " 天";
	}
};
//两日期相隔天数
Vue.prototype.dateDiffFormat = function(startTime,endTime) {
	if (startTime == null || startTime == '' || typeof(startTime) == "undefined" || startTime <= 0) {
		return startTime;
	} else if (endTime == null || endTime == '' || typeof(endTime) == "undefined" || endTime <= 0) {
		return endTime;
	} else {
		var date = new Date();
		var getStartTime = new Date(Date.parse(startTime.replace(/-/g,"/"))).getTime();
		var getEndTime = new Date(Date.parse(endTime.replace(/-/g,"/"))).getTime();
		var dates = (getEndTime - getStartTime)/(1000*60*60*24);
		return Math.round(dates) + " 天";
	}
};