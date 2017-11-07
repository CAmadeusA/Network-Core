package com.camadeusa.module.game;

import java.util.LinkedList;

import com.camadeusa.module.Module;

public class OrionGame extends Module{
	LinkedList<OrionSegment> ll;

	public OrionGame() {
		ll = new LinkedList<OrionSegment>();
	}
	
	public <T extends OrionSegment> void addSegment(T ...segment) {
		for (T seg : segment) {
			ll.add(seg);
		}
	}

	public LinkedList<OrionSegment> getSegments() {
		return ll;
	}
}
