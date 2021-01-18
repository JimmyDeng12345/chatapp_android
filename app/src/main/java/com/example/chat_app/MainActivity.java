package com.example.chat_app;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static boolean isDark = false;
    private AppBarConfiguration mAppBarConfiguration;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        RelativeLayout rl = findViewById(R.id.messages);
        rl.setVisibility(View.VISIBLE);

        FirebaseUser me = FirebaseAuth.getInstance().getCurrentUser();
        if (me == null) {
            startActivity(new Intent(this, SignIn.class));
        } else {
            startActivity(new Intent(this, MainPage.class));
        }

    }
    public static String getUID() {
        String s = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (s == null) {
            return "";
        } else {
            return s;
        }
    }

    public static void makeNavBar(AppCompatActivity a) {
        BottomNavigationView navView = a.findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(a, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(a, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }


    public static boolean getIsDark() {
        return isDark;
    }



    public static class CustomChatListAdapter extends ArrayAdapter<Message> {

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
        public CustomChatListAdapter(Context context, int resource, ArrayList<Message> objects) {
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

            if (uid == null) {
                return convertView;
            } else if (uid.compareTo(getUID()) == 0) {
                Picasso.get().load(imgUrl).into(holder.my_image);
                holder.message.setGravity(Gravity.RIGHT);
                holder.my_image.setVisibility(View.VISIBLE);
                holder.image.setVisibility(View.INVISIBLE);
            } else {
                Picasso.get().load(imgUrl).into(holder.image);
                holder.message.setGravity(Gravity.LEFT);
                holder.image.setVisibility(View.VISIBLE);
                holder.my_image.setVisibility(View.INVISIBLE);
            }


            return convertView;
        }

        /**
         * Required for setting up the Universal Image loader Library
         */


    }
}