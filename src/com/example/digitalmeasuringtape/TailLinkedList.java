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

  public void add(float x, float y, float z, long time) {
   Node newNode = new Node(x, y, z, time);
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
	while(trav.next != null) {
		double t = (double)trav.time;
		t = t % 1000000;
		t = t / 1000.0;
		tData.add((float)t); 
		trav = trav.next;
	}
	return tData;
  }

  private class Node {
  	public float x;
  	public float y;
  	public float z;
  	public long time;
  	public Node next;

 	 public Node(float newX, float newY, float newZ, long newTime) {
   		x = newX;
   		y = newY;
   		z = newZ;
	   time = newTime;
	   next = null;
	  }

 }

 }
