package jp.ne.nama.openglandroidsdkbook2;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class Global {
	// GLコンテキストを保持する変数
	public static GL10 gl;
	
	// ランダムな値を生成する
	public static Random rand = new Random(System.currentTimeMillis());
	
	// MainActivity
	public static MainActivity mainActivity;
	
	// デバッグモードであるか
	public static boolean isDebuggable;
}
