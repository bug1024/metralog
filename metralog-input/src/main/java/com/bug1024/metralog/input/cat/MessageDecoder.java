package com.bug1024.metralog.input.cat;

import com.dianping.cat.message.io.BufReleaseHelper;
import com.dianping.cat.message.spi.internal.DefaultMessageTree;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * CatMessageDecoder
 * @author bug1024
 * @date 2019-06-29
 */
@Slf4j
public class MessageDecoder extends ByteToMessageDecoder {
    private long m_processCount;

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

                // todo handle tree
                log.info("tree:{}", tree);
            } else {
                // client message is error
                buffer.readBytes(length);
                BufReleaseHelper.release(buffer);
            }
        } catch (Exception e) {
            log.error("decode message error", e);
            //m_serverStateManager.addMessageTotalLoss(1);
            //m_logger.error(e.getMessage(), e);
        }
    }
}
