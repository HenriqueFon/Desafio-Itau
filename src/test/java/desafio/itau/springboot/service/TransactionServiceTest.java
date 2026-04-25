package desafio.itau.springboot.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.DoubleSummaryStatistics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import desafio.itau.springboot.config.StatisticsProperties;
import desafio.itau.springboot.model.Transaction;

class TransactionServiceTest {

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        StatisticsProperties statisticsProperties = new StatisticsProperties();
        statisticsProperties.setWindowSeconds(60);

        transactionService = new TransactionService(statisticsProperties);
    }

    @Test
    void shouldAddTransactionAndCalculateStatisticsFromLast60Seconds() {
        OffsetDateTime now = OffsetDateTime.now();

        transactionService.addTransaction(new Transaction(10.0, now.minusSeconds(10)));
        transactionService.addTransaction(new Transaction(20.0, now.minusSeconds(20)));

        DoubleSummaryStatistics statistics = transactionService.getStatistics();

        assertThat(statistics.getCount()).isEqualTo(2);
        assertThat(statistics.getSum()).isEqualTo(30.0);
        assertThat(statistics.getAverage()).isEqualTo(15.0);
        assertThat(statistics.getMin()).isEqualTo(10.0);
        assertThat(statistics.getMax()).isEqualTo(20.0);
    }

    @Test
    void shouldIgnoreTransactionsOlderThanConfiguredWindow() {
        OffsetDateTime now = OffsetDateTime.now();

        transactionService.addTransaction(new Transaction(10.0, now.minusSeconds(10)));
        transactionService.addTransaction(new Transaction(999.0, now.minusSeconds(120)));

        DoubleSummaryStatistics statistics = transactionService.getStatistics();

        assertThat(statistics.getCount()).isEqualTo(1);
        assertThat(statistics.getSum()).isEqualTo(10.0);
        assertThat(statistics.getAverage()).isEqualTo(10.0);
        assertThat(statistics.getMin()).isEqualTo(10.0);
        assertThat(statistics.getMax()).isEqualTo(10.0);
    }

    @Test
    void shouldClearTransactions() {
        OffsetDateTime now = OffsetDateTime.now();

        transactionService.addTransaction(new Transaction(10.0, now.minusSeconds(10)));
        transactionService.addTransaction(new Transaction(20.0, now.minusSeconds(20)));

        transactionService.clearTransactions();

        DoubleSummaryStatistics statistics = transactionService.getStatistics();

        assertThat(statistics.getCount()).isZero();
        assertThat(statistics.getSum()).isZero();
    }

    @Test
    void shouldUseCustomStatisticsWindow() {
        StatisticsProperties statisticsProperties = new StatisticsProperties();
        statisticsProperties.setWindowSeconds(120);

        TransactionService serviceWith120SecondsWindow = new TransactionService(statisticsProperties);

        OffsetDateTime now = OffsetDateTime.now();

        serviceWith120SecondsWindow.addTransaction(new Transaction(50.0, now.minusSeconds(90)));
        serviceWith120SecondsWindow.addTransaction(new Transaction(100.0, now.minusSeconds(130)));

        DoubleSummaryStatistics statistics = serviceWith120SecondsWindow.getStatistics();

        assertThat(statistics.getCount()).isEqualTo(1);
        assertThat(statistics.getSum()).isEqualTo(50.0);
    }
}