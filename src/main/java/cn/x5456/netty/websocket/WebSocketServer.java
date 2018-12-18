package cn.x5456.netty.websocket;

import cn.x5456.netty.websocket.initializer.WebSocketChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class WebSocketServer {

    @Value("${netty.server.port}")
    private Integer port;

    @Autowired
    private WebSocketChannelInitializer webSocketChannelInitializer;

    public void setPort(Integer port) {
        this.port = port;
    }

    private NioEventLoopGroup bossGroup = new NioEventLoopGroup();
    private NioEventLoopGroup workerGroup = new NioEventLoopGroup();

    private ServerBootstrap serverBootstrap = new ServerBootstrap();

    private void startServer() {

        try {
            serverBootstrap.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO)) // handler是对于bossGroup施加的
                    .childHandler(webSocketChannelInitializer);
    //                .option(ChannelOption.SO_BACKLOG,1024)  // 客户端存放的最大线程数
    //                .childOption(ChannelOption.SO_KEEPALIVE,true);
            ChannelFuture channelFuture = serverBootstrap.bind(port == null ? 8899:port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

//    public static void main(String[] args) {
//        new WebSocketServer().startServer();
//    }
    @PostConstruct
    public void init(){
        //需要开启一个新的线程来执行netty server 服务器
        new Thread(() -> startServer()).start();
    }

}
