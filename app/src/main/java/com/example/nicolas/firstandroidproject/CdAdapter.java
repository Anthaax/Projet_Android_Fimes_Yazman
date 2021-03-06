package com.example.nicolas.firstandroidproject;


import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;

public class CdAdapter extends RecyclerView.Adapter<CdAdapter.CdHolder>{

    private AlbumInfo[] _albumInfos;
    private AppCompatActivity callingActivity;
    private OnItemClickListener itemClickListener;

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.itemClickListener = mItemClickListener;
    }



    @Override
    public CdHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cd_list, null, false);

        return new CdHolder(v);
    }

    @Override
    public void onBindViewHolder(CdHolder holder, int position) {
        AlbumInfo current = _albumInfos[position];
        holder._year.setText(current.year);
        holder._genre.setText(current.genre[0]);
        holder._artistName.setText(current.artist);
        holder._albumName.setText(current.title);
        Glide.with(callingActivity).load(current.thumb).into(holder._albumIMG);
    }

    @Override
    public int getItemCount() {
        return _albumInfos.length;
    }

    public class CdHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        ImageView _albumIMG;
        TextView _artistName;
        TextView _albumName;
        TextView _genre;
        TextView _year;
        View _view;
        public CdHolder(View itemView){
            super(itemView);
            _albumIMG = itemView.findViewById(R.id.cd_image);
            _artistName = itemView.findViewById(R.id.artist_name);
            _albumName = itemView.findViewById(R.id.album_name);
            _genre = itemView.findViewById(R.id.genre_name);
            _year = itemView.findViewById(R.id.year);
            _view = itemView;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            itemClickListener.onItemClick(v, getAdapterPosition(), _albumName.getText().toString());

        }
    }

    public CdAdapter(AlbumInfo[] albumInfos, AppCompatActivity callActivity){
        super();
        _albumInfos = albumInfos;
        callingActivity = callActivity;
    }
}


