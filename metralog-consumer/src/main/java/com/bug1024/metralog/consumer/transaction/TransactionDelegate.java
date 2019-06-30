package com.bug1024.metralog.consumer.transaction;


import com.bug1024.metralog.consumer.ReportDelegate;
import com.bug1024.metralog.consumer.transaction.model.entity.TransactionReport;

import java.util.Date;

public class TransactionDelegate implements ReportDelegate {

	@Override
	public TransactionReport makeReport(String domain, long startTime, long duration) {
		TransactionReport report = new TransactionReport(domain);

		report.setStartTime(new Date(startTime));
		report.setEndTime(new Date(startTime + duration - 1));

		return report;
	}

}
