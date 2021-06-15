package youssef.kecheima.topchat_v12.Managers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityOptionsCompat;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import youssef.kecheima.topchat_v12.Message.MessageActivity;
import youssef.kecheima.topchat_v12.Model.Common;
import youssef.kecheima.topchat_v12.Model.User;
import youssef.kecheima.topchat_v12.R;
import youssef.kecheima.topchat_v12.Settings.DisplayPictureActivity;
import youssef.kecheima.topchat_v12.Settings.FriendProfileActivity;

public class DialogViewUser {
    private Context context;
    private final Dialog dialog;
    private FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();

    public DialogViewUser(Context context, User user) {
        this.context = context;
        this.dialog=new Dialog(context);
        initialize(user);
    }

    private void initialize(final User user) {
        dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        dialog.setContentView(R.layout.dialog_view_user);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width=WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height=WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setAttributes(lp);

        ImageButton btnChat,btnCall,btnVideoCall,btnInfo;
        final ImageView profile;
        TextView userName;

        btnChat=dialog.findViewById(R.id.DialogUserChat);
        btnCall=dialog.findViewById(R.id.DialogUserPhone);
        btnVideoCall=dialog.findViewById(R.id.DialogUserVideo);
        btnInfo=dialog.findViewById(R.id.DialogUserInfoProfile);

        profile=dialog.findViewById(R.id.DialogUserImage);
        userName=dialog.findViewById(R.id.DialogUserName);

        userName.setText(user.getUser_name());
        firebaseFirestore.collection("Users").document(user.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.get("imageUrl").equals("default")){
                    profile.setImageResource(R.drawable.empty_user);
                }
                else {
                    Glide.with(context).load(user.getImageUrl()).into(profile);
                }
            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, MessageActivity.class)
                .putExtra("newUserId",user.getId()));
                dialog.dismiss();
            }
        });
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, FriendProfileActivity.class)
                .putExtra("newUserId",user.getId()));
                dialog.dismiss();
            }
        });
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Call", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        btnVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Video Call", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile.invalidate();
                Drawable dr=profile.getDrawable();
                Common.IMAGE_BITMAP=((BitmapDrawable)dr.getCurrent()).getBitmap();
                ActivityOptionsCompat activityOptionsCompat=ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)context,profile,"image");
                Intent intent =new Intent(context, DisplayPictureActivity.class);
                context.startActivity(intent,activityOptionsCompat.toBundle());
            }
        });
        dialog.show();
    }
}
