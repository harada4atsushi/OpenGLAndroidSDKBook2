package jp.ne.nama.openglandroidsdkbook2;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class Global {
	// GL�R���e�L�X�g��ێ�����ϐ�
	public static GL10 gl;
	
	// �����_���Ȓl�𐶐�����
	public static Random rand = new Random(System.currentTimeMillis());
	
	// MainActivity
	public static MainActivity mainActivity;
	
	// �f�o�b�O���[�h�ł��邩
	public static boolean isDebuggable;
}
