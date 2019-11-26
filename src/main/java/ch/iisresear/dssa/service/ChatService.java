/*
 * Copyright (c) 2019. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package ch.iisresear.dssa.service;

import ch.iisresear.dssa.data.domain.TrainingData;
import ch.iisresear.dssa.data.repository.TrainingDataRepository;
import gate.AnnotationSet;
import gate.util.GateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private TrainingDataRepository trainingDataRepository;
    private GateService gateService;

    @Autowired
    public ChatService(TrainingDataRepository trainingDataRepository, GateService gateService) {
        this.trainingDataRepository = trainingDataRepository;
        this.gateService = gateService;
    }

    public String message(String utterance, String userId){
        String response;
        TrainingData trainingData = new TrainingData();
        trainingData.setUserId(userId);
        trainingData.setUtterance(utterance);

        try {
            AnnotationSet annotationSet = gateService.processWithGate(utterance);
            response = utterance + " " + annotationSet.toString();
        } catch (GateException e) {
            e.printStackTrace();
            response = e.getMessage();
        }

        trainingData.setResponse(response);
        trainingDataRepository.save(trainingData);
        return response;
    }
}
