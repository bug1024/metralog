package com.bug1024.metralog.consumer;

import com.bug1024.metralog.consumer.transaction.TransactionDelegate;
import com.bug1024.metralog.consumer.transaction.model.entity.TransactionReport;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bug1024
 * @date 2019-06-30
 */
public class ReportManager {

    public static final long HOUR = 60 * 60 * 1000L;

    private ReportDelegate reportDelegate = new TransactionDelegate();

    public ReportDelegate getReportDelegate() {
        return reportDelegate;
    }

    /**
     * Map<startTime, Map<domain, report>>
     */
    private Map<Long, Map<String, TransactionReport>> reportsMap = new ConcurrentHashMap<>();

    public TransactionReport getHourlyReport(long startTime, String domain, boolean createIfNotExist) {
        Map<String, TransactionReport> reports = reportsMap.get(startTime);

        if (reports == null && createIfNotExist) {
            synchronized (reportsMap) {
                reports = reportsMap.get(startTime);

                if (reports == null) {
                    reports = new ConcurrentHashMap<>();
                    reportsMap.put(startTime, reports);
                }
            }
        }

        if (reports == null) {
            reports = new LinkedHashMap<String, TransactionReport>();
        }

        TransactionReport report = reports.get(domain);

        if (report == null && createIfNotExist) {
            //synchronized (reports) {
                report = reportDelegate.makeReport(domain, startTime, HOUR);
                reports.put(domain, report);
            //}
        }

        if (report == null) {
            report = reportDelegate.makeReport(domain, startTime, HOUR);
        }

        return report;
    }

}
