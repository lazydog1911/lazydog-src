package com.lazydog.lavalamp;

public class Metaball {
  public boolean alive;
  public boolean statik;

  public float r;
  public float R;

  public float z;
  public float posR;
  public float posTh;
  public float dr, dz;

  public float x,y;

  public Metaball leader;  //stay close to this other ball
  
  public void copyBall(Metaball b)
  {
    this.alive = b.alive;
    this.statik = b.statik;
    this.r = b.r;
    this.R = b.R;
    this.z = b.z;
    this.posR=b.posR;
    this.posTh= b.posTh;
    this.dr=b.dr;
    this.dz=b.dz;
    this.x=x;
    this.y=y;
  }


  public String toString() 
  {
    return ( (alive ? "" : "DEAD ") + 
        "statik="+statik+" "+
        "r="+r+" "+
	"R="+R+" "+
	"z="+z+" "+
	"posR="+posR+" "+
	"posTh="+posTh+" "+
	"dr="+dr+" "+
	"dz="+dz+" "+
	"x="+x+" "+
	"y="+y+" " );
  }

}

