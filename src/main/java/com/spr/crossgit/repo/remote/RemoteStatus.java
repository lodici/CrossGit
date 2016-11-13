package com.spr.crossgit.repo.remote;

public class RemoteStatus {
    
    private String aheadRefs = "";

    void setAheadRefs(String s) {
        this.aheadRefs = s;
    }

    public String getAheadRefs() {
        return aheadRefs;
    }
}
