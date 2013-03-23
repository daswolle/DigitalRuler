package com.example.digitalmeasuringtape;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

public class Calibrate extends Activity { 
	AlertDialog dialog;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("00:05").setTitle("CALIBRATING");
		dialog = builder.create();
		dialog.show();
		
		//TODO call Calibrate()
		
		new CountDownTimer(6000,1000){
			@Override
			public void onTick(long millisUntilFinished){
				dialog.setMessage("00:" + (millisUntilFinished/1000));
			}
			
			@Override
			public void onFinish(){
				dialog.dismiss();
				finish();
			}
		}.start();
		
//        setContentView(R.layout.calibrate);
    }
    
    //TODO add calibrate() function
}