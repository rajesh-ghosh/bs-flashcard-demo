package org.bigspring.service;

import org.bigspring.model.LocaleEntity;
import org.bigspring.model.RevisionControlBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class LocaleServiceTest {

    @Autowired
    @Qualifier("localeService")
    private LocaleService svc;

    @Before
    public void before() {
        svc.deleteAll();
        var locales = makeLocales();
        svc.saveAll(locales);
    }

    @After
    public void after() {
        svc.deleteAll();
    }

    @Test
    public void test_add() {

        var locales = svc.findAllLocales();
        assertNotNull("Locales should exist", locales);
        assertTrue("More than 1 locales", locales.size() > 1);

    }

    private List<LocaleEntity> makeLocales() {
        var locales = new ArrayList<LocaleEntity>();
        var entity1 = new LocaleEntity();
        entity1.setLocale("en_US");
        entity1.setIsoCharSet("UTF-8");
        entity1.setName("English-US");
        entity1.setRtlLang(false);
        entity1.setEnabled(true);
        entity1.setRevisionControl(makeRev());
        locales.add(entity1);

        var entity2 = new LocaleEntity();
        entity2.setLocale("en_GB");
        entity2.setIsoCharSet("UTF-8");
        entity2.setName("English-GB");
        entity2.setRtlLang(false);
        entity2.setEnabled(true);
        entity2.setRevisionControl(makeRev());
        locales.add(entity2);

        var entity3 = new LocaleEntity();
        entity3.setLocale("es_ES");
        entity3.setIsoCharSet("UTF-8");
        entity3.setName("Spanish-Spain");
        entity3.setRtlLang(false);
        entity3.setEnabled(true);
        entity3.setRevisionControl(makeRev());
        locales.add(entity3);

        return locales;
    }

    private RevisionControlBean makeRev() {
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
