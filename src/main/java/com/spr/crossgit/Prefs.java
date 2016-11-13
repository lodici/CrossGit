package com.spr.crossgit;

import java.util.prefs.Preferences;

public final class Prefs {

    public static final String DEBUG_BORDER_STYLE = "-fx-border-color: red;";

    public static final Preferences userPrefs =
            Preferences.userRoot().node("com/spr/crossgit");

    public static final boolean isDevMode = Boolean.getBoolean("devMode");

    private Prefs() {}
}
