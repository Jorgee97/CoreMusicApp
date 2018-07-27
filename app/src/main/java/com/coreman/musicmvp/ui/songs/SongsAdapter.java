package com.coreman.musicmvp.ui.songs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.coreman.musicmvp.R;
import com.coreman.musicmvp.models.Song;
import com.coreman.musicmvp.utils.ActionUtils;
import com.coreman.musicmvp.utils.OnSongClick;

import java.util.List;
import java.util.logging.Handler;


public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {
    private static final String TAG = "SongsAdapter";
    
    private Context mContext;
    private List<Song> mSongList;
    private final OnSongClick mOnSongClickListener;

    public SongsAdapter(Context context, List<Song> songList, OnSongClick onSongClick) {
        mContext = context;
        mSongList = songList;
        mOnSongClickListener = onSongClick;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mSong = mSongList.get(position);

        holder.mTitle.setText(holder.mSong.getTitle());
        holder.mArtist.setText(holder.mSong.getArtist());

        Log.i(TAG, "onBindViewHolder: empty URI " + ActionUtils.getAlbumArtUri(holder.mSong.getAlbumId()).toString());
        
        Glide.with(mContext)
                .load(ActionUtils.getAlbumArtUri(holder.mSong.getAlbumId()).toString())
                .apply(new RequestOptions().centerCrop().override(64, 64).placeholder(R.drawable.default_album_image_x64))
                .into(holder.mAlbumPhoto);
    }

    @Override
    public int getItemCount() {
        return mSongList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.song_item, parent, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mAlbumPhoto;
        TextView mTitle;
        TextView mArtist;
        Song mSong;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mAlbumPhoto = itemView.findViewById(R.id.album_image);
            mTitle = itemView.findViewById(R.id.txt_title_song);
            mArtist = itemView.findViewById(R.id.txt_artist_song);
        }

        @Override
        public void onClick(View view) {
            mOnSongClickListener.onSongClicked(getAdapterPosition());
        }
    }

}
