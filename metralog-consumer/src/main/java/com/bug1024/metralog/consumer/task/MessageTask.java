package com.bug1024.metralog.consumer.task;

import com.dianping.cat.message.spi.MessageQueue;
import com.dianping.cat.message.spi.MessageTree;

/**
 * @author bug1024
 * @date 2019-06-30
 */
public class MessageTask implements Runnable {

    private MessageQueue messageQueue;

    private MessageAnalyzer messageAnalyzer;

    public MessageTask(MessageQueue messageQueue, MessageAnalyzer messageAnalyzer) {
        this.messageQueue = messageQueue;
        this.messageAnalyzer = messageAnalyzer;
    }

    public boolean enqueue(MessageTree messageTree) {
        return messageQueue.offer(messageTree);
    }

    @Override
    public void run() {
        messageAnalyzer.process(messageQueue);
    }
}
