package org.pixelgaffer.turnierserver;

import airbrake.AirbrakeNoticeBuilder;
import airbrake.AirbrakeNotifier;

public final class Airbrake {
	
	private Airbrake() {}
	
	private static AirbrakeNotifier notifier = new AirbrakeNotifier();
	
	public static <T extends Throwable> T log(T t) {
		String apiKey = PropertyUtils.getString("turnierserver.airbrake.key");
		if(apiKey != null) {
			notifier.notify(new AirbrakeNoticeBuilder(apiKey, t).newNotice());
		}
		return t;
	}
	
	public static String log(String s) {
		String apiKey = PropertyUtils.getString("turnierserver.airbrake.key");
		if(apiKey != null) {
			notifier.notify(new AirbrakeNoticeBuilder(apiKey, s).newNotice());
		}
		return s;
	}
	
}
