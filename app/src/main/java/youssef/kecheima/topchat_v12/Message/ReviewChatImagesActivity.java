package youssef.kecheima.topchat_v12.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.jsibbold.zoomage.ZoomageView;

import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import youssef.kecheima.topchat_v12.Model.Common;
import youssef.kecheima.topchat_v12.R;
import youssef.kecheima.topchat_v12.Settings.DisplayPictureActivity;

import static android.bluetooth.BluetoothGattCharacteristic.PERMISSION_WRITE;
import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class ReviewChatImagesActivity extends AppCompatActivity {
    private ZoomageView imageViewChat;
    private ImageButton download;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_chat_images);
        copoments();
        statusBar_and_actionBar_Tool();
        imageViewChat.setImageBitmap(Common.IMAGE_BITMAP);
        Intent intent = getIntent();
        String url = intent.getStringExtra("Image_Url");
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddmmyyyyhhmmss");
                String date =simpleDateFormat.format(new Date());
                DownloadImageView(date,url);
            }
        });
    }

    private void copoments() {
        imageViewChat = findViewById(R.id.ImageViewChat);
        download = findViewById(R.id.DownloadUrl);
    }

    @SuppressLint("NewApi")
    private void statusBar_and_actionBar_Tool() {
        Window window = ReviewChatImagesActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(ReviewChatImagesActivity.this, R.color.black));
    }

    private void DownloadImageView(String fileName,String downloadUrlOfImage){
        progressDialog = new ProgressDialog(ReviewChatImagesActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        try {
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(downloadUrlOfImage);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(fileName)
                    .setMimeType("image/jpeg")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, File.separator + fileName + ".jpg");
            downloadManager.enqueue(request);
            progressDialog.dismiss();
            Toast.makeText(this, "Image Downloaded", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            progressDialog.dismiss();
            Toast.makeText(this, "Images download failed", Toast.LENGTH_SHORT).show();
        }
    }
}