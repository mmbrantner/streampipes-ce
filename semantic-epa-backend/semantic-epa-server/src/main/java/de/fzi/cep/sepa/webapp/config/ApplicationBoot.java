package de.fzi.cep.sepa.webapp.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import de.fzi.cep.sepa.webapp.examples.DemoDataGenerator;



public class ApplicationBoot implements ServletContextListener {	
	

	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void contextInitialized(ServletContextEvent arg0) {
		DemoDataGenerator.generateDemoData();
		
	}
	
}