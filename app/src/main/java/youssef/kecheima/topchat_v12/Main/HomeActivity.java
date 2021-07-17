package youssef.kecheima.topchat_v12.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.HashMap;
import youssef.kecheima.topchat_v12.Adapters.FragmentsAdapter;
import youssef.kecheima.topchat_v12.Auth.LoginActivity;
import youssef.kecheima.topchat_v12.Fragments.ChatFragment;
import youssef.kecheima.topchat_v12.Model.Chat;
import youssef.kecheima.topchat_v12.Model.ChatList;
import youssef.kecheima.topchat_v12.R;
import youssef.kecheima.topchat_v12.Settings.SettingsActivity;

public class HomeActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private DatabaseReference statusRef;
    private FirebaseUser firebaseUser;
    private static BadgeDrawable badgeDrawable;
    private BadgeDrawable badgeDrawable2;


    //Main Methode
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        components();
        statusBar_and_actionBar_Tool();

        statusRef= FirebaseDatabase.getInstance().getReference("UserStatus");
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        FirebaseMessaging.getInstance().subscribeToTopic(firebaseUser.getUid());


        viewPager2.setAdapter(new FragmentsAdapter(this));
         TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
             @Override
             public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                 switch (position){
                     case 0:
                         tab.setIcon(R.drawable.chat);
                         tab.getIcon().setColorFilter(getResources().getColor(R.color.purple_500), PorterDuff.Mode.SRC_IN);
                         badgeDrawable=tab.getOrCreateBadge();
                         badgeDrawable.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.red));
                         badgeDrawable.setVisible(false);
                         break;
                     case 1:
                         tab.setIcon(R.drawable.people);
                         break;
                     case 2:
                         tab.setIcon(R.drawable.request);
                         badgeDrawable2=tab.getOrCreateBadge();
                         badgeDrawable2.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.red));
                         badgeDrawable2.setVisible(false);
                         break;
                 }
             }
         });

      tabLayoutMediator.attach();

        Query query= FirebaseDatabase.getInstance().getReference("Friendsrequest").child(firebaseUser.getUid()).orderByChild("request_type").equalTo("received");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int requests=(int)snapshot.getChildrenCount();
                if(requests==0){
                    badgeDrawable2.setVisible(false);
                }
                else {
                    badgeDrawable2.setVisible(true);
                    badgeDrawable2.setNumber(requests);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

         Query query1 =FirebaseDatabase.getInstance().getReference("Chats");
        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int unread=1;
                for(DataSnapshot data:snapshot.getChildren()) {
                    Log.d("inbox", "Data : " + data);
                    Chat chat=data.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isIs_seen()){
                        badgeDrawable.setNumber(unread++);
                        badgeDrawable.setVisible(true);
                    }
                    else{
                        badgeDrawable.setNumber(0);
                        badgeDrawable.setVisible(false);
                    }
                }
                if(unread==0){
                    badgeDrawable.setVisible(false);
                    badgeDrawable.setNumber(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


      tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
          @Override
          public void onTabSelected(TabLayout.Tab tab) {
              int tabIconSelected=ContextCompat.getColor(getApplicationContext(),R.color.purple_500);
              tab.getIcon().setColorFilter(tabIconSelected, PorterDuff.Mode.SRC_IN);
          }

          @Override
          public void onTabUnselected(TabLayout.Tab tab) {
              int tabIconSelected=ContextCompat.getColor(getApplicationContext(),R.color.black);
              tab.getIcon().setColorFilter(tabIconSelected, PorterDuff.Mode.SRC_IN);

          }

          @Override
          public void onTabReselected(TabLayout.Tab tab) {

          }
      });

    }

    public static BadgeDrawable getBadge(){
        return badgeDrawable;
    }

    private void checkOnlineStatus(String status){
        HashMap<String,Object> hashMap =new HashMap<>();
        hashMap.put("status",status);
        statusRef.child(firebaseUser.getUid()).updateChildren(hashMap);

    }

    @Override
    protected void onStart() {
        checkOnlineStatus("online");
        super.onStart();
    }

    @Override
    protected void onPause() {
        String timestamp= String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timestamp);
        super.onPause();
    }

    @Override
    protected void onResume() {
        checkOnlineStatus("online");
        super.onResume();
    }

    @Override
    protected void onStop() {
        String timestamp= String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timestamp);
        super.onStop();
    }

    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.Logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
                return true;
            case R.id.Settings:
                startActivity(new Intent(HomeActivity.this,SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    //Cast Components
    private void components() {
        toolbar=findViewById(R.id.Toolbar);
        tabLayout=findViewById(R.id.tabLayout);
        viewPager2=findViewById(R.id.ViewPager2);
    }
    //StatusBar and ActionBar
    @SuppressLint("NewApi")
    private void statusBar_and_actionBar_Tool() {
        Window window=HomeActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(HomeActivity.this,R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + getString(R.string.app_name)+ "</font>"));
    }
}