/*******************************************************************************
 * Copyright (c) 2015 Pivotal, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Pivotal, Inc. - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.eclipse.boot.dash.test.requestmappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.ide.eclipse.boot.dash.test.requestmappings.RequestMappingAsserts.assertRequestMappingWithPath;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.jdt.core.IType;
import org.junit.Test;
import org.springframework.ide.eclipse.boot.dash.model.requestmappings.RestActuatorClient;
import org.springframework.ide.eclipse.boot.dash.model.requestmappings.RequestMapping;
import org.springframework.ide.eclipse.boot.dash.model.requestmappings.TypeLookup;
import org.springsource.ide.eclipse.commons.frameworks.core.util.IOUtil;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class ActuatorClientTest {

	@Test public void testBasic() throws Exception {
		Client rest = restClientReturning(getContents("sample.json"));

		TypeLookup types = mock(TypeLookup.class);
		RestActuatorClient client = new RestActuatorClient(new URI("http://sample"), types, rest);

		List<RequestMapping> mappings = client.getRequestMappings();

		assertRequestMappingWithPath(mappings, "/error");
		assertRequestMappingWithPath(mappings, "/**/favicon.ico");
	}

	protected Client restClientReturning(String contents) throws Exception {
		Client rest = mock(Client.class);
		WebResource target = mock(WebResource.class);
		WebResource resource = mock(WebResource.class);
		when(rest.resource(new URI("http://sample"))).thenReturn(target);
		when(target.path("/mappings")).thenReturn(resource);
		when(resource.get(String.class)).thenReturn(contents);
		return rest;
	}

	protected Client restClientThrowing(Throwable exeption) {
		Client rest = mock(Client.class);
		WebResource target = mock(WebResource.class);
		WebResource resource = mock(WebResource.class);
		when(rest.resource("http://sample")).thenReturn(target);
		when(target.path("/mappings")).thenReturn(resource);
		when(resource.get(String.class)).thenThrow(exeption);
		return rest;
	}

	@Test public void testException() throws Exception {
		Client rest = restClientThrowing(new RuntimeException("Something went wrong!"));
		TypeLookup types = mock(TypeLookup.class);

		RestActuatorClient client = new RestActuatorClient(new URI("http://sample"), types, rest);
		assertNull(client.getRequestMappings());
	}

	@Test public void testUnparsableData() throws Exception {
		Client rest = restClientReturning("{This is not json,,,");
		TypeLookup types = mock(TypeLookup.class);
		RestActuatorClient client = new RestActuatorClient(new URI("http://sample"), types, rest);

		assertNull(client.getRequestMappings());
	}


	@Test public void testRequestMappingInfos() throws Exception {
		TypeLookup types = mock(TypeLookup.class);
		IType type = mock(IType.class);

		String json =
				"{" +
				"   \"{[/env],methods=[GET]}\":{\n" +
				"      \"bean\":\"endpointHandlerMapping\",\n" +
				"      \"method\":\"public java.lang.Object org.springframework.boot.actuate.endpoint.mvc.EndpointMvcAdapter.invoke()\"\n" +
				"   }\n"+
				"}";
		String fqTypeName = "org.springframework.boot.actuate.endpoint.mvc.EndpointMvcAdapter";
		String methodName = "invoke";

		Client rest = restClientReturning(json);
		RestActuatorClient client = new RestActuatorClient(new URI("http://sample"), types, rest);
		when(types.findType(fqTypeName)).thenReturn(type);

		RequestMapping rm = assertRequestMappingWithPath(client.getRequestMappings(), "/env");

		assertEquals(fqTypeName, rm.getFullyQualifiedClassName());
		assertEquals(methodName, rm.getMethodName());
		assertEquals(type, rm.getType());

		//Testing getMethod and isUserDefined requires mocking too much eclipse stuff to test it here.
		// These are tested in 'testRequestMappings' in BootDashModelTest
	}

	@Test public void testRequestMappingExpandOrPaths() throws Exception {
		TypeLookup types = mock(TypeLookup.class);
		IType type = mock(IType.class);

		String json =
				"{" +
				"   \"{[/env || /env.json],methods=[GET],produces=[application/json]}\":{\n" +
				"        \"bean\":\"endpointHandlerMapping\",\n" +
				"        \"method\":\"public java.lang.Object org.springframework.boot.actuate.endpoint.mvc.EndpointMvcAdapter.invoke()\"\n" +
				"     }\n" +
				"}";
		String fqTypeName = "org.springframework.boot.actuate.endpoint.mvc.EndpointMvcAdapter";
		String methodName = "invoke";

		Client rest = restClientReturning(json);
		RestActuatorClient client = new RestActuatorClient(new URI("http://sample"), types, rest);
		when(types.findType(fqTypeName)).thenReturn(type);

		List<RequestMapping> requestMappings = client.getRequestMappings();
		{
			RequestMapping rm = assertRequestMappingWithPath(requestMappings, "/env");
			assertEquals(fqTypeName, rm.getFullyQualifiedClassName());
			assertEquals(methodName, rm.getMethodName());
			assertEquals(type, rm.getType());
		}

		{
			RequestMapping rm = assertRequestMappingWithPath(requestMappings, "/env.json");
			assertEquals(fqTypeName, rm.getFullyQualifiedClassName());
			assertEquals(methodName, rm.getMethodName());
			assertEquals(type, rm.getType());
		}
	}


	//////////////////////////////////////////////////////////////////

	private String getContents(String resourcePath) throws Exception {
		InputStream input = this.getClass().getResourceAsStream(resourcePath);
		String s = IOUtil.toString(input);
		System.out.println(s);
		return s;
	}

}
