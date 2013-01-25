package org.spoutcraft.launcher.technic.skin;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import org.spoutcraft.launcher.skin.MetroLoginFrame;
import org.spoutcraft.launcher.skin.components.LiteButton;
import org.spoutcraft.launcher.skin.components.LiteTextBox;
import org.spoutcraft.launcher.util.Utils;

public class ImportOptions extends JDialog implements ActionListener, MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	private static final String QUIT_ACTION = "quit";
	private static final String IMPORT_ACTION = "import";
	private static final String CHANGE_FOLDER = "folder";
	private static final int FRAME_WIDTH = 520;
	private static final int FRAME_HEIGHT = 222;
	private JLabel msgLabel;
	private JLabel background;
	private JFileChooser fileChooser;
	private int mouseX = 0, mouseY = 0;
	
	public ImportOptions() {
		setTitle("Add a Pack");
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		addMouseListener(this);
		addMouseMotionListener(this);
		setResizable(false);
		setUndecorated(true);
		initComponents();
	}
	
	public void initComponents() {
		Font minecraft = MetroLoginFrame.getMinecraftFont(12);
		
		background = new JLabel();
		background.setBounds(0,0, FRAME_WIDTH, FRAME_HEIGHT);
		MetroLoginFrame.setIcon(background, "platformBackground.png", background.getWidth(), background.getHeight());
		
		Container contentPane = getContentPane();
		contentPane.setLayout(null);
		
		ImageButton optionsQuit = new ImageButton(MetroLoginFrame.getIcon("quit.png", 28, 28), MetroLoginFrame.getIcon("quit.png", 28, 28));
		optionsQuit.setRolloverIcon(MetroLoginFrame.getIcon("quitHover.png", 28, 28));
		optionsQuit.setBounds(FRAME_WIDTH - 38, 10, 28, 28);
		optionsQuit.setActionCommand(QUIT_ACTION);
		optionsQuit.addActionListener(this);
		
		msgLabel = new JLabel();
		msgLabel.setBounds(10, 90, FRAME_WIDTH - 20, 25);
		msgLabel.setText("Enter your Technic Platform delivery URL below to add a new pack:");
		msgLabel.setForeground(Color.white);
		msgLabel.setFont(minecraft);
		
		LiteTextBox url = new LiteTextBox(this, "Paste Platform URL Here");
		url.setBounds(10, msgLabel.getY() + msgLabel.getHeight() + 10, FRAME_WIDTH - 20, 30);
		url.setFont(minecraft);
		
		LiteButton save = new LiteButton("Add Modpack");
		save.setFont(minecraft.deriveFont(14F));
		save.setBounds(FRAME_WIDTH - 130, FRAME_HEIGHT - 50, 120, 30);
		save.setActionCommand(IMPORT_ACTION);
		save.addActionListener(this);
		
		fileChooser = new JFileChooser(Utils.getLauncherDirectory());
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		LiteButton folder = new LiteButton("Change Folder");
		folder.setFont(minecraft.deriveFont(14F));
		folder.setBounds(FRAME_WIDTH - 270, FRAME_HEIGHT - 50, 130, 30);
		folder.setActionCommand(CHANGE_FOLDER);
		folder.addActionListener(this);
		
		contentPane.add(optionsQuit);
		contentPane.add(msgLabel);
		contentPane.add(folder);
		contentPane.add(url);
		contentPane.add(save);
		contentPane.add(background);
		
		setLocationRelativeTo(this.getOwner());
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JComponent) {
			action(e.getActionCommand(), (JComponent)e.getSource());
		}
	}
	
	private void action(String action, JComponent c) {
		if (action.equals(QUIT_ACTION)) {
			dispose();
		} else if (action.equals(CHANGE_FOLDER)) {
			int result = fileChooser.showOpenDialog(this);
			
			if (result == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				file.exists();
				// File is chosen here
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		this.setLocation(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
