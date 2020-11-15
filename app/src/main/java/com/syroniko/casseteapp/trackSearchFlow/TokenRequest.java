package com.syroniko.casseteapp.trackSearchFlow;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class TokenRequest extends Request<String> {

    private Response.Listener<String> listener;

    public TokenRequest(
            int method,
            String url,
            Response.Listener<String> listener,
            @Nullable Response.ErrorListener errorListener
    ) {
        super(method, url, errorListener);
        this.listener = listener;
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
    }

}
