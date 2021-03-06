package com.bug1024.metralog.consumer.transaction.model.transform;

import com.bug1024.metralog.consumer.transaction.model.IEntity;
import com.bug1024.metralog.consumer.transaction.model.IVisitor;
import com.bug1024.metralog.consumer.transaction.model.entity.*;

import java.lang.reflect.Array;
import java.util.Collection;

import static com.bug1024.metralog.consumer.transaction.model.Constants.*;

public class DefaultXmlBuilder implements IVisitor {

   private IVisitor m_visitor = this;

   private int m_level;

   private StringBuilder m_sb;

   private boolean m_compact;

   public DefaultXmlBuilder() {
      this(false);
   }

   public DefaultXmlBuilder(boolean compact) {
      this(compact, new StringBuilder(4096));
   }

   public DefaultXmlBuilder(boolean compact, StringBuilder sb) {
      m_compact = compact;
      m_sb = sb;
      m_sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");
   }

   public String buildXml(IEntity<?> entity) {
      entity.accept(m_visitor);
      return m_sb.toString();
   }

   protected void endTag(String name) {
      m_level--;

      indent();
      m_sb.append("</").append(name).append(">\r\n");
   }

   protected String escape(Object value) {
      return escape(value, false);
   }
   
   protected String escape(Object value, boolean text) {
      if (value == null) {
         return null;
      }

      String str = toString(value);
      int len = str.length();
      StringBuilder sb = new StringBuilder(len + 16);

      for (int i = 0; i < len; i++) {
         final char ch = str.charAt(i);

         switch (ch) {
         case '<':
            sb.append("&lt;");
            break;
         case '>':
            sb.append("&gt;");
            break;
         case '&':
            sb.append("&amp;");
            break;
         case '"':
            if (!text) {
               sb.append("&quot;");
               break;
            }
         default:
            sb.append(ch);
            break;
         }
      }

      return sb.toString();
   }
   
   protected void indent() {
      if (!m_compact) {
         for (int i = m_level - 1; i >= 0; i--) {
            m_sb.append("   ");
         }
      }
   }

   protected void startTag(String name) {
      startTag(name, false, null);
   }
   
   protected void startTag(String name, boolean closed, java.util.Map<String, String> dynamicAttributes, Object... nameValues) {
      startTag(name, null, closed, dynamicAttributes, nameValues);
   }

   protected void startTag(String name, java.util.Map<String, String> dynamicAttributes, Object... nameValues) {
      startTag(name, null, false, dynamicAttributes, nameValues);
   }

   protected void startTag(String name, Object text, boolean closed, java.util.Map<String, String> dynamicAttributes, Object... nameValues) {
      indent();

      m_sb.append('<').append(name);

      int len = nameValues.length;

      for (int i = 0; i + 1 < len; i += 2) {
         Object attrName = nameValues[i];
         Object attrValue = nameValues[i + 1];

         if (attrValue != null) {
            m_sb.append(' ').append(attrName).append("=\"").append(escape(attrValue)).append('"');
         }
      }

      if (dynamicAttributes != null) {
         for (java.util.Map.Entry<String, String> e : dynamicAttributes.entrySet()) {
            m_sb.append(' ').append(e.getKey()).append("=\"").append(escape(e.getValue())).append('"');
         }
      }

      if (text != null && closed) {
         m_sb.append('>');
         m_sb.append(escape(text, true));
         m_sb.append("</").append(name).append(">\r\n");
      } else {
         if (closed) {
            m_sb.append('/');
         } else {
            m_level++;
         }
   
         m_sb.append(">\r\n");
      }
   }

