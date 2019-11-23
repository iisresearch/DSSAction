/*
 * Copyright (c) 2019. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package ch.iisresear.dssa.service;

import gate.*;
import gate.util.GateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class GateService {
    @Autowired
    private CorpusController gateApplication;
    private Corpus corpus = null;

    private static final Logger LOGGER = LoggerFactory.getLogger(GateService.class);

    @PostConstruct
    public void init() throws GateException {
        corpus = Factory.newCorpus("GateService corpus");
        gateApplication.setCorpus(corpus);
    }

    @PreDestroy
    public void destroy() {
        Factory.deleteResource(corpus);
    }

    public AnnotationSet processWithGate(String text) throws GateException {
        Document doc = null;

        try {
            doc = Factory.newDocument(text);
            gateApplication.getCorpus().add(doc);
            gateApplication.execute();
            gateApplication.getCorpus().clear();
            return doc.getAnnotations();
        } finally {
            if (doc != null) {
                Factory.deleteResource(doc);
            }
        }
    }
}
