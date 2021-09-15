package org.bigspring.ws;

import org.bigspring.model.CardEntity;
import org.bigspring.model.CardTLBean;
import org.bigspring.model.FacetBean;
import org.bigspring.service.CardService;
import org.bigspring.service.LocaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class CardController {

    @Autowired
    @Qualifier("cardService")
    private CardService svc;

    @GetMapping("/cards")
    @Transactional
    @ResponseBody
    public List<CardEntity> findCards(@RequestParam(value="filterby", required = false, defaultValue = "all") String filterby) {

        List<CardEntity> cards = new ArrayList<>();
        if ("all".equalsIgnoreCase(filterby))
            cards = svc.findAll();
        else if ("fulltls".equalsIgnoreCase(filterby))
            cards = svc.getCardsByTlStatus(true);
        else if ("parttls".equalsIgnoreCase(filterby))
            cards = svc.getCardsByTlStatus(false);
        else
            throw new IllegalArgumentException("Invalid filter type - " + filterby);


        return(cards);
    }

    @PostMapping("/cards")
    @Transactional
    @ResponseBody
    public List<CardEntity> saveAll(@RequestBody List<CardEntity> cards) {
        var saved = svc.saveAll(cards);
        return(saved);
    }

    @DeleteMapping("/cards")
    @Transactional
    @ResponseBody
    public List<CardEntity> deleteAll(@RequestBody List<Long> iDs) {
        var cards = svc.deleteAll(iDs);
        return(cards);
    }

    @GetMapping("/cards/search")
    @Transactional
    @ResponseBody
    public List<CardEntity> fuzzySearch(@RequestParam(value = "q", required = true)  String term) {
        var cards = svc.fuzzySearch(term);
        if (cards == null || cards.isEmpty()) {
            cards = svc.wildCardSearch(term);
        }
        return (cards);
    }

    @GetMapping("/cards/tagcloud")
    @Transactional
    @ResponseBody
    public List<FacetBean> getTagCloud() {
        var tc = svc.tagCloud();
        return (tc);
    }

    @GetMapping("/cards/{id:[0-9]+}")
    @Transactional
    @ResponseBody
    public CardEntity findOne(@PathVariable(value = "id", required = true) Long id) {
        var card = svc.findById(id);
        return (card);
    }

    @GetMapping("/cards/{id:[0-9]+}/tls")
    @Transactional
    @ResponseBody
    public List<CardTLBean> findAll(@PathVariable(value = "id", required = true) Long id) {
        var card = svc.findById(id);
        var tls = new ArrayList<CardTLBean>(card.getTranslations());

        return(tls);
    }

    @PostMapping("/cards/{id:[0-9]+}/tls")
    @Transactional
    @ResponseBody
    public CardEntity addTranslations(@PathVariable(value = "id", required = true) Long id, @RequestBody List<CardTLBean> tlBeans) {
        var card = svc.addTranslations(id, tlBeans);
        return (card);
    }

}
