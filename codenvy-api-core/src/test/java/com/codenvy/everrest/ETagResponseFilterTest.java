/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/

package com.codenvy.everrest;

import com.codenvy.api.core.rest.ApiExceptionMapper;

import org.everrest.core.impl.ApplicationContextImpl;
import org.everrest.core.impl.ApplicationProviderBinder;
import org.everrest.core.impl.ContainerRequest;
import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.impl.EverrestConfiguration;
import org.everrest.core.impl.EverrestProcessor;
import org.everrest.core.impl.ResourceBinderImpl;
import org.everrest.core.tools.DependencySupplierImpl;
import org.everrest.core.tools.ResourceLauncher;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.EntityTag;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.NOT_MODIFIED;
import static javax.ws.rs.core.Response.Status.OK;
import static org.testng.Assert.assertEquals;

/**
 * Test the ETag filter
 * @author Florent Benoit
 */
public class ETagResponseFilterTest {

    /**
     * Base URI
     */
    private static final String BASE_URI = "http://localhost/service";

    /**
     * Base Service
     */
    private static final String SERVICE_PATH = BASE_URI + "/myservice";


    /**
     * Dummy JAX-RS POJO
     */
    @Path("/myservice")
    public static class MyJaxRSService {

        @GET
        @Path("/list")
        @Produces(APPLICATION_JSON)
        public List<String> getMembers() {
            return Arrays.asList("a", "b", "c");
        }

        @GET
        @Path("/single")
        @Produces(APPLICATION_JSON)
        public String getMember() {
            return "hello";
        }

    }

    /**
     * Resource Launcher
     */
    private ResourceLauncher resourceLauncher;

    /**
     * Setup env for launching requests
     * @throws Exception
     */
    @BeforeMethod
    public void before() throws Exception {
        //set up launcher
        final ResourceBinderImpl resources = new ResourceBinderImpl();
        resources.addResource(MyJaxRSService.class, null);
        final DependencySupplierImpl dependencies = new DependencySupplierImpl();
        final ApplicationProviderBinder binder = new ApplicationProviderBinder();
        binder.addExceptionMapper(ApiExceptionMapper.class);
        binder.addResponseFilter(ETagResponseFilter.class);
        final URI uri = new URI(BASE_URI);
        final ContainerRequest req = new ContainerRequest(null, uri, uri, null, null, null);
        final ApplicationContextImpl contextImpl = new ApplicationContextImpl(req, null, binder);
        contextImpl.setDependencySupplier(dependencies);
        ApplicationContextImpl.setCurrent(contextImpl);
        final EverrestProcessor processor = new EverrestProcessor(resources,
                                                                  binder,
                                                                  dependencies,
                                                                  new EverrestConfiguration(),
                                                                  null);
        resourceLauncher = new ResourceLauncher(processor);
    }


    /**
     * Check if ETag is generated for a list of JSON
     */
    @Test
    public void filterListEntityTest() throws Exception {

        final ContainerResponse response = resourceLauncher.service("GET", SERVICE_PATH + "/list", BASE_URI, null, null, null);
        assertEquals(response.getStatus(), OK.getStatusCode());
        // check entity
        Assert.assertEquals(response.getEntity(), Arrays.asList("a", "b", "c"));
        // Check etag
        List<Object> headerTags = response.getHttpHeaders().get("ETag");
        Assert.assertNotNull(headerTags);
        Assert.assertEquals(headerTags.size(), 1);
        Assert.assertEquals(headerTags.get(0), new EntityTag("900150983cd24fb0d6963f7d28e17f72"));
    }

    /**
     * Check if ETag is generated for a simple entity of JSON
     */
    @Test
    public void filterSingleEntityTest() throws Exception {

        final ContainerResponse response = resourceLauncher.service("GET", SERVICE_PATH + "/single", BASE_URI, null, null, null);
        assertEquals(response.getStatus(), OK.getStatusCode());
        // check entity
        Assert.assertEquals(response.getEntity(), "hello");
        // Check etag
        List<Object> headerTags = response.getHttpHeaders().get("ETag");
        Assert.assertNotNull(headerTags);
        Assert.assertEquals(headerTags.size(), 1);
        Assert.assertEquals(headerTags.get(0), new EntityTag("fd186dd49a16b1bf2bd2f44e495e14c9"));
    }



    /**
     * Check if ETag sent with header is redirecting to NOT_MODIFIED
     */
    @Test
    public void filterListEntityTestWithEtag() throws Exception {

        Map<String, List<String>> headers = new HashMap<>();
        headers.put("If-None-Match", Collections.singletonList(new EntityTag("ce1473cf80c6b3fda8e3dfc006adc315").toString()));


        final ContainerResponse response = resourceLauncher.service("GET", SERVICE_PATH + "/list", BASE_URI, headers, null, null);
        assertEquals(response.getStatus(), NOT_MODIFIED.getStatusCode());
        // check null body
        Assert.assertNull(response.getEntity());
    }

    /**
     * Check if ETag sent with header is redirecting to NOT_MODIFIED
     */
    @Test
    public void filterSingleEntityTestWithEtag() throws Exception {

        Map<String, List<String>> headers = new HashMap<>();
        headers.put("If-None-Match", Collections.singletonList(new EntityTag("fd186dd49a16b1bf2bd2f44e495e14c9").toString()));


        final ContainerResponse response = resourceLauncher.service("GET", SERVICE_PATH + "/single", BASE_URI, headers, null, null);
        assertEquals(response.getStatus(), NOT_MODIFIED.getStatusCode());
        // check null body
        Assert.assertNull(response.getEntity());
    }

}
