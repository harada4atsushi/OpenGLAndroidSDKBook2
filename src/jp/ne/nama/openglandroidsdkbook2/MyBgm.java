package jp.ne.nama.openglandroidsdkbook2;

import android.content.Context;
import android.media.MediaPlayer;

public class MyBgm {
	private MediaPlayer mBgm;
	
	public MyBgm(Context context) {
		// BGMファイルを読み込む
		this.mBgm = MediaPlayer.create(context, R.raw.master1);
		this.mBgm.setLooping(true); // ループするようにする
		this.mBgm.setVolume(1.0f, 1.0f); // 左右のボリュームを最大にする
	}
	
	// BGMを再生する
	public void start() {
		if (!mBgm.isPlaying()) {
			mBgm.seekTo(0);
			mBgm.start();
		}
	}
	
	// BGMを停止する
	public void stop() {
		if (mBgm.isPlaying()) {
			mBgm.stop();
			mBgm.prepareAsync();
		}
	}
}
