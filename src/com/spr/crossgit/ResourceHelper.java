package com.spr.crossgit;

import java.io.InputStream;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public final class ResourceHelper {
    
    private static final ResourceHelper instance = new ResourceHelper();

    public static ImageView getImage(String name) {
        final InputStream inStream = instance.getClass().getResourceAsStream(
                "/resources/images/" + name);
        return new ImageView(new Image(inStream));
    }

    private ResourceHelper() {
    }
}
