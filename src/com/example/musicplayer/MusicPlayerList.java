package com.example.musicplayer;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class MusicPlayerList extends RecyclerView.Adapter<ViewHolder> {
	
	private Context context;
	private int layoutResourceId;
	private List<Song> musicPlayerList;
	private OnClickListener onClickListener;
	
	
	MusicPlayerList(Activity context,int layoutResourceId,List<Song> songList){
	this.context = context;
	this.layoutResourceId = layoutResourceId;
	this.musicPlayerList = songList;
	}
	
	
	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return musicPlayerList.size();
	}

	
	public void onBindViewHolder(ViewHolder arg0, int position) {
		((DataHolder)arg0).getTitle().setText(musicPlayerList.get(position).getTitle());
		((DataHolder)arg0).getAlbum().setText(musicPlayerList.get(position).getAlbum());
		((DataHolder)arg0).getArtist().setText(musicPlayerList.get(position).getArtist());
	}

	@Override
	public DataHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
		View view = LayoutInflater.from(context).inflate(layoutResourceId,arg0,false);
		DataHolder dataholder = new DataHolder(view);
		view.setOnClickListener(onClickListener);
		return dataholder;
	}
	
	public void setOnClickListener(OnClickListener clickListener){
		onClickListener = clickListener;
	}
	
	
	private class DataHolder extends ViewHolder{
		
		TextView title;
		public TextView getTitle() {
			return title;
		}

		public TextView getAlbum() {
			return album;
		}

		public TextView getArtist() {
			return artist;
		}

		TextView album;
		TextView artist;

		public DataHolder(View itemView) {
			super(itemView);
			title = (TextView)itemView.findViewById(R.id.title_text);
			album = (TextView)itemView.findViewById(R.id.album_text);
			artist = (TextView)itemView.findViewById(R.id.artist_text);
			
		}		
	}

}
