package com.gokulsundar4545.connectwithpeople.Adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.gokulsundar4545.connectwithpeople.Model.VedioMode;
import com.gokulsundar4545.connectwithpeople.R;

import java.util.List;

public class VedioAdapter extends FirebaseRecyclerAdapter<VedioMode,VedioAdapter.VedioViewHolder> {



    public VedioAdapter(@NonNull FirebaseRecyclerOptions<VedioMode> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull VedioViewHolder holder, int position, @NonNull VedioMode model) {

        holder.setVedioData(model);
        holder.vedioname.setText(model.getVedioDescription());


    }

    @NonNull
    @Override
    public VedioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.vedioview,parent,false);
        return new VedioViewHolder(view);
    }

    public class VedioViewHolder extends RecyclerView.ViewHolder{

        VideoView videoView;
        TextView vedioname;
        com.github.ybq.android.spinkit.SpinKitView progressBar;

        public VedioViewHolder(@NonNull View itemView) {
            super(itemView);

            videoView=itemView.findViewById(R.id.videoView);
            vedioname=itemView.findViewById(R.id.vedioname);
            progressBar=itemView.findViewById(R.id.progressbar);

        }

        public void setVedioData(VedioMode vedioMode){
            vedioname.setText(vedioMode.getVedioname());
            videoView.setVideoPath(vedioMode.getVedioUrl());


            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    progressBar.setVisibility(View.GONE);
                    mediaPlayer.start();
                }
            });


            

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    progressBar.setVisibility(View.VISIBLE);
                    mediaPlayer.stop();
                }
            });

        }
    }




}
