package com.lazydog.lavalamp;

import android.util.Log;

public class  Timer
{
public final String TAG = "TIMER";
//Debug
public long m_LastTime;
public float m_Sum = 0;
public long m_Count = 0;
public void time0()
{
  m_LastTime = System.currentTimeMillis();
}

public void time1()
{
  long t_diff = System.currentTimeMillis()-m_LastTime;
  m_Sum += t_diff;
  ++m_Count;
  m_LastTime=System.currentTimeMillis();
}

public String toString()
{
  return ("Timer: Avg: "+(m_Sum/m_Count)+") ");

}

}
