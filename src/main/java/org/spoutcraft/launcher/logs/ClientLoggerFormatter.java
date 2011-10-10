/*
 * This file is part of Spoutcraft Launcher (http://wiki.getspout.org/).
 * 
 * Spoutcraft Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spoutcraft Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spoutcraft.launcher.logs;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class ClientLoggerFormatter extends Formatter{

	public String format(LogRecord record) {
		
		// Create a StringBuffer to contain the formatted record
		// start with the date.
		StringBuilder sb = new StringBuilder();
		
		// Get the date from the LogRecord and add it to the buffer
		sb.append("[");
		sb.append( new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date(System.currentTimeMillis())));
		sb.append("] ");
		
		// Get the level name and add it to the buffer
		sb.append(record.getLevel().toString().equals("STDOUT") ? "[INFO]" : "[SEVERE]");
		sb.append("\t");
		 
		// Get the formatted message (includes localization 
		// and substitution of paramters) and add it to the buffer
		sb.append(formatMessage(record));
		sb.append("\n");

		return sb.toString();
	}

}
