package com.invengo.lib.media;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.invengo.lib.diagnostics.InvengoLog;

public class SoundPlayer {
	private static final String TAG = SoundPlayer.class.getSimpleName();
	
	
	private MediaPlayer mPlayer = null;
	public SoundPlayer(Context context, int res) {
	
		mPlayer = MediaPlayer.create(context, res);
		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		
	}	
	
	public void play() {
		if(null != mPlayer) {
	  
			//ATLog.d(TAG, "mPlayer.start()");
			
			mPlayer.seekTo(0);
			mPlayer.start();
			
		} else {
			InvengoLog.e(TAG, "Failed to play the sound !!!");
		}
	}
	
	public void close() {

		if(null != mPlayer) {
			if(mPlayer.isPlaying()){
				mPlayer.stop();
			}
	  
			//ATLog.e(TAG, "mPlayer.start()");
			mPlayer.release();
			mPlayer = null;
		}
	}
}
