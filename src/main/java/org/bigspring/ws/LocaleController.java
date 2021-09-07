package org.bigspring.ws;

import org.bigspring.model.LocaleEntity;
import org.bigspring.service.LocaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class LocaleController {

    @Autowired
    @Qualifier("localeService")
    private LocaleService locSvc;

    @GetMapping("/locales")
    @ResponseBody
    public List<LocaleEntity> findAll(@RequestParam(name="enabled", required = false, defaultValue = "false") boolean enabled) {

        List<LocaleEntity> locales = null;
        if (enabled)
            locales = locSvc.findByEnabledLocales();
        else
            locales = locSvc.findAllLocales();

        return (locales);
    }

    @PostMapping(value = "/locales")
    @ResponseBody
    public List<LocaleEntity> addAll(@RequestBody List<LocaleEntity> locales) {

        var locales2 = locSvc.saveAll(locales);

        return(locales2);
    }

    @DeleteMapping(value = "/locales")
    @ResponseBody
    public List<LocaleEntity> delete(@RequestBody  List<Long> iDs) {
        var locales = locSvc.delete(iDs);
        return(locales);
    }

    @GetMapping("locales/{locale[A-Za-z]+}")
    @ResponseBody
    public LocaleEntity findByCode(@PathVariable("locale") String locale) {
        var one = locSvc.findByLocale(locale);
        return one;
    }

    @PutMapping("locales/{id:[0-9]+}")
    @ResponseBody
    public LocaleEntity save(@PathVariable("id") Long id, @RequestBody LocaleEntity locale) {
        if (locale.getId() == null)
            locale.setId(id);

        var locale2 = locSvc.save(locale);
        return (locale2);
    }
}
