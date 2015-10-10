package cz.cvut.fel.adaptiverestfulapi.core;

/**
 * Makes Filter chaining easy and beautiful.
 *
 * @author klimesf
 */
public class FilterChainBuilder {

    private Filter first;

    private Filter last;

    /**
     * Adds Filter to the end of the chain.
     *
     * @param filter The Filter to be added as a last one.
     * @return This builder to provide fluent API.
     */
    public FilterChainBuilder chain(Filter filter) {

        if (first == null) {
            first = filter;
            last = filter;
            return this;
        }

        last.setNext(filter);
        last = filter;

        return this;
    }

    /**
     * Returns the Filter chain.
     *
     * @return First Filter in the chain.
     */
    public Filter build() {
        // TODO throw InvalidStateException if first == null?
        return first;
    }

}
