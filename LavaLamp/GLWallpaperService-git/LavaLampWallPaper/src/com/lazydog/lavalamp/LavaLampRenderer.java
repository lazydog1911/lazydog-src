package com.lazydog.lavalamp;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
//for blending:
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11ExtensionPack;



import android.opengl.GLSurfaceView;
import java.lang.Math;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

//
import android.content.Context;
import android.util.Log;


//for onTouchEvent:
import android.view.MotionEvent;
//for fetching android screen dimensions:
/*
import android.view.WindowManager;
import android.view.Display;
*/

//from nehe08tutorial:
//import com.lazydog.lavalamp.objects.Cube;


//for fetching settings:
import android.preference.Preference;
import android.preference.PreferenceManager;
import  android.content.SharedPreferences;
import java.util.HashMap; 
//for parsing settings:
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import android.widget.Toast;






public class LavaLampRenderer implements GLSurfaceView.Renderer, SharedPreferences.OnSharedPreferenceChangeListener
{
    public final static int SS_SUNLIGHT = GL10.GL_LIGHT0; 
    public final static int SS_FILLLIGHT1 = GL10.GL_LIGHT1;
    public final static int SS_FILLLIGHT2 = GL10.GL_LIGHT2;
	public final static int X_VALUE = 0;
	public final static int Y_VALUE = 1;
	public final static int Z_VALUE = 2;
	Planet m_Earth;
        LavaLamp m_Lamp;
        Box m_Box;
        //Cube m_Cube;
        //Cube cube;
        //GuideRail m_GuideRail;
        Square mSquare1;

        public float m_Horizontal, m_Vertical;
        public float m_ScreenWidth, m_ScreenHeight;
        public float m_RatioX, m_RatioY; //XX-NotUsed
        public float  m_RotatePortrait, m_RotateLandscape; // for glRotatef() ...
 
        //X-Panning:
        public float m_Panning;
        public final static float PANNING_SPEED = 10.0f; //  bigger number = slower...
        //public final static float PANNING_DECAY = 0.05f;

        //Y-Rotation:
        public float m_VerticalRotation;
        public final static float MAX_VERTICAL_ROTATION = 40.0f;

  //From old LavaLampView.java:
  public static final long  TIMEOUT = 50;
                public float m_TempHorizontal, m_TempVertical;
  public float m_InitialX = 0.0f, m_InitialY = 0.0f;
  public long m_LastEventTime = 0;
       public final static float ROTATION_SPEED = 4.0f;  // bigger number = faster

/*
//from nehe08tutorial:
      //* The initial light values 
        private float[] lightAmbient = { 0.5f, 0.5f, 0.5f, 1.0f };
        private float[] lightDiffuse = { 1.0f, 1.0f, 1.0f, 1.0f };
        private float[] lightPosition = { 0.0f, 0.0f, 2.0f, 1.0f };

        //* The buffers for our light values *
        private FloatBuffer lightAmbientBuffer;
        private FloatBuffer lightDiffuseBuffer;
        private FloatBuffer lightPositionBuffer;
*/

  //customizable color values:
  HashMap <String, Color> m_customizableColors;


 private Context context;

public void release()
{
Log.v(this.getClass().getName(),"release() called.");
  //TODO: release stuff
}

       /**
         * Override the touch screen listener.
         * 
         * React to moves and presses on the touchscreen.
         */
  public boolean onTouchEvent(MotionEvent ev)
  {

    switch (ev.getAction() & MotionEvent.ACTION_MASK)
    {
      case MotionEvent.ACTION_MOVE:
        if (ev.getEventTime() - m_LastEventTime > TIMEOUT )  {
          m_InitialX = ev.getX(0);
          m_InitialY = ev.getY(0);
       }

        //Log.v(this.getClass().getName(), "ACTION_MOVE: getX(0) = "+ev.getX(0)+" getY(0)="+ev.getY(0) +" m_InitialX="+m_InitialX+" m_InitialY="+m_InitialY); 
        m_LastEventTime=ev.getEventTime();
//        float y = ev.getY(0) - m_InitialY - (SCREEN_HEIGHT/2) ;
        float y =m_InitialY - ev.getY(0);
        m_TempVertical = (y / m_ScreenHeight);

      //apply 45 degree limit on vertical rotation:
      float verticalRotation = (m_Vertical + m_TempVertical) * ROTATION_SPEED;
      if ( verticalRotation > -MAX_VERTICAL_ROTATION && verticalRotation < MAX_VERTICAL_ROTATION ) {
        m_Vertical = m_Vertical + m_TempVertical;
        m_VerticalRotation = verticalRotation;
//DEBUGGING:
//Log.v(this.getClass().getName(), "m_VerticalRotation = "+m_VerticalRotation);
        //mRenderer.setAngle ( (y / (SCREEN_HEIGHT/2)) * MAX_ANGLE );
      } 

	float x =m_InitialX - ev.getX(0);
        m_TempHorizontal = (x / m_ScreenWidth);
         
        m_Horizontal = m_Horizontal + m_TempHorizontal;
        m_Panning = m_Horizontal / PANNING_SPEED;

        break;
//      case MotionEvent.ACTION_DOWN: break //initial press...
    }

    return true;
  }




/*
public void setPanning(float panning)
{
  m_Panning = panning;
  Log.v(this.getClass().getName(), "setPanning: m_Panning="+m_Panning);
}


public void setRocking(float rocking)
{
Log.v(this.getClass().getName(), "setRocking: rocking="+rocking);
m_Rocking = rocking;
m_GuideRail.setT(m_Rocking);
}
*/


/*
public void setContext(Context context)
{
this.context = (Context)context;
}
*/

public void setDimensions(float width, float height)
{
  m_ScreenWidth = width; 
  m_ScreenHeight = height;

  if ( m_ScreenWidth > m_ScreenHeight )  //landscape mode: not sure if works or reached:
  {
    m_RatioX = 1.1f * width/height;
    m_RatioY = 1.1f;
    m_RotatePortrait = 0.0f;
    m_RotateLandscape = 1.0f;
    mSquare1.setXYFactors(m_RatioX,m_RatioY);
  }
  else  //portrait mode:
  {
    m_RatioX = 1.1f;
    m_RatioY = 1.1f * height/width;
    m_RotatePortrait = -1.0f;
    m_RotateLandscape = 0.0f;
    mSquare1.setXYFactors(m_RatioX,m_RatioY);
  }

}

public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
{
  Log.v (this.getClass().getName(),"Renderer: onSharedPreferenceChanged()!");
//reload preferences
loadColorsFromPreferences();
//set colors:
mSquare1.setColors(m_customizableColors.get("prefColorTopLeft"),m_customizableColors.get("prefColorTopRight"),m_customizableColors.get("prefColorBottomRight"),m_customizableColors.get("prefColorBottomLeft"));
m_Lamp.setColor(m_customizableColors.get("prefColorBlobs"));
}


