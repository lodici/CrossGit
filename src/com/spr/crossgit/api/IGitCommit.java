package com.spr.crossgit.api;

public interface IGitCommit {
    public String getCommittedOn();
    public String getAuthor();
    public String getMessage();
    public Object getHeadRefs();
}
