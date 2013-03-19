package me.pepyakin.minisiri;

import java.net.URI;
import java.net.URISyntaxException;

import me.pepyakin.minisiri.remote.SiriRequest;
import me.pepyakin.minisiri.remote.SiriService;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends Activity {
	
	private EditText questionText;
	private ListView messageList;
	
	private SiriService service;
	
	private Listener listener = new Listener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        resolveUi();
        serviceConnect();
    }

	private void resolveUi() {
		Button sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(listener);
        
        questionText = (EditText) findViewById(R.id.question_text);
        messageList = (ListView) findViewById(R.id.message_list);
	}

	private void serviceConnect() {
		URI serverUri = specialMethodForWrappingDumbExceptions();
        
		service = new SiriService(serverUri);
		service.connect();
	}

	private URI specialMethodForWrappingDumbExceptions() {
		try {
			URI serverUri = new URI(AppConfig.SERVICE_ENDPOINT);
			return serverUri;
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	int idFactory = 0;
	
	private void onSendButtonClicked() {
		SiriRequest r = new SiriRequest();
		r.setId(idFactory++);
		r.setQuestion(questionText.getText().toString());
		
		service.send(r);
	}
	
	private class Listener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.send_button) {
				onSendButtonClicked();
			}
		}
		
	}
}
