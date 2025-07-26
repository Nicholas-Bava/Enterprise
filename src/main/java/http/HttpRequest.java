package http;

import java.util.HashMap;
import java.util.Map;


// Class to contain the details of an HTTP Request
public class HttpRequest {
    private String method;
    private String path;
    private String body;
    private Map<String, String> headers;
    private Map<String, String> queryParams;

    public HttpRequest(String method, String path) {
        this.method = method;
        this.path = path;
        this.body = "";
        this.headers = new HashMap<>();
        this.queryParams = new HashMap<>();
    }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public Map<String, String> getHeaders() { return headers; }
    public void addHeader(String key, String value) {
        headers.put(key.toLowerCase(), value);
    }

    public Map<String, String> getQueryParams() { return queryParams; }
    public void addQueryParam(String key, String value) {
        queryParams.put(key, value);
    }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }


}
