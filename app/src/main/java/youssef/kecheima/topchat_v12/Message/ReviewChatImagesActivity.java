package youssef.kecheima.topchat_v12.Message;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.jsibbold.zoomage.ZoomageView;

import java.io.File;

import youssef.kecheima.topchat_v12.Model.Common;
import youssef.kecheima.topchat_v12.R;
import youssef.kecheima.topchat_v12.Settings.DisplayPictureActivity;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class ReviewChatImagesActivity extends AppCompatActivity {
    private ZoomageView imageViewChat;
    private ImageButton download;
    private String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_chat_images);
        copoments();
        statusBar_and_actionBar_Tool();
        imageViewChat.setImageBitmap(Common.IMAGE_BITMAP);
        Intent intent =getIntent();
        url=intent.getStringExtra("Image_Url");
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager downloadManager = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri =Uri.parse(url);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalFilesDir(ReviewChatImagesActivity.this,DIRECTORY_DOWNLOADS,System.currentTimeMillis()+".JPG");
                downloadManager.enqueue(request);
            }
        });
    }

    private void copoments() {
        imageViewChat=findViewById(R.id.ImageViewChat);
        download=findViewById(R.id.Download);
    }
    @SuppressLint("NewApi")
    private void statusBar_and_actionBar_Tool() {
        Window window= ReviewChatImagesActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(ReviewChatImagesActivity.this,R.color.black));
    }
}