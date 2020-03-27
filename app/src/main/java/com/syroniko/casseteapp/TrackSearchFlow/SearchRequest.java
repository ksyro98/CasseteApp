package com.syroniko.casseteapp.TrackSearchFlow;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class SearchRequest extends Request<JSONObject> {

//    private static final String AUTH_TOKEN = "BQACACN7c_WECJZo3iRBLgIRBUlex5VhozyyvBwxW3vzPUxa9ss2EGwnqfmAqf8RQV7Ws-jA3HpdwOZDKCNeR_pARQg9qKd3qh3SUTTmICFFtlqsxZbXGXxxKpCKP3rQSUM-jF27WlBdB27K0wS820_YHmDnpP0kd1xZvxCg8pv4eQpHDDFdLgO4b6qEIRQph_4G3pS3vVi2Zo1sFGilGm4YQ9meQM-yiiShsswPhOnaEqMwZLa20TPjlo3PvCFX7H4_RJFD7vwf1hWH2k1RMnv86OmV__4Pbek";

    private Response.Listener<JSONObject> listener;
    private String authToken = null;
//    private Map<String, String> params;

    public SearchRequest(int method, String url, String authToken,
//                         Map<String, String> params,
                         Response.Listener<JSONObject> listener,
                         @Nullable Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.authToken = authToken;
        this.listener = listener;
//        this.params = params;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> params = new HashMap<>();

        if (authToken == null){
            return null;
        }

        params.put("gcm_token", authToken);

        return params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();

        if(authToken == null){
            return null;
        }

        headers.put("Content-Type", "application/json");

        String bearer = "Bearer ".concat(authToken);

        headers.put("Authorization", bearer);

        return headers;
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

//    @Override
//    protected void deliverResponse(JSONObject response) {
//
//    }
}
