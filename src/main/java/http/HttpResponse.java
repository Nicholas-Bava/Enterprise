package http;

import java.util.HashMap;
import java.util.Map;

// Class to contain the details of an HTTP Response
public class HttpResponse {

    private int statusCode;
    private String statusMessage;
    private Map<String, String> headers;
    private String body;

    public HttpResponse(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.headers = new HashMap<>();
        this.body = "";

        headers.put("Content-Type", "text/html; charset=UTF-8");
        headers.put("Connection", "close");
    }

    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }

    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }

    public Map<String, String> getHeaders() { return headers; }
    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String toHttpString() {
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 ").append(statusCode).append(" ").append(statusMessage).append("\r\n");

        headers.put("Content-Length", String.valueOf(body.getBytes().length));

        for (Map.Entry<String, String> header : headers.entrySet()) {
            response.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }

        response.append("\r\n");
        response.append(body);

        return response.toString();
    }


}
