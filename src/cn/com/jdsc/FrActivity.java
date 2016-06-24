/*
 * fooree.net@hotmail.com
 */

package cn.com.jdsc;

import cn.com.util.ExitManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TextView;

public class FrActivity extends TabActivity {
	TabHost tabHost;
	private RadioButton main_tab_home, main_tab_bluetooth, main_tab_exit;
	//public static RadioButton main_tab_setting;
	//public static RadioButton main_tab_player;
	
	public static String EXTRA_ENTRANCE = "activity_entrance";
	Intent intent;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initTab();
        init();
        ExitManager.getInstance().addActivity(this);
    }
    
    public void init(){
    	main_tab_home=(RadioButton)findViewById(R.id.main_tab_home);
    	main_tab_bluetooth = (RadioButton) findViewById(R.id.main_tab_bluetooth);
    	
		//main_tab_setting = (RadioButton) findViewById(R.id.main_tab_setting);
		//main_tab_player = (RadioButton) findViewById(R.id.main_tab_player);
		
		main_tab_exit = (RadioButton) findViewById(R.id.main_tab_exit);
		
		main_tab_home.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				tabHost.setCurrentTabByTag("home");
			}
		});

		main_tab_bluetooth.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				tabHost.setCurrentTabByTag("bluetooth");

			}
		});
		
//		main_tab_setting.setOnClickListener(new OnClickListener() {

//			public void onClick(View view) {
//				tabHost.setCurrentTabByTag("setting");
				
//				Log.d("bluex", "setting");

//				Bundle bundle = new Bundle();
//		    	bundle.putInt(EXTRA_ENTRANCE, 2);
//		    	intent.putExtras(bundle);
//		    	startActivity(intent);
//			}
//		});
//		main_tab_player.setOnClickListener(new OnClickListener() {

//			public void onClick(View view) {
//				tabHost.setCurrentTabByTag("player");
//				Log.d("bluex", "player");
				
//				Bundle bundle3 = new Bundle();
//		    	bundle3.putInt(EXTRA_ENTRANCE, 3);
//		    	startActivity(intent);
//			}
//		});
		
		main_tab_exit.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				tabHost.setCurrentTabByTag("exit");
				//exit the app??
			}
			

		});
    }
    
    public void initTab(){
//    	intent = new Intent(this, BluetoothActivity.class);
    	
    	tabHost=getTabHost();
    	tabHost.addTab(tabHost.newTabSpec("home").setIndicator("home")
				.setContent(new Intent(this, HomeActivity.class)));
    	tabHost.addTab(tabHost.newTabSpec("bluetooth").setIndicator("bluetooth")
				.setContent(new Intent(this, BluetoothActivity.class)));

//		tabHost.addTab(tabHost.newTabSpec("setting").setIndicator("setting").setContent(intent));
//		tabHost.addTab(tabHost.newTabSpec("player").setIndicator("player").setContent(intent));
		
		tabHost.addTab(tabHost.newTabSpec("exit").setIndicator("exit")
				.setContent(new Intent(this, ExitActivity.class)));
    }
    
    public boolean dispatchKeyEvent( KeyEvent event) {
		int keyCode=event.getKeyCode();
		  Log.d("BlueFr", "dispatch Key Event"+keyCode);
	      if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (event.getRepeatCount() == 0) {
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						FrActivity.this);
				alertDialog.setTitle(FrActivity.this
						.getString(R.string.app_close));
				alertDialog.setPositiveButton(FrActivity.this
						.getString(R.string.btn_ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								ExitManager.getInstance().exit();
							}
						});
				alertDialog.setNegativeButton(FrActivity.this
						.getString(R.string.btn_cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
							}
						});
				alertDialog.show();
			}
		}
		return super.dispatchKeyEvent(event);
	}

}