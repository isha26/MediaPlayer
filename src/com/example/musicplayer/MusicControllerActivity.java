package com.example.musicplayer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.TextView;

import com.example.musicplayer.MusicPlayerService.MusicServiceBinder;
import com.example.musicplayer.MusicPlayerService.ServiceCallback;

public class MusicControllerActivity extends Activity implements ServiceCallback,MediaPlayerControl{
	
	private MediaController mediaController;
	private MusicServiceConnection connection;
	private static boolean serviceBound;
	private MusicPlayerService musicPlayerService;
	private ArrayList<Song> songList;
	private int songPosition;
	private TextView songName;
	private int seekToPosition;
	private String songTitle;
	private String songArtist;
	private String songAlbum;
	private static final String TAG = MusicControllerActivity.class.getName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_activity);
		songName = (TextView)findViewById(R.id.song_name);
		Intent intent = getIntent();
		
		songList = intent.getParcelableArrayListExtra(MainActivity.SONG_LIST);
		songPosition = intent.getIntExtra(MainActivity.SONG_POSITION, 0);
		if(songList != null){
		if(songPosition < songList.size()){
		Song song = ((Song)songList.get(songPosition));
		songTitle = song.getTitle();
		songName.setText(songTitle);
		
		songArtist = song.getArtist();
		
		songAlbum = song.getAlbum();
		}
		}
		
		
		mediaController = new MusicMediaController(this, true);
		mediaController.setMediaPlayer(this);
		mediaController.setAnchorView(songName);
		mediaController.setEnabled(true);
		mediaController.setPrevNextListeners(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				musicPlayerService.playNext();
				
			}
		}, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				musicPlayerService.playPrevious();
			}
		});
		Intent serviceIntent = new Intent(getApplicationContext(), MusicPlayerService.class);
		connection = new MusicServiceConnection();
		bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
	}
	
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		
		super.onStart();
	}
	
	@Override
	protected void onDestroy() {
		hideMediaController();
		unbindService(connection);
		super.onDestroy();
	}
	
	private class MusicMediaController extends MediaController{

		public MusicMediaController(Context context) {
			super(context);
		}
		
		public MusicMediaController(Context context,boolean useForward) {
			super(context, useForward);
		}
		
		
		@Override
		public void show(int timeout) {
			// TODO Auto-generated method stub
			super.show(0);
		}
		
		@Override
		public void show() {
			// TODO Auto-generated method stub
			super.show(0);
		}
		
	}
	
	private class MusicServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			MusicServiceBinder serviceBinder = (MusicServiceBinder) arg1;
			musicPlayerService = serviceBinder.getMusicService();
			musicPlayerService.setSongs(songList);
			musicPlayerService.setServiceCallback(MusicControllerActivity.this);
			musicPlayerService.setPosition(songPosition);
			Song song = songList.get(songPosition);
			int songId = song.getId();
			musicPlayerService.setSeekPosition(seekToPosition);
			musicPlayerService.playSong(songId);
			musicPlayerService.setSongTitle(songTitle);
			musicPlayerService.setSongArtist(songArtist);
			musicPlayerService.setSongAlbum(songAlbum);
			serviceBound = true;

		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			musicPlayerService.stopMediaPlayer();
			serviceBound = false;
		}

	}
	
	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return true;
	}

	@Override
	public boolean canSeekForward() {
		return true;
	}

	@Override
	public int getAudioSessionId() {
		return 0;
	}

	@Override
	public int getBufferPercentage() {
		return 0;
	}

	@Override
	public int getCurrentPosition() {
		return musicPlayerService.getCurrentPosition();
	}

	@Override
	public int getDuration() {
		// TODO Auto-generated method stub
		return musicPlayerService.getDuration();
	}

	@Override
	public boolean isPlaying() {
		return musicPlayerService.isPlaying();
	}

	@Override
	public void pause() {
		musicPlayerService.pause();
		
	}

	@Override
	public void seekTo(int arg0) {
		musicPlayerService.seekTo(arg0);
		
	}

	@Override
	public void start() {
		musicPlayerService.start();
	}

	@Override
	public void showMediaController() {
		mediaController.show(0);
	}
	
	@Override
	public void hideMediaController() {
		mediaController.hide();
		
	}
	
	@Override
	protected void onPause() {
		Log.d(TAG,"Inside On Pause");
		super.onPause();
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
			finish();
		}
		return super.dispatchKeyEvent(event);
	}
	

}
