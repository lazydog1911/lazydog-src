package com.lazydog.lavalamp;


import java.util.*;
import java.nio.*;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES11;
//import javax.microedition.khronos.opengles.GL11; //for gl.glDrawArrays()


//
import android.util.Log;



public class LavaLamp 
{
  int nBalls;
  Metaball balls[];
  boolean justStarted;
  float launchChance;

  //FloatBuffer m_ColorData;  
  Color m_Color; 
  public int nVertices;

  public float[] m_Pos = {0.0f, 0.0f, 0.0f}; 



  public static final int BLOBS_PER_GROUP  = 4;
  public static final float  GRAVITY       = 0.000013f;
  public static final float  CONVECTION    = 0.005f;
  public static final float   TILT         = 0.00166666f;
  public static final int MAX_VERTICES     = 20000;
  public static final int RESOLUTION       = 40;
  public static final float RESOLUTIONF       = 40.0f; //==RESOLUTION
  public static final float ISOLEVEL       = 0.3f;
//  public static final float MAX_POSR       = 0.7f; //how far ball sticks out
  public static final float MAX_POSR       = 0.5f; //how far ball sticks out

  //Optimization:
  public MarchingWork m_MarchingWork1, m_MarchingWork2;
  public MarchingWorkPart m_MarchingWorkPart1, m_MarchingWorkPart2;
  public Thread m_Thread1, m_Thread2;
  FloatBuffer m_VertexData1, m_VertexData2;
  FloatBuffer m_NormalData1, m_NormalData2;
  public static final float CUSHIONF = 3.5f; //used to calculate min/max:
  public float m_MinZ, m_MaxZ; //from 0 to 1
  public float  m_MidZ; //from 0 to 40...:w

  Random m_Random;
  Timer m_Timer0, m_Timer1;

//Debugging:
public float minx, maxx,miny,maxy,minz,maxz;
public int  nnormals;

public void setColor(Color color)
{
  m_Color = color;
}

  public LavaLamp(Color color)
  {
    justStarted=true;
    launchChance =  0.003f; // 0.6f; //0.003f;
    nBalls = BLOBS_PER_GROUP + 2;
    balls = new Metaball[nBalls];
    for (int i=0; i< nBalls ; i++)
      balls[i] = new Metaball();


    m_Color = color;

    //generateStaticBlobs();

    //for thread 1's results:
    m_VertexData1 = makeFloatBuffer(MAX_VERTICES * 3 ); 
    m_NormalData1 = makeFloatBuffer(MAX_VERTICES * 3 );
    //for thread 2's results:
    m_VertexData2 = makeFloatBuffer(MAX_VERTICES * 3 ); 
    m_NormalData2 = makeFloatBuffer(MAX_VERTICES * 3 );

/*
    m_ColorData = makeFloatBuffer(MAX_VERTICES * 4);

    final float red = 0.7f; //constant color:
    final float green = 0.7f;
    final float blue = 0.7f;
    final float alpha = 0.6f;
    for (int i=0;i < 4 * MAX_VERTICES; i+= 4)  {
      m_ColorData.put(i,   red );
      m_ColorData.put(i+1, green); //(float) (Math.random()) );
      m_ColorData.put(i+2, blue ); //0.0f); //(float) (Math.random()) );
      m_ColorData.put(i+3, alpha); //alpha
    }
*/
    
    //Optimization:
    m_MarchingWork1 = new MarchingWork();
    m_MarchingWork2 = new MarchingWork();


    m_MarchingWorkPart1= new MarchingWorkPart();
    m_MarchingWorkPart1.setMarchingWork(m_MarchingWork1);
    m_MarchingWorkPart1.setLavaLamp(this);
    m_MarchingWorkPart1.setWorkPortions(0, 20);

    m_MarchingWorkPart2= new MarchingWorkPart();
    m_MarchingWorkPart2.setMarchingWork(m_MarchingWork2);  
    m_MarchingWorkPart2.setLavaLamp(this);
    m_MarchingWorkPart2.setWorkPortions(20, 40);

    
    m_Thread1 = new Thread(m_MarchingWorkPart1);
    m_Thread1.setPriority (Thread.MAX_PRIORITY );

    m_Thread2 = new Thread(m_MarchingWorkPart2);
    m_Thread2.setPriority (Thread.MAX_PRIORITY );




    //Misc:
    m_Random = new Random(); //12345
    m_Timer0 = new Timer();
    m_Timer1 = new Timer();

    
  }

