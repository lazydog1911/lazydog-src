//was: LaunchActivity
package lazydog.androidterm.sample.huntrunner2;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


//
import java.util.Properties;
import android.content.res.Resources;
import android.content.res.AssetManager;
import android.widget.TextView;

//for DataHolder:
import jackpal.androidterm.emulatorview.TermSession;


//for looking up IP:
import java.util.Enumeration;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.InetAddress;
import android.text.format.Formatter;

//
//import android.app.AlertDialog;
//import android.app.AlertDialog.Builder;
//import android.content.DialogInterface;
//import android.content.DialogInterface.*;

//for launching browser activity:
import android.net.Uri;


//for checking of ARM processor:
import android.widget.Toast;




/**
 * Provides a UI to launch the terminal emulator activity, connected to
 * either a local shell or a Telnet server.
 */
public class HuntRunner2Activity extends Activity
{
    private static final String TAG = "TelnetLaunchActivity";
public Process mProcess = null;


public String getLocalIpAddressText() {
    String retval="";
    try {
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
            NetworkInterface intf = en.nextElement();
            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                InetAddress inetAddress = enumIpAddr.nextElement();
                if (!inetAddress.isLoopbackAddress()) {
                    //String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                    String ip = inetAddress.getHostAddress();
                    Log.i(TAG, "***** IP="+ ip);
                    //return ip;
                    retval=ip+","+retval;;
                }
            }
        }
    } catch (SocketException ex) {
        Log.e(TAG, ex.toString());
    }
    if ( retval.length() == 0 )  return "Unknown, check connection!";
    return retval.substring(0, retval.length()-1); //chop
}

public void onDestroy()
{

  

//  Log.w("HuntRunner2","HuntRunner2Activity: onDestroy DATA="+DataHolder.getData());

  if ( mProcess != null ) 
  {
    Log.w("HuntRunner2","onDestroy.killing server");
    mProcess.destroy();
  }

  //termactivity doesn't (always) get the onDestroy event for some reason:

  Process exec = DataHolder.getExec();
  Log.w("HuntRunner2","onDestroy. exec="+exec);
  if ( exec != null ) 
  {
    Log.w("HuntRunner2","onDestroy. killing client process: "+exec);
    exec.destroy();
  }

  TermSession session = DataHolder.getSession();
  if ( session != null )
  {
    Log.w("HuntRunner2","onDestroy. killing session: "+session);
    session.finish();
  }


/*
  //this is stupid:
        Intent intent = new Intent(this, TermActivity.class);
                intent.putExtra("keyMessage", "onDestroy");
                startActivity(intent);

  try { Thread.sleep(4000); } catch (InterruptedException  e) {}
*/

  
  super.onDestroy();

}

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch_activity);
        final Context context = this;
/*
        addClickListener(R.id.launchLocal, new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, TermActivity.class);
                intent.putExtra("type", "local");
                startActivity(intent);
            }});
*/


        final EditText hostEdit = (EditText) findViewById(R.id.hostname);
/*
        addClickListener(R.id.hostname, new OnClickListener() {
            public void onClick(View v) {
              EditText text = (EditText)v;
              if (text.getText() != null && text.getText().equals(getString(R.string.default_telnet_hostname)))
              {
                text.setText("");
              }
            }});
*/

