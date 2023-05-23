package com.eriklievaart.antastic.boot.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.assertj.core.api.Assertions;

import com.eriklievaart.antastic.ant.AntJob;
import com.eriklievaart.antastic.ant.AntJobBuilder;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.ListTool;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class CliExpectations {

	private AntJobBuilder builder;
	private List<ExpectJob> expectations = new ArrayList<>();

	public CliExpectations(AntJobBuilder builder) {
		this.builder = builder;
	}

	public void verify() {
		Map<String, AntJob> map = createCallMap();
		verifyInvocationsMatch(map.keySet());

		for (ExpectJob expectation : expectations) {
			Map<String, String> actualProperties = map.get(expectation.getKey()).getProperties();
			Assertions.assertThat(actualProperties.keySet()).containsExactlyElementsOf(expectation.properties.keySet());
			Assertions.assertThat(actualProperties).containsAllEntriesOf(expectation.properties);
		}
	}

	private Map<String, AntJob> createCallMap() {
		Map<String, AntJob> map = NewCollection.map();
		builder.getJobs().forEach(job -> {
			map.put(job.getProject().getName() + ":" + job.getTarget(), job);
		});
		return map;
	}

	private void verifyInvocationsMatch(Set<String> calls) {
		List<String> expect = ListTool.map(expectations, e -> e.jobName + ":" + e.target);
		Assertions.assertThat(calls).containsExactlyElementsOf(expect);
	}

	public ExpectJob expectJob(String job, String target) {
		ExpectJob expect = new ExpectJob(job, target);
		expectations.add(expect);
		return expect;
	}

	public static class ExpectJob {
		private String jobName;
		private String target;
		private Map<String, String> properties = NewCollection.map();

		public ExpectJob(String job, String target) {
			Check.noneNull(job, target);
			this.jobName = job;
			this.target = target;
		}

		public String getKey() {
			return jobName + ":" + target;
		}

		public void prop(String key, String value) {
			properties.put(key, value);
		}
	}
}
