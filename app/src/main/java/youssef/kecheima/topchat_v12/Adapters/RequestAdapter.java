package youssef.kecheima.topchat_v12.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import youssef.kecheima.topchat_v12.Fragments.RequestFragment;
import youssef.kecheima.topchat_v12.Message.MessageActivity;
import youssef.kecheima.topchat_v12.Model.Request;
import youssef.kecheima.topchat_v12.Model.User;
import youssef.kecheima.topchat_v12.R;
import youssef.kecheima.topchat_v12.Settings.FriendProfileActivity;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.MyViewHolder> {
    private Context context;
    private List<User> users;
    private DatabaseReference friendRequestRef,friendRef;
    private FirebaseUser firebaseUser;


    public RequestAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.request_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String hisUid=users.get(position).getId();
        String image=users.get(position).getImageUrl();
        String userName=users.get(position).getUser_name();

        holder.txt_UserName.setText(userName);
        if(image.equals("default"))
            holder.userImage.setImageResource(R.drawable.empty_user);
        else
            Glide.with(context).load(image).into(holder.userImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, FriendProfileActivity.class)
                        .putExtra("newUserId",hisUid));

                Bundle bundle = new Bundle();
                bundle.putString("otherUserId",hisUid);
                RequestFragment fragment = new RequestFragment();
                fragment.setArguments(bundle);
            }
        });
        holder.btn_Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AcceptRequestFriend(hisUid);
            }
        });

        holder.btn_Decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeclineRequestfriend(hisUid);
            }
        });
    }

    private void DeclineRequestfriend(String hisUid) {
        friendRequestRef= FirebaseDatabase.getInstance().getReference("Friendsrequest");
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        friendRequestRef.child(firebaseUser.getUid()).child(hisUid)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            friendRequestRef.child(hisUid).child(firebaseUser.getUid())
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(context, "You have Decline the Request", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptRequestFriend(String hisUid) {
        friendRequestRef= FirebaseDatabase.getInstance().getReference("Friendsrequest");
        friendRef=FirebaseDatabase.getInstance().getReference("Friends");
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        friendRef.child(firebaseUser.getUid()).child(hisUid).child("friend_id").setValue(hisUid)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            friendRef.child(hisUid).child(firebaseUser.getUid()).child("friend_id").setValue(firebaseUser.getUid())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                friendRequestRef.child(firebaseUser.getUid()).child(hisUid)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    friendRequestRef.child(hisUid)
                                                                            .child(firebaseUser.getUid())
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()){
                                                                                        Toast.makeText(context, "You have Accepted the Request", Toast.LENGTH_SHORT).show();
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

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userImage;
        TextView txt_UserName;
        Button btn_Accept,btn_Decline;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage=itemView.findViewById(R.id.RequestUserImage);
            txt_UserName=itemView.findViewById(R.id.RequeestUsername);
            btn_Accept=itemView.findViewById(R.id.Accept_btn);
            btn_Decline=itemView.findViewById(R.id.Decline_btn);
        }
    }
}
