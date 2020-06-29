/*******************************************************************************
 * Copyright (c) 2020 Pivotal Software, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Pivotal Software, Inc. - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.eclipse.boot.dash.model.remote;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.JavaRuntime;
import org.springsource.ide.eclipse.commons.livexp.util.Log;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class RemoteJavaLaunchUtil {

	public static void cleanupOldLaunchConfigs(Collection<GenericRemoteAppElement> existinginElements) {
		//TODO: implement this and find a good place and time to call it from.
		Set<String> validIds = new HashSet<>();
		for (GenericRemoteAppElement el : existinginElements) {

		}
	}

	public static final String APP_NAME = "sts.boot.dash.element.name";

	/**
	 * Check the state of a remote boot dash element and whether it needs to have a debugger
	 * attached. If yes, make sure there is a debugger attached.
	 */
	public synchronized static void synchronizeWith(GenericRemoteAppElement app) {
		if (isDebuggable(app)) {
			ensureDebuggerAttached(app);
		}
	}

	private static void ensureDebuggerAttached(GenericRemoteAppElement app) {
		try {
			ILaunchConfiguration conf = getLaunchConfig(app);
			if (conf==null) {
				conf = createLaunchConfig(app);
			}
			ensureActiveLaunch(conf);
		} catch (Exception e) {
			Log.log(e);
		}
	}


	private static ILaunchConfiguration createLaunchConfig(GenericRemoteAppElement app) throws CoreException {
		ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
		String launchConfName = lm.generateLaunchConfigurationName(app.getStyledName(null).getString());

		ILaunchConfigurationWorkingCopy wc = remoteJavaType().newInstance(null, launchConfName);
		wc.setAttribute(APP_NAME, app.getName());
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_ALLOW_TERMINATE, true);
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_CONNECTOR, JavaRuntime.getDefaultVMConnector().getIdentifier());

		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, app.getProject().getName());

		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP, ImmutableMap.of(
				"hostname", "localhost",
				"port", ""+app.getDebugPort()
		));
		return wc.doSave();
	}


	private static void ensureActiveLaunch(ILaunchConfiguration conf) throws CoreException {
		ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
		for (ILaunch l : lm.getLaunches()) {
			if (conf.equals(l.getLaunchConfiguration())) {
				if (!l.isTerminated()) {
					return;
				}
			}
		};
		conf.launch(ILaunchManager.DEBUG_MODE, new NullProgressMonitor(), false, true);
	}

	private static ILaunchConfiguration getLaunchConfig(GenericRemoteAppElement app) {
		try {
			ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
			ILaunchConfigurationType remoteJavaType = remoteJavaType();
			ILaunchConfiguration[] configs = lm.getLaunchConfigurations(remoteJavaType);
			for (ILaunchConfiguration conf : configs) {
				if (app.getName().equals(getAppName(conf))) {
					return conf;
				}
			}
		} catch (Exception e) {
			Log.log(e);
		}
		return null;
	}


	private static ILaunchConfigurationType remoteJavaType() {
		ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType remoteJavaType = lm.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_REMOTE_JAVA_APPLICATION);
		return remoteJavaType;
	}


	private static String getAppName(ILaunchConfiguration conf) {
		try {
			return conf.getAttribute(APP_NAME, (String)null);
		} catch (CoreException e) {
			Log.log(e);
		}
		return null;
	}


	private static boolean isDebuggable(GenericRemoteAppElement app) {
		try {
			int debugPort = app.getDebugPort();
			IProject project = app.getProject();
			return debugPort>0 && project!=null && project.isAccessible() && project.hasNature(JavaCore.NATURE_ID);
		} catch (Exception e) {
			Log.log(e);
		}
		return false;
	}

	public static ImmutableSet<ILaunchConfiguration> getLaunchConfigs(GenericRemoteAppElement element) {
		try {
			String appName = element.getName();
			ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
			ILaunchConfigurationType type = lm.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_REMOTE_JAVA_APPLICATION);
			ILaunchConfiguration[] confs = lm.getLaunchConfigurations(type);
			if (confs!=null) {
				ImmutableSet.Builder<ILaunchConfiguration> found = ImmutableSet.builder();
				for (ILaunchConfiguration c : confs) {
					if (appName.equals(c.getAttribute(APP_NAME, (String)null))) {
						found.add(c);
					}
				}
				return found.build();
			}
		} catch (Exception e) {
			Log.log(e);
		}
		return ImmutableSet.of();
	}
}