package com.alunw.moany.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alunw.moany.model.BudgetItem;
import com.alunw.moany.repository.BudgetItemRepository;

@RestController
@RequestMapping("/rest/budgeting/v2/")
public class RestBudgetItemController {

	@Autowired
	private BudgetItemRepository budgetRepo;

	private static Logger logger = LoggerFactory.getLogger(RestBudgetItemController.class);

	@RequestMapping(value={"/"}, method = RequestMethod.GET)
	public List<BudgetItem> getBudgetItems() {

		logger.info("getBudgetItems()");

		return budgetRepo.findAll();
	}

	@RequestMapping(value={"/"}, method = RequestMethod.PUT)
	public BudgetItem putBudgetItem(@Validated @RequestBody BudgetItem item, BindingResult result, Model model) {

		logger.info("putBudgetItem()");

		// TODO testing/validation
		if (result != null) {
			logger.info("errors = " + result.getErrorCount());
			logger.info("errors = " + result.getAllErrors());
		}

		// TODO Validate data
		if (item != null) {
			logger.debug("item = " + item);
		}
		// Store data
		return budgetRepo.save(item);
	}

	@RequestMapping(value={"/id","/id/{itemId}"}, method = RequestMethod.DELETE)
	public void deleteBudgetItem(@PathVariable(name="itemId", required=false) String itemId) {

		logger.info("deleteBudgetItem({})", itemId);

		if (itemId == null) {
			throw new BadRequestException();
		}

		budgetRepo.deleteById(itemId);
	}
}
