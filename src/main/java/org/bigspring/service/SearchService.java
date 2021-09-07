package org.bigspring.service;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.logging.Logger;

//@Service
public class SearchService {

    //@Autowired
    private EntityManager em;

    private Logger logger = Logger.getLogger(SearchService.class.getName());

    //@Autowired
    public SearchService(EntityManager em) {
        this.em = em;
    }

    public void initializeHibernateSearch() {

        logger.info("### Initializing indexer ... ");

        String idxLoc = System.getProperty("hibernate.search.default.indexBase");
        logger.info("### Hibernate search index location from system property - " + idxLoc);
        idxLoc = System.getenv("hibernate.search.default.indexBase");
        logger.info("### Hibernate search index location from system env - " + idxLoc);

        try {
            FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
            fullTextEntityManager.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            logger.info("### Indexer received interrupt event !");
            e.printStackTrace();
        }
    }

}
