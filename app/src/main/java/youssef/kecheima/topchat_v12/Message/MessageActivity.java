package youssef.kecheima.topchat_v12.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import de.hdodenhof.circleimageview.CircleImageView;
import youssef.kecheima.topchat_v12.Adapters.MessageAdapter;
import youssef.kecheima.topchat_v12.Interfaces.OnReadChatCallBack;
import youssef.kecheima.topchat_v12.Services.ChatService;
import youssef.kecheima.topchat_v12.Managers.DialogReviewSendImage;
import youssef.kecheima.topchat_v12.Model.Chat;
import youssef.kecheima.topchat_v12.Model.User;
import youssef.kecheima.topchat_v12.R;
import youssef.kecheima.topchat_v12.Services.FirebaseServices;
import youssef.kecheima.topchat_v12.Settings.FriendProfileActivity;

public class MessageActivity extends AppCompatActivity {
    private static final int REQUEST_CORD_PERMISSION = 332;
    private CircleImageView userImage;
    private TextView userName, status;
    private ImageButton backArraw;
    private FirebaseUser firebaseUser;
    private FloatingActionButton sendBtn;
    private EditText messageText;
    private FirebaseFirestore firebaseFirestore;
    private DatabaseReference reference, statusRef;
    private List<Chat> chatList;
    private MessageAdapter messageAdapter;
    private RecyclerView messageRecycler;
    private LinearLayout userBar, btnGalery,btnDocuments;
    private ValueEventListener valueEventListener;
    private CardView layoutActions;
    private ImageView fileAtach,camera,emoji;
    private boolean isActionShow = false;
    private ChatService chatService;
    private String newUserId;
    private int IMAGE_GALLERY_REQUEST = 111;
    private int FILEPICKER_REQUEST = 112;
    private Uri imageUri,file;
    private ProgressDialog progressDialog;
    private RecordView recordView;
    private RecordButton recordButton;
    //Audio
    private MediaRecorder mediaRecorder;
    private String audio_path;
    private String sTime;

    //Main Methode
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        compoments();
        statusBar_and_actionBar_Tool();


        //get idUser
        Intent intent = getIntent();
        newUserId = intent.getStringExtra("newUserId");


        //FireBase Instances
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        statusRef = FirebaseDatabase.getInstance().getReference("UserStatus");

        chatList = new ArrayList<>();
        messageRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageRecycler.setLayoutManager(linearLayoutManager);
        messageAdapter = new MessageAdapter(chatList, this);
        messageRecycler.setAdapter(messageAdapter);

        initialisation();

        readMessage();

