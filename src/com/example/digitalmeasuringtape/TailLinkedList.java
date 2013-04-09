package com.example.digitalmeasuringtape;

import java.util.ArrayList;

public class TailLinkedList {
  private Node head;
  private Node tail;
  public ArrayList<Float> xData;
  public ArrayList<Float> yData;
  public ArrayList<Float> zData;
  public ArrayList<Float> tData;

  public TailLinkedList() {
	  head = null;
	  tail = null;
	  xData = new ArrayList<Float>();
	  yData = new ArrayList<Float>();
	  zData = new ArrayList<Float>();
	  tData = new ArrayList<Float>();
  }

  public TailLinkedList(Node firstNode) {
	  head = firstNode;
	  tail = firstNode;
	  xData = new ArrayList<Float>();
	  yData = new ArrayList<Float>();
	  zData = new ArrayList<Float>();
	  tData = new ArrayList<Float>();
  }

  public void add(long time, float ... args) {
   Node newNode = new Node(time, args);
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
		  t = trav.time - t0;
		  t /= 1000000000.0;
		  tData.add((float)t);
		  trav = trav.next;
	  }
	  return;
  }
  
  private class Node {
  	public float x;
  	public float y;
  	public float z;
  	
  	public long time;
  	public Node next;

 	 public Node(long newTime, float ... args) {
   		x = args[0];
   		if(args.length >=2) y = args[1];
   		if(args.length >=3) z = args[2];
	   time = newTime;
	   next = null;
	  }

 }

 }
