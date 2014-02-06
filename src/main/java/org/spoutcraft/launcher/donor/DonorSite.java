package org.spoutcraft.launcher.donor;

import net.technicpack.launchercore.exception.RestfulAPIException;
import net.technicpack.launchercore.restful.RestObject;

import java.util.Dictionary;
import java.util.Hashtable;

public class DonorSite {
	private String baseUrl;
	private Dictionary<Integer, EventDonors> allEventDonors = new Hashtable<Integer, EventDonors>();

	private String getEventUrl(int eventId) {
		return baseUrl + "api/event/" + eventId + "/users";
	}

	public DonorSite(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public boolean doesUserQualify(int eventId, String username, float threshold) {
		EventDonors donors = getEventDonors(eventId);

		if (donors == null) return false;

		Donor donor = donors.getDonor(username);

		if (donor == null) return false;
		return (donor.getAmount() + 0.001f >= threshold);
	}

	private EventDonors getEventDonors(int eventId) {
		Integer key = new Integer(eventId);

		EventDonors donors = allEventDonors.get(key);

		if (donors != null)
			return donors;

		donors = downloadEventDonors(eventId);

		if (donors == null)
			donors = new EventDonors();

		allEventDonors.put(key, donors);

		return donors;
	}

	private EventDonors downloadEventDonors(int eventId) {
		try {
			return RestObject.getRestObject(EventDonors.class, getEventUrl(eventId));
		} catch (RestfulAPIException ex) {
			//This can happen if we get a 404 or something, which probably means the event ID is garbage,
			//or maybe there's a network outage.
			//Either way, just handle it gracefully
			ex.printStackTrace();
		}

		return null;
	}
}
