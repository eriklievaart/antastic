package com.eriklievaart.antastic.boot.cli;

import org.junit.Test;

public class JobParserI {

	@Test
	public void createAntJobsProjectMissingTarget() {
		JobParserFixture fixture = new JobParserFixture();
		fixture.metadata.setConfiguredArgs("pikachu", "shock");
		fixture.createAntJobs("pikachu");
		fixture.expectations.expectJob("pikachu", "shock");
		fixture.expectations.verify();
	}

	@Test
	public void createAntJobsProjectAndTarget() {
		JobParserFixture fixture = new JobParserFixture();
		fixture.createAntJobs("pikachu:shock");
		fixture.expectations.expectJob("pikachu", "shock");
		fixture.expectations.verify();
	}

	@Test
	public void createAntJobsProjectMissingTargetHasProperty() {
		JobParserFixture fixture = new JobParserFixture();
		fixture.metadata.setConfiguredArgs("pikachu", "target");
		fixture.createAntJobs("pikachu:shocked=true");
		fixture.expectations.expectJob("pikachu", "target").prop("shocked", "true");
		fixture.expectations.verify();
	}

	@Test
	public void createAntJobsProjectTargetAndProperty() {
		JobParserFixture fixture = new JobParserFixture();
		fixture.createAntJobs("pikachu:shock:charged=true");
		fixture.expectations.expectJob("pikachu", "shock").prop("charged", "true");
		fixture.expectations.verify();
	}

	@Test
	public void createAntJobsProjectMissingTargetGlobalBefore() {
		JobParserFixture fixture = new JobParserFixture();
		fixture.metadata.setConfiguredArgs("pikachu", "shock");
		fixture.createAntJobs("charged=true", "pikachu");
		fixture.expectations.expectJob("pikachu", "shock").prop("charged", "true");
		fixture.expectations.verify();
	}

	@Test
	public void createAntJobsProjectMissingTargetGlobalAfter() {
		JobParserFixture fixture = new JobParserFixture();
		fixture.metadata.setConfiguredArgs("pikachu", "shock");
		fixture.createAntJobs("pikachu", "charged=true");
		fixture.expectations.expectJob("pikachu", "shock");
		fixture.expectations.verify();
	}

	@Test
	public void createAntJobsProjectMissingTargetGlobalBeforeOverride() {
		JobParserFixture fixture = new JobParserFixture();
		fixture.metadata.setConfiguredArgs("pikachu", "shock");
		fixture.createAntJobs("charged=true", "pikachu:charged=false");
		fixture.expectations.expectJob("pikachu", "shock").prop("charged", "false");
		fixture.expectations.verify();
	}

	@Test
	public void createAntJobsProjectWithTargetGlobalBeforeLocalPropertyOverride() {
		JobParserFixture fixture = new JobParserFixture();
		fixture.createAntJobs("charged=true", "pikachu:jump:charged=false");
		fixture.expectations.expectJob("pikachu", "jump").prop("charged", "false");
		fixture.expectations.verify();
	}

	@Test
	public void createAntJobsDuoProject() {
		JobParserFixture fixture = new JobParserFixture();
		fixture.metadata.setConfiguredArgs("pikachu", "shock");
		fixture.metadata.setConfiguredArgs("umbreon", "dodge");
		fixture.createAntJobs("pikachu", "umbreon");
		fixture.expectations.expectJob("pikachu", "shock");
		fixture.expectations.expectJob("umbreon", "dodge");
		fixture.expectations.verify();
	}

	@Test
	public void createAntJobsDuoProjectGlobalBefore() {
		JobParserFixture fixture = new JobParserFixture();
		fixture.metadata.setConfiguredArgs("pikachu", "shock");
		fixture.metadata.setConfiguredArgs("umbreon", "dodge");
		fixture.createAntJobs("global=CO2", "pikachu", "umbreon");
		fixture.expectations.expectJob("pikachu", "shock").prop("global", "CO2");
		fixture.expectations.expectJob("umbreon", "dodge").prop("global", "CO2");
		fixture.expectations.verify();
	}

	@Test
	public void createAntJobsDuoProjectGlobalInbetween() {
		JobParserFixture fixture = new JobParserFixture();
		fixture.metadata.setConfiguredArgs("pikachu", "shock");
		fixture.metadata.setConfiguredArgs("umbreon", "dodge");
		fixture.createAntJobs("pikachu", "global=CO2", "umbreon");
		fixture.expectations.expectJob("pikachu", "shock");
		fixture.expectations.expectJob("umbreon", "dodge").prop("global", "CO2");
		fixture.expectations.verify();
	}

	@Test
	public void createAntJobsDuoProjectLocalPropertyOnFirst() {
		JobParserFixture fixture = new JobParserFixture();
		fixture.metadata.setConfiguredArgs("pikachu", "shock");
		fixture.metadata.setConfiguredArgs("umbreon", "dodge");
		fixture.createAntJobs("pikachu:global=CO2", "umbreon");
		fixture.expectations.expectJob("pikachu", "shock").prop("global", "CO2");
		fixture.expectations.expectJob("umbreon", "dodge");
		fixture.expectations.verify();
	}

	@Test
	public void createAntJobsDuoProjectLocalPropertyOnSecond() {
		JobParserFixture fixture = new JobParserFixture();
		fixture.metadata.setConfiguredArgs("pikachu", "shock");
		fixture.metadata.setConfiguredArgs("umbreon", "dodge");
		fixture.createAntJobs("pikachu", "umbreon:global=CO2");
		fixture.expectations.expectJob("pikachu", "shock");
		fixture.expectations.expectJob("umbreon", "dodge").prop("global", "CO2");
		fixture.expectations.verify();
	}

	@Test
	public void createAntJobsDuoProjectGlobalPropertyOverrideOnSecond() {
		JobParserFixture fixture = new JobParserFixture();
		fixture.metadata.setConfiguredArgs("pikachu", "shock");
		fixture.metadata.setConfiguredArgs("umbreon", "dodge");
		fixture.createAntJobs("global=CH4", "pikachu", "umbreon:global=CO2");
		fixture.expectations.expectJob("pikachu", "shock").prop("global", "CH4");
		fixture.expectations.expectJob("umbreon", "dodge").prop("global", "CO2");
		fixture.expectations.verify();
	}

	@Test
	public void createAntJobsResolve() {
		JobParserFixture fixture = new JobParserFixture();
		fixture.metadata.setConfiguredArgs("pikachu", "shock");
		fixture.metadata.setConfiguredArgs("umbreon", "dodge");
		fixture.metadata.setAnnotatedArgs("umbreon", "resolve");
		fixture.createAntJobs("pikachu", "@umbreon");
		fixture.expectations.expectJob("pikachu", "shock");
		fixture.expectations.expectJob("umbreon", "resolve");
		fixture.expectations.verify();
	}
}
