package org.bigspring.service;

import org.bigspring.model.AllEnums;
import org.bigspring.model.CardEntity;
import org.bigspring.model.CardGroupEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class CardGroupServiceTest {

    @Autowired
    @Qualifier("cardGroupService")
    private CardGroupService svc;

    @Autowired
    @Qualifier("cardRepository")
    private CardRepository cardRepo;

    @Before
    public void before() {
        svc.deleteAll();
        var groups = makeCardGroups();
        svc.saveAll(groups);
    }

    @After
    public void after() {
        svc.deleteAll();
    }

    //@Test
    @Transactional
    public void test_findAll() {

        var groups = svc.findAllCardGroups();
        assertNotNull("Card groups must be present", groups);
        assertTrue("More than one card groups", groups.size() > 1);

        CardGroupEntity group = groups.get(0);
        assertNotNull("Cards should not be null", group.getCards());
        assertTrue("Atleast 1 card must be present", group.getCards().size() >= 1);
    }

    @Test
    @Transactional
    public void test_addNewCard() {

        var groups = svc.findAllCardGroups();

        List<CardGroupEntity> groups2 = new ArrayList<>(groups);
        assertNotNull("Card groups must be present", groups2);
        assertTrue("More than one card groups", groups2.size() > 1);

        var group2 = groups2.get(0);

        CardEntity card = makeCard();
        card.setCardGroupRef(group2);
        group2.getCards().add(card);

        var groups3 = svc.saveAll(groups2);
        //assertNotNull("Card groups must be present", groups3);
        //assertTrue("More than one card groups", groups3.size() > 1);
    }

    //@Test
    @Transactional
    public void test_deleteAll() {

        svc.deleteAll();
        List<CardEntity> cards = cardRepo.findAll();

    }

    private List<CardGroupEntity> makeCardGroups() {

        var groups = new ArrayList<CardGroupEntity>();

        CardGroupEntity entity1 = new CardGroupEntity();
        entity1.setTitle("UT-Group1");
        entity1.setEnabled(true);
        entity1.getCards().add(makeCard());
        entity1.getCards().add(makeCard());
        groups.add(entity1);

        CardGroupEntity entity2 = new CardGroupEntity();
        entity2.setTitle("UT-Group2");
        entity2.setEnabled(true);
        entity2.getCards().add(makeCard());
        groups.add(entity2);

        return(groups);
    }

    private CardEntity makeCard() {
        var rand = Math.random();
        var card = new CardEntity();
        card.setAnswerType(AllEnums.CardType.TEXT);
        card.setChallengeText("Question - " + Double.toHexString(rand));
        card.setAnswerType(AllEnums.CardType.TEXT);
        card.setAnswerText("Answer - " + Double.toHexString(rand));
        card.setTagString("test sample demo");
        return(card);
    }

}
