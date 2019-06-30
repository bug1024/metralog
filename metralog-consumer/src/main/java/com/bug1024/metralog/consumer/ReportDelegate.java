
package com.bug1024.metralog.consumer;

import com.bug1024.metralog.consumer.transaction.model.entity.TransactionReport;

public interface ReportDelegate<T> {
//	void afterLoad(Map<String, T> reports);
//
//	void beforeSave(Map<String, T> reports);
//
//	byte[] buildBinary(T report);
//
//	T parseBinary(byte[] bytes);
//
//	String buildXml(T report);
//
//	String getDomain(T report);
//
//	T mergeReport(T old, T other);
//
//	T parseXml(String xml) throws Exception;
//
//	boolean createHourlyTask(T report);

	TransactionReport makeReport(String domain, long startTime, long duration);


}