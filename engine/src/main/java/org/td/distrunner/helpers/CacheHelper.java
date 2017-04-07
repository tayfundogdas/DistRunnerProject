package org.td.distrunner.helpers;

import java.io.File;
import java.io.FilenameFilter;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.model.AppSettings;

public class CacheHelper {
	public static void LoadProcessCache() {
		File f = new File(AppSettings.ProcessJarPath);
		File[] matchingFiles = f.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar");
			}
		});
		for (File file : matchingFiles) {
			String processName = file.getName().replace(".jar", "");
			RuleFlowProcess process;
			try {
				process = JarHelper.getProcessByName(processName);
				if (process != null)
					// TODO:What do to in case uploaded new version of current process?
					InMemoryObjects.processCache.put(processName, process);
			} catch (Exception e) {
				LogHelper.logError(e);
			}
		}
	}
}
