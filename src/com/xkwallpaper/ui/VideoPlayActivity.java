/**
 * 视频播放页面
 */
package com.xkwallpaper.ui;

import com.xkwallpaper.baidumtj.BaiduMTJActivity;
import com.xkwallpaper.constants.AppConstants;
import com.xkwallpaper.http.base.Paper;
import com.xkwallpaper.ui.component.SoundView;
import com.xkwallpaper.ui.component.SoundView.OnVolumeChangedListener;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class VideoPlayActivity extends BaiduMTJActivity {

	private VideoView vv = null;
	private SeekBar seekBar = null;
	private TextView durationTextView = null;
	private TextView playedTextView = null;
	private AudioManager mAudioManager = null;

	private int maxVolume = 0;
	private int currentVolume = 0;

	private ImageButton bn2 = null;
	private ImageButton bn3 = null;
	private ImageButton bn4 = null;
	private ImageButton bn5 = null;

	private SoundView mSoundView = null;
	private PopupWindow mSoundWindow = null;

	private final static int TIME = 3000;

	private boolean isControllerShow = true;
	private boolean isPaused = false;
	private boolean isSilent = false;
	private boolean isSoundShow = false;

	private LinearLayout bottomControlLayout = null;
	private String path;

	/** Called when the activity is first created. */
	private final static int PROGRESS_CHANGED = 0;
	private final static int HIDE_CONTROLER = 1;

	Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			switch (msg.what) {

			case PROGRESS_CHANGED:

				int i = vv.getCurrentPosition();
				seekBar.setProgress(i);

				i /= 1000;
				int minute = i / 60;
				int hour = minute / 60;
				int second = i % 60;
				minute %= 60;
				playedTextView.setText(String.format("%02d:%02d:%02d", hour, minute, second));

				sendEmptyMessage(PROGRESS_CHANGED);
				break;

			case HIDE_CONTROLER:
				hideController();
				break;
			}

			super.handleMessage(msg);
		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (!isControllerShow) {
				showController();
				hideControllerDelay();
			} else {
				hideController();
			}
		}
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if (mSoundWindow.isShowing()) {
			mSoundWindow.dismiss();
		}
		super.onDestroy();
	}

	private void hideController() {
		if (mSoundWindow.isShowing()) {
			mSoundWindow.dismiss();
			isSoundShow = false;
		}
		bottomControlLayout.setVisibility(View.GONE);
		isControllerShow = false;
	}

	private void hideControllerDelay() {
		myHandler.removeMessages(HIDE_CONTROLER);
		myHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
	}

	private void showController() {
		bottomControlLayout.setVisibility(View.VISIBLE);
		isControllerShow = true;
	}

	private int findAlphaFromSound() {
		if (mAudioManager != null) {
			int alpha = currentVolume * (0xCC - 0x55) / maxVolume + 0x55;
			return alpha;
		} else {
			return 0xCC;
		}
	}

	private void updateVolume(int index) {
		if (mAudioManager != null) {
			if (isSilent) {
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
			} else {
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
			}
			currentVolume = index;
			bn5.setAlpha(findAlphaFromSound());
		}
	}

	private Paper paper;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// 实现全屏播放
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.video_play);
		super.onCreate(savedInstanceState);

		Bundle bundle = this.getIntent().getExtras();
		paper = (Paper) bundle.getSerializable("paper");

		path = AppConstants.APP_FILE_PATH + "/download/" + paper.getId() + ".mp4";

		bottomControlLayout = (LinearLayout) this.findViewById(R.id.bottomControlLayout);
		durationTextView = (TextView) this.findViewById(R.id.duration);
		playedTextView = (TextView) this.findViewById(R.id.has_played);

		mSoundView = new SoundView(this);
		mSoundView.setOnVolumeChangeListener(new OnVolumeChangedListener() {

			@Override
			public void setYourVolume(int index) {
				updateVolume(index);
				hideControllerDelay();
			}
		});

		mSoundWindow = new PopupWindow(mSoundView);

		bn2 = (ImageButton) this.findViewById(R.id.button2);
		bn3 = (ImageButton) this.findViewById(R.id.button3);
		bn4 = (ImageButton) this.findViewById(R.id.button4);
		bn5 = (ImageButton) this.findViewById(R.id.button5);

		vv = (VideoView) findViewById(R.id.vv);

		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		vv.setVideoPath(path);
		bn4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int playedTime = vv.getCurrentPosition();
				vv.seekTo(playedTime + 3000);
			}

		});

		bn3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isPaused) {
					vv.start();
					bn3.setImageResource(R.drawable.pause);
					hideControllerDelay();
				} else {
					vv.pause();
					bn3.setImageResource(R.drawable.play);
				}
				isPaused = !isPaused;

			}

		});

		bn2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int playedTime = vv.getCurrentPosition();
				vv.seekTo(playedTime - 3000);
			}

		});

		bn5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isSoundShow) {
					mSoundWindow.dismiss();
				} else {
					if (mSoundWindow.isShowing()) {
						mSoundWindow.update(15, 0, SoundView.MY_WIDTH, SoundView.MY_HEIGHT);
					} else {
						mSoundWindow.showAtLocation(vv, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 15, 0);
						mSoundWindow.update(15, 0, SoundView.MY_WIDTH, SoundView.MY_HEIGHT);
					}
				}
				isSoundShow = !isSoundShow;
				hideControllerDelay();
			}
		});

		bn5.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				if (isSilent) {
					bn5.setImageResource(R.drawable.soundenable);
				} else {
					bn5.setImageResource(R.drawable.sounddisable);
				}
				isSilent = !isSilent;
				updateVolume(currentVolume);
				hideControllerDelay();
				return true;
			}

		});

		seekBar = (SeekBar) this.findViewById(R.id.seekbar);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
				if (fromUser) {
					vv.seekTo(progress);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				myHandler.removeMessages(HIDE_CONTROLER);
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				myHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
			}
		});

		vv.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer arg0) {
				if (isControllerShow) {
					showController();
				}

				int i = vv.getDuration();
				Log.d("onCompletion", "" + i);
				seekBar.setMax(i);
				i /= 1000;
				int minute = i / 60;
				int hour = minute / 60;
				int second = i % 60;
				minute %= 60;
				durationTextView.setText(String.format("%02d:%02d:%02d", hour, minute, second));
				vv.start();
				bn3.setImageResource(R.drawable.pause);
				hideControllerDelay();
				myHandler.sendEmptyMessage(PROGRESS_CHANGED);
			}
		});

		vv.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer arg0) {
				finish();
			}
		});
	}

}