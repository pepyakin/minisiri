/**
 * 
 */
package me.pepyakin.minisiri.model;

/**
 * Модель запроса. Имеет строку содержащую сам вопрос, его ID и опционально имеет ответ.
 * 
 * @author knott
 */
public class Question {
	
	private int id;
	
	private String question;
	private String answer;
	
	public Question(String question) {
		this.question = question;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public boolean isPending() {
		return answer == null;
	}
}
