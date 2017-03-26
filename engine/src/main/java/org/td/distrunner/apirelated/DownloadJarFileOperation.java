package org.td.distrunner.apirelated;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.td.distrunner.processmodelparser.JarHelper;

public class DownloadJarFileOperation {
	public static void writeJartoResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String path = request.getPathInfo();
		String namespace = path.substring(path.lastIndexOf('/') + 1, path.length()).replace("jar", "");
		File[] matchingFiles = JarHelper.getMatchingFiles(namespace);
		if (matchingFiles.length > 0) {
			int character;
			FileInputStream inputStream = new FileInputStream(matchingFiles[0]);
			while ((character = inputStream.read()) != -1)
				response.getWriter().append((char) character);
			inputStream.close();
		} else
			response.getWriter().print(namespace + "jar not found!");
	}
}
