package com.example.digitalmeasuringtape;

import java.util.ArrayList;

public class TailLinkedList {
  private Node head;
  private Node tail;
  public ArrayList<Float> xData;
  public ArrayList<Float> yData;
  public ArrayList<Float> zData;
  public ArrayList<Float> oxData;
  public ArrayList<Float> oyData;
  public ArrayList<Float> ozData;
  public ArrayList<Long> tData;

  public TailLinkedList() {
	  head = null;
	  tail = null;
	  xData = new ArrayList<Float>();
	  yData = new ArrayList<Float>();
	  zData = new ArrayList<Float>();
	  oxData = new ArrayList<Float>();
	  oyData = new ArrayList<Float>();
	  ozData = new ArrayList<Float>();
	  tData = new ArrayList<Long>();
  }

  public TailLinkedList(Node firstNode) {
	  head = firstNode;
	  tail = firstNode;
	  xData = new ArrayList<Float>();
	  yData = new ArrayList<Float>();
	  zData = new ArrayList<Float>();
	  oxData = new ArrayList<Float>();
	  oyData = new ArrayList<Float>();
	  ozData = new ArrayList<Float>();
	  tData = new ArrayList<Long>();
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
  
  
  public void unravel(){
	  System.out.println("Entering Unravel");
	  Node trav = head;
	  if(trav == null) return;
	  while(trav.next != null) {
		  xData.add(trav.x);
		  yData.add(trav.y);
		  zData.add(trav.z);
		  oxData.add(trav.ox);
		  oyData.add(trav.oy);
		  ozData.add(trav.oz);
		  tData.add(trav.time);
		  trav = trav.next;
	  }
	  return;
  }
  
  public ArrayList<Float> getxData() {
   System.out.println("Entering getxData");
   ArrayList<Float> xDataToReturn = new ArrayList<Float>();
   Node trav = head;
   if (trav == null) return null;
   while(trav.next != null) {
    xDataToReturn.add(trav.x);
    trav = trav.next;
   }
   return xData;
  }

  public ArrayList<Float> getyData() {
	  System.out.println("Entering getyData");
   ArrayList<Float> yDataToReturn = new ArrayList<Float>();
   Node trav = head;
   if (trav == null) return null;
   while(trav.next != null) {
    yDataToReturn.add(trav.y);
    trav = trav.next;
   }
   return yData;
  }

  public ArrayList<Float> getzData() {
	  System.out.println("Entering getzData");
   ArrayList<Float> zDataToReturn = new ArrayList<Float>();
   Node trav = head;
   if (trav == null) return null;
   while(trav.next != null) {
	   zDataToReturn.add(trav.z);
	   trav = trav.next;
   }
   return zData;
  	}

  public ArrayList<Float> gettData() {
	  System.out.println("Entering gettData");
	ArrayList<Float> tDataToReturn = new ArrayList<Float>();
	Node trav = head;
	if (trav == null) return null;
	float t0 = trav.time;
	while(trav.next != null) {
		//float t = trav.time;
		double t = (double)trav.time - t0;
		//t = t % 1000000;
		t = t / 1000000000.0;
		tDataToReturn.add((float)t); 
		trav = trav.next;
	}
	return tData;
  }
  
  public ArrayList<Float> getoxData() {
	   System.out.println("Entering getoxData");
	   ArrayList<Float> oxDataToReturn = new ArrayList<Float>();
	   Node trav = head;
	   if (trav == null) return null;
	   while(trav.next != null) {
	    oxDataToReturn.add(trav.ox);
	    trav = trav.next;
	   }
	   return oxData;
	  }
  
  public ArrayList<Float> getoyData() {
	   System.out.println("Entering getoyData");
	   ArrayList<Float> oyDataToReturn = new ArrayList<Float>();
	   Node trav = head;
	   if (trav == null) return null;
	   while(trav.next != null) {
	    oyDataToReturn.add(trav.oy);
	    trav = trav.next;
	   }
	   return oyData;
	  }
  
  public ArrayList<Float> getozData() {
	   System.out.println("Entering getozData");
	   ArrayList<Float> ozDataToReturn = new ArrayList<Float>();
	   Node trav = head;
	   if (trav == null) return null;
	   while(trav.next != null) {
	    ozDataToReturn.add(trav.oz);
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
