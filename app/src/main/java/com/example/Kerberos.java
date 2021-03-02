package com.example;

import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.login.LoginException;

import com.ibm.security.auth.module.Krb5LoginModule;

import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;

public class Kerberos {
	
	Kerberos() {

	}

    Subject doKerberosLogin(String principal) throws LoginException {
        Subject subject = new Subject();
        Krb5LoginModule krb5 = new Krb5LoginModule();
        Map<String, String> options = new HashMap<String, String>();
        Map<String, Object> sharedState = new HashMap<String, Object>();
        CallbackHandler callback = null;
  
        String keytab = "/etc/krb5.keytab";
        String ccache = null;
/*  
		options.put("debug", "true");
        options.put("isInitiator", "true");
        //options.put("refreshKrb5Config", "true");

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
        
        //if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
        //    options.put("debug", "true");
        //    Tr.debug(tc, "All kerberos config properties are: " + options);
        //}

        for (Map.Entry entry : options.entrySet()) {
            System.out.println("key: " + entry.getKey() + "; value: " + entry.getValue());
        }
  
        krb5.initialize(subject, callback, sharedState, options);
        krb5.login();
        krb5.commit();
  
        // If the created Subject does not have a GSSCredential, then create one and
        // associate it with the Subject
        
        Set<GSSCredential> gssCreds = subject.getPrivateCredentials(GSSCredential.class);
        if (gssCreds == null || gssCreds.size() == 0) {
            GSSCredential gssCred = createGSSCredential(subject, getKerberosTicketFromSubject(subject));
            subject.getPrivateCredentials().add(gssCred);

        }
        
  
        return subject;
    }
    
    private static GSSCredential createGSSCredential(Subject subject, final KerberosTicket ticket) {
        GSSCredential gssCred = null;
        PrivilegedAction<Object> action = new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                GSSCredential inGssCrd = null;
                try {
                    String clientName = ticket.getClient().getName();
                    Oid krbOid = new Oid("1.2.840.113554.1.2.2");
                    if (clientName != null && clientName.length() > 0) {
                        GSSManager gssMgr = GSSManager.getInstance();
                        GSSName gssName = gssMgr.createName(clientName, GSSName.NT_USER_NAME, krbOid);
                        inGssCrd = gssMgr.createCredential(gssName.canonicalize(krbOid),
                                                           GSSCredential.INDEFINITE_LIFETIME,
                                                           krbOid, GSSCredential.INITIATE_AND_ACCEPT);
                    }
  
                } catch (GSSException e) {
                        System.out.println(e.getMessage());
                }
                return inGssCrd;
  
            }
        };
  
        gssCred = (GSSCredential) Subject.doAs(subject, action);
  
        return gssCred;
    }
    
    private static KerberosTicket getKerberosTicketFromSubject(final Subject subject) {
        KerberosTicket kerberosTicketInSubject = null;
        Set<KerberosTicket> privCredentials = subject.getPrivateCredentials(KerberosTicket.class);
        if (privCredentials != null) {
            Iterator<KerberosTicket> privCredItr = privCredentials.iterator();
            while (privCredItr.hasNext()) {
                KerberosTicket kTicket = privCredItr.next();
                return kTicket;                    
            }
        }
        return kerberosTicketInSubject;
    }
}
