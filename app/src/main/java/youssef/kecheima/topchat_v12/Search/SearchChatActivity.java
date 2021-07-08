package youssef.kecheima.topchat_v12.Search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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

public class SearchChatActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageButton back,refreshSearchChats;
    private RecyclerView recyclerViewChats;
    private List<User> userList;
    private List<Friends> friendsList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private UserAdapter userAdapter;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;
    private LinearLayout connectionSearchChat,searchChats;
    private EditText searchTypeChat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_chat);
        statusBar_and_actionBar_Tool();
        setSupportActionBar(toolbar);
        components();

        firebaseFirestore= FirebaseFirestore.getInstance();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        recyclerViewChats.setHasFixedSize(true);
        recyclerViewChats.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        friendsList = new ArrayList<>();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchTypeChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(searchTypeChat.isEnabled()) {
                    filter(s.toString());
                }
                else {
                    Toast.makeText(SearchChatActivity.this, "You can't Search without internet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void filter(String s) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore.collection("Users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                userList.clear();
                for(QueryDocumentSnapshot data:queryDocumentSnapshots){
                    User user=data.toObject(User.class);
                    if(!user.getId().equals(firebaseUser.getUid())){
                        if(user.getUser_name().contains(s)){
                            userList.add(user);
                        }
                    }
                }
                userAdapter=new UserAdapter(userList,getApplicationContext());
                recyclerViewChats.setAdapter(userAdapter);
            }
        });
    }


    private void components() {
        toolbar=findViewById(R.id.BarSearchChat);
        back=findViewById(R.id.backChats);
        recyclerViewChats=findViewById(R.id.NewChats);
        refreshSearchChats=findViewById(R.id.RefrechSearchChat);
        connectionSearchChat=findViewById(R.id.ConnectionSearchChat);
        searchChats=findViewById(R.id.SearchChat);
        progressBar=findViewById(R.id.ProgressBarSearchChat);
        searchTypeChat=findViewById(R.id.SearchTypeChats);
    }

    private void loadUsers() {
        userList=new ArrayList<>();
        firebaseFirestore.collection("Users").limit(5).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                    userAdapter= new UserAdapter(userList,getApplicationContext());
                    recyclerViewChats.setAdapter(userAdapter);
                }
            }
        });
    }

    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager =(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo =connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo!=null && activeNetworkInfo.isConnected();
    }

    @SuppressLint("NewApi")
    private void statusBar_and_actionBar_Tool() {
        Window window = SearchChatActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(SearchChatActivity.this, R.color.purple_700));

    }

    private class LoadingContens extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            searchChats.setVisibility(View.GONE);
            connectionSearchChat.setVisibility(View.GONE);
            recyclerViewChats.setVisibility(View.INVISIBLE);
            progressBar.animate();

        }
        @Override
        protected Void doInBackground(Void... voids) {
            if(!isNetworkAvailable()){
                progressBar.setVisibility(View.GONE);
                connectionSearchChat.setVisibility(View.VISIBLE);
                searchChats.setVisibility(View.GONE);
                recyclerViewChats.setVisibility(View.INVISIBLE);
                searchTypeChat.setEnabled(false);
            }
            else {
                databaseReference = FirebaseDatabase.getInstance().getReference("Friends").child(firebaseUser.getUid());
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        friendsList.clear();
                        if (snapshot.exists()) {
                            progressBar.setVisibility(View.GONE);
                            searchChats.setVisibility(View.GONE);
                            connectionSearchChat.setVisibility(View.GONE);
                            recyclerViewChats.setVisibility(View.VISIBLE);
                            searchTypeChat.setEnabled(true);
                            for (DataSnapshot data : snapshot.getChildren()) {
                                Friends friends = data.getValue(Friends.class);
                                friendsList.add(friends);
                            }
                            loadUsers();
                        }
                        else {
                            progressBar.setVisibility(View.GONE);
                            searchChats.setVisibility(View.VISIBLE);
                            connectionSearchChat.setVisibility(View.GONE);
                            recyclerViewChats.setVisibility(View.INVISIBLE);
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