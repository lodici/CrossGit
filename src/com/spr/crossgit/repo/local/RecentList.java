package com.spr.crossgit.repo.local;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * @see http://www.javaspecialists.eu/archive/Issue219.html
 */
public class RecentList<E> implements Iterable<E> {

    private final ArrayList<E> recent = new ArrayList<>();
    private final int maxLength;

    public RecentList(int maxLength) {
        this.maxLength = maxLength;
    }

    public void addToTop(E element) {
        recent.remove(element);
        recent.add(0, element);
        reduce();
    }

    void add(E element) {
        recent.add(element);
        reduce();
    }

    private void reduce() {
        while (recent.size() > maxLength) {
            recent.remove(recent.size() - 1);
        }
    }

    public void clear() {
        recent.clear();
    }

    @Override
    public Iterator<E> iterator() {
        return Collections.unmodifiableCollection(recent).iterator();
    }
}
