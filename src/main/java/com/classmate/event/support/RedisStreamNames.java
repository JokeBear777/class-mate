package com.classmate.event.support;

public final class RedisStreamNames {

	public static final String LECTURE_EVENTS_STREAM = "classmate:lecture-events";
	public static final String LECTURE_EVENTS_DLQ_STREAM = "classmate:lecture-events:dlq";

	public static final String REALTIME_CONSUMER_GROUP = "realtime-consumer-group";
	public static final String MONITORING_CONSUMER_GROUP = "monitoring-consumer-group";
	public static final String NOTIFICATION_CONSUMER_GROUP = "notification-consumer-group";
	public static final String LLM_SUMMARY_CONSUMER_GROUP = "llm-summary-consumer-group";

	private RedisStreamNames() {
	}
}
