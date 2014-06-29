package com.lazydog.lavalamp;


public class Color {
public float r,g,b,a;
public Color(float r,float g,float b,float a)
{
  this.r = r;
  this.g = g;
  this.b = b;
  this.a = a;
}
public Color()
{
  this.r = this.g = this.b = this.a = 0.0f;
}

public String toString () { return "("+r+","+g+","+b+","+a+")"; }

public float[] asFloats() { float f[] = { r, g, b, a }; return f; }

}


