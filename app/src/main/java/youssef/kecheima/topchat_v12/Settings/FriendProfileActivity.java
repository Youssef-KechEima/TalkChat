package youssef.kecheima.topchat_v12.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import youssef.kecheima.topchat_v12.Message.MessageActivity;
import youssef.kecheima.topchat_v12.Model.Common;
import youssef.kecheima.topchat_v12.R;

public class FriendProfileActivity extends AppCompatActivity {
    private CircleImageView friendProfilePic;
    private TextView friendProfileName,friendProfileEmail,friendProfileGender,friendProfileAbout,txtSendRequest;
    private FirebaseFirestore firebaseFirestore;
    private ImageView GenderLogo,sendRequest,cancelRequst,acceptRequest,acceptedRequest;
    private FirebaseUser firebaseUser;
    private DatabaseReference friendRequestRef,friendRef;
    private Button sentFirstMessage;
    private ImageButton friendProfileback;
    private FrameLayout sendandCancalandAccept,declineRequuest;
    private String Current_state,saveCurrentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        components();
        statusBar_and_actionBar_Tool();

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        friendRequestRef=FirebaseDatabase.getInstance().getReference("Friendsrequest");
        friendRef=FirebaseDatabase.getInstance().getReference("Friends");

        Intent intent = getIntent();
        String newUserId=intent.getStringExtra("newUserId");

