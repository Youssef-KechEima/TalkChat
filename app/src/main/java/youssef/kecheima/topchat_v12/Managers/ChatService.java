package youssef.kecheima.topchat_v12.Managers;

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import youssef.kecheima.topchat_v12.Interfaces.OnReadChatCallBack;
import youssef.kecheima.topchat_v12.Model.Chat;


public class ChatService {
   private Context context;
   private DatabaseReference reference;
   private FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
   private String userId;


   public ChatService(Context context, String userId) {
      this.context = context;
      this.userId = userId;
   }

   public void readChatData(OnReadChatCallBack onReadChatCallBack){
      List<Chat> chatList= new ArrayList<>();
      reference = FirebaseDatabase.getInstance().getReference("Chats");
      reference.addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot snapshot) {
            chatList.clear();
            for (DataSnapshot data : snapshot.getChildren()) {
               Chat chat = data.getValue(Chat.class);
               if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId) ||
                       chat.getReceiver().equals(userId) && chat.getSender().equals(firebaseUser.getUid())) {
                  chatList.add(chat);
               }
            }
            onReadChatCallBack.onReadSuccess(chatList);
         }

         @Override
         public void onCancelled(@NonNull DatabaseError error) {
            onReadChatCallBack.onReadFailed();
         }
      });
   }
   public void sendTextMsg(String Message){
      //addMessage
      HashMap<String,Object> hashMap = new HashMap<>();
      hashMap.put("sender",firebaseUser.getUid());
      hashMap.put("receiver",userId);
      hashMap.put("is_seen",false);
      hashMap.put("message_type","TEXT");
      hashMap.put("message",Message);
      hashMap.put("images_Url","");
      hashMap.put("time",getCurrentDate());
      DatabaseReference databaseReference =FirebaseDatabase.getInstance().getReference("Chats").push();
      databaseReference.setValue(hashMap);

      //Inbox fot both users
      DatabaseReference listref1=FirebaseDatabase.getInstance().getReference("Inbox").child(firebaseUser.getUid()).child(userId);
      listref1.child("chat_id").setValue(userId);

      DatabaseReference listref2=FirebaseDatabase.getInstance().getReference("Inbox").child(userId).child(firebaseUser.getUid());
      listref2.child("chat_id").setValue(firebaseUser.getUid());
   }
   public void sendImage(String imageUrl){
      //addMessage
      HashMap<String,Object> hashMap = new HashMap<>();
      hashMap.put("sender",firebaseUser.getUid());
      hashMap.put("receiver",userId);
      hashMap.put("is_seen",false);
      hashMap.put("message_type","IMAGE");
      hashMap.put("message","");
      hashMap.put("images_Url",imageUrl);
      hashMap.put("time",getCurrentDate());
      DatabaseReference databaseReference =FirebaseDatabase.getInstance().getReference("Chats").push();
      databaseReference.setValue(hashMap);

      //Inbox fot both users
      DatabaseReference listref1=FirebaseDatabase.getInstance().getReference("Inbox").child(firebaseUser.getUid()).child(userId);
      listref1.child("chat_id").setValue(userId);

      DatabaseReference listref2=FirebaseDatabase.getInstance().getReference("Inbox").child(userId).child(firebaseUser.getUid());
      listref2.child("chat_id").setValue(firebaseUser.getUid());
   }
   public void sendFile(String fileUrl,String extension){
      //addMessage
      HashMap<String,Object> hashMap = new HashMap<>();
      hashMap.put("sender",firebaseUser.getUid());
      hashMap.put("receiver",userId);
      hashMap.put("is_seen",false);
      hashMap.put("message_type","FILE");
      hashMap.put("file_type",extension);
      hashMap.put("message","");
      hashMap.put("file_Url",fileUrl);
      hashMap.put("time",getCurrentDate());
      DatabaseReference databaseReference =FirebaseDatabase.getInstance().getReference("Chats").push();
      databaseReference.setValue(hashMap);

      //Inbox fot both users
      DatabaseReference listref1=FirebaseDatabase.getInstance().getReference("Inbox").child(firebaseUser.getUid()).child(userId);
      listref1.child("chat_id").setValue(userId);

      DatabaseReference listref2=FirebaseDatabase.getInstance().getReference("Inbox").child(userId).child(firebaseUser.getUid());
      listref2.child("chat_id").setValue(firebaseUser.getUid());
   }
   public String getCurrentDate(){
      Date c = Calendar.getInstance().getTime();
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
      String time = simpleDateFormat.format(c);
      return time;
   }

}
