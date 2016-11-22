package com.spr.crossgit;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public final class DesktopHelper {

    public static final boolean IS_WINDOWS_OS = System.getProperty("os.name")
            .toLowerCase(Locale.ENGLISH)
            .startsWith("windows");

    /**
     * Opens specified directory in OS file explorer.
     */
    public static void openDirectory(final File folder) throws IOException {
        if (IS_WINDOWS_OS) {
            // Specific fix for Windows.
            // If running Windows and path is the default "Magarena" directory
            // then Desktop.getDesktop() will start a new instance of Magarena
            // instead of opening the directory! This is because the "Magarena"
            // directory and "Magarena.exe" are both at the same level and
            // Windows incorrectly assumes you mean "Magarena.exe".
            new ProcessBuilder("explorer.exe", folder.getPath()).start();
        } else {
            Runtime.getRuntime().exec("nemo " + folder.getPath());
        }
    }

    public static void openDirectory(final String path) throws IOException {
        openDirectory(new File(path));
    }

    public static void openURL(final String url) {
        try {
            new ProcessBuilder("x-www-browser", url).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private DesktopHelper() {}
}
