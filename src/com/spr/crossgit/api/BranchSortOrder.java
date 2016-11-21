package com.spr.crossgit.api;

import com.spr.crossgit.Prefs;

public enum BranchSortOrder {

    NAME,
    NAME_REVERSED,
    DATETIME,
    DATETIME_REVERSED;

    private static BranchSortOrder value =
            BranchSortOrder.valueOf(Prefs.userPrefs.get("branches.sortorder", NAME.name()));

    public static void setValue(BranchSortOrder sortOrder) {
        value = sortOrder;
        Prefs.userPrefs.put("branches.sortorder", value.name());
    }

    public static BranchSortOrder getValue() {
        return value;
    }

    public static boolean isEqualTo(BranchSortOrder sortOrder) {
        return value.equals(sortOrder);
    }
}