   @SuppressWarnings("unchecked")
   protected String toString(Object value) {
      if (value instanceof String) {
         return (String) value;
      } else if (value instanceof Collection) {
         Collection<Object> list = (Collection<Object>) value;
         StringBuilder sb = new StringBuilder(32);
         boolean first = true;

         for (Object item : list) {
            if (first) {
               first = false;
            } else {
               sb.append(',');
            }

            if (item != null) {
               sb.append(item);
            }
         }

         return sb.toString();
      } else if (value.getClass().isArray()) {
         int len = Array.getLength(value);
         StringBuilder sb = new StringBuilder(32);
         boolean first = true;

         for (int i = 0; i < len; i++) {
            Object item = Array.get(value, i);

            if (first) {
               first = false;
            } else {
               sb.append(',');
            }

            if (item != null) {
               sb.append(item);
            }
         }
		
         return sb.toString();
      }
 
      return String.valueOf(value);
   }

   protected void tagWithText(String name, String text, Object... nameValues) {
      if (text == null) {
         return;
      }
      
      indent();

      m_sb.append('<').append(name);

      int len = nameValues.length;

      for (int i = 0; i + 1 < len; i += 2) {
         Object attrName = nameValues[i];
         Object attrValue = nameValues[i + 1];

         if (attrValue != null) {
            m_sb.append(' ').append(attrName).append("=\"").append(escape(attrValue)).append('"');
         }
      }

      m_sb.append(">");
      m_sb.append(escape(text, true));
      m_sb.append("</").append(name).append(">\r\n");
   }

   protected void element(String name, String text, String defaultValue, boolean escape) {
      if (text == null || text.equals(defaultValue)) {
         return;
      }
      
      indent();
      
      m_sb.append('<').append(name).append(">");
      
      if (escape) {
         m_sb.append(escape(text, true));
      } else {
         m_sb.append("<![CDATA[").append(text).append("]]>");
      }
      
      m_sb.append("</").append(name).append(">\r\n");
   }

   protected String toString(java.util.Date date, String format) {
      if (date != null) {
         return new java.text.SimpleDateFormat(format).format(date);
      } else {
         return null;
      }
   }

   protected String toString(Number number, String format) {
      if (number != null) {
         return new java.text.DecimalFormat(format).format(number);
      } else {
         return null;
      }
   }

   @Override
   public void visitAllDuration(AllDuration allDuration) {
      startTag(ENTITY_ALL_DURATION, true, null, ATTR_VALUE, allDuration.getValue(), ATTR_COUNT, allDuration.getCount());
   }

   @Override
   public void visitDuration(Duration duration) {
      startTag(ENTITY_DURATION, true, null, ATTR_VALUE, duration.getValue(), ATTR_COUNT, duration.getCount());
   }

   @Override
   public void visitGraph(Graph graph) {
      startTag(ENTITY_GRAPH, true, null, ATTR_DURATION, graph.getDuration(), ATTR_SUM, graph.getSum(), ATTR_AVG, graph.getAvg(), ATTR_COUNT, graph.getCount(), ATTR_FAILS, graph.getFails());
   }

   @Override
   public void visitGraph2(Graph2 graph2) {
      startTag(ENTITY_GRAPH2, true, null, ATTR_DURATION, graph2.getDuration(), ATTR_SUM, graph2.getSum(), ATTR_AVG, graph2.getAvg(), ATTR_COUNT, graph2.getCount(), ATTR_FAILS, graph2.getFails());
   }

   @Override
   public void visitGraphTrend(GraphTrend graphTrend) {
      startTag(ENTITY_GRAPH_TREND, true, null, ATTR_DURATION, graphTrend.getDuration(), ATTR_SUM, graphTrend.getSum(), ATTR_AVG, graphTrend.getAvg(), ATTR_COUNT, graphTrend.getCount(), ATTR_FAILS, graphTrend.getFails());
   }

   @Override
   public void visitMachine(Machine machine) {
      startTag(ENTITY_MACHINE, null, ATTR_IP, machine.getIp());

      if (!machine.getTypes().isEmpty()) {
         for (TransactionType type : machine.getTypes().values()) {
            type.accept(m_visitor);
         }
      }

      endTag(ENTITY_MACHINE);
   }

