package com.piyush.newu.kiitcab;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapDrawableResource;
import com.bumptech.glide.load.resource.drawable.DrawableResource;
import com.firebase.geofire.GeoFire;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String TAG="RecyclerAdapter";

    private ArrayList<String> mText=new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(Context context, ArrayList<String> mText) {
        this.mContext=context;
        this.mText = mText;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d(TAG,"onCreateViewHolder:called.");

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_list,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        Log.d(TAG,"onBindViewHolder: called.");



        if(i==0) {
            Glide.with(mContext).load(R.drawable.omni).into(viewHolder.image1);
        }
        else if(i==1){
            Glide.with(mContext).load(R.drawable.ambulance).into(viewHolder.image1);
        }
        else {
            Glide.with(mContext).load(R.drawable.bus).into(viewHolder.image1);
        }
        viewHolder.textView1.setText(mText.get(i));
    }

    @Override
    public int getItemCount() {
        return mText.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image1;
        TextView textView1;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image1=itemView.findViewById(R.id.recyclerimage);
            textView1=itemView.findViewById(R.id.recyclertext);
        }
    }
}