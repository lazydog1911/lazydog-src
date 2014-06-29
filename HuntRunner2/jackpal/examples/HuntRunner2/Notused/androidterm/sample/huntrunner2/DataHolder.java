package lazydog.androidterm.sample.huntrunner2;

import jackpal.androidterm.emulatorview.TermSession;



public class DataHolder {
//  private static String data;
//  public static String getData() {return data;}
//  public static void setData(String myData) {data = myData;}

  private static Process exec;
  public static Process getExec() {return exec;}
  public static void setExec(Process myExec) {exec = myExec;}

  private static TermSession session;
  public static TermSession getSession() {return session;}
  public static void setSession(TermSession mySession) {session = mySession;}
}

