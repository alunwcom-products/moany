package com.alunw.moany.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.EntityListeners;
//import javax.persistence.EnumType;
//import javax.persistence.Enumerated;
//import javax.persistence.GeneratedValue;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//import javax.persistence.Table;

//import org.hibernate.annotations.GenericGenerator;
import jakarta.persistence.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * TODO EXP Budget item - as parent to (virtual) budget transactions. This defines the budget transactions.
 *
 * NOTES:
 *
 *
 * @author aluwilliam
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "budgetitems")
public class BudgetItem {

	/**
	 * UUID primary key.
	 */
	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
//	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "uuid", nullable = false, unique = true)
	private String id;

	/**
	 * TODO Last modified date. Not really necessary - currently - but trying out.
	 */
	@Column(name = "last_modified_date", nullable = false)
	@LastModifiedDate
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private LocalDateTime lastModifiedDate;

	/**
	 * Description to appear on budgeting transactions
	 */
	@Column(name = "description")
	private String description;

	/**
	 * A budget item must have a startDate. This defines when the budget transactions will be generated from.
	 * This will be modified over time, as the start date moves into the past, and the user marks a transaction as
	 * no longer required (i.e. when the actual transaction is entered or cancelled).
	 */
	@Column(name = "start_date", nullable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate startDate;

	/**
	 * The budget item end date is optional. If null, the budget item will continue indefinitely.
	 */
	@Column(name = "end_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate endDate;

	/**
	 * The budget item amount.
	 */
	@Column(name = "amount", nullable = false)
	private BigDecimal amount;

	/**
	 * Currently, budget items can only have monthly frequency. This defines which day of the month.
	 * If the value is less than 1, or greater than the available days of a given month, then the last day of the month
	 * will be used.
	 * Default value = 0
	 *
	@Column(name = "day_of_month", nullable = false)
	private int dayOfMonth = 0;
	*/

	/**
	 * Either WEEKS or MONTHS. Default MONTHS
	 */
	@Column(name = "period_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private ChronoUnit periodType = ChronoUnit.MONTHS;

	/**
	 * The day within the selected period type (WEEKS or MONTHS).
	 * If the value is less than 1, 1 will be used.
	 * If the value is greater than the period allows, then the maximum value for that period will be used.
	 */
	@Column(name = "day_of_period", nullable = false)
	private int dayOfPeriod = 1;

	/**
	 * The account for these budget transactions.
	 */
	@ManyToOne
	@JoinColumn(name = "account", nullable = false)
	private Account account;

	/**
	 * The (optional) category for these budget transactions.
	 */
	@ManyToOne
	@JoinColumn(name = "category", nullable = true)
	private Category category;

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

//	public int getDayOfMonth() {
//		return dayOfMonth;
//	}
//
//	public void setDayOfMonth(int dayOfMonth) {
//		this.dayOfMonth = dayOfMonth;
//	}

	public Account getAccount() {
		return account;
	}

	public ChronoUnit getPeriodType() {
		return periodType;
	}

	public void setPeriodType(ChronoUnit periodType) {
		this.periodType = periodType;
	}

	public int getDayOfPeriod() {
		return dayOfPeriod;
	}

	public void setDayOfPeriod(int dayOfPeriod) {
		this.dayOfPeriod = dayOfPeriod;
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

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "BudgetItem[id=" + id + ", description=" + description + ", startDate=" + startDate + ", amount=" + amount + ", account=" + account + "]";
	}
}
