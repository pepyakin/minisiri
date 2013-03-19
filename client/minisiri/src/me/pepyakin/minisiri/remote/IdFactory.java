/**
 * 
 */
package me.pepyakin.minisiri.remote;

/**
 * @author knott
 *
 */
class IdFactory {

	
	private int currentId = 0;
	
	public int nextId() {
		return currentId++;
	}
}
