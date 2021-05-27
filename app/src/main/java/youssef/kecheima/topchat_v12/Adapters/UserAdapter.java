package youssef.kecheima.topchat_v12.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import youssef.kecheima.topchat_v12.Fragments.RequestFragment;
import youssef.kecheima.topchat_v12.Message.MessageActivity;
import youssef.kecheima.topchat_v12.Model.User;
import youssef.kecheima.topchat_v12.R;
import youssef.kecheima.topchat_v12.Search.SearchFriendsActivity;
import youssef.kecheima.topchat_v12.Settings.FriendProfileActivity;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
   private List<User> userList;
    private Context context;

    public UserAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.MyViewHolder holder, int position) {
        User user=userList.get(position);
        holder.txtContact.setText(user.getUser_name());
        if(user.getImageUrl().equals("default"))
            holder.ContactImage.setImageResource(R.drawable.empty_user);
        else
        Glide.with(context).load(user.getImageUrl()).into(holder.ContactImage);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,FriendProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("newUserId",user.getId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtContact;
        CircleImageView ContactImage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtContact=itemView.findViewById(R.id.userUsername);
            ContactImage=itemView.findViewById(R.id.userUserImage);
        }
    }
}
