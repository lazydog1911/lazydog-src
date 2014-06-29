package com.lazydog.lavalamp;

import java.util.*;
import java.nio.*;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES11;


import android.util.Log;



//GuideRail: quarter-circle 
public class GuideRail 
{
    public FloatBuffer m_VertexData;
    public float[] m_Pos = {0.0f, 0.0f, 0.0f};
    public float yscale , zscale;
    public int m_nVertices = 0;
    public float m_T = 0.0f;
    public float m_endT ; //draw 'till what 't'
    public float m_markerY = 0.0f, m_markerZ = 0.0f;

    public static final int LINE_SEGMENTS = 100;

    public GuideRail()
    {
      float vertices[] = vertices = new float[ 3 * LINE_SEGMENTS * 2 ]; 
      float x = 0.0f, y, z;
     

      yscale =  50.0f;
      zscale =  25.0f;
      m_endT = 3.14f;
      float zoffset = zscale - 40.0f;

      float step = m_endT / (float)(LINE_SEGMENTS);
      m_endT-=step;


      Log.v(this.getClass().getName(),"GUIDE START step="+step);


      int i=0;
      for (float t = 0.0f; t  <= m_endT; t+= step)
      {
        z = zscale * (float)(Math.cos(m_endT - t)) + zoffset;
        y = - yscale * (float)(Math.sin(m_endT - t));

        vertices[i] = x; vertices[i+1] = y; vertices[i+2] = z;
        m_nVertices++;
 
        i+=3;
        if ( i == 3 )  continue;

//        Log.v(this.getClass().getName(),"GUIDE rail:  t="+t+" adjustedt="+(endt - t + startt)+" x="+x+" y="+y+" z="+z);
 
        vertices[i] = x; vertices[i+1] = y; vertices[i+2] = z;
        m_nVertices++;
        i+=3;
      }

      m_VertexData = makeFloatBuffer(vertices); 	             

      Log.v(this.getClass().getName(), " nVertices="+m_nVertices);

    }

    public void setT(float t)
    {
      m_T = t;
    }

    public void draw(GL10 gl) 
    {
         gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glDisable(GL10.GL_LIGHTING);
 
 
         m_VertexData.position(0);

         gl.glVertexPointer(3, GL10.GL_FLOAT, 0, m_VertexData);
         gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

         //TODO:gl.glDisableClientState(COLOR_ARRAY);gl.color4f(a,b,c,d); 
         gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
 
         int startMarker = (int) ( (m_T / m_endT) * (float)(m_nVertices));
         startMarker-=startMarker % 2;
         m_markerY = m_VertexData.get(startMarker * 3 + 1);
         m_markerZ = m_VertexData.get(startMarker * 3 + 2);



         gl.glColor4f(1.0f,1.0f,1.0f,1.0f);

         gl.glDrawArrays(GL10.GL_LINES, 0, startMarker );

         gl.glColor4f(1.0f,0.0f,0.0f,1.0f);
         

         gl.glDrawArrays(GL10.GL_LINES, startMarker, m_nVertices - startMarker );

         gl.glEnable(GL10.GL_LIGHTING);

    }

    public float getMarkerY()
    {
      return m_markerY;
    }
    public float getMarkerZ()
    {
      return m_markerZ;
    }


    public void setPosition(float x, float y, float z) 
    {
         m_Pos[0] = x;
         m_Pos[1] = y;
         m_Pos[2] = z;        
    }

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
