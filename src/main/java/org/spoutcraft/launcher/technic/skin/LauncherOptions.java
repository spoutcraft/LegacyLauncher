package org.spoutcraft.launcher.technic.skin;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.spoutcraft.launcher.Memory;
import org.spoutcraft.launcher.Settings;
import org.spoutcraft.launcher.skin.MetroLoginFrame;
import org.spoutcraft.launcher.skin.components.LiteButton;

public class LauncherOptions extends JDialog implements ActionListener, MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;

	private static final int FRAME_WIDTH = 300;
	private static final int FRAME_HEIGHT = 300;
	private static final String QUIT_ACTION = "quit";
	private static final String SAVE_ACTION = "save";

	private JLabel background;
	private JComboBox memory;
	private int mouseX = 0, mouseY = 0;

	public LauncherOptions() {
		setTitle("Launcher Options");
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		addMouseListener(this);
		addMouseMotionListener(this);
		setResizable(false);
		setUndecorated(true);
		initComponents();
	}

	private void initComponents() {
		Font minecraft = MetroLoginFrame.getMinecraftFont(12);
		
		background = new JLabel();
		background.setBounds(0,0, FRAME_WIDTH, FRAME_HEIGHT);
		MetroLoginFrame.setIcon(background, "optionsBackground.png", background.getWidth(), background.getHeight());

		ImageButton optionsQuit = new ImageButton(MetroLoginFrame.getIcon("quit.png", 28, 28), MetroLoginFrame.getIcon("quit.png", 28, 28));
		optionsQuit.setRolloverIcon(MetroLoginFrame.getIcon("quitHover.png", 28, 28));
		optionsQuit.setBounds(FRAME_WIDTH - 38, 10, 28, 28);
		optionsQuit.setActionCommand(QUIT_ACTION);
		optionsQuit.addActionListener(this);

		JLabel title = new JLabel("Launcher Options");
		title.setFont(minecraft.deriveFont(14F));
		title.setBounds(50, 10, 200, 20);
		title.setForeground(Color.WHITE);
		title.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel memoryLabel = new JLabel("Memory: ");
		memoryLabel.setFont(minecraft);
		memoryLabel.setBounds(50, 100, 75, 25);
		memoryLabel.setForeground(Color.WHITE);

		memory = new JComboBox();
		memory.setBounds(150, 100, 100, 25);
		populateMemory(memory);

		LiteButton save = new LiteButton("Save and Close");
		save.setFont(minecraft.deriveFont(14F));
		save.setBounds(10, FRAME_HEIGHT - 40, 280, 30);
		save.setActionCommand(SAVE_ACTION);
		save.addActionListener(this);

		Container contentPane = getContentPane();
		contentPane.add(optionsQuit);
		contentPane.add(title);
		contentPane.add(memory);
		contentPane.add(memoryLabel);
		contentPane.add(save);
		contentPane.add(background);

		setLocationRelativeTo(this.getOwner());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JComponent) {
			action(e.getActionCommand(), (JComponent) e.getSource());
		}
	}

	public void action(String action, JComponent c) {
		if (action.equals(QUIT_ACTION)) {
			dispose();
		} else if (action.equals(SAVE_ACTION)) {
			Settings.setMemory(Memory.memoryOptions[memory.getSelectedIndex()].getSettingsId());
			Settings.getYAML().save();
			dispose();
		}
	}

	@SuppressWarnings("restriction")
	private void populateMemory(JComboBox memory) {
		long maxMemory = 1024;
		String architecture = System.getProperty("sun.arch.data.model", "32");
		boolean bit64 = architecture.equals("64");

		try {
			OperatingSystemMXBean osInfo = ManagementFactory.getOperatingSystemMXBean();
			if (osInfo instanceof com.sun.management.OperatingSystemMXBean) {
				maxMemory = ((com.sun.management.OperatingSystemMXBean) osInfo).getTotalPhysicalMemorySize() / 1024 / 1024;
			}
		} catch (Throwable t) {
		}
		maxMemory = Math.max(512, maxMemory);

		if (maxMemory >= Memory.MAX_32_BIT_MEMORY && !bit64) {
			memory.setToolTipText("<html>Sets the amount of memory assigned to Spoutcraft<br/>" + "You have more than 1.5GB of memory available, but<br/>"
					+ "you must have 64bit java installed to use it.</html>");
		} else {
			memory.setToolTipText("<html>Sets the amount of memory assigned to Spoutcraft<br/>" + "More memory is not always better.<br/>"
					+ "More memory will also cause your CPU to work more.</html>");
		}

		if (!bit64) {
			maxMemory = Math.min(Memory.MAX_32_BIT_MEMORY, maxMemory);
		}
		System.out.println("Maximum usable memory detected: " + maxMemory + " mb");

		for (Memory mem : Memory.memoryOptions) {
			if (maxMemory >= mem.getMemoryMB()) {
				memory.addItem(mem.getDescription());
			}
		}

		int memoryOption = Settings.getMemory();
		try {
			Settings.setMemory(memoryOption);
			memory.setSelectedIndex(Memory.getMemoryIndexFromId(memoryOption));
		} catch (IllegalArgumentException e) {
			memory.removeAllItems();
			memory.addItem(String.valueOf(Memory.memoryOptions[0]));
			Settings.setMemory(1); // 512 == 1
			memory.setSelectedIndex(0); // 1st element
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
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
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

}
