package org.bigspring.ws;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.bigspring.model.LocaleEntity;
import org.bigspring.service.CardGroupService;
import org.bigspring.service.CardService;
import org.bigspring.service.LocaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lov")
@CrossOrigin
public class LovController {

    @Autowired
    @Qualifier("localeService")
    private LocaleService locSvc;

    @Autowired
    @Qualifier("cardGroupService")
    private CardGroupService groupSvc;

    @Autowired
    @Qualifier("cardService")
    private CardService cardSvc;


    @GetMapping("/locales")
    public MappingJacksonValue findaAllLocales(@RequestParam(name="enabled", required = false, defaultValue = "false") boolean enabled) {

        List<LocaleEntity> locales = null;
        if (enabled)
            locales = locSvc.findByEnabledLocales();
        else
            locales = locSvc.findAllLocales();

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "locale", "name", "isoCharSet");
        FilterProvider filters = new SimpleFilterProvider().addFilter("lovFilter", filter);

        var map = new MappingJacksonValue(locales);
        map.setFilters(filters);

        return (map);
    }

    @GetMapping("/cardsets")
    public MappingJacksonValue findAllCardGroup() {

        var groups = groupSvc.findAllCardGroups();

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "title", "enabled");
        FilterProvider filters = new SimpleFilterProvider().addFilter("lovFilter", filter);

        var map = new MappingJacksonValue(groups);
        map.setFilters(filters);

        return (map);
    }

    @GetMapping("/cards")
    public MappingJacksonValue findAllCards() {

        var cards = cardSvc.findAll();

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "challengeType", "challengeText", "challengeImageFileLoc");
        FilterProvider filters = new SimpleFilterProvider().addFilter("lovFilter", filter);

        var map = new MappingJacksonValue(cards);
        map.setFilters(filters);

        return (map);
    }

}