/*
        //set about button onclick
        Button myButton = (Button) findViewById(R.id.aboutButton);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               AlertDialog dialog = new AlertDialog.Builder(context).setMessage("R.string.someText")
                .setPositiveButton(android.R.string.ok, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // Do stuff if user accepts
                    }
                }).setNegativeButton(android.R.string.cancel, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // Do stuff when user neglects.
                    }
                }).setOnCancelListener(new OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                        // Do stuff when cancelled
                    }
                }).create();
                dialog.show();
            }});
*/
               


        hostEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               EditText text = (EditText)v;
               Log.v("HuntRunner2Activity", "getText=*"+text.getText()+"* vs *"+getString(R.string.default_telnet_hostname)+"* "+text.getText().length()+" "+getString(R.string.default_telnet_hostname).length());
               //if (text.getText() != null && text.getText().equals(getString(R.string.default_telnet_hostname)))
               if (text.getText().toString().equals(getString(R.string.default_telnet_hostname)))
               {
                   //Log.v("HuntRunner2Activity", "setting text");
                   text.setText("");
               }
            }});


        //set up help button on click
        Button myButton = (Button) findViewById(R.id.helpButton);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String url = "http://manpages.ubuntu.com/manpages/hardy/man6/hunt.6.html";
               Intent i = new Intent(Intent.ACTION_VIEW);
               i.setData(Uri.parse(url));
               startActivity(i);
            }});


        //set up license button on click
        Button licenseButton = (Button) findViewById(R.id.licenseButton);
        licenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, LicenseDialogActivity.class);
                startActivity(i);
            }});


        addClickListener(R.id.launchTelnet, new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, TermActivity.class);
                intent.putExtra("type", "telnet");
                String hostname = hostEdit.getText().toString();
                intent.putExtra("host", hostname);
                startActivity(intent);
            }});

        addClickListener(R.id.startServer, new OnClickListener() {
            public void onClick(View v) {
TextView localIPLabel = (TextView) findViewById(R.id.localIPLabel);
if (mProcess == null )
{
	String cmdline=getDataDir(v.getContext())+"/bin/huntd-arm";
	ProcessBuilder processBuilder = new ProcessBuilder(cmdline, "-s");
        Button myButton = (Button) findViewById(R.id.startServer);
	try{
	  mProcess=  processBuilder.start();
	} 
	catch (java.io.IOException e) { Log.e("HuntRunner2","canot start server: "+e);
          myButton.setText("ERROR");
          return;
	} 
	Log.w("HuntRunner2", "p="+mProcess);
myButton.setText("Stop "+mProcess);


localIPLabel.setText("Server IP may be: "+getLocalIpAddressText());
        
}
else
{
mProcess.destroy();
Button myButton = (Button) findViewById(R.id.startServer);
myButton.setText("Start Server");
mProcess=null;
localIPLabel.setText("");
}
            }});
             
                

        // Unpack the binary executable if not already done
        setupBinDir();

	//get the build info and show it
        String buildTimestamp = "unknown";
	Resources resources = this.getResources();
	AssetManager assetManager = resources.getAssets();

	// Read from the /res/raw directory
	try {
	    InputStream rawResource = resources.openRawResource(R.raw.buildtimestamp);
	    Properties properties = new Properties();
	    properties.load(rawResource);
	    Log.w("HuntRunner2","The properties are now loaded");
	    Log.w("HuntRunner2","properties: " + properties);
            buildTimestamp = properties.getProperty("tstamp");
            TextView helpText = (TextView) findViewById(R.id.help);
            helpText.setText(helpText.getText()+" (Build Timestamp: "+buildTimestamp+")");

/*
	} catch (NotFoundException e) {
	    Log.e("HuntRunner2","Did not find raw resource: "+e);
*/
	} catch (IOException e) {
	    Log.e("HuntRunner2","Failed to open microlog property file");
	}
    }

    private void addClickListener(int buttonId, OnClickListener onClickListener) {
        ((Button) findViewById(buttonId)).setOnClickListener(onClickListener);
    }

    /**
     * Stuff to grab the 'execpty' binary for this architecture and unpack it
     * into bin/ under our data directory.  See TermActivity to see how we use
     * this program.
     */
    static String getDataDir(Context context) {
        /* On API 4 and later, you can just do this */
        // return context.getApplicationInfo().dataDir;

        String packageName = context.getPackageName();
        PackageManager pm = context.getPackageManager();
        String dataDir = null;
        try {
            dataDir = pm.getApplicationInfo(packageName, 0).dataDir;
        } catch (Exception e) {
            // Won't happen -- we know we're installed
        }
        return dataDir;
    }

    private void setupBinDir() {
        String dataDir = getDataDir(this);
        File binDir = new File(dataDir, "bin");
        if (!binDir.exists()) {
            try {
                binDir.mkdir();
                //chmod("755", binDir.getAbsolutePath());
            } catch (Exception e) {
              Log.e("HuntRunner2","setupBinDir: "+e.toString());
            }
        }
      

        /** //TODO LOL:
         * NB: If you actually plan on deploying an app which ships a binary
         * this way, you will want to implement versioning of the binary so
         * that you aren't writing it out every time the app is run.
         */
       


        Log.w(this.getClass().getName(), "about tO call getArch()");

        File binary = new File(binDir, "execpty");
        String arch = getArch();

     
        Log.v("HuntRunner2Activity", "arch = "+arch);
        Log.v(this.getClass().getName(), "setupBinDir: arch="+arch);

        //bail if not ARM:
        if ( (arch == null) || (arch != null && !arch.equals("arm")) ) {
          Toast.makeText(getApplicationContext(), "Currently,Only ARM(HF) processors are supported. Sorry, your processor type is: "+arch, Toast.LENGTH_LONG).show();
          finish();
          return;   
        }


       
        try {
            InputStream src = getAssets().open("execpty-" + arch);
            FileOutputStream dst = new FileOutputStream(binary);
            copyStream(dst, src);
            Log.w("HuntRunner2","setupBinDir(part2): about to chmod: "+binary.getAbsolutePath());
            //chmod("755", binary.getAbsolutePath());
            binary.setExecutable(true, false);
        } catch (Exception e) {
          Log.e("HuntRunner2","setupBinDir(part2): "+e.toString());
        }

        File binary2 = new File(binDir, "hunt-arm");
        try {
            InputStream src = getAssets().open("hunt-arm");
            Log.w("HuntRunner2","setupBinDir(part3): src="+src);
            FileOutputStream dst = new FileOutputStream(binary2);
            Log.w("HuntRunner2","setupBinDir(part3): dst="+dst);
            copyStream(dst, src);
            Log.w("HuntRunner2","setupBinDir(part3): about to chmod: "+binary2.getAbsolutePath());
            //chmod("755", binary.getAbsolutePath());
	    binary2.setExecutable(true, false);
        } catch (Exception e) {
          Log.e("HuntRunner2","setupBinDir(part3): "+e.toString());
        }

        //now the huntd-arm file:
        File binary4 = new File(binDir, "huntd-arm");
        try {
            InputStream src = getAssets().open("huntd-arm");
            Log.w("HuntRunner2","setupBinDir(part3): src="+src);
            FileOutputStream dst = new FileOutputStream(binary4);
            Log.w("HuntRunner2","setupBinDir(part3): dst="+dst);
            copyStream(dst, src);
            Log.w("HuntRunner2","setupBinDir(part3): about to chmod: "+
binary4.getAbsolutePath());
            //chmod("755", binary.getAbsolutePath());
            binary4.setExecutable(true, false);
        } catch (Exception e) {
          Log.e("HuntRunner2","setupBinDir(part3): "+e.toString());
        }



	//now the terminfo file
        File vDir = new File(dataDir, "v");
        if (!vDir.exists()) {
            try {
                vDir.mkdir();
                //chmod("755", binDir.getAbsolutePath());
            } catch (Exception e) {
              Log.e("HuntRunner2","setupBinDir: "+e.toString());
            }
        }
        else {
	Log.w("HuntRunner2","setupBinDir(part 4): skipping...");
	}

        File binary3 = new File(vDir, "vt100");
        try {
            InputStream src = getAssets().open("vt100");
            Log.w("HuntRunner2","setupBinDir(part5): src="+src);
            FileOutputStream dst = new FileOutputStream(binary3);
            Log.w("HuntRunner2","setupBinDir(part5): dst="+dst);
            copyStream(dst, src);
            Log.w("HuntRunner2","setupBinDir(part5): about to chmod: "+binary3.getAbsolutePath());
            //chmod("755", binary.getAbsolutePath());
            binary3.setExecutable(true, false);
        } catch (Exception e) {
          Log.e("HuntRunner2","setupBinDir(part5): "+e.toString());
        }




    }

    private String getArch() {
        /* Returns the value of uname -m */
        String machine = System.getProperty("os.arch");
        Log.d(TAG, "os.arch is " + machine);

        /* Convert machine name to our arch identifier */
        if (machine.matches("armv[0-9]+(tej?)?l")) {
            return "arm";
        } else if (machine.matches("i[3456]86")) {
            return "x86";
        } else if (machine.equals("OS_ARCH")) {
            /* This is what API < 5 devices seem to return.  Presumably all
               of these are ARM devices. */
            return "arm";
        } else {
            /* Result is correct for mips, and this is probably the best thing
               to do for an unknown arch */
            return machine;
        }
    }

    private void copyStream(OutputStream dst, InputStream src) throws IOException {
        byte[] buffer = new byte[4096];
        int bytesRead = 0;
        while ((bytesRead = src.read(buffer)) >= 0) {
            dst.write(buffer, 0, bytesRead);
        }
        dst.close();
    }

//XXX:depends on busybox?
//maybe try:http://stackoverflow.com/questions/664432/how-do-i-programmatically-change-file-permissions
    private void chmod(String... args) throws IOException {

/* XXX:NOT USED */

//check who am i
//String[] cmdline0 = new String[1];
//cmdline0[0]="/system/xbin/whoami";
//Process p= ProcessBuilder(cmdline).start();
//OutputStream s =p.getOutputStream();
//TODO: ...



/*
        String[] cmdline = new String[args.length + 1];
//        cmdline[0] = "/system/bin/chmod";
        cmdline[0] = "/system/xbin/chmod";
        System.arraycopy(args, 0, cmdline, 1, args.length);
        new ProcessBuilder(cmdline).start();
*/


    }
}