  public float hexToFloat(String hex)
  {
      if (hex == null)  return 0;

      int value = Integer.parseInt(hex, 16);
      float f = (float) ( (float)(value)/256.0f );

      System.out.println(hex+" -> "+value+" -> "+f);

      return f;
  }


public Color rgbaToColor(String rgba)
{
  //not sure if this is the correct way to do rgba -> 4 floats,no matter:
  if (rgba == null)  return null;

  Pattern c = Pattern.compile("(\\p{XDigit}{2})");
  Matcher m = c.matcher(rgba);

  Color color = new Color();

  try {
    m.find(); color.r=hexToFloat(m.group());
    m.find(); color.g=hexToFloat(m.group());
    m.find(); color.b=hexToFloat(m.group());
    m.find(); color.a=hexToFloat(m.group());
  } catch (IllegalStateException e)  { //...
    Log.v(this.getClass().getName(), "cant parse string: "+rgba);
    return null;
  }

  return color;
}

public void loadColorsFromPreferences() { 

m_customizableColors = new HashMap <String, Color>();
//hard-coded app defaults:
final float alpha = 0.5f;
final float intensity = 0.7f;
Color colorTopLeft= new Color(0.0f, 0.0f, 0.75f, 0.75f);
Color colorTopRight = new Color(intensity, intensity, 0.0f, alpha);
Color colorBottomRight = new Color(0.0f, 0.0f, 0.875f, 0.875f);
Color colorBottomLeft = new Color(intensity, 0.0f, 0.0f, alpha);


m_customizableColors.put("prefColorTopLeft", colorTopLeft);
m_customizableColors.put("prefColorTopRight", colorTopRight);
m_customizableColors.put("prefColorBottomRight", colorBottomRight);
m_customizableColors.put("prefColorBottomLeft", colorBottomLeft);
final float intensity2 = 0.86328125f;
m_customizableColors.put("prefColorBlobs", new Color(intensity2, 0.0f, intensity2 , 0.65f));


Log.v(this.getClass().getName() , "loadColorsFromPreference: Default m_customizeableColors="+m_customizableColors+", loading preferences...");

/* //old:
getPreferenceManager().setSharedPreferencesName(LavaLampWallpaperService.PREFERENCES);
SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
*/


//new way:

SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
Log.v(this.getClass().getName() , "loadColorsFromPreference: preferences="+preferences.getAll());

//onSharedPreferenceChanged() should be called:
preferences.registerOnSharedPreferenceChangeListener(this);

//override color values if present in preferences...
   boolean warning = false;
   String keys[] = {
 "prefColorTopLeft"  , "prefColorTopRight", "prefColorBottomRight", "prefColorBottomLeft","prefColorBlobs" };
   for (String key : keys  )
   {
     String value = preferences.getString(key,null);

     Color color = rgbaToColor(value);

     if (color != null)  m_customizableColors.put(key, color);
     else if (value != null && !value.equals("") )   warning = true;
       
   }

if (warning)
{
  Toast.makeText(context, R.string.color_warning, Toast.LENGTH_LONG).show();
}


Log.v(this.getClass().getName() , "loadColorsFromPreference: Preferences loaded. NEW m_customizeableColors="+m_customizableColors+".");

//SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LavaLampSettingsActivity);

}

