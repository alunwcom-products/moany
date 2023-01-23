package com.alunw.moany.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "daily_totals")
public class DailyTotal {
    @Id
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "trans_date")
    private LocalDate transactionDate;

    private BigDecimal incoming;

    private BigDecimal outgoing;

    private BigDecimal net;

    private BigDecimal balance;

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public BigDecimal getIncoming() {
        return incoming;
    }

    public void setIncoming(BigDecimal incoming) {
        this.incoming = incoming;
    }

    public BigDecimal getOutgoing() {
        return outgoing;
    }

    public void setOutgoing(BigDecimal outgoing) {
        this.outgoing = outgoing;
    }

    public BigDecimal getNet() {
        return net;
    }

    public void setNet(BigDecimal net) {
        this.net = net;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
