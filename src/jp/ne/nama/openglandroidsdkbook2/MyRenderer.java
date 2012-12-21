package jp.ne.nama.openglandroidsdkbook2;

import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.Log;

public class MyRenderer implements GLSurfaceView.Renderer {

	public static final int TARGET_NUM = 10;
	private static final int GAME_INTERVAL = 60;
	
	// コンテキスト
	private Context mContext;
	
	private int mWidth;
	private int mHeight;
	
	// テクスチャ
	private int mBgTexture; // 背景テクスチャ
	private int mTargetTexture; // 標的用テクスチャ
	private int mNumberTexture; // 数字用テクスチャ
	private int mGameOverTexture; // ゲームオーバー用テクスチャ
	private int mParticleTexture; // パーティクル用テクスチャ
	
	private Handler mHandler = new Handler();
	
	// 標的
	private MyTarget[] mTargets = new MyTarget[TARGET_NUM];
	
	// 得点
	private int mScore;
	
	// 開始時間
	private long mStartTime;
	
	private boolean mGameOverFlag;
	
	// 効果音
	private MySe mSe;
	
	// パーティクルシステム
	private ParticleSystem mParticleSystem;
	
	private long mFpsCountStartTime = System.currentTimeMillis();
	private int mFramesInSecond = 0;
	private int mFps = 0;
	
	public MyRenderer(Context context) {
		this.mContext = context;
		this.mParticleSystem = new ParticleSystem(300, 30);
		this.mSe = new MySe(context);
		startNewGame();
	}
	
	public void startNewGame() {
		Random rand = Global.rand;
		// 標的の状態を初期化する
		for (int i = 0; i < TARGET_NUM; i++) {
			// 標的の初期座標は(-1.0～1.0, -1.0～1.0)の間のランダムな地点にする
			float x = rand.nextFloat() * 2.0f - 1.0f;
			float y = rand.nextFloat() * 2.0f - 1.0f;
			
			// 角度をランダムに設定する
			float angle = rand.nextInt(360);
			
			// 標的の大きさを0.25～0.5の間でランダムに決定する
			float size = rand.nextFloat() * 0.25f + 0.25f;
			
			// 標的の移動速度を0.01～0.02の間でランダムに決定する
			float speed = rand.nextFloat() * 0.01f + 0.01f;
			
			// 標的の旋回角度を-2.0ｆ～2.0ｆの間でランダムに決定する
			float turnAngle = rand.nextFloat() * 4.0f - 2.0f;
			mTargets[i] = new MyTarget(x, y, angle, size, speed, turnAngle);
		}
		this.mScore = 0;
		this.mStartTime = System.currentTimeMillis();
		this.mGameOverFlag = false;
	}
	
