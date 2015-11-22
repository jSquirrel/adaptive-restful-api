package cz.cvut.fel.adaptiverestfulapi.core;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.MalformedURLException;

/**
 * @author klimesf
 */
public class HttpRouterTest {

    @Test
    public void testPathWithResourceAndIdentifier() {
        HttpRouter router = HttpRouter.createRouter("/project/123");
        Assert.assertEquals(router.getResource(), "project");
        Assert.assertEquals(router.getIdentifier(), "123");
    }

    @Test
    public void testPathWithResourceOnly() {
        HttpRouter router = HttpRouter.createRouter("/project");
        Assert.assertEquals(router.getResource(), "project");
        Assert.assertNull(router.getIdentifier());
    }

// TODO: add support for associations
//    @Test
//    public void testAssociations() {
//        // This fails because there is no support for associations
//        HttpRouter router = HttpRouter.createRouter("http://adaptiveapi.cz/project/123/issue");
//        Assert.assertEquals("issue", router.getResource());
//        Assert.assertNull(router.getIdentifier());
//    }

}
