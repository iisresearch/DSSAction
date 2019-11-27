/*
 * Copyright (c) 2019. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package ch.iisresear.dssa.config;

import gate.CorpusController;
import gate.Gate;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.util.GateException;
import gate.util.persistence.PersistenceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Configuration
@ConditionalOnProperty("gate.application")
public class GateConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(GateConfig.class);


    @Autowired
    GateProperties gateProperties;

    @PostConstruct
    private void initGate() {
        try {
            LOGGER.info("Initializing Gate");
            Gate.init();
            LOGGER.info("Gate initialized");
        } catch (GateException e) {
            e.printStackTrace();
        }
    }

    @Bean
    public CorpusController gateApplication() {
        LOGGER.info("Initializing Gate Application");
        try {
            CorpusController corpusController = (CorpusController) PersistenceManager.loadObjectFromUrl(ResourceUtils.getURL(gateProperties.getApplication()));
            LOGGER.info("Gate Application initialized");
            return corpusController;
        } catch (IOException | ResourceInstantiationException | PersistenceException e) {
            e.printStackTrace();
            return null;
        }
    }
}