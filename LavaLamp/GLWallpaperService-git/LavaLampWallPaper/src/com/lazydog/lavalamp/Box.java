package com.lazydog.lavalamp;

import java.util.*;
import java.nio.*;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES11;


//Box: draw a box from (0,0,0) to (width, height, length)
public class Box 
{
    public FloatBuffer m_VertexData;
    public FloatBuffer m_FacesVertexData;
    public FloatBuffer m_FacesNormalData;
    public FloatBuffer m_ColorData;
    public float[] m_Pos = {0.0f, 0.0f, 0.0f};
    public float width, height, length;

    public Box(float width, float height, float length)
    {
      this.width = width;
      this.height = height;
      this.length = length;
            
      float vertexData[] =  {
        //near face:
        0.0f, 0.0f, 0.0f, //line segment 1
	width, 0.0f, 0.0f,

	0.0f, 0.0f, 0.0f, //line segment 2
        0.0f, height, 0.0f,

        0.0f, height, 0.0f, 
	width, height, 0.0f,

	width, height, 0.0f,
        width, 0.0f, 0.0f,

        //far face:
        0.0f, 0.0f, length, //line segment 1
	width, 0.0f, length,

	0.0f, 0.0f, length, //line segment 2
        0.0f, height, length,

        0.0f, height, length, 
	width, height, length,

        width, height, length,
	width, 0.0f, length,

        //4 connecting beams:
        0.0f, 0.0f, 0.0f,
        0.0f, 0.0f, length,

        width, 0.0f, 0.0f,
        width, 0.0f, length,

        width, height, 0.0f,
        width, height, length,

        0.0f, height, 0.0f,
        0.0f, height, length

      };

      //float colorData[] = new float[ 24 * 4 ];
      

      //Triangle far-face
      float facesVertexData[] = {
        0.0f, 0.0f, length,
        width, 0.0f, length,
        width, height,length,

        width, height, length,
        0.0f, height, length,
        0.0f, 0.0f, length
      };
      float facesNormalData[]= {
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f
      };
float alpha=0.5f;
float colorData[]  = {
  1.0f, 0.0f, 0.0f, alpha,
  0.0f, 1.0f, 0.0f, alpha,
  0.0f, 0.0f, 1.0f, alpha,
  0.5f, 0.5f, 0.0f, alpha,
  0.0f, 0.5f, 0.5f, alpha,
  0.5f, 0.5f, 0.5f, alpha
 } ;

      
/*
      for (int i=0;i < 24 * 4; i+=4)
      {
        colorData[i] = 0.3f;
        colorData[i+1] = 0.0f;
        colorData[i+2] = 0.f;
        colorData[i+3] = 0.3f;
      }
*/
      m_VertexData = makeFloatBuffer(vertexData); 	             
      m_FacesVertexData = makeFloatBuffer(facesVertexData); 	             
      m_FacesNormalData = makeFloatBuffer(facesNormalData);
      m_ColorData = makeFloatBuffer(colorData);

    }

    //TODO:, setDimensions()

    public void draw(GL10 gl) 
    {
         gl.glMatrixMode(GL10.GL_MODELVIEW);
       // gl.glDisable(GL10.GL_LIGHTING);
 
 
         m_VertexData.position(0);
//         m_ColorData.position(0);

         gl.glVertexPointer(3, GL10.GL_FLOAT, 0, m_VertexData);
         gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

 //        gl.glColorPointer(4, GL10.GL_FLOAT, 0, m_ColorData);        
   //      gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

        
         gl.glColor4f(1.0f,1.0f,1.0f,1.0f);
         gl.glDrawArrays(GL10.GL_LINES, 0, 24);


//gl.glEnable(GL10.GL_LIGHTING);
//now draw triangles:
/*
int nVertices = 6;

gl.glEnable(GL10.GL_LIGHTING);
    gl.glVertexPointer(3, GL10.GL_FLOAT, 0, m_FacesVertexData);

         gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

         gl.glColorPointer(4, GL10.GL_FLOAT, 0, m_ColorData);
         gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
 gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
         gl.glDrawArrays(GL10.GL_TRIANGLES, 0, nVertices);


gl.glDrawArrays(GL10.GL_TRIANGLES , 0, nVertices); 
*/

         

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
