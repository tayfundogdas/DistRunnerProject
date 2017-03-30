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

	public void sendMessagetoMaster(Message message) throws Exception {
		URL website = new URL(getMasterWSAddress());
		HttpURLConnection con = (HttpURLConnection) website.openConnection();
		// add request header
		con.setRequestMethod("POST");

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		byte[] messageBytes = message.toString().getBytes("UTF8");
		for (int i = 0; i < messageBytes.length; i++) {
			wr.writeByte(messageBytes[i]);
		}
		wr.flush();
		wr.close();

		// process response message
		if (con.getResponseCode() == 200) {
			String response = IOUtils.toString(con.getInputStream(), "UTF-8");			
			if (response != null && response.length() > 0)
				MessageDispatcher.HandleMessage(response);
		}
	}

	public void sendMessagetoAddress(Message message, String url) throws Exception {

	}
}
