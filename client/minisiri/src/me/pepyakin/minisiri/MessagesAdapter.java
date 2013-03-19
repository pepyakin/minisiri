/**
 * 
 */
package me.pepyakin.minisiri;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import me.pepyakin.minisiri.model.Question;

/**
 * @author knott
 *
 */
public class MessagesAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<Question> questions;

	public MessagesAdapter(LayoutInflater inflater) {
		this.inflater = inflater;
		
		questions = new ArrayList<Question>();
	}

	@Override
	public int getCount() {
		return questions.size();
	}

	@Override
	public Question getItem(int position) {
		return questions.get(position);
	}

	@Override
	public long getItemId(int position) {
		Question q = getItem(position);
		return q.getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 // TODO: сделать ок.
		View v = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
		
		Question q = getItem(position);
		
		TextView text1 = (TextView) v.findViewById(android.R.id.text1);
		TextView text2 = (TextView) v.findViewById(android.R.id.text2);
		
		text1.setText(q.getQuestion());
		
		if (!q.isPending()) {
			String fullAnswer = q.getAnswer();
			String answer = stripAnswer(fullAnswer);
			
			text2.setText(answer);
		}
		
		return v;
	}

	private String stripAnswer(String fullAnswer) {
		final int MAX_CHARS = 40;
		
		String answer;
		
		if (fullAnswer.length() > MAX_CHARS) {
			answer = fullAnswer.substring(0, MAX_CHARS - 3) + "...";
		} else {
			answer = fullAnswer;
		}
		
		return answer;
	}
	
	public void addQuestion(Question q) {
		questions.add(q);
		notifyDataSetChanged();
	}
	
	public Question byId(int id) {
		for (Question q : questions) {
			if (q.getId() == id) {
				return q;
			}
		}
		
		return null;
	}
}
