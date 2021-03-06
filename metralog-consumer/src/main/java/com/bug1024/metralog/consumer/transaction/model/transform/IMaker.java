package com.bug1024.metralog.consumer.transaction.model.transform;

import com.bug1024.metralog.consumer.transaction.model.entity.*;

public interface IMaker<T> {

   public AllDuration buildAllDuration(T node);

   public String buildDomain(T node);

   public Duration buildDuration(T node);

   public Graph buildGraph(T node);

   public Graph2 buildGraph2(T node);

   public GraphTrend buildGraphTrend(T node);

   public String buildIp(T node);

   public Machine buildMachine(T node);

   public TransactionName buildName(T node);

   public Range buildRange(T node);

   public Range2 buildRange2(T node);

   public StatusCode buildStatusCode(T node);

   public TransactionReport buildTransactionReport(T node);

   public TransactionType buildType(T node);
}
