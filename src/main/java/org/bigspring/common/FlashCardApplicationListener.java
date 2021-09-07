package org.bigspring.common;

import org.bigspring.service.LoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class FlashCardApplicationListener {

    @Autowired
    @Qualifier("loaderService")
    private LoaderService loader;

    @EventListener
    public void onApplicationEvent(ContextStartedEvent event) {
        System.out.println("Lifecycle event ContextStartedEvent fired - " + event.toString());
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("Lifecycle event ContextRefreshedEvent fired - " + event.toString());
        try {
            loader.loadStarterDb();
        } catch (Exception e) {
            throw new IllegalStateException("Loading stater db failed !! ", e);
        }
    }

}
