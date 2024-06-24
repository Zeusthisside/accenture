Package com.mockcompany.webapp.controller;

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
 51 changes: 51 additions & 0 deletions51  
SearchController (2).java
Original file line number	Diff line number	Diff line change
@@ -0,0 +1,51 @@
/*
 * Java classes are grouped in "packages". This allows them to be referenced and used in other
 * classes using import statements.  Any class in this project is prefixed in the com.mockcompany.webapp
 * package.
 *
 *   https://www.w3schools.com/java/java_packages.asp
 *
 * For general help with Java, see the tutorialspoint tutorial:
 *
 *   https://www.tutorialspoint.com/java/index.htm
 */
package com.mockcompany.webapp.controller;

/*
 * An import statement allows the current class to use the class being imported
 */
import com.mockcompany.webapp.data.ProductItemRepository;
import com.mockcompany.webapp.model.ProductItem;
/* The springframework package allows us to take advantage of the spring capabilities */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/* java.util package provides useful utilities */
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
public class SearchController {
    private final SearchService searchService;

    @Autowired
    public SearchController(ProductItemRepository productItemRepository) {
        this.productItemRepository = productItemRepository;
    }

    /**
     * The search method, annotated with @GetMapping telling spring this method should be called
     * when an HTTP GET on the path /api/products/search is made.  A single query parameter is declared
     * using the @RequestParam annotation.  The value that is passed when performing a query will be
     * in the query parameter.
     * @param query
     * @return The filtered products
     */
    @GetMapping("/api/products/search")
    public Collection<ProductItem> search(@RequestParam("query") String query) {
    return this.searchService.search(query);    
    }
}
 61 changes: 61 additions & 0 deletions61  
SearchController.java
Original file line number	Diff line number	Diff line change
@@ -0,0 +1,61 @@
package com.mockcompany.webapp.controller;

import com.mockcompany.webapp.data.ProductItemRepository;
import com.mockcompany.webapp.model.ProductItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
public class SearchController {

    private final ProductItemRepository productItemRepository;

    @Autowired
    public SearchController(ProductItemRepository productItemRepository) {
        this.productItemRepository = productItemRepository;
    }

    @GetMapping("/api/products/search")
    public Collection<ProductItem> search(@RequestParam("query") String query) {
        Iterable<ProductItem> allItems = this.productItemRepository.findAll();
        List<ProductItem> itemList = new ArrayList<>();

        boolean exactMatch = false;
        if (query.startsWith("\"") && query.endsWith("\"")) {
            exactMatch = true;
            query = query.substring(1, query.length() - 1);
        } else {
              query = query.toLowerCase();
        }

       for (ProductItem item : allItems) {
            boolean nameMatches;
            boolean descMatches;
            // Check if we are doing exact match or not
            if (exactMatch) {
                // Check if name is an exact match
                nameMatches = query.equals(item.getName());
                // Check if description is an exact match
                descMatches = query.equals(item.getDescription());
            } else {
                // We are doing a contains ignoring case check, normalize everything to lowercase
                // Check if name contains query
                nameMatches = item.getName().toLowerCase().contains(query);
                // Check if description contains query
                descMatches = item.getDescription().toLowerCase().contains(query);
            }

            // If either one matches, add to our list
            if (nameMatches || descMatches) {
                itemList.add(item);
            }
        }
        return itemList;
    }
}
 50 changes: 50 additions & 0 deletions50  
SearchService.java
Original file line number	Diff line number	Diff line change
@@ -0,0 +1,50 @@
package com.mockcompany.webapp.service;

import com.mockcompany.webapp.data.ProductItemRepository;
import com.mockcompany.webapp.model.ProductItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class SearchService {

     private final ProductItemRepository productItemRepository;

    @Autowired
    public SearchService(ProductItemRepository productItemRepository) {
        this.productItemRepository = productItemRepository;
    }

        Iterable<ProductItem> allItems = this.productItemRepository.findAll();
        List<ProductItem> itemList = new ArrayList<>();

        boolean exactMatch = false;
        if (query.startsWith("\ "") && query.endsWith("\"")){
            exactMatch = true;
            //Extract the qoutes
            query = query.substring(1, query.length() - 1);
        }else{
            query = query.toLowerCase();
        }

         for (ProductItem item : allItems) {
            boolean nameMatches;
            boolean descMatches;
            if(exactMatch){
                nameMatches = query.equals(item.getName());
                descMatches = query.equals(item.getDescription());
            }else{
                nameMatches = item.getName().toLowerCase().contains(query);
                descMatches = item.getDescription().toLowerCase().contains(query);
            }
            if(nameMatches || descMatches){
                itemList.add(item);
            }
        }
        return itemList;
    }
}