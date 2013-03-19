package me.pepyakin.minisiri;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

class ConnectivityListener extends BroadcastReceiver {

	private static final String ACTION_CONNECTIVITY = ConnectivityManager.CONNECTIVITY_ACTION;

	private boolean lastNetworkAvailable;

	private OnNetworkStateChangeListener onNetworkStateChangeListener;
	
	

	public void setOnNetworkStateChangeListener(
			OnNetworkStateChangeListener onNetworkStateChangeListener) {
		this.onNetworkStateChangeListener = onNetworkStateChangeListener;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (!isIntrestedIn(intent)) {
			return;
		}

		ConnectivityManager connectivityManager = getConnectivityManager(context);
		
		boolean networkAvailable = isNetworkAvailable(connectivityManager);

		if (networkAvailable != lastNetworkAvailable) {
			if (onNetworkStateChangeListener != null) {
				onNetworkStateChangeListener
						.onNetworkStateChange(networkAvailable);
			}

			lastNetworkAvailable = networkAvailable;
		}
	}

	private ConnectivityManager getConnectivityManager(Context context) {
		return (ConnectivityManager) 
				context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	private boolean isIntrestedIn(Intent intent) {
		return ACTION_CONNECTIVITY.equals(intent.getAction());
	}

	private boolean isNetworkAvailable(ConnectivityManager connectivityManager) {
		NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

		boolean networkAvailable = (activeNetwork != null)
				&& (activeNetwork.isConnected());
		return networkAvailable;
	}

	public interface OnNetworkStateChangeListener {

		/**
		 * Вызывается когда сеть привалилась или отвалилась.
		 * 
		 * @param isAvailable
		 */
		void onNetworkStateChange(boolean isAvailable);
	}
}