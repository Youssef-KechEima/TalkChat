package youssef.kecheima.topchat_v12.Main;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import youssef.kecheima.topchat_v12.Auth.LoginActivity;
import youssef.kecheima.topchat_v12.R;

public class MainActivity extends AppCompatActivity {
    protected int SPLASH_SCREEN=3000;
    protected boolean _active=true;
    private FirebaseUser firebaseUser;


    //Main Methode
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusBar_and_actionBar_Tool();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();


        Thread splashThread = new Thread() {

            @Override
            public void run() {
                try {
                    int waited=0;
                    while (_active && (waited<SPLASH_SCREEN)){
                        sleep(100);
                        if(_active){
                            waited+=100;
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if(firebaseUser!=null){
                        Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }
            }
        };
        splashThread.start();
    }

    //StatusBar and ActionBar
    @SuppressLint("NewApi")
    private void statusBar_and_actionBar_Tool() {
        Window window=MainActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.grayprofond));
    }


}