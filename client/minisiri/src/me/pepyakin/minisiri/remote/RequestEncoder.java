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
class RequestEncoder {
	
	private static final String REQUEST_MSG_NAME = "siri.ask";

	public String encode(SiriRequest req) {
		try {
			return encode0(req);
		} catch (JSONException e) {
			throw new RuntimeException("WTF-like exception", e);
		}
	}

	private String encode0(SiriRequest req) throws JSONException {
		JSONArray resultRequest = new JSONArray();
		JSONObject payload = encodePayload(req);

		resultRequest.put(REQUEST_MSG_NAME);
		resultRequest.put(req.getId());
		resultRequest.put(payload);
		
		return serializeInString(resultRequest);
	}

	private JSONObject encodePayload(SiriRequest req) throws JSONException {
		JSONObject payload = new JSONObject();
		payload.put("question", req.getQuestion());
		
		return payload;
	}
	
	private String serializeInString(JSONArray arr) {
		return arr.toString();
	}
	
}
