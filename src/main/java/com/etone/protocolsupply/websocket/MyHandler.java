package com.etone.protocolsupply.websocket;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * 相当于controller的处理器
 */
public class MyHandler extends TextWebSocketHandler {
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
// Map<String, String> map = JSONObject.parseObject(payload, HashMap.class);
// System.out.println("=====接受到的数据"+map);
        session.sendMessage(new TextMessage("服务器返回收到的信息," + payload));
    }
}

