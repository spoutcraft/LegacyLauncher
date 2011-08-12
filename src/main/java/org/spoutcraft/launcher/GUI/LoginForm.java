package org.spoutcraft.launcher.GUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.spoutcraft.launcher.GameUpdater;
import org.spoutcraft.launcher.MinecraftUtils;
import org.spoutcraft.launcher.PlatformUtils;
import org.spoutcraft.launcher.SettingsHandler;
import org.spoutcraft.launcher.AsyncDownload.Download;
import org.spoutcraft.launcher.AsyncDownload.DownloadListener;
import org.spoutcraft.launcher.Exceptions.BadLoginException;
import org.spoutcraft.launcher.Exceptions.MCNetworkException;
import org.spoutcraft.launcher.Exceptions.OutdatedMCLauncherException;

public class LoginForm extends JFrame implements ActionListener, DownloadListener, KeyListener {

    /**
     *
     */
    private static final long serialVersionUID = -192904429165686059L;

    private JPanel contentPane;
    private JPasswordField txtPassword;
    private JComboBox cmbUsername = new JComboBox();
    private JButton btnLogin = new JButton("Login");
    private JCheckBox cbRemember = new JCheckBox("Remember");
    private JButton btnLogin1;
    private JButton btnLogin2;
    private JProgressBar progressBar;
    HashMap<String, String> usernames = new HashMap<String, String>();
    public Boolean mcUpdate = false;
    public Boolean spoutUpdate = false;

    GameUpdater gu = new GameUpdater();
    private SettingsHandler settings = new SettingsHandler("defaults/spoutcraft.properties", new File(PlatformUtils.getWorkingDirectory(), "spoutcraft" + File.separator + "spoutcraft.properties"));
    OptionDialog options = new OptionDialog();

    public LoginForm() {

        settings.load();
        gu.setListener(this);

        options.setVisible(false);
        btnLogin.setBounds(745, 375, 86, 23);
        btnLogin.setOpaque(false);
        btnLogin.addActionListener(this);
        JButton btnOptions = new JButton("Options");
        btnOptions.setOpaque(false);
        btnOptions.addActionListener(this);
        cmbUsername.addActionListener(this);

        setIconImage(Toolkit.getDefaultToolkit().getImage(LoginForm.class.getResource("/org/spoutcraft/launcher/favicon.png")));
        setResizable(false);

        setTitle("Spoutcraft Launcher");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((dim.width - 861) / 2, (dim.height - 500) / 2, 861, 500);

        contentPane = new JPanel();

        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        JLabel lblLogo = new JLabel("");
        lblLogo.setBounds(8, 0, 294, 99);
        lblLogo.setIcon(new ImageIcon(LoginForm.class.getResource("/org/spoutcraft/launcher/spoutcraft.png")));

        JLabel lblMinecraftUsername = new JLabel("Minecraft Username: ");
        lblMinecraftUsername.setHorizontalAlignment(SwingConstants.RIGHT);
        lblMinecraftUsername.setBounds(456, 379, 150, 14);

        JLabel lblPassword = new JLabel("Password: ");
        lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);
        lblPassword.setBounds(506, 404, 100, 20);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(617, 404, 119, 20);

        btnLogin1 = new JButton("Login as Player");
        btnLogin1.setBounds(72, 428, 119, 23);
        btnLogin1.setOpaque(false);
        btnLogin1.addActionListener(this);
        btnLogin1.setVisible(false);
        btnLogin2 = new JButton("Login as Player");
        btnLogin2.setBounds(261, 428, 119, 23);
        btnLogin2.setOpaque(false);
        btnLogin2.addActionListener(this);
        btnLogin2.setVisible(false);

        progressBar = new JProgressBar();
        progressBar.setBounds(30, 100, 400, 23);
        progressBar.setVisible(false);
        progressBar.setStringPainted(true);

        readUsedUsernames();

        JLabel lblNewLabel = new HyperlinkJLabel("<html><u>Need a minecraft account?</u></html>", "http://www.minecraft.net/register.jsp");
        lblNewLabel.setBounds(741, 432, 86, 14);

        lblNewLabel.setText("<html><u>Need an account?</u></html>");
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
        lblNewLabel.setForeground(new Color(0, 0, 255));
        cmbUsername.setBounds(616, 376, 119, 20);

        cbRemember.setOpaque(false);

        final JTextPane editorPane = new JTextPane();
        editorPane.setContentType("text/html");