    public void setPosition(float x, float y, float z)
    {
         m_Pos[0] = x;
         m_Pos[1] = y;
         m_Pos[2] = z;
    }


    public float getMid()  //scale out to RESOLUTION
    {
      return m_MidZ ;
    }

    protected static FloatBuffer makeFloatBuffer(int capacity)
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(capacity*4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        return fb;
    }


  public float frand (float extend)
  {
    return ( m_Random.nextFloat() * extend );

  }

  public float bellrand (float extent)    /* like frand(), but a bell curve. */
  {
    return (((frand(extent) + frand(extent) + frand(extent)) / 3)
          - (extent/2));
  }


  public void resetBall (Metaball b)
  {

    b.r = 0.00001f;
    b.R = 0.12f  + bellrand(0.10f);

    b.posR = bellrand (MAX_POSR);
    b.posTh = frand((float)(Math.PI)*2); // 
    b.z = 0.0f;

    b.dr = bellrand(TILT);
    b.dz = CONVECTION;

    b.leader = null;

    if (!b.alive)
      b.alive = true;

    moveBall ( b);
  }
  
  public Metaball getBall ()
  {
    int i;
    for (i = 0; i < nBalls; i++)
    {
      Metaball b = balls[i];
      if (!b.alive)
        return b;
    }
    return null;
  }

  public void generateStaticBlobs() //not used
  {
    Metaball b0 = new Metaball();
    Metaball b1 = new Metaball();
    int i;

    b0 = getBall ();
    if (b0==null)  System.exit(1);
    b0.statik = true;
    b0.alive = true;
    b0.R = 0.6f;
    b0.r = 0.3f;

    /* the giant blob at the bottom of the bottle.
    */
    b0.posR = 0.0f;
    b0.posTh = 0.0f;
    b0.dr = 0.0f;
    b0.dz = 0.0f;
    b0.x = 0.0f;
    b0.y = 0.0f;
    b0.z = -0.43f;

    /* the small blob at the top of the bottle.
    */
    b1 = getBall();
    if (b1==null)  System.exit(1);

    b1.statik = true;
    b1.alive = true;
    b1.posR = 0.0f;
    b1.posTh = 0.0f;
    b1.dr = 0.0f;
    b1.dz = 0.0f;
    b1.x = 0.0f;
    b1.y = 0.0f;
    b1.R = 0.16f;
    b1.r = 0.135f;
    b1.z = 1.078f;

    //TODO: ??Some extra blobs at the bottom of the bottle, to jumble the surface.
  }

  public void moveBall (Metaball b)
  {
    float realR;

    if (b.statik) return;

    b.posR += b.dr;  //space out the balls (spaceballs)
    b.z     += b.dz;

    b.dz -= GRAVITY;

    if (b.posR > 0.9f)
    {
      b.posR = 0.9f;
      b.dr = -b.dr;
    }
    else if (b.posR < 0.0f)
    {
      b.posR = -b.posR;
      b.dr = -b.dr;
    }

    realR = b.posR; // * bottle_radius_at (bp, b->z);

    b.x = (float)(Math.cos ((float)b.posTh) * realR);
    b.y = (float)(Math.sin ((float)b.posTh) * realR);

    if (b.z < -b.R)  /* dropped below bottom of glass - turn it off */
      b.alive = false;

  }


