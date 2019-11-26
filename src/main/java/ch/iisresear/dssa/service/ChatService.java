/*
 * Copyright (c) 2019. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package ch.iisresear.dssa.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.iisresear.dssa.action.Story;
import ch.iisresear.dssa.data.domain.TrainingData;
import ch.iisresear.dssa.data.repository.TrainingDataRepository;
import gate.util.GateException;

@Service
public class ChatService {
    private TrainingDataRepository trainingDataRepository;
    private GateService gateService;
    Map<String,Story> stories;

    @Autowired
    public ChatService(TrainingDataRepository trainingDataRepository, GateService gateService) {
        this.trainingDataRepository = trainingDataRepository;
        this.gateService = gateService;
        stories = new HashMap<String,Story>();
    }

    public String message(String utterance, String userId){
        String response;
        TrainingData trainingData = new TrainingData();
        trainingData.setUserId(userId);
        trainingData.setUtterance(utterance);
        
        Story curStory = null;
        if(!stories.containsKey(userId)) {
        	curStory = new Story("bi-constraints.csv", gateService);
        	stories.put(userId, curStory);
        	return curStory.getNextResponse("");
        }
        else curStory = stories.get(userId);

        try {
            Set<String> concepts = gateService.getConcepts(utterance);
            response = curStory.getNextResponse(utterance);
            //response = userId + ", " + utterance + " " + concepts;
        } catch (GateException e) {
            e.printStackTrace();
            response = e.getMessage();
        }

        trainingData.setResponse(response);
        trainingDataRepository.save(trainingData);
        return response;
    }
}
