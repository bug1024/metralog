package com.bug1024.metralog.consumer;

import com.bug1024.metralog.consumer.task.MessageTask;
import com.bug1024.metralog.consumer.task.TransactionAnalyzer;
import com.dianping.cat.message.queue.DefaultMessageQueue;
import com.dianping.cat.message.spi.MessageTree;
import com.dianping.cat.util.Threads;
import lombok.extern.slf4j.Slf4j;

/**
 * @author bug1024
 * @date 2019-06-30
 */
@Slf4j
public class MetralogConsumer {

    private static final MessageTask messageTask = new MessageTask(new DefaultMessageQueue(30000), new TransactionAnalyzer());
    static {
        Threads.forGroup("metralog-task").start(messageTask);
    }

    public void handle(MessageTree messageTree) {
        messageTask.enqueue(messageTree);
    }

}
