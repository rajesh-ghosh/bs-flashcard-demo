package org.bigspring.ws;

import org.bigspring.common.CardsSummaryBean;
import org.bigspring.service.CardGroupService;
import org.bigspring.service.CardService;
import org.bigspring.service.LocaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin
public class DashboardController {

    @Autowired
    @Qualifier("localeService")
    private LocaleService locSvc;

    @Autowired
    @Qualifier("cardGroupService")
    private CardGroupService groupSvc;

    @Autowired
    @Qualifier("cardService")
    private CardService cardSvc;

    @GetMapping("/cardsets")
    @ResponseBody
    public int getCardGroupCount() {

        var groups = groupSvc.findAllCardGroups();
        int count = groups == null ? 0 : groups.size();

        return(count);
    }

    @GetMapping("/cards")
    @ResponseBody
    @Transactional
    public CardsSummaryBean getCardsCount() {
        var bean = cardSvc.getCardsSummary();
        return(bean);
    }

}
