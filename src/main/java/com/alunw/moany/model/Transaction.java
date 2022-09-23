package com.alunw.moany.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "uuid", nullable = false, unique = true)
    private String id;

    @Column(name = "trans_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate transactionDate;

    @Column(name = "entry_date")
    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime entryDate;

    @Column(name = "type")
    @Type(type = "string")
    private String type;

    @Column(name = "description")
    @Type(type = "string")
    private String description;

    @Column(name = "source_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType sourceType;

    @Column(name = "source_name")
    @Type(type = "string")
    private String sourceName;

    @Column(name = "source_row")
    @Type(type = "long")
    private long sourceRow;

    @Column(name = "statement_amount")
    private BigDecimal statementAmount;

    @Column(name = "net_amount")
    private BigDecimal netAmount;

    @Column(name = "statement_balance")
    private BigDecimal stmtBalance;

    @Column(name = "account_balance")
    private BigDecimal accBalance;

    @ManyToOne
    @JoinColumn(name = "account", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "category", nullable = true)
    private Category category;

    @Column(name = "comment")
    @Type(type = "string")
    private String comment;

    public Transaction() {
    }

    public String getId() {
        return id;
    }

//	private void setId(String id) {
//		this.id = id;
//	}

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate date) {
        this.transactionDate = date;
    }

    public LocalDateTime getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDateTime date) {
        this.entryDate = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TransactionType getSourceType() {
        return sourceType;
    }

    public void setSourceType(TransactionType sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public Long getSourceRow() {
        return sourceRow;
    }

    public void setSourceRow(long sourceRow) {
        this.sourceRow = sourceRow;
    }

    public BigDecimal getStatementAmount() {
        return statementAmount;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setStatementAmount(BigDecimal statementAmount) {
        this.statementAmount = statementAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public BigDecimal getStmtBalance() {
        return stmtBalance;
    }

    public void setStmtBalance(BigDecimal stmtBalance) {
        this.stmtBalance = stmtBalance;
    }

    public BigDecimal getAccBalance() {
        return accBalance;
    }

    public void setAccBalance(BigDecimal accBalance) {
        this.accBalance = accBalance;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Transaction{id=" + id + ", transactionDate=" + transactionDate + ", amount=" + statementAmount
                + ", account=" + account + ", category=" + category + "}";
    }
}
