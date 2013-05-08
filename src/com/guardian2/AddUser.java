package com.guardian2;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import android.widget.TextView;


public class AddUser extends Activity {
	static Context appContext;
	private static final String TAG = "Guardian";
	
	UserController userController;
	Button btnCreate;
	Spinner spnPrivilege;
	EditText editName;
	EditText editRfid;
	EditText editPrivilege, editSearchRfid;
	TextView txtName, txtRfid, txtPrivilege;
	
    private final Button.OnClickListener btnCreateOnClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
        	
            try {
            	int flag = userController.requestAddNewUser(editName.getText().toString(),editRfid.getText().toString(),Integer.parseInt(spnPrivilege.getSelectedItem().toString()));
            	if (flag > 0){
                	Toast.makeText(getApplicationContext(), "User Created", Toast.LENGTH_LONG).show();
                }
            	else Toast.makeText(getApplicationContext(), "Error: User not Created", Toast.LENGTH_LONG).show();
            }
            catch (Exception e) {}
            
            
        }
    };
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main); 
		appContext=getApplicationContext();
		userController = new UserController(this);
		
        setContentView(R.layout.add_user);
        userController = new UserController(this);
        btnCreate = (Button) findViewById(R.id.btnCreate);
        editName = (EditText) findViewById(R.id.editName);
        editRfid = (EditText) findViewById(R.id.editRfid);
        spnPrivilege = (Spinner) findViewById(R.id.spnPrivilege);

        btnCreate.setOnClickListener(btnCreateOnClick);		
	}
    
	public void onResume() {
		super.onResume();
		Log.d(TAG, "...onResume...");
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "...onPause()...");
	}
}
