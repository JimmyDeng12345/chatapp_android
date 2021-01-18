package com.example.chat_app.ui.profile;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.chat_app.Camera;
import com.example.chat_app.Profile;
import com.example.chat_app.R;

public class ProfileFragment extends Fragment {

    private ProfileViewModel notificationsViewModel;
    private ImageView profilePicture;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.activity_profile, container, false);
        RelativeLayout relativeLayout = getActivity().findViewById(R.id.messages);
        relativeLayout.setVisibility(View.INVISIBLE);
        profilePicture = root.findViewById(R.id.profilePicture);
        profilePicture.setImageBitmap(Camera.getCircularImage(BitmapFactory.decodeResource(this.getResources(), R.drawable.obama2)));
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), Camera.class), 2);
            }
        });
        return root;
    }
}