package com.alunw.moany.rest;

import com.alunw.moany.model.DailyTotals;
import com.alunw.moany.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST controller to return data to construct running balances display.
 */
@RestController
@RequestMapping("/rest/balances/v2/")
public class RestRunningTotalsController {

    @Autowired
    private TransactionRepository transactionRepo;

    private static Logger logger = LoggerFactory.getLogger(RestRunningTotalsController.class);

    @RequestMapping(value = {"/"}, method = RequestMethod.GET)
    @CrossOrigin("*")
    public List<Map<String, Object>> getTotals() {
        logger.info("getTotals()");
        return transactionRepo.findByQuery();
    }
}