	public void renderMain(GL10 gl) {
		// 経過時間を計算する
		int passedTime = (int)(System.currentTimeMillis() - mStartTime) / 1000;
		int remainTime = GAME_INTERVAL - passedTime;
		if (remainTime <= 0) {
			remainTime = 0;
			if (!mGameOverFlag) {
				mGameOverFlag = true;
				// Global.mainActivity.showRetryButton()を
				// UIスレッド上で実行する
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						Global.mainActivity.showRetryButton();
					}
				});
			}
		}
		Log.i(getClass().toString(), "passed time = " + passedTime);
		Random rand = Global.rand;
		MyTarget[] targets = mTargets;
		// すべての標的を1つずつ動かす
		for (int i = 0; i < TARGET_NUM; i++) {
			// ランダムなタイミングで方向転換するようにする
			if (rand.nextInt(100) == 0) {
				// 旋回する角度を -2.0～2.0の間でランダムに設定する
				targets[i].mTurnAngle = rand.nextFloat() * 4.0f - 2.0f;
			}
			// 標的を旋回
			targets[i].mAngle = targets[i].mAngle + targets[i].mTurnAngle;
			// 標的を動かす
			targets[i].move();
			// パーティクルを使って軌跡を描画する
			float moveX = (rand.nextFloat() - 0.5f) * 0.01f;
			float moveY = (rand.nextFloat() - 0.5f) * 0.01f;
			mParticleSystem.add(targets[i].mX, targets[i].mY, 0.1f, moveX, moveY);
		}
		// 背景を描画する
		GraphicUtil.drawTexture(gl, 0.0f, 0.0f, 2.0f, 3.0f, mBgTexture, 1.0f, 1.0f, 1.0f, 1.0f);
		
		// パーティクルを描画する
		mParticleSystem.update();
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
		mParticleSystem.draw(gl, mParticleTexture);
		
		// 標的を描画する
		for (int i = 0; i < TARGET_NUM; i++) {
			targets[i].draw(gl, mTargetTexture);
		}
		gl.glDisable(GL10.GL_BLEND);
		
		// 得点を描画する
		GraphicUtil.drawNumbers(gl, -0.5f, 1.25f, 0.125f, 0.125f,
			mNumberTexture, mScore, 8, 1.0f, 1.0f, 1.0f, 1.0f);
		// 残り時間を描画する
		GraphicUtil.drawNumbers(gl, 0.5f, 1.2f, 0.4f, 0.4f,
			mNumberTexture, remainTime, 2, 1.0f, 1.0f, 1.0f, 1.0f);
		// ゲームオーバーテクスチャを描画する
		if (mGameOverFlag) {
			GraphicUtil.drawTexture(gl, 0.0f, 0.0f, 2.0f, 0.5f,
				mGameOverTexture, 1.0f, 1.0f, 1.0f, 1.0f);
		}
		
		// FPSを表示する
		if (Global.isDebuggable) {
			// 現在時間を取得する
			long nowTime = System.currentTimeMillis();
			// 現在時間との差分を計算する
			long difference = nowTime - mFpsCountStartTime;
			// 1秒経過していた場合は、フレーム数のカウント終了
			if (difference >= 1000) {
				mFps = mFramesInSecond;
				mFramesInSecond = 0;
				mFpsCountStartTime = nowTime;
			}
			mFramesInSecond++; // フレーム数をカウント
			GraphicUtil.drawNumbers(gl, -0.5f, -1.25f, 0.2f, 0.2f,
				mNumberTexture, mFps, 2, 1.0f, 1.0f, 1.0f, 1.0f);
		}
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		
		gl.glViewport(0,  0, mWidth, mHeight);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		
		gl.glLoadIdentity();
		gl.glOrthof(-1.0f, 1.0f, -1.5f, 1.5f, 0.5f, -0.5f);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		renderMain(gl);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		this.mWidth = width;
		this.mHeight = height;
		
		Global.gl = gl; // GLコンテキストを保持する
		// テクスチャをロードする
		loadTextures(gl);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {}

	public void touched(float x, float y) {
		Log.i(getClass().toString(), String.format("touched! x = %f, y = %f", x, y));
		MyTarget[] targets = mTargets;
		
		Random rand = Global.rand;
		if (!mGameOverFlag) {
			for (int i = 0; i < TARGET_NUM; i++) {
				// 距離が標的のサイズ(半径)より小さければ当たったことにする
				if (targets[i].isPointInside(x, y)) {
					// パーティクルを放出する
					for (int j = 0; j < 40; j++) {
						float moveX = (rand.nextFloat() - 0.5f) * 0.05f;
						float moveY = (rand.nextFloat() - 0.5f) * 0.05f;
						mParticleSystem.add(targets[i].mX, targets[i].mY, 0.2f, moveX, moveY);
					}
					// 標的をランダムな位置に移動する
					float dist = 2.0f;
					float theta = Global.rand.nextFloat() * 360.0f / 180.0f * (float)Math.PI;
					targets[i].mX = (float)Math.cos(theta) * dist;
					targets[i].mY = (float)Math.sin(theta) * dist;
					mScore += 100;
					mSe.playHitSound();
					Log.i(getClass().toString(), "Hit!");
				}
			}
		}
	}
	
	public void subtractPausedTime(long pausedTime) {
		mStartTime += pausedTime;
	}
	
	public long getStartTime() {
		return mStartTime;
	}
	
	public int getScore() {
		return mScore;
	}
	
	public void setScore(int score) {
		mScore = score;
	}
	
	// テクスチャを読み込むメソッド
	private void loadTextures(GL10 gl) {
		Resources res = mContext.getResources();
		this.mBgTexture = GraphicUtil.loadTexture(gl, res, R.drawable.circuit);
		if (mBgTexture == 0) {
			Log.e(getClass().toString(), "load texture error! circuit");
		}
		
		this.mTargetTexture = GraphicUtil.loadTexture(gl, res, R.drawable.fly);
		if (mTargetTexture == 0) {
			Log.e(getClass().toString(), "load texture error! fly");
		}
		
		this.mNumberTexture = GraphicUtil.loadTexture(gl, res, R.drawable.number_texture);
		if (mNumberTexture == 0) {
			Log.e(getClass().toString(), "load texture error! number_texture");
		}
		
		this.mGameOverTexture = GraphicUtil.loadTexture(gl, res, R.drawable.game_over);
		if (mGameOverTexture == 0) {
			Log.e(getClass().toString(), "load texture error! game_over");
		}
		
		this.mParticleTexture = GraphicUtil.loadTexture(gl, res, R.drawable.particle_blue);
		if (mParticleTexture == 0) {
			Log.e(getClass().toString(), "load texture error! particle_blue");
		}
	}
	
}