    public LavaLampRenderer(Context theContext) 
    {
context = theContext;
Log.v(this.getClass().getName(),"LavaLampRenderer(): constructor called.");

//known good:    	            
//int stacks, int slices, float radius, float squash, GL10 gl, Context

    	////m_Earth = new Planet(50, 50, .3f, 1.0f);
    	////m_Earth.setPosition(0.0f, -0.5f, -2.0f);   //push it back and down, lights will be above and behind eyepoint...

//        m_Earth = new Planet(50, 50, 10.0f, 1.0f);
//        m_Earth.setPosition(-9.0f, -9.0f, -20.0f);



	loadColorsFromPreferences();

        m_Lamp  = new LavaLamp(m_customizableColors.get("prefColorBlobs"));

        //m_Lamp.setPosition(-9.0f, -9.0f, -80.0f);
        //m_Lamp.setPosition(-22.0f, -35.0f, -40.0f); //known good
        //m_Lamp.setPosition(-22.0f, -17.0f, -100.0f); //z value: pushed back aways
//        m_Lamp.setPosition(-52.0f, -17.0f, -100.0f); //z value: pushed back aways
//        m_Lamp.setPosition(-20.0f, -20.0f, -130.0f); //need to bring lamp forward 40.0 because the rotatation in executeLamp() moves the lamp into the -z quadrant



        //m_Lamp.setPosition(-20.0f, 30.0f, -60.0f); //june 5
//        m_Lamp.setPosition(-20.0f, 20.0f, -30.0f); //june 7 
        m_Lamp.setPosition(-20.0f, -20.0f, -30.0f);  


	//m_Box = new Box(0.5f, 0.35f, 0.7f);


	//m_Box.setPosition(-0.25f, -0.175f, -1.8f);


       // m_Box = new Box(40.f, 40.f, 40.f);
       // m_Box.setPosition(-22.0f, -19.0f, -170.0f);
/*
        m_Box = new Box(40.0f, 40.0f, 40.0f);
        m_Box.setPosition(-20.0f, 30.0f, -100.0f);
        m_Cube = new Cube(40.0f);
        m_Cube.setPosition(-20.0f, 30.0f, -100.0f);
*/
//        m_Box = new Box(40.0f, 40.0f, 40.0f);
//        m_Box.setPosition(-20.0f, -60.0f, -100.0f);



/*
       m_GuideRail = new GuideRail(); //40.0f, 40.0f); //height,length of rail
       m_GuideRail.setPosition(0.0f, 50.0f, -140.0f);
       setRocking( LavaLampView.MAX_ROCKING );
*/


/*
float alpha = 0.5f;
        float squareColorsYMCA[] =
        {
//                         0.8f, 0.8f, 0.0f, 0.2f,
//                         0.8f, 0.0f, 0.3f, 0.7f,
//                         0.0f, 0.8f, 0.3f, 0.7f,
//                         0.8f, 0.0f, 0.3f, 0.0f

1.0f,0.0f,0.0f,alpha,
0.0f,1.0f,0.0f,alpha,
0.0f,0.0f,1.0f,alpha,
1.0f,1.0f,1.0f,alpha

//0.5f,0.5f,0.5f,0.5f,
//0.5f,0.5f,0.5f,0.5f,
//0.5f,0.5f,0.5f,0.5f,
//0.5f,0.5f,0.5f,0.5f

        };
*/



       mSquare1 = new Square(40.0f);
	mSquare1.setColors(m_customizableColors.get("prefColorTopLeft"),m_customizableColors.get("prefColorTopRight"),m_customizableColors.get("prefColorBottomRight"),m_customizableColors.get("prefColorBottomLeft"));
       mSquare1.setPosition(0.0f,0.0f,-75.0f);

/*
//from nehe08tutorial:
                ByteBuffer byteBuf = ByteBuffer.allocateDirect(
lightAmbient.length * 4);
                byteBuf.order(ByteOrder.nativeOrder());
                lightAmbientBuffer = byteBuf.asFloatBuffer();
                lightAmbientBuffer.put(lightAmbient);
                lightAmbientBuffer.position(0);

                byteBuf = ByteBuffer.allocateDirect(lightDiffuse.length * 4);
                byteBuf.order(ByteOrder.nativeOrder());
                lightDiffuseBuffer = byteBuf.asFloatBuffer();
                lightDiffuseBuffer.put(lightDiffuse);
                lightDiffuseBuffer.position(0);

                byteBuf = ByteBuffer.allocateDirect(lightPosition.length * 4);
                byteBuf.order(ByteOrder.nativeOrder());
                lightPositionBuffer = byteBuf.asFloatBuffer();
                lightPositionBuffer.put(lightPosition);
                lightPositionBuffer.position(0);

*/
//cube = new Cube();


    }
    
/*
public void onDrawFrame_nehe08(GL10 gl) //from nehe08:
{
                // Clear Screen And Depth Buffer
                gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
                gl.glLoadIdentity(); // Reset The Current Modelview Matrix

                // Check if the light flag has been set to enable/disable lighti
//                        gl.glEnable(GL10.GL_LIGHTING);
//            gl.glEnable(SS_SUNLIGHT); //from initLightingMine:
//            gl.glEnable(SS_FILLLIGHT1);
//            gl.glEnable(SS_FILLLIGHT2);




                // Check if the blend flag has been set to enable/disable blendi
                        gl.glEnable(GL10.GL_BLEND); // Turn Blending On ( NEW )
                        gl.glDisable(GL10.GL_DEPTH_TEST); // Turn Depth Testing 

gl.glEnable(GL10.GL_COLOR_MATERIAL); //mine 
//Mine:
  gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
         gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        gl.glEnableClientState(GL11.GL_COLOR_ARRAY); 

initLightingFull(gl); //mine

//gl.glPushMatrix();
//gl.glTranslatef(0.0f,0.0f,-2.0f);
//int filter=0;
//cube.draw(gl, filter);
//executePlanet(m_Earth, gl);
//gl.glPopMatrix();


           gl.glPushMatrix();
            gl.glScalef(m_RatioX,m_RatioY,1.0f);
	gl.glTranslatef(0.0f, -40.0f , 0.0f);
            executeLavaLamp(m_Lamp, gl);
           gl.glPopMatrix();


     gl.glPushMatrix();
     gl.glTranslatef(0.0f,0.0f,-2.5f);
     mSquare1.draw(gl);
     gl.glPopMatrix();


//  TODO: disable client states maybe other stuff
}
*/


public void onDrawFrame(GL10 gl)
{

gl.glDisable(GL11.GL_DEPTH_TEST); // NOTE: <-- GL_DEPTH_TEST must be disabled fo

                gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        gl.glClearColor(0.0f,0.0f,0.0f,0.0f);
        gl.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        //Colorize the Texture:
        //gl.glEnableClientState(GL11.GL_COLOR_ARRAY);



        gl.glMatrixMode(GL11.GL_MODELVIEW);


//if (m_Panning < -1 )  m_Panning += PANNING_DECAY;
//else if (m_Panning > 1 )  m_Panning -= PANNING_DECAY;

            executeLavaLamp(m_Lamp, gl);
            executeSquare(mSquare1, gl);

}

public void executeSquare(Square square, GL10 gl)
{
     gl.glPushMatrix();
     gl.glTranslatef(square.m_Pos[0],square.m_Pos[1]  ,square.m_Pos[2]);
     mSquare1.draw(gl);
     gl.glPopMatrix();
}



/*
    public void onDrawFrame_old(GL10 gl)
	    {
                // Clear Screen And Depth Buffer
                gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
                gl.glLoadIdentity(); // Reset The Current Modelview Matrix
                gl.glEnable(GL10.GL_LIGHTING);
                gl.glEnable(GL10.GL_BLEND); // Turn Blending On ( NEW )
                gl.glDisable(GL10.GL_DEPTH_TEST); // Turn Depth Testing OFF  - required for blending
        gl.glDisable(GL10.GL_DITHER); //from :
     gl.glEnable(GL11.GL_CULL_FACE);
         gl.glShadeModel(GL11.GL_SMOOTH);
         gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);                          
         gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        gl.glEnableClientState(GL11.GL_COLOR_ARRAY); //Colorize the Texture
       initLightingMine(gl);


        gl.glClearColor(0.0f,0.0f,0.0f,1.0f);



        gl.glMatrixMode(GL11.GL_MODELVIEW);
        gl.glEnableClientState(GL11.GL_VERTEX_ARRAY);
//end of new--

	   //gl.glPushMatrix();
           //gl.glTranslatef(m_Panning, 0.0f, 0.0f);
            //executeGuideRail(m_GuideRail, gl);

	   gl.glPushMatrix();
//           Log.v(this.getClass().getName(), " m_GuideRail.getMarkerZ() - 40.0f = "+( m_GuideRail.getMarkerZ() - 40.0f)); // 40.0f==RESOLUTION
            gl.glScalef(m_RatioX,m_RatioY,1.0f);

           //gl.glTranslatef(0.0f, m_GuideRail.getMarkerY() ,  m_GuideRail.getMarkerZ() - 40.0f); // 40.0f==RESOLUTION
gl.glTranslatef(0.0f, -40.0f , 0.0f);

            executeLavaLamp(m_Lamp, gl);
            executeBox(m_Box, gl); 

	   //gl.glPopMatrix();
	   gl.glPopMatrix();


     //XXX
//     gl.glPushMatrix();
//
//     gl.glTranslatef(0.0f,0.0f,-2.5f);
//     mSquare1.draw(gl);
//     gl.glPopMatrix();
////m_Earth.m_Pos[1] = m_Earth.m_Pos[1] +  0.001f;
////	    executePlanet(m_Earth, gl);                    
	  //  gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_EMISSION, makeFloatBuffer(black));  
    }
*/

