package com.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;

public class DB2Krb5Configuration extends Configuration {
	
	private String principal;
	private String keytab;
	private String ccache;
	
	public DB2Krb5Configuration() {
		principal = "dbuser";
		keytab = "/etc/krb5.keytab";
		ccache = null;
	}
	
	@Override
	public AppConfigurationEntry[] getAppConfigurationEntry(String name) {

		System.out.println("getAppConfigurationEntry with name: " + name + "    principal: " + principal + "    keytab: " + keytab + "   ccache: " + ccache);

		new Exception().printStackTrace(System.out);

		Map<String, String> options = new HashMap<String, String>();

		/*
		options.put("debug", "true");
        options.put("isInitiator", "true");
        //ptions.put("refreshKrb5Config", "true");

        options.put("doNotPrompt", "true");

        if (ccache != null) {
            options.put("useTicketCache", "true");
            options.put("ticketCache", ccache);
        }
        // If no keytab path specified, still set useKeyTab=true because then the
        // default JDK or default OS locations will be checked
        options.put("useKeyTab", "true");
        if (keytab != null) {
            options.put("keyTab", keytab);
        }
        options.put("principal", principal);
*/
		options.put("debug", "true");
		options.put("principal",principal);
		if (keytab != null && !keytab.equals("")) {
			options.put("useKeytab",keytab);
			options.put("credsType","both");
		}
		if (ccache != null && !ccache.equals("")) options.put("useCcache",ccache);
		if ((keytab == null || keytab.equals("")) && (ccache == null || ccache.equals(""))) options.put("useDefaultCcache","true");


		AppConfigurationEntry[] configArray = {
			//new AppConfigurationEntry("com.sun.security.auth.module.Krb5LoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,options)
			new AppConfigurationEntry("com.ibm.security.auth.module.Krb5LoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,options)
		};

		//if (name.equals("JaasClient") || name.equals("com.sun.security.jgss.initiate") || name.equals("com.sun.security.jgss.krb5.initiate") || name.equals("com.sun.security.jgss.accept") || name.equals("com.sun.security.jgss.krb5.accept")) {
		if (name.equals("JaasClient") || name.equals("com.ibm.security.jgss.initiate") || name.equals("com.ibm.security.jgss.krb5.initiate") || name.equals("com.ibm.security.jgss.accept") || name.equals("com.ibm.security.jgss.krb5.accept")) {
			return configArray;
		}
		return null;

	}
}
