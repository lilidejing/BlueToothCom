/*
 * fooree.net@hotmail.com
 */

package cn.com.jdsc;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;



public class BlueListActivity  extends ListActivity{
	private static final String TAG = "blueList";
	private BluetoothAdapter bluetooth; 
	private Button discButt;
	private ArrayAdapter<String> mArrayAdapter;
	private int connectState;
	private String name = "TBD";
	private static final boolean D = false;
   
    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sub_bluetooth);
		
		List<String> planets = new ArrayList<String>(); 
		mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, planets);
		setListAdapter(mArrayAdapter);
		
		
		// Initialize array adapters. One for already paired devices and one for newly discovered devices
        //mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.device_name);
//        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.sub_bluetooth);
        
        // Find and set up the ListView for newly discovered devices
//        ListView newDevicesListView = (ListView) findViewById(R.id.list);
//        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
//        newDevicesListView.setOnItemClickListener(mDeviceClickListener);
        
		bluetooth = BluetoothAdapter.getDefaultAdapter();
   		//连接设备
		Set<BluetoothDevice> pairedDevices = bluetooth.getBondedDevices();
		
		// If there are paired devices
		if (pairedDevices.size() > 0) {
		    // Loop through paired devices
			Log.v(TAG, "paired devices number" + Integer.toString(pairedDevices.size()));
				
			//for(int i=0; i<pairedDevices.size(); i++)
			//while(pairedDevices.iterator().hasNext())
			for (BluetoothDevice device : pairedDevices)
			{
			   /// device = (BluetoothDevice) pairedDevices.iterator().next();
			    //System.out.println(device.getName()+"\n"+ device.getAddress());
			
			    // Add the name and address to an array adapter to show in a ListView
			    mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
			         
			}
			mArrayAdapter.notifyDataSetChanged();
		}
		

		// Set broadcast filter
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
		intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

		// Register broadcast
		registerReceiver(receiver, intentFilter);
		
		discButt = (Button)findViewById(R.id.discButton1);
		discButt.setOnClickListener(new Button.OnClickListener(){
			@Override
        	public void onClick(View v)
        	{
				mArrayAdapter.clear();
				mArrayAdapter.notifyDataSetChanged();
				
				bluetooth = BluetoothAdapter.getDefaultAdapter();
		   		//连接设备
				Set<BluetoothDevice> pairedDevices = bluetooth.getBondedDevices();
				// If there are paired devices
				if (pairedDevices.size() > 0) {
				    // Loop through paired devices
					Log.v(TAG, "paired devices:" + Integer.toString(pairedDevices.size()));
						
					//for(int i=0; i<pairedDevices.size(); i++)
					//while(pairedDevices.iterator().hasNext())
					for (BluetoothDevice device : pairedDevices)
					{
					   /// device = (BluetoothDevice) pairedDevices.iterator().next();
					    //System.out.println(device.getName()+"\n"+ device.getAddress());
					
					    // Add the name and address to an array adapter to show in a ListView
					    mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
					         
					}
					mArrayAdapter.notifyDataSetChanged();	
				}
				
				discButt.setEnabled(false);
				// Search BT devices, android while search the devices by broadcast
				bluetooth.startDiscovery();

        	}
		});


	}
	

    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	if(D) Log.v(TAG, "position:"+Integer.toString(position));
    	
    	bluetooth.cancelDiscovery();
    	
    	// Get the device MAC address, which is the last 17 chars in the View
        String info = ((TextView) v).getText().toString();
        String address = info.substring(info.length() - 17);
        
        if(D) Log.v(TAG, "address:"+address);
        
        // Create the result Intent and include the MAC address
        Intent intent = new Intent();
		intent.putExtra(EXTRA_DEVICE_ADDRESS , address);
        // Set result and finish this Activity
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		 @Override
		 public void onReceive(Context context, Intent intent) {
		     String action = intent.getAction();
		     
		     if(action == null) return;
		     
		     if (BluetoothDevice.ACTION_FOUND.equals(action)) {
		         // Get the BT devices that found
		         BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		         
//		         System.out.println(device.getName()+"\n"+device.getAddress());		         
		         mArrayAdapter.add(device.getName() + "\n" + device.getAddress()); //temp added
		         
		         // Deal with the BT device
		         if (false){ //device.getName().equalsIgnoreCase(name)) {
		             // Search the BT device will use large resource, if found close search
		             bluetooth.cancelDiscovery();
		             // Get the connection state of BT devices
		             connectState = device.getBondState();
		             switch (connectState) {

				        case BluetoothDevice.BOND_NONE:
				            // Preparing
				            try {
				                Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
				                createBondMethod.invoke(device);
				            } catch (Exception e) { 
				                e.printStackTrace();
				            }
				            break;
				        // Prepared
				        case BluetoothDevice.BOND_BONDED:
				            try {
				                connect(device);
				            } catch (IOException e) {
				                e.printStackTrace();
				            }
				            break;
		             }
		         }
		     } else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
		    	 if(D) Log.v(TAG, "Bond state changed to "+Integer.toString(connectState));
		         // The broadcast that the state changed
		         BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		         if (device.getName().equalsIgnoreCase(name)) { 
		             connectState = device.getBondState();
		             switch (connectState) {
				     case BluetoothDevice.BOND_NONE: 	      break;
				     case BluetoothDevice.BOND_BONDING:       break;
				     case BluetoothDevice.BOND_BONDED:
		                 try {
		                     connect(device);
		                 } catch (IOException e) {
		                      e.printStackTrace();
                         }
		                 break;
		             }
		         }
		     } else if(bluetooth.ACTION_DISCOVERY_FINISHED.equals(action)){
		    	 if(D) Log.v(TAG, "discovery finished.");
		    	 discButt.setEnabled(true);
		     }
		  }
	  };
	

		// To connect BT
		private void connect(BluetoothDevice device) throws IOException {
		    // Fix UUID
		    final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
		    UUID uuid = UUID.fromString(SPP_UUID);
		    BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuid);
		    socket.connect();
		}
	
		@Override
		protected void onStop()
		{
		    unregisterReceiver(receiver);
		    super.onStop();
		}
		

}