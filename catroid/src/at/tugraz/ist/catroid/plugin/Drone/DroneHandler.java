/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.plugin.Drone;

import android.content.Context;
import android.util.Log;

public class DroneHandler {

	private static DroneHandler droneHandler;
	private DroneLibraryWrapper droneLibraryWrapper;
	private boolean wasAlreadyConnected;

	public static DroneHandler getInstance() {
		if (droneHandler == null) {
			droneHandler = new DroneHandler();
		}
		return droneHandler;
	}

	private DroneHandler() {
	}

	public boolean createDroneFrameworkWrapper(Context context) {

		try {
			droneLibraryWrapper = new DroneLibraryWrapper(context);
		} catch (Exception e) {
			Log.e(DroneConsts.DroneLogTag, "could not create DroneLibraryWrapper. Drone plugin not installed.", e);
			droneLibraryWrapper = null;
			return false;
		}

		return true;
	}

	public DroneLibraryWrapper getDrone() {
		return droneLibraryWrapper;
	}

	public boolean wasAlreadyConnected() {
		if (DroneHandler.getInstance().getDrone().getFlyingMode() == -1) {
			return false;
		}
		return wasAlreadyConnected;
	}

	public void setWasAlreadyConnected() {
		wasAlreadyConnected = true;
	}
}
