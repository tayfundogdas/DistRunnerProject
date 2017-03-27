package org.td.distrunner.wsrelated;

import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class ServerHandler extends WebSocketHandler
{
    private ClientList clientList = new ClientList();

    @Override
    public void configure(WebSocketServletFactory factory)
    {
        factory.setCreator(new ServerCreator(clientList));
    }
}