   @Override
   public void visitName(TransactionName name) {
      startTag(ENTITY_NAME, null, ATTR_ID, name.getId(), ATTR_TOTALCOUNT, name.getTotalCount(), ATTR_FAILCOUNT, name.getFailCount(), ATTR_FAILPERCENT, toString(name.getFailPercent(), "0.00"), ATTR_MIN, toString(name.getMin(), "0.00"), ATTR_MAX, toString(name.getMax(), "0.00"), ATTR_AVG, toString(name.getAvg(), "0.0"), ATTR_SUM, toString(name.getSum(), "0.0"), ATTR_SUM2, toString(name.getSum2(), "0.0"), ATTR_STD, toString(name.getStd(), "0.0"), ATTR_TPS, toString(name.getTps(), "0.00"), ATTR_LINE95VALUE, toString(name.getLine95Value(), "0.00"), ATTR_LINE99VALUE, toString(name.getLine99Value(), "0.00"), ATTR_LINE999VALUE, toString(name.getLine999Value(), "0.00"), ATTR_LINE90VALUE, toString(name.getLine90Value(), "0.00"), ATTR_LINE50VALUE, toString(name.getLine50Value(), "0.00"), ATTR_LINE9999VALUE, toString(name.getLine9999Value(), "0.00"));

      element(ELEMENT_SUCCESSMESSAGEURL, name.getSuccessMessageUrl(), null,  true);

      element(ELEMENT_FAILMESSAGEURL, name.getFailMessageUrl(), null,  true);

      element(ELEMENT_LONGESTMESSAGEURL, name.getLongestMessageUrl(), null,  true);

      if (!name.getRanges().isEmpty()) {
         for (Range range : name.getRanges().values()) {
            range.accept(m_visitor);
         }
      }

      if (!name.getDurations().isEmpty()) {
         for (Duration duration : name.getDurations().values()) {
            duration.accept(m_visitor);
         }
      }

      if (!name.getGraphs().isEmpty()) {
         for (Graph graph : name.getGraphs().values()) {
            graph.accept(m_visitor);
         }
      }

      if (name.getGraphTrend() != null) {
         name.getGraphTrend().accept(m_visitor);
      }

      if (!name.getStatusCodes().isEmpty()) {
         for (StatusCode statusCode : name.getStatusCodes().values()) {
            statusCode.accept(m_visitor);
         }
      }

      endTag(ENTITY_NAME);
   }

   @Override
   public void visitRange(Range range) {
      startTag(ENTITY_RANGE, true, null, ATTR_VALUE, range.getValue(), ATTR_COUNT, range.getCount(), ATTR_SUM, range.getSum(), ATTR_AVG, toString(range.getAvg(), "0.0"), ATTR_FAILS, range.getFails(), ATTR_MIN, toString(range.getMin(), "0.00"), ATTR_MAX, toString(range.getMax(), "0.00"), ATTR_LINE95VALUE, toString(range.getLine95Value(), "0.00"), ATTR_LINE99VALUE, toString(range.getLine99Value(), "0.00"), ATTR_LINE999VALUE, toString(range.getLine999Value(), "0.00"), ATTR_LINE90VALUE, toString(range.getLine90Value(), "0.00"), ATTR_LINE50VALUE, toString(range.getLine50Value(), "0.00"), ATTR_LINE9999VALUE, toString(range.getLine9999Value(), "0.00"));
   }

   @Override
   public void visitRange2(Range2 range2) {
      startTag(ENTITY_RANGE2, true, null, ATTR_VALUE, range2.getValue(), ATTR_COUNT, range2.getCount(), ATTR_SUM, range2.getSum(), ATTR_AVG, toString(range2.getAvg(), "0.0"), ATTR_FAILS, range2.getFails(), ATTR_MIN, toString(range2.getMin(), "0.00"), ATTR_MAX, toString(range2.getMax(), "0.00"), ATTR_LINE95VALUE, toString(range2.getLine95Value(), "0.00"), ATTR_LINE99VALUE, toString(range2.getLine99Value(), "0.00"), ATTR_LINE999VALUE, toString(range2.getLine999Value(), "0.00"), ATTR_LINE90VALUE, toString(range2.getLine90Value(), "0.00"), ATTR_LINE50VALUE, toString(range2.getLine50Value(), "0.00"), ATTR_LINE9999VALUE, toString(range2.getLine9999Value(), "0.00"));
   }

