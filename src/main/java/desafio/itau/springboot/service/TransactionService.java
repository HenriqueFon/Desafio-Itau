package desafio.itau.springboot.service;

import java.time.OffsetDateTime;
import java.util.DoubleSummaryStatistics;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.springframework.stereotype.Service;

import desafio.itau.springboot.config.StatisticsProperties;
import desafio.itau.springboot.model.Transaction;

@Service
public class TransactionService {

    private final Queue<Transaction> transactions = new ConcurrentLinkedDeque<>();
    private final StatisticsProperties statisticsProperties;

    public TransactionService(StatisticsProperties statisticsProperties) {
        this.statisticsProperties = statisticsProperties;
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public void clearTransactions() {
        transactions.clear();
    }

    public DoubleSummaryStatistics getStatistics() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime startTime = now.minusSeconds(statisticsProperties.getWindowSeconds());

        return transactions.stream()
                .filter(transaction -> !transaction.getDataHora().isBefore(startTime))
                .mapToDouble(Transaction::getValor)
                .summaryStatistics();
    }
}