package com.bug1024.metralog.consumer;

import com.dianping.cat.message.spi.MessageQueue;

/**
 * @author bug1024
 * @date 2019-06-30
 */
public interface MessageAnalyzer {

    void process(MessageQueue messageQueue);

    ReportManager getReportManager();
}
