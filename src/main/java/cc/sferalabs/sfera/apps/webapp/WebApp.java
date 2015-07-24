package cc.sferalabs.sfera.apps.webapp;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.common.eventbus.Subscribe;

import cc.sferalabs.sfera.apps.Application;
import cc.sferalabs.sfera.core.Configuration;
import cc.sferalabs.sfera.http.HttpServer;
import cc.sferalabs.sfera.http.HttpServerException;
import cc.sferalabs.sfera.http.api.HttpEvent;

public class WebApp extends Application {

	static final Path ROOT = Paths.get("webapp/");

	@Override
	public void onEnable(Configuration configuration) {
		boolean useApplicationCache = configuration.getBoolProperty("application_cache", true);
		try {
			InterfaceCache.init(useApplicationCache);
		} catch (Exception e) {
			logger.error("Error creating cache", e);
		}

		for (String interfaceName : InterfaceCache.getInterfaces()) {
			try {
				HttpServer.addServlet(InterfaceServletHolder.INSTANCE, "/" + interfaceName + "/*");
				HttpServer.addServlet(WebappServletHolder.INSTANCE,
						"/" + interfaceName + "/login/*");
			} catch (Exception e) {
				logger.error("Error registering servlet for interface " + interfaceName, e);
			}
		}
	}

	@Override
	public void onDisable() {
		try {
			HttpServer.removeServlet(InterfaceServletHolder.INSTANCE);
			HttpServer.removeServlet(WebappServletHolder.INSTANCE);
		} catch (HttpServerException e) {
			logger.error("Error removing servlet", e);
		}
	}
	
	@Subscribe
	public void handleHttpEvent(HttpEvent e) {
		// TODO maybe handle GUI events here
		try {
			e.reply("ciao " + e.getUser().getUsername() + " - " + e.getValue());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

}
