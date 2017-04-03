package org.td.distrunner.apirelated;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.td.distrunner.commandhandlers.workschedule.MasterWorkSchedulingJob;
import org.td.distrunner.engine.JsonHelper;
import org.td.distrunner.engine.LogHelper;

public class ApiHandler extends HttpServlet {

	private static final long serialVersionUID = -2009821448033352521L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getPathInfo();

		// download package operation
		if (path.indexOf("DownloadJar") != -1) {
			DownloadJarFileOperation.writeJartoResponse(request, response);
			return;
		}

		switch (path) {
		case "/ClientList":
			ClientListOperation.printClientList(response);
			break;
		case "/UploadProcess":
			UploadProcessOperation.HandleUpload(request, response);
			break;
		case "/StartProcess":
			try {
				String firstParam = JsonHelper.getJsonString("https://en.wikipedia.org/wiki/Java");
				String correlationId = MasterWorkSchedulingJob.startProcess("org.td.samples.StringProcessor", firstParam);
				response.getWriter().print(correlationId);
			} catch (Exception e) {
				LogHelper.logError(e);
			}
			break;
		default:
			response.getWriter().print("<h1>Hello World</h1>");
			break;
		}

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = request.getPathInfo();

		switch (path) {
		case "/UploadProcess":
			UploadProcessOperation.HandleUpload(request, response);
			break;
		default:
			response.getWriter().print("<h1>Hello World</h1>");
			break;
		}
	}
}
