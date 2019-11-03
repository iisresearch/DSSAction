/*
 * Copyright (c) 2019. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package ch.iisresear.dssa.action;

import com.google.actions.api.ActionRequest;
import com.google.actions.api.ActionResponse;
import com.google.actions.api.DialogflowApp;
import com.google.actions.api.ForIntent;
import com.google.actions.api.response.ResponseBuilder;
import com.google.api.services.actions_fulfillment.v2.model.User;

import ch.iisresear.dssa.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.ws.rs.client.Invocation; 
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Client; 
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ActionsApp extends DialogflowApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionsApp.class);

    @ForIntent("Welcome Intent")
    public ActionResponse welcome(ActionRequest request) {
        LOGGER.info("Welcome Intent -> start");
        ResponseBuilder responseBuilder = getResponseBuilder(request);
        ResourceBundle rb = ResourceBundle.getBundle("resources", Locale.forLanguageTag("en-US"));
        User user = request.getUser();

        if (user != null && user.getLastSeen() != null) {
            responseBuilder.add(rb.getString("welcome_back"));
        } else {
            responseBuilder.add(rb.getString("welcome"));
        }
        
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(Constants.WEB_SERVICE_URL);
        Invocation.Builder req = target.request();
        
        Response res = req.accept(MediaType.APPLICATION_JSON).get();
        LOGGER.debug(res.toString());

        LOGGER.info("Welcome intent end.");
        return responseBuilder.build();
    }

    @ForIntent("Good Bye Intent")
    public ActionResponse bye(ActionRequest request) {
        LOGGER.info("Good Bye Intent -> start");
        ResponseBuilder responseBuilder = getResponseBuilder(request);
        ResourceBundle rb = ResourceBundle.getBundle("resources", Locale.forLanguageTag("en-US"));

        responseBuilder.add(rb.getString("bye")).endConversation();
        LOGGER.info("Bye intent end.");
        return responseBuilder.build();
    }
}