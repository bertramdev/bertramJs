package com.bertramlabs.js.internals.interfaces;


public interface JSObject {
	JSObject getPrototype();

	String getClassName();

	String setClassName(String className);



}
