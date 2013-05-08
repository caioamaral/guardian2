package com.guardian2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

public class SetRoomPrivilege extends Activity{
	Spinner spnSetPrivilege;
	Button btnStart;
	Guardian myApp;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        setContentView(R.layout.set_privilege);
        spnSetPrivilege = (Spinner) findViewById(R.id.spnSetPrivilege);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(btnStartOnClick);
        myApp = ((Guardian)getApplicationContext());
	}
	
	private final Button.OnClickListener btnStartOnClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
        	myApp.setRoomPrivilege(Integer.parseInt(spnSetPrivilege.getSelectedItem().toString()));
            Intent intent = new Intent(SetRoomPrivilege.this, MainActivity.class);
            SetRoomPrivilege.this.startActivity(intent);
        }
    };
}
