package com.lazydog.lavalamp;

public class MarchingWork
{
  public Triangle tri[];
  public GridCell cell;
  public Point vertpool[];
  public Point vertlist[];
  public float layers[];
 

  public float vertices[];
  public float normals[];
  public int nVertices, nCount;

  public MarchingWork()
  {
    tri = new Triangle[6];
    cell = new GridCell();
    vertlist = new Point[12]; 
    vertpool = new Point[12]; 
    layers = new float[ LavaLamp.RESOLUTION * LavaLamp.RESOLUTION * 2 ];

    vertices = new float[ 3 * LavaLamp.MAX_VERTICES  ]; 
    normals = new float[ 3 * LavaLamp.MAX_VERTICES  ]; 

    for (int j=0;j<6;j++ )   tri[j] = new Triangle();
    for (int k=0;k<12 ;k++)   vertpool[k] = new Point();

    nVertices=0; nCount=0;
  }


}


