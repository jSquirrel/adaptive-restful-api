package cz.cvut.fel.adaptiverestfulapi.data;

import cz.cvut.fel.adaptiverestfulapi.core.HttpContext;
import cz.cvut.fel.adaptiverestfulapi.core.Filter;
import cz.cvut.fel.adaptiverestfulapi.core.FilterException;


public abstract class DataHandler extends Filter {

    @Override
    public void process(HttpContext httpContext) throws FilterException {
        this.resign(httpContext);
    }

    // TODO define abstract CRUD operations

}
