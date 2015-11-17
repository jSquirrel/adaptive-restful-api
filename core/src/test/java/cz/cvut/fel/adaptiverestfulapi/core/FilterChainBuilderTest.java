package cz.cvut.fel.adaptiverestfulapi.core;

import cz.cvut.fel.adaptiverestfulapi.meta.configuration.Configuration;
import cz.cvut.fel.adaptiverestfulapi.meta.model.Model;
import org.testng.annotations.Test;

import java.lang.reflect.Field;

/**
 * @author klimesf
 */
public class FilterChainBuilderTest {

    @Test
    public void testBuildEmpty() {
        FilterChainBuilder builder = new FilterChainBuilder();
        assert (builder.build() == null) : "Calling build() on FilterChainBuilder should return null.";
    }

    @Test
    public void testBuildValid() throws NoSuchFieldException, IllegalAccessException {
        Filter filter1 = new Filter() {
            @Override
            public HttpContext process(HttpContext context, Model model, Configuration configuration) throws FilterException {
                return null;
            }
        };
        Filter filter2 = new Filter() {
            @Override
            public HttpContext process(HttpContext context, Model model, Configuration configuration) throws FilterException {
                return null;
            }
        };
        Filter filter3 = new Filter() {
            @Override
            public HttpContext process(HttpContext context, Model model, Configuration configuration) throws FilterException {
                return null;
            }
        };

        Field nextField = Filter.class.getDeclaredField("next");
        nextField.setAccessible(true);

        FilterChainBuilder builder = new FilterChainBuilder();
        Filter chain = builder
                .chain(filter1)
                .chain(filter2)
                .chain(filter3)
                .build();
        assert (chain == filter1) : "The first Filter in the chain should be the first one added.";

        Filter filter2chained = (Filter) nextField.get(chain);
        assert (filter2chained == filter2) : "The second Filter in the chain should be the second one added.";

        Filter filter3chained = (Filter) nextField.get(filter2chained);
        assert (filter3chained == filter3) : "The third Filter in the chain should be the third one added.";
    }

}
