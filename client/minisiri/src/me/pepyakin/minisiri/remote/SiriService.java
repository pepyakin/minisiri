/**
 * 
 */
package me.pepyakin.minisiri.remote;

import android.util.SparseArray;

import com.codebutler.android_websockets.WebSocketClient;

/**
 * Класс занимается отсылкой данных к бекенду и приемов результатов обработки от него. 
 * 
 * @author knott
 */
public class SiriService {
	
	private SparseArray<SiriRequest> pendingRequests;
	
	private RequestEncoder encoder;
	private ResponseDecoder decoder;
	
	private WebSocketClient wsClient;
	private OnResultListener onResultListener;
	
	public void send(SiriRequest request) {
		pendingRequests.put(request.getId(), request);
		
		String encodedRequest = encoder.encode(request);
		wsClient.send(encodedRequest);
	}
	
	void onResultReceived() {
		
	}
	
	public interface OnResultListener {
		void onResultReceived(SiriRequest initRequest);
	}
	
	/**
	 * Класс который реагирует на события посланные WebSocket клиентом.
	 * 
	 * @author knott
	 */
	class Handler implements WebSocketClient.Listener {

		@Override
		public void onConnect() {
		}

		@Override
		public void onDisconnect(int errorCode, String reason) {
		}

		@Override
		public void onError(Exception throwable) {
		}

		@Override
		public void onMessage(String msg) {
		}

		@Override
		public void onMessage(byte[] msg) {
			
		}
	}

}

