/*
 * fooree.net@hotmail.com
 */

package cn.com.jdsc;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;


public class ExitActivity extends Activity {
	//private Button mButton1;
	private static final String TAG = "exit"; 
	private static final boolean D = false;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	//Log.v(TAG, "super.");
    	
    	setContentView(R.layout.main_exit);
    	if(D) Log.v(TAG, "layout main_exit");

    	TextView yymmdd = (TextView) findViewById(R.id.textViewPath);
    	
    	Calendar c = Calendar.getInstance();
    	String time=c.get(Calendar.YEAR)+"-"+(c.get(Calendar.MONTH)+1)+"-"+c.get(Calendar.DAY_OF_MONTH);
    	yymmdd.setText(time);
    	
    	new AlertDialog.Builder(this)
    	.setTitle(R.string.app_close)
    	.setMessage(R.string.app_close_msg)
    	.setPositiveButton(R.string.str_ok,
    			new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						finish();
					}
				}
    	).setNegativeButton(R.string.str_no,
    			new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				}).show();
    	
		TextView history = (TextView)findViewById(R.id.textView2);
    	history.setMovementMethod(ScrollingMovementMethod.getInstance());
    }
    
    @Override
    public synchronized void onResume() {
        super.onResume();
        if(D) Log.v(TAG, "+ ON RESUME +");
        
    	new AlertDialog.Builder(this)
    	.setTitle(R.string.app_close)
    	.setMessage(R.string.app_close_msg)
    	.setPositiveButton(R.string.str_ok,
    			new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						finish();
					}
				}
    	).setNegativeButton(R.string.str_no,
    			new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				}).show();
        
    }
}