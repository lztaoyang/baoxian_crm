
//判断对象是否为空对象
function isEmptyObject(obj) {
    if (obj.length!=null && obj.length == 0) return false;
    if (Object.prototype.toString.apply(obj) !== '[object Object]') return false;
    for (var p in obj) if (obj.hasOwnProperty(p)) return false;
    return true
};
(function (t) {
    //自动重接的WebSocket
    function WS(name) {
        this.name=name;
        this.url = "";
        this.state = 0; //状态 (0:未初始化,1:未连接,2:连接中,3:已连接,4:关闭中,5:错误)
        this.ws = null;
        this.connCount = 0;
        this.isReConn = false; //是否正在自动重新连接
        if (!WebSocket)
            throw "浏览器不支持WebSocket!";
    }
    /* WebSocket.readyState
      0   CONNECTING   连接尚未建立
      1   OPEN         WebSocket的链接已经建立
      2   CLOSING      连接正在关闭
      3   CLOSED       连接已经关闭或不可用
    */
    WS.prototype.Connect = function (url) {
        var self = this;
        if (self.state == 3) return;
        if(url)
            self.url = url;
        if (self.ws) {
            self.ws.onopen = null;
            self.ws.onerror = null;
            self.ws.onclose = null;
            self.ws.onmessage = null;
            self.ws.close();
            self.ws = null;
        }
        self.ws = new WebSocket(self.url);
        self.ws.onmessage = function (evt) { if (self.OnMsg) self.OnMsg(evt.data, evt.type, evt.origin); };
        self.ws.onopen = function () {
            self.state = 3;
            self.connCount++;
            if (self.isReConn && self.connCount>1) {
                self.isReConn = false;
                if (self.OnReOpen) self.OnReOpen(self.connCount);
            }
            else if (self.OnOpen) self.OnOpen();
        }
        self.ws.onerror = function (evt) {
            console.error(self.name+" WS: ", evt.type, evt);
            //断开连接了
            if (!self.isReConn && this.readyState != 1) {
                self.state = 2;
                self.ReConn();
            }
        }
        self.ws.onclose = function () {
            //主动关闭,不自动连接
            //远端正常关闭,或出错导致关闭,自动重新连接
            if (!self.isReConn && self.state != 4 && this.readyState != 1) {
                self.state = 2;
                self.ReConn();
            }
            if (self.OnClose) self.OnClose();
        }
        self.state = 2;
    };
    WS.prototype.Send = function (msg) {
        if (this.state!=3)
            console.warn(this.name+" WebSocket连接"+(state==2?"正在":"尚未")+"建立,不能发送", msg);
        else
            this.ws.send(msg);
    };
    WS.prototype.Close = function () { if (this.ws) { self.connCount=0; this.state = 4; if (this.ws && this.ws.close) this.ws.close(); } };
    WS.prototype.OnMsg = function (data, type, origin) { console.log(this.name+" WS.OnMsg: ", data, type, origin); };
    WS.prototype.OnOpen = function () { console.info(this.name+" WS.OnOpen: connected"); };
    WS.prototype.OnReOpen = function (n) { console.info(this.name+" WS.OnReOpen: reConnected",n); };
    WS.prototype.OnClose = function () { console.info(this.name+" WS.OnClose: closed"); };
    WS.prototype.ReConn=function () {
        var self=this;
        if (this.state != 3) {
            if(this.state == 4)
                this.isReConn = false;
            else{
                this.isReConn = true;
                this.Connect();
                setTimeout(function(){self.ReConn.apply(self)}, 1000);
            }
        }
    }
    t.WS = WS;
})(this);
/*
auth:lzpong 2014/07/08
use WebSocket
Use Like: 第三方使用
PbxTransfer.init(9).Connect(ip,port);
*/
var PbxTransfer = PbxTransfer || function () {

    var State = 0,//连接状态 (0:未初始化,1:已初始化(未连接),3:连接中,5:已连接,10:已登录,15:分机登陆)
    isIdl = 1,//坐席状态是否空闲
    User = "",//工号
    Uname="",//姓名
    ExtNum = "",//分机号
    OutNum = "9",//出局号
    SObj = null,//Socket对象
    loginNum = 0,//限制登录延时执行次数
    connectNum = 0;//限制连接延时执行次数
    var Ip, Port=2051;
    //呼入量,呼入未接量,呼出量,呼出未接量
    var InCallNum = InCallNumN = OutCallNum = OutCallNumN = 0;
    var RecFile = "";
    var MsgQueue = [];//发送失败的消息,3秒内的重新发送
    var ConnTimerID = 0;
    /*
    用于与服务器的消息的转换
    auth:lzpong 2014/08/18
    use for ParsXml with server msg
    $.parseXML的功能
    */
    //String解析->Xml对象
    function loadXML(xmlString) {
        if ($ && $.parseXML)
            return $.parseXML(xmlString);
        var xmlDoc = null;
        if (window.ActiveXObject) {
            xmlDoc = new ActiveXObject('Microsoft.XMLDOM');
            if (!xmlDoc) xmldoc = new ActiveXObject("MSXML2.DOMDocument.3.0");
            xmlDoc.async = false;
            xmlDoc.loadXML(xmlString);
        }
        else if (document.implementation && document.implementation.createDocument) {
            var domParser = new DOMParser();
            xmlDoc = domParser.parseFromString(xmlString, 'text/xml');
        } else {; }
        return xmlDoc;
    }
    //obj转为Xml字符串
    function ToXml(msg) {
        for (v in msg) { msg[v] =  (msg[v] == null) ? "": msg[v]+""; }
        msg.To = msg.To || "";
        msg.Body = msg.Body || "";
        msg.P1 = msg.P1 || "";
        msg.P2 = msg.P2 || "";
        msg.P3 = msg.P3 || "";
        msg.P4 = msg.P4 || "";
        msg.P5 = msg.P5 || "";
        msg.P6 = msg.P6 || "";
        msg.P7 = msg.P7 || "";
        return "<Root><Code>" + msg.Code + "</Code><Ext>" + msg.Ext + "</Ext><From>" + msg.From + "</From><To>" + msg.To + "</To><Body>" + msg.Body + "</Body><P1>" + msg.P1 + "</P1><P2>" + msg.P2 + "</P2><P3>" + msg.P3 + "</P3><P4>" + msg.P4 + "</P4><P5>" + msg.P5 + "</P5><P6>" + msg.P6 + "</P6><P7>" + msg.P7 + "</P7><DT></DT></Root>";
    }
    //Xml字符串转为Obj
    function ToMsg(str) {
        if (typeof (str) == "object")
            return str;
        if (str.substring( 0, 1) != "<"){
            var ar=str.match(/\"[\d]{8}[\\/][\d]{3,}/g);//Fix for 录音路径的分机号 不是8开头的会丢掉第一位数字
            if(ar && ar.length>0)
                ar.forEach(function(v){str=str.replace(v,v.replace(/[\\/]/g,''))});
            return eval('(' + str + ')');
        }
        //解析xml,返回xml对象
        var doc = loadXML(str);
        var msg = {};
        if (doc) {
            var re = doc.childNodes[0].childNodes;
            for (var i = 0; i < re.length; i++) {
                switch (re[i].nodeType) {
                    case 1://ElementNode
                        msg[re[i].nodeName] = re[i].innerHTML;
                        break;
                    case 2://AtrributeNode
                        break;
                    case 3://TextNode
                        msg[re[i].nodeName] = re[i].nodeValue;
                        break;
                    case 4://CDATA_SECTION_NODE
                        barek;
                }
            }
            msg.Code = msg.Code ? parseInt(msg.Code) : msg.Code;
        }
        return msg;
    }
    //消息发往PBX
    function SendMsg(msg) {
        if (State < 5) {
            console.log("提示","未连接服务或未登录," + State);
            return -1;
        }
        if (msg.Code > 1 && msg.Code < 27)//27-30 AutoCall
            if (ExtNum == '')
                return console.log("提示","未关联分机登录CTI服务,不能使用电话功能," + State), -2;
        msg.Ext = ExtNum;
        msg.From = User;
        var s = ToXml(msg);
        //console.log(s);
        return SObj.SendMsg(s);
    }

    //事件回调广播,发给所有有此事件接口的window,供Pbx等事件调用
    function OnEvent(evt) {
        var n=0;
        var arr = [];
        for (var i = 1; i < arguments.length; i++)
            arr[i - 1] = arguments[i];
        if (window[evt])
        {
            window[evt].apply(evt, arr);
            n++;
        }
        for (var i = 0; i < window.length; i++) {
            try{
                if (typeof (window[i]) == "object" && typeof (window[i][evt]) == "function") {
                    setTimeout(window[i][evt].apply(evt, arr), 1);//采用定时器执行,以获得立即返回效果(异步)
                    n++;
                }
           }catch(e){}
        }
        return n;
    }

    //检查(自动重新)连接的状态
    function ReLogin() {
        if (ConnTimerID < 1) {
            if (State < 5) {
                console.info("尝试自动重新连接/登陆", State);
                console.log(++connectNum, "Connect " + Ip + ":" + Port, SObj.GetState());
                State = 3;
                SObj.Connect(Ip, Port);
                setTimeout(ReLogin, 1000);
            }
        }
    }

    if((SObj==null) && (typeof (WebSocket) != "undefine"))
        SObj = function () {
            var ws;
            return {
                WS:ws,
                Connect: function (ip, port) {
                    if (ws) {
                        ws.onopen = null;
                        ws.onerror = null;
                        ws.onclose = null;
                        ws.onmessage = null;
                        ws.close?ws.close():null;
                        ws = null;
                    }
                    ws = new WebSocket("ws://" + ip + ":" + port);
                    ws.onmessage = function (msg) { PbxTransfer.OnMsg(msg.data) };
                    ws.onopen = function (evt) {
                        ConnTimerID = 0;
                        OnEvent("OnConnect",true);
                        State = 5;
                        connectNum = 0;
                        console.log("连接状态", "已连接");
                        if (User.length > 2)
                            SendMsg({ Code: 1, P1: Uname, P2: "", P3: "CRM", P5: isIdl, P6: "自动登录" });
                    };
                    ws.onerror = function (evt) {
                        console.error("错误:" , evt.type , evt);
                        if (State > 3) {
                            if (SObj.GetState() > 1)//断开连接了
                                State = 1;
                        }
                    };
                    ws.onclose = function () {
                        if (State > 3) {
                            State = 1;
                        } else {//出错导致关闭
                            State = 1;
                        }
                        OnEvent("OnClose");
                        console.info("连接关闭,自动重连");
                        ReLogin();
                    };
                },
                SendMsg: function (msg) {
                  var n=0;
                  switch(ws.readyState) {
                  case 0:console.error("WebSocket连接尚未建立",ws,msg);State = 1;break;//CONNECTING 连接尚未建立
                  case 1:n=ws.send(msg); n=n?n:msg.length; break;
                  case 2:console.error("WebSocket连接正在关闭",ws,msg);State = 1;break;//CLOSING 连接正在关闭
                  case 3:console.error("WebSocket连接已经关闭或不可用",ws,msg);State = 1;break;//CLOSED  连接已经关闭或不可用
                  default:console.error("WebSocket未知状态",ws,msg);State = 1;break;
                  }
                  return n;
                },
                Close: function () { return ws.close(), 0; },
                /*
                  0   CONNECTING   连接尚未建立
                  1   OPEN         WebSocket的链接已经建立
                  2   CLOSING      连接正在关闭
                  3   CLOSED       连接已经关闭或不可用
                */
                GetState: function () { return ws ? ws.readyState : -1; }
            }
        }();

    /*
    ** Public API
    */
    return {
        //是否已登陆CTI (关联分机号)
        isCti:function(){return State>10},
        //初始化
        Init: function (outnum) {
            console.log("PBXTransfer.Init");
            if (typeof (WebSocket) == "undefine") //消息服务 现在支持WebSocket
                return alert("浏览器不支持WebSocket!");
            outnum=""+outnum;
            OutNum = outnum.trim()||"9";
            State = 1;
            return this;
        },
        GetState: function () { return SObj.GetState(); },
        //设置外呼出局号/前缀号码
        SetPreNum: function (pre) { OutNum = (""+(pre || OutNum)).trim() },
        //连接PBX
        Connect: function (ip, port) {
            if (State < 5) {
                if (SObj == null)
                    return console.log("提示", "浏览器不支持WebSocket") , State;
                State = 3;
                if (ip != null && port == null) {
                    ip = ip.split(":")[0];
                    port = ip.split(":")[1] || Port;
                }
                Ip=ip||Ip,Port=port||Port;
                console.log(++connectNum, " Connect "+Ip+":"+Port);
                SObj.Connect(Ip, Port);
            }
            else
                console.log(" 已连接");
            return State;
        },
        Close: function () { SObj.Close(); State = 1; },
        Send: function (msg) { if (typeof (msg) == "object") return SendMsg(msg); else return SObj.SendMsg(msg); },
        GetRecFile: function (){ return RecFile;},
        /*
        **PBX调用函数
        */
        //登陆(分机号,工号,[密码],姓名[,是否置闲,额外消息])
        Login: function (ext, user, pass, name, bIdl, msg) {
            if (State < 5 || State >=10)
                return 0;

            User = user||User;
            Uname=name||Uname;
            ExtNum = ext||ExtNum;
            loginNum = 0;
            if (name == null)
                name = "";
            if(typeof(bIdl)=="number" || typeof(bIdl)=="string")
              isIdl=bIdl.toString();
            return SendMsg({ Code: 1, P1: Uname, P2: pass, P3: "CRM", P5: isIdl, P6:(msg||"")});
        },
        //登出(true)
        Logout: function () { console.log("退出分机登陆"); return SendMsg({ Code: 2 }), State = 5; },
        //拨号(电话号码[,客户ID,联系人ID,拨打历史ID,数据池ID,显示号码])
        MakeCall: function (number, CMID, CTID, DDHID, DPID, OutCallShow) { RecFile = ""; return SendMsg({ Code: 3, P1: number, P2: OutNum, P3: CMID || "", P4: CTID || "", P5: DDHID || "", P6: DPID || "", P7: OutCallShow||"" }); },
        //加密拨号,扩展(拨打历史ID,客户ID,数据池ID)
        MakeCalls: function (ddhid, cmid, dpid) { RecFile = "";  return SendMsg({ Code: 51, P1: ddhid, P2: OutNum, P3: cmid, P5: dpid }); },
        //挂机
        HangUp: function () { return SendMsg({ Code: 4 }); },
        //置闲
        SetReady: function () { isIdl = 1; return SendMsg({ Code: 5 }); },
        //置忙(原因,置忙时长)
        SetBusy: function (reason, time) { isIdl = 0; return SendMsg({ Code: 6, P1: reason, P2: (time || '0') }); },
        //转接分机
        TransExt: function (toExt) { return SendMsg({ Code: 7, P1: toExt }); },
        //转接外线  //原 Code=22 , 因转分机转外线公用的
        TransPhone: function (toPhone) { return SendMsg({ Code: 7, P1: OutNum+toPhone, P2: OutNum }); },
        //闪断
        Flash: function () { return SendMsg({ Code: 8 }); },
        //播放IVR
        PlayIvr: function (ivr) { return SendMsg({ Code: 9, P1: ivr, P2: '0' }); },
        //播放录音文件(PBX),(替换反斜杠)
        PlayRec: function (fpath) { return SendMsg({ Code: 16, Body: fpath.replace("\\","\\\\") }); },
        //播放语音留言,(替换反斜杠)
        PlayVoce: function (fpath) { return SendMsg({ Code: 17, Body: fpath.replace("\\", "\\\\") }); },
        //组内代接
        GroupPick: function (ext) { return SendMsg({ Code: 10, P1:ext}); },
        //监听分机
        ListenExt: function (ext) { return SendMsg({ Code: 11, P1: ext }); },
        //插话
        ChipIn: function (ext) { return SendMsg({ Code: 12, P1: ext }); },
        //强拆/截断外线
        BreakTrunk: function (ext) { return SendMsg({ Code: 13, P1: ext }); },
        //强拆/截断坐席
        BreakExt: function (ext) { return SendMsg({ Code: 14, P1: ext }); },
        //三方会议-转分机
        JoinConExt: function (numb) { return SendMsg({ Code: 15, P1: numb }); },
        //三方会议-转外线
        JoinConPhone: function (numb) { return SendMsg({ Code: 15, P1: OutNum+numb, P2: OutNum }); },
        //坐席闭锁
        CloseAgent: function (ext) { return SendMsg({ Code: 18, P1: ext }); },
        //坐席开锁
        OpenAgent: function (ext) { return SendMsg({ Code: 19, P1: ext }); },
        //耳语
        Whisper: function (ext) { return SendMsg({ Code: 20, P1: ext }); },
        //回拨,桥接拨号(拨打历史ID,本机号码)
        BridgeDial: function (ddhid, srcPhone) { return SendMsg({ Code: 21, Body: ddhid, P7: ddhid, P1: (srcPhone||""), P3: OutNum }); },
        //强制置忙(中间服务做,改变分机号)
        SetItBusy: function (ext) { return SendMsg({ Code: 23, P1: ext }); },
        //强制置闲(中间服务做,改变分机号)
        SetItReady: function (ext) { return SendMsg({ Code: 24, P1: ext }); },
        //强制退出
        ForceLogOut: function (ext) { return SendMsg({ Code: 25, P1: ext }); },
        /*
        **扩展消息方法
        */
        //获取所有分机状态
        GetAllExtState:function(){ return SendMsg({ Code: 101, P1: '0' }); },
        //获取指定分机状态
        GetExtState:function(ext){ return SendMsg({ Code: 101, P1: '1', P2: ext }); },
        //获取指定分机组的所有分机状态,参数:分机组组号
        GetExtStateByExtGroup:function(extgroup){ return SendMsg({ Code: 101, P1: '2', P2: extgroup }); },
        //获取指定用户工号组的所有用户(登陆分机的)分机状态,参数:用户工号的组名
        GetExtStateByUserGroup:function(usrgroup){ return SendMsg({ Code: 101, P1: '3', P2: usrgroup }); },
        /*
        **聊天消息
        */
        //聊天,个人
        SendPeer: function (msg, to) { return SendMsg({ Code: 54, To: to, Body: msg.substr(0, 1024) }); },
        //聊天,组
        SendGroup: function (msg, to) { return SendMsg({ Code: 55, To: to, Body: msg.substr(0, 1024) }); },
        /*
        **自动外拨函数
        */
        //启动自动外拨项目(项目ID)
        AutoCallStart: function (PrjID) { if (PrjID) return SendMsg({ Code: 27, P1: PrjID }); else return 0; },
        //停止自动外拨项目(项目ID)
        AutoCallStop: function (PrjID) { if (PrjID) return SendMsg({ Code: 28, P1: PrjID }); else return 0; },
        //获取IVR名字列表
        AutoCallGetIvrs: function () { return SendMsg({ Code: 29 }); },
        //获取启动状态项目列表
        AutoCallGetPrjs: function () { return SendMsg({ Code: 30 }); },
        /*
        **事件入口
        */
        //Socket,接收消息
        OnMsg: function (msgstr, rt) {
            if (rt == null)
                return setTimeout(PbxTransfer.OnMsg(msgstr, true), 0);
            var msg = ToMsg(msgstr);
            if (!isEmptyObject(msg)) {
                switch (msg.Code) {
                    case "25":
                    case 25:
                        State = 5;
                        this.Logout();
                        OnEvent("OnLogout");
                        console.log( "您已退出分机登陆,请点击头像重新登陆");
                        alert("您已被强制退出分机登陆/关联");
                        break;
                    case "31":
                    case 31://LoginOK, 登陆成功
                        State = (msg.Ext.length>2?15:10);
                        OnEvent("OnLoginOK",msg.P3,msg.P1);
                        console.log("登陆成功," + (msg.Ext == "" ? "(未关联分机登陆)" : "分机号:" + msg.Ext) + "  状态:" + msg.P3);
                        break;
                    case "32":
                    case 32://LoginError, 登陆出错
                        ExtNum = "";
                        OnEvent("OnLoginError","是否分机号错误?"+msg.Body);
                        console.log("登陆失败", "登陆失败,分机号:" + msg.Ext + "  是否分机号错误?"+msg.Body);
                        break;
                    case "33":
                    case 33://LoginAgain, 重复登陆
                        ExtNum = "";
                        OnEvent("OnLoginAgain",msg.P1,msg.P2,msg.P3,msg.P4);
                        console.log("登陆失败: 重复登陆  座席IP:" + msg.P4 + "  座席工号:" + msg.P2 + "  座席姓名:" + msg.P3 + "  失败原因:" + msg.P1);
                        break;
                    case "34":
                    case 34://LoginNone, 未登陆
                        ExtNum = "";State = (msg.Ext.length>2?10:5);
                        OnEvent("OnLoginNone",msg.P1,msg.P2,msg.P3,msg.P4);
                        console.log("错误的操作", "错误: 分机关联未登陆的操作  座席IP:" + msg.P4 + "  座席工号:" + msg.P2 + "  座席姓名:" + msg.P3 + "  失败原因:" + msg.P1);
                        break;
                    case "35":
                    case 35://InBound, 呼入来电
                        InCallNum++; InCallNumN++;
                        RecFile = "";
                        if (msg.P6 && msg.P6 != "")//自动外拨项目ID
                            OnEvent("OnAutoCallInbound", msg.P2, msg.P3, msg.P1, msg.p6);
                        else
                            OnEvent("OnInBound", msg.P2, msg.P3, msg.P1);
                        console.log("电话呼入...", "IVR:" + msg.P1 + (msg.P6 != ""?"  (自动外拨项目)":"") +  "  被叫:" + msg.P3 );
                        break;
                    case "36":
                    case 36://InAnswer, 呼入接听
                        InCallNumN--;
                        RecFile = msg.Body;
                        var dt = msg.DT || (new Date).toString("yyyy-MM-dd hh:mm:ss");
                        OnEvent("OnAnswered", "in", RecFile, dt);
                        console.log("呼入应答...","被叫:" + msg.P3 + "  IVR:" + msg.P1 + "  分机状态:" + msg.P5 );
                        break;
                    case "37":
                    case 37://BusyCallIn, 遇忙呼入
                        InCallBusyNum++;
                        OnEvent("OnBusyCallIn", "in", RecFile, dt);
                        console.log("遇忙呼入提示", "来电号码:" + msg.P2 + "  IVR名称:" + msg.P1);
                        break;
                    case "38":
                    case 38://OutBound, 呼出拨号
                        OutCallNum++; OutCallNumN++;
                        OnEvent("OnOutBound",msg.Body,msg.P1);
                        console.log("正在拨号...","分机状态:" + msg.P3 );
                        break;
                    case "39":
                    case 39://OutAnswer, 呼出接听
                        OutCallNumN--;
                        RecFile = msg.Body;//.replace("\\", "\\\\");
                        var dt = msg.DT || (new Date).toString("yyyy-MM-dd hh:mm:ss");
                        OnEvent("OnAnswered", "out", RecFile, dt);
                        console.log("呼出应答...", "分机:" + msg.P2 + "  录音:" + msg.Body );
                        break;
                    case "40":
                    case 40://PickUp, 分机摘机
                         OnEvent("OnPickUp");
                        console.log("话机摘机...", "分机:" + msg.P3 + "  就绪:" + (msg.P1 == "1" ? "空闲" : "置忙") );
                        break;
                    case "41":
                    case 41://HangUp, 分机挂机
                        OnEvent("OnHangUp", "in", msg.P1 ,msg.DT);
                        console.log("话机挂机...", "分机:" + msg.P3 + "  就绪:" + (msg.P1 == "1" ? "空闲" : "置忙") );
                        break;
                    case "42":
                    case 42://OpHangUp, 外线挂机
                        OnEvent("OnHangUp", "out", msg.P1, msg.DT);
                        console.log("对方挂机...", "分机:" + msg.P3 + "  就绪:" + (msg.P1 == "1" ? "空闲" : "置忙") );
                        break;
                    case "43":
                    case 43://StateChange, 状态改变
                        OnEvent("OnStateChange", msg.P1);
                        console.log("分机状态更改", "分机:" + msg.P3 + "  就绪:" + (msg.P1 == "1" ? "空闲" : "置忙") );
                        break;
                    case "44":
                    case 44://非座席分机不允许登陆
                        OnEvent("OnLoginError","非座席分机,不能登陆分机");
                        console.log("非座席分机,不能登陆分机");
                        break;
                    case "45":
                    case 45://未登录座席不允许外拨
                        OnEvent("OnMakeCallFalse","未登录座席不允许外拨",msg.Body+msg.P1);
                        console.log("未登录 或 未登陆异常,不能拨号  请确认分机号并重新登陆");
                        break;
                    case "46":
                    case 46://排队,队列变化
                        OnEvent("OnQueue", msg.P1,msg.P2,msg.P3);
                        console.log("分机组:"+msg.P1+"  队列:"+msg.P2+"  队列数量:"+(msg.P3.length>0?msg.P3:msg.P2.split(",").length));
                        break;
                    /*
                    **自动外拨函数
                    */
                    case "48"://接收IVR名字列表
                    case 48://Body:IVR名字列表(分号分割)
                        //调用AutoCallGetIvrs方法的页面要实现"OnAutoCallGetIvrs(Array Ivrs)"方法
                        //var str = msg.Body.replace(/;;/g, "");
                        //if (str.substring(str.length - 1, str.length) == ";")
                        //    str = str.substring(0, str.length - 1)
                        console.info(msgstr);
                        OnEvent("OnAutoCallGetIvrs", msg.Body);
                        break;
                    case "49"://自动外拨项目状态
                    case 49://P1:ID P2:Started/Stoped
                        //调用AutoCallState方法的页面要实现"OnAutoCallState(PrjID,bool,str)"方法
                        console.info(msgstr);
                        OnEvent("OnAutoCallState", msg.P1, (msg.P2 == "Started"), msg.P3);
                        break;
                    case "50"://启动状态项目列表
                    case 50://Body:已启动的项目ID(竖线分割)
                        //调用AutoCallGetPrjs方法的页面要实现"OnAutoCallGetPrjs(Array PrjIds)"方法
                        //var str = msg.Body.replace(/\|\|/g, "");
                        //if (str.substring(str.length - 1, str.length) == "|")
                        //    str = str.substring(0, str.length - 1)
                        console.info(msgstr);
                        OnEvent("OnAutoCallGetPrjs", msg.Body);//
                        break;
                    /*
                    **以下为扩展消息
                    */
                    case "51":
                    case 51://普通消息 type=5
                        console.log(msg.Body,msg.P1,null,60000);
                        break;
                    case "52":
                    case 52://MakeCallOk, 正在拨号
                        OnEvent("OnMakeCallOK");
                        console.log("拨号中...");
                        break;
                    case "53":
                    case 53://MakeCallFalse, 寻找号码失败,或错误的号码
                        OnEvent("OnMakeCallFalse",msg.Body,msg.P1);
                        console.info("错误","拨号失败:"+msg.Body+msg.P1);
                        break;
                    case "54":
                    case 54://Peer Msg, 单聊
                        OnEvent("OnPeerMsg");
                        console.log( msg.From + " 发来信息: "+msg.Body);
                        break;
                    case "55":
                    case 55://Group Msg, 群聊
                        OnEvent("OnGroupMsg");
                        console.log("组消息  来自:" + msg.From, msg.Body );
                        break;
                    case "56":
                    case 56://Everyone Msg, 公告
                        OnEvent("OnPublicMsg");
                        console.log("公告  :" + msg.P1, msg.Body, "  来自:" + msg.From+" 时间:" + msg.P2 );
                        break;
                    case "57":
                    case 57://系统通知
                        OnEvent("OnSysMsg");
                        console.log("系统通知  来自:" + msg.From, msg.Body, + "  时间:" + msg.DT );
                        break;
                    case "58":
                    case 58://本分机状态和本分机组的队列信息
                        OnEvent("OnExtGroupInfo", msg.Body, msg.DT);
                        break;
                    case '59':
                    case 59://分机状态信息,每次单个分机状态
                        OnEvent("OnExtInfo", msg.Body, msg.DT);
                        break;
                    case '60':
                    case 60://微信消息(OpenID,消息,消息类型,时间)
                        OnEvent("OnWXmsgInfo", msg.P1, msg.Body, msg.P2, msg.DT);
                        break;
                    case '61':
                    case 61://微信用户待接入(OpenID,昵称,时间)
                        OnEvent("OnWXuserIncomming", msg.P1, msg.Body, msg.DT);
                        break;

                    //操作响应消息
                    case '71':
                    case 71://    Conference     加入会议成功
                        OnEvent("OnConference");
                        break;
                    case '72':
                    case 72://    ListenError    监听失败
                        OnEvent("OnListenExt",false);
                        break;
                    case '73':
                    case 73://    ListenTalk    监听成功
                        OnEvent("OnListenExt",true);
                        break;
                    case '74':
                    case 74://    InsertTalk    插话成功
                        OnEvent("OnChipIn");
                        break;
                    case '75':
                    case 75://    BreakTrunk    截断外线成功
                        OnEvent("OnBreakTrunk");
                        break;
                    case '76':
                    case 76://    BreakExt    截断分机成功
                        OnEvent("OnBreakExt");
                        break;
                    case '0':
                    case 0:
                        OnEvent("OnError",msg.Body);
                        console.info(msg.Body);
                        break;
                    default:
                        console.log("defult",msg);
                        console.info("未知的消息", "未知的消息/命令: " + msg.Code + "," + msg.Body + "," + msg.P1 );
                }
            }
        }
    };
}();