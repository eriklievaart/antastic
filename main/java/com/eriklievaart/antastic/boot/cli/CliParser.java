package com.eriklievaart.antastic.boot.cli;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.Box2;

public class CliParser {

	private String raw;
	private boolean globalProperty = false;

	public CliParser(String raw) {
		Check.notBlank(raw);
		this.raw = raw;
		validate();
	}

	private void validate() {
		if (raw.contains("=") && !raw.contains(":")) {
			globalProperty = true;
		}
		for (String part : raw.split(":")) {
			if (!part.matches("@[a-zA-Z-]++")) {
				Check.matches(part, "[a-zA-Z0-9._-]++(=[a-zA-Z0-9._-]++)?", "invalid arg $", raw);
			}
		}
	}

	public void ifIsJob(Consumer<CliJob> consumer) {
		Check.notNull(consumer);
		if (globalProperty) {
			return;
		}
		String[] parts = raw.split(":++");
		Check.isFalse(parts[0].contains("="), "invalid arg $ => expecting project name", parts[0]);

		CliJob job = new CliJob(parts[0].replaceFirst("^@", ""), parts[0].startsWith("@"));
		for (int i = 1; i < parts.length; i++) {
			String part = parts[i];

			if (part.contains("=")) {
				Box2<String, String> property = parseProperty(part);
				job.put(property.getKey(), property.getValue());
			} else {
				job.addTarget(part);
			}
		}
		consumer.accept(job);
	}

	public void ifIsProperty(BiConsumer<String, String> consumer) {
		if (!globalProperty) {
			return;
		}
		Box2<String, String> property = parseProperty(raw);
		consumer.accept(property.getKey(), property.getValue());
	}

	public static Box2<String, String> parseProperty(String raw) {
		String[] keyValue = raw.split("=");
		return new Box2<>(keyValue[0].trim(), keyValue[1].trim());
	}
}
