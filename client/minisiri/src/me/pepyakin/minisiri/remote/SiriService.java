/**
 * 
 */
package me.pepyakin.minisiri.remote;

import java.net.URI;

import android.util.Log;
import android.util.SparseArray;

import com.codebutler.android_websockets.WebSocketClient;

/**
 * Класс занимается отсылкой данных к бекенду и приемов результатов обработки от
 * него.
 * 
 * @author knott
 */
public class SiriService {

	private SparseArray<SiriRequest> pendingRequests;

	private RequestEncoder encoder;
	private ResponseDecoder decoder;

	private WebSocketClient wsClient;
	private SiriServiceCallbacks callbacks;
	
	private URI serverUri;
	
	public SiriService(URI serverUri) {
		pendingRequests = new SparseArray<SiriRequest>();
		
		encoder = new RequestEncoder();
		decoder = new ResponseDecoder();
		
		this.serverUri = serverUri;
	}
	
	public void connect() {
		wsClient = new WebSocketClient(serverUri, new Handler(), null);
		wsClient.connect();
		
		for (int i = 0; i < pendingRequests.size(); i++) {
			SiriRequest r = pendingRequests.valueAt(i);
			send(r);
		}
	}

	/**
	 * Метод для постановки запроса в очередь на отправку.
	 * 
	 * @param request Сообщение для постановки в очередь.
	 */
	public void enqueRequest(SiriRequest request) {
		pendingRequests.put(request.getId(), request);
		send(request);
	}

	private void send(SiriRequest request) {
		String encodedRequest = encoder.encode(request);
		wsClient.send(encodedRequest);
	}

	void handleIncomingMessage(String messageData) {
		SiriResponse response = decoder.decode(messageData);

		markAsCompleted(response.getId());

		if (callbacks != null) {
			callbacks.onResultReceived(response);
		}
	}

	private void markAsCompleted(int id) {
		pendingRequests.delete(id);
	}
	
	public void setCallbacks(SiriServiceCallbacks callbacks) {
		this.callbacks = callbacks;
	}

	public interface SiriServiceCallbacks {
		void onResultReceived(SiriResponse response);
		void onError();
	}

	/**
	 * Класс который реагирует на события посланные {@link WebSocketClient
	 * WebSocket клиентом}, делегируя родительскому {@link SiriService}.
	 * 
	 * @author knott
	 */
	class Handler implements WebSocketClient.Listener {

	    private static final String TAG = "Handler";

		@Override
	    public void onConnect() {
	        Log.d(TAG, "Connected!");
	    }

	    @Override
	    public void onMessage(String message) {
	        handleIncomingMessage(message);
	    }

	    @Override
	    public void onMessage(byte[] data) {
	        Log.d(TAG, String.format("Got binary message! %s", new String(data)));
	    }

	    @Override
	    public void onDisconnect(int code, String reason) {
	        Log.d(TAG, String.format("Disconnected! Code: %d Reason: %s", code, reason));
	    }

	    @Override
	    public void onError(Exception error) {
	        Log.e(TAG, "Error!", error);
	    }
	}

}
