package com.example.musicplayer;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Song implements Parcelable{
	
	private static final String TAG = Song.class.getName();
	
	Song(String title,String album,String artist , String data, int id){
		this.title = title;
		this.album = album;
		this.artist = artist;
		this.data = data;
		this.id = id;
	}
	
	Song(Parcel song){
		this.title = song.readString();
		this.album = song.readString();
		this.artist= song.readString();
		this.data = song.readString();
		this.id = song.readInt();
	}
	
	private String title;
	private String album;
	private String data;
	private int id;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	private String artist;
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		if (artist.equals("<unknown>")) {
			this.artist = artist;
		} else {
			this.artist = "";
		}
	}
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		if(album.equals("<unknown>")){
			this.album = album;
		}
		else{
			this.album="";
		}
		
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		String song = "Song is: " + " Title: " + title + " Artist: "+ artist + " Album: "+ album+
				" Data: " + data + " id: " + id;
		 Log.d(TAG, song);
		 return song;
	}
	
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeString(album);
		dest.writeString(artist);
		dest.writeString(data);
		dest.writeInt(id);
		
	}
	
	public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>() {

		@Override
		public Song createFromParcel(Parcel source) {
			Song song = new Song(source);
			return song;
		}

		@Override
		public Song[] newArray(int size) {
			return new Song[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}
}
