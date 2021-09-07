package org.bigspring.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bigspring.model.CardGroupEntity;
import org.bigspring.model.LocaleEntity;
import org.bigspring.model.RevisionControlBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Service("loaderService")
public class LoaderService {

    @Autowired
    @Qualifier("localeService")
    private LocaleService localeSvc;

    @Autowired
    @Qualifier("cardGroupService")
    private CardGroupService groupSvc;

    @Value("classpath:/locales.json")
    private Resource localeFile;

    @Value("classpath:/cardGroups.json")
    private Resource cardGroupFile;

    @Value("${app.loader.load:false}")
    private boolean loadFlag;

    @Value("${app.loader.delete-old:false}")
    private boolean reset;

    private Logger logger = Logger.getLogger(LoaderService.class.getName());

    @Transactional
    List<LocaleEntity> loadLocale() throws IOException {

        if (!loadFlag) {
            logger.info("Starter db loader is not enabled. Skipping data load for locales !");
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        List<LocaleEntity> locales = mapper.readValue(localeFile.getInputStream(), new TypeReference<List<LocaleEntity>>() {});

        if (locales != null && !locales.isEmpty()) {

            locales.forEach(locale -> { if (locale.getRevisionControl() == null) locale.setRevisionControl(makeRevision()); });

            if (reset)
                localeSvc.deleteAll();

            localeSvc.saveAll(locales);
        }

        return locales;
    }

    List<CardGroupEntity> loadCardGroups() throws IOException {

        if (!loadFlag) {
            logger.info("Starter db loader is not enabled. Skipping data load for card groups !");
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        List<CardGroupEntity> groups = mapper.readValue(cardGroupFile.getInputStream(), new TypeReference<List<CardGroupEntity>>() {});

        if (reset)
            groupSvc.deleteAll();

        List<CardGroupEntity> groups2 = groupSvc.saveAll(groups);

        return(groups2);
    }

    @Transactional
    public void loadStarterDb() throws IOException {
        var locales = loadLocale();
        if (locales == null)
            logger.warning("No locales loaded !");
        else
            logger.info("Locales added -- " + locales.size());

        var groups = loadCardGroups();
        if (groups == null)
            logger.warning("No card groups loaded !");
        else
            logger.info("Card Groups added -- " + groups.size());
    }

    @Transactional
    public void resetStarterDb() {
        if (reset) {
            localeSvc.deleteAll();
            groupSvc.deleteAll();
        }
    }

    private RevisionControlBean makeRevision() {

        var rev = new RevisionControlBean();
        rev.setCreatedBy("admin");
        rev.setCreatedById(0);
        rev.setCreationDate(new Date());
        rev.setRevisedBy("admin");
        rev.setRevisedById(0);
        rev.setRevisionDate(new Date());

        return rev;
    }

}
