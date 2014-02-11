
package cz.cvut.fel.adaptiverestfulapi.core;

import java.util.Map;


/**
 * Class that holds both HTTP request and response.
 */
public class HttpContext {

    // request
    private final String uri;
    private final HttpMethod method;
    private final HttpHeaders requestHeaders;
    private String requestContent;

    // response
    private HttpStatus status;
    private HttpHeaders responseHeaders;
    private String responseContent;

    public HttpContext(String uri, HttpMethod method, HttpHeaders headers, String content) {
        this.uri = uri;
        this.method = method;
        this.requestHeaders = headers;
        this.requestContent = content;
    }

    public String getUri() {
        return this.uri;
    }

    public HttpMethod getMethod() {
        return this.method;
    }

    public Map<String, String> getRequestHeaders() {
        return this.requestHeaders;
    }

    public String getRequestContent() {
        return this.requestContent;
    }

    public void response(HttpStatus status, HttpHeaders headers, String content) {
        this.status = status;
        this.responseHeaders = headers;
        this.responseContent = content;
    }

    public HttpStatus getStatus() {
        return this.status;
    }

    public Map<String, String> getResponseHeaders() {
        return this.responseHeaders;
    }

    public String getResponseContent() {
        return this.responseContent;
    }

}
