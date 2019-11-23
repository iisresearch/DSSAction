/*
 * Copyright (c) 2019. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package ch.iisresear.dssa.action;

import ch.iisresear.dssa.Constants;
import ch.iisresear.dssa.service.ChatService;
import com.google.actions.api.ActionRequest;
import com.google.actions.api.ActionResponse;
import com.google.actions.api.DialogflowApp;
import com.google.actions.api.ForIntent;
import com.google.actions.api.response.ResponseBuilder;
import com.google.api.services.dialogflow_fulfillment.v2.model.WebhookRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Locale;
import java.util.ResourceBundle;

@Component
public class ActionsApp extends DialogflowApp {

    private ChatService chatService;

    @Autowired
    public ActionsApp(ChatService chatService) {
        super();
        this.chatService = chatService;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionsApp.class);

    @ForIntent("Dialog Capturing Intent")
    public ActionResponse dialogCapturing (ActionRequest request) {
        LOGGER.info("Dialog Capturing Intent -> start");
        WebhookRequest webhookRequest = request.getWebhookRequest();
        String response = null;
        if (webhookRequest != null) {
            response = chatService.message(webhookRequest.getQueryResult().getQueryText(), webhookRequest.getSession());
        }

        ResponseBuilder responseBuilder = getResponseBuilder(request);
        if (response != null) {
            responseBuilder.add(response);
        }

        LOGGER.info("Dialog Capturing Intent -> end");
        return responseBuilder.build();
    }

    @ForIntent("Welcome Intent")
    public ActionResponse welcome(ActionRequest request) {
        LOGGER.info("Welcome Intent -> start");
        ResponseBuilder responseBuilder = getResponseBuilder(request);
        ResourceBundle rb = ResourceBundle.getBundle("resources", Locale.forLanguageTag("en-US"));

        responseBuilder.add(rb.getString("welcome"));

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(Constants.WEB_SERVICE_URL);
        Invocation.Builder req = target.request();
        
        Response res = req.accept(MediaType.APPLICATION_JSON).get();
        String strResponse = res.readEntity(String.class);
        LOGGER.info("AOAME response: " + strResponse);

        LOGGER.info("Welcome Intent -> end");
        return responseBuilder.build();
    }

    @ForIntent("Good Bye Intent")
    public ActionResponse bye(ActionRequest request) {
        LOGGER.info("Good Bye Intent -> start");
        ResponseBuilder responseBuilder = getResponseBuilder(request);
        ResourceBundle rb = ResourceBundle.getBundle("resources", Locale.forLanguageTag("en-US"));

        responseBuilder.add(rb.getString("bye")).endConversation();
        LOGGER.info("Good Bye intent -> end");
        return responseBuilder.build();
    }
}
