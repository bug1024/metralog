package com.bug1024.metralog.consumer.transaction.model.transform;

import com.bug1024.metralog.consumer.transaction.model.entity.*;

import java.util.ArrayList;
import java.util.List;

public class DefaultLinker implements ILinker {
   private boolean m_deferrable;

   private List<Runnable> m_deferedJobs = new ArrayList<Runnable>();

   public DefaultLinker(boolean deferrable) {
      m_deferrable = deferrable;
   }

   public void finish() {
      for (Runnable job : m_deferedJobs) {
         job.run();
      }
   }

   @Override
   public boolean onAllDuration(final TransactionType parent, final AllDuration allDuration) {
      if (m_deferrable) {
         m_deferedJobs.add(new Runnable() {
            @Override
            public void run() {
               parent.addAllDuration(allDuration);
            }
         });
      } else {
         parent.addAllDuration(allDuration);
      }

      return true;
   }

   @Override
   public boolean onAllDuration(final TransactionName parent, final AllDuration allDuration) {
      if (m_deferrable) {
         m_deferedJobs.add(new Runnable() {
            @Override
            public void run() {
               parent.addAllDuration(allDuration);
            }
         });
      } else {
         parent.addAllDuration(allDuration);
      }

      return true;
   }

   @Override
   public boolean onAllDuration(final Range parent, final AllDuration allDuration) {
      if (m_deferrable) {
         m_deferedJobs.add(new Runnable() {
            @Override
            public void run() {
               parent.addAllDuration(allDuration);
            }
         });
      } else {
         parent.addAllDuration(allDuration);
      }

      return true;
   }

   @Override
   public boolean onAllDuration(final Range2 parent, final AllDuration allDuration) {
      if (m_deferrable) {
         m_deferedJobs.add(new Runnable() {
            @Override
            public void run() {
               parent.addAllDuration(allDuration);
            }
         });
      } else {
         parent.addAllDuration(allDuration);
      }

      return true;
   }

   @Override
   public boolean onDuration(final TransactionName parent, final Duration duration) {
      if (m_deferrable) {
         m_deferedJobs.add(new Runnable() {
            @Override
            public void run() {
               parent.addDuration(duration);
            }
         });
      } else {
         parent.addDuration(duration);
      }

      return true;
   }

   @Override
   public boolean onGraph(final TransactionName parent, final Graph graph) {
      if (m_deferrable) {
         m_deferedJobs.add(new Runnable() {
            @Override
            public void run() {
               parent.addGraph(graph);
            }
         });
      } else {
         parent.addGraph(graph);
      }

      return true;
   }

   @Override
   public boolean onGraph2(final TransactionType parent, final Graph2 graph2) {
      if (m_deferrable) {
         m_deferedJobs.add(new Runnable() {
            @Override
            public void run() {
               parent.addGraph2(graph2);
            }
         });
      } else {
         parent.addGraph2(graph2);
      }

      return true;
   }

   @Override
   public boolean onGraphTrend(final TransactionType parent, final GraphTrend graphTrend) {
      parent.setGraphTrend(graphTrend);
      return true;
   }

   @Override
   public boolean onGraphTrend(final TransactionName parent, final GraphTrend graphTrend) {
      parent.setGraphTrend(graphTrend);
      return true;
   }

   @Override
   public boolean onMachine(final TransactionReport parent, final Machine machine) {
      if (m_deferrable) {
         m_deferedJobs.add(new Runnable() {
            @Override
            public void run() {
               parent.addMachine(machine);
            }
         });
      } else {
         parent.addMachine(machine);
      }

      return true;
   }

   @Override
   public boolean onName(final TransactionType parent, final TransactionName name) {
      if (m_deferrable) {
         m_deferedJobs.add(new Runnable() {
            @Override
            public void run() {
               parent.addName(name);
            }
         });
      } else {
         parent.addName(name);
      }

      return true;
   }

   @Override
   public boolean onRange(final TransactionName parent, final Range range) {
      if (m_deferrable) {
         m_deferedJobs.add(new Runnable() {
            @Override
            public void run() {
               parent.addRange(range);
            }
         });
      } else {
         parent.addRange(range);
      }

      return true;
   }

   @Override
   public boolean onRange2(final TransactionType parent, final Range2 range2) {
      if (m_deferrable) {
         m_deferedJobs.add(new Runnable() {
            @Override
            public void run() {
               parent.addRange2(range2);
            }
         });
      } else {
         parent.addRange2(range2);
      }

      return true;
   }

   @Override
   public boolean onStatusCode(final TransactionName parent, final StatusCode statusCode) {
      if (m_deferrable) {
         m_deferedJobs.add(new Runnable() {
            @Override
            public void run() {
               parent.addStatusCode(statusCode);
            }
         });
      } else {
         parent.addStatusCode(statusCode);
      }

      return true;
   }

   @Override
   public boolean onType(final Machine parent, final TransactionType type) {
      if (m_deferrable) {
         m_deferedJobs.add(new Runnable() {
            @Override
            public void run() {
               parent.addType(type);
            }
         });
      } else {
         parent.addType(type);
      }

      return true;
   }
}
