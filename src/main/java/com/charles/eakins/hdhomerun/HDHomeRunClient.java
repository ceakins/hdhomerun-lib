package com.charles.eakins.hdhomerun;


import com.charles.eakins.hdhomerun.coms.HTTPClient;
import com.charles.eakins.hdhomerun.coms.ObjectMapperWrapper;
import com.charles.eakins.hdhomerun.pojos.Lineup;
import com.charles.eakins.hdhomerun.pojos.LineupStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class HDHomeRunClient {
    private static final Logger LOG = LoggerFactory.getLogger(HDHomeRunClient.class);
    private final static ObjectMapper MAPPER = ObjectMapperWrapper.getObjectMapper();
    private HTTPClient httpClient = null;

    private int maxConnections;
    private int maxConnectionsPerRoute;
    private int maxKeepAliveInSeconds;
    private String baseUrl;

    private final static int MAX_CONNECTIONS_DEFAULT = 20;
    private final static int MAX_CONNECTIONS_PER_ROUTE_DEFAULT = 10;
    private final static int MAX_KEEP_ALIVE_DEFAULT = 30;

    public HDHomeRunClient(String baseUrl) {
        this.maxConnections = MAX_CONNECTIONS_DEFAULT;
        this.maxConnectionsPerRoute = MAX_CONNECTIONS_PER_ROUTE_DEFAULT;
        this.maxKeepAliveInSeconds = MAX_KEEP_ALIVE_DEFAULT;
        this.baseUrl = baseUrl;
        this.httpClient = HTTPClient.getClient(MAX_CONNECTIONS_DEFAULT, MAX_CONNECTIONS_PER_ROUTE_DEFAULT,
                MAX_KEEP_ALIVE_DEFAULT);
    }
    public HDHomeRunClient(int maxConnections, int maxConnectionsPerRoute, int maxKeepAliveInSeconds, String baseUrl) {
        this.maxConnections = maxConnections;
        this.maxConnectionsPerRoute = maxConnectionsPerRoute;
        this.maxKeepAliveInSeconds = maxKeepAliveInSeconds;
        this.baseUrl = baseUrl;
        this.httpClient = HTTPClient.getClient(maxConnections, maxConnectionsPerRoute, maxKeepAliveInSeconds);
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public int getMaxKeepAliveInSeconds() {
        return maxKeepAliveInSeconds;
    }

    public int getMaxConnectionsPerRoute() {
        return maxConnectionsPerRoute;
    }

    public List<Lineup> getLineup() throws IOException {
        HttpResponse response = httpClient.httpGet(baseUrl + "/lineup.json");
        String json = httpClient.readResponse(response);
        Lineup[] lineup = MAPPER.readValue(json, Lineup[].class);
        List<Lineup> lineupList = Arrays.asList(lineup);
        return lineupList;
    }

    public LineupStatus getLineupStatus() throws IOException {
        HttpResponse response = httpClient.httpGet(baseUrl + "/lineup_status.json");
        String json = httpClient.readResponse(response);
        LineupStatus lineupStatus = MAPPER.readValue(json, LineupStatus.class);
        return lineupStatus;
    }
}
