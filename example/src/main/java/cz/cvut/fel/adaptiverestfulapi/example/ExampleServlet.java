
package cz.cvut.fel.adaptiverestfulapi.example;

import cz.cvut.fel.adaptiverestfulapi.caching.IfModifiedSinceCache;
import cz.cvut.fel.adaptiverestfulapi.core.Filter;
import cz.cvut.fel.adaptiverestfulapi.core.FilterChainBuilder;
import cz.cvut.fel.adaptiverestfulapi.data.Dispatcher;
import cz.cvut.fel.adaptiverestfulapi.example.security.SimpleAuthentication;
import cz.cvut.fel.adaptiverestfulapi.example.security.Users;
import cz.cvut.fel.adaptiverestfulapi.serialization.Resolver;
import cz.cvut.fel.adaptiverestfulapi.servlet.FilteredServlet;


public class ExampleServlet extends FilteredServlet {

    public ExampleServlet() {
        this.filter = Filter.createChainBuilder()
                .chain(Users.getInstance().getAuthentication())
                .chain(Users.getInstance().getMethodAuthorization())
                .chain(new IfModifiedSinceCache())
                .chain(new Resolver())
                .chain(new Dispatcher())
                .build();
    }

}
