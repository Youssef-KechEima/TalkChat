package youssef.kecheima.topchat_v12.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneMultiFactorAssertion;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import youssef.kecheima.topchat_v12.Message.ReviewChatImagesActivity;
import youssef.kecheima.topchat_v12.Model.Chat;
import youssef.kecheima.topchat_v12.Model.Common;
import youssef.kecheima.topchat_v12.R;
import youssef.kecheima.topchat_v12.Services.AudioVoiceService;
import youssef.kecheima.topchat_v12.Settings.DisplayPictureActivity;
import youssef.kecheima.topchat_v12.Settings.UserProfileActivity;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {
    public static int MSG_TYPE_RECEIVER = 0;
    public static int MSG_TYPE_SENDER = 1;
    private List<Chat> chatList;
    private Context context;
   private FirebaseUser firebaseUser;
   private ProgressDialog progressDialog;
   private ImageButton tmpPlay;
   private AudioVoiceService audioVoiceService;
    public MessageAdapter(List<Chat> chatList, Context context) {
        this.chatList = chatList;
        this.context = context;
        this.audioVoiceService= new AudioVoiceService(context);
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
        switch (chat.getMessage_type()) {
            case "TEXT":
                holder.relative_Image.setVisibility(View.GONE);
                holder.relative_File.setVisibility(View.GONE);
                holder.relative_Text.setVisibility(View.VISIBLE);
                holder.linearRelativeAudioVoice.setVisibility(View.GONE);
                holder.show_Message.setText(chat.getMessage());
                holder.timeChat.setText(chat.getTime());
                if (position == chatList.size() - 1) {
                    if (chat.isIs_seen()) {
                        holder.is_seen.setText("S");
                    } else {
                        holder.is_seen.setText("D");
                    }
                } else {
                    holder.is_seen.setVisibility(View.GONE);
                }
                break;
            case "IMAGE":
                holder.relative_Text.setVisibility(View.GONE);
                holder.relative_File.setVisibility(View.GONE);
                holder.relative_Image.setVisibility(View.VISIBLE);
                holder.linearRelativeAudioVoice.setVisibility(View.GONE);
                Glide.with(context).load(chat.getImages_Url()).apply(RequestOptions.centerCropTransform()).into(holder.show_image);
                holder.timeImage.setText(chat.getTime());
                if (position == chatList.size() - 1) {
                    if (chat.isIs_seen()) {
                        holder.Is_seenImage.setText("S");
                    } else {
                        holder.Is_seenImage.setText("D");
                    }
                } else {
                    holder.Is_seenImage.setVisibility(View.GONE);
                }
                break;
            case "FILE":
                holder.relative_Text.setVisibility(View.GONE);
                holder.relative_Image.setVisibility(View.GONE);
                holder.relative_File.setVisibility(View.VISIBLE);
                holder.linearRelativeAudioVoice.setVisibility(View.GONE);
                switch (chat.getFile_type()){
                    case "pdf":
                        holder.imageFile.setImageResource(R.drawable.pdf);
                        holder.show_textFile.setText("PDF FILE");
                        break;
                    case "doc":
                    case "docx":
                        holder.imageFile.setImageResource(R.drawable.docx);
                        holder.show_textFile.setText("Document");
                        break;
                    case "ppt":
                    case "pptx":
                        holder.imageFile.setImageResource(R.drawable.pptx);
                        holder.show_textFile.setText("Presentation");
                        break;
                    case "xls":
                    case "xlsx":
                        holder.imageFile.setImageResource(R.drawable.xlsx);
                        holder.show_textFile.setText("Sheet");
                        break;
                    case "zip":
                        holder.imageFile.setImageResource(R.drawable.zip);
                        holder.show_textFile.setText("ZIP File");
                        break;
                    case "txt":
                        holder.imageFile.setImageResource(R.drawable.txt);
                        holder.show_textFile.setText("TEXT File");
                        break;
                }
                holder.timeFile.setText(chat.getTime());
                if (position == chatList.size() - 1) {
                    if (chat.isIs_seen()) {
                        holder.is_seenFile.setText("S");
                    } else {
                        holder.is_seenFile.setText("D");
                    }
                } else {
                    holder.is_seenFile.setVisibility(View.GONE);
                }
                break;

            case "AUDIO_VOICE":
                holder.relative_Text.setVisibility(View.GONE);
                holder.relative_Image.setVisibility(View.GONE);
                holder.relative_File.setVisibility(View.GONE);
                holder.linearRelativeAudioVoice.setVisibility(View.VISIBLE);
                holder.linearRelativeAudioVoice.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onClick(View v) {
                        if(tmpPlay!=null){
                            tmpPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.play_arrow));
                        }
                        holder.audioVoicePlay.setImageDrawable(context.getResources().getDrawable(R.drawable.pause_arrow));
                        audioVoiceService.playAudioVoiceFromUrl(chat.getAudio_Url(), new AudioVoiceService.OnPlayCallBack() {
                            @Override
                            public void OnFinished() {
                                holder.audioVoicePlay.setImageDrawable(context.getResources().getDrawable(R.drawable.play_arrow));
                            }
                        });
                        tmpPlay=holder.audioVoicePlay;
                    }
                });
                holder.timeAudioVoice.setText(chat.getTime());
                if (position == chatList.size() - 1) {
                    if (chat.isIs_seen()) {
                        holder.is_seenAudioVoice.setText("S");
                    } else {
                        holder.is_seenAudioVoice.setText("D");
                    }
                } else {
                    holder.is_seenAudioVoice.setVisibility(View.GONE);
                }
                break;
        }
        holder.show_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.show_image.invalidate();
                Drawable dr=holder.show_image.getDrawable();
                Common.IMAGE_BITMAP=((BitmapDrawable)dr.getCurrent()).getBitmap();
                ActivityOptionsCompat activityOptionsCompat=ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, holder.show_image,"imageView");
                Intent intent =new Intent(context, ReviewChatImagesActivity.class);
                intent.putExtra("Image_Url",chat.getImages_Url());
                context.startActivity(intent,activityOptionsCompat.toBundle());
            }
        });
        holder.downloadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddmmyyyyhhmmss");
                String date =simpleDateFormat.format(new Date());
                DownloadFile(date,chat.getFile_Url(),chat.getFile_type());
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
         TextView show_Message, timeChat,is_seen,timeImage,Is_seenImage,show_textFile,timeFile,is_seenFile,timeAudioVoice,is_seenAudioVoice;
         ImageView show_image,imageFile;
         ImageButton downloadFile,audioVoicePlay;
         RelativeLayout relative_Text,relative_Image,relative_File;
         LinearLayout linearRelativeAudioVoice;
         View audioVoiceLength;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            timeChat=itemView.findViewById(R.id.TimeMessage);
            show_Message=itemView.findViewById(R.id.show_Message);
            is_seen=itemView.findViewById(R.id.is_Seen);
            show_image=itemView.findViewById(R.id.Show_Image);
            timeImage=itemView.findViewById(R.id.TimeImage);
            Is_seenImage=itemView.findViewById(R.id.is_SeenImage);
            show_textFile=itemView.findViewById(R.id.Show_TextFile);
            timeFile=itemView.findViewById(R.id.TimeFile);
            is_seenFile=itemView.findViewById(R.id.is_SeenFile);
            imageFile=itemView.findViewById(R.id.ImageFile);
            downloadFile=itemView.findViewById(R.id.downloadFile);
            timeAudioVoice=itemView.findViewById(R.id.TimeAudioVoice);
            is_seenAudioVoice=itemView.findViewById(R.id.is_SeenAudioVoice);
            audioVoicePlay=itemView.findViewById(R.id.AudioVoice_play);
            audioVoiceLength=itemView.findViewById(R.id.AudioVoice_length);
            relative_Text=itemView.findViewById(R.id.RelativeText);
            relative_Image=itemView.findViewById(R.id.RelativeImage);
            relative_File=itemView.findViewById(R.id.RelativeFile);
            linearRelativeAudioVoice=itemView.findViewById(R.id.LinearRelativeAudioVoice);
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

    private void DownloadFile(String fileName,String downloadUrlOfImage,String extension){
        progressDialog = new ProgressDialog(context);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        try {
            DownloadManager downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(downloadUrlOfImage);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(fileName)
                    .setMimeType("file/pdf")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, File.separator + fileName +"."+extension);
            downloadManager.enqueue(request);
            progressDialog.dismiss();
            Toast.makeText(context, "File Downloaded", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            progressDialog.dismiss();
            Toast.makeText(context, "Images download failed", Toast.LENGTH_SHORT).show();
        }
    }
}
