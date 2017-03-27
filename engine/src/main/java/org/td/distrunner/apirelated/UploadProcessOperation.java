package org.td.distrunner.apirelated;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringEscapeUtils;
import org.jbpm.bpmn2.xml.XmlBPMNProcessDumper;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.td.distrunner.engine.InMemoryObjects;
import org.td.distrunner.model.AppSettings;
import org.td.distrunner.processmodelparser.JarHelper;

public class UploadProcessOperation {

	// 5mb
	private static int maxFileSize = 5 * 1024 * 1024;
	private static int maxMemSize = 4 * 1024;

	public static void HandleUpload(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// Check that we have a file upload request
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		response.setContentType("text/html");
		if (!isMultipart) {
			StringBuilder res = new StringBuilder();
			res.append("<html>");
			res.append("<head>");
			res.append("<title>Servlet upload</title>");
			res.append("</head>");
			res.append("<body>");

			res.append("<h3>File Upload:</h3>");
			res.append("Select a file to upload: <br />");
			res.append("<form action='UploadProcess' method='post' enctype='multipart/form-data'>");
			res.append("<input type='file' name='file' size='50' /><br />");
			res.append("<input type='submit' value='Upload File' />");
			res.append("</form>");

			res.append("</body>");
			res.append("</html>");
			response.getWriter().print(res);
			return;
		}
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// maximum size that will be stored in memory
		factory.setSizeThreshold(maxMemSize);
		// Location to save data that is larger than maxMemSize.
		factory.setRepository(new File(AppSettings.UploadTemp));

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);
		// maximum file size to be uploaded.
		upload.setSizeMax(maxFileSize);

		try {
			// Parse the request to get file items.
			List<FileItem> fileItems = upload.parseRequest(request);

			// Process the uploaded file items
			Iterator<FileItem> i = fileItems.iterator();

			StringBuilder res = new StringBuilder();
			res.append("<html>");
			res.append("<head>");
			res.append("<title>Servlet upload</title>");
			res.append("</head>");
			res.append("<body>");
			while (i.hasNext()) {
				FileItem fi = (FileItem) i.next();
				if (!fi.isFormField()) {
					// Get the uploaded file
					String fileName = fi.getName();
					// Write the file
					File file;
					if (fileName.lastIndexOf("\\") >= 0) {
						fileName = fileName.substring(fileName.lastIndexOf("\\"));
					} else {
						fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
					}
					file = new File(AppSettings.ProcessJarPath + fileName);
					fi.write(file);

					// parse process file
					String processXML = null;
					try {
						String processName = fileName.replace(".jar", "");
						RuleFlowProcess process = JarHelper.getProcessByName(processName);
						processXML = XmlBPMNProcessDumper.INSTANCE.dump(process);
						InMemoryObjects.processCache.put(processName, process);
					} catch (Exception e) {
						file.delete();
					}

					// print parse result
					if (processXML != null) {
						res.append("Uploaded Process: <br>");
						res.append(StringEscapeUtils.escapeHtml(processXML));
					} else {
						file.delete();
						res.append("Invalid file! <br>");
					}
				}
			}
			res.append("</body>");
			res.append("</html>");
			response.getWriter().print(res);
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}
}
