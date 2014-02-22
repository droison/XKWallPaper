package com.xkwallpaper.ui;

import java.io.File;

import com.xkwallpaper.baidumtj.BaiduMTJActivity;
import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.http.base.Paper;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayActivity extends BaiduMTJActivity{

	private Paper paper;
	private String path;
	private VideoView vv;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// 实现全屏播放
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.video_play);
		super.onCreate(savedInstanceState);

		vv = (VideoView) findViewById(R.id.surface_view);

		Bundle bundle = this.getIntent().getExtras();
		paper = (Paper) bundle.getSerializable("paper");

		path = AppConstants.APP_FILE_PATH + "/download/" + paper.getId() + ".mp4";
	
		MediaController mController = new MediaController(this);  
		mController.show(1);
		mController.setVisibility(View.VISIBLE);
        vv.setMediaController(mController); 
       
		vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				Log.v("useVideoView", "onCompletion");
				finish();
			}
		});

		if(new File(path).isFile()){
			vv.setVideoPath(path);
			
			vv.setOnPreparedListener(new OnPreparedListener(){
                @Override
                public void onPrepared(MediaPlayer mp) {

                        Log.v("v","getDuration"+vv.getDuration());
                }
        });

			
			vv.start();

		}else{
			finish();
		}	
		
		vv.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				switch (what) {

				case MediaPlayer.MEDIA_ERROR_UNKNOWN:
					Log.v("useVideoView", "MEDIA_ERROR_UNKNOWN");
					return true;

				case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
					Log.v("useVideoView", "MEDIA_ERROR_SERVER_DIED");
					return true;

				case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
					Log.v("useVideoView",
							"MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK");
					return true;
				}
				return false;
			}
		});
	}
	
}