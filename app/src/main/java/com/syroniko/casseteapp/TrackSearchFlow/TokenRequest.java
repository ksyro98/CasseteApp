package com.syroniko.casseteapp.TrackSearchFlow;

import androidx.annotation.Nullable;
import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class TokenRequest extends Request<String> {


//    private static final String AUTH_TOKEN = "BQACACN7c_WECJZo3iRBLgIRBUlex5VhozyyvBwxW3vzPUxa9ss2EGwnqfmAqf8RQV7Ws-jA3HpdwOZDKCNeR_pARQg9qKd3qh3SUTTmICFFtlqsxZbXGXxxKpCKP3rQSUM-jF27WlBdB27K0wS820_YHmDnpP0kd1xZvxCg8pv4eQpHDDFdLgO4b6qEIRQph_4G3pS3vVi2Zo1sFGilGm4YQ9meQM-yiiShsswPhOnaEqMwZLa20TPjlo3PvCFX7H4_RJFD7vwf1hWH2k1RMnv86OmV__4Pbek";

    private Response.Listener<String> listener;
//    private String authToken = null;
//    private Map<String, String> params;
 
    public TokenRequest(int method, String url,
                         Response.Listener<String> listener,
                         @Nullable Response.ErrorListener errorListener) {
        super(method, url, errorListener);
//        this.authToken = authToken;
        this.listener = listener;
//        this.params = params;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> params = new HashMap<>();

        params.put("grant_type", "client_credentials");

        return params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();

        headers.put("Content-Type", "application/application/x-www-form-urlencoded  ");
//
//        String bearer = "Bearer ".concat(authToken);

        headers.put("Authorization", "Basic 846a7d470725449994155b664cb7959b:545521afc0984768b9e3e799c33dc3f7");

        return headers;
    }

    @Override
    protected void deliverResponse(String response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String string = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(string,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
//        } catch (JSONException je) {
//            return Response.error(new ParseError(je));
//        }
    }

//    @Override
//    protected void deliverResponse(JSONObject response) {
//
//    }



}
