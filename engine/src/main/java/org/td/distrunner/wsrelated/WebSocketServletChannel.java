package org.td.distrunner.wsrelated;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class WebSocketServletChannel extends WebSocketServlet
{
	private static final long serialVersionUID = 6487439122319909731L;

	@Override
    public void configure(WebSocketServletFactory factory)
    {
        factory.register(WebSocketServerChannel.class);
    }
}
