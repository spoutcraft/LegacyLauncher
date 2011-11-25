package org.spoutcraft.launcher.gui;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JTextPane;
import javax.swing.SwingWorker;

public class TumblerFeedParsingWorker extends SwingWorker<Object, Object>{
	JTextPane editorPane;
	public TumblerFeedParsingWorker(JTextPane editorPane) {
		this.editorPane = editorPane;
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
				editorPane.setText(text);
				editorPane.setVisible(true);
			}
			else {
				editorPane.setText("Oh Noes! Our Tumblr Feed is Down!");
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return null;
	}

}
