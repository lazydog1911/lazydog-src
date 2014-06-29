//package jackpal.androidterm.sample.telnet;
package lazydog.androidterm.sample.huntrunner2;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.method.TextKeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import jackpal.androidterm.emulatorview.EmulatorView;
import jackpal.androidterm.emulatorview.TermSession;


//
import android.view.MotionEvent;
import android.content.Context;
import android.widget.LinearLayout;
//import lazydog.androidterm.sample.huntrunner2.InterceptContainer;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.view.ViewGroup;





/**
 * This sample activity demonstrates the use of EmulatorView.
 *
 * This activity also demonstrates how to set up a simple TermSession connected
 * to a local program.  The Telnet connection demonstrates a more complex case;
 * see the TelnetSession class for more details.
 */
public class TermActivity extends Activity
{
    private EditText mEntry;
    private EmulatorView mEmulatorView;
    private TermSession mSession;
//    private InterceptContainer interceptContainer;
  
    //
    public Process mExec = null;

/*
public void onStop()
{
  super.onStop();
  Log.w("HuntRunner2","TermActivity: onStop: mExec="+mExec);
  if ( mExec != null )
  {
    Log.w("HuntRunner2","TermActivity: onStop.killing client");
    mExec.destroy();
    mExec = null;
  }
}
*/


//XXX:doesnt capture events from child views 
    public boolean onTouch(View v, MotionEvent event)
    {
        Log.w("HuntRunner2","onTouch: view="+v+" event="+event);
        return true;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent myIntent = getIntent();
/*
        Bundle extras = myIntent.getExtras(); 
        //todo: this is stupid:
        String msg = extras.getString("keyMessage");
        Log.v("TermActivity", "msg="+msg);
        if ( msg != null && msg.equals("onDestroy") )
        {
          if (mSession != null) {
            mSession.finish();
          }
          if (mExec != null) {
            mExec.destroy();
          }

          finish();
          super.onCreate(savedInstanceState);
          Log.v("TermActivity","TermActivity: onDestroy received.");
          return;
        }
*/
        
        

	//
	String hostname = "";

        
        setContentView(R.layout.term_activity);
        //XXX: not needed after all
        //interceptContainer = new InterceptContainer(this);
        //Log.w("HuntRunner2: interceptContainer="+interceptContainer);
        //setContentView(new InterceptContainer(this));

        /* Text entry box at the bottom of the activity.  Note that you can
           also send input (whether from a hardware device or soft keyboard)
           directly to the EmulatorView. */

/*
        mEntry = (EditText) findViewById(R.id.term_entry);
        mEntry.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int action, KeyEvent ev) {
                // Ignore enter-key-up events
                if (ev != null && ev.getAction() == KeyEvent.ACTION_UP) {
                    return false;
                }
                // Don't try to send something if we're not connected yet
                TermSession session = mSession;
                if (mSession == null) {
                    return true;
                }

                Editable e = (Editable) v.getText();
                // Write to the terminal session
                session.write(e.toString());
                session.write('\r');
                TextKeyListener.clear(e);
                return true;
            }
        });

        // Sends the content of the text entry box to the terminal, without
        //   sending a carriage return afterwards 
        Button sendButton = (Button) findViewById(R.id.term_entry_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Don't try to send something if we're not connected yet
                TermSession session = mSession;
                if (mSession == null) {
                    return;
                }
                Editable e = (Editable) mEntry.getText();
                session.write(e.toString());
                TextKeyListener.clear(e);
            }
        });
*/


        /**
         * EmulatorView setup.
         */
        final EmulatorView view = (EmulatorView) findViewById(R.id.emulatorView);
        mEmulatorView = view;

        /* Let the EmulatorView know the screen's density. */
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Log.w("HuntRunner2", "EmulatorView setup: view="+view+" metrics="+metrics);
        view.setDensity(metrics);

        /* Create a TermSession. */
        //getintent
        String sessionType = myIntent.getStringExtra("type");
        TermSession session;

        if (sessionType != null && sessionType.equals("telnet")) {
            /* Telnet connection: we need to do the network connect on a
               separate thread, so kick that off and wait for it to finish. */
            //connectToTelnet(myIntent.getStringExtra("host"));
            //return;
	    hostname=myIntent.getStringExtra("host");
        } 

        // Create a local shell session.
        session = createLocalTermSession(hostname);
        mSession = session;
        DataHolder.setSession(session);

/*
	Button button1 = (Button) findViewById(R.id.button1);
	button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
		Log.w("HuntRunner2", "button1 clicked.");
//InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);

	    }
  	});
*/

/*
	String[] letters = new String[] {
	"", "", "k", "", "",
"", "", "K", "", "",
"h", "H", "", "L", "l",
"", "", "J", "", "",
"", "", "j", "", "",
"", "1", "2", "3", "4"

};


	//make D-pad
	GridView grid = (GridView) findViewById(R.id.gridview);
	ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, letters);
	grid.setAdapter(adapter);
	grid.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView parent, View v, int position, long id) {

               Toast.makeText(getApplicationContext(),

                ((TextView) v).getText(), Toast.LENGTH_SHORT).show();

            }

        });
*/

int[] ids = {R.id.qText,R.id.yText,R.id.nText};

for (int i=0;i<ids.length;i++)
{
  int id=ids[i];
  Log.w("HuntRunner2","setting up id "+id);
	
  //find the show and hide buttons and set up their listeners:
  TextView iText = (TextView) findViewById(id);
  if (iText != null)
  {
  iText.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                 // Don't try to send something if we're not connected yet

                  TermSession session = mSession;
                  if (mSession == null) {
                    return;
                  }
                  String text = (String) ((TextView) v).getText();
	          Log.w("HuntRunner2","sending text: "+text);
                  session.write(text);


		}});
  }
  else{
    Log.w("Huntrunner2","Cant get iText");
  }
}


