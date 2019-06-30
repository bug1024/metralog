package com.bug1024.metralog.consumer.transaction.model.transform;

import com.bug1024.metralog.consumer.transaction.model.entity.*;

public interface IParser<T> {
   public TransactionReport parse(IMaker<T> maker, ILinker linker, T node);

   public void parseForAllDuration(IMaker<T> maker, ILinker linker, AllDuration parent, T node);

   public void parseForDuration(IMaker<T> maker, ILinker linker, Duration parent, T node);

   public void parseForGraph(IMaker<T> maker, ILinker linker, Graph parent, T node);

   public void parseForGraph2(IMaker<T> maker, ILinker linker, Graph2 parent, T node);

   public void parseForGraphTrend(IMaker<T> maker, ILinker linker, GraphTrend parent, T node);

   public void parseForMachine(IMaker<T> maker, ILinker linker, Machine parent, T node);

   public void parseForTransactionName(IMaker<T> maker, ILinker linker, TransactionName parent, T node);

   public void parseForRange(IMaker<T> maker, ILinker linker, Range parent, T node);

   public void parseForRange2(IMaker<T> maker, ILinker linker, Range2 parent, T node);

   public void parseForStatusCode(IMaker<T> maker, ILinker linker, StatusCode parent, T node);

   public void parseForTransactionType(IMaker<T> maker, ILinker linker, TransactionType parent, T node);
}
