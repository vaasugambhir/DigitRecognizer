package com.example.digitrecognizer;

import android.content.Context;
import android.media.MediaPlayer;

public class VoicePlayer {

    private MediaPlayer player;

    public VoicePlayer(Context context, int number) {
        int musicFile = 0;
        switch (number) {
            case 0: {
                musicFile =  R.raw.zero;
                break;
            }
            case 1: {
                musicFile =  R.raw.one;
                break;
            }
            case 2: {
                musicFile =  R.raw.two;
                break;
            }
            case 3: {
                musicFile =  R.raw.three;
                break;
            }
            case 4: {
                musicFile =  R.raw.four;
                break;
            }
            case 5: {
                musicFile =  R.raw.five;
                break;
            }
            case 6: {
                musicFile =  R.raw.six;
                break;
            }
            case 7: {
                musicFile =  R.raw.seven;
                break;
            }
            case 8: {
                musicFile =  R.raw.eight;
                break;
            }
            case 9: {
                musicFile =  R.raw.nine;
                break;
            }
        }
        if (musicFile != 0)
            player = MediaPlayer.create(context, musicFile);
    }

    public void play() {
        player.start();
    }

}
