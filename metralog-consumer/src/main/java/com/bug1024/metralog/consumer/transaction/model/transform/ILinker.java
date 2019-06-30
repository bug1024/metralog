package com.bug1024.metralog.consumer.transaction.model.transform;

import com.bug1024.metralog.consumer.transaction.model.entity.*;

public interface ILinker {

   public boolean onAllDuration(TransactionType parent, AllDuration allDuration);

   public boolean onAllDuration(TransactionName parent, AllDuration allDuration);

   public boolean onAllDuration(Range parent, AllDuration allDuration);

   public boolean onAllDuration(Range2 parent, AllDuration allDuration);

   public boolean onDuration(TransactionName parent, Duration duration);

   public boolean onGraph(TransactionName parent, Graph graph);

   public boolean onGraph2(TransactionType parent, Graph2 graph2);

   public boolean onGraphTrend(TransactionType parent, GraphTrend graphTrend);

   public boolean onGraphTrend(TransactionName parent, GraphTrend graphTrend);

   public boolean onMachine(TransactionReport parent, Machine machine);

   public boolean onName(TransactionType parent, TransactionName name);

   public boolean onRange(TransactionName parent, Range range);

   public boolean onRange2(TransactionType parent, Range2 range2);

   public boolean onStatusCode(TransactionName parent, StatusCode statusCode);

   public boolean onType(Machine parent, TransactionType type);
}
