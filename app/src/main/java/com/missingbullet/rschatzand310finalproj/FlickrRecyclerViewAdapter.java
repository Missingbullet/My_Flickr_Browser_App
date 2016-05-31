package com.missingbullet.rschatzand310finalproj;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by robschatz on 5/30/16.
 */
public class FlickrRecyclerViewAdapter extends RecyclerView.Adapter<FlickrImageViewHolder> {
    private List<Photo> mPhotosList;
    private Context mContext;
    private final String LOG_TAG = FlickrRecyclerViewAdapter.class.getSimpleName();

    public FlickrRecyclerViewAdapter(Context context, List<Photo> photosList) {
        mContext = context;
        this.mPhotosList = photosList;
    }

    @Override
    public FlickrImageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        //This will inflate the layout
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.browse, null);

        FlickrImageViewHolder flickrImageViewHolder = new FlickrImageViewHolder(view);
        return flickrImageViewHolder;

    }

    //Anytime the UI needs to be updated this method below is called automatically
    @Override
    public void onBindViewHolder(FlickrImageViewHolder flickrImageViewHolder, int i) {
        //this tells us the index position of the photo or element in the view
        Photo photoItem = mPhotosList.get(i);

        Log.d(LOG_TAG, "Processing: " + photoItem.getmTitle() + " ---> " + Integer.toString(i));

        //Picasso is being used here to load the images
        Picasso.with(mContext).load(photoItem.getmImage())
                .error(R.drawable.placeholder)//a custom error image can be shown if need be
                .placeholder(R.drawable.placeholder)
                .into(flickrImageViewHolder.thumbnail);
        flickrImageViewHolder.title.setText(photoItem.getmTitle());
    }

    @Override
    //This is a null check to make sure we handle any situation where we get back a null gracefully
    public int getItemCount() {
        return (null != mPhotosList ? mPhotosList.size() : 0);
    }
}
