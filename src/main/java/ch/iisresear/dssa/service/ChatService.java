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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class ChatService {
    private TrainingDataRepository trainingDataRepository;
    private GateService gateService;
    private DSSAProperties dssaProperties;
    private Map<String,Story> stories;

    @Autowired
    public ChatService(TrainingDataRepository trainingDataRepository, GateService gateService, DSSAProperties dssaProperties) {
        this.trainingDataRepository = trainingDataRepository;
        this.gateService = gateService;
        this.dssaProperties = dssaProperties;
        stories = new HashMap<String,Story>();
    }

    public String message(String utterance, String userId){
        String response = "";
        TrainingData trainingData = new TrainingData();
        trainingData.setUserId(userId);
        trainingData.setUtterance(utterance);
        
        Story curStory = null;
        if(!stories.containsKey(userId)) {
            try {
                curStory = new Story(ResourceUtils.getFile(dssaProperties.getStoryFile()), gateService);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