  public void clampBalls ()
  {
    int i;
 
    //Optimization:
    m_MinZ=1.0f; m_MaxZ=0.0f;
    float runningTotal = 0.0f; 
   int c = 0;

    for (i = 0; i < nBalls; i++)
    {
      Metaball b = balls[i];
      if (b.alive && b.leader != null)
        {
          float zslack = 0.1f;
          float minz = b.leader.z - zslack;
          float maxz = b.leader.z + zslack;

          /* Try to keep the Z values near those of the leader.
             Don't let it go out of range (above or below) and clamp it
             if it does.  If we've clamped it, make sure dz will be
             moving it in the right direction (back toward the leader.)

             We aren't currently clamping r, only z -- doesn't seem to
             be needed.

             This is kind of flaky, I think.  Sometimes you can see
             the blobbies "twitch".  That's no good.
           */

          if (b.z < minz)
          {
            if (b.dz < 0) b.dz = -b.dz;
            b.z = minz - b.dz;
          }

          if (b.z > maxz)
          {
            if (b.dz > 0) b.dz = -b.dz;
            b.z = maxz + b.dz;
          }

          
        }
        if ( b.alive ) { //
          runningTotal += b.z;
          c++;
          float w = CUSHIONF*b.R;
          if (b.z-w < m_MinZ )  m_MinZ = b.z-w;
          else if (b.z+w > m_MaxZ )  m_MaxZ = b.z+w;
        }
    }
    m_MidZ = runningTotal/(float)(c) * RESOLUTIONF; //m_MidZ is special (because of the * RESOLUTION)
  }


  public void moveBalls ()
  {
    int i;
    

    for (i = 0; i < nBalls; i++)
    {
      Metaball b = balls[i];
      if (b.alive)
        moveBall(b);
    }

    clampBalls();
  }

  public float computeMetaballInfluence (float x, float y, float z)
  {
    float vv = 0.0f;  //retval
    int i;
    Metaball b;

    for (i = 0; i < nBalls; i++)
    {
      b= balls[i];
      float dx, dy, dz;
      float d2, r, R, r2, R2;

      if (!b.alive) continue;

      dx = x - b.x;
      dy = y - b.y;
      dz = z - b.z;
      R = b.R;

      if (dx > R || dx < -R ||    /* quick check before multiplying */
          dy > R || dy < -R ||
          dz > R || dz < -R)
        continue;

      d2 = (dx*dx + dy*dy + dz*dz);
      r = b.r;

      r2 = r*r;
      R2 = R*R;

      if (d2 <= r2)             /* (d <= r)   inside the hard radius */
        vv += 1;
      else if (d2 > R2)         /* (d > R)   outside the radius of influence */
        ;
      else          /* somewhere in between: linear drop-off from r=1 to R=0 */
        {
          /* was: vv += 1 - ((d-r) / (R-r)); */
          vv += 1 - ((d2-r2) / (R2-r2));
        }
    }

    return vv;
  }


  public float objCompute (float x, float y, float z)
  {
    //double clip;


    //TODO: optimize this:
       x /= 	RESOLUTIONF; /* convert from 0-N to 0-1. */
   // x=getCachedValues(x);
    y /=  RESOLUTIONF; 
    //y=getCachedValues(y);
    z /=  RESOLUTIONF;
    //z=getCachedValues(z);


    x -= 0.5;	/* X and Y range from -.5 to +.5; z ranges from 0-1. */
    y -= 0.5;


    return   computeMetaballInfluence(x,y,z);
  }


