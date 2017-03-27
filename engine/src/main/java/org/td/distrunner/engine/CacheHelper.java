package org.td.distrunner.engine;

import java.io.File;
import java.io.FilenameFilter;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.td.distrunner.model.AppSettings;
import org.td.distrunner.processmodelparser.JarHelper;

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
				InMemoryObjects.processCache.put(processName, process);
			} catch (Exception e) {
				LogHelper.logError(e);
			}
		}
	}
}
