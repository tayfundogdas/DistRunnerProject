package org.td.distrunner.wsrelated;

import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

public class ServerCreator implements WebSocketCreator
{
    private ClientList clientList;

    public ServerCreator(ClientList clientList)
    {
        this.clientList = clientList;
    }

	@Override
	public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
		return new ServerSocket(clientList);
	}
}