  public void launchBalls ()
  {
    Metaball b0 = getBall();
    int i;

    if (b0 == null) return;

    resetBall(b0);

    for (i = 0; i < BLOBS_PER_GROUP; i++)
    {
      Metaball b1 = getBall();
      if (b1 == null) break;

      ////???:*b1 = *b0;:
      b1.copyBall(b0);

      resetBall ( b1);
      b1.leader = b0;

//# define FROB(FIELD,AMT) \
//         b1->FIELD += (bellrand(AMT) * b0->FIELD)
   /* FROB (pos_r,  0.7); */
   /* FROB (pos_th, 0.7); */
      ////From: FROB (dr, 0.8);
      b1.dr += (bellrand(0.8f) * b0.dr);
      ////From: FROB (dz, 0.6);
      b1.dz += (bellrand(0.6f) * b0.dz);
    }
  }

private int count =0;


//Debugging:
//LavaLamp lamp = this;
//minx=50.0f;maxx=0.0f;miny=50.0f;maxy=0.0f;minz=50.0f;maxz=0.0f;
//    lamp.nnormals=0;

  public void drawTriangles (GL10 gl, FloatBuffer vertices, FloatBuffer normals,  int nVertices)
{
//Log.v(this.getClass().getName(), "drawTriangles: nVertices="+nVertices);


//DEBUGGING:
//if ( (count & 15)==0 )  Log.v(this.getClass().getName(), "drawTriangles: nVertices="+nVertices);

         //gl.glEnable(GL10.GL_CULL_FACE);
         //gl.glCullFace(GL10.GL_BACK);
   //    if ( nVertices != 0 )  {
         vertices.position(0);
         normals.position(0);
         gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices);//m_VertexData);                 
         //gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
         //gl.glColorPointer(4, GL10.GL_FLOAT, 0, m_ColorData);
         //gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
         
         gl.glNormalPointer(GL10.GL_FLOAT, 0, normals);//m_NormalData);
         //gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);

//gl.glEnable(GL10.GL_BLEND);
//gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

gl.glEnable(GL10.GL_COLOR_MATERIAL); 
//gl.glColor4f(1.0f,0.0f,1.0f,0.7f); //constant color
gl.glColor4f(m_Color.r,m_Color.g,m_Color.b,m_Color.a); 
gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