        friendProfileback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sentFirstMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FriendProfileActivity.this, MessageActivity.class)
                .putExtra("newUserId",newUserId));
            }
        });


        friendProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendProfilePic.invalidate();
                Drawable dr=friendProfilePic.getDrawable();
                Common.IMAGE_BITMAP=((BitmapDrawable)dr.getCurrent()).getBitmap();
                ActivityOptionsCompat activityOptionsCompat=ActivityOptionsCompat.makeSceneTransitionAnimation(FriendProfileActivity.this,friendProfilePic,"image");
                Intent intent =new Intent(FriendProfileActivity.this,DisplayPictureActivity.class);
                startActivity(intent,activityOptionsCompat.toBundle());
            }
        });

        sendandCancalandAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Current_state.equals("no_friends")){
                        SendFriendRequest(newUserId);
                    }
                    if(Current_state.equals("request_sent")){
                        CancelFriendRequest(newUserId);
                    }
                    if(Current_state.equals("request_received")){
                        AcceptFriendRequest(newUserId);
                    }
                }
            });

        declineRequuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeclineFriendRequest(newUserId);
            }
        });

        getData(newUserId);
    }

    private void DeclineFriendRequest(String newUserId) {
        friendRequestRef.child(firebaseUser.getUid()).child(newUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            friendRequestRef.child(newUserId).child(firebaseUser.getUid())
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                txtSendRequest.setText("add Friend");
                                                sendRequest.setVisibility(View.VISIBLE);
                                                cancelRequst.setVisibility(View.GONE);
                                                declineRequuest.setVisibility(View.GONE);
                                                Current_state="no_friends";
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptFriendRequest(String newUserId) {

        friendRef.child(firebaseUser.getUid()).child(newUserId).child("friend_id").setValue(newUserId)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            friendRef.child(newUserId).child(firebaseUser.getUid()).child("friend_id").setValue(firebaseUser.getUid())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                friendRequestRef.child(firebaseUser.getUid()).child(newUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    friendRequestRef.child(newUserId)
                                                                            .child(firebaseUser.getUid())
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()){

                                                                                        txtSendRequest.setText("Friends");
                                                                                        sendRequest.setVisibility(View.GONE);
                                                                                        acceptedRequest.setVisibility(View.VISIBLE);
                                                                                        acceptRequest.setVisibility(View.GONE);
                                                                                        cancelRequst.setVisibility(View.GONE);
                                                                                        sentFirstMessage.setVisibility(View.VISIBLE);
                                                                                        declineRequuest.setVisibility(View.GONE);
                                                                                        Current_state="friends";
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });

                                            }
                                        }
                                    });
                        }
                    }
                });
    }


    private void CancelFriendRequest(String userId) {
        friendRequestRef.child(firebaseUser.getUid()).child(userId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            friendRequestRef.child(userId).child(firebaseUser.getUid())
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                txtSendRequest.setText("add Friend");
                                                sendRequest.setVisibility(View.VISIBLE);
                                                cancelRequst.setVisibility(View.GONE);
                                                Current_state="no_friends";
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void SendFriendRequest(String userId) {
        HashMap hashMap1 = new HashMap();
        hashMap1.put("request_type","sent");
        hashMap1.put("request_id",userId);
        friendRequestRef.child(firebaseUser.getUid()).child(userId)
                .setValue(hashMap1)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            HashMap hashMap2 =new HashMap();
                            hashMap2.put("request_type","received");
                            hashMap2.put("request_id",firebaseUser.getUid());
                            friendRequestRef.child(userId).child(firebaseUser.getUid())
                                    .setValue(hashMap2)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                txtSendRequest.setText("Cancel Request");
                                                sendRequest.setVisibility(View.GONE);
                                                cancelRequst.setVisibility(View.VISIBLE);
                                                Current_state="request_sent";
                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    private void getData(String Userid) {
        firebaseFirestore.collection("Users").document(Userid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                friendProfileName.setText(documentSnapshot.get("user_name").toString());
                friendProfileEmail.setText(documentSnapshot.get("email").toString());
                friendProfileAbout.setText(documentSnapshot.get("desc").toString());
                if(documentSnapshot.get("sexe").equals("Male")){
                    friendProfileGender.setText(documentSnapshot.get("sexe").toString());
                    GenderLogo.setImageResource(R.drawable.maleblack);
                }
                else{
                    friendProfileGender.setText(documentSnapshot.get("sexe").toString());
                    GenderLogo.setImageResource(R.drawable.femaleblack);
                }
                if(documentSnapshot.get("imageUrl").equals("default"))
                    friendProfilePic.setImageResource(R.drawable.empty_user);
                else
                    Glide.with(FriendProfileActivity.this).load(documentSnapshot.get("imageUrl")).into(friendProfilePic);


                MaintananceOfButton(Userid);
            }
        });
    }

    private void MaintananceOfButton(String userID) {

        friendRequestRef.child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(userID)){
                            String request_type=snapshot.child(userID).child("request_type").getValue().toString();
                            if (request_type.equals("sent")){
                                Current_state="request_sent";
                                txtSendRequest.setText("Cancel Request");
                                sendRequest.setVisibility(View.GONE);
                                cancelRequst.setVisibility(View.VISIBLE);

                            }
                            else if(request_type.equals("received")){
                                Current_state="request_received";
                                txtSendRequest.setText("Accept Friend Request");
                                acceptRequest.setVisibility(View.VISIBLE);
                                sendRequest.setVisibility(View.GONE);
                                cancelRequst.setVisibility(View.GONE);
                                declineRequuest.setVisibility(View.VISIBLE);
                            }

                        }
                        else{
                            friendRef.child(firebaseUser.getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.hasChild(userID)){
                                        Current_state="friends";
                                        txtSendRequest.setText("Friends");
                                        declineRequuest.setVisibility(View.GONE);
                                        sentFirstMessage.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void components() {
        friendProfilePic=findViewById(R.id.FriendPrfilePic);
        friendProfileName=findViewById(R.id.FriendProfileName);
        friendProfileEmail=findViewById(R.id.FriendProfileEmail);
        friendProfileGender=findViewById(R.id.FriendProfileGender);
        GenderLogo=findViewById(R.id.GenderLogo);
        sentFirstMessage=findViewById(R.id.btn_sentfirstessage);
        friendProfileback=findViewById(R.id.FriendProfileBack);
        friendProfileAbout=findViewById(R.id.FriendProfileAbout);
        sendandCancalandAccept=findViewById(R.id.SendAndCancelAndAccepRequest);
        declineRequuest=findViewById(R.id.DeclineRequest_btn);
        sendRequest=findViewById(R.id.SendRequest);
        txtSendRequest= findViewById(R.id.txtSendRequest);
        cancelRequst=findViewById(R.id.CancelRequest);
        acceptRequest=findViewById(R.id.AcceptRequest);
        acceptedRequest=findViewById(R.id.AceptedRequest);

        Current_state="no_friends";
    }

    @SuppressLint("NewApi")
    private void statusBar_and_actionBar_Tool() {
        Window window = FriendProfileActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(FriendProfileActivity.this, R.color.purple_500));
    }
}