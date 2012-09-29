package org.spoutcraft.launcher.util;
import java.util.List;

/**
 *
 * This is the 3rd version of SwingWorker (also known as
 * SwingWorker 3), an abstract class that you subclass to
 * perform GUI-related work in a dedicated thread.  For
 * instructions on using this class, see:
 *
 * http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html
 *
 * Note that the API changed slightly in the 3rd version:
 * You must now invoke start() on the SwingWorker after
 * creating it.
 *
 * Created by IntelliJ IDEA.
 * Date created: 05.05.2005 19:52:58
 * by: wanja
 * Changes:
 *
 * @author $Author: wanja $
 *         Date: $Date: 2006-11-07 04:52:09 +0100 (Di, 07 Nov 2006) $
 *         $Id: SwingWorker.java 971 2006-11-07 03:52:09Z wanja $
 * @version $Revision: 971 $
 */
public abstract class SwingWorker<T, V> extends org.jdesktop.swingworker.SwingWorker<T, V> {

    /**
     * This is a wrapper for get()
     *
     * @return T
     */
    public T getValue() {
        try {
            return super.get();
        }
        catch( Exception ex) {
            return null;
        }
    }


    /**
     * Receives data chunks from the {@code publish} method asynchronously on the
     * <i>Event Dispatch Thread</i>.
     * <p/>
     * <p/>
     * Please refer to the {@link #publish} method for more details.
     *
     * @param chunks intermediate results to process
     * @see #publish
     */
    protected void process(List<V> chunks) {
        super.process(chunks);
    }
}