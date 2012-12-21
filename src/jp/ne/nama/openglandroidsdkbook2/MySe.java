package jp.ne.nama.openglandroidsdkbook2;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class MySe {
	private SoundPool mSoundPool; // SoundPool
	private int mHitSound; // 読み込んだ効果音オブジェクト
	
	public MySe(Context context) {
		this.mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		this.mHitSound = mSoundPool.load(context, R.raw.explosion, 1);
	}
	
	public void playHitSound() {
		mSoundPool.play(mHitSound, 1.0f, 1.0f, 1, 0, 1.0f);
	}
}
