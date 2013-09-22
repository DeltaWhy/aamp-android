package net.miscjunk.aamp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class EventServerHandler extends ChannelInboundHandlerAdapter {
    EventServer server;
    public EventServerHandler(EventServer server) {
        this.server = server;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        server.channels.add(ctx);
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("connection closed");
        server.channels.remove(ctx);
        ctx.close();
    }
}
