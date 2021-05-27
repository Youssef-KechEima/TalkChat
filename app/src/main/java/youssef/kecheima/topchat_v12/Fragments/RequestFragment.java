package youssef.kecheima.topchat_v12.Fragments;

import android.content.SharedPreferences;
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

import youssef.kecheima.topchat_v12.Adapters.RequestAdapter;
import youssef.kecheima.topchat_v12.Adapters.UserAdapter;
import youssef.kecheima.topchat_v12.Model.Friends;
import youssef.kecheima.topchat_v12.Model.Request;
import youssef.kecheima.topchat_v12.Model.User;
import youssef.kecheima.topchat_v12.R;

import static android.content.Context.MODE_PRIVATE;


public class RequestFragment extends Fragment {



    public RequestFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerRequest;
    private RequestAdapter requestAdapter;
    private List<User> userList;
    private List<Request> requestList;
    private FirebaseFirestore firebaseFirestore;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_request, container, false);
        recyclerRequest=view.findViewById(R.id.RequestRecycler);

        Bundle bundle=this.getArguments();
        String otherUserId=bundle.getString("otherUserId");

        firebaseFirestore= FirebaseFirestore.getInstance();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        recyclerRequest.setHasFixedSize(true);
        recyclerRequest.setLayoutManager(new LinearLayoutManager(getContext()));

        userList = new ArrayList<>();
        requestList = new ArrayList<>();


        databaseReference= FirebaseDatabase.getInstance().getReference("Friendsrequest").child(firebaseUser.getUid()).child(otherUserId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestList.clear();
                for(DataSnapshot data:snapshot.getChildren()){
                    Request request = data.getValue(Request.class);
                    requestList.add(request);
                }
                loadRequests();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    private void loadRequests() {
        userList=new ArrayList<>();
        firebaseFirestore.collection("Users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot data:queryDocumentSnapshots){
                    User user = data.toObject(User.class);
                    for (Request request:requestList){
                        if(user.getId()!=null && user.getId().equals(request.getRequest_id())){
                            userList.add(user);
                            break;
                        }
                    }
                    requestAdapter= new RequestAdapter(getContext(),userList);
                    recyclerRequest.setAdapter(requestAdapter);
                }
            }
        });
    }
}