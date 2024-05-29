package youssef.kecheima.topchat_v12.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jsibbold.zoomage.ZoomageView;

import java.io.File;
import java.io.FileOutputStream;

import youssef.kecheima.topchat_v12.Model.Common;
import youssef.kecheima.topchat_v12.R;

public class DisplayPictureActivity extends AppCompatActivity {
    
    private ZoomageView ImageViewProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_picture);
        components();
        statusBar_and_actionBar_Tool();

        ImageViewProfile.setImageBitmap(Common.IMAGE_BITMAP);

    }


    private void components() {
        ImageViewProfile=findViewById(R.id.ImageViewProfile);
    }


    @SuppressLint("NewApi")
    private void statusBar_and_actionBar_Tool() {
        Window window= DisplayPictureActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(DisplayPictureActivity.this,R.color.black));
    }
}