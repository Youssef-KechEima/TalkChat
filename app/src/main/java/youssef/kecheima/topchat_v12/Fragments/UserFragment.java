package youssef.kecheima.topchat_v12.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import youssef.kecheima.topchat_v12.Adapters.UserAdapter;
import youssef.kecheima.topchat_v12.Model.Friends;
import youssef.kecheima.topchat_v12.Model.User;
import youssef.kecheima.topchat_v12.R;
import youssef.kecheima.topchat_v12.Search.SearchFriendsActivity;

public class UserFragment extends Fragment {


    private RecyclerView recyclerViewFriends;
    private UserAdapter userAdapter;
    private List<User> userList;
    private List<Friends> friendsList;
    private FirebaseFirestore firebaseFirestore;
    private FloatingActionButton addFriends;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private LinearLayout friends,connectionFriends;
    private ProgressBar progressBar;
    private ImageButton refreshFriends;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_users, container, false);
        recyclerViewFriends=view.findViewById(R.id.RecyclerFriends);
        addFriends=view.findViewById(R.id.AddFriends);
        friends=view.findViewById(R.id.Friends);
        connectionFriends=view.findViewById(R.id.ConnectionFriends);
        progressBar=view.findViewById(R.id.ProgressBarFriends);
        refreshFriends=view.findViewById(R.id.RefrechFriends);

        addFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SearchFriendsActivity.class));
            }
        });
        firebaseFirestore= FirebaseFirestore.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        recyclerViewFriends.setHasFixedSize(true);
        recyclerViewFriends.setLayoutManager(new LinearLayoutManager(getContext()));

        userList = new ArrayList<>();
        friendsList = new ArrayList<>();

        refreshFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoadingContens().onPreExecute();
                new LoadingContens().doInBackground();
            }
        });

        new LoadingContens().execute();

        return view;
    }

    private void loadUsers() {
        userList=new ArrayList<>();
        firebaseFirestore.collection("Users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot data:queryDocumentSnapshots){
                    User user = data.toObject(User.class);
                    for (Friends friends:friendsList){
                        if(user.getId()!=null && user.getId().equals(friends.getFriend_id())){
                            userList.add(user);
                            break;
                        }
                    }
                    userAdapter= new UserAdapter(userList,getContext());
                    recyclerViewFriends.setAdapter(userAdapter);
                }
            }
        });
    }

    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager =(ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo =connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo!=null && activeNetworkInfo.isConnected();
    }

    private class LoadingContens extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            friends.setVisibility(View.GONE);
            connectionFriends.setVisibility(View.GONE);
            recyclerViewFriends.setVisibility(View.INVISIBLE);
            progressBar.animate();

        }
        @Override
        protected Void doInBackground(Void... voids) {
            if(!isNetworkAvailable()){
                progressBar.setVisibility(View.GONE);
                connectionFriends.setVisibility(View.VISIBLE);
                friends.setVisibility(View.GONE);
                recyclerViewFriends.setVisibility(View.INVISIBLE);
            }
            else {
                databaseReference = FirebaseDatabase.getInstance().getReference("Friends").child(firebaseUser.getUid());
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        friendsList.clear();
                        if (snapshot.exists()) {
                            progressBar.setVisibility(View.GONE);
                            friends.setVisibility(View.GONE);
                            connectionFriends.setVisibility(View.GONE);
                            recyclerViewFriends.setVisibility(View.VISIBLE);
                            for (DataSnapshot data : snapshot.getChildren()) {
                                Friends friends = data.getValue(Friends.class);
                                friendsList.add(friends);
                            }
                            loadUsers();
                        }
                        else {
                            progressBar.setVisibility(View.GONE);
                            friends.setVisibility(View.VISIBLE);
                            connectionFriends.setVisibility(View.GONE);
                            recyclerViewFriends.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            return null;
        }
    }


}