package com.alunw.moany.model;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PreRemove;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "categories")
@JsonInclude(Include.NON_NULL)
public class Category implements Comparable<Category> {
	
	public static final Category ALL_CATEGORIES = new Category("All Categories", null);
	public static final Category NULL_CATEGORY = new Category("Uncategorized", null);
	
	public static final String FULL_NAME_SEPARATOR = " :: ";
	
	/**
	 * Primary identifier
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "uuid", nullable = false, unique = true)
	private String id;
	
	/** 
	 * Category name 
	 */
	@Column(name = "name", nullable = false)
	@Type(type = "string")
	private String name;
	
	/** 
	 * Category parent 
	 */
	@ManyToOne(optional = true)
	@JoinColumn(name = "parent_id")
	private Category parent;
	
	/**
	 * No accessor methods for this property - only included to allow cascading (i.e. deletion of children when
	 * category is deleted).
	 */
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "parent")
	private Set<Category> children;
	
	/**
	 * No accessor methods for this property - only included to allow cascading (i.e. removal of category in 
	 * transactions when category is deleted).
	 */
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "category")
	private Set<Transaction> transactions;
	
	/**
	 * No accessor methods for this property - only included to allow cascading (i.e. removal of category in 
	 * budget items when category is deleted).
	 */
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "category")
	private Set<BudgetItem> budgetItems;
	
	/**
	 * CategoryBudgets items that are children of this category.
	 */
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "categoryId")
	@OrderBy("startDate ASC")
	private SortedSet<CategoryBudget> categoryBudgets = new TreeSet<>();
	
	public Category() {}

	public Category(String name, Category parent) {
		this.name = name;
		this.parent = parent;
	}

	/**
	 * Persistence (deletion) callback to allow a category to be removed from referenced transactions and budget items
	 * prior to deletion.
	 */
	@PreRemove
	private void preRemove() {
		budgetItems.forEach(budgetItem -> budgetItem.setCategory(null));
		transactions.forEach(transaction -> transaction.setCategory(null));
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Category getParent() {
		return parent;
	}

	public void setParent(Category parent) {
		this.parent = parent;
	}
	
	public SortedSet<CategoryBudget> getCategoryBudgets() {
		return categoryBudgets;
	}
	
	/**
	 * @param categoryBudget
	 */
	public void addCategoryBudget(CategoryBudget categoryBudget) {
		if (categoryBudget != null) {
			categoryBudget.setCategoryId(this.getId());
			categoryBudgets.add(categoryBudget);
		}
	}
	
	public boolean removeCategoryBudget(CategoryBudget categoryBudget) {
		return categoryBudgets.remove(categoryBudget);
	}
	
	/**
	 * Returns the monthly budget for the category for the supplied month. If no budget is set - then a null value will
	 * be returned.
	 * 
	 * NOTE: This works by checking each category budget record in start date ascending order. The last valid matching
	 * value is returned. If there are duplicate records for a given month - the result cannot be predicted.
	 * 
	 * @param month
	 * @return
	 */
	public BigDecimal getCategoryBudget(YearMonth month) {
		BigDecimal result = null;
		// Budget records will be in ascending start date order.
		for (CategoryBudget budget : categoryBudgets) {
			if (budget.getStartDate().isAfter(month)) {
				// Budget record starts after specified month - ignore.
			} else {
				if (budget.getEndDate() != null && budget.getEndDate().isBefore(month)) {
					// Budget record ends before specified month - ignore.
				} else {
					// Valid budget record - update result.
					result = budget.getMonthlyBudget();
				}
			}
		}
		return result;
	}
	
	public String getFullName() {
		StringBuilder builder = new StringBuilder();
		if (name == null) {
			builder.append("null");
		} else {
			builder.append(name);
		}
		Category parent = getParent();
		while (parent != null) {
			String parentName = parent.getName();
			if (parentName == null) { parentName = "null"; }
			builder.insert(0, parent.getName() + FULL_NAME_SEPARATOR);
			parent = parent.getParent();
		}
		return builder.toString();
	}

	@Override
	public String toString() {
		return "Category[" + id  + ", " + getFullName() + ", " + getCategoryBudgets() + "]";
	}

	/**
	 * Simple comparison of full name for sorting of categories in lists
	 */
	@Override
	public int compareTo(Category cat) {
		return this.getFullName().compareTo(cat.getFullName());
	}
}