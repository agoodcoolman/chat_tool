package com.pkmdz.chattool.service;

public interface onSucessReceiveLisener {
	void onSucessReceive(String result);
	
	void receiveVideoCall();
	
	void receiveRoster(String roster);
}
