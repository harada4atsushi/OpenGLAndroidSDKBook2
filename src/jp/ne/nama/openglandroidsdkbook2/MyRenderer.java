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
	
	// �R���e�L�X�g
	private Context mContext;
	
	private int mWidth;
	private int mHeight;
	
	// �e�N�X�`��
	private int mBgTexture; // �w�i�e�N�X�`��
	private int mTargetTexture; // �W�I�p�e�N�X�`��
	private int mNumberTexture; // �����p�e�N�X�`��
	private int mGameOverTexture; // �Q�[���I�[�o�[�p�e�N�X�`��
	private int mParticleTexture; // �p�[�e�B�N���p�e�N�X�`��
	
	private Handler mHandler = new Handler();
	
	// �W�I
	private MyTarget[] mTargets = new MyTarget[TARGET_NUM];
	
	// ���_
	private int mScore;
	
	// �J�n����
	private long mStartTime;
	
	private boolean mGameOverFlag;
	
	// ���ʉ�
	private MySe mSe;
	
	// �p�[�e�B�N���V�X�e��
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
		// �W�I�̏�Ԃ�����������
		for (int i = 0; i < TARGET_NUM; i++) {
			// �W�I�̏������W��(-1.0�`1.0, -1.0�`1.0)�̊Ԃ̃����_���Ȓn�_�ɂ���
			float x = rand.nextFloat() * 2.0f - 1.0f;
			float y = rand.nextFloat() * 2.0f - 1.0f;
			
			// �p�x�������_���ɐݒ肷��
			float angle = rand.nextInt(360);
			
			// �W�I�̑傫����0.25�`0.5�̊ԂŃ����_���Ɍ��肷��
			float size = rand.nextFloat() * 0.25f + 0.25f;
			
			// �W�I�̈ړ����x��0.01�`0.02�̊ԂŃ����_���Ɍ��肷��
			float speed = rand.nextFloat() * 0.01f + 0.01f;
			
			// �W�I�̐���p�x��-2.0���`2.0���̊ԂŃ����_���Ɍ��肷��
			float turnAngle = rand.nextFloat() * 4.0f - 2.0f;
			mTargets[i] = new MyTarget(x, y, angle, size, speed, turnAngle);
		}
		this.mScore = 0;
		this.mStartTime = System.currentTimeMillis();
		this.mGameOverFlag = false;
	}
	
	public void renderMain(GL10 gl) {
		// �o�ߎ��Ԃ��v�Z����
		int passedTime = (int)(System.currentTimeMillis() - mStartTime) / 1000;
		int remainTime = GAME_INTERVAL - passedTime;
		if (remainTime <= 0) {
			remainTime = 0;
			if (!mGameOverFlag) {
				mGameOverFlag = true;
				// Global.mainActivity.showRetryButton()��
				// UI�X���b�h��Ŏ��s����
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
		// ���ׂĂ̕W�I��1��������
		for (int i = 0; i < TARGET_NUM; i++) {
			// �����_���ȃ^�C�~���O�ŕ����]������悤�ɂ���
			if (rand.nextInt(100) == 0) {
				// ���񂷂�p�x�� -2.0�`2.0�̊ԂŃ����_���ɐݒ肷��
				targets[i].mTurnAngle = rand.nextFloat() * 4.0f - 2.0f;
			}
			// �W�I�����
			targets[i].mAngle = targets[i].mAngle + targets[i].mTurnAngle;
			// �W�I�𓮂���
			targets[i].move();
			// �p�[�e�B�N�����g���ċO�Ղ�`�悷��
			float moveX = (rand.nextFloat() - 0.5f) * 0.01f;
			float moveY = (rand.nextFloat() - 0.5f) * 0.01f;
			mParticleSystem.add(targets[i].mX, targets[i].mY, 0.1f, moveX, moveY);
		}
		// �w�i��`�悷��
		GraphicUtil.drawTexture(gl, 0.0f, 0.0f, 2.0f, 3.0f, mBgTexture, 1.0f, 1.0f, 1.0f, 1.0f);
		
		// �p�[�e�B�N����`�悷��
		mParticleSystem.update();
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
		mParticleSystem.draw(gl, mParticleTexture);
		
		// �W�I��`�悷��
		for (int i = 0; i < TARGET_NUM; i++) {
			targets[i].draw(gl, mTargetTexture);
		}
		gl.glDisable(GL10.GL_BLEND);
		
		// ���_��`�悷��
		GraphicUtil.drawNumbers(gl, -0.5f, 1.25f, 0.125f, 0.125f,
			mNumberTexture, mScore, 8, 1.0f, 1.0f, 1.0f, 1.0f);
		// �c�莞�Ԃ�`�悷��
		GraphicUtil.drawNumbers(gl, 0.5f, 1.2f, 0.4f, 0.4f,
			mNumberTexture, remainTime, 2, 1.0f, 1.0f, 1.0f, 1.0f);
		// �Q�[���I�[�o�[�e�N�X�`����`�悷��
		if (mGameOverFlag) {
			GraphicUtil.drawTexture(gl, 0.0f, 0.0f, 2.0f, 0.5f,
				mGameOverTexture, 1.0f, 1.0f, 1.0f, 1.0f);
		}
		
		// FPS��\������
		if (Global.isDebuggable) {
			// ���ݎ��Ԃ��擾����
			long nowTime = System.currentTimeMillis();
			// ���ݎ��ԂƂ̍������v�Z����
			long difference = nowTime - mFpsCountStartTime;
			// 1�b�o�߂��Ă����ꍇ�́A�t���[�����̃J�E���g�I��
			if (difference >= 1000) {
				mFps = mFramesInSecond;
				mFramesInSecond = 0;
				mFpsCountStartTime = nowTime;
			}
			mFramesInSecond++; // �t���[�������J�E���g
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
		
		Global.gl = gl; // GL�R���e�L�X�g��ێ�����
		// �e�N�X�`�������[�h����
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
				// �������W�I�̃T�C�Y(���a)��菬������Γ����������Ƃɂ���
				if (targets[i].isPointInside(x, y)) {
					// �p�[�e�B�N������o����
					for (int j = 0; j < 40; j++) {
						float moveX = (rand.nextFloat() - 0.5f) * 0.05f;
						float moveY = (rand.nextFloat() - 0.5f) * 0.05f;
						mParticleSystem.add(targets[i].mX, targets[i].mY, 0.2f, moveX, moveY);
					}
					// �W�I�������_���Ȉʒu�Ɉړ�����
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
	
	// �e�N�X�`����ǂݍ��ރ��\�b�h
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
