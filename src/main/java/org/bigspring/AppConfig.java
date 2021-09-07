package org.bigspring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import javassist.Loader;
import org.bigspring.service.SearchService;

import org.hibernate.search.cfg.SearchMapping;
import org.hibernate.search.jpa.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.logging.Logger;

@Configuration
public class AppConfig {

    @Autowired
    private EntityManager em;

    private Logger logger = Logger.getLogger(AppConfig.class.getName());

    @Bean
    @Autowired
    @Transactional
    public SearchService getSearchService() {
        logger.info("### Launching search indexer ... ");
        var svc = new SearchService(em);
        svc.initializeHibernateSearch();

        return svc;
    }

//    @Configuration
//    public class JacksonConfiguration {
//        public JacksonConfiguration(ObjectMapper objectMapper) {
//            SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAll();
//            //objectMapper.setFilterProvider(new SimpleFilterProvider().addFilter("lovFilter", filter).setFailOnUnknownId(false));
//            objectMapper.setFilterProvider(new SimpleFilterProvider().setDefaultFilter(filter).setFailOnUnknownId(false));
//        }
//    }

    @Bean
    @Autowired
    public ObjectMapper getJacksonObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAll();
        mapper.setFilterProvider(new SimpleFilterProvider().setDefaultFilter(filter).setFailOnUnknownId(false));
        return(mapper);
    }

}
