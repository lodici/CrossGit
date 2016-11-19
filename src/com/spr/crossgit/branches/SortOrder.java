package com.spr.crossgit.branches;

import com.spr.crossgit.Prefs;

enum SortOrder {

    NAME,
    NAME_REVERSED,
    DATETIME,
    DATETIME_REVERSED;

    private static SortOrder value =
            SortOrder.valueOf(Prefs.userPrefs.get("branches.sortorder", NAME.name()));

    static void setValue(SortOrder sortOrder) {
        value = sortOrder;
        Prefs.userPrefs.put("branches.sortorder", value.name());
    }

    static SortOrder getValue() {
        return value;
    }

    static boolean isEqualTo(SortOrder sortOrder) {
        return value.equals(sortOrder);
    }
}
