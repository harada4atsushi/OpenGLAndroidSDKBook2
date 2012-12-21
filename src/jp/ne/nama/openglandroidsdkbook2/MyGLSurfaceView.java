package jp.ne.nama.openglandroidsdkbook2;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class MyGLSurfaceView extends GLSurfaceView {
	
	// âÊñ ÉTÉCÉY
	private float mWidth;
	private float mHeight;
	
	// MyRendererÇï€éù
	private MyRenderer mMyRenderer;
	
	public MyGLSurfaceView(Context context) {
		super(context);
		setFocusable(true);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		super.surfaceChanged(holder, format, w, h);
		this.mWidth = w;
		this.mHeight = h;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = (event.getX() / (float)mWidth) * 2.0f - 1.0f;
		float y = (event.getY() / (float)mHeight) * -3.0f + 1.5f;
		mMyRenderer.touched(x, y);
		return false;
	}
	
	@Override
	public void setRenderer(Renderer renderer) {
		super.setRenderer(renderer);
		this.mMyRenderer = (MyRenderer)renderer;
	}
}
