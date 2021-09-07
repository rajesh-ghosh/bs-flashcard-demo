package org.bigspring.service;

import org.bigspring.model.LocaleEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class LoaderTest {

    @Autowired
    private LoaderService loader;

    @Autowired
    @Qualifier("localeService")
    private LocaleService locSvc;

    @Before
    public void before() {
        locSvc.deleteAll();
    }

    @After
    public void after() {
        locSvc.deleteAll();
    }

    @Test
    public void test_loadLocales() throws IOException {
        loader.loadLocale();
        List<LocaleEntity> all = locSvc.findAllLocales();
        assertNotNull("locales should be present", all);
        assertTrue("more than 1 locales", all.size() > 1);
    }


}
