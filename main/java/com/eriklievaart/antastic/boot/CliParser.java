package com.eriklievaart.antastic.boot;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.eriklievaart.toolkit.lang.api.check.Check;

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
			Check.matches(part, "[a-zA-Z0-9._-]++(=[a-zA-Z0-9._-]++)?", "invalid arg $", raw);
		}
	}

	public void ifIsJob(Consumer<CliJob> consumer) {
		Check.notNull(consumer);
		if (globalProperty) {
			return;
		}
		String[] parts = raw.split(":++");
		Check.isFalse(parts[0].contains("="), "invalid arg $ => expecting project name", parts[0]);

		CliJob job = new CliJob(parts[0]);
		for (int i = 1; i < parts.length; i++) {
			String part = parts[i];

			if (part.contains("=")) {
				String[] keyValue = part.split("=");
				job.put(keyValue[0], keyValue[1]);
			} else {
				job.addTarget(part);
			}
		}
		consumer.accept(job);
	}

	public void ifIsGlobal(BiConsumer<String, String> property) {
		if (!globalProperty) {
			return;
		}
		String[] keyValue = raw.split("=");
		property.accept(keyValue[0], keyValue[1]);
	}
}
