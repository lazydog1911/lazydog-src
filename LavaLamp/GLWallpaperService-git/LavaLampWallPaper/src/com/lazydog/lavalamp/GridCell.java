package com.lazydog.lavalamp;
public class GridCell {
  public Point p[];
  public float val[];
  public GridCell ()
  {
    p = new Point[8];
    for (int i=0; i<8; i++)
      p[i] = new Point();
    val = new float[8];
  }
    
}
