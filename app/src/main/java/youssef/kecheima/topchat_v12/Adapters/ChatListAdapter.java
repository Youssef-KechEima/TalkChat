package youssef.kecheima.topchat_v12.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.badge.BadgeDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import youssef.kecheima.topchat_v12.Main.HomeActivity;
import youssef.kecheima.topchat_v12.Message.MessageActivity;
import youssef.kecheima.topchat_v12.Model.Chat;
import youssef.kecheima.topchat_v12.Model.User;
import youssef.kecheima.topchat_v12.R;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder>{
    private List<User> userList;
   private Context context;
    private HashMap<String,String>lastMessageMap;
    private BadgeDrawable badgeDrawable;
    private FirebaseUser firebaseUser;

    public ChatListAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
        lastMessageMap=lastMessageMap=new HashMap<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.contact_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String hisUid=userList.get(position).getId();
        String image=userList.get(position).getImageUrl();
        String userName=userList.get(position).getUser_name();
        String LastMessage=lastMessageMap.get(hisUid);

        holder.txUser.setText(userName);
        if(LastMessage==null || LastMessage.equals("default")){
            holder.txLastMessage.setVisibility(View.GONE);
        }
        else {
            holder.txLastMessage.setVisibility(View.VISIBLE);
            holder.txLastMessage.setText(LastMessage);
        }
       if(image.equals("default"))
           holder.userImage.setImageResource(R.drawable.empty_user);
       else
           Glide.with(context).load(image).into(holder.userImage);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, MessageActivity.class)
                        .putExtra("newUserId",hisUid));
                badgeDrawable=HomeActivity.getBadge();
                badgeDrawable.setNumber(0);
                badgeDrawable.setVisible(false);
                holder.txt_badgeNotification.setText(String.valueOf(0));
                holder.txt_badgeNotification.setVisibility(View.GONE);
            }
        });
        DatabaseReference inbox= FirebaseDatabase.getInstance().getReference("Chats");
        inbox.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int unread=1;
                for(DataSnapshot data : snapshot.getChildren()){
                    Chat chat=data.getValue(Chat.class);
                    Log.d("Chats","Data : "+data);
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isIs_seen()){
                        holder.txt_badgeNotification.setVisibility(View.VISIBLE);
                        holder.txt_badgeNotification.setText(String.valueOf(unread++));
                    }
                    else{
                        holder.txt_badgeNotification.setText(String.valueOf(0));
                        holder.txt_badgeNotification.setVisibility(View.GONE);
                    }
                }
                if(unread==0){
                    holder.txt_badgeNotification.setText(String.valueOf(0));
                    holder.txt_badgeNotification.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setLastMessageMap(String userId,String lastMessage){
        lastMessageMap.put(userId,lastMessage);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txUser,txLastMessage,txt_badgeNotification;
        private CircleImageView userImage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txUser=itemView.findViewById(R.id.ChatListUsername);
            txLastMessage=itemView.findViewById(R.id.Lastmessage);
            userImage=itemView.findViewById(R.id.ChatListUserImage);
            txt_badgeNotification=itemView.findViewById(R.id.txt_BagdeNotification);
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        }
    }
}
