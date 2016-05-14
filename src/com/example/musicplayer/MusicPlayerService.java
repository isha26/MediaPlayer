package com.example.musicplayer;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class MusicPlayerService extends Service implements
		OnSeekCompleteListener, OnPreparedListener, OnErrorListener,
		OnCompletionListener {

	private Binder binder;
	private ArrayList<Song> songList;
	private MediaPlayer mediaPlayer;
	private static final String TAG = MusicPlayerService.class.getName();
	private static int currentPosition;
	private ServiceCallback serviceCallback;
	private int seekPos;
	private String songTitle;
	private String songAlbum;
	private static final int NOTIFICATION_ID = 1;
	private MusicBroadcastReceivers musicBroadcastreceivers;
	
	private static final String PREVIOUS_ACTION_FILTER = "com.example.musicplayer.previous";
	private static final String PAUSE_ACTION_FILTER = "com.example.musicplayer.pause";
	private static final String NEXT_ACTION_FILTER = "com.example.musicplayer.next";
	
	public String getSongAlbum() {
		return songAlbum;
	}

	public void setSongAlbum(String songAlbum) {
		this.songAlbum = songAlbum;
	}

	public String getSongArtist() {
		return songArtist;
	}

	public void setSongArtist(String songArtist) {
		this.songArtist = songArtist;
	}

	private String songArtist;

	@Override
	public void onCreate() {
		super.onCreate();
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnSeekCompleteListener(this);
		mediaPlayer.setOnErrorListener(this); 
		IntentFilter intentFiler = new IntentFilter();
		//intentFiler.addAction(PAUSE_ACTION_FILTER);
		//intentFiler.addAction(PREVIOUS_ACTION_FILTER);
		//intentFiler.addAction(NEXT_ACTION_FILTER);
		musicBroadcastreceivers = new MusicBroadcastReceivers();
		LocalBroadcastManager.getInstance(this).registerReceiver(musicBroadcastreceivers, intentFiler);
	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {
		// TODO Auto-generated method stub

	}

	@Override
	public IBinder onBind(Intent arg0) {
		binder = new MusicServiceBinder();
		return binder;
	}
	
	public void setSeekPosition(int seekPosition){
		seekPos = seekPosition;
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		Log.d(MusicPlayerService.class.getName(),"Inside on Error Listener");
		mp.reset();
		return false;
	}

	@Override
	public void onPrepared(MediaPlayer arg0) {
		if(!mediaPlayer.isPlaying()){
		mediaPlayer.seekTo(seekPos);
		showNotificationAndStartForeground();
		mediaPlayer.start();
		serviceCallback.showMediaController();
		}
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void showNotificationAndStartForeground(){
		Intent previousActionIntent = new Intent(this,MusicBroadcastReceivers.class);
		//previousActionIntent.setAction(PREVIOUS_ACTION_FILTER);
		PendingIntent previousPendingIntent = PendingIntent.getBroadcast(this, 0, previousActionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		Intent pauseActionIntent = new Intent(this,MusicBroadcastReceivers.class);
		//pauseActionIntent.setAction(PAUSE_ACTION_FILTER);
		PendingIntent pausePendingIntent = PendingIntent.getBroadcast(this, 0, pauseActionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		Intent nextActionIntent = new Intent(this,MusicBroadcastReceivers.class);
		//pauseActionIntent.setAction(NEXT_ACTION_FILTER);
		PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 0, nextActionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this).
				setContentText(getSongArtist()).setContentTitle(getSongTitle()).setSubText(getSongAlbum());
		builder.addAction(R.drawable.previous_button, "Previous", previousPendingIntent).
		addAction(R.drawable.pause_button, "Pause", pausePendingIntent).addAction(R.drawable.next_button, "Next", nextPendingIntent);
		Intent intent = new Intent(this,MusicControllerActivity.class);
		intent.putParcelableArrayListExtra(MainActivity.SONG_LIST, songList);
		intent.putExtra(MainActivity.SONG_POSITION, currentPosition);
		PendingIntent pendingIntent;
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(MusicControllerActivity.class);
		stackBuilder.addNextIntent(intent);
		pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		}
		else{
			pendingIntent= PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		}
		builder.setContentIntent(pendingIntent);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			builder.setColor(getResources().getColor(R.color.blue));
			builder.setSmallIcon(R.drawable.music_player);

		} else {
			builder.setSmallIcon(R.drawable.music_player);
		}
		builder.setAutoCancel(false);
		builder.setOngoing(true);
		
		NotificationManager notificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(NOTIFICATION_ID, builder.build());
	}
	
	public void setSongTitle(String title){
		songTitle = title;
	}
	
	public String getSongTitle(){
		return songTitle;
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		playNext();

	}

	public class MusicServiceBinder extends Binder {

		public MusicPlayerService getMusicService() {
			return MusicPlayerService.this;
		}
	}

	public void setSongs(ArrayList<Song> song) {
		songList = song;
	}

	public void stopMediaPlayer() {
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}
	
	public void setPosition(int position){
		currentPosition = position;
	}
	
	public void playSong(int id){
		try{
		Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
		if(mediaPlayer.isPlaying()){
			mediaPlayer.stop();
		}
		mediaPlayer.reset();
			
		mediaPlayer.setDataSource(this, uri);
		mediaPlayer.prepareAsync();
		}
		catch(Exception ex){
			Log.d(TAG,"Exception ocurred",ex);
		}
	}
	
	public void pause(){
		if(mediaPlayer != null){
		mediaPlayer.pause();
		}
	}
	
	public void seekTo(int arg){
		mediaPlayer.seekTo(arg);
	}
	
	public void start(){
		if(mediaPlayer != null){
		mediaPlayer.start();
	}
		}
	
	public boolean isPlaying(){
		if(mediaPlayer != null){
		return mediaPlayer.isPlaying();
		}
		else
			return false;
	}
	
	public int getDuration(){
		if(mediaPlayer != null){
		return mediaPlayer.getDuration();
		}
		else{
			return 0;
		}
	}
	
	public int getCurrentPosition(){
		if (mediaPlayer !=null && mediaPlayer.isPlaying()) {
			return mediaPlayer.getCurrentPosition();
		} else {
			return 0;
		}
	}
	
	public void playNext(){
		serviceCallback.hideMediaController();
		int id;
		currentPosition = currentPosition+1;
		if(currentPosition< songList.size()){
			 id = songList.get(currentPosition).getId();
		}
		else{
			id = songList.get(0).getId();
		}
		playSong(id);
	}
	
	public void playPrevious(){
		serviceCallback.hideMediaController();
		int id;
		currentPosition = currentPosition-1;
		if(currentPosition>= 0){
			 id = songList.get(currentPosition).getId();
		}
		else{
			id = songList.get(songList.size()-1).getId();
		}
		playSong(id);
		
	}
	
	public void setServiceCallback(ServiceCallback callback){
		serviceCallback = callback;
	}
	
	public interface ServiceCallback{
		public void showMediaController();
		public void hideMediaController();
	}
	
	@Override
	public void onDestroy() {
		Log.d("Music Player Service","On Destroy called");
		LocalBroadcastManager.getInstance(this).unregisterReceiver(musicBroadcastreceivers);
		super.onDestroy();
	}
	
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public void onTaskRemoved(Intent rootIntent) {
		NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(NOTIFICATION_ID);
		super.onTaskRemoved(rootIntent);
	}
	
	
	private class MusicBroadcastReceivers extends BroadcastReceiver{

		@Override
		public void onReceive(Context arg0, Intent intent) {
			Log.d(TAG,"Inside broadcast receivers");
			if(intent.getAction().equals(NEXT_ACTION_FILTER)){
				playNext();
			}
			else if(intent.getAction().equals(PAUSE_ACTION_FILTER)){
				
			}
			else if(intent.getAction().equals(PREVIOUS_ACTION_FILTER)){
				playPrevious();
			}
			
		}
		
	}

}
