package cn.wolfcode.cloud.shop.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/{uuid}")
@Component
public class WebSocketServer {
    public static ConcurrentHashMap<String, Session> clients = new ConcurrentHashMap<>();

    private final Logger log = LoggerFactory.getLogger("WEB_SOCKET_SERVER");

    @OnOpen
    public void onOpen(Session session, @PathParam("uuid") String uuid) {
        log.info("客户端：" + uuid + "连接上来了");
        clients.put(uuid, session);
    }

    @OnClose
    public void onClose(@PathParam("uuid") String uuid) {
        log.info("客户端：" + uuid + "断开连接了");
        clients.remove(uuid);
    }

    @OnError
    public void onError(Throwable error) {
        error.printStackTrace();
    }
}