package org.myec3.socle.webapp.config;

public class ConfigTechException extends RuntimeException{


    public ConfigTechException(String message) {
        super(message);
    }

    public ConfigTechException(String message, Throwable cause) {
        super(message, cause);
    }
}
