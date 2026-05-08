package com.classmate.common.ratelimit;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitProperties {

	private boolean enabled = true;
	private long defaultLimit = 60;
	private long defaultWindowSeconds = 60;
	private Policy question = new Policy(3, 10);
	private Policy feedback = new Policy(3, 5);
	private Policy post = new Policy(5, 60);
	private Policy teamRecruit = new Policy(3, 60);
	private Policy dashboard = new Policy(5, 1);

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public long getDefaultLimit() {
		return defaultLimit;
	}

	public void setDefaultLimit(long defaultLimit) {
		this.defaultLimit = defaultLimit;
	}

	public long getDefaultWindowSeconds() {
		return defaultWindowSeconds;
	}

	public void setDefaultWindowSeconds(long defaultWindowSeconds) {
		this.defaultWindowSeconds = defaultWindowSeconds;
	}

	public Policy getQuestion() {
		return question;
	}

	public void setQuestion(Policy question) {
		this.question = question;
	}

	public Policy getFeedback() {
		return feedback;
	}

	public void setFeedback(Policy feedback) {
		this.feedback = feedback;
	}

	public Policy getPost() {
		return post;
	}

	public void setPost(Policy post) {
		this.post = post;
	}

	public Policy getTeamRecruit() {
		return teamRecruit;
	}

	public void setTeamRecruit(Policy teamRecruit) {
		this.teamRecruit = teamRecruit;
	}

	public Policy getDashboard() {
		return dashboard;
	}

	public void setDashboard(Policy dashboard) {
		this.dashboard = dashboard;
	}

	public record Policy(long limit, long windowSeconds) {
	}
}
