package cn.x5456.netty.websocket.handler;

import cn.x5456.netty.websocket.constant.WebSocketMsgStatusConstant;
import cn.x5456.netty.websocket.pojo.WebSocketMsg;
import cn.x5456.netty.websocket.utils.JsonUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class WebSocketFrameHandler extends SimpleChannelInboundHandler<Object> {

    private static int retryNum;

    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof WebSocketFrame) {
            retryNum = 0;   // 重置
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    /**
     * 客户端连接刚刚建立时
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        // 广播到组内到所有到channel
        channelGroup.writeAndFlush(new TextWebSocketFrame("[客户端] - " + channel.remoteAddress() + "加入\n"));
        // 将新连接的channel加入
        channelGroup.add(channel);

        log.info("客户端{}加入",channel.remoteAddress());
    }


    /**
     * 客户端断开连接时调用
     *  netty会自动将断开的channel从channelGroup移除
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 获取断开连接的客户端的channel对象
        Channel channel = ctx.channel();
        // 广播到组内到所有到channel
        channelGroup.writeAndFlush(new TextWebSocketFrame("[客户端] - " + channel.remoteAddress() + "离开\n"));
        log.info("客户端{}断开连接",channel.remoteAddress());
    }



    /**
     * 处理Socket请求
     * @param ctx
     * @param frame
     * @throws Exception
     */
    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        if (frame instanceof TextWebSocketFrame) {
            String msg = ((TextWebSocketFrame) frame).text();

            WebSocketMsg webSocketMsg = JsonUtils.toBean(msg, WebSocketMsg.class);

            // 如果是普通消息，则回显，回显的也是json格式的
            if (webSocketMsg != null && webSocketMsg.getState() == WebSocketMsgStatusConstant.COMMON_MSG) {
                // {"from":"123","msg":"abv","sendTime":1545048367425,"state":10000}
                channelGroup.writeAndFlush(new TextWebSocketFrame(msg));
            }
//            channelGroup.forEach(x -> {
//                if (x != ctx.channel()){
//                    x.writeAndFlush(new TextWebSocketFrame(webSocketMsg.getFrom() + "发送的消息为：" + webSocketMsg.getMsg() + "\n"));
//                } else {
//                    x.writeAndFlush(new TextWebSocketFrame("[自己] " + webSocketMsg.getMsg() + "\n"));
//                }
//            });
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        log.error("异常发生");
        ctx.close();    // 关闭连接
    }

    /**
     * 触发了某一个 事件 后，会调用
     * @param ctx 上下文对象
     * @param evt 事件对象
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;

            String eventType = null;
            switch (event.state()){
                case READER_IDLE:
                    eventType = "读空闲";
                    retryNum++;
                    if (retryNum > 2) ctx.channel().close();
                    break;
                case WRITER_IDLE:
                    eventType = "写空闲";
                    break;
                case ALL_IDLE:
                    eventType = "读写空闲";

                    String pong = JsonUtils.toString(new WebSocketMsg("pong", WebSocketMsgStatusConstant.PONG));
                    ctx.writeAndFlush(new TextWebSocketFrame(pong));
                    break;
            }

            log.info(ctx.channel().remoteAddress() + "的超时事件为：" + eventType);
        }
    }
}
