package com.example.musicplayer;

import android.app.Application;
import android.content.Intent;

public class MusicPlayerAppliction extends Application{
	
	@Override
	public void onCreate() {
		Intent intent = new Intent(this,MusicPlayerService.class);
		startService(intent);
		super.onCreate();
	}
	
	

}
