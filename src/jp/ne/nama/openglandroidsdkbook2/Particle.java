package jp.ne.nama.openglandroidsdkbook2;

import javax.microedition.khronos.opengles.GL10;

public class Particle {
	
	public float mX;
	public float mY;
	public float mSize;
	public boolean mIsActive;
	public float mMoveX; // 1フレームあたりのX軸方向の移動量
	public float mMoveY; // 1フレームあたりのY軸方向の移動量
	public int mFrameNumber; // 生成からの時間（フレーム数）
	public int mLifeSpan; // 寿命（フレーム数）
	
	public Particle() {
		this.mX = 0.0f;
		this.mY = 0.0f;
		this.mSize = 1.0f;
		this.mIsActive = false;
		this.mMoveX = 0.0f;
		this.mMoveY = 0.0f;
		this.mFrameNumber = 0;
		this.mLifeSpan = 60;
	}
	
	public void update() {
		mFrameNumber++;
		if (mFrameNumber >= mLifeSpan) mIsActive = false;
		mX += mMoveX;
		mY += mMoveY;
		//mMoveY -= 0.001f;
	}
	
	public void draw(GL10 gl, int texture) {
		float lifePercentage = (float)mFrameNumber / (float)mLifeSpan;
		float alpha = 1.0f;
		if (lifePercentage <= 0.5f) {
			alpha = lifePercentage * 2.0f;
		} else {
			alpha = 1.0f - (lifePercentage - 0.5f) * 2.0f;
		}
		GraphicUtil.drawTexture(gl, mX, mY, mSize, mSize, texture, 1.0f, 1.0f, 1.0f, alpha);
	}
	
}
