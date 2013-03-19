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
		View view;
		ViewHolder holder;

		if (convertView == null) {
			view = inflater.inflate(android.R.layout.simple_list_item_2,
					parent, false);

			holder = new ViewHolder();
			holder.text1 = (TextView) view.findViewById(android.R.id.text1);
			holder.text2 = (TextView) view.findViewById(android.R.id.text2);

			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}

		Question q = getItem(position);

		String firstLine = q.getQuestion();
		String secondLine = getSecondLine(q);

		holder.text1.setText(firstLine);
		holder.text2.setText(secondLine);

		return view;
	}

	private String getSecondLine(Question q) {
		if (!q.isPending()) {
			String fullAnswer = q.getAnswer();
			String answer = stripAnswer(fullAnswer);

			return answer;
		} else {
			return "...";
		}
	}

	private String stripAnswer(String fullAnswer) {
		final int MAX_CHARS = 40;

		if (fullAnswer.length() > MAX_CHARS) {
			return fullAnswer.substring(0, MAX_CHARS - 3) + "...";
		} else {
			return fullAnswer;
		}
	}

	public void addQuestion(Question q) {
		final int FIRST = 0;
		
		questions.add(FIRST, q);
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

	private class ViewHolder {
		public TextView text1;
		public TextView text2;
	}
}
