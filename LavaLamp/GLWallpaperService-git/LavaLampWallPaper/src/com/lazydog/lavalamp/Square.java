
//package book.BouncySquare3;
package com.lazydog.lavalamp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;


import android.util.Log;
//Log.v(this.getClass().getName(), ... )


/**
 * A vertex shaded square.
 */
class Square
{

 public FloatBuffer mNormals;
public float[] m_Pos = {0.0f, 0.0f, 0.0f};
public float m_HalfSize;


    public void setPosition(float x, float y, float z)
    {
         m_Pos[0] = x;
         m_Pos[1] = y;
         m_Pos[2] = z;
    }



  public void setVertex1(float x, float y)
  {
        //mFVertexBuffer.position(0);
        mFVertexBuffer.put(0, x);
        mFVertexBuffer.put(1, y);
  }


public void setColors(Color topLeft, Color topRight, Color bottomRight, Color bottomLeft)
{
        mColorBuffer.position(0);
       mColorBuffer.put(topLeft.asFloats());
       mColorBuffer.put(topRight.asFloats());
       mColorBuffer.put(bottomRight.asFloats());
       mColorBuffer.put(bottomLeft.asFloats());
        mColorBuffer.position(0);

}

public void setXYFactors(float xfactor, float yfactor)
{
	float vertices[] = {
                -m_HalfSize*xfactor, m_HalfSize*yfactor, 0.0f,  //topLeft ,was:2
                m_HalfSize*xfactor, m_HalfSize*yfactor, 0.0f, //topRight ,was:3
                m_HalfSize*xfactor, -m_HalfSize*yfactor, 0.0f, // bottom right ,was:1
                -m_HalfSize*xfactor, -m_HalfSize*yfactor, 0.0f // bottom left, was:0
        }; 
        mFVertexBuffer.position(0);
 mFVertexBuffer.put(vertices);
        mFVertexBuffer.position(0);
}
	

    //NOTE: this one is pre-centered...
    public Square(float size)  //NOTE: must set colors before drawing..
    {
mUseTexture=false; //changed when setTexture() is called

        m_HalfSize = size/2;
        float vertices[] =
        {
/*
                -halfSize, -halfSize, 0.0f, 
                 halfSize, -halfSize, 0.0f, 
                -halfSize,  halfSize, 0.0f, 
                 halfSize,  halfSize, 0.0f,
*/
                -m_HalfSize, m_HalfSize, 0.0f,  //topLeft ,was:2
                m_HalfSize, m_HalfSize, 0.0f, //topRight ,was:3
                m_HalfSize, -m_HalfSize, 0.0f, // bottom right ,was:1
                -m_HalfSize, -m_HalfSize, 0.0f // bottom left, was:0
        }; 

/*
        float textureCoords[] = 
   		 {				
   					0.0f, 1.0f, 
   					1.0f, 1.0f, 
   					0.0f, 0.0f, 
   					1.0f, 0.0f
   		 };
*/

//new mapping: 2->0 , 3->1, 1->2, 0->3
        byte indices[] = 
        {
/*
            0, 3, 1,
            0, 2, 3
*/
            3, 1, 2,
            3, 0, 1
        };

   float normals[]= {
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f
      };


        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mFVertexBuffer = vbb.asFloatBuffer();
        mFVertexBuffer.put(vertices);
        mFVertexBuffer.position(0);
        
        vbb = ByteBuffer.allocateDirect(16 * 4);
        vbb.order(ByteOrder.nativeOrder());
        mColorBuffer = vbb.asFloatBuffer();
        //mColorBuffer.put(colors);
        //mColorBuffer.position(0);
        //setColors(topLeft,topRight,bottomRight,bottomLeft);

        mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
        mIndexBuffer.put(indices);
        mIndexBuffer.position(0);
        
/*
        vbb = ByteBuffer.allocateDirect(colors.length * 2);
        vbb.order(ByteOrder.nativeOrder());
        mTextureCoords = vbb.asFloatBuffer();
        mTextureCoords.put(textureCoords);
        mTextureCoords.position(0);
*/

        mNormals = makeFloatBuffer(normals);

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


    public void draw(GL10 gl)
    {
/* draw textures: /*/
/*
if (mUseTexture )
{
    	gl.glEnable(GL10.GL_TEXTURE_2D);
	    gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
	    gl.glTexCoordPointer(2, GL10.GL_FLOAT,0,mTextureCoords);
	    gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
}
*/
	    

         gl.glFrontFace(GL11.GL_CW);
         gl.glNormalPointer(GL10.GL_FLOAT, 0, mNormals);
       //  gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        gl.glVertexPointer(3, GL11.GL_FLOAT, 0, mFVertexBuffer);
gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

       gl.glColorPointer(4, GL11.GL_FLOAT, 0, mColorBuffer);
        gl.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_BYTE, mIndexBuffer);
        gl.glFrontFace(GL11.GL_CCW);
    }

//XXX:NOT USED:
    public void setTexture(GL10 gl,Context context,int resourceID)
    {
    	createTexture(gl,context,resourceID); 
    }

    private int[] textures = new int[1];

    public int createTexture(GL10 gl, Context contextRegf, int resource) 
    {
mUseTexture=true;
    	Bitmap tempImage = BitmapFactory.decodeResource(contextRegf.getResources(), resource); // 1

    	gl.glGenTextures(1, textures, 0); 					// 2
    	gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]); 	// 3

    	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, tempImage, 0); // 4
    		
    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR); // 5a
    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR); // 5b	

    	tempImage.recycle();//6

    	return resource;
    }
    
    private FloatBuffer mFVertexBuffer;
    private FloatBuffer mColorBuffer;
    private FloatBuffer mTextureCoords;
    private ByteBuffer  mIndexBuffer;
private boolean mUseTexture;
}
