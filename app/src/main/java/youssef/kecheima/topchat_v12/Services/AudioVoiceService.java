package youssef.kecheima.topchat_v12.Services;

import android.content.ContentResolver;
import android.content.Context;
import android.media.MediaPlayer;

import java.io.IOException;

public class AudioVoiceService {
    private Context context;
    private MediaPlayer tmpmediaPlayer;
    private OnPlayCallBack onPlayCallBack;

    public AudioVoiceService(Context context) {
        this.context = context;
    }
    public  void playAudioVoiceFromUrl(String url,final OnPlayCallBack onPlayCallBack){
        if(tmpmediaPlayer!=null){
            tmpmediaPlayer.stop();
        }
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();

            tmpmediaPlayer=mediaPlayer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                onPlayCallBack.OnFinished();
            }
        });
    }

    public interface OnPlayCallBack{
        void OnFinished();
    }

}
