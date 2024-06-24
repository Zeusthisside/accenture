package com.mockcompany.webapp.controller;

import com.mockcompany.webapp.api.SearchReportResponse;
import com.mockcompany.webapp.model.ProductItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Management decided it is super important that we have lots of products that match the following terms.
 * So much so, that they would like a daily report of the number of products for each term along with the total
 * product count.
 */
@RestController
public class ReportController {
    //After reading this code/tests, we can capture the important terms in an array!
   private static final String[] importantTerms = new String[] {
       "Cool";
       "Amazing";
       "Perfect";
       "Kids";   
   };
    private final EntityManager entityManager;

   private final SearchService searchService;

    @Autowired
    public ReportController(EntityManager entityManager, SearchService searchService) {
        this.entityManager = entityManager;
        this.searchService = searchService;
    }


    @GetMapping("/api/products/report")
    public SearchReportResponse runReport() {
        Number count = (Number) this.entityManager.createQuery("SELECT count(item) FROM ProductItem item").getSingleResult();
        Map<String, Integer> hits = new HashMap<>();
        for(String term : importantTerms){
            hits.put(term, searchService.search(term).size());
        }

        SearchReportResponse response = new SearchReportResponse();
        response.setProductCount(count.intValue());
        response.setSearchTermHits(hits);

        
        return response;
    }
}