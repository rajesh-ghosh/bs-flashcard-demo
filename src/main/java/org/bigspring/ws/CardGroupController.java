package org.bigspring.ws;

import org.bigspring.model.CardGroupEntity;
import org.bigspring.service.CardGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class CardGroupController {

    @Autowired
    @Qualifier("cardGroupService")
    private CardGroupService groupSvc;

    @Transactional
    @GetMapping("/cardsets")
    @ResponseBody
    public List<CardGroupEntity> findAllCardGroups() {
        List<CardGroupEntity> all = groupSvc.findAllCardGroups();
        return(all);
    }

    @Transactional
    @PostMapping("/cardsets")
    @ResponseBody
    public List<CardGroupEntity> saveAll(@RequestBody List<CardGroupEntity> groups) {

        List<CardGroupEntity> groups2 = groupSvc.saveAll(groups);
        return(groups2);
    }

    @Transactional
    @DeleteMapping("/cardsets")
    @ResponseBody
    public List<CardGroupEntity> deleteAll(@RequestBody List<Long> iDs) {
        List<CardGroupEntity> groups2 = groupSvc.deleteAll(iDs);
        return(groups2);
    }

    @Transactional
    @GetMapping("/cardsets/search")
    @ResponseBody
    public List<CardGroupEntity> fuzzySearch(@RequestParam("q") String term) {
        var groups = groupSvc.fuzzySearch(term);
        return(groups);
    }

    @Transactional
    @GetMapping("/cardsets/{id:[0-9]+}")
    @ResponseBody
    public CardGroupEntity findById(@PathVariable(value = "id") Long id) {
        CardGroupEntity one = groupSvc.findById(id);
        return(one);
    }

    @Transactional
    @PutMapping("/cardsets/{id:[0-9]+}")
    @ResponseBody
    public CardGroupEntity update(@PathVariable("id") Long id, @RequestBody CardGroupEntity group) {

        if (group.getId() == null)
            group.setId(id);

        CardGroupEntity group2 = groupSvc.save(group);
        return(group2);
    }

    @Transactional
    @DeleteMapping("/cardsets/{id:[0-9]+}")
    @ResponseBody
    public CardGroupEntity delete(@PathVariable("id") Long id) {
        var group = groupSvc.delete(id);
        return(group);
    }

}
