/*
 * This file is part of Spoutcraft Launcher.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spoutcraft Launcher is licensed under the SpoutDev License Version 1.
 *
 * Spoutcraft Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spoutcraft Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spoutcraft.launcher.api.security;

import java.io.FileDescriptor;
import java.net.InetAddress;
import java.security.Permission;
import java.util.HashMap;
import java.util.HashSet;

public class CommonSecurityManager extends SecurityManager implements Secure {
	private final double key;
	private boolean locked = false;
	private final static HashSet<String> allowedPermissions;
	private final static HashMap<String, HashSet<String>> systemMethodWhiteList;

	static {
		// This defines permissions that are not protected

		allowedPermissions = new HashSet<String>();

		allowedPermissions.add("accessDeclaredMembers");

		/*
		 * This defines the white list for class/methods pairs that can be used when sandboxed
		 *
		 * This is the last java.x.y class the is detected in the stack trace, before normal classes are detected. This means that the only system level classes are called between the security manager check and the calling method
		 *
		 * This allows whitelisting of system methods that are safe, even if they use protected functionality
		 */
		systemMethodWhiteList = new HashMap<String, HashSet<String>>();

		addMethodToWhiteList("java.lang.Enum", "valueOf");
	}

	private static void addMethodToWhiteList(String className, String methodName) {
		HashSet<String> enumMethods = systemMethodWhiteList.get(className);
		if (enumMethods == null) {
			enumMethods = new HashSet<String>();
			systemMethodWhiteList.put(className, enumMethods);
		}
		enumMethods.add(methodName);
	}

	public CommonSecurityManager(double key) {
		if (System.getSecurityManager() instanceof CommonSecurityManager) {
			throw new SecurityException("Warning, duplicate SimpleSecurityManager created!");
		}
		this.key = key;
	}

	public boolean isLocked() {
		return locked;
	}

	public boolean lock(double key) {
		boolean old = locked;
		if (key == this.key) {
			locked = true;
		}
		return old;
	}

	public void unlock(double key) {
		if (key == this.key) {
			locked = false;
		}
	}

	private void checkAccess() {
		if (isLocked()) {
			Thread.dumpStack();
		}
	}

	public void checkAccept(String host, int port) {
		checkAccess();
	}

	@Override
	public void checkAccess(ThreadGroup g) {
		super.checkAccess(g);
	}

	@Override
	public void checkConnect(String host, int port) {
		checkAccess();
	}

	@Override
	public void checkConnect(String host, int port, Object context) {
		checkAccess();
	}

	@Override
	public void checkDelete(String file) {
		if (isLocked()) {
			if (!hasFileAccess(file)) {
				throw new SecurityException("Access is restricted! Addon tried to delete " + file);
			}
		}
	}

	@Override
	public void checkExec(String cmd) {
		checkAccess();
	}

	@Override
	public void checkExit(int status) {
		checkAccess();
	}

	@Override
	public void checkLink(String lib) {
		checkAccess();
	}

	@Override
	public void checkListen(int port) {
		checkAccess();
	}

	@Override
	public void checkMulticast(InetAddress maddr) {
		checkAccess();
	}

	@Override
	public void checkPermission(Permission perm) {
		if (isLocked()) {
			if (allowedPermissions.contains(perm.getName())) {
				return;
			}
			StackTraceElement trace[] = Thread.currentThread().getStackTrace();
			Class<?> stack[] = getClassContext();

			int nonSystemIndex = getFirstNonSystem(stack, trace, 1);
			StackTraceElement systemClass = getIndexedStackTraceElement(trace, nonSystemIndex - 1);

			if (systemClass != null) {
				String systemClassName = systemClass.getClassName();
				HashSet<String> systemAllowedMethods = systemMethodWhiteList.get(systemClassName);

				if (systemAllowedMethods != null && systemAllowedMethods.contains(systemClass.getMethodName())) {
					return;
				}
			}

			checkAccess();
		}
	}

	private int getFirstNonSystem(Class<?> stack[], StackTraceElement trace[], int start) {
		int stackPos = start;
		int tracePos = start + 1;

		while (stackPos < stack.length && stack[stackPos].getClassLoader() == null) {
			stackPos++;
		}

		if (stackPos >= stack.length) {
			return trace.length;
		}

		while (tracePos < trace.length && !trace[tracePos].getClassName().equals(stack[stackPos].getName())) {
			tracePos++;
		}

		return tracePos;
	}

	private StackTraceElement getIndexedStackTraceElement(StackTraceElement trace[], int index) {
		if (index < 0 || index >= trace.length) {
			return null;
		} else {
			return trace[index];
		}
	}

	@Override
	public void checkPermission(Permission per, Object context) {
		checkAccess();
	}

	@Override
	public void checkPrintJobAccess() {
		checkAccess();
	}

	@Override
	public void checkPropertiesAccess() {
		checkAccess();
	}

	@Override
	public void checkPropertyAccess(String property) {
		checkAccess();
	}

	@Override
	public void checkRead(String file) {
		if (isLocked()) {
			if (file.endsWith(".class") || file.endsWith(".jar")) {
				return; // class loader will have already decided it's safe if we got here
			}
			if (!hasFileAccess(file)) {
				System.out.println("Reading from " + file);
				throw new SecurityException("Access is restricted! Addon tried to read " + file);
			}
		}
	}

	@Override
	public void checkRead(String file, Object context) {
		checkRead(file);
	}

	@Override
	public void checkSecurityAccess(String target) {
		checkAccess();
	}

	@Override
	public void checkSetFactory() {
		checkAccess();
	}

	@Override
	public void checkSystemClipboardAccess() {
		checkAccess();
	}

	@Override
	public boolean checkTopLevelWindow(Object window) {
		return !locked;
	}

	@Override
	public void checkWrite(FileDescriptor fd) {
		checkAccess();
	}

	@Override
	public void checkWrite(String file) {
		if (isLocked()) {
			if (!hasFileAccess(file)) {
				System.out.println("Writing to " + file);
				throw new SecurityException("Access is restricted! Addon tried to write to " + file);
			}
		}
	}

	public static boolean hasFileAccess(String file) {
		return false;
	}
}
