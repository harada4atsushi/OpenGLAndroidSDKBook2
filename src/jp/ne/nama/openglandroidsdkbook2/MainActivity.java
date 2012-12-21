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

	private Button mRetryButton; // ���g���C�{�^��
	private MyBgm mBgm;
	private MyRenderer mRenderer;
	private long mPauseTime = 0L;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// �t���X�N���[��
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		// �f�o�b�O���[�h�ł��邩�𔻒肷��
		try {
			PackageManager pm = getPackageManager();
			ApplicationInfo ai = pm.getApplicationInfo(getPackageName(), 0);
			Global.isDebuggable = (ApplicationInfo.FLAG_DEBUGGABLE == 
				(ai.flags & ApplicationInfo.FLAG_DEBUGGABLE));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		Global.mainActivity = this;
		
		this.mRenderer = new MyRenderer(this); // MyRenderer�̐���
		// MyGLSurfaceView�̐���
		MyGLSurfaceView glSurfaceView = new MyGLSurfaceView(this);
		// GLSurfaceView��MyRenderer��K�p
		glSurfaceView.setRenderer(mRenderer);
		
		setContentView(glSurfaceView); // �r���[��GLSurfaceView�Ɏw��
		
		// �{�^���̃��C�A�E�g
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		params.setMargins(0, 150, 0, 0);
		// �{�^���̍쐬
		this.mRetryButton = new Button(this);
		this.mRetryButton.setText("Retry");
		hideRetryButton();
		addContentView(mRetryButton, params);
		// �C�x���g�̒ǉ�
		this.mRetryButton.setOnClickListener(
			new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					hideRetryButton();
					mRenderer.startNewGame();
				}
			}
		);
		// MyBgm�̐���
		this.mBgm = new MyBgm(this);
		// �ۑ�������Ԃɖ߂�
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
		// �L�[�������ꂽ
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
				case KeyEvent.KEYCODE_BACK: // Back�{�^��
					return false;
				default:
			}
		}
		return super.dispatchKeyEvent(event);
	}
	
	// ���g���C�{�^����\������
	public void showRetryButton() {
		mRetryButton.setVisibility(View.VISIBLE);
	}
	
	// ���g���C�{�^�����\���ɂ���
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
		mBgm.start(); // BGM�Đ�
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mBgm.stop(); // BGM��~
		// �e�N�X�`�����폜����
		GL10 gl = Global.gl;
		TextureManager.deleteAll(gl);
		
		mPauseTime = System.currentTimeMillis();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState); // ��Ԃ�ۑ�����
		// �J�n����
		outState.putLong("startTime", mRenderer.getStartTime());
		// onPause��������
		outState.putLong("pauseTime", System.currentTimeMillis());
		outState.putInt("score", mRenderer.getScore()); // �X�R�A
	}
}
