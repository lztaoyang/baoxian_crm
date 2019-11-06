package com.lazhu.core.support.websocket;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.lazhu.core.util.WebUtil;
 
 
/**
 * Socket建立连接（握手）和断开
 * 
 * @author naxj
 */
public class HandShake implements HandshakeInterceptor {
 
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        System.out.println("Websocket:用户[ID:" + ((ServletServerHttpRequest) request).getServletRequest().getSession(false).getAttribute("uid") + "]已经建立连接");
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            @SuppressWarnings("unused")
			HttpSession session = servletRequest.getServletRequest().getSession(false);
            // 标记用户
//            Long uid = (Long) session.getAttribute("uid");
//            SessionUtil.getAttribute(request, "wxUserName", new String());

            Long userId = WebUtil.getCurrentUser();
            if(userId!=null){
                attributes.put("userId", userId);
            }else{
                return false;
            }
        }
        return true;
    }
 
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }
 
}