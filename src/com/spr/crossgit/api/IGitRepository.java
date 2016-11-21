package com.spr.crossgit.api;

public interface IGitRepository<T> {
    public String getBranch();
    public boolean hasUnCommittedChanges();
    public T get();
}
