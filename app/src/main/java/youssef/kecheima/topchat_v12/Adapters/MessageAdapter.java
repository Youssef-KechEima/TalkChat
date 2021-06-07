package youssef.kecheima.topchat_v12.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import youssef.kecheima.topchat_v12.Model.Chat;
import youssef.kecheima.topchat_v12.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {
    public static int MSG_TYPE_RECEIVER = 0;
    public static int MSG_TYPE_SENDER = 1;
    private List<Chat> chatList;
    private Context context;
    private String imageUrl;
   private FirebaseUser firebaseUser;
    public MessageAdapter(List<Chat> chatList, Context context) {
        this.chatList = chatList;
        this.context = context;
    }


    public void setChatList(List<Chat> chatList){
        this.chatList=chatList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_SENDER) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_sender, parent, false);
            return new MessageAdapter.MyViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_receiver, parent, false);
            return new MessageAdapter.MyViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MyViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.show_Message.setText(chat.getMessage());
        holder.timeChat.setText(chat.getTime());
        if(position==chatList.size()-1){
            if(chat.isIs_seen()){
                holder.is_seen.setText("Seen");
            }
            else{
                holder.is_seen.setText("Delivered");
            }
        }
        else {
            holder.is_seen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView show_Message, timeChat,is_seen;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            timeChat=itemView.findViewById(R.id.TimeMessage);
            show_Message=itemView.findViewById(R.id.show_Message);
            is_seen=itemView.findViewById(R.id.is_Seen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(chatList.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_SENDER;
        }
        else{
            return MSG_TYPE_RECEIVER;
        }
    }
}
