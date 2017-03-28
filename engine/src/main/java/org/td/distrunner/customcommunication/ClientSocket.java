package org.td.distrunner.customcommunication;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.commons.io.IOUtils;
import org.td.distrunner.commandhandlers.MessageDispatcher;
import org.td.distrunner.model.AppSettings;
import org.td.distrunner.model.Message;

public class ClientSocket {

	private String getMasterWSAddress() {
		StringBuilder str = new StringBuilder();
		str.append("http://");
		str.append(AppSettings.MasterAddress);
		str.append(":");
		str.append(AppSettings.JettyPort);
		str.append("/Message/");

		return str.toString();
	}

	public void sendMessagetoMaster(@SuppressWarnings("rawtypes") Message message) throws Exception {
		URL website = new URL(getMasterWSAddress());
		HttpURLConnection con = (HttpURLConnection) website.openConnection();
		// add request header
		con.setRequestMethod("POST");

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(message.toString());
		wr.flush();
		wr.close();

		// process response message
		String response = IOUtils.toString(con.getInputStream());
		if (response != null && response.length() > 0)
			MessageDispatcher.HandleMessage(response);
	}

	public void sendMessagetoAddress(@SuppressWarnings("rawtypes") Message message, String url) throws Exception {

	}
}