    //TODO: make Planet,LavaLamp,Box,etc extend new class Drawable, then make a generic execute method
    private void executePlanet(Planet planet, GL10 gl) 
    {
        float posX, posY, posZ;
        posX = planet.m_Pos[0];                      
        posY = planet.m_Pos[1];
        posZ = planet.m_Pos[2];
            
        gl.glPushMatrix();
        gl.glTranslatef(posX, posY, posZ);         //translate comes before draw...  
        planet.draw(gl);                                       
        gl.glPopMatrix();
    }


    private void executeGuideRail(GuideRail guideRail, GL10 gl) 
    {
        float posX, posY, posZ;
        posX = guideRail.m_Pos[0];                      
        posY = guideRail.m_Pos[1];
        posZ = guideRail.m_Pos[2];
            
        gl.glPushMatrix();
        gl.glTranslatef(posX, posY, posZ);         //translate comes before draw...  
        guideRail.draw(gl);                                       
        gl.glPopMatrix();
    }

    private void executeBox(Box box, GL10 gl) 
    {
        float posX, posY, posZ;
        posX = box.m_Pos[0];                      
        posY = box.m_Pos[0];
        posZ = box.m_Pos[2];
            
        gl.glPushMatrix();
        gl.glTranslatef(posX, posY, posZ);         //translate comes before draw...  



/*

       gl.glTranslatef(20.0f, 20.0f,  20.0f); //m_Lamp.getMid()); //xx20.0f);

//       gl.glRotatef( (float) angle, m_RotateLandscape+  m_RotatePortrait, 0.0f, 0.0f); //m_RotateLandscape);

       gl.glTranslatef(-20.0f, -20.0f,  -20.0f); //-m_Lamp.getMid()); //xx20.0f);

*/

        //m_Cube.draw(gl);                                       
        m_Box.draw(gl);
        gl.glPopMatrix();
    }



