package com.bug1024.metralog.consumer;

import com.dianping.cat.message.spi.MessageQueue;
import com.dianping.cat.message.spi.MessageTree;

/**
 * @author bug1024
 * @date 2019-06-30
 */
public class MessageTask implements Runnable {

    private MessageQueue messageQueue;

    private MessageAnalyzer messageAnalyzer;

    private long startTime;

    public MessageTask(MessageQueue messageQueue, MessageAnalyzer messageAnalyzer, long starTime) {
        this.messageQueue = messageQueue;
        this.messageAnalyzer = messageAnalyzer;
        this.startTime = starTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public MessageAnalyzer getMessageAnalyzer() {
        return messageAnalyzer;
    }

    public MessageQueue getMessageQueue() {
        return messageQueue;
    }

    public boolean enqueue(MessageTree messageTree) {
        return messageQueue.offer(messageTree);
    }

    @Override
    public void run() {
        messageAnalyzer.process(messageQueue);
    }
}
