package com.guardian2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	static Context appContext;
	private static final String TAG = "Guardian";
	UserController userController;
	TextView txtName, txtId, txtPrivilege, labelIntro, txtAccess;    // instances of manipulated view components
	Handler h;								// Handler for bluetooth messages
	Guardian myApp;							// Holds global variable
	int requiredPrivilege, userPrivilege;
	final int RECIEVE_MESSAGE = 1;        // Status  for Handler
	private BluetoothAdapter btAdapter = null;
	private BluetoothSocket btSocket = null;
	private StringBuilder sb = new StringBuilder();
	private ConnectedThread mConnectedThread;
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");   // SPP UUID service
	private static String address = "00:15:FF:F4:0C:07";   // MAC-address of the sensor node Bluetooth module

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);   // View activity_main
		appContext=getApplicationContext();
		userController = new UserController(this);
		myApp = ((Guardian)getApplicationContext());
		
		/* View Components */
		txtName = (TextView) findViewById(R.id.txtName); 
	    txtId = (TextView) findViewById(R.id.txtId); 
	    txtPrivilege = (TextView) findViewById(R.id.txtPrivilege); 
	    labelIntro = (TextView) findViewById(R.id.labelIntro); 
	    txtAccess = (TextView) findViewById(R.id.txtAccess); 
	    requiredPrivilege = myApp.getRoomPrivilege();
	    /* -------------- */
	    
	    /* Bluetooth Connection */
	    btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
	    checkBTState();
	    /* -------------------- */
	    
	    
	    h = new Handler() {			// Message Handler (Processing)
	        public void handleMessage(android.os.Message msg) {
	            switch (msg.what) {
	            case RECIEVE_MESSAGE:                                                   // If receive massage
	                byte[] readBuf = (byte[]) msg.obj;
	                String strIncom = new String(readBuf, 0, msg.arg1);                 // Create string
	                sb.append(strIncom);                                            
	                int endOfLineIndex = sb.indexOf("\r\n");                            // Determine the end-of-line
	                if (endOfLineIndex > 0) {                                           // If end-of-line,
	                	String first = sb.substring(0, 1);
	                	if (first.equals("")){										// Flag of RFID code transmission
	                        String sbprint = sb.substring(1, endOfLineIndex);           // Extract string from message
	                        sb.delete(0, sb.length()); 									// Cut string
	                        User usuario = userController.getUserByRfid(sbprint);		// Search for user with Rfid
	                        txtName.setText(usuario.getName());							// Change view to show details
	                        txtId.setText("Id: " + usuario.getUserId());
	                        txtPrivilege.setText("Privilege: " + usuario.getPrivilege());
	                        userPrivilege = usuario.getPrivilege();						// Get room privilege level
	                        
	                        if (userPrivilege >= requiredPrivilege){					// User level enough? YES
	                        	txtAccess.setBackgroundColor(Color.GREEN);
	                        	txtAccess.setText("ACCESS GRANTED");
	                        	mConnectedThread.write("3");							// Authorization Byte = 3 Send over Bluetooth
	                        }
	                        else {														// User level enough? NOT
	                        	txtAccess.setBackgroundColor(Color.RED);				
	                        	txtAccess.setText("ACCESS DENIED");
	                        	mConnectedThread.write("4");							// Authorization Byte = 4 Send over Bluetooth
	                        }
	                	}
	                }
	                break;
	            }
	        };
	    };	
	}
	
	private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
	      if(Build.VERSION.SDK_INT >= 10){
	          try {
	              final Method  m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
	              return (BluetoothSocket) m.invoke(device, MY_UUID);
	          } catch (Exception e) {
	              Log.e(TAG, "Could not create Insecure RFComm Connection",e);
	          }
	      }
	      return  device.createRfcommSocketToServiceRecord(MY_UUID);
	}
	
	public void onResume() {
		super.onResume();
		Log.d(TAG, "...onResume - try connect...");
		
		requiredPrivilege = myApp.getRoomPrivilege();
		
		BluetoothDevice device = btAdapter.getRemoteDevice(address);           // Set up a pointer to the remote node using it's address.
	         
		try {
			btSocket = createBluetoothSocket(device); 							// Create BT Socket
		} catch (IOException e) {
			errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
		}
	   
		btAdapter.cancelDiscovery();

		Log.d(TAG, "...Connecting...");
		try {
			btSocket.connect();
			Log.d(TAG, "....Connection ok...");
		} catch (IOException e) {
			try {
				btSocket.close();
			} catch (IOException e2) {
				errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
			}
		}

		Log.d(TAG, "...Create Socket...");
	    
		mConnectedThread = new ConnectedThread(btSocket);
		mConnectedThread.start();
	}

	private void checkBTState() {
		if(btAdapter==null) { 
			errorExit("Fatal Error", "Bluetooth not support");
		} else {
			if (btAdapter.isEnabled()) {
				Log.d(TAG, "...Bluetooth ON...");
			} else {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, 1);
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "...In onPause()...");
		try     {
			btSocket.close();
		} catch (IOException e2) {
			errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
		}
	}
	private void errorExit(String title, String message){
		Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
		finish();
	}
	
	private class ConnectedThread extends Thread {
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket) {
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) { }

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {
			byte[] buffer = new byte[256];  // buffer store for the stream
			int bytes; 
			// Keep listening to the InputStream
			while (true) {
				try {
					// Read from the InputStream
					bytes = mmInStream.read(buffer);        // Get number of bytes and message in "buffer"
					h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();     // Send to message Handler
				} catch (IOException e) {
					break;
				}
			}
		}

		public void write(String message) {                    // Write BT stream
			Log.d(TAG, "...Data to send: " + message + "...");
			byte[] msgBuffer = message.getBytes();
			try {
				mmOutStream.write(msgBuffer);
			} catch (IOException e) {
				Log.d(TAG, "...Error data send: " + e.getMessage() + "...");     
			}
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 1, Menu.NONE, "Add User");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			Intent intent = new Intent(MainActivity.this, AddUser.class);
			MainActivity.this.startActivity(intent);
			return true;
		}
		return false;
	}
}
