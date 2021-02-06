package com.example.chat_app.ui.dashboard;

import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.chat_app.Camera;
import com.example.chat_app.R;
import com.example.chat_app.SignIn;
import com.example.chat_app.ui.profile.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static com.example.chat_app.SignIn.ff;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    public static final String PATH = "total_users";
    public static final String DOC_NAME = "user_num";
    public static final String FIELD_NAME = "index";
    private TextView displayResult;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.chat_matching, container, false);
        ImageButton ib = root.findViewById(R.id.imagebutton);
        displayResult = root.findViewById(R.id.display_result);

        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("yeeeeeeeeennnnnner0");
                ff.collection("users").orderBy("index", Query.Direction.DESCENDING).limit(1).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                System.out.println("yeeeeeeeeennnnnner1");
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    int total = Integer.parseInt(document.getData().get(FIELD_NAME).toString());
                                    int randIndex = (int) (Math.random() * total);
                                    if (randIndex == ProfileFragment.myIndex && ProfileFragment.myIndex != -1) {
                                        randIndex = (int) (Math.random() * total);
                                    }
                                    randIndex = 11;
                                    ff.collection("users")
                                            .whereEqualTo("index", randIndex).limit(1).get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Map<String, Object> ye = document.getData();
                                                        displayResult.setText(ye.get("name").toString());
                                                        String myUid = FirebaseAuth.getInstance().getUid();
                                                        Map<String, Object> updated = (Map<String, Object>) ye.get("strangers");
                                                        //ye.remove("strangers");
                                                        updated.put(myUid, myUid);
                                                        ye.put("strangers", updated);
                                                        addUserToStrangers(myUid, document.getId());
                                                        ff.collection("users").document(document.getId()).set(ye);
                                                    }
                                                }
                                            });
                                }
                            }
                        });

            }
        });
        //ib.setImageBitmap(Camera.getCircularImage(BitmapFactory.decodeResource(root.getResources(), R.drawable.circle_button)));
        RelativeLayout relativeLayout = getActivity().findViewById(R.id.messages);
        relativeLayout.setVisibility(View.INVISIBLE);
        return root;
    }
    //0yDPbfsTHqh4QpuNq1vpii2Qr3H2
    public static void addUserToStrangers (String addTo, String from) {

        ff.collection("users").document(addTo)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        Map<String, Object> data = value.getData();
                        Map<String, Object> updated = (Map<String, Object>) data.get("strangers");
                        updated.put(from, from);
                        ff.collection("users").document(addTo).set(data);
                    }
                });
    }

}