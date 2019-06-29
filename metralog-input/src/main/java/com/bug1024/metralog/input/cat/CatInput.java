package com.bug1024.metralog.input.cat;

import com.bug1024.metralog.input.MetralogInput;
import com.dianping.cat.message.io.ClientMessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * tcp server
 * @author bug1024
 * @date 2019-06-29
 */
public class CatInput implements MetralogInput {

    private static class CatInputWrapper {
        final static CatInput instance = new CatInput();
    }

    public static CatInput getInstance() {
        return CatInputWrapper.instance;
    }

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;;
    private ServerBootstrap serverBootstrap;
    private ChannelFuture channelFuture;

    public CatInput() {
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();
        this.serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addLast("decode", new MessageDecoder());
                        pipeline.addLast("encode", new ClientMessageEncoder());
                    }
                });

        serverBootstrap.childOption(ChannelOption.SO_REUSEADDR, true);
        serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        serverBootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
    }

    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void init() {
        this.channelFuture = this.serverBootstrap.bind(getPort());
    }
}
