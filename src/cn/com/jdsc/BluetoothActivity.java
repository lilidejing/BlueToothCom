
package cn.com.jdsc;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.R.integer;
import android.app.Activity;
import android.app.ListActivity;
import android.app.TabActivity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleCursorAdapter;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class BluetoothActivity  extends Activity{
	private static final boolean D = false;
	private static final String TAG = "bluetooth";
	private static final String TAG2 = "blueevent";
	private BluetoothAdapter bluetooth; 
	private TextView statustxt; 
	private TextView conntxt;
	private Button connb;
	private Button sendb;
	private Button viewb;
	private EditText mOutEditText;
	private int SUBACT_BLUELIST = 11;
	
	private TextView recTxt;
	private TextView sndTxt;
	private TextView recBuf;
	private int recCnt;
	private int sndCnt;
	private Button clearButt;
	private ToggleButton tb;
	
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    
    // Member object for the chat services
    private BluetoothChatService mChatService = null;
    // String buffer for recvd messages
    private byte[] mInBufRing = null;
    private static int RING_LEN = 500;
    private static int START_IDX = 0;
    private int pRingStart = START_IDX; //to read from buf
    private int pRingEnd = START_IDX;   //to add into buf
    // Name of the connected device
    private String mConnectedDeviceName = null;
    private BluetoothDevice remotedevice = null;
    
	//added for HFP and A2dp, need android V3.0 or later version
	//android.os.Build.VERSION.SDK_INT > 11			
	private BluetoothHeadset mBluetoothHeadset = null;
	private BluetoothA2dp mBluetoothA2dp = null;
	private BluetoothProfile.ServiceListener mProfileListener; 
	private BroadcastReceiver mBlueToothHeadSetEventReceiver = null;
	private Button hfpbut = null;
	private Button a2dpbut = null;
	private int conn_status_hfp = 0;
	private int conn_status_a2dp = 0;
	
    public int view_tab = 0;  
    private static int VIEW_BT = 0;
    private static int VIEW_SETT = 1;
    private static int VIEW_PLAYER = 2;
    private static int VIEW_FM = 3;
	
	private static byte SOP1 = 0x55;
	private static byte SOP2 = 0xaa-256;
	
	public static byte[] cmd_mode_next = {SOP1, SOP2, 0x00, 0x01, 0x01, 0xfe-256};
	public byte[] cmd_mode_select = {SOP1, SOP2, 0x01, 0x01, 0x02, 0x00, 0x00}; //modeId, CS
	public static byte[] cmd_mode_get = {SOP1, SOP2, 0x00, 0x01, 0x03, 0xfc-256};

	private byte[] evnt_mode_next = {SOP1, SOP2, 0x01, 0x81-256, 0x01}; //modeId, CS
	private byte[] evnt_mode_get = {SOP1, SOP2, 0x01, 0x81-256, 0x03}; //modeId, CS

	private static byte[] cmd_file_foldersum = {SOP1, SOP2, 0x00, 0x02, 0x01, 0xfd-256};
	private static byte[] cmd_file_filesum = {SOP1, SOP2, 0x00, 0x02, 0x02, 0xfc-256};
	private byte[] cmd_file_folderinfo = {SOP1, SOP2, 0x04, 0x02, 0x03, 0, 0, 0, 0, 0};  //parentfolder#, folder#, CS
	private byte[] cmd_file_fileinfo = {SOP1, SOP2, 0x04, 0x02, 0x04, 0, 0, 0, 0, 0};  //folder#, file#, CS
	
	public static byte[] cmd_music_play = {SOP1, SOP2, 0x00, 0x03, 0x01, 0xfc-256};
	public static byte[] cmd_music_pause = {SOP1, SOP2, 0x00, 0x03, 0x02, 0xfb-256};
	public static byte[] cmd_music_stop = {SOP1, SOP2, 0x00, 0x03, 0x03, 0xfa-256};
	private static byte[] cmd_music_next = {SOP1, SOP2, 0x00, 0x03, 0x04, 0xf9-256};
	private static byte[] cmd_music_prve = {SOP1, SOP2, 0x00, 0x03, 0x05, 0xf8-256};
	private static byte[] cmd_music_playpause = {SOP1, SOP2, 0x00, 0x03, 0x06, 0xf7-256};
	private static byte[] cmd_music_ff = {SOP1, SOP2, 0x00, 0x03, 0x07, 0xf6-256};
	private static byte[] cmd_music_fb = {SOP1, SOP2, 0x00, 0x03, 0x08, 0xf5-256};
	private static byte[] cmd_music_stopf = {SOP1, SOP2, 0x00, 0x03, 0x09, 0xf4-256};
	private  byte[] cmd_music_selsong = {SOP1, SOP2, 0x04, 0x03, 0x0D, 0,0,0,0,0}; //folder#, file#, CS
	private static byte[] cmd_music_getplaystatus = {SOP1, SOP2, 0x00, 0x03, 0x20, 0xdd-256};
	private static byte[] cmd_music_getsonginfo = {SOP1, SOP2, 0x00, 0x03, 0x21, 0xdc-256};
	
	private static byte[] evnt_music_play = {SOP1, SOP2, 0x00, 0x83-256, 0x01, 0x7c}; 
	private static byte[] evnt_music_pause = {SOP1, SOP2, 0x00, 0x83-256, 0x02, 0x7b}; 
	private static byte[] evnt_music_stop = {SOP1, SOP2, 0x00, 0x83-256, 0x03, 0x7a}; 
	private static byte[] evnt_music_next = {SOP1, SOP2, 0x00, 0x83-256, 0x04, 0x79}; 
	private static byte[] evnt_music_prev = {SOP1, SOP2, 0x00, 0x83-256, 0x05, 0x78}; 	
	private static byte[] evnt_music_playpause = {SOP1, SOP2, 0x00, 0x83-256, 0x06, 0x77}; 
	private static byte[] evnt_music_ff = {SOP1, SOP2, 0x00, 0x83-256, 0x07, 0x76}; 
	private static byte[] evnt_music_fb = {SOP1, SOP2, 0x00, 0x83-256, 0x08, 0x75}; 	
	private static byte[] evnt_music_stopf = {SOP1, SOP2, 0x00, 0x83-256, 0x09, 0x74};
	
	private static byte[] cmd_audio_voladd = {SOP1, SOP2, 0x00, 0x04, 0x01, 0xfb-256};
	private static byte[] cmd_audio_volsub = {SOP1, SOP2, 0x00, 0x04, 0x02, 0xfa-256};
	private byte[] cmd_audio_volset = {SOP1, SOP2, 0x01, 0x04, 0x03, 0x00, 0x00};//vol,CS
	private static byte[] cmd_audio_volget = {SOP1, SOP2, 0x00, 0x04, 0x04, 0xf8-256};
	private byte[] cmd_audio_setEQ = {SOP1, SOP2, 0x01, 0x04, 0x05, 0x00, 0x00}; //EQ,CS
	private static byte[] cmd_audio_getEQ = {SOP1, SOP2, 0x00, 0x04, 0x06, 0xf6-256};
	private static byte[] cmd_audio_mute = {SOP1, SOP2, 0x00, 0x04, 0x07, 0xf5-256};
	private static byte[] cmd_audio_unmute = {SOP1, SOP2, 0x00, 0x04, 0x08, 0xf4-256};
	
	private static byte[] event_audio_voladd = {SOP1, SOP2, 0x00, 0x84-256, 0x01, 0x7b};
	private static byte[] event_audio_volsub = {SOP1, SOP2, 0x00, 0x84-256, 0x02, 0x7a};
	private static byte[] event_audio_volset = {SOP1, SOP2, 0x00, 0x84-256, 0x03, 0x79};
	private byte[] event_audio_volget = {SOP1, SOP2, 0x01, 0x84-256, 0x04, 0x00,0x00};//vol,CS
	private static byte[] event_audio_setEQ = {SOP1, SOP2, 0x00, 0x84-256, 0x05, 0x77};
	private byte[] event_audio_getEQ = {SOP1, SOP2, 0x01, 0x84-256, 0x06, 0x00,0x00};//EQ,CS
	private static byte[] event_audio_mute = {SOP1, SOP2, 0x00, 0x84-256, 0x07, 0x75};
	private static byte[] event_audio_unmute = {SOP1, SOP2, 0x00, 0x84-256, 0x08, 0x74};

	private static byte[] cmd_adc_vin = {SOP1, SOP2, 0x00, 0x07, 0x04, 0xf5-256};
	private static byte[] event_adc_vin = {SOP1, SOP2, 0x02, 0x87-256, 0x04, 0,0,0}; //3400~4600,CS
	
	private  byte[] cmd_radio_para = {SOP1, SOP2, 0x02, 0x0A, 0x01, 0,0, 0}; //
	private  byte[] cmd_radio_freq = {SOP1, SOP2, 0x02, 0x0A, 0x02, 0,0, 0}; //
	private static byte[] cmd_radio_nextstep = {SOP1, SOP2, 0x00, 0x0A, 0x03, 0xf3-256};
	private static byte[] cmd_radio_prevstep = {SOP1, SOP2, 0x00, 0x0A, 0x04, 0xf2-256}; 
	private static byte[] cmd_radio_nextstation = {SOP1, SOP2, 0x00, 0x0A, 0x05, 0xf1-256};
	private static byte[] cmd_radio_prevstation = {SOP1, SOP2, 0x00, 0x0A, 0x06, 0xf0-256}; 
	private static byte[] cmd_radio_autoscan = {SOP1, SOP2, 0x00, 0x0A, 0x07, 0xef-256};
	private static byte[] cmd_radio_nextseek = {SOP1, SOP2, 0x00, 0x0A, 0x08, 0xee-256}; 
	private static byte[] cmd_radio_prevseek = {SOP1, SOP2, 0x00, 0x0A, 0x09, 0xed-256};
	private static byte[] cmd_radio_getstation = {SOP1, SOP2, 0x00, 0x0A, 0x0D, 0xe9-256};

	
//	private static byte[] cmd_power_get = {SOP1, SOP2, 0x00, 0x0B, 0x01, 0xf4-256};
//	private static byte[] event_power_get = {SOP1, SOP2, 0x01, 0x8B-256, 0x01, 0, 0}; //power, CS

	private static byte[] cmd_special_vbon = {SOP1, SOP2, 0x00, 0x70, 0x01, 0x8f-256};
	private static byte[] cmd_special_vboff = {SOP1, SOP2, 0x00, 0x70, 0x02, 0x8e-256};

	private static byte[] event_special_vbon = {SOP1, SOP2, 0x00, 0xf0-256, 0x01, 0x0f};
	private static byte[] event_special_vboff = {SOP1, SOP2, 0x00, 0xf0-256, 0x02, 0x0e};

	
	private static int MODE_CNT = 13;
	private static int MODE_SD = 1;  //SD mode: index = 1
	private static int MODE_USB = 2;
	private static int MODE_FM = 5;
	private int mode_cur = MODE_SD; 
	private static String[] mode_show = {"IDLE", "SD", "USB", "BlueTooth", "Recorder", "Radio", "LineIn", 
			"USB Audio", "USB Reader", "USB Audio&Reader", "RTC", "Browser", "Setting"}; //0~12
	private TextView tv_mode;
	private static int EQ_CNT = 11;
	private int EQ_cur = 0;
	private int vbOnStatus = 0; //0-vb off, 1-vb on
	private static String[] EQ_show = {"NORMAL", "ROCK", "POP", "CLASSIC", "JAZZ", "BLUE", "HALL", "BASS", "SOFT", "COUNTRY", "OPERA"}; 

	private static int VOL_MAX = 32;
	private int vol_cur = 0;
	private int vol_mute = 0; //0-Normal, 1-mute
    
	private static int BATT_MAX = 4600;
	private static int BATT_MIN = 3200;
	private static int BATT_STEP = 200;  //7 level
	private int batt_cur = BATT_MIN;
	
	private String rootPath = "/";
	private int folder_sum = 0;
	private int file_sum = 0;
	private int folder_root = 1; // root dir \
	private int folder_cur = folder_root;
	private int folder_seq = -1;  //folder seq start from 1
	private int file_seq = 0;
	public List<String> item_player = null;
	public List<String> path_player = null;
	public List<Integer> seqno_player = null;
	private static int LEVEL_MAX = 16;
	private int ff_level = 0;
	private int[] ff_number;
	public String mypath = "";
    public TextView mPath;
    public ListView PlayerList;
    public MyAdapter myPlayerAda;
    public Button player_stop;
    public Button player_pp;
    public FrActivity frAct;
	
    private TextView tv_freq;
    private static int FREQ_MAX = 1080; //108MHz
	private static int FREQ_MIN = 875;  //87.5MHz
	private int freq_cur = FREQ_MIN;
	
    private TabHost tabs;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab);
		
		tabs = (TabHost)findViewById(R.id.blue_tabhost);
		tabs.setup();

		TabWidget tabWidget = tabs.getTabWidget(); 

		LayoutInflater.from(this).inflate(R.layout.main_bluetooth, tabs.getTabContentView(), true);
		LayoutInflater.from(this).inflate(R.layout.main_setting, tabs.getTabContentView(), true);
		LayoutInflater.from(this).inflate(R.layout.main_player, tabs.getTabContentView(), true);
		LayoutInflater.from(this).inflate(R.layout.main_fm, tabs.getTabContentView(), true);
		
		tabs.addTab(tabs.newTabSpec("BT").setIndicator("连接").setContent(R.id.layoutBluetooth));  
		tabs.addTab(tabs.newTabSpec("Setting").setIndicator("设置").setContent(R.id.layoutSetting));
		tabs.addTab(tabs.newTabSpec("Player").setIndicator("浏览").setContent(R.id.layoutPlayer));  
		tabs.addTab(tabs.newTabSpec("FM").setIndicator("收音机").setContent(R.id.layoutFM));
		
		int height = 50;                
        for (int i=0; i<tabWidget.getChildCount(); i++) {  
  
            tabWidget.getChildAt(i).getLayoutParams().height = height;   
  
            /**设置tab中标题文字的颜色，不然默认为黑色 */   
             final TextView tv = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);   
             tv.setTextColor(this.getResources().getColorStateList(android.R.color.white));
             //tv.setBackgroundColor(Color.BLUE);
        } 
		
		tabs.getTabWidget().getChildAt(VIEW_PLAYER).setVisibility(View.GONE);
		tabs.getTabWidget().getChildAt(VIEW_SETT).setVisibility(View.GONE);
		tabs.getTabWidget().getChildAt(VIEW_FM).setVisibility(View.GONE);
		tabs.setCurrentTab (VIEW_BT);
		
		//to store/assemble received packet
		mInBufRing = new byte[RING_LEN];
				
		mPath = (TextView)findViewById(R.id.textViewPath); 
	    mPath.setText(rootPath);
	    ff_number = new int[LEVEL_MAX+1];
	    ff_level = 0;
	    ff_number[ff_level] = 1;

	    item_player = new ArrayList<String>();
	    path_player = new ArrayList<String>();
	    seqno_player = new ArrayList<Integer>();
	    myPlayerAda = new MyAdapter(this, item_player, path_player);
	    
		view_tab = VIEW_BT;
		JumpToLayoutBlue();
		
    	tabs.setOnTabChangedListener(new OnTabChangeListener(){
    		@Override
            public void onTabChanged(String tabId) {
                // TODO Auto-generated method stub
    			
    			pRingStart = START_IDX;
    			pRingEnd = START_IDX; 
    			
    			if (tabId.equals("BT")) {
    				view_tab = VIEW_BT;
    				JumpToLayoutBlue();
    	        }else if(tabId.equals("Setting")){
    	        	view_tab = VIEW_SETT;
    	        	JumpToLayoutSetting();
    	        }else if(tabId.equals("Player")){
    	        	view_tab = VIEW_PLAYER;
    	        	JumpToLayoutPlayer();
    	        }else if(tabId.equals("FM")){
    	        	view_tab = VIEW_FM;
    	        	JumpToLayoutFM();
    	        }else {
    	        	
    	        }
    			
            }
    	});
    	    	
    }
    
  
    protected void JumpToLayoutFM()
	{
    	if(D) Log.d(TAG, "view changed to Radio");
    	
    	tv_freq = (TextView)findViewById(R.id.textViewFreq);
    	tv_freq.setText((float)freq_cur/10 + "MHz");
    	
    	ImageButton im_prev = (ImageButton)findViewById(R.id.FreqButton2);
    	im_prev.setOnClickListener(new ImageButton.OnClickListener(){
    		@Override
    		public void onClick(View v){
    			if(D) Log.d(TAG, "prev short");
    			sendHex(cmd_radio_prevstep);
    		}
    	});
    	im_prev.setOnLongClickListener(new ImageButton.OnLongClickListener(){
    		@Override
    		public boolean onLongClick(View v){
    			if(D) Log.d(TAG, "prev long");
    		    sendHex(cmd_radio_prevseek);
    		    return true;
    		}
    	});
    	
    	ImageButton im_next = (ImageButton)findViewById(R.id.FreqButton1);
    	im_next.setOnClickListener(new ImageButton.OnClickListener(){
    		@Override
    		public void onClick(View v){
    			if(D) Log.d(TAG, "next short");
    			sendHex(cmd_radio_nextstep);
    		}
    	});
    	im_next.setOnLongClickListener(new ImageButton.OnLongClickListener(){
    		@Override
    		public boolean onLongClick(View v){
    			if(D) Log.d(TAG, "next long");
    			sendHex(cmd_radio_nextseek);
    			return true;
    		}
    	});
    	
	}

	protected void JumpToLayoutBlue()
	{
//	  	setContentView(R.layout.main_bluetooth);
	  	if(D) Log.d(TAG, "view changed to Bluetooth"); 
	 
//		view_tab = VIEW_BT;
		bluetooth = BluetoothAdapter.getDefaultAdapter();
		
		
	  	statustxt = (TextView)findViewById(R.id.textView1);
		conntxt = (TextView)findViewById(R.id.textView2);
		tb=(ToggleButton)findViewById(R.id.toggleButton1);
		connb = (Button)findViewById(R.id.connButton);
		viewb = (Button)findViewById(R.id.viewButton);
		sendb = (Button)findViewById(R.id.sendButton);
		clearButt = (Button)findViewById(R.id.clearButton);
		mOutEditText = (EditText)findViewById(R.id.editText1); 
		
		if (bluetooth == null) {
		    // Device does not support Bluetooth
			statustxt.setTextColor(Color.RED);
			statustxt.setText("蓝牙开关：不支持");
			tb.setEnabled(false);
			connb.setEnabled(false);
			return;
		}
		
		if(bluetooth.isEnabled()){
			tb.setChecked(true);
			statustxt.setText("蓝牙开关：已开启");
			connb.setEnabled(true);
			if (mChatService == null) setupServ();
		} else {
			tb.setChecked(false);
			statustxt.setText("蓝牙开关：已关闭");
			connb.setEnabled(false);			
		}
		
		if((mChatService!=null)&&(mChatService.getState() == BluetoothChatService.STATE_CONNECTED)){
			conntxt.setText(R.string.title_connected_to);
            if(mConnectedDeviceName != null)  conntxt.append(mConnectedDeviceName);

            connb.setText(R.string.disconnectButton);
            
            tabs.getTabWidget().getChildAt(VIEW_SETT).setVisibility(View.VISIBLE);
		}
	  	
		tb.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1){
				if(arg1){
					//on
					if(D) Log.v(TAG, "turn ON bluetooth");
					
				    Intent enableBtIntent = new Intent(bluetooth.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
				    
				}else {
					//off
					if(D) Log.v(TAG, "Turn OFF bluetooth");
					bluetooth.disable();
					statustxt.setText("蓝牙开关：已关闭");
					connb.setEnabled(false);
				}
			}
		});
		
		connb.setOnClickListener(new Button.OnClickListener(){
			@Override
        	public void onClick(View v)
        	{
				if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED){
					//to disconnect
					mChatService.stop();
				}else{
					//to connect
					Intent intent = new Intent(BluetoothActivity.this, BlueListActivity.class);
					startActivityForResult(intent, SUBACT_BLUELIST);
				}				
        	}
		});
		
		
		sendb.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v)
			{
				if(true){  //hex.isChecked()
					byte[] SendBytes = null;
					String SendData0 = mOutEditText.getText().toString().toLowerCase();
					//剔除所有空格
					String SendData = SendData0.replace(" ", "");
					if((SendData.length() & 0x01) != 0){
						Log.e(TAG, "input length error.");
					    return;  //length error
					}
					char[] ascArray = SendData.toCharArray();
					//每两个字符放进认为一个字节
					for (int i = 0; i < SendData.length(); i++)
					{
						if((ascArray[i]<'0') || (ascArray[i]>'f') || ((ascArray[i]>'9')&&(ascArray[i]<'a'))){
							Log.e(TAG, "input Hex error.");
						    return;  //length error							
						}
					}
					
					SendBytes = new byte[SendData.length()/2];
					for (int j = 0; j < SendData.length(); j=j+2)
					{
						if((ascArray[j]<'a') &&(ascArray[j+1]<'a'))  //0~9, 0~9
							SendBytes[j/2] = (byte) (((ascArray[j]-'0')<<4) + (ascArray[j+1]-'0'));
						else if((ascArray[j]<'a') &&(ascArray[j+1]>='a')) //0~9, a~f
							SendBytes[j/2] = (byte) (((ascArray[j]-'0')<<4) + (ascArray[j+1]-'a'+10));
						else if((ascArray[j]>='a') &&(ascArray[j+1]>='a')) //a~f, a~f
							SendBytes[j/2] = (byte) (((ascArray[j]-'a'+10)<<4) + (ascArray[j+1]-'a'+10));
						else if((ascArray[j]>='a') &&(ascArray[j+1]<'a')) //a~f, 0~9
							SendBytes[j/2] = (byte) (((ascArray[j]-'a'+10)<<4) + (ascArray[j+1]-'0'));
						else
							Log.e(TAG, "impossible to get here");					
						
						if(D) Log.d(TAG, Integer.toString(j/2) + ":" + Integer.toString(0xff & SendBytes[j/2]));
					}
					
					sendHex(SendBytes);	
					
				}

			}
		});
		
		clearButt.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v)
			{				
				recCnt = 0;
				sndCnt = 0;
				recTxt.setText("RX:"+Integer.toString(recCnt));
				sndTxt.setText("TX:"+Integer.toString(sndCnt));
				recBuf.setText("");
			}
		});
		
		viewb.setOnClickListener(new Button.OnClickListener(){
			@Override
        	public void onClick(View v)
        	{				
				JumpToLayoutSetting();
        	}
		});
		
		recTxt = (TextView)findViewById(R.id.textView4);
		sndTxt = (TextView)findViewById(R.id.textView5);
		recBuf = (TextView)findViewById(R.id.textView6);
		
		

		// Get the default adapter
		//BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
		hfpbut = (Button)findViewById(R.id.hfpbutton1);
		a2dpbut = (Button)findViewById(R.id.a2dpbutton2);
		hfpbut.setEnabled(false);
		a2dpbut.setEnabled(false);
		if(android.os.Build.VERSION.SDK_INT < 11){
			Toast.makeText(getApplicationContext(), "手机Android版本至少要v3.0或更高才能支持HFP/A2DP", Toast.LENGTH_SHORT).show();
			return;
		}

		mProfileListener = new BluetoothProfile.ServiceListener() {
		    public void onServiceConnected(int profile, BluetoothProfile proxy) {
		        if (profile == BluetoothProfile.HEADSET) {
		            mBluetoothHeadset = (BluetoothHeadset) proxy;
		            hfpbut.setEnabled(true);
		            if(null != mBluetoothHeadset.getConnectedDevices()){
		            	conn_status_hfp = BluetoothProfile.STATE_CONNECTED;
		            }
		        } else if(profile == BluetoothProfile.A2DP){
		        	mBluetoothA2dp = (BluetoothA2dp) proxy;
		        	a2dpbut.setEnabled(true);
		        	if(null != mBluetoothA2dp.getConnectedDevices()){
		        		conn_status_a2dp = BluetoothProfile.STATE_CONNECTED;	
		        	}
		        }
		    }
		    
		    public void onServiceDisconnected(int profile) {
		    	if (profile == BluetoothProfile.HEADSET) {
		            mBluetoothHeadset = null;
		            hfpbut.setEnabled(false);
		        } else if(profile == BluetoothProfile.A2DP){
		        	mBluetoothA2dp = null;
		        	a2dpbut.setEnabled(false);
		        }
		    }
		};
		
		// Establish connection to the proxy.
		bluetooth.getProfileProxy(getApplicationContext(), mProfileListener, BluetoothProfile.HEADSET);
		bluetooth.getProfileProxy(getApplicationContext(), mProfileListener, BluetoothProfile.A2DP);

		
		hfpbut.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v){	
				if(remotedevice == null){
					Toast.makeText(getApplicationContext(), "请先选择音频设备", Toast.LENGTH_SHORT).show();
					return;
				}
				Class<? extends BluetoothHeadset> clazz = mBluetoothHeadset.getClass();
				Method m1;
				if(BluetoothProfile.STATE_CONNECTED == mBluetoothHeadset.getConnectionState(remotedevice)){
					try{
						m1 = clazz.getMethod("disconnect", BluetoothDevice.class);
						m1.invoke(mBluetoothHeadset, remotedevice);						
					}catch(Exception e){
						e.printStackTrace();
					}
				}else{
					try{
						m1 = clazz.getMethod("connect", BluetoothDevice.class);
						m1.invoke(mBluetoothHeadset, remotedevice);						
					}catch(Exception e){
						e.printStackTrace();
					}
				}								
			}
		});
		
		a2dpbut.setOnClickListener(new Button.OnClickListener(){
			@Override
        	public void onClick(View v){
				//mBluetoothA2dp.connect(remotedevice);
				if(remotedevice == null){
					Toast.makeText(getApplicationContext(), "请先选择音频设备", Toast.LENGTH_SHORT).show();
					return;
				}
				Class<? extends BluetoothA2dp> clazz = mBluetoothA2dp.getClass();
				Method m2;
				if(BluetoothProfile.STATE_CONNECTED == mBluetoothA2dp.getConnectionState(remotedevice)){
					try{
						m2 = clazz.getMethod("disconnect", BluetoothDevice.class);
						m2.invoke(mBluetoothA2dp, remotedevice);						
					}catch(Exception e){
						e.printStackTrace();
					}
				}else{
					try{
						m2 = clazz.getMethod("connect", BluetoothDevice.class);
						m2.invoke(mBluetoothA2dp, remotedevice);						
					}catch(Exception e){
						e.printStackTrace();
					}
				}				
			}
		});
		
		if(mBlueToothHeadSetEventReceiver == null){
			mBlueToothHeadSetEventReceiver = new BroadcastReceiver() {
		    	@Override
		    	public void onReceive(Context context, Intent intent){
		    		String EXTRA_STATE = "android.bluetooth.headset.extra.STATE";
		    		try {
		    	        String action = intent.getAction();
		    	        if(action == null) return;
		    	        if(action.equals("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED")){
		    	        	if(BluetoothProfile.STATE_CONNECTED == mBluetoothHeadset.getConnectionState(remotedevice)){
		    	        		Log.d(TAG, "HFP connected.");
		    	        		if(conn_status_hfp != BluetoothProfile.STATE_CONNECTED){
		    	        			Toast.makeText(getApplicationContext(), "HFP已连接", Toast.LENGTH_SHORT).show();
		    	        			conn_status_hfp = BluetoothProfile.STATE_CONNECTED;
		    	        		}		    	        		
		    	        	}else{
		    	        		if(conn_status_hfp != BluetoothProfile.STATE_DISCONNECTED){
		    	        			Toast.makeText(getApplicationContext(), "HFP未连接", Toast.LENGTH_SHORT).show();
		    	        			conn_status_hfp = BluetoothProfile.STATE_DISCONNECTED;
		    	        		}
		    	        	}
		    	        	
		    	        }else if(action.equals("android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED")){
		    	        	if(BluetoothProfile.STATE_CONNECTED == mBluetoothA2dp.getConnectionState(remotedevice)){
		    	        		if(conn_status_a2dp != BluetoothProfile.STATE_CONNECTED){
		    	        			Toast.makeText(getApplicationContext(), "A2DP已连接", Toast.LENGTH_SHORT).show();
		    	        			conn_status_a2dp = BluetoothProfile.STATE_CONNECTED;
		    	        		}
		    	        	}else{
		    	        		if(conn_status_a2dp != BluetoothProfile.STATE_DISCONNECTED){
		    	        			Toast.makeText(getApplicationContext(), "A2DP未连接", Toast.LENGTH_SHORT).show();
		    	        			conn_status_a2dp = BluetoothProfile.STATE_DISCONNECTED;
		    	        		}
		    	        	}
		    	        }
		    		}catch(Exception e){
		    			e.printStackTrace();
		    		}
		    	}
		    };
		    
			// Set broadcast filter
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(android.bluetooth.BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
			intentFilter.addAction(android.bluetooth.BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
			// Register broadcast
			registerReceiver(mBlueToothHeadSetEventReceiver, intentFilter);
		}
		
				
		// Close proxy connection after use.
		//bluetooth.closeProfileProxy(BluetoothProfile.HEADSET, mBluetoothHeadset);
		//bluetooth.closeProfileProxy(BluetoothProfile.A2DP, mBluetoothA2dp);
	}
  
    protected void JumpToLayoutSetting()
    {
//		setContentView(R.layout.main_setting);
		if(D) Log.d(TAG, "view changed to setting");

//		view_tab = VIEW_SETT; 


    	tv_mode = (TextView)findViewById(R.id.textViewMode);
    	tv_mode.setText(mode_show[mode_cur]);
    	
		Button modeButt = (Button) findViewById(R.id.settle_accounts);
    	modeButt.setOnClickListener(new Button.OnClickListener(){
    		@Override
    		public void onClick(View v)
    		{
    			//clear RingBuffer in case pakcet disorder
    			pRingStart = START_IDX;
    			pRingEnd = START_IDX; 
    			
    			JumpToLayoutPlayer();
    		}
    	});
    	
 	
    	SeekBar volset = (SeekBar)findViewById(R.id.seekBar1);
    	volset.setProgress(vol_cur);
    	volset.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
    		@Override
    		public void onStopTrackingTouch(SeekBar arg0) {
    			// TODO Auto-generated method stub
    			//Log.d(TAG, "StopTracking");

    		}

    		@Override
    		public void onStartTrackingTouch(SeekBar arg0) {
    			// TODO Auto-generated method stub
    			//Log.d(TAG, "StartTracking");

    		}

    		@Override
    		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    			if(D) Log.d(TAG, "vol change to"+progress);
    			vol_cur = progress;
    			cmd_audio_volset[5] = (byte)vol_cur;
    			cmd_audio_volset[6] = CheckSum(cmd_audio_volset);
    			sendHex(cmd_audio_volset);
    		}
    	});
    	

    	
    	ImageButton im3 = (ImageButton)findViewById(R.id.imageButton3);
    	im3.setOnClickListener(new ImageButton.OnClickListener(){
    		@Override
    		public void onClick(View v)
    		{
    			if(vol_mute == 0){
    				//to mute
    				sendHex(cmd_audio_mute);
    				vol_mute = 1;
    			}else{
    				//unmute
    				sendHex(cmd_audio_unmute);
    				vol_mute = 0;
//    				im3.setImageResource(R.drawable.normal);
    			}
    			
    		}
    	});
    

    	Button mbutt1 = (Button)findViewById(R.id.modebutton1);
    	mbutt1.setOnClickListener(new Button.OnClickListener(){
    		@Override
    		public void onClick(View v){
    			if(mode_cur > 0){
    				//mode_cur = mode_cur - 1;
    				cmd_mode_select[5] = (byte)(mode_cur-1);
    				cmd_mode_select[6] = CheckSum(cmd_mode_select);
    				sendHex(cmd_mode_select);
    			}
    			if(D) Log.d(TAG, "Current mode:" + mode_show[mode_cur]);
    		}
    	});
    	
    	Button mbutt2 = (Button)findViewById(R.id.modebutton2);
    	mbutt2.setOnClickListener(new Button.OnClickListener(){
    		@Override
    		public void onClick(View v){
    			if(mode_cur < MODE_CNT){    				
    				sendHex(cmd_mode_next);
    				//mode_cur = mode_cur + 1;
    			}else{
    				mode_cur = 0;
    			}
    			if(D) Log.d(TAG, "curretn mode:" + mode_show[mode_cur]);
    		}
    	});

    	Spinner spinner_EQ = (Spinner)findViewById(R.id.spinner1);
    	ArrayAdapter<String> adapter_EQ = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, EQ_show);
    	adapter_EQ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	spinner_EQ.setAdapter(adapter_EQ);
    	
    	adapter_EQ.notifyDataSetChanged();
        spinner_EQ.setSelection(EQ_cur);
        spinner_EQ.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
        	@Override
        	public void onItemSelected(AdapterView<?> adapterView,
                          View view, int position, long id) {
        		EQ_cur = position;
        		cmd_audio_setEQ[5] = (byte) (EQ_cur & 0xff);
        		//cmd_audio_setEQ[6] = (byte) (0-((cmd_audio_setEQ[3]+cmd_audio_setEQ[4]+cmd_audio_setEQ[5])) & 0xff);
        		cmd_audio_setEQ[6] = CheckSum(cmd_audio_setEQ);
        		sendHex(cmd_audio_setEQ); 
        	}
        	
        	@Override
        	public void onNothingSelected(AdapterView<?> arg0){
        		//Auto-generated method stub
        	}
        	
        });

        ImageButton vbButt = (ImageButton)findViewById(R.id.vbImageButton);
        if(vbOnStatus == 0){
        	vbButt.setImageResource(R.drawable.vboff);
        }else{
        	vbButt.setImageResource(R.drawable.vbon);
        }
        vbButt.setOnClickListener(new ImageButton.OnClickListener(){
        	@Override
        	public void onClick(View v){
        		if(vbOnStatus == 0){
    				sendHex(cmd_special_vbon);
    				vbOnStatus = 1;
//    				vbButt.setImageResource(R.drawable.vbon);
    			}else{
    				sendHex(cmd_special_vboff);
    				vbOnStatus = 0;
//    				vbButt.setImageResource(R.drawable.vboff);
    			}
        	}
        });
        
        ImageButton playButt = (ImageButton)findViewById(R.id.playButton);
        playButt.setOnClickListener(new ImageButton.OnClickListener(){
    		@Override
    		public void onClick(View v)
    		{
    		    //send to play music
    			sendHex(cmd_music_play);
    		}
        });

        ImageButton pauseButt = (ImageButton)findViewById(R.id.pauseButton);
        pauseButt.setOnClickListener(new ImageButton.OnClickListener(){
    		@Override
    		public void onClick(View v)
    		{
    		    //send to pause music
    			sendHex(cmd_music_pause);
    		}
        });
        
        ImageButton stopButt = (ImageButton)findViewById(R.id.stopButton);
        stopButt.setOnClickListener(new ImageButton.OnClickListener(){
    		@Override
    		public void onClick(View v)
    		{
    		    //send to stop music
    			sendHex(cmd_music_stop);
    		}
        });
        
        Button blueButt = (Button)findViewById(R.id.bluebutton);
        blueButt.setOnClickListener(new Button.OnClickListener(){
        	@Override
        	public void onClick(View v){
        		//clear RingBuffer in case pakcet disorder
    			pRingStart = START_IDX;
    			pRingEnd = START_IDX; 
    			
    			JumpToLayoutBlue();
        	}
        });
        
        
        Button refButt = (Button)findViewById(R.id.refbutton1);
        refButt.setOnClickListener(new Button.OnClickListener(){
        	@Override
        	public void onClick(View v){
        		//clear RingBuffer in case pakcet disorder
    			pRingStart = START_IDX;
    			pRingEnd = START_IDX; 
    			
        		sendHex(cmd_mode_get);
        	}
        });
             
        
		ImageView battView = (ImageView) findViewById(R.id.imageView1);
		battView.setOnClickListener(new ImageView.OnClickListener(){
			@Override
			public void onClick(View v){
				//get vin voltage(3400~4600)
				sendHex(cmd_adc_vin);
			}
		});
		
		show_batt();	
		
        //to get current mode
		sendHex(cmd_mode_get);
		sendHex(cmd_adc_vin);
		//sendHex(cmd_audio_getEQ);

    }
    
    protected void show_batt(){
    	ImageView battView = (ImageView) findViewById(R.id.imageView1);
    	
//		Bitmap bmap = BitmapFactory.decodeResource(getResources(),R.drawable.batt);
//		final int bwidth = bmap.getWidth();
//		final int bheight = bmap.getHeight();
		if(D) Log.d(TAG, "batt:"+batt_cur);
//		if(batt_cur > BATT_MAX) batt_cur = BATT_MAX;
//		if(batt_cur < BATT_MIN) batt_cur = BATT_MIN;
		if(batt_cur > (BATT_MIN+6*BATT_STEP)){
			battView.setImageResource(R.drawable.batt7);
		}else if(batt_cur > (BATT_MIN+5*BATT_STEP)){
			battView.setImageResource(R.drawable.batt6);
		}else if(batt_cur > (BATT_MIN+4*BATT_STEP)){
			battView.setImageResource(R.drawable.batt5);
		}else if(batt_cur > (BATT_MIN+3*BATT_STEP)){
			battView.setImageResource(R.drawable.batt4);
		}else if(batt_cur > (BATT_MIN+2*BATT_STEP)){
			battView.setImageResource(R.drawable.batt3);
		}else if(batt_cur > (BATT_MIN+BATT_STEP)){
			battView.setImageResource(R.drawable.batt2);
		}else if(batt_cur > (BATT_MIN)){
			battView.setImageResource(R.drawable.batt1);
		}else{
			battView.setImageResource(R.drawable.batt);
		}

    }
    
    
    protected void JumpToLayoutPlayer()
    {   	
//    	setContentView(R.layout.main_player);
    	if(D) Log.d(TAG, "view changed to player");
    
//    	view_tab = VIEW_PLAYER;   	
    	
    	Button modeButt = (Button) findViewById(R.id.clearButton);
    	modeButt.setOnClickListener(new Button.OnClickListener(){
    		@Override
    		public void onClick(View v)
    		{
    			//clear RingBuffer in case pakcet disorder
    			pRingStart = START_IDX;
    			pRingEnd = START_IDX; 
    			
    			JumpToLayoutBlue();
    		}
    	});
    	
    	player_pp = (Button)findViewById(R.id.play_pause_btn);
    	player_pp.setOnClickListener(new Button.OnClickListener(){
    		@Override
    		public void onClick(View v){
    			//send to play/pause music
    			sendHex(cmd_music_play);  //sendHex(cmd_music_pause);
    		}
    		
    	});
    	
    	player_stop = (Button)findViewById(R.id.stop_btn);
    	player_stop.setOnClickListener(new Button.OnClickListener(){
    		@Override
    		public void onClick(View v){
    			//send to stop music
    			sendHex(cmd_music_stop);
    		}
    		
    	});
    	
    	//mypath = "";
//		mPath = (TextView)findViewById(R.id.textViewPath); 
//	    mPath.setText(rootPath);
//	    ff_number = new int[LEVEL_MAX+1];
//	    ff_level = 0;
//	    ff_number[ff_level] = 1;
	    
    	PlayerList = (ListView)findViewById(R.id.listView1);
//	    item_player = new ArrayList<String>();
//	    path_player = new ArrayList<String>();
//	    seqno_player = new ArrayList<Integer>();
//	    myPlayerAda = new MyAdapter(this, item_player, path_player);
//	    myPlayerAda.notifyDataSetChanged();
    	PlayerList.setBackgroundResource(R.drawable.android_layout_bg);
	    PlayerList.setAdapter(myPlayerAda);
	    PlayerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    	@Override
	    	public void onItemClick(AdapterView l, View v, int position, long id) {
	    	
	    		String tempStr;
	    		tempStr = item_player.get(position).toString();
	    		if(D) Log.d(TAG, "type:"+tempStr+",folder:"+folder_cur+",seqno"+seqno_player.get(position));//1111111
	    	    
	    		if(tempStr.equals("b5")){  //file
	    		}else{
	    			//clear RingBuffer to ignore previous folder operation 
	    			pRingStart = START_IDX;
	    			pRingEnd = START_IDX; 
	    		}
	    		
	    		mPath = (TextView)findViewById(R.id.textViewPath); //2222222
	    		if(tempStr.equals("b5")){  //file
	    			mPath.setText(mypath+"/"+path_player.get(position).toString());
	    			//to play the music
	    			cmd_music_selsong[6] = (byte)(folder_cur&0xff);
	    			cmd_music_selsong[5] = (byte)(folder_cur>>8);
	    			cmd_music_selsong[8] = (byte)(seqno_player.get(position) & 0xff);
	    			cmd_music_selsong[7] = (byte)(seqno_player.get(position)>>8);
	    			cmd_music_selsong[9] = CheckSum(cmd_music_selsong);
	    			sendHex(cmd_music_selsong);
	    		}else if(tempStr.equals("b1")){  //return to root
	    			mypath = "";
	    			mPath.setText(rootPath);
    				ff_level = 0;
    				ff_number[ff_level] = 1; 
	    			item_player.clear();
	    			path_player.clear();
	    			seqno_player.clear();
	    			myPlayerAda.notifyDataSetChanged();
	    			
	    			folder_cur = folder_root;
	    				    			
	        		folder_seq = -1;
	        		file_seq = 0;
	    			//to get root dir info
	        		cmd_file_folderinfo[5] = 0;
	        		cmd_file_folderinfo[6] = 0;
	        		cmd_file_folderinfo[8] = (byte)folder_root;
	        		cmd_file_folderinfo[7] = 0;
	        		cmd_file_folderinfo[9] = CheckSum(cmd_file_folderinfo);

	    			sendHex(cmd_file_folderinfo);
	    		}else if(tempStr.equals("b2")){ //upper level
	    			if(ff_level > 0){
	    				ff_level = ff_level - 1;
	    				folder_cur = ff_number[ff_level];
	    				
	    				int pos = mypath.lastIndexOf("/");
	    				mypath = mypath.substring(0, pos);   				
		    			mPath.setText(mypath);
		    			
		    			item_player.clear();
		    			path_player.clear();
		    			seqno_player.clear();
		    			myPlayerAda.notifyDataSetChanged();
		    			
		    			//get folder info
		    			folder_seq = -1;
		    			file_seq = 0;
		    			cmd_file_folderinfo[5] = 0;//(byte)(folder_cur&0xff);
			    		cmd_file_folderinfo[6] = 0;//(byte)(folder_cur>>8);
			    		cmd_file_folderinfo[8] = (byte)(folder_cur&0xff);
			    		cmd_file_folderinfo[7] = (byte)(folder_cur>>8);
			    		cmd_file_folderinfo[9] = CheckSum(cmd_file_folderinfo);

						sendHex(cmd_file_folderinfo);
	    			}

	    			if(ff_level == 0){
	    				mPath.setText(rootPath);
	    			}
					
	    		}else if(tempStr.equals("b3")){ //folder
	    			folder_cur = seqno_player.get(position);
	    			if(D) Log.d(TAG2, "enter folder#"+seqno_player.get(position));
	    			
	    			mypath = mypath + "/" + path_player.get(position);
    				mPath.setText(mypath);
    				
    				if(ff_level < LEVEL_MAX){
        				ff_level = ff_level + 1;
        				ff_number[ff_level] = folder_cur;     					
    				}
    						
	    			item_player.clear();
	    			path_player.clear();
	    			seqno_player.clear();
	    			myPlayerAda.notifyDataSetChanged();
	    			
	    			//get folder info
	    			folder_seq = -1;
	    			file_seq = 0;
	    			cmd_file_folderinfo[5] = 0;//(byte)(folder_cur&0xff);
		    		cmd_file_folderinfo[6] = 0;//(byte)(folder_cur>>8);
		    		cmd_file_folderinfo[8] = (byte)(folder_cur&0xff);
		    		cmd_file_folderinfo[7] = (byte)(folder_cur>>8);
		    		cmd_file_folderinfo[9] = CheckSum(cmd_file_folderinfo);

					sendHex(cmd_file_folderinfo);
	    		}
	    		
	    		if(tempStr.equals("b5")){  //file
	    			mPath.setVisibility(View.INVISIBLE);
	    			player_pp.setVisibility(View.VISIBLE);
	    			player_stop.setVisibility(View.VISIBLE);
	    		
		    		myPlayerAda.setSelectItem(position);  ///333333
		    		myPlayerAda.notifyDataSetInvalidated();
	    		}else{	    			
	    			mPath.setVisibility(View.VISIBLE);
	    			player_pp.setVisibility(View.INVISIBLE);
	    			player_stop.setVisibility(View.INVISIBLE);
	    			
	    			myPlayerAda.setSelectItem(-1);  ///333333
	    			myPlayerAda.notifyDataSetInvalidated();
	    		}	    		
	    	}
	    });
	    
    	Button setButt = (Button)findViewById(R.id.backbutton1);
    	setButt.setOnClickListener(new Button.OnClickListener(){
    		@Override
    		public void onClick(View v)
    		{
    			//clear RingBuffer in case pakcet disorder
    			pRingStart = START_IDX;
    			pRingEnd = START_IDX; 
    			
    			JumpToLayoutSetting();
    		}
    	});
    	
    	Button refreshButt = (Button)findViewById(R.id.refreshbutton1);
    	refreshButt.setOnClickListener(new Button.OnClickListener(){
    		@Override
    		public void onClick(View v)
    		{
    			//clear RingBuffer in case pakcet disorder
    			pRingStart = START_IDX;
    			pRingEnd = START_IDX; 
    			
    			item_player.clear();
    			path_player.clear();
    			seqno_player.clear();
    			
    			//get folder info
    			folder_seq = -1;
    			file_seq = 0;
    			cmd_file_folderinfo[5] = 0;
	    		cmd_file_folderinfo[6] = 0;
	    		cmd_file_folderinfo[8] = (byte)(folder_cur&0xff);
	    		cmd_file_folderinfo[7] = (byte)(folder_cur>>8);
	    		cmd_file_folderinfo[9] = CheckSum(cmd_file_folderinfo);

				sendHex(cmd_file_folderinfo);
    		}
    	});
	    
    	refresh_folder();
    	
    }
  
    private void refresh_folder(){
    	//clear RingBuffer in case pakcet disorder
		pRingStart = START_IDX;
		pRingEnd = START_IDX; 
		
		item_player.clear();
		path_player.clear();
		seqno_player.clear();
		
		//get folder info
		folder_seq = -1;
		file_seq = 0;
		cmd_file_folderinfo[5] = 0;
		cmd_file_folderinfo[6] = 0;
		cmd_file_folderinfo[8] = (byte)(folder_cur&0xff);
		cmd_file_folderinfo[7] = (byte)(folder_cur>>8);
		cmd_file_folderinfo[9] = CheckSum(cmd_file_folderinfo);

		sendHex(cmd_file_folderinfo);
    }
    
    
    public byte CheckSum(byte[] barray){  //calculate CheckSum
    	
    	char cs = 0;
    	for(int i=2; i<barray.length - 1; i++){
    		cs = (char)((cs - barray[i]) & 0xff);
    	}
    	barray[barray.length - 1] = (byte) cs;
    	return barray[barray.length-1];
    }

  
    protected void onActivityResult (int requestCode, int resultCode, Intent data)
    {
    	if(D) Log.v(TAG, "on Activity Result:"+Integer.toString(requestCode)); 
    	
        if(requestCode == REQUEST_ENABLE_BT)
        {
            if(resultCode == RESULT_OK){
            	statustxt.setText("蓝牙开关：已开启");
            	connb.setEnabled(true);
            	setupServ();
            }else{
            	//RESULT_CANCELED
            	tb.setChecked(false);
    			connb.setEnabled(false);
            }
        } else if(requestCode == SUBACT_BLUELIST){
        	if(D) Log.v(TAG, "result code:"+Integer.toString(resultCode));
        	if(resultCode == RESULT_OK){
        		// Get the device MAC address
                String address = data.getExtras().getString(BlueListActivity.EXTRA_DEVICE_ADDRESS);
                // Get the BluetoothDevice object
                remotedevice = bluetooth.getRemoteDevice(address);
            
                // Attempt to connect to the device
                mChatService.connect(remotedevice);
        	}
                        
        }
        
    
	}
    
    
    @Override
    public synchronized void onResume() {
        super.onResume();
        if(D) Log.v(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
              // Start the Bluetooth chat services
              mChatService.start();
            }
        }
		
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
        if(D) Log.v(TAG, "- ON DESTROY -");
    }
 
    
    private void setupServ() {
//        Log.d(TAG, "setupServ");

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

    }
    

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	if(D) Log.i(TAG, "handleMessage: " + Integer.toString(msg.what) );  //1111111
            switch (msg.what) {
            
            case MESSAGE_STATE_CHANGE:
            	if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);//111111
                
                switch (msg.arg1) {
                case BluetoothChatService.STATE_CONNECTED:
                    conntxt.setText(R.string.title_connected_to);
                    if(mConnectedDeviceName != null) conntxt.append(mConnectedDeviceName);
                    connb.setText(R.string.disconnectButton);
                    
                    tabs.getTabWidget().getChildAt(VIEW_SETT).setVisibility(View.VISIBLE);
                    
                    break;
                case BluetoothChatService.STATE_CONNECTING:
                	conntxt.setText(R.string.title_connecting);
                    break;
                case BluetoothChatService.STATE_LISTEN:
                case BluetoothChatService.STATE_NONE:
                	conntxt.setText(R.string.title_not_connected);
                	connb.setText(R.string.connButton);
                	
                	tabs.setCurrentTab(VIEW_BT);
                	tabs.getTabWidget().getChildAt(VIEW_SETT).setVisibility(View.GONE);
                	tabs.getTabWidget().getChildAt(VIEW_PLAYER).setVisibility(View.GONE);
                	tabs.getTabWidget().getChildAt(VIEW_FM).setVisibility(View.GONE);
                    break;
                }
                break;
                
            case MESSAGE_WRITE:
            	if(view_tab == VIEW_BT){
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    sndCnt += writeMessage.length();
                    sndTxt.setText("TX: "+Integer.toString(sndCnt));
                    //Log.d(TAG, "W:"+writeMessage);            		
            	}
                break;
                
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                int readLen = msg.arg1;
//                if(msg.arg1 == 1)
//                    Log.d(TAG,"len:"+msg.arg1);
                
                if(VIEW_BT == view_tab)
                {
//                	for(int i=0; i<readLen; i++){
//                		recBuf.append(Integer.toHexString(readBuf[i]&0xff)+" ");
//                	}
                	if((pRingEnd + readLen) < RING_LEN){
                		for(int i=0; i<readLen; i++){
                			mInBufRing[pRingEnd+i] = readBuf[i];
                		}
                		pRingEnd = pRingEnd + readLen;
                		//if(pRingEnd >= 6)
                		{
                        	recCnt += pRingEnd;
                        	for(int j=0; j<pRingEnd; j++)
                        		recBuf.append(Integer.toString(mInBufRing[j]&0xff, 16)+" ");
                        	pRingEnd = 0;
        	                recTxt.setText("RX: "+ recCnt);                			
                		}
                	}
            		                	
                }else {  //if(VIEW_SETT == view_tab) || (VIEW_PLAYER == view_tab)
                	if((pRingEnd + readLen) < RING_LEN){
                		for(int i=0; i<readLen; i++){
                			mInBufRing[pRingEnd+i] = readBuf[i];
                		}
                		pRingEnd = pRingEnd + readLen;
                	}else{
                		for(int i=0; i<(RING_LEN-pRingEnd); i++){
                			mInBufRing[pRingEnd+i] = readBuf[i];
                		}                		
                		for(int j=0; j<(pRingEnd+readLen-RING_LEN); j++){
                			mInBufRing[j] = readBuf[RING_LEN-pRingEnd+j];
                		}
                		pRingEnd = pRingEnd+readLen-RING_LEN;
                	}
                	handle_recv();  //need to release 
                	
                }

                break;
                
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "连接到 "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
        
        
    };
    
    
    private void sendHex(byte hexx[]) {
    	if(D) Log.d(TAG,"send hex:"+hexx.length); 
//    	for(int i=0; i<hexx.length; i++){
//    		Log.d(TAG, Integer.toString(i)+":"+hexx[i]);
//    	}
    		
    	
        // Check that we're actually connected before trying anything
        if ((mChatService==null) || (mChatService.getState() != BluetoothChatService.STATE_CONNECTED)) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (hexx.length > 0) {
            // tell the BluetoothChatService to write
            mChatService.write(hexx);
        }
    }
    
    private void handle_recv(){
    	int ret = -1;
    	byte[] onepac = null;
    	
      do{  //44444
    	  ret = -1;
    	if((pRingEnd > pRingStart) && ((pRingEnd-pRingStart)>=6)){
    		if(pRingStart<(RING_LEN-2)){
    			int len = 6 + mInBufRing[pRingStart+2] & 0xff;
    			
    			if(D) Log.d(TAG, "PacLen, len:"+len+" start:"+pRingStart+" End:"+pRingEnd); //444444
//    			for(int i=pRingStart; i<pRingEnd; i++){
//    				Log.d(TAG, " "+(mInBufRing[i]&0xff) );
//    			}

        		if ((pRingEnd-pRingStart) >= len){  //we have 1 complete packet
        			
        			onepac = new byte[len];
        			int i = 0;
        			while(i < len){
        				onepac[i] = mInBufRing[pRingStart+i];
        				i = i + 1;
        			}
        			pRingStart = pRingStart + len;
        			ret = handle_1pac(onepac, len);
        		}
    		}
    		
    	} else if((pRingEnd < pRingStart) && ((pRingEnd+RING_LEN-pRingStart)>=6)){
    		if((pRingStart+2) < RING_LEN){
    			int len = 6 + mInBufRing[pRingStart+2] & 0xff;
    			if(D) Log.d(TAG, "pacLen:"+(6+mInBufRing[pRingStart+2])+" Start:"+pRingStart+" End:"+pRingEnd);  //
//    			for(int i=pRingStart; i<RING_LEN; i++){
//    				Log.d(TAG, " "+(mInBufRing[i]&0xff) );
//    			}
//    			for(int j=0; j<pRingEnd; j++){
//    				Log.d(TAG, " "+(mInBufRing[j]&0xff));
//    			}
    			
    			if((pRingEnd + RING_LEN - pRingStart) >= len){ //we have 1 complete packet
    				//int len = 6 + mInBufRing[pRingStart+2]&0xff;
    				//Log.d(TAG, "Receive 1 packet, start:"+pRingStart+" len:"+(pRingEnd+RING_LEN-pRingStart));
    				onepac = new byte[len];
    				int i = 0;
    				while(((pRingStart+i) < RING_LEN) && (i < len)){
    					onepac[i] = mInBufRing[pRingStart+i];
    					i = i+1;
    				}
    				pRingStart = (pRingStart + i) % RING_LEN;
    				if(i < len){ //ring back to 0 and start again
    					pRingStart = 0;
    					while(i < len){
    						onepac[i] = mInBufRing[pRingStart];
    						i = i + 1;
    						pRingStart = pRingStart + 1;
    					}
    				}
    				
    				ret = handle_1pac(onepac, len);
    			}
    		} else {
    			int len = 6 + mInBufRing[pRingStart+2-RING_LEN] & 0xff;
    			if(D) Log.d(TAG, "paclen:"+(6+mInBufRing[pRingStart+2-RING_LEN])+" Start:"+pRingStart+" End:"+pRingEnd); //
//    			for(int i=pRingStart; i<RING_LEN; i++){
//    				Log.d(TAG, " "+(mInBufRing[i]&0xff) );
//    			}
//    			for(int j=0; j<pRingEnd; j++){
//    				Log.d(TAG, " "+(mInBufRing[j]&0xff));
//    			}

    			if((pRingEnd + RING_LEN - pRingStart) >= len){  //we have 1 complete packet
    				//int len = 6 + mInBufRing[pRingStart+2-RING_LEN] & 0xff;
    				//Log.d(TAG, "receive 1 packet, start:"+pRingStart+" len:"+(pRingEnd+RING_LEN-pRingStart));
    				onepac = new byte[len];
    				int i = 0;
    				while((pRingStart+i) < RING_LEN){
    					onepac[i] = mInBufRing[pRingStart+i];
    					i = i + 1;
    				}
    				pRingStart = 0;
    				while(i < len){
    					onepac[i] = mInBufRing[pRingStart];
    					pRingStart = pRingStart + 1;
    					i = i + 1;
    				}
    				ret = handle_1pac(onepac, len);
    			}
    		}
    	} 
      }while((ret==0)&&(pRingEnd != pRingStart));
    }
    
    protected int handle_1pac(byte[] onePac, int len){ //event packet len >= 6
    	if(D) Log.d(TAG2, "to handle and remove 1 packet, len:"+len);  //4444444
    	if((onePac[0] != SOP1)||(onePac[1] != SOP2)){
    		Log.e(TAG2, "SOP wrong:"+Integer.toHexString(onePac[0]&0xf)+","+Integer.toHexString(onePac[1]&0xff));
    		for (int error_i=0; error_i<(len-2); error_i++) Log.e(TAG2, Integer.toHexString(onePac[2+error_i]&0xff));
    		
    		pRingStart = START_IDX;
    		pRingEnd = START_IDX;
    		return 1;
    	}
    	
    	int pacLen = onePac[2] & 0xff;
    	int evnt_cat = onePac[3] & 0xff;
    	int evnt_code = onePac[4] & 0xff;
    	byte cs = CheckSum(onePac);
    	if(onePac[len-1] != cs){
    		//check packet content to see what's wrong
    		Log.e(TAG2, "checksum:"+onePac[len-1]+" rcvd:"+cs);
    	}
    	
    	switch(evnt_cat){
    	case 0x81: //mode
    		switch(evnt_code){
    		case 01:  //next mode
    		case 03: //get mode
    			if(pacLen != 1)
    			    Log.e(TAG2, "len should be 1:"+pacLen);
    			if(D) Log.d(TAG, "got mode:"+onePac[5]); //111111
    			if(onePac[5] < MODE_CNT){
    				mode_cur = onePac[5];
    				if(view_tab == VIEW_SETT){
						tv_mode = (TextView)findViewById(R.id.textViewMode);
		            	tv_mode.setText(mode_show[mode_cur]);
		            	
		            	//after get mode and it is music, then to get volume
		            	if(//(evnt_code == 3) && 
		            	((mode_cur == MODE_SD) || (mode_cur == MODE_USB))){
		            		sendHex(cmd_audio_volget);	
		            		if(D) Log.d(TAG, "cmd_audio_volget"); //333333
		            		tabs.getTabWidget().getChildAt(VIEW_PLAYER).setVisibility(View.VISIBLE);
		            		tabs.getTabWidget().getChildAt(VIEW_FM).setVisibility(View.GONE);
		            	}else if(mode_cur == MODE_FM){
		            		tabs.getTabWidget().getChildAt(VIEW_FM).setVisibility(View.VISIBLE);
		            		tabs.getTabWidget().getChildAt(VIEW_PLAYER).setVisibility(View.GONE);
		            	}else {
		            		tabs.getTabWidget().getChildAt(VIEW_FM).setVisibility(View.GONE);
		            		tabs.getTabWidget().getChildAt(VIEW_PLAYER).setVisibility(View.GONE);
		            	}
					}
    			}
    			break;
    		case 02: //select mode
    			if(pacLen != 1){
    			    Log.e(TAG2, "len should be 1:"+pacLen);
    			    break;
    			}
    			if(D) Log.d(TAG, "set mode:"+onePac[5]);
    			if(onePac[5] < MODE_CNT){
    				mode_cur = onePac[5];
    				if(view_tab == VIEW_SETT){
						tv_mode = (TextView)findViewById(R.id.textViewMode);
		            	tv_mode.setText(mode_show[mode_cur]);						
					} else if(view_tab == VIEW_PLAYER){
						
//			    		folder_seq = -1;
//			    		file_seq = 0;
						//to get root dir info
//			    		cmd_file_folderinfo[5] = 0;
//			    		cmd_file_folderinfo[6] = 0;
//			    		cmd_file_folderinfo[8] = (byte)folder_root;
//			    		cmd_file_folderinfo[7] = 0;
//			    		cmd_file_folderinfo[9] = CheckSum(cmd_file_folderinfo);

//						sendHex(cmd_file_folderinfo);
					}
    			}
    			break;
    		case 04: //get device link?
    			//Todo
    			if(D) Log.d(TAG2, "get device link:"+onePac[5]);
    			break;
    		}
    		break;
    	case 0x82: //file system
    		switch(evnt_code){
    		case 0x01: //get folder sum
    			if(pacLen != 4){
    				Log.e(TAG, "len should be 4:"+pacLen);
    				break;
    			}
    			//folder_sum = (onePac[7]<<8)+(onePac[8]&0xff);
    			if(D) Log.d(TAG, "folder#:"+((onePac[5]<<8)+(onePac[6]&0xff)) + " null folder#:"+((onePac[7]<<8)+(onePac[8]&0xff)));
    			
    			//to get root dir info
        		cmd_file_folderinfo[5] = 0;
        		cmd_file_folderinfo[6] = 0;
        		cmd_file_folderinfo[8] = 1; //(byte)folder_cur;
        		cmd_file_folderinfo[7] = 0;
        		cmd_file_folderinfo[9] = CheckSum(cmd_file_folderinfo);

    			sendHex(cmd_file_folderinfo);
    			break;
    		case 0x02: //get file sum
    			if(pacLen != 2){
    				Log.e(TAG, "len should be 2:"+pacLen);
    				break;
    			}
    			if(D) Log.d(TAG, "file#:"+((onePac[5]<<8)+(onePac[6]&0xff)));
    			break;
    		case 0x03: //folder info
    			if(pacLen < 0x17){
    				Log.e(TAG, "folder info len should at least 23:"+pacLen);
    				break;
    			}
    			
    			int len_lfname = onePac[27] & 0xff;
    			StringBuffer sbf = new StringBuffer();
    			byte[] folder_name = new byte[8];
    			if(len_lfname > 0){ //have long name
    				
    				if((len_lfname & 0x1) != 0) {
    					Log.e(TAG, "long folder name len error:"+len_lfname);
    					break;
    				}
    				int lcnt = 0;
    				
    				while(lcnt < len_lfname){
    					char c = (char) ((onePac[lcnt+29]<<8)+(onePac[lcnt+28]&0x0ff));
    					sbf.append(c);
    					lcnt = lcnt + 2;
    				}
    				if(D) Log.d(TAG, sbf.toString()); 
    				
    			} else {
        			
        			for(int i=0; i<8; i++){
        				folder_name[i] = onePac[19+i];
        			}
        			if(D) Log.d(TAG, "folder name:"+ new String(folder_name));
    				
    			}
    				
    			
    			//if(folder_name[0] == 0x5c){ //"\"
    			if((folder_seq==-1)&&(file_seq==0)){ //root or enter sub-folder
    				file_sum = (onePac[11]<<8) + (onePac[12]&0xff);
    				folder_sum = (onePac[13]<<8) + (onePac[14]&0xff);
    				file_seq = file_sum;
    				folder_seq = folder_sum;
    				if(D) Log.d(TAG, "folder#:"+ folder_sum +",file#:"+file_sum);
    			
    				item_player.clear();
	    			path_player.clear();
	    			seqno_player.clear();
    				item_player.add("b1");
    			    path_player.add(rootPath);
    			    seqno_player.add(1);
    			    item_player.add("b2");
    			    path_player.add(rootPath); //to parent
    			    seqno_player.add(1);
    			    myPlayerAda.notifyDataSetChanged();
    			    
    				if(folder_seq > 0){
    					cmd_file_folderinfo[6] = (byte)(folder_cur&0xff);
    		    		cmd_file_folderinfo[5] = (byte)(folder_cur>>8);
    		    		cmd_file_folderinfo[8] = (byte)(folder_seq&0xff);
    		    		cmd_file_folderinfo[7] = (byte)(folder_seq>>8);
    		    		cmd_file_folderinfo[9] = CheckSum(cmd_file_folderinfo);

    					sendHex(cmd_file_folderinfo);
    					folder_seq = folder_seq - 1;
    					
    				} else if(file_seq > 0){
    					cmd_file_fileinfo[6] = (byte)(folder_cur&0xff);
    					cmd_file_fileinfo[5] = (byte)(folder_cur>>8);
    					cmd_file_fileinfo[8] = (byte)(file_seq&0xff);
    					cmd_file_fileinfo[7] = (byte)(file_seq>>8);
    					cmd_file_fileinfo[9] = CheckSum(cmd_file_fileinfo);

    					sendHex(cmd_file_fileinfo); 
    					file_seq = file_seq - 1;
    				} 
    			}
    			else 
    			{  //get sub-folder info
    				if(true) { //(onePac[5]==onePac[7])&&(onePac[6]==onePac[8])){//none blank folder
    					if(D) Log.d(TAG, "show:"+folder_cur);
        				
    					seqno_player.add(((onePac[5]<<8) + (onePac[6]&0xff)));
    				    if(len_lfname > 0){
    				    	path_player.add(sbf.toString());
    				    }else{
    				    	path_player.add(new String(folder_name));	
    				    }
    				    if((onePac[11]==0)&&(onePac[12]==0)&&(onePac[13]==0)&&(onePac[14]==0)){
    				    	item_player.add("b4"); //blank folder
    				    }else{
    				    	item_player.add("b3");
    				    }
    				    myPlayerAda.notifyDataSetChanged(); 

    				}


    				if(folder_seq > 0){
    					cmd_file_folderinfo[6] = (byte)(folder_cur&0xff);
    		    		cmd_file_folderinfo[5] = (byte)(folder_cur>>8);
    		    		cmd_file_folderinfo[8] = (byte)(folder_seq&0xff);
    		    		cmd_file_folderinfo[7] = (byte)(folder_seq>>8);
    		    		cmd_file_folderinfo[9] = CheckSum(cmd_file_folderinfo);
    		    		
    					sendHex(cmd_file_folderinfo); 
    					folder_seq = folder_seq - 1;
    					
    				} else if(file_seq > 0){
    					cmd_file_fileinfo[6] = (byte)(folder_cur&0xff);
    					cmd_file_fileinfo[5] = (byte)(folder_cur>>8);
    					cmd_file_fileinfo[8] = (byte)(file_seq&0xff);
    					cmd_file_fileinfo[7] = (byte)(file_seq>>8);
    					cmd_file_fileinfo[9] = CheckSum(cmd_file_fileinfo);

    					sendHex(cmd_file_fileinfo); 
    					file_seq = file_seq - 1;
    				} 
    			}
    			break;
    		case 0x04: //file info
    			if(pacLen < 0x12){
    				Log.e(TAG, "file info len should at least 18:"+pacLen);
    				break;
    			}

    			if((onePac[22]&0xff) > 0){  //have long name
    				int len_llname = onePac[22];
    				if((len_llname & 0x1) != 0) {
    					Log.e(TAG, "long name len error:"+len_llname);
    					break;
    				}
    				int lcnt = 0;
    				StringBuffer sb = new StringBuffer();
    				while(lcnt < len_llname){
    					char c = (char) ((onePac[lcnt+24]<<8)+(onePac[lcnt+23]&0x0ff));
    					sb.append(c);
    					lcnt = lcnt + 2;
    				}
    				if(D) Log.d(TAG, sb.toString());
    				
    				path_player.add(sb.toString()); 
    			}else{//use short name
        			byte[] file_name = new byte[11];
        			for(int i=0; i<11; i++){
        				file_name[i] = onePac[11+i];
        			}
        			
        			if(D) Log.d(TAG, "file name:"+ new String(file_name));
        			path_player.add(new String(file_name)); 
    			}

    			seqno_player.add((onePac[7]<<8) + (onePac[8]&0xff));
    			item_player.add("b5");
			    myPlayerAda.notifyDataSetChanged();
			    
    			if(file_seq > 0){
					cmd_file_fileinfo[6] = (byte)(folder_cur&0xff);
					cmd_file_fileinfo[5] = (byte)(folder_cur>>8);
					cmd_file_fileinfo[8] = (byte)(file_seq&0xff);
					cmd_file_fileinfo[7] = (byte)(file_seq>>8);
					cmd_file_fileinfo[9] = CheckSum(cmd_file_fileinfo);

					sendHex(cmd_file_fileinfo); 
					file_seq = file_seq - 1;
    			}
    			break;
    		}
    		break;
    	case 0x83: //play control
    		switch(evnt_code){
    		case 0x0d: //select song
    			if(pacLen != 1) Log.e(TAG2, "Len should be 1:"+pacLen);
    			if(D) Log.d(TAG2, "select song ok:"+onePac[5]);
    			break;
    		
    		}
    		break;
    	case 0x84: //audio
    		switch(evnt_code){
    		case 0x1: //vol add
    		case 0x2: //vol sub
    		case 0x3: //set vol
    		case 0x5: //set EQ    		
    		case 0x0a: //set line gain
    			if(pacLen != 0)
    			    Log.e(TAG2, "len should be 0:"+pacLen);
    			break;
    		case 0x7: //mute
    			if(view_tab == VIEW_SETT){
    				ImageButton im3 = (ImageButton)findViewById(R.id.imageButton3);
    				im3.setImageResource(R.drawable.mute);
    			}
    			break;
    		case 0x8: //unmute
    			if(view_tab == VIEW_SETT){
    				ImageButton im3 = (ImageButton)findViewById(R.id.imageButton3);
    				im3.setImageResource(R.drawable.normal);
    			}
    			break;
    		case 0x4:  //get vol
    			if(D) Log.d(TAG, "got vol:"+onePac[5]);  //111111
    			if(onePac[5] < VOL_MAX){
    				vol_cur = onePac[5];
        			if(view_tab == VIEW_SETT){ 
        				SeekBar volset = (SeekBar)findViewById(R.id.seekBar1);
        		    	volset.setProgress(vol_cur);
        			}	
    			}
				break;
    		case 0x6: //get EQ
    			if(D) Log.d(TAG, "got EQ:"+onePac[5]);
    			if(onePac[5] < EQ_CNT){
    				EQ_cur = onePac[5];
        			if(view_tab == VIEW_SETT){
        				//Todo
        			}
    			}
    			break;
    		}
    		break;
    	case 0x85: //RTC
    		//Todo
    		break;
    	case 0x86: //GPIO
    		//Todo 
    		break;
    	case 0x87: //ADC
    		switch(evnt_code){
    		case 0x04: //Get Vin voltage
    			batt_cur = (onePac[5]<<8) + (onePac[6]&0xff);
    			if(D) Log.d(TAG, "got vin voltage:"+batt_cur); //1111111
    			show_batt();
    			break;
    		
    		}
    		break;
    	case 0x88: //PWM
    		//Todo
    		break;
    	case 0x89: //wake up
    		//Todo
    		break;
    	case 0x8A: //radio
    		switch(evnt_code){
    		case 0x03: //next step
    			freq_cur = freq_cur + 1;
    			if(freq_cur > FREQ_MAX) freq_cur = FREQ_MIN;
			    tv_freq.setText((float)freq_cur/10 + "MHz");
    			break;
    		case 0x04: //prev step
    			freq_cur = freq_cur - 1;
    			if(freq_cur < FREQ_MIN) freq_cur = FREQ_MAX;
			    tv_freq.setText((float)freq_cur/10 + "MHz");
    			break;
    		case 0x0B: //get current status
    			if(pacLen != 6){
    				Log.e(TAG, "file info len should 6:"+pacLen);
    				break;
    			}
    			int freq_temp = (onePac[7]&0xff) + (onePac[8]<<8);
    			if(true) Log.d(TAG, "got freq:"+freq_temp); //1111111
    			if((freq_temp <= FREQ_MAX) && (freq_temp >= FREQ_MIN)){
    				freq_cur = freq_temp;
    			    tv_freq.setText((float)freq_cur/10 + "MHz");
    			}    			
    			break;
    		}
    		break;
    	case 0xf0: //special
    		switch(evnt_code){
    		case 0x01:// enable vb
    			if(VIEW_SETT == view_tab){
    				ImageButton vbButt = (ImageButton)findViewById(R.id.vbImageButton);
        			vbButt.setImageResource(R.drawable.vbon);
    			}
    			break;
    		case 0x02: //disable vb
    			if(VIEW_SETT == view_tab){
    				ImageButton vbButt2 = (ImageButton)findViewById(R.id.vbImageButton);
        			vbButt2.setImageResource(R.drawable.vboff);	
    			}
    			break;
    		}
    		break;
    	}
        return 0;
    }


    
    	
}
