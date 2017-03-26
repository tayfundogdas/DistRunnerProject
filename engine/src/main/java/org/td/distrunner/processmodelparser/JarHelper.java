package org.td.distrunner.processmodelparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import org.td.distrunner.engine.LogHelper;
import org.td.distrunner.model.AppSettings;
import org.td.distrunner.model.ProcessModel;

public class JarHelper {

	public static ProcessModel getProcessByName(String processName) throws Exception {
		ProcessModel result = null;

		// jar file
		File jarFile = new File(AppSettings.ProcessJarPath + processName + ".jar");
		JarFile jar = new JarFile(jarFile);
		// Getting the files from jar
		Enumeration<? extends JarEntry> enumeration = jar.entries();
		// find bpmn file
		ZipEntry zipEntry = Collections.list(enumeration).stream()
				.filter(x -> x.getName().startsWith("org/td/samples/") && x.getName().endsWith(".bpmn")).findFirst()
				.get();

		if (zipEntry != null) {
			String entryName = zipEntry.getName();
			if (entryName.startsWith("org/td/samples/") && entryName.endsWith(".bpmn")) {

				InputStream in = jar.getInputStream(zipEntry);
				BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

				String str = null;
				StringBuilder sb = new StringBuilder(8192);
				while ((str = r.readLine()) != null) {
					sb.append(str);
				}
				String content = sb.toString();
				result = ProcessfromXML.getFromXml(content);
			}
		}

		// close jar file
		jar.close();

		return result;
	}

	public static File[] getMatchingFiles(String startswith)
	{
		File f = new File(AppSettings.ProcessJarPath);
		File[] matchingFiles = f.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith(startswith);
			}
		});
		
		return matchingFiles;
	}
	
	public static void main(String[] args) throws Exception {
		// for logging
		LogHelper.setupLog();
		getProcessByName("org.td.samples.StringProcessor");
	}
}
