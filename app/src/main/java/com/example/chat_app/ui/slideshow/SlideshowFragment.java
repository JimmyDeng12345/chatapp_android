package com.example.chat_app.ui.slideshow;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.chat_app.Camera;
import com.example.chat_app.Profile;
import com.example.chat_app.R;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Intent openProfile = new Intent(getActivity(), Profile.class);
        getActivity().startActivity(openProfile);

        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        return root;
    }
}