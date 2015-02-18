package com.mygdx.Advancedemblem.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.Advancedemblem.MyGdxGame;

public class DesktopLauncher {

    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1280; //1280
        config.height = 720; //720
        new LwjglApplication(new MyGdxGame(), config);
    }
}
