package com.lazydog.lavalamp;


public class Point {
public float x,y,z;
public Point(float x,float y,float z) 
{
  this.x=x;
  this.y=y;
  this.z=z;
}
public Point()
{
  x = 0.0f;
  y = 0.0f;
  z = 0.0f;
}

public String toString () { return "("+x+","+y+","+z+")"; }
}