    private void executeLavaLamp(LavaLamp lamp, GL10 gl)
    {
        gl.glPushMatrix();

        gl.glTranslatef(lamp.m_Pos[0],lamp.m_Pos[1]  ,lamp.m_Pos[2]);         //translate comes before draw


       gl.glTranslatef( m_Panning, 0.0f, 0.0f);
        
       gl.glPushMatrix();
       gl.glRotatef( (float) 90.0f, m_RotatePortrait, m_RotateLandscape, 0.0f );


       gl.glTranslatef(20.0f, 20.0f, m_Lamp.getMid()); //20.0f); //m_Lamp.getMid()); //20.0f); // m_Lamp.getMid()); //xx20.0f);


      gl.glRotatef( m_VerticalRotation , m_RotatePortrait, 0.0f, m_RotateLandscape);
      gl.glRotatef( m_Horizontal * ROTATION_SPEED, m_RotateLandscape, 0.0f, m_RotatePortrait);
      
      

       gl.glTranslatef(-20.0f , -20.0f, -m_Lamp.getMid()); //-20.0f); //-m_Lamp.getMid()); //20.0f); // -m_Lamp.getMid()); // -20.0f); //center prior to skewering
       
       
        lamp.draw(gl);
	gl.glPopMatrix();

        gl.glPopMatrix();
    }



/*
        public void onSurfaceChanged_nehe08(GL10 gl, int width, int height) { //from nehe08:
//setDimensions(width, height);
                if (height == 0) { // Prevent A Divide By Zero By
                        height = 1; // Making Height Equal One
                }

                gl.glViewport(0, 0, width, height); // Reset The Current Viewpor
                gl.glMatrixMode(GL10.GL_PROJECTION); // Select The Projection Ma
                gl.glLoadIdentity(); // Reset The Projection Matrix

                // Calculate The Aspect Ratio Of The Window
//                GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.
//1f, 100.0f);

                gl.glMatrixMode(GL10.GL_MODELVIEW); // Select The Modelview Matr
                gl.glLoadIdentity(); // Reset The Modelview Matrix
        }
*/

	public void onSurfaceChanged(GL10 gl, int width, int height) 
	{
	     

Log.v(this.getClass().getName(),"onSurfaceChanged: width="+width+" height="+height);
setDimensions(width, height);


	 	float aspectRatio;
		float zNear =.1f;
		float zFar =1000f;
		float fieldOfView = 30.0f/57.3f;
		float	size;
		
                if (height == 0)    height = 1; // Making Height Equal One

	    gl.glViewport(0, 0, width, height);
		//gl.glEnable(GL10.GL_NORMALIZE);
            gl.glMatrixMode(GL10.GL_PROJECTION);
		 gl.glLoadIdentity(); 

		//gl.glMatrixMode(GL10.GL_PROJECTION);
		aspectRatio=(float)width/(float)height;			
		size = zNear * (float)(Math.tan((double)(fieldOfView/2.0f)));
		gl.glFrustumf(-size, size, -size/aspectRatio, size /aspectRatio, zNear, zFar);
				
		gl.glMatrixMode(GL10.GL_MODELVIEW);
                gl.glLoadIdentity();

	}


/*
        public void onSurfaceCreated_nehenew(GL10 gl, EGLConfig config) {               
                // And there'll be light!
//                gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbientBuffer
); // Setup The Ambient Light
//                gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuseBuffer
); // Setup The Diffuse Light
//                gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPositionBuffer); // Position The Light
//                gl.glEnable(GL10.GL_LIGHT0); // Enable Light 0

initLightingMine(gl); //mine

                // Blending
                ///gl.glColor4f(1.0f, 1.0f, 1.0f, 0.5f); // Full Brightness. 50% Al
                //gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE); // Set The Blend
gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA); //mine

                // Settings
                gl.glDisable(GL10.GL_DITHER); // Disable dithering
                //gl.glEnable(GL10.GL_TEXTURE_2D); // Enable Texture Mapping
                gl.glShadeModel(GL10.GL_SMOOTH); // Enable Smooth Shading
                gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Black Background
                gl.glClearDepthf(1.0f); // Depth Buffer Setup
                gl.glDisable(GL10.GL_DEPTH_TEST); //mine
                //gl.glEnable(GL10.GL_DEPTH_TEST); // Enables Depth Testing
                //gl.glDepthFunc(GL10.GL_LEQUAL); // The Type Of Depth Testing To 

                // Really Nice Perspective Calculations

            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);



                // Load the texture for the cube once during Surface creation
                //cube.loadGLTexture(gl, this.context);
        }
*/


//Log.v(this.getClass().getName(),"onSurfaceCreated: context="+context);
	public void onSurfaceCreated(GL10 gl, EGLConfig config) 
	{
initLightingNew(gl);
        gl.glDisable(GL10.GL_DITHER);
     gl.glEnable(GL11.GL_CULL_FACE);
   //gl.glDisable(GL11.GL_CULL_FACE);
         gl.glShadeModel(GL11.GL_SMOOTH);

//        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
//        gl.glEnable(GL10.GL_CULL_FACE);
//        gl.glCullFace(GL10.GL_BACK);
//        gl.glDepthMask(true);

            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);



         gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);                          
 //        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
         gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);


  gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
         gl.glEnable(GL10.GL_BLEND);
//gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
//gl.glColor4f(0.0f,0.0f,0.0f,0.0f); //doesnt seem to do anything
// gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE);



