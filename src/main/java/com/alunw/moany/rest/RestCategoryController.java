package com.alunw.moany.rest;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alunw.moany.model.Category;
import com.alunw.moany.model.CategoryBudget;
import com.alunw.moany.repository.CategoryBudgetRepository;
import com.alunw.moany.repository.CategoryRepository;
import com.alunw.moany.services.AccountService;
import com.alunw.moany.services.CategoryService;

@RestController
@RequestMapping("/rest/categories/v2/")
public class RestCategoryController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CategoryRepository categoryRepo;

	@Autowired
	private CategoryBudgetRepository categoryBudgetRepo;

	@Autowired
	private AccountService accountService;

	private static Logger logger = LoggerFactory.getLogger(RestCategoryController.class);

	/**
	 * GET a list of all categories.
	 *
	 * @return A list of all categories.
	 */
	@RequestMapping(value={"/"}, method = RequestMethod.GET)
	public List<Category> getCategories() {
		logger.info("getCategories()");
		return categoryRepo.findAll();
	}

	/**
	 * TODO
	 *
	 * Return 12 (whole) months of category data
	 *
	 *
	 * @param accountIdsStr
	 *     Comma-delimited list of account IDs
	 * @param monthStr
	 *     The start month - if not defined, will use first month of current year
	 * @return
	 */
	@RequestMapping(value={"/summary/"}, method = RequestMethod.GET)
	public List<Map<String, Object>> getCategorySummary(
			@RequestParam(name="acc", required=false) String accountIdsStr,
			@RequestParam(value="month", required = false) String monthStr) {

		logger.info("getCategorySummary({}, {})", accountIdsStr, monthStr);

		YearMonth startMonth = null;
		try {
			// TODO hack to parse date - remove day! YYYY-MM-DD
			logger.debug("yearmonth = {}", monthStr.substring(0, 7));
			startMonth = YearMonth.parse(monthStr.substring(0, 7));
		} catch (Exception e) {
			logger.error("Unable to parse start month [{}]", monthStr);
		}

		if (startMonth == null) {
			startMonth = YearMonth.now().withMonth(1); // default to first month of current year
		}

		return categoryService.getCategorySummary(accountService.findByIdIn(accountIdsStr), startMonth);
	}

	@RequestMapping(value={"/"}, method = RequestMethod.PUT)
	public Category putCategory(@RequestBody Category category, BindingResult result) {
		logger.info("putCategory()");
		// TODO testing
		if (result != null) {
			logger.info("errors = " + result.getErrorCount());
			logger.info("errors = " + result.getAllErrors());
		}

		// TODO Validate data
		if (category != null) {
			logger.debug("category = {}", category);
		}
		// Store data
		if (category != null && category.getName() != null && !category.getName().isEmpty()) {
			return categoryRepo.save(category);
		} else {
			logger.warn("Category has blank name - not saving!");
			return null;
		}
	}

	@RequestMapping(value={"/id","/id/{categoryId}"}, method = RequestMethod.DELETE)
	public void deleteCategoryById(@PathVariable(name="categoryId", required=false) String categoryId) {

		logger.info("deleteCategoryById(" + categoryId + ")");

		if (categoryId == null) {
			throw new BadRequestException();
		}

		categoryRepo.deleteById(categoryId);
	}

	@RequestMapping(value={"/categoryBudget/{categoryId}"}, method = RequestMethod.PUT)
	public CategoryBudget addCategoryBudget(@PathVariable(name="categoryId") String categoryId, @RequestBody CategoryBudget categoryBudget) {

		logger.info("addCategoryBudget(): {}; {}", categoryId, categoryBudget);

		Optional<Category> catResult = categoryRepo.findById(categoryId);
		if (catResult.isPresent()) {

			// TODO validation

			logger.debug("adding budget");

			Category category = catResult.get();
			category.addCategoryBudget(categoryBudget);

			logger.debug("category {}", category);

			categoryRepo.save(category);

		} else {
			logger.warn("No matching category found [id = {}], category budget cannot be added.", categoryId);
		}

		return null;
	}

	@RequestMapping(value={"/categoryBudget/{categoryBudgetId}"}, method = RequestMethod.DELETE)
	public void removeCategoryBudget(@PathVariable(name="categoryBudgetId") String categoryBudgetId) {

		logger.info("removeCategoryBudget(): {}", categoryBudgetId);

		Optional<CategoryBudget> categoryBudgetResult = categoryBudgetRepo.findById(categoryBudgetId);
		if (!categoryBudgetResult.isPresent()) {
			logger.error("Can't fetch CategoryBudget: id = {}", categoryBudgetId);
			throw new BadRequestException(); // TODO
		}

		CategoryBudget budget = categoryBudgetResult.get();
		if (budget.getCategoryId() == null) {
			// This should not be possible ;)
			logger.error("CategoryBudget has no Category id defined.");
			throw new BadRequestException();
		}

		Optional<Category> categoryResult = categoryRepo.findById(budget.getCategoryId());
		if (!categoryResult.isPresent()) {
			logger.error("Can't fetch Category: id = {}", budget.getCategoryId());
			throw new BadRequestException(); // TODO
		}
		Category category = categoryResult.get();
		boolean removed = category.removeCategoryBudget(budget);
		if (removed) {
			logger.debug("CategoryBudget [{}] removed from Category [{}]", budget.getId(), budget.getCategoryId());
			categoryRepo.save(category);
		} else {
			logger.error("CategoryBudget [{}] *not* removed from Category [{}]", budget.getId(), budget.getCategoryId());
		}
	}
}
