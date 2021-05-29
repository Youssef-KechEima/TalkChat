package youssef.kecheima.topchat_v12.Message;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import youssef.kecheima.topchat_v12.Adapters.MessageAdapter;
import youssef.kecheima.topchat_v12.Fragments.RequestFragment;
import youssef.kecheima.topchat_v12.Model.Chat;
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
    private MessageAdapter messageAdapter;
    private List<Chat> chatList;
    private RecyclerView messageRecycler;
    private String baseUrl="https://fcm.googleapis.com/fcm/send";
    private RequestQueue requestQueue;
    private LinearLayout userBar;


    //Main Methode
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        compoments();
        statusBar_and_actionBar_Tool();


        messageRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        messageRecycler.setLayoutManager(linearLayoutManager);



        //get idUser
        Intent intent = getIntent();
        String newUserId=intent.getStringExtra("newUserId");




        //FireBase Instances
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore=FirebaseFirestore.getInstance();
        statusRef=FirebaseDatabase.getInstance().getReference("UserStatus");

        requestQueue= Volley.newRequestQueue(this);


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

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initBtnClick(newUserId);
                messageText.setText("");
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
                readMessage(firebaseUser.getUid(),newUserId,user.getImageUrl());
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
                startActivity(intent);
            }
        });
    }

    //Mehhode to verify the messge then send it
    private void initBtnClick(String userID){
        String msg=messageText.getText().toString();
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        String time = simpleDateFormat.format(c);
        if(!TextUtils.isEmpty(msg))
            sendMessage(firebaseUser.getUid(),userID,msg,time);
        else
            Toast.makeText(this, "You can't send a empty message", Toast.LENGTH_SHORT).show();
    }

    //Send Message
    private void sendMessage(String sender ,String receiver,String Message,String time){
        //addMessage
        DatabaseReference databaseReference =FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",Message);
        hashMap.put("time",time);
        databaseReference.child("Chats").push().setValue(hashMap);

        //add ChatList Receiver
        Map<String,String> inboxReceiver= new HashMap<>();
        inboxReceiver.put("chat_id",receiver);
        inboxReceiver.put("message_type","received");
        DatabaseReference listref1=FirebaseDatabase.getInstance().getReference("Inbox").child(sender).child(receiver);
        listref1.setValue(inboxReceiver);

        //add ChatList Sender
        Map<String,String> inboxSender = new HashMap<>();
        inboxSender.put("chat_id",sender);
        inboxSender.put("message_type","sent");
        DatabaseReference listref2=FirebaseDatabase.getInstance().getReference("Inbox").child(receiver).child(sender);
        listref2.setValue(inboxSender);


        //send Notification

        sendNotification(receiver,Message);


    }

    private void sendNotification(String receiver, String message) {
        firebaseFirestore.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user=documentSnapshot.toObject(User.class);
                JSONObject jsonObject= new JSONObject();
                try {
                    jsonObject.put("to","/topics/"+receiver);
                    JSONObject jsonObject1= new JSONObject();
                    jsonObject1.put("title",user.getUser_name());
                    jsonObject1.put("body",message);

                    JSONObject jsonObject2=new JSONObject();
                    jsonObject2.put("UserId",firebaseUser.getUid());
                    jsonObject2.put("type","message");

                    jsonObject.put("data",jsonObject2);
                    jsonObject.put("notification",jsonObject1);

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, baseUrl, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String,String> map= new HashMap<>();
                            map.put("content-type","application/json");
                            map.put("authorization","key=AAAA504BXng:APA91bER4hPRvyFuZWtmnYkhWHnByX8FgtwPEswneZ0K6fA7Cf9GM2kbZm1zIrdYR8cp625jpTlHa1DUSSwfHZhvQh2yst3CC84_mUkKxGIJFSh2L10YjvejF3RRIgorLXiPAdOMDB9d");
                            return map;
                        }
                    };
                    requestQueue.add(request);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
        super.onPause();
    }

    @Override
    protected void onResume() {
        checkOnlineStatus("online");
        super.onResume();
    }

    //Read Message
    private void readMessage(String myId,String userId,String imageUrl){
        chatList=new ArrayList<>();
            reference = FirebaseDatabase.getInstance().getReference("Chats");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    chatList.clear();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Chat chat = data.getValue(Chat.class);
                        if (chat.getReceiver().equals(myId) && chat.getSender().equals(userId) ||
                                chat.getReceiver().equals(userId) && chat.getSender().equals(myId)) {
                            chatList.add(chat);
                        }

                        messageAdapter = new MessageAdapter(chatList, MessageActivity.this, imageUrl);
                        messageRecycler.setAdapter(messageAdapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
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