//gl.glDepthMask(false); // not sure if needed....
gl.glDepthMask(true); // not sure if needed

//         gl.glDisable(GL11.GL_DEPTH_TEST);
                gl.glClearDepthf(1.0f); // Depth Buffer Setup

    }

public void initLightingNew(GL10 gl)
{
  //used by 
   float[] posSun={0.0f, 0.0f, -4.0f, 3.5f}; //fourth position = intensity ?
 float[] white={1.0f, 1.0f, 1.0f, 1.0f};
 float[] yellow={1.0f, 1.0f, 0.0f, 1.0f};
      float[] bright = {0.7f, 0.7f, 0.7f, 0.7f};

//notused:
/*
    float[] lightcyan={0.0f,0.5f,0.5f,0.5f};
    float[] lightyellow={0.5f,0.5f,0.0f,0.5f};
*/


//Fill Lights Position & Colors:
   float[] posFill1={0.0f, -1.0f, -3.5f, 1.0f};
   float[] posFill2={0.0f, 1.0f, -3.5f, 1.0f};
 float[] cyan={0.0f, 1.0f, 1.0f, 1.0f};
    float[] blue={0.0f, 0.0f, 1.0f, 1.0f};
      float[] magenta={1.0f, 0.0f, 1.0f, 1.0f};

//


       gl.glLightfv(SS_SUNLIGHT, GL10.GL_POSITION, makeFloatBuffer(posSun))
;
            gl.glLightfv(SS_SUNLIGHT, GL10.GL_DIFFUSE, makeFloatBuffer(white));
            gl.glLightfv(SS_SUNLIGHT, GL10.GL_SPECULAR, makeFloatBuffer(bright))
;

//

     gl.glLightfv(SS_FILLLIGHT1, GL10.GL_POSITION, makeFloatBuffer(posFill1));
      gl.glLightfv(SS_FILLLIGHT1, GL10.GL_DIFFUSE, makeFloatBuffer(blue));
      gl.glLightfv(SS_FILLLIGHT1, GL10.GL_SPECULAR, makeFloatBuffer(cyan));
      gl.glLightfv(SS_FILLLIGHT2, GL10.GL_POSITION, makeFloatBuffer(posFill2));
      gl.glLightfv(SS_FILLLIGHT2, GL10.GL_SPECULAR, makeFloatBuffer(magenta));
      gl.glLightfv(SS_FILLLIGHT2, GL10.GL_DIFFUSE, makeFloatBuffer(blue));


   gl.glEnable(GL10.GL_COLOR_MATERIAL);

 //Materials go here.

/*
      gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, makeFloatBuffer(lightyellow));
      gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, makeFloatBuffer(lightcyan));
*/
      gl.glMaterialf(GL10.GL_FRONT_AND_BACK,GL10.GL_SHININESS, 25);
      gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, makeFloatBuffer(yellow));

//Misc:
gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE, 1.0f);

            gl.glEnable(GL10.GL_LIGHTING);
            gl.glEnable(SS_SUNLIGHT);
            gl.glEnable(SS_FILLLIGHT1);
            gl.glEnable(SS_FILLLIGHT2);
}


/*
	private void initLighting(GL10 gl) 
	{
	    float[] sunPos={0.0f, 30.0f, 5.0f, 1.0f};            
	    float[] posFill1={-30.0f, 30.0f, 5.0f, 1.0f};            
	    float[] posFill2={30.0f, 30.0f, 5.0f, 1.0f};            
	    float[] white={1.0f, 1.0f, 1.0f, 1.0f};            
	    float[] dimblue={0.0f, 0.0f, .2f, 1.0f};            
	    float[] cyan={0.0f, 1.0f, 1.0f, 1.0f};            
	    float[] yellow={1.0f, 1.0f, 0.0f, 1.0f};
	    float[] magenta={1.0f, 0.0f, 1.0f, 1.0f};            
	    float[] dimmagenta={.75f, 0.0f, .25f, 1.0f};            
	    float[] dimcyan={0.0f, .5f, .5f, 1.0f};            
            float[] dimwhite={0.3f,0.3f,0.3f,1.0f};
            float[] dimyellow={0.3f,0.3f,0.0f,1.0f};

	        
	    //Lights go here.
	        
	    gl.glLightfv(SS_SUNLIGHT, GL10.GL_POSITION, makeFloatBuffer(sunPos));
	    gl.glLightfv(SS_SUNLIGHT, GL10.GL_DIFFUSE, makeFloatBuffer(white));
	    gl.glLightfv(SS_SUNLIGHT, GL10.GL_SPECULAR, makeFloatBuffer(yellow));        
	    
	    gl.glLightfv(SS_FILLLIGHT1, GL10.GL_POSITION, makeFloatBuffer(posFill1));
	    gl.glLightfv(SS_FILLLIGHT1, GL10.GL_DIFFUSE, makeFloatBuffer(dimblue));
	    gl.glLightfv(SS_FILLLIGHT1, GL10.GL_SPECULAR, makeFloatBuffer(dimcyan));    

	    gl.glLightfv(SS_FILLLIGHT2, GL10.GL_POSITION, makeFloatBuffer(posFill2));
	    gl.glLightfv(SS_FILLLIGHT2, GL10.GL_SPECULAR, makeFloatBuffer(dimmagenta));
	    gl.glLightfv(SS_FILLLIGHT2, GL10.GL_DIFFUSE, makeFloatBuffer(dimblue));

	    //Materials go here.
	        
	    gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, makeFloatBuffer(white));
	    gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, makeFloatBuffer(white));

	    gl.glLightf(SS_SUNLIGHT, GL10.GL_QUADRATIC_ATTENUATION,.001f);
	    gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, 25);    
	    gl.glShadeModel(GL10.GL_SMOOTH);                
	    gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE, 0.0f);

   //turn on color vertex data:
            gl.glEnable(GL10.GL_COLOR_MATERIAL);

	        
	    gl.glEnable(GL10.GL_LIGHTING);
	    gl.glEnable(SS_SUNLIGHT);
	    gl.glEnable(SS_FILLLIGHT1);
	    gl.glEnable(SS_FILLLIGHT2);
	} 
*/

