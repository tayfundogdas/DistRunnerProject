package org.td.distrunner.communication.custom;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.commons.io.IOUtils;
import org.td.distrunner.communication.IClientSocket;
import org.td.distrunner.helpers.CommunicationHelper;
import org.td.distrunner.helpers.LogHelper;
import org.td.distrunner.model.AppSettings;

public class ClientSocket implements IClientSocket {

	private String getMasterWSAddress(int messageType) {
		StringBuilder str = new StringBuilder();
		str.append("http://");
		str.append(AppSettings.MasterAddress);
		str.append(":");
		str.append(AppSettings.JettyPort);
		str.append("/communication/");
		str.append(messageType);

		return str.toString();
	}

	public String sendMessagetoMaster(int messageType, String payLoad) {
		String result = null;
		try {
			URL website = new URL(getMasterWSAddress(messageType));
			HttpURLConnection con = (HttpURLConnection) website.openConnection();
			// add request header
			con.setRequestMethod("POST");

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			byte[] messageBytes = payLoad.toString().getBytes("UTF8");
			for (int i = 0; i < messageBytes.length; i++) {
				wr.writeByte(messageBytes[i]);
			}
			wr.flush();
			wr.close();

			// process response message
			if (con.getResponseCode() == 200) {
				String response = IOUtils.toString(con.getInputStream(), "UTF-8");
				if (response != null && response.length() > 0)
					result = response;
			}
		} catch (Exception e) {
			result = CommunicationHelper.CommunicationError;
			LogHelper.logError(e);
		}
		return result;
	}

	public String sendMessagetoAddress(int messageType, String payLoad, String url) {
		// TODO Auto-generated method stub
		return null;
	}
}
