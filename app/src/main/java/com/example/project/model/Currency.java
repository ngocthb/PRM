package com.example.project.model;
import java.util.Map;
public class Currency {
    private boolean success;
    private String terms ;
    private String privacy;
    private String timestamp;
    private String source;
    private Map<String, Double> quotes;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getTimestamp() {
        return timestamp;
        }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;


    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }


    public Map<String, Double> getQuotes() {
        return quotes;
    }
    public void setQuotes(Map<String, Double> quotes) {
        this.quotes = quotes;
    }

}
