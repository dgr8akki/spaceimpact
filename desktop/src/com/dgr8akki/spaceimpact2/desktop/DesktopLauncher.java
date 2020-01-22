package com.dgr8akki.spaceimpact2.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.dgr8akki.spaceimpact2.GlobalVariables;
import com.dgr8akki.spaceimpact2.SpaceImpact2;

public class DesktopLauncher {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Space Impact II";
        config.width = GlobalVariables.windowsWidth;
        config.height = GlobalVariables.windowsHeight;
        config.fullscreen = true;
        new LwjglApplication(new SpaceImpact2(), config);
    }
}
