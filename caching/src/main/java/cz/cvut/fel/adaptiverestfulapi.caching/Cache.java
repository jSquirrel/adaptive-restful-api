package cz.cvut.fel.adaptiverestfulapi.caching;

import cz.cvut.fel.adaptiverestfulapi.core.HttpContext;
import cz.cvut.fel.adaptiverestfulapi.core.Filter;
import cz.cvut.fel.adaptiverestfulapi.core.FilterException;


/**
 * Abstract class for cache support.
 */
public abstract class Cache extends Filter {

    @Override
    public void process(HttpContext httpContext) throws FilterException {
        if (!this.load(httpContext)) { // try load data from cache
            this.resign(httpContext);  // ask for data someone else
            this.save(httpContext);    // finally save new data to cache
        }
    }

    /**
     * Load data from the cache.
     * @param httpContext
     * @return true if hit was made
     */
    protected abstract boolean load(HttpContext httpContext);

    /**
     * Save data to the cache.
     * @param httpContext
     */
    protected abstract void save(HttpContext httpContext);

}
