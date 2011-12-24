package org.spoutcraft.launcher.gui;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Random;

import javax.swing.JTextPane;
import javax.swing.SwingWorker;

public class TumblerFeedParsingWorker extends SwingWorker<Object, Object>{
	JTextPane editorPane;
	private String username = null;
	private Random rand = new Random();
	public TumblerFeedParsingWorker(JTextPane editorPane) {
		this.editorPane = editorPane;
	}
	
	public void setUser(String name) {
		username = name;
	}

	@Override
	protected Object doInBackground() throws Exception {
		try {
			URL url = new URL("http://updates.getspout.org/");
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
				editorPane.setVisible(false);
				editorPane.setPage(url);
				try {
					Thread.sleep(5000);
				}
				catch (InterruptedException e) { }
				
				String text = editorPane.getText();
				
				int index = text.indexOf("<!-- BEGIN TUMBLR CODE -->");
				int endIndex = text.indexOf("<!-- END TUMBLR CODE -->") + "<!-- END TUMBLR CODE -->".length();
				if (index > -1 && endIndex > -1) {
					text = text.substring(0, index) + text.substring(endIndex);
				}
				text = text.replaceAll("<li>", "- ");
				text = text.replaceAll("</li>", "<br/>");
				text = text.replaceAll("<p>", "");
				text = text.replace("</p>", "<br/>");
				text = text.replaceAll("</p>", "<br/><br/>");
				text = text.replaceAll("@time_of_day", getTimeOfDay());
				text = text.replaceAll("@username", getUsername());
				editorPane.setText(text);
				editorPane.setVisible(true);
			}
			else {
				editorPane.setText(getErrorMessage());
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return null;
	}
	
	private String getErrorMessage() {
		String[] errors = {
			"Oh dear, I'm out of tea and crumpets again. I'll have to go make some more.",
			"I'm sorry, were you looking for something here? I couldn't find it.",
			"This isn't the tumbler news feed you are looking for. Move along now.", 
			"What do you mean the website is down...? Hey! What's that over there!",
			"Looks like the %mob%s got into the servers again...", 
			"Oh noes! Our Tumblr feed is down!"
			
		};
		return errors[rand.nextInt(errors.length)].replaceAll("%mob%", getRandomMob());
	}
	
	private String getUsername() {
		return username != null ? username : "Player";
	}
	
	private String getRandomMob() {
		int mob = rand.nextInt(5);
		switch(mob) {
			case 0: return "Spider";
			case 1: return "Zombie";
			case 2: return "Creeper";
			case 3: return "Skeleton";
			case 4: return "Ghast";
			default: return "";
		}
	}
	
	private String getTimeOfDay() {
		int hours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		if (hours < 6)
			return "Night";
		if (hours < 12)
			return "Morning";
		if (hours < 14)
			return "Day";
		if (hours < 18)
			return "Afternoon";
		if (hours < 22) {
			return "Evening";
		}
		return "Night";
	}

}
