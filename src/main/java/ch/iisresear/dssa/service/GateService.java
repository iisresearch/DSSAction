/*
 * Copyright (c) 2019. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package ch.iisresear.dssa.service;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.CorpusController;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.GateConstants;
import gate.corpora.DocumentImpl;
import gate.corpora.RepositioningInfo;
import gate.util.GateException;

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
    
    public Set<String> getConcepts(String content) throws GateException{
    	
    	Set<String> ret = new HashSet<String>();
    	AnnotationSet defaultAnnotSet = null;
		defaultAnnotSet = processWithGate(content);
		
		Set annotTypesRequired = new HashSet();
		annotTypesRequired.add("BICONCEPT");
		
		if(defaultAnnotSet != null) {
			Set<Annotation> instances = new HashSet<Annotation>(defaultAnnotSet.get(annotTypesRequired));
			for(Annotation instance : instances) {
				FeatureMap curFeatures = instance.getFeatures();
				if(curFeatures.containsKey("concept"))ret.add((String)curFeatures.get("concept"));
			}
		}
		return ret;
    }
}
