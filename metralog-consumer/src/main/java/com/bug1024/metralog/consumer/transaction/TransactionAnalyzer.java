package com.bug1024.metralog.consumer.transaction;

import com.bug1024.metralog.consumer.MessageAnalyzer;
import com.bug1024.metralog.consumer.ReportManager;
import com.bug1024.metralog.consumer.transaction.model.entity.*;
import com.dianping.cat.CatConstants;
import com.dianping.cat.analyzer.DurationComputer;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.message.spi.MessageQueue;
import com.dianping.cat.message.spi.MessageTree;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bug1024
 * @date 2019-06-30
 */
@Slf4j
public class TransactionAnalyzer implements MessageAnalyzer {

    public static final long MINUTE = 60 * 1000L;

    public static final long ONE_HOUR = 60 * 60 * 1000L;

    public static final long ONE_DAY = 24 * ONE_HOUR;

    private TransactionStatisticsComputer computer = new TransactionStatisticsComputer();

    private DurationMeta durationMeta = new DurationMeta();

    private ReportManager reportManager = new ReportManager();

    @Override
    public void process(MessageQueue messageQueue) {
        while (true) {
            MessageTree messageTree = messageQueue.poll();
            if (messageTree != null) {
                process(messageTree);
                log.info("tree:{}", messageTree);
            }
        }
    }

    @Override
    public ReportManager getReportManager() {
        return reportManager;
    }

    private void process(MessageTree tree) {
        String domain = tree.getDomain();
        TransactionReport report = reportManager.getHourlyReport(getStartTime(), domain, true);
        List<Transaction> transactions = tree.findOrCreateTransactions();

        for (Transaction t : transactions) {
            String data = String.valueOf(t.getData());

            if (data.length() > 0 && data.charAt(0) == CatConstants.BATCH_FLAG) {
                processBatchTransaction(tree, report, t, data);
            } else {
                processTransaction(report, tree, t);
            }
        }
    }

    private void processBatchTransaction(MessageTree tree, TransactionReport report, Transaction t, String data) {
        String[] tabs = data.substring(1).split(CatConstants.SPLIT);
        int total = Integer.parseInt(tabs[0]);
        int fail = Integer.parseInt(tabs[1]);
        long sum = Long.parseLong(tabs[2]);
        String type = t.getType();
        String name = t.getName();

        String ip = tree.getIpAddress();
        TransactionType transactionType = findOrCreateType(report.findOrCreateMachine(ip), type);
        TransactionName transactionName = findOrCreateName(transactionType, name, report.getDomain());
        DurationMeta durations = computeBatchDuration(t, tabs, transactionType, transactionName, report.getDomain());

        processTypeAndName(tree, t, transactionType, transactionName, total, fail, sum, durations);
    }

    private void parseDurations(DurationMeta meta, String duration) {
        meta.clear();

        String[] tabs = duration.split("\\|");

        for (String tab : tabs) {
            String[] item = tab.split(",");

            if (item.length == 2) {
                meta.add(Integer.parseInt(item[0]), Integer.parseInt(item[1]));
            }
        }
    }

    private DurationMeta computeBatchDuration(Transaction t, String[] tabs, TransactionType transactionType,
                                              TransactionName transactionName, String domain) {
        if (tabs.length >= 4) {
            String duration = tabs[3];
            parseDurations(durationMeta, duration);

            Map<Integer, Integer> durations = durationMeta.getDurations();
            Map<Integer, AllDuration> allTypeDurations = transactionType.getAllDurations();
            Map<Integer, AllDuration> allNameDurations = transactionName.getAllDurations();
            long current = t.getTimestamp() / 1000 / 60;
            int min = (int) (current % (60));
            Range2 typeRange = transactionType.findOrCreateRange2(min);
            Range nameRange = transactionName.findOrCreateRange(min);

            int maxValue = (int) durationMeta.getMax();
            int minValue = (int) durationMeta.getMin();

            transactionType.setMax(Math.max(maxValue, transactionType.getMax()));
            transactionType.setMin(Math.min(minValue, transactionType.getMin()));
            transactionName.setMax(Math.max(maxValue, transactionName.getMax()));
            transactionName.setMin(Math.min(minValue, transactionName.getMin()));
            typeRange.setMax(Math.max(maxValue, typeRange.getMax()));
            typeRange.setMin(Math.min(minValue, typeRange.getMin()));
            nameRange.setMax(Math.max(maxValue, nameRange.getMax()));
            nameRange.setMin(Math.min(minValue, nameRange.getMin()));

            mergeMap(allTypeDurations, durations);
            mergeMap(allNameDurations, durations);

            boolean statistic = true;
            //boolean statistic = m_statisticManager.shouldStatistic(t.getType(), domain);

            if (statistic) {
                mergeMap(typeRange.getAllDurations(), durations);
                mergeMap(nameRange.getAllDurations(), durations);
            }

            return durationMeta;
        }
        return null;
    }

