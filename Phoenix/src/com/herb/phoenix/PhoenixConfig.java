package com.herb.phoenix;

/*
 * Interface containing functions for interacting with the filesystem and the console.
 * All Phoenix game classes should implement this interface.
 */
public interface PhoenixConfig
{
	//Prints a message out to console or logcat.
	public abstract void log(String s);
	
	//Quits game.
	public abstract void quit();
}
