package org.spoutcraft.launcher.GUI;

import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;

import javax.swing.JLabel;

public class HyperlinkJLabel extends JLabel implements MouseListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3801443131566852907L;
	private String url;
	public void mouseClicked(MouseEvent arg0) {
		if (!Desktop.isDesktopSupported()) return;
		Desktop desktop = Desktop.getDesktop();
		if (!desktop.isSupported( java.awt.Desktop.Action.BROWSE ) ) return;
		try {
            URI uri = new java.net.URI( url );
            desktop.browse( uri );
        }
        catch ( Exception e ) {
        	System.err.println( e.getMessage() );
        }
	}
	
	public HyperlinkJLabel(String text, String url) {
		super(text);
		this.url = url;
		super.addMouseListener(this);
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
