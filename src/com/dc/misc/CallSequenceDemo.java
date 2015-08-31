package com.dc.misc;

public class CallSequenceDemo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		B b = new B(100);
	}
}

class A {
	public A() {
		System.out.println("Construct A");
	}
}

class B extends A {
	int mVal = 1;
	public B() {
		System.out.println("in B(),mVal=" + mVal);
		mVal = 2;
		System.out.println("in B(),mVal=" + mVal);
	}

	public B(int val) {
		this();
		System.out.println("in B(int ),mVal=" + mVal);
		mVal = val;
		System.out.println("in B(int ),mVal=" + mVal);
	}

	{
		System.out.println("in instance initialize: mVal=" + mVal);
		mVal = 3;
		System.out.println("mVal=" + mVal);
	}
}