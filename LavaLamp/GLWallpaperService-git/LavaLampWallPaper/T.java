import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class T
{
  public void hexToFloat(String hex)
  {
    
      int value = Integer.parseInt(hex, 16);  
      float f = (float) ( (float)(value)/256.0f );

      System.out.println(hex+" -> "+value+" -> "+f);
      
  }

  public void doSomething()
  {
//o    System.out.println("Hey1");
   //Pattern c = Pattern.compile("#*([0-9][a-f]{2})*([0-9][a-f]{2})*([0-9][a-f]{2})*([0-9][a-f]{2})");
//   Pattern c = Pattern.compile("#*(\\p{XDigit}{2})*(\\p{XDigit}{2})*(\\p{XDigit}{2})*(\\p{XDigit}{2})");
Pattern c = Pattern.compile("(\\p{XDigit}{2})");
String input="#001122FF";
    Matcher m = c.matcher(input);

try {
m.find(); hexToFloat(m.group());
m.find(); hexToFloat(m.group());
m.find(); hexToFloat(m.group());
m.find(); hexToFloat(m.group());
} catch (IllegalStateException e)  { //...
}


/*
while (m.find()) { System.out.println("Found: "+m.group()); }
System.out.println(m.groupCount());
*/
  }

  public static void main(String[] args)
  {
    T myT = new T();
    myT.doSomething();
  }
}
