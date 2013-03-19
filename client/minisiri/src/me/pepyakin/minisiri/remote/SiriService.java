/**
 * 
 */
package me.pepyakin.minisiri.remote;

import java.net.URI;
import java.util.concurrent.ArrayBlockingQueue;

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
	private ArrayBlockingQueue<SiriRequest> requestQueue;

	private RequestEncoder encoder;
	private ResponseDecoder decoder;

	private WebSocketClient wsClient;
	private SiriServiceCallbacks callbacks;
	
	private Thread pollerThread;
	
	private URI serverUri;
	
	private IdFactory idFactory;
	
	private boolean inErrorState = false;
	
	public SiriService(URI serverUri) {
		pendingRequests = new SparseArray<SiriRequest>();
		requestQueue = new ArrayBlockingQueue<SiriRequest>(10);
		
		encoder = new RequestEncoder();
		decoder = new ResponseDecoder();
		
		this.serverUri = serverUri;
	}
	
	public void connect() {
		inErrorState = false;
		requestQueue.clear();
		
		wsClient = new WebSocketClient(serverUri, new Handler(), null);
		wsClient.connect();
		
		for (int i = 0; i < pendingRequests.size(); i++) {
			SiriRequest r = pendingRequests.valueAt(i);
			
			try {
				requestQueue.put(r);
			} catch (InterruptedException e) {
				// TODO: swallow for now
			}
		}
	}
	
	public int enqueQuestion(String question) {
		int id = idFactory.nextId();
		
		SiriRequest request = new SiriRequest();
		request.setId(id);
		request.setQuestion(question);
		
		return id;
	}

	/**
	 * Метод для постановки запроса в очередь на отправку.
	 * 
	 * @param request Сообщение для постановки в очередь.
	 */
	public void enqueRequest(SiriRequest request) {
		pendingRequests.put(request.getId(), request);
		
		try {
			requestQueue.put(request);
		} catch (InterruptedException e) {
			return;
		}
	}

	private void send(SiriRequest request) {
		String encodedRequest = encoder.encode(request);
		
		if (!inErrorState) {
			wsClient.send(encodedRequest);
		}
	}

	void handleIncomingMessage(String messageData) {
		SiriResponse response = decoder.decode(messageData);

		markAsCompleted(response.getId());

		if (callbacks != null) {
			callbacks.onResultReceived(response);
		}
	}
	
	public void handleError(Exception exception) {
		inErrorState = true;
		
		if (callbacks != null) {
			callbacks.onError(exception);
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
		void onError(Exception exception);
	}
	
	class Poller implements Runnable {

		private static final String TAG = "Poller";

		@Override
		public void run() {
			Thread currentThread = Thread.currentThread();
			
			while (!currentThread.isInterrupted()) {
				try {
					SiriRequest r = requestQueue.take();
					if (currentThread.isInterrupted()) {
						Log.d(TAG, "currentThread " + currentThread + " interrupted");
						break;
					}
					
					send(r);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
		
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
	        
	        pollerThread = new Thread(new Poller());
	        pollerThread.start();
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
	        
	        if (pollerThread != null) {
	        	pollerThread.interrupt();
	        	pollerThread = null;
	        }
	        
	        handleError(error);
	    }
	}



}
