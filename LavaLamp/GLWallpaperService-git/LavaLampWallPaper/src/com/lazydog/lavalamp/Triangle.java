package com.lazydog.lavalamp;

public class Triangle {
  public Point p[];

  public Triangle () 
  {
    p = new Point[4];
    for (int i=0;i<4;i++)  
      p[i] = new Point();
  }

  public String toString()
  { 
    return p[0]+", "+p[1]+", "+p[2]+", "+p[3];

  }

}
