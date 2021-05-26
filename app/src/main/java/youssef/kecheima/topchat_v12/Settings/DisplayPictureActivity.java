package youssef.kecheima.topchat_v12.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.jsibbold.zoomage.ZoomageView;

import youssef.kecheima.topchat_v12.Model.Common;
import youssef.kecheima.topchat_v12.R;

public class DisplayPictureActivity extends AppCompatActivity {
    private ImageButton share;
    private ZoomageView ImageViewProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_picture);
        components();
        statusBar_and_actionBar_Tool();
        /*back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });*/
        ImageViewProfile.setImageBitmap(Common.IMAGE_BITMAP);
    }

    private void components() {
       // back=findViewById(R.id.Back);
        share=findViewById(R.id.Share);
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