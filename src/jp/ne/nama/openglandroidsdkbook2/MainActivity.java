package jp.ne.nama.openglandroidsdkbook2;

import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

public class MainActivity extends Activity {

	private Button mRetryButton; // リトライボタン
	private MyBgm mBgm;
	private MyRenderer mRenderer;
	private long mPauseTime = 0L;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// フルスクリーン
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		// デバッグモードであるかを判定する
		try {
			PackageManager pm = getPackageManager();
			ApplicationInfo ai = pm.getApplicationInfo(getPackageName(), 0);
			Global.isDebuggable = (ApplicationInfo.FLAG_DEBUGGABLE == 
				(ai.flags & ApplicationInfo.FLAG_DEBUGGABLE));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		Global.mainActivity = this;
		
		this.mRenderer = new MyRenderer(this); // MyRendererの生成
		// MyGLSurfaceViewの生成
		MyGLSurfaceView glSurfaceView = new MyGLSurfaceView(this);
		// GLSurfaceViewにMyRendererを適用
		glSurfaceView.setRenderer(mRenderer);
		
		setContentView(glSurfaceView); // ビューをGLSurfaceViewに指定
		
		// ボタンのレイアウト
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		params.setMargins(0, 150, 0, 0);
		// ボタンの作成
		this.mRetryButton = new Button(this);
		this.mRetryButton.setText("Retry");
		hideRetryButton();
		addContentView(mRetryButton, params);
		// イベントの追加
		this.mRetryButton.setOnClickListener(
			new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					hideRetryButton();
					mRenderer.startNewGame();
				}
			}
		);
		// MyBgmの生成
		this.mBgm = new MyBgm(this);
		// 保存した状態に戻す
		if (savedInstanceState != null) {
			long startTime = savedInstanceState.getLong("startTime");
			long pauseTime = savedInstanceState.getLong("pauseTime");
			int score = savedInstanceState.getInt("score");
			long pausedTime = pauseTime - startTime;
			mRenderer.subtractPausedTime(-pausedTime);
			mRenderer.setScore(score);
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// キーが押された
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
				case KeyEvent.KEYCODE_BACK: // Backボタン
					return false;
				default:
			}
		}
		return super.dispatchKeyEvent(event);
	}
	
	// リトライボタンを表示する
	public void showRetryButton() {
		mRetryButton.setVisibility(View.VISIBLE);
	}
	
	// リトライボタンを非表示にする
	public void hideRetryButton() {
		mRetryButton.setVisibility(View.INVISIBLE);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (mPauseTime != 0L) {
			long pausedTime = System.currentTimeMillis() - mPauseTime;
			mRenderer.subtractPausedTime(pausedTime);
		}
		mBgm.start(); // BGM再生
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mBgm.stop(); // BGM停止
		// テクスチャを削除する
		GL10 gl = Global.gl;
		TextureManager.deleteAll(gl);
		
		mPauseTime = System.currentTimeMillis();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState); // 状態を保存する
		// 開始時間
		outState.putLong("startTime", mRenderer.getStartTime());
		// onPauseした時間
		outState.putLong("pauseTime", System.currentTimeMillis());
		outState.putInt("score", mRenderer.getScore()); // スコア
	}
}
