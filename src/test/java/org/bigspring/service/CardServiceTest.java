package org.bigspring.service;

import org.bigspring.model.AllEnums;
import org.bigspring.model.CardEntity;
import org.bigspring.model.CardGroupEntity;
import org.bigspring.model.CardTLBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Tag;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class CardServiceTest {

    @Autowired
    @Qualifier("cardGroupService")
    private CardGroupService groupSvc;

    @Autowired
    @Qualifier("cardService")
    private CardService svc;

    @Before
    @Transactional
    public void before() {
        //svc.deleteAll();
        //var list = groupSvc.saveAll(makeCardGroups());
        //makeCards(list);
    }

    @After
    @Transactional
    public void after() {
        //svc.deleteAll();
    }

    //@Test
    @Transactional
    public void test_findAll() {
        var all = svc.findAll();
        assertNotNull("cards should exist", all);
        assertTrue("more than 1 cards", all.size() > 1);
    }

    @Test
    @Transactional
    public void test_addCard() {

        var groups = groupSvc.findAllCardGroups();
        CardGroupEntity group  = new CardGroupEntity();
        group.setId(groups.get(1).getId());

        int numCards = groups.get(1).getCards().size();

        CardEntity card = makeCard();
        card.setCardGroupRef(group);

        var cards = new ArrayList<CardEntity>();
        cards.add(card);

        var cards2 = svc.saveAll(cards);
        assertNotNull("cards should exist", cards2);
        assertTrue("atleast 1 card", cards2.size() > 0);
        var card2 = cards2.get(0);
        assertNotNull("Card should exist");
        assertNotNull("ID must exist", card2.getId());

        CardGroupEntity group2 = groupSvc.findById(groups.get(1).getId());
        assertNotNull("Group should exist", group2);
        assertTrue("Cards in group must be more than before", group2.getCards().size() > numCards);
    }

    @Test
    @Transactional
    public void test_addLanguage() {

        var cards = svc.findAll();
        var card = cards.get(1);

        CardTLBean tl1 = new CardTLBean();
        tl1.setLocaleCode("es_ES");
        tl1.setChallengeText("Spanish translation of - " + card.getChallengeText());
        tl1.setAnswerText("Spanish translation of - " + card.getAnswerText());

        CardTLBean tl2 = new CardTLBean();
        tl2.setLocaleCode("fr_FR");
        tl2.setChallengeText("French translation of - " + card.getChallengeText());
        tl2.setAnswerText("French translation of - " + card.getAnswerText());

        List<CardTLBean> tlList = new ArrayList<>();
        tlList.add(tl1); tlList.add(tl2);

        var card2 = svc.addTranslations(card.getId(), tlList);
        var card3 = svc.findById(card.getId());
        assertNotNull("card should exist", card3);
        assertTrue("atleast 2 translations", card3.getTranslations().size() >=2 );
    }

    //@Test
    @Transactional
    public void test_search() {

        List<CardEntity> cards = svc.fuzzySearch("india");

        assertNotNull("Matching cards should exist", cards);
        assertTrue("atleast 1 card", cards.size() > 0);
    }

    //@Test
    @Transactional
    public void test_tagCloud() {
        svc.tagCloud();
    }

    private List<CardGroupEntity> makeCardGroups() {
        var list = new ArrayList<CardGroupEntity>();
        list.add(makeCardGroup("TestGroup1"));
        list.add(makeCardGroup("TestGroup2"));
        return (list);
    }

    private List<CardEntity> makeCards(List<CardGroupEntity> groups) {

        var list = new ArrayList<CardEntity>();

        var group1 = groups.get(0);
        var group2 = groups.get(1);

        var card1 = makeCard();
        var card2 = makeCard();
        card1.setCardGroupRef(group1);
        card2.setCardGroupRef(group1);

        list.add(card1); list.add(card2);

        return (list);
    }

    private CardGroupEntity makeCardGroup(String title) {
        var group = new CardGroupEntity();
        group.setTitle(title);
        group.setEnabled(true);

        return(group);
    }

    private CardEntity makeCard() {
        var rand = Math.random();
        var card = new CardEntity();
        card.setChallengeType(AllEnums.CardType.TEXT);
        card.setChallengeText("Question - " + Double.toHexString(rand));
        card.setAnswerType(AllEnums.CardType.TEXT);
        card.setAnswerText("Answer - " + Double.toHexString(rand));
        card.setTagString("test sample demo");
        return(card);
    }



}
