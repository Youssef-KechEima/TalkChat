package youssef.kecheima.topchat_v12.Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_users, container, false);
        recyclerViewFriends=view.findViewById(R.id.RecyclerFriends);
        addFriends=view.findViewById(R.id.AddFriends);

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

        databaseReference=FirebaseDatabase.getInstance().getReference("Friends").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendsList.clear();
                for(DataSnapshot data:snapshot.getChildren()){
                    Friends friends = data.getValue(Friends.class);
                    friendsList.add(friends);
                }
                loadUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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


}