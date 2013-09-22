package net.miscjunk.aamp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import net.miscjunk.aamp.common.ServerListener;

public class ServerEventListener {
    ListenerThread thread;
    List<ServerListener> listeners;
    String host;
    int port;
    
    public ServerEventListener(String host, int port) {
        this.host = host;
        this.port = port;
        listeners = new ArrayList<ServerListener>();
    }

    public void addServerListener(ServerListener listener) {
        listeners.add(listener);
    }
    public void removeServerListener(ServerListener listener) {
        listeners.remove(listener);
    }
    
    public boolean start() {
        thread = new ListenerThread();
        thread.start();
        return true;
    }
    public boolean stop() {
        return false;
    }
    
    void onServerEvent(final String message) {
        for (final ServerListener l : listeners) {
            if (l instanceof Activity) {
                ((Activity)l).runOnUiThread(new Runnable() {
                    public void run() {
                        l.onServerEvent(message);
                    }
                });
            } else {
                l.onServerEvent(message);
            }
        }
    }
    
    class ListenerThread extends Thread {
        public void run() {
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            
            try {
                Bootstrap b = new Bootstrap();
                b.group(workerGroup);
                b.channel(NioSocketChannel.class);
                b.option(ChannelOption.SO_KEEPALIVE, true);
                b.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LineBasedFrameDecoder(10000, true, true),
                                new StringDecoder(), new EventHandler());
                    }
                });
                
                ChannelFuture f = b.connect(host, port).sync();

                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
            } 
        }
    }
    
    class EventHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg)
                throws Exception {
            onServerEvent((String)msg);
        }
        
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
