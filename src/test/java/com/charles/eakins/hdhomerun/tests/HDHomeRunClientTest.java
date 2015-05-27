package com.charles.eakins.hdhomerun.tests;

import com.charles.eakins.hdhomerun.HDHomeRunClient;
import com.charles.eakins.hdhomerun.pojos.Lineup;
import com.charles.eakins.hdhomerun.pojos.LineupStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

public class HDHomeRunClientTest {
    private HDHomeRunClient client = new HDHomeRunClient("http://192.168.2.105");
    private static final Logger LOG = LoggerFactory.getLogger(HDHomeRunClientTest.class);
    @Test
    public void LineupTest() throws IOException {
        List<Lineup> lineupList = client.getLineup();
        assertNotNull(lineupList);
        for (Lineup lineup : lineupList) {
            LOG.info("Guide Name : {}",lineup.getGuideName());
            LOG.info("Guide Number : {}", lineup.getGuideNumber());
            LOG.info("URL : {}", lineup.getURL());
            LOG.info("DRM : {}", String.valueOf(lineup.getDRM()));
        }
    }

    @Test
    public void LineupStatusTest() throws IOException {
        LineupStatus lineupStatus = client.getLineupStatus();
        assertNotNull(lineupStatus);
        LOG.info("Source : {}", lineupStatus.getSource());
        LOG.info("Scan in progress : {}", String.valueOf(lineupStatus.getScanInProgress()));
        LOG.info("Scan possible : {}", String.valueOf(lineupStatus.getScanPossible()));
        LOG.info("Source list : {}", String.valueOf(lineupStatus.getSourceList()));
    }
}
