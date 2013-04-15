package com.example.digitalmeasuringtape;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.os.Environment;

public class TailLinkedList {
  private Node head;
  private Node tail;
  public ArrayList<Float> xData;
  public ArrayList<Float> yData;
  public ArrayList<Float> zData;
  public ArrayList<Float> azimuthData;
  public ArrayList<Float> tData;

  public TailLinkedList() {
	  head = null;
	  tail = null;
	  xData = new ArrayList<Float>();
	  yData = new ArrayList<Float>();
	  zData = new ArrayList<Float>();
	  azimuthData = new ArrayList<Float>();
	  tData = new ArrayList<Float>();
  }

  public TailLinkedList(Node firstNode) {
	  head = firstNode;
	  tail = firstNode;
	  xData = new ArrayList<Float>();
	  yData = new ArrayList<Float>();
	  zData = new ArrayList<Float>();
	  azimuthData = new ArrayList<Float>();
	  tData = new ArrayList<Float>();
  }
  
  public void trim(float Gx) {
	  System.out.println("Trimming Linked List");
	  Node trav = head;
	  boolean firstTrim = true;
	  while(head.next != null) {
		  if(trav.x > 4 * Gx && firstTrim) {
			  System.out.println("Trimmed at t = " + trav.time);
			  firstTrim = false;
			  head = trav;
			  break;
		  }
		  trav = trav.next;
	  }
	  
  }

  public void add(long time, float azimuth, float ... args) {
   Node newNode = new Node(time, azimuth, args);
   if(head==null && tail == null)
   {
	   //then this is the first node inserted ever
	   head = newNode;
	   tail = newNode;
   }
   else
   {
	   //then we should stick this after tail,
	   //and then have tail point to this
	   tail.next = newNode;
	   tail = newNode;
   }
  }
  
  public void unravel(){
	  //TODO: filter outliers
	  System.out.println("Entering Unravel");
	  Node trav = head;
	  if(trav == null) return;
	  float t0 = trav.time;
	  float t;
	  while(trav.next != null) {
		  xData.add(trav.x);
		  yData.add(trav.y);
		  zData.add(trav.z);
		  azimuthData.add(trav.azimuth);
		  t = trav.time - t0;
		  t /= 1000000000.0;
		  tData.add(t);
		  trav = trav.next;
	  }
	  return;
  }
  
  public ArrayList<Float> smooth(ArrayList<Float> input){
	  //implementation of basic moving average; m = 1
	  ArrayList<Float> sData = new ArrayList<Float>();
	  float p;
	  float p2;
	  float c;
	  float n;
	  float n2;
	  float avg;
	  
	  int STEPS = input.size();
	  for(int i = 2; i < STEPS-3; i++)
	  {
		  p = input.get(i-1);
		  p2 = input.get(i-2);
		  c = input.get(i);
		  n = input.get(i+1);
		  n2 = input.get(i+2);
		 avg = (p + p2 + c + n + n2)/5;  
		  sData.add(avg);
	  }
	 
	  return sData;
  }
  
	public String listToString(ArrayList<Float> data, String c)
	{
		String x = c + ", ";
		
		for (Float s : data)
		{
			x += s + ", ";
		}
		
		return x;
	}
	
	public void writeGraph(String sFileName, String xData, String yData, String tData)
	{
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    // We can only read the media
		    mExternalStorageAvailable = true;
		    mExternalStorageWriteable = false;
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need
		    //  to know is we can neither read nor write
		    mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		
		if (mExternalStorageAvailable && mExternalStorageWriteable)
		{
			try
			{
				File x = new File(Environment.getExternalStorageDirectory(), "mTape");
				if (!x.exists())
				{
					x.mkdirs();
				}
				
				File y = new File(x, sFileName);
				FileWriter writer = new FileWriter(y);
				writer.append(xData + "\n" + yData + "\n" + tData);
				writer.flush();
				writer.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		} else {
			System.out.println("can't write file");
		}
	}
  
  private class Node {
  	public float x;
  	public float y;
  	public float z;
  	public float azimuth;
  	
  	public long time;
  	public Node next;

 	 public Node(long newTime, float azimuth, float ... args) {
   		x = args[0];
   		if(args.length >=2) y = args[1];
   		if(args.length >=3) z = args[2];
	   time = newTime;
	   this.azimuth = azimuth;  
	   next = null;
	  }

 }

 }
