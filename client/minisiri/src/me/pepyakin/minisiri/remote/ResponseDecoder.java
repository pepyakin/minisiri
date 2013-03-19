/**
 * 
 */
package me.pepyakin.minisiri.remote;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author knott
 *
 */
public class ResponseDecoder {

	public SiriResponse decode(String response) {
		try {
			return decode0(response);
		} catch (JSONException e) {
			throw new RuntimeException("WTF-like exception", e);
		}
	}

	private SiriResponse decode0(String response) throws JSONException {
		JSONArray array = new JSONArray(response);
		
		int id = decodeId(array);
		String answer = decodeAnswer(array);
		
		return new SiriResponse(id, answer);
	}

	private String decodeAnswer(JSONArray array) throws JSONException {
		JSONObject payload = array.getJSONObject(2);
		String answer = payload.getString("answer");
		
		return answer;
	}
	
	private int decodeId(JSONArray array) throws JSONException {
		String idString = array.getString(1);
		int id = Integer.parseInt(idString);
		return id;
	}
}
