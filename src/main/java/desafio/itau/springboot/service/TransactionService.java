package desafio.itau.springboot.service;

import java.time.OffsetDateTime;
import java.util.DoubleSummaryStatistics;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.springframework.stereotype.Service;

import desafio.itau.springboot.model.Transaction;

@Service
public class TransactionService {

    private final Queue<Transaction> transactions = new ConcurrentLinkedDeque<>();

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public void clearTransactions() {
        transactions.clear();
    }

    //filtrar pelos ultimos 60 segundos
    public DoubleSummaryStatistics getStatistics() {
        OffsetDateTime now = OffsetDateTime.now();
        return transactions.stream()
            .filter(t -> t.getDataHora().isAfter(now.minusSeconds(60)))
            .mapToDouble(Transaction::getValor)
            .summaryStatistics();
    }
}