//find the hide buttons and set up their listeners:
TextView retText = (TextView) findViewById(R.id.retText);
if (retText != null)
{
retText.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
//Log.w("HuntRunner2","click on retText, about to send ret...");
                 // Don't try to send something if we're not connected yet
                  TermSession session = mSession;
                  if (mSession == null) {
                    return;
                  }
                  session.write("\n");


		}});
}
else{
  Log.w("Huntrunner2","Cant get retText");
}


//find the textviews and listen to them:
	TableLayout layout = (TableLayout) findViewById(R.id.tableLayout1);
int count = layout.getChildCount();
for (int i = 0; i <= count; i++) {
    ViewGroup v = (ViewGroup) layout.getChildAt(i);
    if (v instanceof TableRow) {
    	int count2 = v.getChildCount();
	for (int j=0;j<=count2;j++)  {
	     TextView textView = (TextView) v.getChildAt(j);
	     Log.w("HuntRunner2","tableLayout: row"+i+" position"+j+"="+textView);
             if (textView != null && !textView.getText().equals(" "))  {
               textView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
		  String text = (String) ((TextView) v).getText();
                  Log.w("HuntRunner2", "button clicked.text="+text);
                  // Don't try to send something if we're not connected yet
                  TermSession session = mSession;
                  if (mSession == null) {
                    return;
                  }
                  session.write(text);


                }
                });

              }

    }
  }
}

	

//hide keyboard after 2s
/*
new Thread(new Runnable() {
    @Override
    public void run() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }
}).start();
*/


        //listening to view works:
