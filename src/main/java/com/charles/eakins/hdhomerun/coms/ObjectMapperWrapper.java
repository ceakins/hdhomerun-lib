package com.charles.eakins.hdhomerun.coms;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectMapperWrapper {
    private static final Logger LOG = LoggerFactory.getLogger(ObjectMapperWrapper.class);
    private static com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    private com.fasterxml.jackson.databind.ObjectMapper createMapper() {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    /**
     * @return ObjectMapper instance which is used for converting between Java objects
     * (instances of JDK provided core classes, beans)
     */
    public static synchronized com.fasterxml.jackson.databind.ObjectMapper getObjectMapper() {

        if (objectMapper == null) {
            objectMapper = new ObjectMapperWrapper().createMapper();
        }
        return objectMapper;

    }
}