        SwingWorker<Object, Object> newsThread = new SwingWorker<Object, Object>() {
            @Override
            protected Object doInBackground() throws Exception {
                try {
                    editorPane.setPage(new URL("http://updates.getspout.org/"));
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                return null;
            }
        };
        newsThread.execute();

        editorPane.setEditable(false);
        editorPane.setOpaque(false);

        JLabel trans2;

        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setBounds(473, 11, 372, 340);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        editorPane.setCaretPosition(0);
        trans2 = new JLabel();
        trans2.setBackground(new Color(229, 246, 255, 100));
        trans2.setOpaque(true);
        trans2.setBounds(473, 11, 372, 340);

        JLabel login = new JLabel();
        login.setBackground(new Color(255, 255, 255, 120));
        login.setOpaque(true);
        login.setBounds(473, 362, 372, 99);

        JLabel trans;
        trans = new JLabel();
        trans.setBackground(new Color(229, 246, 255, 60));
        trans.setOpaque(true);
        trans.setBounds(0, 0, 854, 480);

        cmbUsername.getEditor().addActionListener(this);
        txtPassword.addKeyListener(this);
        cbRemember.addKeyListener(this);

        cmbUsername.setEditable(true);
        contentPane.setLayout(null);
        cbRemember.setBounds(617, 428, 93, 23);
        contentPane.add(cbRemember);
        contentPane.add(lblLogo);
        contentPane.add(lblPassword);
        contentPane.add(lblMinecraftUsername);
        contentPane.add(txtPassword);
        contentPane.add(cmbUsername);
        contentPane.add(btnLogin);
        btnOptions.setBounds(745, 403, 86, 23);
        contentPane.add(btnOptions);
        contentPane.add(lblNewLabel);
        contentPane.add(btnLogin1);
        contentPane.add(btnLogin2);

        contentPane.add(scrollPane);
        contentPane.add(trans2);
        contentPane.add(login);
        contentPane.add(trans);
        contentPane.add(progressBar);

        final JLabel background = new JLabel("Loading...");
        background.setVerticalAlignment(SwingConstants.CENTER);
        background.setHorizontalAlignment(SwingConstants.CENTER);
        background.setBounds(0, 0, 854, 480);
        contentPane.add(background);

        try {
            final File bgCache;
            bgCache = new File(PlatformUtils.getWorkingDirectory(), "launcher_cache.jpg");
            SwingWorker<Object, Object> bgThread = new SwingWorker<Object, Object>() {
                @Override
                protected Object doInBackground() throws MalformedURLException {
                    if (!bgCache.exists() || System.currentTimeMillis() - bgCache.lastModified() > 1000 * 60 * 60 * 24 * 7) {
                        Download download = new Download("http://www.getspout.org/splash/index.php", bgCache.getPath());
                        download.run();
                    }
                    return null;
                }

                @Override
                protected void done() {
                    background.setIcon(new ImageIcon(bgCache.getPath()));
                }
            };
            bgThread.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Vector<Component> order = new Vector<Component>(5);
        order.add(cmbUsername.getEditor().getEditorComponent());
        order.add(txtPassword);
        order.add(cbRemember);
        order.add(btnLogin);
        order.add(btnOptions);

        setFocusTraversalPolicy(new SpoutFocusTraversalPolicy(order));
    }

    public void drawCharacter(String url, int x, int y) {
        BufferedImage originalImage;
        try {
            try {
                originalImage = ImageIO.read(new URL(url));
            } catch (Exception e) {
                originalImage = ImageIO.read(new URL("https://www.minecraft.net/img/char.png"));
            }
            int type = BufferedImage.TYPE_INT_ARGB;//originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

            drawCroped(originalImage, type, 40, 8, 48, 16, x - 4, y - 5, 8); // HAT

            drawCroped(originalImage, type, 8, 8, 16, 16, x, y, 7); // HEAD

            drawCroped(originalImage, type, 20, 20, 28, 32, x, y + 56, 7); // BODY

            drawCroped(originalImage, type, 44, 20, 48, 32, x - 28, y + 56, 7); // ARMS
            drawCroped(originalImage, type, 44, 20, 48, 32, x + 56, y + 56, 7, true);

            drawCroped(originalImage, type, 4, 20, 8, 32, x, y + 140, 7); // LEGS
            drawCroped(originalImage, type, 4, 20, 8, 32, x + 28, y + 140, 7, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drawCroped(BufferedImage img, int type, int sx1, int sy1, int sx2, int sy2, int x, int y, int scale) {
        drawCroped(img, type, sx1, sy1, sx2, sy2, x, y, scale, false);
    }

    public void drawCroped(BufferedImage img, int type, int sx1, int sy1, int sx2, int sy2, int x, int y, int scale, boolean reflect) {
        BufferedImage resizedImage = new BufferedImage((sx2 - sx1) * scale, (sy2 - sy1) * scale, type);
        Graphics2D g = resizedImage.createGraphics();
        int asx2 = sx2, asx1 = sx1;
        if (reflect) {
            asx2 = sx1;
            asx1 = sx2;
        }
        g.drawImage(img, 0, 0, (sx2 - sx1) * scale, (sy2 - sy1) * scale, asx1, sy1, asx2, sy2, null);
        g.dispose();

        JLabel tmp = new JLabel(new ImageIcon(resizedImage));
        tmp.setBounds(x, y, (sx2 - sx1) * scale, (sy2 - sy1) * scale);
        contentPane.add(tmp);
    }

    public void stateChanged(String fileName, float progress) {
        int intProgress = Math.round(progress);

        progressBar.setValue(intProgress);
        progressBar.setString(intProgress + "% " + fileName);
        //System.out.println(fileName + ": " + progress);
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            doLogin();
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public static class SpoutFocusTraversalPolicy extends FocusTraversalPolicy {
        Vector<Component> order;

        public SpoutFocusTraversalPolicy(Vector<Component> order) {
            this.order = new Vector<Component>(order.size());
            this.order.addAll(order);
        }

        public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {
            int idx = (order.indexOf(aComponent) + 1) % order.size();
            return order.get(idx);
        }

        public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
            int idx = order.indexOf(aComponent) - 1;
            if (idx < 0) {
                idx = order.size() - 1;
            }
            return order.get(idx);
        }

        public Component getDefaultComponent(Container focusCycleRoot) {
            return order.get(0);
        }

        public Component getLastComponent(Container focusCycleRoot) {
            return order.lastElement();
        }

        public Component getFirstComponent(Container focusCycleRoot) {
            return order.get(0);
        }
    }

    private void readUsedUsernames() {
        int i = 0;
        try {
            File lastLogin = new File(PlatformUtils.getWorkingDirectory(), "lastlogin");
            if (!lastLogin.exists()) return;
            Cipher cipher = getCipher(2, "passwordfile");

            DataInputStream dis;
            if (cipher != null)
                dis = new DataInputStream(new CipherInputStream(new FileInputStream(lastLogin), cipher));
            else {
                dis = new DataInputStream(new FileInputStream(lastLogin));
            }

            try {
                //noinspection InfiniteLoopStatement
                while (true) {
                    String user = dis.readUTF();
                    String pass = dis.readUTF();

                    if (!pass.isEmpty()) {
                        i++;
                        if (i == 1) {
                            btnLogin1.setText(user);
                            btnLogin1.setVisible(true);
                            drawCharacter("http://s3.amazonaws.com/MinecraftSkins/" + user + ".png", 103, 170);
                        } else if (i == 2) {
                            btnLogin2.setText(user);
                            btnLogin2.setVisible(true);
                            drawCharacter("http://s3.amazonaws.com/MinecraftSkins/" + user + ".png", 293, 170);
                        }
                    }

                    usernames.put(user, pass);
                    this.cmbUsername.addItem(user);
                }
            } catch (EOFException ignored) {
            }
            dis.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        this.txtPassword.setText(usernames.get(this.cmbUsername.getSelectedItem().toString()));
        this.cbRemember.setSelected(this.txtPassword.getPassword().length > 0);
    }


    private void writeUsernameList() {
        try {
            File lastLogin = new File(PlatformUtils.getWorkingDirectory(), "lastlogin");

            Cipher cipher = getCipher(1, "passwordfile");
            DataOutputStream dos;
            if (cipher != null)
                dos = new DataOutputStream(new CipherOutputStream(new FileOutputStream(lastLogin), cipher));
            else {
                dos = new DataOutputStream(new FileOutputStream(lastLogin, true));
            }
            for (String user : usernames.keySet()) {
                dos.writeUTF(user);
                dos.writeUTF(usernames.get(user));
            }
            dos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent evt) {
        String btnID = evt.getActionCommand();
        if (evt.getSource() == btnLogin1 || evt.getSource() == btnLogin2) {
            btnID = "Login";
            this.cmbUsername.setSelectedItem(((JButton) evt.getSource()).getText());
        }
        if (btnID.equals("Login") || btnID.equals(cmbUsername.getSelectedItem())) {
            doLogin();
        } else if (btnID.equals("Options")) {
            options.setVisible(true);
            options.setBounds((int) getBounds().getCenterX() - 250, (int) getBounds().getCenterY() - 75, 500, 150);
        } else if (btnID.equals("comboBoxChanged")) {
            this.txtPassword.setText(usernames.get(this.cmbUsername.getSelectedItem().toString()));
            this.cbRemember.setSelected(this.txtPassword.getPassword().length > 0);
        }
    }

    private void doLogin() {
    	
    	if (cmbUsername.getSelectedItem().toString() == null || new String(txtPassword.getPassword()) == null) {
    		JOptionPane.showMessageDialog(getParent(), "Incorrect username/password combination");
    		return;
    	}
    	
        this.btnLogin.setEnabled(false);
        this.btnLogin1.setEnabled(false);
        this.btnLogin2.setEnabled(false);
        options.setVisible(false);
        SwingWorker<Boolean, Boolean> loginThread = new SwingWorker<Boolean, Boolean>() {
            String[] values;

            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    values = MinecraftUtils.doLogin(cmbUsername.getSelectedItem().toString(), new String(txtPassword.getPassword()));
                    return true;
                } catch (BadLoginException e) {
                    JOptionPane.showMessageDialog(getParent(), "Incorrect username/password combination");
                    this.cancel(true);
                } catch (MCNetworkException e) {
                    JOptionPane.showMessageDialog(getParent(), "Cannot connect to minecraft.net");
                    this.cancel(true);
                } catch (OutdatedMCLauncherException e) {
                    JOptionPane.showMessageDialog(getParent(), "The unthinkable has happened, alert alta189@getspout.org!!!!");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    this.cancel(true);
                } catch (Exception e) {
                }
                btnLogin.setEnabled(true);
                btnLogin1.setEnabled(true);
                btnLogin2.setEnabled(true);
                this.cancel(true);
                return false;
            }

            @Override
            protected void done() {
            	if (values == null || values.length < 4) return;
                usernames.remove(cmbUsername.getSelectedItem().toString());
                gu.user = values[2].trim();
                gu.downloadTicket = values[1].trim();
                gu.latestVersion = Long.parseLong(values[0].trim());
                if (settings.checkProperty("devupdate")) gu.devmode = settings.getPropertyBoolean("devupdate");
                usernames.put(gu.user, cbRemember.isSelected() ? new String(txtPassword.getPassword()) : "");
                writeUsernameList();

                progressBar.setVisible(true);
                SwingWorker<Boolean, String> updateThread = new SwingWorker<Boolean, String>() {
                    @Override
                    protected void done() {
                        progressBar.setVisible(false);
                        LauncherFrame launcher = new LauncherFrame();
                        launcher.runGame(values[2].trim(), values[3].trim(), values[1].trim(), new String(txtPassword.getPassword()));
                        setVisible(false);
                    }

                    @Override
                    protected Boolean doInBackground() throws Exception {

                        publish("Checking for Minecraft Update...\n");
                        try {
                            mcUpdate = gu.checkMCUpdate(new File(gu.binDir + File.separator + "version"));
                        } catch (Exception e) {
                            mcUpdate = false;
                        }

                        publish("Checking for Spout update...\n");
                        try {
                            spoutUpdate = mcUpdate || gu.checkSpoutUpdate();
                        } catch (Exception e) {
                            spoutUpdate = false;
                        }

                        if (mcUpdate) {
                            gu.updateMC();
                        }
                        if (spoutUpdate) {
                            gu.updateSpout();
                        }
                        return true;
                    }

                    @Override
                    protected void process(List<String> chunks) {
                        progressBar.setString(chunks.get(0));
                    }
                };
                updateThread.execute();
            }
        };
        loginThread.execute();
    }

    private Cipher getCipher(int mode, String password) throws Exception {
        Random random = new Random(43287234L);
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 5);

        SecretKey pbeKey = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(new PBEKeySpec(password.toCharArray()));
        Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
        cipher.init(mode, pbeKey, pbeParamSpec);
        return cipher;
    }
}
