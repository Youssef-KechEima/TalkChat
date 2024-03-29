package youssef.kecheima.topchat_v12.Search;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import youssef.kecheima.topchat_v12.Adapters.UserAdapter;
import youssef.kecheima.topchat_v12.Model.User;
import youssef.kecheima.topchat_v12.R;
import youssef.kecheima.topchat_v12.Settings.FriendProfileActivity;

public class SearchFriendsActivity extends AppCompatActivity {
    private RecyclerView recyclerViewFriends;
    private UserAdapter userAdapter;
    private List<User> userList;
    private FirebaseFirestore firebaseFirestore;
    private Toolbar toolbar;
    private ImageButton back,refreshSearchFriend;
    private EditText search;
    private ProgressBar progressBar;
    private LinearLayout connectionSearchFriend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);
        statusBar_and_actionBar_Tool();
        setSupportActionBar(toolbar);
        components();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        firebaseFirestore= FirebaseFirestore.getInstance();
        recyclerViewFriends.setHasFixedSize(true);
        recyclerViewFriends.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(search.isEnabled()) {
                    filter(s.toString());
                }
                else {
                    Toast.makeText(SearchFriendsActivity.this, "You can't Search without internet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        new LoadingContens().execute();

        refreshSearchFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoadingContens().onPreExecute();
                new LoadingContens().doInBackground();
            }
        });

    }

    private class LoadingContens extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            recyclerViewFriends.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            connectionSearchFriend.setVisibility(View.GONE);
            progressBar.animate();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(!isNetworkAvailable()){
                recyclerViewFriends.setVisibility(View.INVISIBLE);
                connectionSearchFriend.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                search.setEnabled(false);
            }
            else {
                recyclerViewFriends.setVisibility(View.VISIBLE);
                connectionSearchFriend.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                search.setEnabled(true);
                readUserData();
            }
            return null;
        }
    }

    private  boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager =(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo =connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo!=null && activeNetworkInfo.isConnected();
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
                recyclerViewFriends.setAdapter(userAdapter);
            }
        });
    }

    private void readUserData() {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore.collection("Users").limit(5).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                userList.clear();
                for(QueryDocumentSnapshot data :queryDocumentSnapshots){
                    User user=data.toObject(User.class);
                    assert user!=null;
                    assert firebaseUser!=null;
                    if(!user.getId().equals(firebaseUser.getUid())){
                        userList.add(user);
                    }
                }
                userAdapter = new UserAdapter(userList,getApplicationContext());
                recyclerViewFriends.setAdapter(userAdapter);
            }
        });
    }

    private void components(){
        recyclerViewFriends=findViewById(R.id.NewFriends);
        toolbar=findViewById(R.id.BarSearch);
        back=findViewById(R.id.back);
        search=findViewById(R.id.Search);
        progressBar=findViewById(R.id.ProgressBarSearchFriend);
        connectionSearchFriend=findViewById(R.id.ConnectionSearchFriend);
        refreshSearchFriend=findViewById(R.id.RefrechSearchFriend);
    }

    @SuppressLint("NewApi")
    private void statusBar_and_actionBar_Tool() {
        Window window = SearchFriendsActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(SearchFriendsActivity.this, R.color.purple_700));

    }
}