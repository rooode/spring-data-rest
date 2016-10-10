/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.rest.webmvc;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.webmvc.RepositoryRestHandlerMapping.CorsConfigurationAccessor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.cors.CorsConfiguration;

/**
 * Unit tests for {@link CorsConfigurationAccessor}.
 * 
 * @author Mark Paluch
 * @soundtrack Aso Mamiko - Drive Me Crazy (Club Mix)
 */
@RunWith(MockitoJUnitRunner.class)
public class CorsConfigurationAccessorUnitTests {

	private CorsConfigurationAccessor accessor;

	@Mock private ResourceMappings mappings;
	@Mock private Repositories repositories;

	@Before
	public void before() throws Exception {
		accessor = new CorsConfigurationAccessor(mappings, repositories, null);
	}

	/**
	 * @see DATAREST-573
	 */
	@Test
	public void hasConfigurationShouldDiscoverCrossOriginAnnotation() {

		assertThat(accessor.hasConfiguration(PlainRepository.class), is(false));
		assertThat(accessor.hasConfiguration(AnnotatedRepository.class), is(true));
	}

	/**
	 * @see DATAREST-573
	 */
	@Test
	public void createConfigurationShouldConstructCorsConfiguration() {

		CorsConfiguration configuration = accessor.createConfiguration(AnnotatedRepository.class);

		assertThat(configuration, is(notNullValue()));
		assertThat(configuration.getAllowCredentials(), is(true));
		assertThat(configuration.getAllowedHeaders(), hasItem("*"));
		assertThat(configuration.getAllowedOrigins(), hasItem("*"));
		assertThat(configuration.getMaxAge(), is(1800L));
	}

	/**
	 * @see DATAREST-573
	 */
	@Test
	public void createConfigurationShouldConstructFullCorsConfiguration() {

		CorsConfiguration configuration = accessor.createConfiguration(FullyConfiguredCorsRepository.class);

		assertThat(configuration, is(notNullValue()));
		assertThat(configuration.getAllowCredentials(), is(true));
		assertThat(configuration.getAllowedHeaders(), hasItem("Content-type"));
		assertThat(configuration.getExposedHeaders(), hasItem("Accept"));
		assertThat(configuration.getAllowedOrigins(), hasItem("http://far.far.away"));
		assertThat(configuration.getAllowedMethods(), hasItem("PATCH"));
		assertThat(configuration.getAllowCredentials(), is(true));
		assertThat(configuration.getMaxAge(), is(1234L));
	}

	interface PlainRepository {}

	@CrossOrigin
	interface AnnotatedRepository {}

	@CrossOrigin(origins = "http://far.far.away", allowedHeaders = "Content-type", maxAge = 1234,
			exposedHeaders = "Accept", methods = RequestMethod.PATCH, allowCredentials = "true")
	interface FullyConfiguredCorsRepository {}
}
