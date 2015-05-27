package com.charles.eakins.hdhomerun.pojos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LineupStatus {

    @JsonProperty("ScanInProgress")
    private Integer ScanInProgress;
    @JsonProperty("ScanPossible")
    private Integer ScanPossible;
    @JsonProperty("Source")
    private String Source;
    @JsonProperty("SourceList")
    private List<String> SourceList = new ArrayList<String>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The ScanInProgress
     */
    @JsonProperty("ScanInProgress")
    public Integer getScanInProgress() {
        return ScanInProgress;
    }

    /**
     *
     * @param ScanInProgress
     * The ScanInProgress
     */
    @JsonProperty("ScanInProgress")
    public void setScanInProgress(Integer ScanInProgress) {
        this.ScanInProgress = ScanInProgress;
    }

    /**
     *
     * @return
     * The ScanPossible
     */
    @JsonProperty("ScanPossible")
    public Integer getScanPossible() {
        return ScanPossible;
    }

    /**
     *
     * @param ScanPossible
     * The ScanPossible
     */
    @JsonProperty("ScanPossible")
    public void setScanPossible(Integer ScanPossible) {
        this.ScanPossible = ScanPossible;
    }

    /**
     *
     * @return
     * The Source
     */
    @JsonProperty("Source")
    public String getSource() {
        return Source;
    }

    /**
     *
     * @param Source
     * The Source
     */
    @JsonProperty("Source")
    public void setSource(String Source) {
        this.Source = Source;
    }

    /**
     *
     * @return
     * The SourceList
     */
    @JsonProperty("SourceList")
    public List<String> getSourceList() {
        return SourceList;
    }

    /**
     *
     * @param SourceList
     * The SourceList
     */
    @JsonProperty("SourceList")
    public void setSourceList(List<String> SourceList) {
        this.SourceList = SourceList;
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
