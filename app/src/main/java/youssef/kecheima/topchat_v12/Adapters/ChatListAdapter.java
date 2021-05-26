package youssef.kecheima.topchat_v12.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.HashMap;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import youssef.kecheima.topchat_v12.Message.MessageActivity;
import youssef.kecheima.topchat_v12.Model.User;
import youssef.kecheima.topchat_v12.R;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder>{
    private List<User> userList;
   private Context context;
    private HashMap<String,String>lastMessageMap;

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
        private TextView txUser,txLastMessage;
        private CircleImageView userImage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txUser=itemView.findViewById(R.id.ChatListUsername);
            txLastMessage=itemView.findViewById(R.id.Lastmessage);
            userImage=itemView.findViewById(R.id.ChatListUserImage);
        }
    }
}
