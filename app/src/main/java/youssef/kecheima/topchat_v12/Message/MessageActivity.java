package youssef.kecheima.topchat_v12.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import de.hdodenhof.circleimageview.CircleImageView;
import youssef.kecheima.topchat_v12.Adapters.MessageAdapter;
import youssef.kecheima.topchat_v12.Interfaces.OnReadChatCallBack;
import youssef.kecheima.topchat_v12.Managers.ChatService;
import youssef.kecheima.topchat_v12.Managers.DialogReviewSendImage;
import youssef.kecheima.topchat_v12.Model.Chat;
import youssef.kecheima.topchat_v12.Model.User;
import youssef.kecheima.topchat_v12.R;
import youssef.kecheima.topchat_v12.Services.FirebaseServices;
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
    private LinearLayout userBar,btnGalery;
    private ValueEventListener valueEventListener;
    private CardView layoutActions;
    private ImageView fileAtach;
    private boolean isActionShow=false;
    private ChatService chatService;
    private String newUserId;
    private int IMAGE_GALLERY_REQUEST=111;
    private Uri imageUri;
    private ProgressDialog progressDialog;

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

        btnGalery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void openGallery() {
        Intent intent =new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select image"),IMAGE_GALLERY_REQUEST);
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
            if(!TextUtils.isEmpty(msg))
                chatService.sendTextMsg(msg);
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
        btnGalery=findViewById(R.id.btn_Galery);
    }

    //StatusBar and ActionBar
    @SuppressLint("NewApi")
    private void statusBar_and_actionBar_Tool() {
        Window window=MessageActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(MessageActivity.this,R.color.purple_700));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_GALLERY_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            //UploadToFireBase();
            try {
                Bitmap bitmap =MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                reviewImage(bitmap);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private void reviewImage(Bitmap bitmap){
        new DialogReviewSendImage(MessageActivity.this,bitmap).show(new DialogReviewSendImage.OnCallBack() {
            @Override
            public void OnButtonSendClick() {
                if (imageUri!=null) {
                    progressDialog = new ProgressDialog(MessageActivity.this);
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.progress_dialog);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    new FirebaseServices(MessageActivity.this).UploadImagetoFirebaseStorage(imageUri, new FirebaseServices.OnCallBack() {
                        @Override
                        public void OnUploadSuccess(String imagesUrl) {
                            chatService.sendImage(imagesUrl);
                            progressDialog.dismiss();
                        }

                        @Override
                        public void OnUploadFailed(Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }
}