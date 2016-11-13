package com.spr.crossgit.repo.remote;

public class RemoteStatus {
    
    private String aheadRefs = "";
    public int commitsAhead;
    public int commitsBehind;

    void setAheadRefs(String s) {
        this.aheadRefs = s;
    }

    public String getAheadRefs() {
        return aheadRefs;
    }

    boolean isChanged() {
        return commitsAhead > 0 || commitsBehind > 0;
    }

    @Override
    public String toString() {
        return String.format("ahead=%d, behind=%d", commitsAhead, commitsBehind);
    }

}