/*
	private void initLightingMine(GL10 gl) 
	{
	    float[] sunPos={0.0f, 30.0f, 5.0f, 1.0f};            
	    float[] posFill1={-30.0f, 30.0f, 5.0f, 1.0f};            
	    float[] posFill2={30.0f, 30.0f, 5.0f, 1.0f};            
	    float[] white={1.0f, 1.0f, 1.0f, 1.0f};            
	//    float[] dimblue={0.0f, 0.0f, .2f, 1.0f};            
	    float[] cyan={0.0f, 1.0f, 1.0f, 1.0f};            
	    float[] yellow={1.0f, 1.0f, 0.0f, 1.0f};
	    float[] magenta={1.0f, 0.0f, 1.0f, 1.0f};            
	 //   float[] dimmagenta={.75f, 0.0f, .25f, 1.0f};            
	 //   float[] dimcyan={0.0f, .5f, .5f, 1.0f};            
            float[] dimwhite={0.3f,0.3f,0.3f,1.0f};
            float[] dimyellow={0.3f,0.3f,0.0f,1.0f};
//     float[] white = {1.0f, 1.0f, 1.0f, 1.0f};
      float[] notsobright = {0.20f, 0.20f, 0.20f, 0.0f};
      float[] bright = {0.7f, 0.7f, 0.7f, 0.7f};
     //used by the fill lights:
      float[] dimblue={0.0f,0.0f,.2f,1.0f};
      float[] dimcyan={0.0f,.5f,.5f,1.0f};
      float[] dimmagenta={ .75f,0.0f,.25f,1.0f};

   //The world [amblient] value is a constant across your entire OpenGL ES un
//iverse
      float[] colorVector={0.5f, 0.5f, 0.5f, 1.0f};
      gl.glLightModelfv(GL10.GL_LIGHT_MODEL_AMBIENT, makeFloatBuffer(colorVector
));




	        
	    //Lights go here.
	        
//
//	    gl.glLightfv(SS_SUNLIGHT, GL10.GL_POSITION, makeFloatBuffer(sunPos));
//	    gl.glLightfv(SS_SUNLIGHT, GL10.GL_DIFFUSE, makeFloatBuffer(white));
//	    gl.glLightfv(SS_SUNLIGHT, GL10.GL_SPECULAR, makeFloatBuffer(yellow));        

     //For the light SS_SUNLIGHT:
      //Main Light:
      gl.glLightfv(SS_SUNLIGHT, GL10.GL_POSITION, makeFloatBuffer(sunPos));
      gl.glLightfv(SS_SUNLIGHT,GL10.GL_SPECULAR, makeFloatBuffer(notsobright));
      gl.glLightfv(SS_SUNLIGHT, GL10.GL_AMBIENT, makeFloatBuffer(bright));


// Fill lights: 
//
	    
	    gl.glLightfv(SS_FILLLIGHT1, GL10.GL_POSITION, makeFloatBuffer(posFill1));
	    gl.glLightfv(SS_FILLLIGHT1, GL10.GL_DIFFUSE, makeFloatBuffer(dimblue));
	    gl.glLightfv(SS_FILLLIGHT1, GL10.GL_SPECULAR, makeFloatBuffer(dimcyan));    

	    gl.glLightfv(SS_FILLLIGHT2, GL10.GL_POSITION, makeFloatBuffer(posFill2));
	    gl.glLightfv(SS_FILLLIGHT2, GL10.GL_SPECULAR, makeFloatBuffer(dimmagenta));
	    gl.glLightfv(SS_FILLLIGHT2, GL10.GL_DIFFUSE, makeFloatBuffer(dimblue));

     gl.glLightfv(SS_FILLLIGHT1, GL10.GL_POSITION, makeFloatBuffer(posFill1));
      gl.glLightfv(SS_FILLLIGHT1, GL10.GL_DIFFUSE, makeFloatBuffer(dimblue));
      gl.glLightfv(SS_FILLLIGHT1, GL10.GL_SPECULAR, makeFloatBuffer(dimcyan));
      gl.glLightfv(SS_FILLLIGHT2, GL10.GL_POSITION, makeFloatBuffer(posFill2));
      gl.glLightfv(SS_FILLLIGHT2, GL10.GL_SPECULAR, makeFloatBuffer(dimmagenta))
;
      gl.glLightfv(SS_FILLLIGHT2, GL10.GL_DIFFUSE, makeFloatBuffer(dimblue));



	    //Materials go here.
	        
	    gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, makeFloatBuffer(white));
	    gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, makeFloatBuffer(white));

            //turn on color vertex data:
            gl.glEnable(GL10.GL_COLOR_MATERIAL);



	    gl.glLightf(SS_SUNLIGHT, GL10.GL_QUADRATIC_ATTENUATION,.001f);
	    gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, 25);    
	    gl.glShadeModel(GL10.GL_SMOOTH);                
	    gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE, 0.0f);
	        
	    gl.glEnable(GL10.GL_LIGHTING);
	    gl.glEnable(SS_SUNLIGHT);
	    gl.glEnable(SS_FILLLIGHT1);
	    gl.glEnable(SS_FILLLIGHT2);
	} 
*/