         gl.glDrawArrays(GL10.GL_TRIANGLES, 0, nVertices); //or L10.GL_TRIANGLES
         //gl.glMatrixMode(GL10.GL_MODELVIEW);
         //gl.glPointSize(5);
         //gl.glDrawArrays(GL10.GL_POINTS, 0, nVertices); //or L10.GL_TRIANGLES
     //  }
  }

    public void draw(GL10 gl)
    {
  if (m_MarchingWork1.nVertices != 0)
  {
    m_VertexData1.position(0);
    m_NormalData1.position(0);
    m_VertexData1.put(m_MarchingWork1.vertices, 0, m_MarchingWork1.nCount);
    m_NormalData1.put(m_MarchingWork1.normals, 0, m_MarchingWork1.nCount);

    drawTriangles(gl, m_VertexData1, m_NormalData1, m_MarchingWork1.nVertices);
    m_MarchingWork1.nVertices = 0; //reset
  }
  if (m_MarchingWork2.nVertices != 0) //write results of thread 2 to other floatbuffers and draw:
  {
    m_VertexData2.position(0);
    m_NormalData2.position(0);
    m_VertexData2.put(m_MarchingWork2.vertices, 0, m_MarchingWork2.nCount);
    m_NormalData2.put(m_MarchingWork2.normals, 0, m_MarchingWork2.nCount);

    drawTriangles(gl, m_VertexData2, m_NormalData2, m_MarchingWork2.nVertices);
    m_MarchingWork2.nVertices = 0; //reset
  }


if (!m_Thread1.isAlive() && !m_Thread2.isAlive())
{
//  m_Timer0.time0();
  animateLava(); //non-blocking call which starts the threads...
//  m_Timer0.time1();
//  if ( (++count & 15 ) == 0 )  Log.v(this.getClass().getName(), ""+m_Timer0);
 }
}





  public void animateLava()
  {

    if ( justStarted ||   frand(1.0f) < launchChance ) 
    {
      launchBalls();

/*
      if (justStarted) // impatient mode always on:
      {
        justStarted=false;
        while (true)
          { 
            int i;
            moveBalls(); //move balls around until quit condition is met:
            for (i = 0; i < nBalls; i++)
              {
                //metaball *b = &bp->balls[i];
                Metaball b = balls[i];

                //if (b->alive_p && !b->static_p && !b->leader &&
                //    b->z > 0.5)
                //  goto DONE;

                if (b.alive && !b.statik && b.leader == null && b.z > 0.5) break;
              }
          }
      } //if justStarted
*/
    }

    moveBalls();


    //DEBUGGING:
    //if ( (count & 15)==0 )   dumpBalls();


    //set work portions: 
/*
//    float minZ = (float)(Math.floor(m_MinZ * RESOLUTION)) - 1.0f;
    float minZ = m_MinZ * RESOLUTION - 1.0f;
    minZ = (minZ<0 ? 0 : minZ);
//    float maxZ = (float)(Math.ceil(m_MaxZ * RESOLUTION )) + 1.0f;
    float maxZ = m_MaxZ * RESOLUTION + 1.0f;

    maxZ = (maxZ>RESOLUTION ? RESOLUTION : maxZ);
    m_MidZ = minZ + ((maxZ - minZ)/2);
*/

int min,mid,max;
    min = (int)(m_MinZ * RESOLUTIONF);
    min = (min<0 ? 0 : min);
    mid = (int)(m_MidZ);
    max = (int)(m_MaxZ * RESOLUTIONF);
    max = (max>RESOLUTION ? RESOLUTION : max);
if (min<5 && max < 11)  { //edge case -- is this needed?
  min = 0;
  mid = 5;
  max = 10;
} 


//if ( count % 64 == 0 )  Log.v(this.getClass().getName(), " MIN/MID/MAX = " + min+","+mid+","+max);


    m_MarchingWorkPart1.setWorkPortions( min, mid );
    m_MarchingWorkPart2.setWorkPortions( mid, max );
/* 
m_MarchingWorkPart1.setWorkPortions(0, mid);
m_MarchingWorkPart2.setWorkPortions(mid, 40);
*/
/*
m_MarchingWorkPart1.setWorkPortions(0, 20);
m_MarchingWorkPart2.setWorkPortions(20, 40);
*/




    m_Thread1.run();
    m_Thread2.run();

  }

//debugging:
public void dumpBalls()
{
  String s = "nBalls="+nBalls+": ";
  float zTotal = 0.0f;
  int c=0;
  for (int i=0;i<balls.length;i++)
  {
    zTotal += balls[i].z; c++;
    s+="  B"+i+".z="+balls[i].z;
  }
  s+= " avgZ="+(float)(zTotal/(float)(c));
  Log.v(this.getClass().getName(),s);
}

  //Mine:--debugging only--not used
  public void addVertex3f(float x, float y, float z)
  {
//    Log(this.getClass().getName(), "addVertex3f: x="+x+" y="+y+" z="+z);
if ( x < minx ) minx=x;
if ( x > maxx ) maxx=x;

if ( y < miny ) miny=y;
if ( y > maxy ) maxy=y;

if ( z < minz ) minz=z;
if ( z > maxz ) maxz=z;
/*
    nVertices++;
    m_VertexData.put(x);
    m_VertexData.put(y);
    m_VertexData.put(z);
*/
  }

/*
  public void addNormal3f(float x, float y, float z)
  {
//    Log(this.getClass().getName(), "addNormal3f: x="+x+" y="+y+" z="+z);
nnormals++;
    m_NormalData.put(x);
    m_NormalData.put(y);
    m_NormalData.put(z);
  }
*/

  //Mine: -- DEBUGGING
/*
  private static void Log(String s, String msg)
  {
    System.out.println(s+": "+msg);
  }
*/


    
}
