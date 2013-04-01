package com.example.digitalmeasuringtape;

import java.util.ArrayList;

public class TailLinkedList {
  private Node head;
  private Node tail;
  

  public TailLinkedList() {
	  head = null;
	  tail = null;
  }

  public TailLinkedList(Node firstNode) {
   head = firstNode;
   tail = firstNode;
  }

  public void add(float x, float y, float z, float ox, float oy, float oz, long time) {
   Node newNode = new Node(x, y, z, ox, oy, oz, time);
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
  	  
//  public String getxString(){
//	  String xData = "";
//	  System.out.println("Entering getxString");
//	  Node trav = head;
//	  if (trav == null) return null;
//	  while(trav.next != null){
//		  xData = xData + "," + trav.x;
//		  trav = trav.next;
//	  }
//	  return xData;
//  }
  
  public ArrayList<Float> getxData() {
   System.out.println("Entering getxData");
   ArrayList<Float> xData = new ArrayList<Float>();
   Node trav = head;
   if (trav == null) return null;
   while(trav.next != null) {
    xData.add(trav.x);
    trav = trav.next;
   }
   return xData;
  }

  public ArrayList<Float> getyData() {
	  System.out.println("Entering getyData");
   ArrayList<Float> yData = new ArrayList<Float>();
   Node trav = head;
   if (trav == null) return null;
   while(trav.next != null) {
    yData.add(trav.y);
    trav = trav.next;
   }
   return yData;
  }

  public ArrayList<Float> getzData() {
	  System.out.println("Entering getzData");
   ArrayList<Float> zData = new ArrayList<Float>();
   Node trav = head;
   if (trav == null) return null;
   while(trav.next != null) {
	   zData.add(trav.z);
	   trav = trav.next;
   }
   return zData;
  	}

  public ArrayList<Float> gettData() {
	  System.out.println("Entering gettData");
	ArrayList<Float> tData = new ArrayList<Float>();
	Node trav = head;
	if (trav == null) return null;
	float t0 = trav.time;
	while(trav.next != null) {
		//float t = trav.time;
		double t = (double)trav.time - t0;
		//t = t % 1000000;
		t = t / 1000000000.0;
		tData.add((float)t); 
		trav = trav.next;
	}
	return tData;
  }
  
  public ArrayList<Float> getoxData() {
	   System.out.println("Entering getoxData");
	   ArrayList<Float> oxData = new ArrayList<Float>();
	   Node trav = head;
	   if (trav == null) return null;
	   while(trav.next != null) {
	    oxData.add(trav.ox);
	    trav = trav.next;
	   }
	   return oxData;
	  }
  
  public ArrayList<Float> getoyData() {
	   System.out.println("Entering getoyData");
	   ArrayList<Float> oyData = new ArrayList<Float>();
	   Node trav = head;
	   if (trav == null) return null;
	   while(trav.next != null) {
	    oyData.add(trav.oy);
	    trav = trav.next;
	   }
	   return oyData;
	  }
  
  public ArrayList<Float> getozData() {
	   System.out.println("Entering getozData");
	   ArrayList<Float> ozData = new ArrayList<Float>();
	   Node trav = head;
	   if (trav == null) return null;
	   while(trav.next != null) {
	    ozData.add(trav.oz);
	    trav = trav.next;
	   }
	   return ozData;
	  }

  private class Node {
  	public float x;
  	public float y;
  	public float z;
  	public float ox;
  	public float oy;
  	public float oz;
  	
  	public long time;
  	public Node next;

 	 public Node(	float newX, float newY, float newZ,
 			 		float newOX, float newOY, float newOZ,
 			 		long newTime) {
   		x = newX;
   		y = newY;
   		z = newZ;
   		ox = newOX;
   		oy = newOY;
   		oz = newOZ;
	   time = newTime;
	   next = null;
	  }

 }

 }
