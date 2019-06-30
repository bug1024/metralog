package com.bug1024.metralog.consumer.transaction.model;

import com.bug1024.metralog.consumer.transaction.model.entity.*;

public interface IVisitor {

   public void visitAllDuration(AllDuration allDuration);

   public void visitDuration(Duration duration);

   public void visitGraph(Graph graph);

   public void visitGraph2(Graph2 graph2);

   public void visitGraphTrend(GraphTrend graphTrend);

   public void visitMachine(Machine machine);

   public void visitName(TransactionName name);

   public void visitRange(Range range);

   public void visitRange2(Range2 range2);

   public void visitStatusCode(StatusCode statusCode);

   public void visitTransactionReport(TransactionReport transactionReport);

   public void visitType(TransactionType type);
}