   @Override
   public void visitStatusCode(StatusCode statusCode) {
      startTag(ENTITY_STATUSCODE, true, null, ATTR_ID, statusCode.getId(), ATTR_COUNT, statusCode.getCount());
   }

   @Override
   public void visitTransactionReport(TransactionReport transactionReport) {
      startTag(ENTITY_TRANSACTION_REPORT, null, ATTR_DOMAIN, transactionReport.getDomain(), ATTR_STARTTIME, toString(transactionReport.getStartTime(), "yyyy-MM-dd HH:mm:ss"), ATTR_ENDTIME, toString(transactionReport.getEndTime(), "yyyy-MM-dd HH:mm:ss"));

      if (!transactionReport.getDomainNames().isEmpty()) {
         for (String domain : transactionReport.getDomainNames()) {
            tagWithText(ELEMENT_DOMAIN, domain);
         }
      }

      if (!transactionReport.getIps().isEmpty()) {
         for (String ip : transactionReport.getIps()) {
            tagWithText(ELEMENT_IP, ip);
         }
      }

      if (!transactionReport.getMachines().isEmpty()) {
         for (Machine machine : transactionReport.getMachines().values()) {
            machine.accept(m_visitor);
         }
      }

      endTag(ENTITY_TRANSACTION_REPORT);
   }

   @Override
   public void visitType(TransactionType type) {
      startTag(ENTITY_TYPE, type.getDynamicAttributes(), ATTR_ID, type.getId(), ATTR_TOTALCOUNT, type.getTotalCount(), ATTR_FAILCOUNT, type.getFailCount(), ATTR_FAILPERCENT, toString(type.getFailPercent(), "0.00"), ATTR_MIN, toString(type.getMin(), "0.00"), ATTR_MAX, toString(type.getMax(), "0.00"), ATTR_AVG, toString(type.getAvg(), "0.0"), ATTR_SUM, toString(type.getSum(), "0.0"), ATTR_SUM2, toString(type.getSum2(), "0.0"), ATTR_STD, toString(type.getStd(), "0.0"), ATTR_TPS, toString(type.getTps(), "0.00"), ATTR_LINE95VALUE, toString(type.getLine95Value(), "0.00"), ATTR_LINE99VALUE, toString(type.getLine99Value(), "0.00"), ATTR_LINE999VALUE, toString(type.getLine999Value(), "0.00"), ATTR_LINE90VALUE, toString(type.getLine90Value(), "0.00"), ATTR_LINE50VALUE, toString(type.getLine50Value(), "0.00"), ATTR_LINE9999VALUE, toString(type.getLine9999Value(), "0.00"));

      element(ELEMENT_SUCCESSMESSAGEURL, type.getSuccessMessageUrl(), null,  true);

      element(ELEMENT_FAILMESSAGEURL, type.getFailMessageUrl(), null,  true);

      element(ELEMENT_LONGESTMESSAGEURL, type.getLongestMessageUrl(), null,  true);

      if (!type.getNames().isEmpty()) {
         for (TransactionName name : type.getNames().values()) {
            name.accept(m_visitor);
         }
      }

      if (!type.getGraph2s().isEmpty()) {
         for (Graph2 graph2 : type.getGraph2s().values()) {
            graph2.accept(m_visitor);
         }
      }

      if (type.getGraphTrend() != null) {
         type.getGraphTrend().accept(m_visitor);
      }

      if (!type.getRange2s().isEmpty()) {
         for (Range2 range2 : type.getRange2s().values()) {
            range2.accept(m_visitor);
         }
      }

      endTag(ENTITY_TYPE);
   }
}
