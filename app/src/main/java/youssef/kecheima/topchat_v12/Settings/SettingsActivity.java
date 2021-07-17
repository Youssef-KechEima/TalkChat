package youssef.kecheima.topchat_v12.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import de.hdodenhof.circleimageview.CircleImageView;
import youssef.kecheima.topchat_v12.R;


public class SettingsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView userName,txt_Desc;
    private CircleImageView userImage;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private LinearLayout userProfile,userAccount,help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        components();
        statusBar_and_actionBar_Tool();
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        firebaseFirestore=FirebaseFirestore.getInstance();

        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this,UserProfileActivity.class));
            }
        });

        userAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this,AccountActivity.class));
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this,HelpActivity.class));
            }
        });

            getData();
    }


    private void getData(){
        firebaseFirestore.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String user_name=documentSnapshot.get("user_name").toString();
                String Image=documentSnapshot.get("imageUrl").toString();
                String desc=documentSnapshot.get("desc").toString();
                userName.setText(user_name);
                txt_Desc.setText(desc);
                if(Image.equals("default")){
                    userImage.setImageResource(R.drawable.empty_user);
                }
                else{
                    Glide.with(SettingsActivity.this).load(Image).into(userImage);
                }
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getData();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void components() {
        toolbar=findViewById(R.id.ToolbarSettings);
        userName=findViewById(R.id.UserName);
        userImage=findViewById(R.id.UserImage);
        userProfile=findViewById(R.id.UserProfile);
        userAccount=findViewById(R.id.UserAccount);
        txt_Desc=findViewById(R.id.Desc);
        help=findViewById(R.id.Help);
    }
    @SuppressLint("NewApi")
    private void statusBar_and_actionBar_Tool() {
        Window window= SettingsActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(SettingsActivity.this,R.color.purple_700));
    }
}