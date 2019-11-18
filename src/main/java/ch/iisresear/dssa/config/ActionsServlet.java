/*
 * Copyright (c) 2019. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package ch.iisresear.dssa.config;

import com.google.actions.api.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@WebServlet(name = "actions", value = "/*")
public class ActionsServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(ActionsServlet.class);
    @Inject
    private App actionsApp;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String body = req.getReader().lines().collect(Collectors.joining());
        LOG.info("doPost, body = {}", body);

        try {
            String jsonResponse = actionsApp.handleRequest(body, getHeadersMap(req)).get();
            LOG.info("Generated json = {}", jsonResponse);
            res.setContentType("application/json");
            writeResponse(res, jsonResponse);
        } catch (InterruptedException | ExecutionException e) {
            handleError(res, e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("text/plain");
        response
                .getWriter()
                .println(
                        "ActionsServlet is listening but requires valid POST request to respond with Action response.");
    }

    private void writeResponse(HttpServletResponse res, String asJson) {
        try {
            res.getWriter().write(asJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleError(HttpServletResponse res, Throwable throwable) {
        try {
            throwable.printStackTrace();
            LOG.error("Error in App.handleRequest ", throwable);
            res.getWriter().write("Error handling the intent - " + throwable.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, String> getHeadersMap(HttpServletRequest request) {
        HashMap<String, String> map = new HashMap<>();

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }

}

