package org.spoutcraft.launcher.donor;

import net.technicpack.launchercore.restful.RestObject;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

public class EventDonors extends RestObject {
	private List<Donor> users = new LinkedList<Donor>();

	private transient Dictionary<String, Donor> hashedDonors = new Hashtable<String, Donor>();

	public EventDonors() {}

	public Donor getDonor(String username) {
		if (!users.isEmpty() && hashedDonors.isEmpty()) {
			hashAllDonors();
		}

		return hashedDonors.get(username);
	}

	private void hashAllDonors() {
		for(Donor donor : users) {
			hashedDonors.put(donor.getUsername(), donor);
		}
	}
}
