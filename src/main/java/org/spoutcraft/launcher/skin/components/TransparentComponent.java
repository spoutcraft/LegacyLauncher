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

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Adds transparency to a swing component
 */
class TransparentComponent implements MouseListener {
    private final JComponent parent;
    private float transparency = 1F;
    private float hoverTransparency = 1F;
    private boolean hovering = false;
    private final boolean repaint;

    public TransparentComponent(JComponent component) {
        this.parent = component;
        parent.addMouseListener(this);
        repaint = true;
    }

    public TransparentComponent(JComponent component, boolean repaint) {
        this.parent = component;
        parent.addMouseListener(this);
        this.repaint = repaint;
    }

    /**
     * Gets the transparency of the label when no mouse is hovering over it
     * <p/>
     * Values are between 0 and 1
     *
     * @return transparency
     */
    public float getTransparency() {
        return transparency;
    }

    /**
     * Sets the transparency of the label when no mouse is hovering over it
     * <p/>
     * Values should be between 0 and 1
     *
     * @param t transparency
     */
    public void setTransparency(float t) {
        if (t > 1F || t < 0F) {
            throw new IllegalArgumentException("Value out of range");
        }
        this.transparency = t;
    }

    /**
     * Gets the transparency of the label when a mouse is hovering over it
     * <p/>
     * Values are between 0 and 1
     *
     * @return transparency
     */
    public float getHoverTransparency() {
        return hoverTransparency;
    }

    /**
     * Sets the transparency of the label when a mouse is hovering over it
     * <p/>
     * Values should be between 0 and 1
     *
     * @param t transparency
     */
    public void setHoverTransparency(float t) {
        if (t > 1F || t < 0F) {
            throw new IllegalArgumentException("Value out of range");
        }
        this.hoverTransparency = t;
    }

    public Graphics setup(Graphics g) {
        float t;
        if (hovering) {
            t = getHoverTransparency();
        } else {
            t = getTransparency();
        }
        Graphics2D copy = (Graphics2D) g.create();
        copy.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, t));
        return copy;
    }

    public Graphics cleanup(Graphics g) {
        g.dispose();
        return g;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (e.getComponent() == parent) {
            hovering = true;
            if (repaint) {
                parent.repaint();
            }
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (e.getComponent() == parent) {
            hovering = false;
            if (repaint) {
                parent.repaint();
            }
        }
    }
}
