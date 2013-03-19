package me.pepyakin.minisiri;

import java.net.URI;
import java.net.URISyntaxException;

import me.pepyakin.minisiri.model.Question;
import me.pepyakin.minisiri.remote.SiriRequest;
import me.pepyakin.minisiri.remote.SiriResponse;
import me.pepyakin.minisiri.remote.SiriService;
import me.pepyakin.minisiri.remote.SiriService.SiriServiceCallbacks;
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
	
	private MessagesAdapter adapter;
	
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
        
        adapter = new MessagesAdapter(getLayoutInflater());
        messageList.setAdapter(adapter);
	}

	private void serviceConnect() {
		URI serverUri = specialMethodForWrappingDumbExceptions();
        
		service = new SiriService(serverUri);
		service.setCallbacks(listener);
		
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
		String question = questionText.getText().toString();
		int id = idFactory++;
		
		SiriRequest r = new SiriRequest();
		r.setId(id);
		r.setQuestion(question);
		service.send(r);
		
		Question q = new Question(question);
		q.setId(id);
		
		adapter.addQuestion(q);
	}
	
	private class Listener implements View.OnClickListener, SiriServiceCallbacks {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.send_button) {
				onSendButtonClicked();
			}
		}

		@Override
		public void onResultReceived(SiriResponse response) {
			Question q = adapter.byId(response.getId());
			
			q.setAnswer(response.getAnswer());
			
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					adapter.notifyDataSetChanged();
				}
			});
		}

		@Override
		public void onError() {
			// TODO Auto-generated method stub
			
		}
		
	}
}
