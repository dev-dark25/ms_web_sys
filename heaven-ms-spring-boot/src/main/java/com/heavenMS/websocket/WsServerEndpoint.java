package com.heavenMS.websocket;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/myWs/{id}")
@Component
public class WsServerEndpoint {

    private static int onlineCount = 0;

    private static Map<String, WsServerEndpoint> clients = new ConcurrentHashMap<>();

    private Session session;

    private String id;

    /**
     * 连接成功
     *
     * @param session
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("id") String id) {
        System.out.println(this);
        this.session = session;
        this.id = id;
        if (clients.containsKey(id)) {
            System.out.println("id is existing, replace to new link...");
        }
        clients.put(id, this);
        addOnlineCount();
        System.out.println("connection successful");
    }

    /**
     * 连接关闭
     *
     * @param session
     */
    @OnClose
    public void onClose(Session session) {
        System.out.println("connection closed");
    }

    /**
     * 接收到消息
     *
     * @param text
     */
    @OnMessage
    public String onMsg(String text) {
        System.out.println("client send msg: " + text);
        return "serve send: " + "serve";
    }

    //发送消息
    public void sendMessageTo(String id, String message) throws IOException {
        for (WsServerEndpoint item : clients.values()) {
            if (item.id.equals(id)) {
//                item.session.getAsyncRemote().sendText(message);
                item.session.getBasicRemote().sendText(message);
                break;
            }
        }
    }

    //群发消息
    public void sendMessageAll(String message) {
        for (WsServerEndpoint item : clients.values()) {
//            item.session.getAsyncRemote().sendText(message);
            try {
                item.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //操作onlineCount，使用synchronized确保线程安全
    private static synchronized int getOnlineCount() {
        return onlineCount;
    }

    //操作onlineCount，使用synchronized确保线程安全
    private static synchronized void addOnlineCount() {
        WsServerEndpoint.onlineCount++;
    }

    //操作onlineCount，使用synchronized确保线程安全
    private static synchronized void subOnlineCount() {
        WsServerEndpoint.onlineCount--;
    }
}
