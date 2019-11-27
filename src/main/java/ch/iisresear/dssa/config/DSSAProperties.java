/*
 * Copyright (c) 2019. University of Applied Sciences and Arts Northwestern Switzerland FHNW.
 * All rights reserved.
 */

package ch.iisresear.dssa.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="dssa")
public class DSSAProperties {
    private String storyFile;

    public String getStoryFile() {
        return storyFile;
    }

    public void setStoryFile(String storyFile) {
        this.storyFile = storyFile;
    }
}
