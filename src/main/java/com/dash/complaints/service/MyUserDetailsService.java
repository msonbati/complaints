package com.dash.complaints.service;

import com.dash.complaints.entity.DashUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.naming.*;
import javax.naming.directory.*;
import java.util.Hashtable;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private static String LDAP_SERVER_PORT="389";
    private static String LDAP_BASE_DN="dc=daralshifa,dc=com";
    private static String LDAP_SERVER="192.168.20.20";
    private static String LDAP_BIND_DN="CN=dash,CN=Users,DC=daralshifa,DC=com";
    private static String LDAP_BIND_PASSWORD="dash123";
    @Getter  @Setter //
    private String userPassword;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
/*
        Attributes attributes= getUserAttributes(userName);
        System.out.println("------------------------------------------------");
        System.out.println(attributes);
        */
        UserDetails user = new DashUser(userName,"123");
        return user;
    }

    private Attributes getUserAttributes(String userName) {
        Attributes attrs = null;
        Hashtable<String, String> env = new Hashtable<String, String>();


        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://" + LDAP_SERVER + ":" + LDAP_SERVER_PORT + "/" + LDAP_BASE_DN);

        // To get rid of the PartialResultException when using Active Directory
        env.put(Context.REFERRAL, "follow");

        // Needed for the Bind (User Authorized to Query the LDAP server)
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, LDAP_BIND_DN);
        env.put(Context.SECURITY_CREDENTIALS, LDAP_BIND_PASSWORD);

        DirContext ctx;
        try {
            ctx = new InitialDirContext(env);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }

        NamingEnumeration<SearchResult> results = null;


            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE); // Search Entire Subtree
            controls.setCountLimit(1);   //Sets the maximum number of entries to be returned as a result of the search
            controls.setTimeLimit(5000); // Sets the time limit of these SearchControls in milliseconds

            String searchString = "(&(objectCategory=user)(sAMAccountName=" + userName + "))";

        try {
            results = ctx.search("", searchString, controls);

            if (results.hasMore()) {

                SearchResult result = (SearchResult) results.next();
                Attributes attributes = result.getAttributes();

                Attribute userPassword = attributes.get("userPassword");
                if(userPassword!=null){
                    String pwd = new String((byte[]) userPassword.get());
                    System.out.println("user password is "+pwd);
                }
              //  Attribute dnAttr = attrs.get("distinguishedName");
                return attributes;
            }
        } catch (NamingException ex) {
            ex.printStackTrace();
                return null;
        }
        return null;
    }
    private Attributes authinicate(String userName,String userPassword) {
        Attributes attrs = null;
        Hashtable<String, String> env = new Hashtable<String, String>();


        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://" + LDAP_SERVER + ":" + LDAP_SERVER_PORT + "/" + LDAP_BASE_DN);

        // To get rid of the PartialResultException when using Active Directory
        env.put(Context.REFERRAL, "follow");

        // Needed for the Bind (User Authorized to Query the LDAP server)
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, LDAP_BIND_DN);
        env.put(Context.SECURITY_CREDENTIALS, LDAP_BIND_PASSWORD);

        DirContext ctx;
        try {
            ctx = new InitialDirContext(env);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }

        NamingEnumeration<SearchResult> results = null;

        try {
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE); // Search Entire Subtree
            controls.setCountLimit(1);   //Sets the maximum number of entries to be returned as a result of the search
            controls.setTimeLimit(5000); // Sets the time limit of these SearchControls in milliseconds

            String searchString = "(&(objectCategory=user)(sAMAccountName=" + userName + "))";

            results = ctx.search("", searchString, controls);

            if (results.hasMore()) {

                SearchResult result = (SearchResult) results.next();
                attrs = result.getAttributes();
                Attribute dnAttr = attrs.get("distinguishedName");
                String dn = (String) dnAttr.get();

                // User Exists, Validate the Password

                env.put(Context.SECURITY_PRINCIPAL, dn);
                env.put(Context.SECURITY_CREDENTIALS, userPassword);

                new InitialDirContext(env); // Exception will be thrown on Invalid case

                return attrs;
            }
            else
                return null;

        } catch (AuthenticationException e) { // Invalid Login
            //   e.printStackTrace();
            return null;
        } catch (NameNotFoundException e) { // The base context was not found.
            //   e.printStackTrace();
            return null;
        } catch (SizeLimitExceededException e) {
            throw new RuntimeException("LDAP Query Limit Exceeded, adjust the query to bring back less records", e);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        } finally {

            if (results != null) {
                try { results.close(); } catch (Exception e) { /* Do Nothing */ }
            }

            if (ctx != null) {
                try { ctx.close(); } catch (Exception e) { /* Do Nothing */ }
            }
        }
    }


}
