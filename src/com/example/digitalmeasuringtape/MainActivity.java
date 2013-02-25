package com.example.digitalmeasuringtape;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity implements Runnable{

	private String pi_string;
	private TextView tv;
	private ProgressDialog pd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tv = (TextView) this.findViewById(R.id.text);
		tv.setText("--");
		
	}
	
	//connected to button's onClick
	public void record_measurements(View view){
		pd = ProgressDialog.show(this, "Working..", "Calculating Pi", true, false);
		Thread thread = new Thread(this);
		thread.start();
	}
	
	//put the code to be run during execution here
	public void run(){
		pi_string = (Math.floor((Math.random()*5)+2))+ "\"";
		//signal the outside world
		handler.sendEmptyMessage(0);
	}
	
	//Receive thread messages, interpret them and act as needed
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message mg){
			pd.dismiss();
			tv.setText(pi_string);
		}
	};
	

}
