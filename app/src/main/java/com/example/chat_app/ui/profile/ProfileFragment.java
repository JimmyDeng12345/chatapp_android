package com.example.chat_app.ui.profile;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.chat_app.Camera;
import com.example.chat_app.Profile;
import com.example.chat_app.R;
import com.example.chat_app.SignIn;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Map;

public class ProfileFragment extends Fragment {

    private ProfileViewModel notificationsViewModel;
    public static int myIndex = -1;
    public static String myUID = "";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.activity_profile, container, false);
        RelativeLayout relativeLayout = getActivity().findViewById(R.id.messages);
        relativeLayout.setVisibility(View.INVISIBLE);
        TextView displayName = root.findViewById(R.id.tv_name);
        ImageView pp = root.findViewById(R.id.profilePicture);
        TextView email = root.findViewById(R.id.my_email);

        FirebaseUser fu = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference df = SignIn.ff.collection("users").document(fu.getUid());
        df.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    Map<String, Object> data = value.getData();
                    displayName.setText(data.get("name").toString());
                    email.setText(data.get("email").toString());
                    myIndex = Integer.parseInt(data.get("index").toString());

                }
            }
        });

        Profile curr = new Profile();
        curr.setUpProfile(pp, root);
        pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(root.getContext(), Camera.class), 1);
            }
        });

        return root;
    }


}