//package net.markguerra.android.glwallpapertest;
package com.lazydog.lavalamp;

//import com.glwallpaperservice.testing.wallpapers.nehe.lesson08.NeheLesson08Renderer;


import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.ViewGroup;

//
import android.view.MotionEvent;

//
import  android.util.Log;

//
import android.content.Intent;

//
import android.widget.Toast;


public class GalleryActivity extends Activity {
	GLSurfaceView glView;
	LavaLampRenderer renderer;

        //for double-click:
	public static final long TIMEOUT = 400;
        public long m_LastActionUp = 0;

	@Override
	public boolean onTouchEvent(MotionEvent event)  {
		super.onTouchEvent(event);

  switch (event.getAction() & MotionEvent.ACTION_MASK)
    { 
      case MotionEvent.ACTION_UP:

        if (event.getEventTime() - m_LastActionUp < TIMEOUT )  {
	//	Log.v(this.getClass().getName(),"pointer up");
		Intent i = new Intent(this, LavaLampSettingsActivity.class);
		startActivity(i);
                m_LastActionUp = 0;
		break;
        }
       	m_LastActionUp=event.getEventTime();

		
    }

		renderer.onTouchEvent(event);
   		return true;
   }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		//Instantiate OpenGL drawing surface and hold a reference
		ViewGroup container = (ViewGroup)findViewById(R.id.container);
		glView = new GLSurfaceView(this);

		int redSize = 8;
		int greenSize = 8;
		int blueSize = 8;
		int alphaSize = 8;
		int depthSize = 16;
		int stencilSize = 0;
		glView.setEGLConfigChooser(redSize, greenSize, blueSize, alphaSize, depthSize, stencilSize);

                //XXX:CHANGEME
		//NeheLesson08Renderer renderer = new NeheLesson08Renderer();
                renderer = new LavaLampRenderer(this);
		glView.setRenderer(renderer);

		//Put the container on the screen for the world to see.
		container.addView(glView);

		//show some help
		Toast.makeText(getApplicationContext(), R.string.help_msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Allows GLSurfaceView to respond to the Resume event
	 */
	@Override
	protected void onResume() {
		super.onResume();
		glView.onResume();
	}

	/**
	 * Allows GLSurfaceView to respond to the Pause event
	 */
	@Override
	protected void onPause() {
		super.onPause();
		glView.onPause();
	}
}