    private void mergeMap(Map<Integer, AllDuration> allDurations, Map<Integer, Integer> other) {
        for (Map.Entry<Integer, Integer> entry : other.entrySet()) {
            Integer key = entry.getKey();
            Integer value = entry.getValue();
            AllDuration allDuration = allDurations.get(key);

            if (allDuration == null) {
                allDuration = new AllDuration(key);
                allDurations.put(key, allDuration);
            }

            allDuration.incCount(value);
        }
    }



    private void processNameGraph(Transaction t, TransactionName name, int min, double d, boolean statistic,
                                  int allDuration) {
        int dk = formatDurationDistribute(d);

        Duration duration = name.findOrCreateDuration(dk);
        Range range = name.findOrCreateRange(min);

        duration.incCount();
        range.incCount();

        if (!t.isSuccess()) {
            range.incFails();
        }

        range.setSum(range.getSum() + d);
        range.setMax(Math.max(range.getMax(), d));
        range.setMin(Math.min(range.getMin(), d));

        if (statistic) {
            range.findOrCreateAllDuration(allDuration).incCount();
        }
    }

    private void processNameGraph(TransactionName name, int min, int total, int fail, long sum, DurationMeta durations) {
        Range range = name.findOrCreateRange(min);

        range.incCount(total);
        range.incFails(fail);
        range.setSum(range.getSum() + sum);

        if (durations != null) {
            Map<Integer, Integer> ds = durations.getDurations();

            for (Map.Entry<Integer, Integer> entry : ds.entrySet()) {
                int formatDuration = formatDurationDistribute(entry.getKey());
                int count = entry.getValue();
                Duration duration = name.findOrCreateDuration(formatDuration);

                duration.incCount(count);
            }
        }
    }

    private void processTransaction(TransactionReport report, MessageTree tree, Transaction t) {
        String type = t.getType();
        String name = t.getName();

        boolean discard = false;
        //boolean discard= m_filterConfigManager.discardTransaction(type, name);
        if (!discard) {
            boolean valid = checkForTruncatedMessage(tree, t);

            if (valid) {
                String ip = tree.getIpAddress();
                TransactionType transactionType = findOrCreateType(report.findOrCreateMachine(ip), type);
                TransactionName transactionName = findOrCreateName(transactionType, name, report.getDomain());

                processTypeAndName(t, transactionType, transactionName, tree, t.getDurationInMillis());
            }
        }
    }

    private void processTypeAndName(MessageTree tree, Transaction t, TransactionType type, TransactionName name, int total,
                                    int fail, long sum, DurationMeta durations) {
        String messageId = tree.getMessageId();

        if (type.getSuccessMessageUrl() == null) {
            type.setSuccessMessageUrl(messageId);
        }
        if (name.getSuccessMessageUrl() == null) {
            name.setSuccessMessageUrl(messageId);
        }

        if (type.getLongestMessageUrl() == null) {
            type.setLongestMessageUrl(messageId);
        }

        if (name.getLongestMessageUrl() == null) {
            name.setLongestMessageUrl(messageId);
        }

        type.incTotalCount(total);
        name.incTotalCount(total);

        type.incFailCount(fail);
        name.incFailCount(fail);

        type.setSum(type.getSum() + sum);
        name.setSum(name.getSum() + sum);

        long current = t.getTimestamp() / 1000 / 60;
        int min = (int) (current % (60));

        processTypeRange(type, min, total, fail, sum);
        processNameGraph(name, min, total, fail, sum, durations);
    }

    private void processTypeAndName(Transaction t, TransactionType type, TransactionName name, MessageTree tree,
                                    double duration) {
        String messageId = tree.getMessageId();

        type.incTotalCount();
        name.incTotalCount();

        type.setSuccessMessageUrl(messageId);
        name.setSuccessMessageUrl(messageId);

        if (!t.isSuccess()) {
            type.incFailCount();
            name.incFailCount();

            String statusCode = formatStatus(t.getStatus());

            findOrCreateStatusCode(name, statusCode).incCount();
        }

        int allDuration = DurationComputer.computeDuration((int) duration);
        double sum = duration * duration;

        if (type.getMax() <= duration) {
            type.setLongestMessageUrl(messageId);
        }
        if (name.getMax() <= duration) {
            name.setLongestMessageUrl(messageId);
        }
        name.setMax(Math.max(name.getMax(), duration));
        name.setMin(Math.min(name.getMin(), duration));
        name.setSum(name.getSum() + duration);
        name.setSum2(name.getSum2() + sum);
        name.findOrCreateAllDuration(allDuration).incCount();

        type.setMax(Math.max(type.getMax(), duration));
        type.setMin(Math.min(type.getMin(), duration));
        type.setSum(type.getSum() + duration);
        type.setSum2(type.getSum2() + sum);
        type.findOrCreateAllDuration(allDuration).incCount();

        long current = t.getTimestamp() / 1000 / 60;
        int min = (int) (current % (60));
        boolean statistic = true;
        //boolean statistic = m_statisticManager.shouldStatistic(type.getId(), tree.getDomain());

        processNameGraph(t, name, min, duration, statistic, allDuration);
        processTypeRange(t, type, min, duration, statistic, allDuration);
    }

