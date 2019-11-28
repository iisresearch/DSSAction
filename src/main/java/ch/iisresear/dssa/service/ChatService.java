/*
 * Copyright (c) 2019. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package ch.iisresear.dssa.service;

import ch.iisresear.dssa.config.DSSAProperties;
import ch.iisresear.dssa.data.domain.TrainingData;
import ch.iisresear.dssa.data.repository.TrainingDataRepository;
import ch.iisresear.dssa.model.Story;
import gate.util.GateException;
import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ChatService {
    private TrainingDataRepository trainingDataRepository;
    private GateService gateService;
    private DSSAProperties dssaProperties;
    private PassiveExpiringMap<String, Story> stories;

    @Autowired
    public ChatService(TrainingDataRepository trainingDataRepository, GateService gateService, DSSAProperties dssaProperties) {
        this.trainingDataRepository = trainingDataRepository;
        this.gateService = gateService;
        this.dssaProperties = dssaProperties;
        stories = new PassiveExpiringMap<>(1200000); // Dialogflow has a session of 20 minutes
    }

    public String message(String utterance, String userId){
        String response = "";
        TrainingData trainingData = new TrainingData();
        trainingData.setUserId(userId);
        trainingData.setUtterance(utterance);
        
        Story curStory = null;
        if(!stories.containsKey(userId)) {
            curStory = new Story(dssaProperties.getStoryFile(), gateService);
            stories.put(userId, curStory);
            if (curStory != null) {
                return curStory.getNextResponse("");
            }
        }
        else curStory = stories.get(userId);

        try {
            Set<String> concepts = gateService.getConcepts(utterance);
            if (curStory != null) {
                response = curStory.getNextResponse(utterance);
            }
        } catch (GateException e) {
            e.printStackTrace();
            response = e.getMessage();
        }

        trainingData.setResponse(response);
        trainingDataRepository.save(trainingData);
        return response;
    }
}
