/*    Catroid: An on-device graphical programming language for Android devices
 *    Copyright (C) 2010  Catroid development team
 *    (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *    
 *    This file incorporates work covered by the following copyright and  
 *    permission notice: 
 *    
 *		   	Copyright 2010 Guenther Hoelzl, Shawn Brown
 *
 *		   	This file is part of MINDdroid.
 *
 * 		  	MINDdroid is free software: you can redistribute it and/or modify
 * 		  	it under the terms of the GNU General Public License as published by
 * 		  	the Free Software Foundation, either version 3 of the License, or
 *   		(at your option) any later version.
 *
 *   		MINDdroid is distributed in the hope that it will be useful,
 *   		but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   		MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   		GNU General Public License for more details.
 *
 *   		You should have received a copy of the GNU General Public License
 *   		along with MINDdroid.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.LegoNXT;

import java.io.IOException;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import at.tugraz.ist.catroid.bluetooth.BTConnectable;
import at.tugraz.ist.catroid.bluetooth.DeviceListActivity;

/**
 * @author une
 * 
 */
public class LegoNXT implements BTConnectable {

	private static final int REQUEST_CONNECT_DEVICE = 1000;

	private LegoNXTBtCommunicator myBTCommunicator;

	private boolean pairing;
	private static Handler btcHandler;
	private Handler recieverHandler;
	private Activity activity;
	@SuppressWarnings("unused")
	private static int GENERAL_COMMAND = 100;
	@SuppressWarnings("unused")
	private static int MOTOR_COMMAND = 102;
	private static int TONE_COMMAND = 101;

	public LegoNXT(Activity activity, Handler recieverHandler) {
		this.activity = activity;
		this.recieverHandler = recieverHandler;
	}

	public void startBTCommunicator(String mac_address) {

		if (myBTCommunicator != null) {
			try {
				myBTCommunicator.destroyNXTconnection();
			} catch (IOException e) {
			}
		}
		myBTCommunicator = new LegoNXTBtCommunicator(this, recieverHandler, BluetoothAdapter.getDefaultAdapter(),
				activity.getResources());
		btcHandler = myBTCommunicator.getHandler();

		myBTCommunicator.setMACAddress(mac_address);
		myBTCommunicator.start();
	}

	/**
	 * Receive messages from the BTCommunicator
	 */

	public void destroyBTCommunicator() {

		if (myBTCommunicator != null) {
			sendBTCMotorMessage(LegoNXTBtCommunicator.NO_DELAY, LegoNXTBtCommunicator.DISCONNECT, 0, 0);
			try {
				myBTCommunicator.destroyNXTconnection();
			} catch (IOException e) { // TODO Auto-generated method stub

				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			myBTCommunicator = null;
		}
	}

	public static synchronized void sendBTCPlayToneMessage(int frequency, int duration) {
		Bundle myBundle = new Bundle();
		myBundle.putInt("frequency", frequency);
		myBundle.putInt("duration", duration);

		Message myMessage = btcHandler.obtainMessage();
		myMessage.setData(myBundle);
		myMessage.what = TONE_COMMAND;

		btcHandler.sendMessage(myMessage);

	}

	public static synchronized void sendBTCMotorMessage(int delay, int motor, int speed, int angle) {
		Bundle myBundle = new Bundle();
		myBundle.putInt("motor", motor);
		myBundle.putInt("speed", speed);
		myBundle.putInt("angle", angle);
		Message myMessage = btcHandler.obtainMessage();
		myMessage.setData(myBundle);
		myMessage.what = motor;

		if (delay == 0) {

			btcHandler.removeMessages(motor);
			btcHandler.sendMessage(myMessage);

		} else {
			//btcHandler.removeMessages(motor);
			btcHandler.sendMessageDelayed(myMessage, delay);

		}
	}

	public static Handler getBTCHandler() {
		return btcHandler;
	}

	public boolean isPairing() {
		// TODO Auto-generated method stub
		return pairing;
	}

	public void connectLegoNXT() {
		Intent serverIntent = new Intent(this.activity, DeviceListActivity.class);
		activity.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);

	}
}