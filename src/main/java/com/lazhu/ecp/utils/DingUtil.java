package com.lazhu.ecp.utils;

import java.io.IOException;
import java.util.ArrayList;

import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.CorpMessageCorpconversationAsyncsendRequest;
import com.dingtalk.api.response.CorpMessageCorpconversationAsyncsendResponse;
import com.dingtalk.chatbot.DingtalkChatbotClient;
import com.dingtalk.chatbot.SendResult;
import com.dingtalk.chatbot.message.MarkdownMessage;
import com.dingtalk.chatbot.message.Message;
import com.dingtalk.chatbot.message.TextMessage;
import com.lazhu.core.util.PropertiesUtil;
import com.mysql.fabric.xmlrpc.base.Data;
import com.taobao.api.ApiException;

import sun.util.logging.resources.logging;

/**
 * 钉钉消息工具类
 * @author phong
 *
 */
public class DingUtil {

	/**
	 * 发送dd消息
	 * @param content
	 * @param ddid
	 * @return
	 */
	public static String sendMsg(String content, String ddid) {
		DingTalkClient client = new DefaultDingTalkClient("https://eco.taobao.com/router/rest");
		CorpMessageCorpconversationAsyncsendRequest req = new CorpMessageCorpconversationAsyncsendRequest();
		req.setMsgtype("text");
		req.setAgentId(Long.parseLong(PropertiesUtil.getString("dd.token.agentId")));
		req.setUseridList(ddid);//钉钉ID
		req.setToAllUser(false);
		JSONObject jo = new JSONObject();
		jo.put("content", content);
		req.setMsgcontentString(jo.toJSONString());
		try {
			CorpMessageCorpconversationAsyncsendResponse rsp = client.execute(req, DDTokenUtil.getAccessToken());
			return rsp.getBody();
		} catch (ApiException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 发送群组消息
	 * @param group 群名
	 * @param content 发送内容
	 * @param mobile @谁
	 */
	public static void sendGroupMesg(String group, Message message) {
		try {
			String url = PropertiesUtil.getString(group);
			DingtalkChatbotClient client = new DingtalkChatbotClient();
			SendResult result = client.send(url, message);
	        //System.out.println(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