/*
        view.setOnTouchListener(new View.OnTouchListener() {
          public boolean onTouch(View v, MotionEvent event)  {
            Log.w("HuntRunner2","View.OnTouchListener: view="+v+" event="+event);
            return true;
          }
          });
*/

        /* Attach the TermSession to the EmulatorView. */
        view.attachSession(session);

        /* That's all you have to do!  The EmulatorView will call the attached
           TermSession's initializeEmulator() automatically, once it can
           calculate the appropriate screen size for the terminal emulator. */
    }

    @Override
    protected void onResume() {
        Log.v("TermActivity","onResume");
        super.onResume();

        /* You should call this to let EmulatorView know that it's visible
           on screen. */
        mEmulatorView.onResume();

        //mEntry.requestFocus();
    }

    @Override
    protected void onPause() {
        Log.v("TermActivity","onPause");
        /* You should call this to let EmulatorView know that it's no longer
           visible on screen. */
        mEmulatorView.onPause();

        super.onPause();
    }

   // @Override
   // protected 
   public void onDestroy() {
        Log.w("HuntRunner2","TermActivity: onDestroy");
        /**
         * Finish the TermSession when we're destroyed.  This will free
         * resources, stop I/O threads, and close the I/O streams attached
         * to the session.
         *
         * For the local session, closing the streams will kill the shell; for
         * the Telnet session, it closes the network connection.
         */
        if (mSession != null) {
            mSession.finish();
            DataHolder.setSession(null);
        }
  if ( mExec != null )
  {
    Log.w("HuntRunner2","TermActivity: onDestroy. killing client process: "+mExec);
    mExec.destroy();
    DataHolder.setExec(null);
  }

        super.onDestroy();
    }

    /**
     * Create a TermSession connected to a local shell.
     */
    private TermSession createLocalTermSession(String hostname) {
        /* Instantiate the TermSession ... */
        TermSession session = new TermSession();

        /* ... create a process ... */
        String execPath = HuntRunner2Activity.getDataDir(this) + "/bin/execpty";
Log.w("HuntRunner2","execPath="+execPath);

	String huntPath = HuntRunner2Activity.getDataDir(this) + "/bin/hunt-arm";
	Log.w("HuntRunner2","huntPath="+huntPath);

        ProcessBuilder execBuild =
                new ProcessBuilder(execPath, "/system/bin/sh", "-c", "TERM=vt100 TERMINFO="+HuntRunner2Activity.getDataDir(this)+" ROWS=24 COLS=80 "+huntPath+" "+hostname);

//
//ProcessBuilder execBuild =new ProcessBuilder(execPath, "/system/xbin/telnet", "towel.blinkenlights.nl");
//ProcessBuilder execBuild =new ProcessBuilder(execPath, "/system/xbin/bash","-c","echo ABCDEFGHIJKLMNOPQRSTUVWYZYABCDEFGHIJKLMNOPQRSTUVWYZYABCDEFGHIJKLMNOPQRSTUVWYZYABCDEFGHIJKLMNOPQRSTUVWYZYABCDEFGHIJKLMNOPQRSTUVWYZYABCDEFGHIJKLMNOPQRSTUVWYZYABCDEFGHIJKLMNOPQRSTUVWYZYABCDEFGHIJKLMNOPQRSTUVWYZYABCDEFGHIJKLMNOPQRSTUVWYZYABCDEFGHIJKLMNOPQRSTUVWYZYABCDEFGHIJKLMNOPQRSTUVWYZYABCDEFGHIJKLMNOPQRSTUVWYZYABCDEFGHIJKLMNOPQRSTUVWYZYABCDEFGHIJKLMNOPQRSTUVWYZYABCDEFGHIJKLMNOPQRSTUVWYZYABCDEFGHIJKLMNOPQRSTUVWYZYABCDEFGHIJKLMNOPQRSTUVWYZYABCDEFGHIJKLMNOPQRSTUVWYZYABCDEFGHIJKLMNOPQRSTUVWYZYABCDEFGHIJKLMNOPQRSTUVWYZYABCDEFGHIJKLMNOPQRSTUVWYZYABCDEFGHIJKLMNOPQRSTUVWYZYABCDEFGHIJKLMNOPQRSTUVWYZYABCDEFGHIJKLMNOPQRSTUVWYZYABCDEFGHIJKLMNOPQRSTUVWYZYABCDEFGHIJKLMNOPQRSTUVWYZYABCDEFGHIJKLMNOPQRSTUVWYZY");
        execBuild.redirectErrorStream(true);
        mExec = null;
        try {
            mExec = execBuild.start();
        } catch (Exception e) {
            // handle exception here
            Log.e("HuntRunner2", e.toString());
            //finish();
        }

        DataHolder.setExec(mExec);

        /* ... and connect the process's I/O streams to the TermSession. */
Log.w("HuntRunner2", "session="+session);
Log.w("HuntRunner2", "exec="+mExec);
        session.setTermIn(mExec.getInputStream());
        session.setTermOut(mExec.getOutputStream());

        /* You're done! */
        return session;

        /**
         * NB: You can invoke a program without using execpty or a native code
         * method, but the results may not be what you expect, because the
         * process will be connected to a pipe, not a tty device.  tty devices
         * provide services such as flow control and input/output translation
         * which many programs expect.
         *
         * If you do connect a program directly to a TermSession without using
         * a tty, you should probably at a minimum translate '\r' (sent by the
         * Enter key) to '\n' (which most programs expect as their newline
         * input) in write(), and translate '\n' (sent by most programs to
         * indicate a newline) to '\r\n' (which the terminal emulator needs to
         * actually start a new line without overdrawing text or "staircase
         * effect") in processInput(), before sending it to the terminal
         * emulator.
         *
         * For an example of how to obtain and use a tty device in native code,
         * see assets-src/execpty.c.
         */
    }

    /**
     * Connect to the Telnet server.
     */
/*
    public void connectToTelnet(String server) {
        String[] telnetServer = server.split(":", 2);
        final String hostname = telnetServer[0];
        int port = 23;
        if (telnetServer.length == 2) {
            port = Integer.parseInt(telnetServer[1]);
        }
        final int portNum = port;

        // On Android API >= 11 (Honeycomb and later), network operations
          // must be done off the main thread, so kick off a new thread to
          // perform the connect. 
        new Thread() {
            public void run() {
                // Connect to the server
                try {
                    Socket socket = new Socket(hostname, portNum);
                    mSocket = socket;
                } catch (IOException e) {
                    Log.e("Telnet", e.toString());
                    return;
                }

                // Notify the main thread of the connection
                mHandler.sendEmptyMessage(MSG_CONNECTED);
            }
        }.start();
    }

//     * Handler which will receive the message from the Telnet connect thread
  //   * that the connection has been established.
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_CONNECTED) {
                createTelnetSession();
            }
        }
    };
    Socket mSocket;
    private static final int MSG_CONNECTED = 1;
*/

    /* Create the TermSession which will handle the Telnet protocol and
       terminal emulation. */
/*
    private void createTelnetSession() {
        Socket socket = mSocket;

        // Get the socket's input and output streams
        InputStream termIn;
        OutputStream termOut;
        try {
            termIn = socket.getInputStream();
            termOut = socket.getOutputStream();
        } catch (IOException e) {
            // Handle exception here
            return;
        }

         Create the TermSession and attach it to the view.  See the
           TelnetSession class for details. 
        TermSession session = new TelnetSession(termIn, termOut);
        mEmulatorView.attachSession(session);
        mSession = session;
    }
*/




} /* /termactivity */


