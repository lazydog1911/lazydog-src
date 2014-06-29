package com.lazydog.lavalamp;

public class MarchingWorkPart implements Runnable
{
  MarchingWork mMarchingWork;
  LavaLamp mLavaLamp; //so marchingCubes can call .objCompute() and .objInit() (lookups- only)
  int mStartZ, mEndZ;

  public void setMarchingWork(MarchingWork work)
  {
    this.mMarchingWork = work;
  }

  public void setLavaLamp(LavaLamp lamp)
  {
    mLavaLamp = lamp;
  }

  public void setWorkPortions(int startZ, int endZ)
  {
    mStartZ = startZ; mEndZ = endZ;
  }

 
  public void run()
  {
    //Marching.marchingCubes ( work, ..., startz, endz )
// Marching.marchingCubes (mMarchingWork, mLavaLamp , 0, LavaLamp.RESOLUTION );


 Marching.marchingCubes (mMarchingWork, mLavaLamp ,  mStartZ, mEndZ);



  }

}

