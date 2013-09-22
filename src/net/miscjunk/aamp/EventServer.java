package net.miscjunk.aamp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.ArrayList;
import java.util.List;

public class EventServer {
    int port;
    ListenerThread thread;
    List<ChannelHandlerContext> channels;

    public EventServer(int port) {
        this.port = port;
        this.channels = new ArrayList<ChannelHandlerContext>();
    }
    public boolean start() {
        System.out.println("EventServer listening on "+this.port);
        thread = new ListenerThread(this);
        thread.start();
        return true;
    }
    public boolean stop() {
        if (thread != null) {
            try {
                thread.close();
                thread.join();
                System.out.println("Cleaned up EventServer");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
    public boolean sendMessage(String message) {
        for (ChannelHandlerContext ctx : channels) {
            ByteBuf buf = ctx.alloc().buffer();
            buf.writeBytes(message.getBytes());
            ctx.writeAndFlush(buf);
        }
        return true;
    }
    
    class ListenerThread extends Thread {
        EventServer server;
        Channel serverChannel;
        public ListenerThread(EventServer server) {
            this.server = server;
        }
        public void run() {
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new EventServerHandler(server));
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

                ChannelFuture f = b.bind(port).sync();

                serverChannel = f.channel();
                serverChannel.closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        }
        public void close() throws InterruptedException {
            if (serverChannel != null) {
                serverChannel.close().sync();
            }
        }
    }
}
