package com.bug1024.metralog.home.controller;

import com.bug1024.metralog.consumer.MessageTask;
import com.bug1024.metralog.consumer.MetralogConsumer;
import com.bug1024.metralog.consumer.ReportManager;
import com.bug1024.metralog.consumer.transaction.model.entity.TransactionReport;
import com.dianping.cat.message.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author bug1024
 * @date 2019-05-12
 */
@RestController
@RequestMapping("/demo")
public class DemoController {


    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @GetMapping("/t")
    public TransactionReport t() {
        TransactionReport report = MetralogConsumer.messageTask.getMessageAnalyzer().getReportManager().getHourlyReport(123, "haofang", false);
        return report;
    }
}
