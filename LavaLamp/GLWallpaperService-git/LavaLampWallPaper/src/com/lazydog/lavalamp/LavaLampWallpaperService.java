//package com.glwallpaperservice.testing.wallpapers.nehe.lesson08;
package com.lazydog.lavalamp;

import net.rbgrn.android.glwallpaperservice.GLWallpaperService;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

//
import android.util.Log;


public class LavaLampWallpaperService extends GLWallpaperService {
        //we just use 'default preferences' now:
	//public static final String PREFERENCES = "nu.danielsundberg.droid.spinbox.livewallpaper";
 //       public static final String PREFERENCES = "com.lazydog.lavalamp";

	private SensorManager sm;

	public LavaLampWallpaperService() {
		super();
	}

	public Engine onCreateEngine() {
		MyEngine engine = new MyEngine();
		return engine;
	}

	class MyEngine extends GLEngine implements
			SharedPreferences.OnSharedPreferenceChangeListener,
			SensorEventListener {
		//NeheLesson08Renderer renderer;
                LavaLampRenderer renderer;

		public MyEngine() {
			super();
			// handle prefs, other initialization
                        //XXX:CHANGEME
			//renderer = new NeheLesson08Renderer();
                        renderer = new LavaLampRenderer(getBaseContext());
			//renderer.setContext(getBaseContext());
			setRenderer(renderer);
			setRenderMode(RENDERMODE_CONTINUOUSLY);
		}

		public void onDestroy() {
			// Unregister this as listener
			sm.unregisterListener(this);

			// Kill renderer
			if (renderer != null) {
				renderer.release(); // assuming yours has this method - it should!
			}
			renderer = null;

			setTouchEventsEnabled(false);

			super.onDestroy();
		}

		@Override
		public void onTouchEvent(MotionEvent event) {
			super.onTouchEvent(event);
			renderer.onTouchEvent(event);
		}

		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);

			// Add touch events
			setTouchEventsEnabled(true);

			// Get sensormanager and register as listener.
			sm = (SensorManager) getSystemService(SENSOR_SERVICE);
			Sensor orientationSensor = sm.getDefaultSensor(SensorManager.SENSOR_ORIENTATION);
			sm.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_GAME);
		}

		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
Log.v(this.getClass().getName(),"LavaLampWallPaperService.onSharedPreferenceChanged");
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		public void onSensorChanged(SensorEvent event) {
		}
	}
}
