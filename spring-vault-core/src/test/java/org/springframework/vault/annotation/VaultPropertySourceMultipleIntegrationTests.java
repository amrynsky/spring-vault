/*
 * Copyright 2016-2020 the original author or authors.
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
package org.springframework.vault.annotation;

import java.util.Collections;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.vault.core.VaultIntegrationTestConfiguration;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.util.VaultExtension;
import org.springframework.vault.util.VaultInitializer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for {@link VaultPropertySource} using multiple annotations.
 *
 * @author Mark Paluch
 */
@ExtendWith(SpringExtension.class)
@ExtendWith(VaultExtension.class)
@ContextConfiguration
class VaultPropertySourceMultipleIntegrationTests {

	@VaultPropertySource(value = "secret/myapp/profile", propertyNamePrefix = "database.")
	@VaultPropertySource("secret/myapp")
	static class Config extends VaultIntegrationTestConfiguration {

	}

	@Autowired
	Environment env;

	@Autowired
	ApplicationContext context;

	@Value("${myapp}")
	String myapp;

	@BeforeAll
	static void beforeClass(VaultInitializer initializer) {

		VaultOperations vaultOperations = initializer.prepare().getVaultOperations();

		vaultOperations.write("secret/myapp", Collections.singletonMap("myapp", "myvalue"));
		vaultOperations.write("secret/myapp/profile", Collections.singletonMap("myprofile", "myprofilevalue"));
	}

	@Test
	void environmentShouldResolveProperties() {

		assertThat(this.env.getProperty("myapp")).isEqualTo("myvalue");
		assertThat(this.env.getProperty("database.myprofile")).isEqualTo("myprofilevalue");
	}

	@Test
	void valueShouldInjectProperty() {
		assertThat(this.myapp).isEqualTo("myvalue");
	}

}
