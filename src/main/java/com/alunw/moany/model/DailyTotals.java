package com.alunw.moany.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DailyTotals {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate trans_date;

    private BigDecimal incoming;

    private BigDecimal outgoing;

    private BigDecimal net;

    private BigDecimal balance;

    public LocalDate getTrans_date() {
        return trans_date;
    }

    public void setTrans_date(LocalDate trans_date) {
        this.trans_date = trans_date;
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
