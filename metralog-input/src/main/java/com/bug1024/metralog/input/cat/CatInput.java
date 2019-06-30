package com.bug1024.metralog.input.cat;

import com.bug1024.metralog.consumer.MetralogConsumer;
import com.bug1024.metralog.input.MetralogInput;
import com.dianping.cat.message.io.BufReleaseHelper;
import com.dianping.cat.message.io.ClientMessageEncoder;
import com.dianping.cat.message.spi.internal.DefaultMessageTree;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * tcp server
 * @author bug1024
 * @date 2019-06-29
 */
@Slf4j
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
        int threads = 24;
        this.bossGroup = new NioEventLoopGroup(threads);
        this.workerGroup = new NioEventLoopGroup(threads);
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

    private MetralogConsumer metralogConsumer;

    public MetralogConsumer getMetralogConsumer() {
        return metralogConsumer;
    }

    public void setMetralogConsumer(MetralogConsumer metralogConsumer) {
        this.metralogConsumer = metralogConsumer;
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


    public class MessageDecoder extends ByteToMessageDecoder {

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
            if (buffer.readableBytes() < 4) {
                return;
            }
            buffer.markReaderIndex();
            int length = buffer.readInt();
            buffer.resetReaderIndex();
            if (buffer.readableBytes() < length + 4) {
                return;
            }
            try {
                if (length > 0) {
                    ByteBuf readBytes = buffer.readBytes(length + 4);

                    readBytes.markReaderIndex();

                    DefaultMessageTree tree = (DefaultMessageTree) CodecHandler.decode(readBytes);

                    readBytes.resetReaderIndex();
                    tree.setBuffer(readBytes);

                    // handle message tree
                    metralogConsumer.handle(tree);
                } else {
                    // client message is error
                    buffer.readBytes(length);
                    BufReleaseHelper.release(buffer);
                }
            } catch (Exception e) {
                log.error("decode message error", e);
            }
        }
    }
}
