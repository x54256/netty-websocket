package cn.x5456.netty.websocket.initializer;

import cn.x5456.netty.websocket.handler.WebSocketFrameHandler;
import cn.x5456.netty.websocket.utils.AppContextUtil;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Configuration
public class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {

//    @Autowired
//    private WebSocketFrameHandler webSocketFrameHandler;

    @Value("${netty.server.readerIdleTime}")
    private Integer readerIdleTime;

    @Value("${netty.server.writerIdleTime}")
    private Integer writerIdleTime;

    @Value("${netty.server.allIdleTime}")
    private Integer allIdleTime;


    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new HttpObjectAggregator(65535));
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        pipeline.addLast(new IdleStateHandler(readerIdleTime == null ? 60:readerIdleTime,
                writerIdleTime == null ? 45:writerIdleTime,
                allIdleTime == null ? 20:allIdleTime,
                TimeUnit.SECONDS));

//        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
//        WebSocketFrameHandler handler = context.getBean(WebSocketFrameHandler.class);

        WebSocketFrameHandler webSocketFrameHandler = AppContextUtil.getBean(WebSocketFrameHandler.class);
//        System.out.println("webSocketFrameHandler = " + webSocketFrameHandler);
        pipeline.addLast(webSocketFrameHandler);
    }
}
