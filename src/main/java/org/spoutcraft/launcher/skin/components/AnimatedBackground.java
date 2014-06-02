/*
 * This file is part of Technic Launcher.
 * Copyright (C) 2013 Syndicate, LLC
 *
 * Technic Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Technic Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Technic Launcher.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spoutcraft.launcher.skin.components;

import org.jdesktop.swingworker.SwingWorker;
import org.spoutcraft.launcher.skin.backgrounds.HexxitBackground;
import org.spoutcraft.launcher.skin.backgrounds.TekkitBackground;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class AnimatedBackground extends JLabel {
    private static final long serialVersionUID = 1L;
    protected final AtomicReference<TransparencyWorker> worker = new AtomicReference<TransparencyWorker>(null);
    protected volatile float transparency = 0F;
    private String pack = null;
    private Icon newIcon = null;
    private BackgroundImage background;
    private Map<String, EnhancedBackground> enhanced = new HashMap<String, EnhancedBackground>();

    public AnimatedBackground(BackgroundImage background) {
        super();
        super.setVisible(true);
        this.background = background;

        enhanced.put("tekkitmain", new TekkitBackground());
        enhanced.put("hexxit", new HexxitBackground());

        for (EnhancedBackground enhance : enhanced.values()) {
            this.add(enhance);
        }
    }

    public void changeIcon(String name, Icon newIcon, boolean force) {
        if (!name.equals(pack) || force) {
            this.newIcon = newIcon;
            if (worker.get() != null) {
                worker.get().cancel(true);
            }
            setVisible(false);
            pack = name;
            background.setIcon(getIcon());

            for (EnhancedBackground enhance : enhanced.values()) {
                if (!name.equals(enhance.getName())) {
//					enhance.setVisible(false);
                }
            }
        }
    }

    public Map<String, EnhancedBackground> getEnhanced() {
        return enhanced;
    }

    public String getPack() {
        return pack;
    }

    public Icon getNewIcon() {
        return newIcon;
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            if (worker.get() != null) {
                worker.get().cancel(true);
            }
            worker.set(new TransparencyWorker(this, true));
            worker.get().execute();
        } else {
            if (worker.get() != null) {
                worker.get().cancel(true);
            }
            worker.set(new TransparencyWorker(this, false));
            worker.get().execute();
        }
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D copy = (Graphics2D) g.create();
        copy.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
        super.paint(copy);
        copy.dispose();
    }

    private static class TransparencyWorker extends SwingWorker<Object, Object> {
        private final AnimatedBackground label;
        private final boolean increase;

        TransparencyWorker(AnimatedBackground label, boolean increase) {
            this.label = label;
            this.increase = increase;
        }

        @Override
        protected Object doInBackground() throws Exception {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignore) {
            }
            return null;
        }

        @Override
        protected void done() {
            if (increase) {
                if (label.transparency < 1) {
                    label.transparency = Math.min(1F, label.transparency + 0.05F);
                    label.repaint();
                    if (label.worker.compareAndSet(this, new TransparencyWorker(label, increase))) {
                        label.worker.get().execute();
                    }
                }
            } else {
                if (label.transparency > 0) {
                    label.transparency = Math.max(0F, label.transparency - 0.10F);
                    label.repaint();
                    if (label.worker.compareAndSet(this, new TransparencyWorker(label, increase))) {
                        label.worker.get().execute();
                    }
                } else {
                    label.setIcon(label.getNewIcon());
                    label.setVisible(true);
                    String pack = label.getPack();
                    for (EnhancedBackground enhance : label.getEnhanced().values()) {
                        if (pack.equals(enhance.getName())) {
                            enhance.setVisible(true);
                        } else {
                            enhance.setVisible(false);
                        }
                    }
                }
            }
        }
    }
}
