/*
 * Copyright 2012-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.test.context.bootstrap;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.test.context.BootstrapContext;
import org.springframework.test.context.CacheAwareContextLoaderDelegate;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link SpringBootTestContextBootstrapper}.
 *
 * @author Andy Wilkinson
 */
class SpringBootTestContextBootstrapperTests {

	@Test
	void springBootTestWithANonMockWebEnvironmentAndWebAppConfigurationFailsFast() {
		assertThatIllegalStateException()
				.isThrownBy(() -> buildTestContext(SpringBootTestNonMockWebEnvironmentAndWebAppConfiguration.class))
				.withMessageContaining("@WebAppConfiguration should only be used with "
						+ "@SpringBootTest when @SpringBootTest is configured with a mock web "
						+ "environment. Please remove @WebAppConfiguration or reconfigure @SpringBootTest.");
	}

	@Test
	void springBootTestWithAMockWebEnvironmentCanBeUsedWithWebAppConfiguration() {
		buildTestContext(SpringBootTestMockWebEnvironmentAndWebAppConfiguration.class);
	}

	@Test
	void mergedContextConfigurationWhenArgsDifferentShouldNotBeConsideredEqual() {
		TestContext context = buildTestContext(SpringBootTestArgsConfiguration.class);
		Object contextConfiguration = ReflectionTestUtils.getField(context, "mergedContextConfiguration");
		TestContext otherContext2 = buildTestContext(SpringBootTestOtherArgsConfiguration.class);
		Object otherContextConfiguration = ReflectionTestUtils.getField(otherContext2, "mergedContextConfiguration");
		assertThat(contextConfiguration).isNotEqualTo(otherContextConfiguration);
	}

	@Test
	void mergedContextConfigurationWhenArgsSameShouldBeConsideredEqual() {
		TestContext context = buildTestContext(SpringBootTestArgsConfiguration.class);
		Object contextConfiguration = ReflectionTestUtils.getField(context, "mergedContextConfiguration");
		TestContext otherContext2 = buildTestContext(SpringBootTestSameArgsConfiguration.class);
		Object otherContextConfiguration = ReflectionTestUtils.getField(otherContext2, "mergedContextConfiguration");
		assertThat(contextConfiguration).isEqualTo(otherContextConfiguration);
	}

	@Test
	void mergedContextConfigurationWhenWebEnvironmentsDifferentShouldNotBeConsideredEqual() {
		TestContext context = buildTestContext(SpringBootTestMockWebEnvironmentConfiguration.class);
		Object contextConfiguration = ReflectionTestUtils.getField(context, "mergedContextConfiguration");
		TestContext otherContext = buildTestContext(SpringBootTestDefinedPortWebEnvironmentConfiguration.class);
		Object otherContextConfiguration = ReflectionTestUtils.getField(otherContext, "mergedContextConfiguration");
		assertThat(contextConfiguration).isNotEqualTo(otherContextConfiguration);
	}

	@Test
	void mergedContextConfigurationWhenWebEnvironmentsSameShouldtBeConsideredEqual() {
		TestContext context = buildTestContext(SpringBootTestMockWebEnvironmentConfiguration.class);
		Object contextConfiguration = ReflectionTestUtils.getField(context, "mergedContextConfiguration");
		TestContext otherContext = buildTestContext(SpringBootTestAnotherMockWebEnvironmentConfiguration.class);
		Object otherContextConfiguration = ReflectionTestUtils.getField(otherContext, "mergedContextConfiguration");
		assertThat(contextConfiguration).isEqualTo(otherContextConfiguration);
	}

	@SuppressWarnings("rawtypes")
	private TestContext buildTestContext(Class<?> testClass) {
		SpringBootTestContextBootstrapper bootstrapper = new SpringBootTestContextBootstrapper();
		BootstrapContext bootstrapContext = mock(BootstrapContext.class);
		bootstrapper.setBootstrapContext(bootstrapContext);
		given((Class) bootstrapContext.getTestClass()).willReturn(testClass);
		CacheAwareContextLoaderDelegate contextLoaderDelegate = mock(CacheAwareContextLoaderDelegate.class);
		given(bootstrapContext.getCacheAwareContextLoaderDelegate()).willReturn(contextLoaderDelegate);
		return bootstrapper.buildTestContext();
	}

	@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
	@WebAppConfiguration
	static class SpringBootTestNonMockWebEnvironmentAndWebAppConfiguration {

	}

	@SpringBootTest
	@WebAppConfiguration
	static class SpringBootTestMockWebEnvironmentAndWebAppConfiguration {

	}

	@SpringBootTest(args = "--app.test=same")
	static class SpringBootTestArgsConfiguration {

	}

	@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
	static class SpringBootTestMockWebEnvironmentConfiguration {

	}

	@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
	static class SpringBootTestAnotherMockWebEnvironmentConfiguration {

	}

	@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
	static class SpringBootTestDefinedPortWebEnvironmentConfiguration {

	}

	@SpringBootTest(args = "--app.test=same")
	static class SpringBootTestSameArgsConfiguration {

	}

	@SpringBootTest(args = "--app.test=different")
	static class SpringBootTestOtherArgsConfiguration {

	}

}