    private void processTypeRange(Transaction t, TransactionType type, int min, double d, boolean statistic,
                                  int allDuration) {
        Range2 range = type.findOrCreateRange2(min);

        if (!t.isSuccess()) {
            range.incFails();
        }

        range.incCount();
        range.setSum(range.getSum() + d);
        range.setMax(Math.max(range.getMax(), d));
        range.setMin(Math.min(range.getMin(), d));

        if (statistic) {
            range.findOrCreateAllDuration(allDuration).incCount();
        }
    }

    private void processTypeRange(TransactionType type, int min, int total, int fail, long sum) {
        Range2 range = type.findOrCreateRange2(min);

        range.incCount(total);
        range.incFails(fail);
        range.setSum(range.getSum() + sum);
    }

    private TransactionReport queryReport(String domain) {
        long period = getStartTime();
        long timestamp = System.currentTimeMillis();
        long remainder = timestamp % ONE_HOUR;
        long current = timestamp - remainder;

        TransactionReport report = reportManager.getHourlyReport(period, domain, false);

        int tpExpireMinute = 1;
        // int tpExpireMinute = m_serverConfigManager.getTpValueExpireMinute()
        computer.setMaxDurationMinute(tpExpireMinute);

        if (period == current) {
            report.accept(computer.setDuration(remainder / 1000.0));
        } else if (period < current) {
            report.accept(computer.setDuration(3600));
        }
        return report;
    }

    private TransactionName findOrCreateName(TransactionType type, String name, String domain) {
        TransactionName transactionName = type.findName(name);

        if (transactionName == null) {
            int size = type.getNames().size();

            int maxNameThreshold = 1000;
            //int maxNameThreshold = m_atomicMessageConfigManager.getMaxNameThreshold(domain);
            if (size > maxNameThreshold) {
                transactionName = type.findOrCreateName("others");
            } else {
                transactionName = type.findOrCreateName(name);
            }
        }

        return transactionName;
    }

    private TransactionType findOrCreateType(Machine machine, String type) {
        TransactionType transactionType = machine.findType(type);

        if (transactionType == null) {
            int size = machine.getTypes().size();
            int typeCountLimit = 1000;
            if (size > typeCountLimit) {
                transactionType = machine.findOrCreateType("others");
            } else {
                transactionType = machine.findOrCreateType(type);
            }
        }

        return transactionType;
    }

    private boolean checkForTruncatedMessage(MessageTree tree, Transaction t) {
        List<Message> children = t.getChildren();
        int size = children.size();

        // root transaction with children
        if (tree.getMessage() == t && size > 0) {
            Message last = children.get(size - 1);

            if (last instanceof Event) {
                String type = last.getType();
                String name = last.getName();

                return !"RemoteCall".equals(type) || !"Next".equals(name);
            }
        }

        return true;
    }

    public static class DurationMeta {
        private Map<Integer, Integer> m_durations = new LinkedHashMap<Integer, Integer>();

        private double m_min = Integer.MAX_VALUE;

        private double m_max = -1;

        public void clear() {
            m_min = Integer.MAX_VALUE;
            m_max = -1;
            m_durations.clear();
        }

        public void add(Integer key, Integer value) {
            m_durations.put(key, value);

            if (m_min > key) {
                m_min = key;
            }
            if (m_max < key) {
                m_max = key;
            }
        }

        public Map<Integer, Integer> getDurations() {
            return m_durations;
        }

        public double getMax() {
            return m_max;
        }

        public double getMin() {
            return m_min;
        }
    }

    public long getStartTime() {
        return 123;
    }

    private int formatDurationDistribute(double d) {
        int dk = 1;

        if (d > 65536) {
            dk = 65536;
        } else {
            while (dk < d) {
                dk <<= 1;
            }
        }
        return dk;
    }

    private String formatStatus(String status) {
        if (status.length() > 128) {
            return status.substring(0, 128);
        } else {
            return status;
        }
    }

    private StatusCode findOrCreateStatusCode(TransactionName name, String codeName) {
        StatusCode code = name.findStatusCode(codeName);

        if (code == null) {
            int size = name.getStatusCodes().size();

            if (size > 500) {
                code = name.findOrCreateStatusCode("others");
            } else {
                code = name.findOrCreateStatusCode(codeName);
            }
        }
        return code;
    }
}
