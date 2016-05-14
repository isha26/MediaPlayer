package com.example.musicplayer;

import java.util.ArrayList;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends ActionBarActivity{

	private ArrayList<Song> songList;
	public static final String SONG_POSITION = "SONG_POSITION";
	public static final String SONG_LIST = "SONG_LIST"; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		songList = getSongList(new ArrayList<Song>());
		setContentView(R.layout.activity_main);
		final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.song_list);
		MusicPlayerList playerList = new MusicPlayerList(this,
				R.layout.music_player_list, songList);
		
		playerList.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, MusicControllerActivity.class);
				intent.putExtra(SONG_POSITION, recyclerView.getChildAdapterPosition(v));
				intent.putParcelableArrayListExtra(SONG_LIST, songList);
				MainActivity.this.startActivity(intent);
			}
			
		});
		recyclerView.setAdapter(playerList);
		RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(
				this, LinearLayoutManager.VERTICAL);
		recyclerView.addItemDecoration(dividerItemDecoration);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(linearLayoutManager);
	}

	private ArrayList<Song> getSongList(ArrayList<Song> songList) {
		String[] projection = { MediaStore.Audio.Media.TITLE,
				MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
				MediaStore.Audio.Media.DATA, MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.IS_MUSIC,
				MediaStore.Audio.Media.DURATION };
		String selection = MediaStore.Audio.Media.ARTIST + " = ?";
		String selectionArgs[] = { "Arijit Singh" };

		Cursor cursor = this.getContentResolver()
				.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,
						null, null,
						MediaStore.Audio.Media.TITLE + " ASC");

		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				String title = cursor.getString(0);
				String artist = cursor.getString(1);
				String album = cursor.getString(2);
				String data = cursor.getString(3);
				int id = cursor.getInt(4);
				int isMusicColumn = cursor.getInt(5);
				long songDuration = cursor.getInt(6);
				if (isMusicColumn != 0 && songDuration > 0) {
					Song song = new Song(title, album, artist, data, id);
					song.toString();
					songList.add(song);
				}
			}
		}
		return songList;
	}
}
