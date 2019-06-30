package com.bug1024.metralog.consumer.task;

import com.dianping.cat.message.spi.MessageQueue;
import com.dianping.cat.message.spi.MessageTree;
import lombok.extern.slf4j.Slf4j;

/**
 * @author bug1024
 * @date 2019-06-30
 */
@Slf4j
public class TransactionAnalyzer implements MessageAnalyzer{
    @Override
    public void process(MessageQueue messageQueue) {
        while (true) {
            MessageTree messageTree = messageQueue.poll();
            if (messageTree != null) {
                log.info("tree:{}", messageTree);
            }
        }
    }
}