        seenMessage(newUserId);

    }

    //everyThing
    private void initialisation() {

        chatService = new ChatService(this, newUserId);


        messageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(messageText.getText().toString())) {
                    sendBtn.setVisibility(View.INVISIBLE);
                    recordButton.setVisibility(View.VISIBLE);
                } else {
                    sendBtn.setVisibility(View.VISIBLE);
                    recordButton.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        firebaseFirestore.collection("Users").document(newUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                userName.setText(user.getUser_name());
                if (user.getImageUrl().equals("default")) {
                    userImage.setImageResource(R.drawable.empty_user);
                } else {
                    Glide.with(MessageActivity.this).load(user.getImageUrl()).into(userImage);
                }
            }
        });


        statusRef.child(newUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String Status = snapshot.child("status").getValue().toString();
                        if (Status.equals("online")) {
                            status.setText("Online");
                        } else {
                            Calendar cal = Calendar.getInstance(Locale.FRANCE);
                            cal.setTimeInMillis(Long.parseLong(Status));
                            String Date = DateFormat.format("dd/MM/yyyy hh:mm", cal).toString();
                            status.setText("last Seen: " + Date);
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
                intent1.putExtra("newUserId", newUserId);
                startActivity(intent1);
            }
        });

        fileAtach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isActionShow) {
                    layoutActions.setVisibility(View.GONE);
                    isActionShow = false;
                } else {
                    layoutActions.setVisibility(View.VISIBLE);
                    isActionShow = true;
                }
            }
        });

        btnGalery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        btnDocuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDocuments();
            }
        });

        recordButton.setRecordView(recordView);
        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                if(checkPermission()){
                    fileAtach.setVisibility(View.INVISIBLE);
                    emoji.setVisibility(View.INVISIBLE);
                    camera.setVisibility(View.INVISIBLE);
                    messageText.setVisibility(View.INVISIBLE);

                    startRecording();
                    Vibrator vibrator =(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(100);
                }
                else{
                    RequestPermission();
                }
            }

            @Override
            public void onCancel() {
                try {
                    mediaRecorder.reset();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                fileAtach.setVisibility(View.VISIBLE);
                emoji.setVisibility(View.VISIBLE);
                camera.setVisibility(View.VISIBLE);
                messageText.setVisibility(View.VISIBLE);

                try {
                    sTime=getHumanTimetext(recordTime);
                    stopRecord();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onLessThanSecond() {
                fileAtach.setVisibility(View.VISIBLE);
                emoji.setVisibility(View.VISIBLE);
                camera.setVisibility(View.VISIBLE);
                messageText.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLock() {

            }
        });

        recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                fileAtach.setVisibility(View.VISIBLE);
                emoji.setVisibility(View.VISIBLE);
                camera.setVisibility(View.VISIBLE);
                messageText.setVisibility(View.VISIBLE);
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private String getHumanTimetext(long recordTime) {
        return String.format("%02d",
                TimeUnit.MILLISECONDS.toSeconds(recordTime)-
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toSeconds(recordTime)));
    }

    //open Files
    private void openDocuments() {
        String[] mineTypes ={"application/pdf","application/zip","text/plain",
                "application/vnd.ms-word","application/vnd.ms-powerpoint",
                "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.wordprocessing.document",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"};
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            intent.setType(mineTypes.length==1 ? mineTypes[0] : "*/*");
            if(mineTypes.length>0){
                intent.putExtra(Intent.EXTRA_MIME_TYPES,mineTypes);
            }
        }
        else{
            String mineTypeStr="";
            for(String minType : mineTypes){
                mineTypeStr+=minType+ "|";
            }
            intent.setType(mineTypeStr.substring(0,mineTypeStr.length()-1));
        }
        startActivityForResult(Intent.createChooser(intent,"select file"),FILEPICKER_REQUEST);
    }

    //open Image Gallery
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select image"), IMAGE_GALLERY_REQUEST);
    }

    //read Message
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
    private void initBtnClick() {
        String msg = messageText.getText().toString();
        if (!TextUtils.isEmpty(msg))
            chatService.sendTextMsg(msg);
    }


    //seen Message
    private void seenMessage(String userId) {
        reference = FirebaseDatabase.getInstance().getReference();
        valueEventListener = reference.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Chat chat = data.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId)) {
                        HashMap<String, Object> hashMap2 = new HashMap<>();
                        hashMap2.put("is_seen", true);
                        data.getRef().updateChildren(hashMap2);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //Check if User is Online
    private void checkOnlineStatus(String status) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        statusRef.child(firebaseUser.getUid()).updateChildren(hashMap);

    }
    @Override
    protected void onStart() {
        checkOnlineStatus("online");
        super.onStart();
    }
    @Override
    protected void onPause() {
        String timestamp = String.valueOf(System.currentTimeMillis());
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
        userImage = findViewById(R.id.MassageImageProfile);
        userName = findViewById(R.id.MessageuserName);
        backArraw = findViewById(R.id.back_btn);
        messageText = findViewById(R.id.messageText);
        sendBtn = findViewById(R.id.send_btn);
        messageRecycler = findViewById(R.id.MessageRecycler);
        status = findViewById(R.id.MessageLastSeen);
        userBar = findViewById(R.id.Userbar);
        fileAtach = findViewById(R.id.btn_fileAttach);
        layoutActions = findViewById(R.id.layout_actions);
        btnGalery = findViewById(R.id.btn_Galery);
        btnDocuments=findViewById(R.id.btn_documents);
        recordView=findViewById(R.id.RecordAudio);
        recordButton=findViewById(R.id.RecordButton);
        camera=findViewById(R.id.take_photo);
        emoji=findViewById(R.id.btn_emoji);
    }

    //StatusBar and ActionBar
    @SuppressLint("NewApi")
    private void statusBar_and_actionBar_Tool() {
        Window window = MessageActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(MessageActivity.this, R.color.purple_700));
    }

    //get Images and files
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_GALLERY_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                reviewImage(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(requestCode==FILEPICKER_REQUEST && resultCode==RESULT_OK && data !=null && data.getData()!=null){
            file=data.getData();
            UploadToFireBase();

        }
    }

    //Upload File to Firebase Storage
    private void UploadToFireBase() {
        progressDialog= new ProgressDialog(MessageActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        StorageReference riverref= FirebaseStorage.getInstance().getReference().child("Chats/Files/"+firebaseUser.getUid()+"/"+System.currentTimeMillis()+"."+GetFileExtension(file));
        riverref.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask=taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful());
                Uri downlaodUri=urlTask.getResult();
                final String sdownload=String.valueOf(downlaodUri);
                chatService.sendFile(sdownload,GetFileExtension(file));
                progressDialog.dismiss();
                layoutActions.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(MessageActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //get file Extension
    private String GetFileExtension(Uri uri){
        ContentResolver contentResolver=this.getContentResolver();
        MimeTypeMap mimeTypeMap =MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    //review Image then Send it
    private void reviewImage(Bitmap bitmap) {
        new DialogReviewSendImage(MessageActivity.this, bitmap).show(new DialogReviewSendImage.OnCallBack() {
            @Override
            public void OnButtonSendClick() {
                if (imageUri != null) {
                    progressDialog = new ProgressDialog(MessageActivity.this);
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.progress_dialog);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    new FirebaseServices(MessageActivity.this).UploadImagetoFirebaseStorage(imageUri, new FirebaseServices.OnCallBack() {
                        @Override
                        public void OnUploadSuccess(String imagesUrl) {
                            chatService.sendImage(imagesUrl);
                            progressDialog.dismiss();
                            layoutActions.setVisibility(View.GONE);
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

    //check Permission
    private boolean checkPermission(){
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result== PackageManager.PERMISSION_DENIED || record_audio_result==PackageManager.PERMISSION_DENIED;
    }

    //Request Permission
    private void RequestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        },REQUEST_CORD_PERMISSION);
    }

    //save the Recording
    private void setUpMedaiRecorder() {
        String path_save = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ UUID.randomUUID().toString()+"_audio_record.m4a";
        audio_path=path_save;

        mediaRecorder=new MediaRecorder();
        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(path_save);
        }
        catch (Exception e){
            Log.d("TAG","setUpMedaiRecorder: "+e.getMessage());
        }
    }

    //Start Recording
    private void startRecording(){
        setUpMedaiRecorder();

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        }
        catch (IOException e){
            e.printStackTrace();
            Log.d("Error", "startRecording: "+e.getMessage());
            Toast.makeText(this, "Recording Error , please restart your app", Toast.LENGTH_SHORT).show();
        }
    }

    //Stop Recording
    private void stopRecord(){
        try {
            if(mediaRecorder!=null){
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder=null;
                chatService.uploadVoice(audio_path);
                new File(audio_path).delete();
            }
            else {
                Toast.makeText(this, "Null", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            Toast.makeText(this, "stop Recoding Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}