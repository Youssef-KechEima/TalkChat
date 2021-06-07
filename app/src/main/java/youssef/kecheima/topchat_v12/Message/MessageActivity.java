package youssef.kecheima.topchat_v12.Message;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import youssef.kecheima.topchat_v12.Adapters.ChatListAdapter;
import youssef.kecheima.topchat_v12.Adapters.MessageAdapter;
import youssef.kecheima.topchat_v12.Fragments.ChatFragment;
import youssef.kecheima.topchat_v12.Fragments.RequestFragment;
import youssef.kecheima.topchat_v12.Interfaces.OnReadChatCallBack;
import youssef.kecheima.topchat_v12.Main.HomeActivity;
import youssef.kecheima.topchat_v12.Managers.ChatService;
import youssef.kecheima.topchat_v12.Model.AESUtils;
import youssef.kecheima.topchat_v12.Model.Chat;
import youssef.kecheima.topchat_v12.Model.ChatList;
import youssef.kecheima.topchat_v12.Model.User;
import youssef.kecheima.topchat_v12.R;
import youssef.kecheima.topchat_v12.Settings.FriendProfileActivity;

public class MessageActivity extends AppCompatActivity {
    private CircleImageView userImage;
    private TextView userName,status;
    private ImageButton backArraw;
    private FirebaseUser firebaseUser;
    private FloatingActionButton sendBtn;
    private EditText messageText;
    private FirebaseFirestore firebaseFirestore;
    private DatabaseReference reference,statusRef;
    private List<Chat> chatList;
    private MessageAdapter messageAdapter;
    private RecyclerView messageRecycler;
    private LinearLayout userBar;
    private ValueEventListener valueEventListener;
    private CardView layoutActions;
    private ImageView fileAtach;
    private boolean isActionShow=false;
    private ChatService chatService;
    private String newUserId;

    //Main Methode
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        compoments();
        statusBar_and_actionBar_Tool();


        //get idUser
        Intent intent = getIntent();
        newUserId=intent.getStringExtra("newUserId");


        //FireBase Instances
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore=FirebaseFirestore.getInstance();
        statusRef=FirebaseDatabase.getInstance().getReference("UserStatus");

        chatList = new ArrayList<>();
        messageRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageRecycler.setLayoutManager(linearLayoutManager);
        messageAdapter=new MessageAdapter(chatList,this);
        messageRecycler.setAdapter(messageAdapter);

        initialisation();

        readMessage();

        seenMessage(newUserId);

    }
    private void initialisation(){

        chatService = new ChatService(this,newUserId);


        messageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(TextUtils.isEmpty(messageText.getText().toString())){
                    sendBtn.setImageDrawable(getDrawable(R.drawable.voice));
                }
                else{
                    sendBtn.setImageDrawable(getDrawable(R.drawable.send));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        firebaseFirestore.collection("Users").document(newUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user=documentSnapshot.toObject(User.class);
                userName.setText(user.getUser_name());
                if(user.getImageUrl().equals("default")){
                    userImage.setImageResource(R.drawable.empty_user);
                }
                else{
                    Glide.with(MessageActivity.this).load(user.getImageUrl()).into(userImage);
                }
            }
        });


        statusRef.child(newUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String Status=snapshot.child("status").getValue().toString();
                        if(Status.equals("online")){
                            status.setText("Online");
                        }
                        else{
                            Calendar cal= Calendar.getInstance(Locale.FRANCE);
                            cal.setTimeInMillis(Long.parseLong(Status));
                            String Date= DateFormat.format("dd/MM/yyyy hh:mm",cal).toString();
                            status.setText("last Seen: "+Date);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initBtnClick();
                messageText.setText("");
            }
        });

        backArraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        userBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MessageActivity.this, FriendProfileActivity.class);
                intent1.putExtra("newUserId",newUserId);
                startActivity(intent1);
            }
        });

        fileAtach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isActionShow) {
                    layoutActions.setVisibility(View.GONE);
                    isActionShow=false;
                }
                else {
                    layoutActions.setVisibility(View.VISIBLE);
                    isActionShow=true;
                }
            }
        });


    }

    private void readMessage() {
        chatService.readChatData(new OnReadChatCallBack() {
            @Override
            public void onReadSuccess(List<Chat> chatList) {
                messageAdapter.setChatList(chatList);
            }

            @Override
            public void onReadFailed() {

            }
        });
    }

    //Mehhode to verify the messge then send it
    private void initBtnClick(){
        String msg=messageText.getText().toString();
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        String time = simpleDateFormat.format(c);
            if(!TextUtils.isEmpty(msg))
                chatService.sendTextMsg(firebaseUser.getUid(), newUserId, msg, time);;
    }


    //seenMessage
    private void seenMessage(String userId){
       reference=FirebaseDatabase.getInstance().getReference();
        valueEventListener=reference.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    Chat chat=data.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId)){
                        HashMap<String,Object> hashMap2 = new HashMap<>();
                        hashMap2.put("is_seen",true);
                        data.getRef().updateChildren(hashMap2);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
        reference.removeEventListener(valueEventListener);
        super.onPause();
    }

    @Override
    protected void onResume() {
        checkOnlineStatus("online");
        super.onResume();
    }


    //castComponents
    private void compoments() {
        userImage=findViewById(R.id.MassageImageProfile);
        userName=findViewById(R.id.MessageuserName);
        backArraw=findViewById(R.id.back_btn);
        messageText=findViewById(R.id.messageText);
        sendBtn=findViewById(R.id.send_btn);
        messageRecycler=findViewById(R.id.MessageRecycler);
        status=findViewById(R.id.MessageLastSeen);
        userBar=findViewById(R.id.Userbar);
        fileAtach=findViewById(R.id.btn_fileAttach);
        layoutActions=findViewById(R.id.layout_actions);
    }

    //StatusBar and ActionBar
    @SuppressLint("NewApi")
    private void statusBar_and_actionBar_Tool() {
        Window window=MessageActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(MessageActivity.this,R.color.purple_700));
    }
}