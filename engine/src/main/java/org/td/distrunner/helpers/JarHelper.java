package org.td.distrunner.helpers;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import org.apache.commons.io.IOUtils;
import org.jbpm.bpmn2.xml.XmlBPMNProcessDumper;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.td.distrunner.model.AppSettings;

public class JarHelper {

	public static RuleFlowProcess getProcessByName(String processName) throws Exception {
		RuleFlowProcess result = null;

		// jar file
		File jarFile = new File(AppSettings.ProcessJarPath + processName + ".jar");
		JarFile jar = new JarFile(jarFile);
		// Getting the files from jar
		Enumeration<? extends JarEntry> enumeration = jar.entries();
		// find bpmn file
		ZipEntry zipEntry = Collections.list(enumeration).stream()
				.filter(x -> x.getName()
						.startsWith(processName.substring(0, processName.lastIndexOf('.')).replace('.', '/') + "/")
						&& x.getName().endsWith(".bpmn"))
				.findFirst().orElse(null);

		if (zipEntry != null) {
			InputStream in = jar.getInputStream(zipEntry);
			String processXml = IOUtils.toString(in);
			XmlBPMNProcessDumper xmlOp = XmlBPMNProcessDumper.INSTANCE;
			RuleFlowProcess process = (RuleFlowProcess) xmlOp.readProcess(processXml);
			in.close();
			result = process;
		}

		// close jar file
		jar.close();

		return result;
	}

	public static File[] getMatchingFiles(String startswith) {
		File f = new File(AppSettings.ProcessJarPath);
		File[] matchingFiles = f.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith(startswith);
			}
		});

		return matchingFiles;
	}

}
