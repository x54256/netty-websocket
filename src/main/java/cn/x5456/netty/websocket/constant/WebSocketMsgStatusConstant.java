package cn.x5456.netty.websocket.constant;

public class WebSocketMsgStatusConstant {

    public static final int COMMON_MSG = 10000;    // 正常消息

    public static final int PING = 10001;    // 客户端向服务器发出的消息

    public static final int PONG = 10002;   // 服务器向客户端发出的检测消息
}
