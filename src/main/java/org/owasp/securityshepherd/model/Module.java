package org.owasp.securityshepherd.model;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder(builderClassName = "ModuleBuilder")
public class Module {

	@EqualsAndHashCode.Include
	@Id
	@Builder.Default
	@NonNull
	private final String id = RandomStringUtils.randomAlphanumeric(20);

	@Builder.Default
	@NonNull
	private String name = RandomStringUtils.randomAlphanumeric(20);

	static final int maxIdLength = 30;
	static final int maxNameLength = 70;

	public static ModuleBuilder builder() {
		return new ValidatingModuleBuilder();
	}

	// This class adds validation logic to the builder
	private static class ValidatingModuleBuilder extends ModuleBuilder {

		@Override
		public ModuleBuilder id(String id) {
			if (id == null) {
				id = RandomStringUtils.randomAlphanumeric(20);
			} else {
				validateId(id);
			}
			return super.id(id);
		}

		@Override
		public ModuleBuilder name(String name) {
			if (name == null) {
				name = RandomStringUtils.randomAlphanumeric(20);
			} else {
				validateName(name);
			}
			return super.name(name);
		}

	}

	public void setName(String name) {
		validateName(name);

		this.name = name;
	}

	public static boolean validateId(String id) {
		if (id == null) {
			throw new IllegalArgumentException("Module id cannot be null");
		} else if (id.isEmpty()) {
			throw new IllegalArgumentException("Module id cannot be an empty string");
		} else if (id.length() > maxIdLength) {
			throw new IllegalArgumentException("Module id cannot be longer than " + Integer.toString(maxIdLength));
		}

		return true;
	}

	public static boolean validateName(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Module name cannot be null");
		} else if (name.isEmpty()) {
			throw new IllegalArgumentException("Module name cannot be an empty string");
		} else if (name.length() > maxNameLength) {
			throw new IllegalArgumentException("Module name cannot be longer than " + Integer.toString(maxNameLength));
		}

		return true;
	}

}