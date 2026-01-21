package com.example.social_media.config;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DroolsConfig {

    @Bean
    public KieContainer feedKieContainer() {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kfs = kieServices.newKieFileSystem();
        kfs.write(ResourceFactory.newClassPathResource("rules/feed/feed-rules.drl"));
        KieBuilder kieBuilder = kieServices.newKieBuilder(kfs);
        kieBuilder.buildAll();
        return kieServices.newKieContainer(kieBuilder.getKieModule().getReleaseId());
    }

    @Bean
    public KieContainer newUserFeedKieContainer() {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kfs = kieServices.newKieFileSystem();
        kfs.write(ResourceFactory.newClassPathResource("rules/new-user-feed/new_user_feed_rules.drl"));
        KieBuilder kieBuilder = kieServices.newKieBuilder(kfs);
        kieBuilder.buildAll();
        return kieServices.newKieContainer(kieBuilder.getKieModule().getReleaseId());
    }

    @Bean
    public KieContainer adsKieContainer() {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kfs = kieServices.newKieFileSystem();
        kfs.write(ResourceFactory.newClassPathResource("rules/ads/ads-rules.drl"));
        KieBuilder kieBuilder = kieServices.newKieBuilder(kfs);
        kieBuilder.buildAll();
        return kieServices.newKieContainer(kieBuilder.getKieModule().getReleaseId());
    }
}



