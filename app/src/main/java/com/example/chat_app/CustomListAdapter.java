package com.example.chat_app;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomListAdapter extends ArrayAdapter<Message> {

    private static final String TAG = "CustomListAdapter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        TextView message;
        ImageView image;
        ImageView my_image;
    }

    /**
     * Default constructor for the PersonListAdapter
     * @param context
     * @param resource
     * @param objects
     */
    public CustomListAdapter(Context context, int resource, ArrayList<Message> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //get the Card information
        String title = getItem(position).text;
        String imgUrl = getItem(position).photoURL;
        String uid = getItem(position).uid;

        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        ViewHolder holder;


        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.message = (TextView) convertView.findViewById(R.id.message);
            holder.image = (ImageView) convertView.findViewById(R.id.sender_photo);
            holder.my_image = (ImageView) convertView.findViewById(R.id.my_photo);

            result = convertView;

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }


        //Animation animation = AnimationUtils.loadAnimation(mContext,
        //        (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
        //result.startAnimation(animation);
        lastPosition = position;

        holder.message.setText(title);

        //This block gives a null pointer exception when user is not signed in
  /*     if (uid.compareTo(MainActivity.getUID()) == 0) {
            Picasso.get().load(imgUrl).into(holder.my_image);
            holder.message.setGravity(Gravity.RIGHT);
            holder.my_image.setVisibility(View.VISIBLE);
            holder.image.setVisibility(View.INVISIBLE);
        } else {
            Picasso.get().load(imgUrl).into(holder.image);
            holder.message.setGravity(Gravity.LEFT);
            holder.image.setVisibility(View.VISIBLE);
            holder.my_image.setVisibility(View.INVISIBLE);
        } */


        return convertView;
    }

    /**
     * Required for setting up the Universal Image loader Library
     */


}