/*
    private void initLightingFull(GL10 gl) 
    {
      int SS_SUNLIGHT = GL10.GL_LIGHT0;
      int SS_FILLLIGHT1= GL10.GL_LIGHT1;
      int SS_FILLLIGHT2 = GL10.GL_LIGHT2;

      float[] diffuse = {0.0f, 1.0f, 0.0f, 1.0f};

    //  float[] pos = {50.0f, 0.0f, 3.0f, 1.0f};
  //    float[] posFill1={-15.0f,15.0f,0.0f,1.0f};
//      float[] posFill2={-10.0f,-4.0f,1.0f,1.0f};
      float[] pos ={0.0f, 30.0f, 5.0f, 1.0f};            
      float[] posFill1={-30.0f, 30.0f, 5.0f, 1.0f};            
      float[] posFill2={30.0f, 30.0f, 5.0f, 1.0f};            

      float[] white = {1.0f, 1.0f, 1.0f, 1.0f};
      float[] notsobright = {0.20f, 0.20f, 0.20f, 0.0f};
      float[] bright = {0.7f, 0.7f, 0.7f, 0.7f};
      //
      float[] red={1.0f, 0.0f, 0.0f, 1.0f};
      float[] green={0.0f,1.0f,0.0f,1.0f};
      float[] blue={0.0f, 0.0f, 1.0f, 1.0f};
      float[] cyan={0.0f, 1.0f, 1.0f, 1.0f};
      float[] yellow={1.0f, 1.0f, 0.0f, 1.0f};
      float[] magenta={1.0f, 0.0f, 1.0f, 1.0f};
      float[] halfcyan={0.0f, 0.5f, 0.5f, 1.0f};
      //used by the fill lights:
      float[] dimblue={0.0f,0.0f,.2f,1.0f};
      float[] dimcyan={0.0f,.5f,.5f,1.0f};
      float[] dimmagenta={ .75f,0.0f,.25f,1.0f};

      //The world [amblient] value is a constant across your entire OpenGL ES universe
      //float[] colorVector={0.5f, 0.5f, 0.5f, 1.0f};
//      float[] colorVector={1.0f,1.0f,1.0f,1.0f};
//      gl.glLightModelfv(GL10.GL_LIGHT_MODEL_AMBIENT, makeFloatBuffer(colorVector));


      //For the light SS_SUNLIGHT:
      //Main Light:
      gl.glLightfv(SS_SUNLIGHT, GL10.GL_POSITION, makeFloatBuffer(pos));
      gl.glLightfv(SS_SUNLIGHT,GL10.GL_SPECULAR, makeFloatBuffer(bright));
      gl.glLightfv(SS_SUNLIGHT, GL10.GL_AMBIENT, makeFloatBuffer(bright));

      //
      //*
      //* Fill Lights:
      //*
      //*
      gl.glLightfv(SS_FILLLIGHT1, GL10.GL_POSITION, makeFloatBuffer(posFill1));
      gl.glLightfv(SS_FILLLIGHT1, GL10.GL_DIFFUSE, makeFloatBuffer(blue));
      gl.glLightfv(SS_FILLLIGHT1, GL10.GL_SPECULAR, makeFloatBuffer(cyan));
      gl.glLightfv(SS_FILLLIGHT2, GL10.GL_POSITION, makeFloatBuffer(posFill2));
      gl.glLightfv(SS_FILLLIGHT2, GL10.GL_SPECULAR, makeFloatBuffer(magenta));
      gl.glLightfv(SS_FILLLIGHT2, GL10.GL_DIFFUSE, makeFloatBuffer(blue));

      //ATTENUATION due to fog,etc:
     // gl.glLightf(SS_SUNLIGHT, GL10.GL_LINEAR_ATTENUATION, .25f);


      //spotlight: GL_SPOT_CUTOFF specifies the angle at which the spotlight.s beam fa
      //des to 0 intensity from thecenter of the spotlight.s cone and is naturally half
      //the angular diameter of the full beam.
//
//      float direction[]={1.0f,0.0f,0.0f};
//      gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPOT_DIRECTION, makeFloatBuffer(direction));
//

      gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, makeFloatBuffer(cyan));
      gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, makeFloatBuffer(bright));
      gl.glMaterialf(GL10.GL_FRONT_AND_BACK,GL10.GL_SHININESS, 10);
      gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, makeFloatBuffer(bright)
      );

      gl.glEnable(GL10.GL_COLOR_MATERIAL);

      //Emissive Material, eg: sun:
      //gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_EMISSION, makeFloatBuffer(yellow));

      gl.glShadeModel(GL10.GL_SMOOTH);
      gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE, 1.0f);
      gl.glEnable(GL10.GL_LIGHTING);
      gl.glEnable(SS_SUNLIGHT);
      gl.glEnable(SS_FILLLIGHT1);
      gl.glEnable(SS_FILLLIGHT2);

      //gl.glLoadIdentity();
    }
*/
    protected static FloatBuffer makeFloatBuffer(float[] arr) 
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(arr.length*4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(arr);
        fb.position(0);
        return fb;
    }
}
