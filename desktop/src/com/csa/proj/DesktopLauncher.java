package com.csa.proj;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.csa.proj.Catstacker;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("CatStacker");
		config.setResizable(false);
		config.setWindowedMode(480,720);
		config.useVsync(true);
		new Lwjgl3Application(new Catstacker(), config);
	}
}
