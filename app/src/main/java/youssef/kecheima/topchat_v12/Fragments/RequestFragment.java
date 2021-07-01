package youssef.kecheima.topchat_v12.Fragments;


import android.content.Context;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import youssef.kecheima.topchat_v12.Adapters.RequestAdapter;
import youssef.kecheima.topchat_v12.Model.Request;
import youssef.kecheima.topchat_v12.Model.User;
import youssef.kecheima.topchat_v12.R;


public class RequestFragment extends Fragment {



    public RequestFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerRequest;
    private RequestAdapter requestAdapter;
    private List<User> userList;
    private   List<Request> requestList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private LinearLayout  request,connectionRequest;
    private ProgressBar progressBar;
    private ImageButton refreshRequest;
    private Context context = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_request, container, false);
        recyclerRequest=view.findViewById(R.id.RequestRecycler);
        request=view.findViewById(R.id.Request);
        connectionRequest=view.findViewById(R.id.ConnectionRequest);
        progressBar=view.findViewById(R.id.ProgressBarRequest);
        refreshRequest=view.findViewById(R.id.RefrechRequest);


        firebaseFirestore= FirebaseFirestore.getInstance();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        recyclerRequest.setHasFixedSize(true);
        recyclerRequest.setLayoutManager(new LinearLayoutManager(getContext()));

        userList = new ArrayList<>();
        requestList = new ArrayList<>();

        refreshRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoadingContens().onPreExecute();
                new LoadingContens().doInBackground();
            }
        });

        new LoadingContens().execute();


        return view;
    }

    private  void loadRequests() {
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

    private  boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager =(ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo =connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo!=null && activeNetworkInfo.isConnected();
    }

    public class LoadingContens extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            request.setVisibility(View.GONE);
            connectionRequest.setVisibility(View.GONE);
            progressBar.animate();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(!isNetworkAvailable()){
                progressBar.setVisibility(View.GONE);
                connectionRequest.setVisibility(View.VISIBLE);
                request.setVisibility(View.GONE);

            }else {
                Query query = FirebaseDatabase.getInstance().getReference("Friendsrequest").child(firebaseUser.getUid()).orderByChild("request_type").equalTo("received");
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        requestList.clear();
                        if (snapshot.hasChildren()) {
                            progressBar.setVisibility(View.GONE);
                            connectionRequest.setVisibility(View.GONE);
                            request.setVisibility(View.GONE);
                            for (DataSnapshot data : snapshot.getChildren()) {
                                Request request = data.getValue(Request.class);
                                requestList.add(request);
                            }
                            loadRequests();
                        }
                        else{
                            progressBar.setVisibility(View.GONE);
                            connectionRequest.setVisibility(View.GONE);
                            request.setVisibility(View.VISIBLE);

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