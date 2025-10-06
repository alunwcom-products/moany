package com.alunw.moany.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.Table;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Defines a category budget value. This will be a child of Category. These values are per month, with a start date
 * (month) and optional end date (month) - to allow for changing budgets.
 *
 * DEVELOPER NOTES:
 * - Dates values are persisted as dates, as JPA stores YearMonth as a BLOB.
 * - It is possible for a Category to have CategoryBudget records that overlap date periods, however if 2 or more
 *   records are valid for a given month, then the value for that month's budget is not predictable.
 */
@Entity
@Table(name = "category_budgets")
@JsonInclude(Include.NON_NULL)
public class CategoryBudget implements Comparable<CategoryBudget> {
	/**
	 * Primary identifier
	 */
	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
//	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "uuid", nullable = false, unique = true)
	private String id;
	/**
	 * Monthly budget value.
	 * Cannot be null.
	 */
	@Column(name = "monthly_budget", nullable = false)
	private BigDecimal monthlyBudget;
	/**
	 * The month this budget value starts.
	 * Cannot be null.
	 */
	@Column(name = "start_date", nullable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM")
	private LocalDate startDate;
	/**
	 * The month this budget value ends. May be null (i.e. budget value continues without ending).
	 * May be the same as startDate, but not earlier.
	 */
	@Column(name = "end_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM")
	private LocalDate endDate;

	@JoinColumn(name = "category", nullable = false)
	private String categoryId;

	public CategoryBudget() {}

	public CategoryBudget(YearMonth startDate, YearMonth endDate, BigDecimal monthlyBudget) {
		setStartDate(startDate);
		setEndDate(endDate);
		setMonthlyBudget(monthlyBudget);
	}

	public String getId() {
		return id;
	}

	public BigDecimal getMonthlyBudget() {
		return monthlyBudget;
	}

	public void setMonthlyBudget(BigDecimal monthlyBudget) {
		this.monthlyBudget = monthlyBudget;
	}

	public YearMonth getStartDate() {
		if (startDate == null) {
			return null;
		} else {
			return YearMonth.from(startDate);
		}
	}

	public void setStartDate(YearMonth startDate) {
		if (startDate == null) {
			this.startDate = null;
		} else {
			this.startDate = LocalDate.of(startDate.getYear(), startDate.getMonth(), 1);
		}
	}

	public YearMonth getEndDate() {
		if (endDate == null) {
			return null;
		} else {
			return YearMonth.from(endDate);
		}
	}

	public void setEndDate(YearMonth endDate) {
		if (endDate == null) {
			this.endDate = null;
		} else {
			this.endDate = LocalDate.of(endDate.getYear(), endDate.getMonth(), 1);
		}
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 * Implements Comparable so CategoryBidget items can be easily sorted by ascending start date.
	 *
	 * DEVELOPER NOTE: Because matching start dates behave as being equal, the parent Category with only hold one
	 * CategoryBudget per start date.
	 */
	@Override
	public int compareTo(CategoryBudget obj) {
		if (obj == null) {
			throw new NullPointerException();
		}

		int result = getStartDate().compareTo(obj.getStartDate());
		// TODO if start date equal - then object will be treated as equal in set!
		return result;
	}

	@Override
	public String toString() {
		return "CategoryBudget[" + getId() + ", " + getMonthlyBudget() + ", " + getStartDate() + ", " + getEndDate() + ", " + categoryId + "]";
	}
}
