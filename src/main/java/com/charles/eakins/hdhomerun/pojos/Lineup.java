package com.charles.eakins.hdhomerun.pojos;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Lineup {

    @JsonProperty("GuideNumber")
    private String GuideNumber;
    @JsonProperty("GuideName")
    private String GuideName;
    @JsonProperty("URL")
    private String URL;
    @JsonProperty("DRM")
    private Integer DRM;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The GuideNumber
     */
    @JsonProperty("GuideNumber")
    public String getGuideNumber() {
        return GuideNumber;
    }

    /**
     *
     * @param GuideNumber
     * The GuideNumber
     */
    @JsonProperty("GuideNumber")
    public void setGuideNumber(String GuideNumber) {
        this.GuideNumber = GuideNumber;
    }

    /**
     *
     * @return
     * The GuideName
     */
    @JsonProperty("GuideName")
    public String getGuideName() {
        return GuideName;
    }

    /**
     *
     * @param GuideName
     * The GuideName
     */
    @JsonProperty("GuideName")
    public void setGuideName(String GuideName) {
        this.GuideName = GuideName;
    }

    /**
     *
     * @return
     * The URL
     */
    @JsonProperty("URL")
    public String getURL() {
        return URL;
    }

    /**
     *
     * @param URL
     * The URL
     */
    @JsonProperty("URL")
    public void setURL(String URL) {
        this.URL = URL;
    }

    /**
     *
     * @return
     * The DRM
     */
    @JsonProperty("DRM")
    public Integer getDRM() {
        return DRM;
    }

    /**
     *
     * @param DRM
     * The DRM
     */
    @JsonProperty("DRM")
    public void setDRM(Integer DRM) {
        this.DRM = DRM;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
