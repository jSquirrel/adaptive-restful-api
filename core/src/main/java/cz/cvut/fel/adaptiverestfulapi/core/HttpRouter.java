
package cz.cvut.fel.adaptiverestfulapi.core;

import java.lang.reflect.Type;


public class HttpRouter {

    private String host;
    private int port;

    private String resource;
    private String identifier;

    public static HttpRouter createRouter(String path) {
        return new HttpRouter(path);
    }

    private HttpRouter(String path) {
        this.resource = this.resource(path);
        this.identifier = this.identifier(path);
    }

    protected String resource(String path) {
        String[] parts = this.pathParts(path);
        if (parts.length > 0) {
            return parts[0];
        }
        return null;
    }

    protected String identifier(String path) {
        String[] parts = this.pathParts(path);
        if (parts.length == 2) {
            return parts[1];
        }
        return null;
    }

    private String[] pathParts(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path.split("/");
    }

    public String getResource() {
        return this.resource;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public Object getIdentifier(Type type) {
        if (this.identifier == null || this.identifier.isEmpty()) {
            return null;
        }

        // TODO add support for primitive types

        if (type.equals(Integer.class)) {
            return Integer.valueOf(this.identifier);

        } else if (type.equals(Long.class)) {
            return Long.valueOf(this.identifier);

        } else {
            // String class or default
            return this.identifier;
        }
    }

}
