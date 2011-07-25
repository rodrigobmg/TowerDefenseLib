package br.com.jera.towerdefenselib;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.opengl.GLSurfaceView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.Toast;
import br.com.jera.androidutil.AppMainActivity;
import br.com.jera.game.StateManager;
import br.com.jera.platform.android.AndroidSurfaceView;
import br.com.jera.platform.android.JGRunnable;
import br.com.jera.resources.PropertyReader;
import br.com.jera.resources.ResourceIdRetriever;
import br.com.jera.towers.TowerProfile;

public class TDActivity extends AppMainActivity {

	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}*/

	public String getVersion() {
		String versionStr = "";
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			info = null;
		}
		if (info != null) {
			versionStr = info.versionName + "." + info.versionCode;
		}
		return versionStr;
	}

	public static void toast(final int id, final Activity context) {
		toast(id, context, Toast.LENGTH_LONG);
	}
	
	public static void toast(final int id, final Activity context, final int length) {
		context.runOnUiThread(new Runnable() {

			public void run() {
				Toast toast = Toast.makeText(context, id, length);
				toast.setGravity(Gravity.CENTER, toast.getXOffset() / 2, toast.getYOffset() / 2);
				toast.show();
			} });
	}

	public static void toast(final String str, final Activity context) {
		context.runOnUiThread(new Runnable() {

			public void run() {
				Toast toast = Toast.makeText(context, str, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, toast.getXOffset() / 2, toast.getYOffset() / 2);
				toast.show();
			} });
	}

	public GLSurfaceView startGame(ResourceIdRetriever resRet, TowerProfile[] towerProfiles, JGRunnable runnable) {
		propertyReader = new PropertyReader(this);

		if (manager == null) {
			manager = new StateManager(getVersion(), resRet, towerProfiles);
		}

		mGLSurfaceView = new AndroidSurfaceView(this, manager, runnable);
		setContentView(mGLSurfaceView);
		return mGLSurfaceView;
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGLSurfaceView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mGLSurfaceView.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	        return false;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	protected PropertyReader propertyReader;
	private AndroidSurfaceView mGLSurfaceView;
	private static StateManager manager;
}
