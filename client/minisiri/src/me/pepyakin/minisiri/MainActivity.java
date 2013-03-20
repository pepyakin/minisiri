package me.pepyakin.minisiri;

import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;

import me.pepyakin.minisiri.ConnectivityListener.OnNetworkStateListener;
import me.pepyakin.minisiri.model.Question;
import me.pepyakin.minisiri.remote.SiriService;
import me.pepyakin.minisiri.remote.SiriService.SiriServiceCallbacks;
import android.app.Activity;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class MainActivity extends Activity implements OnNetworkStateListener,
		SiriServiceCallbacks, OnClickListener {

	private EditText questionText;
	private ListView messageList;
	private MessagesAdapter adapter;
	
	private Style infinityStyle;
	private SiriService service;
	private ConnectivityListener connectivityListener;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		resolveUi();
		serviceConnect();
		setupConnectivityListener();
		buildInfiniteStyle();
	}

	private void buildInfiniteStyle() {
		int pressable = getResources().getDimensionPixelSize(
				R.dimen.pressable_48dp);

		infinityStyle = new Style.Builder()
				.setBackgroundColorValue(Style.holoRedLight)
				.setHeight(pressable).setDuration(Style.DURATION_INFINITE)
				.build();
	}

	private void setupConnectivityListener() {
		connectivityListener = new ConnectivityListener();
		connectivityListener.setOnNetworkStateChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		unregisterReceiver(connectivityListener);
	}

	@Override
	protected void onResume() {
		super.onResume();

		registerReceiver(connectivityListener, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));
	}

	private void resolveUi() {
		Button sendButton = (Button) findViewById(R.id.send_button);
		sendButton.setOnClickListener(this);

		questionText = (EditText) findViewById(R.id.question_text);
		messageList = (ListView) findViewById(R.id.message_list);

		adapter = new MessagesAdapter(getLayoutInflater());
		messageList.setAdapter(adapter);
	}

	private void serviceConnect() {
		URI serverUri = specialMethodForWrappingDumbExceptions();

		service = new SiriService(serverUri);
		service.setCallbacks(this);
	}

	private URI specialMethodForWrappingDumbExceptions() {
		try {
			URI serverUri = new URI(AppConfig.SERVICE_ENDPOINT);
			return serverUri;
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private void onSendButtonClicked() {
		String questionString = questionText.getText().toString();
		if (questionString.length() == 0) {
			Crouton.showText(this, "Введите вопрос!", Style.ALERT);
			return;
		}
		
		int id = service.enqueQuestion(questionString);

		Question question = new Question(id, questionString);
		adapter.addQuestion(question);
		
		// Очистить текстовое поле.
		questionText.setText("");
	}

	@Override
	public void onNetworkStateChange(boolean networkAvailableNow) {
		if (networkAvailableNow) {
			Crouton.showText(this, "Соединение восстановлено", Style.INFO);
			service.connect();
		} else {
			Crouton.showText(this, "Соединение потеряно", Style.ALERT);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.send_button:
			onSendButtonClicked();
			break;
		}
	}

	@Override
	public void onResultReceived(int id, String answer) {
		Question question = adapter.byId(id);

		if (question != null) {
			question.setAnswer(answer);

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					adapter.notifyDataSetChanged();
				}
			});
		}
	}

	@Override
	public void onError(Exception exception) {
		try {
			// Импровезированный pattern-matching.
			throw exception;
		} catch (ConnectException e) {
			onConnectFailed();
		} catch (Exception e) {
			onGenericException(e.getMessage());
		}
	}

	private void onGenericException(String msg) {
		Crouton.showText(this, "Что-то пошло не так: " + msg, Style.ALERT);
	}

	private void onConnectFailed() {
		final Crouton crouton = Crouton.makeText(this,
				"Ошибка подключения. Попробовать снова?", infinityStyle);
		crouton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Crouton.hide(crouton);
				service.connect();
			}
		});
		crouton.show();
	}

}
