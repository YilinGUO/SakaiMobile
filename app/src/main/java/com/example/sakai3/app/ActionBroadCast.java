package com.example.sakai3.app;


        import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;


/**
 * Created by haoyuxuan on 2014/7/18.
 */

public class ActionBroadCast extends BroadcastReceiver {
    SoundPool sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 4);
    int realsound;

    public void onReceive(Context context, Intent intent) {
        /* TODO Auto-generated method stub */
        realsound= sp.load(context, R.raw.music, 1);

        sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener(){
            @Override
            public void onLoadComplete(SoundPool arg0, int arg1, int arg2 ){
                sp.play(realsound,1,1,0,0,1);
                Log.e("ActionBroadCast", "New Message !");
            }
        });
    }
}

