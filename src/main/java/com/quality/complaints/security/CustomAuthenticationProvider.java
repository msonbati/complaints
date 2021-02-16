package com.quality.complaints.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.naming.*;
import javax.naming.directory.*;
import javax.naming.ldap.InitialLdapContext;
import java.util.*;

@Slf4j
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Value("${ldap.host}")
    String ldapHost;

    @Value("${ldap.port}")
    String ldapPort;

    @Value("${ldap.base-dn}")
    String baseDomainName;

    @Value("${ldap.domain-prefix}")
    String domainPrefix;

    @Value("${ldap.read.timeout}")
    String timeout;
    private static final String DEFAULT_JNDI_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        //user and password are from user's input from login form...
        String user= authentication.getName();
        String password = authentication.getCredentials().toString();

        try {
            Set<String> roles = authenticate(user, password);

            if (CollectionUtils.isEmpty(roles))
                return null;

            List<GrantedAuthority> authorityList = new ArrayList<>();

            for(String role: roles){
                authorityList.add(new SimpleGrantedAuthority(role));
            }

            return new UsernamePasswordAuthenticationToken(user, password, authorityList);

        } catch (NamingException ex) {
            log.info("NamingException",ex);


        }
        return null;
    }

    public Set<String> authenticate(final String username, final String password) throws NamingException {

        InitialLdapContext ctx = null;
        NamingEnumeration<SearchResult> results = null;

        try {
            final Hashtable<String, String> ldapEnvironment = new Hashtable<>();
            ldapEnvironment.put(Context.INITIAL_CONTEXT_FACTORY, DEFAULT_JNDI_CONTEXT_FACTORY);
            ldapEnvironment.put(Context.PROVIDER_URL, "ldap://" + ldapHost + ":" + ldapPort + "/" + baseDomainName);
            ldapEnvironment.put(Context.SECURITY_PROTOCOL, "simple");
            ldapEnvironment.put(Context.SECURITY_CREDENTIALS, password);
            ldapEnvironment.put(Context.SECURITY_PRINCIPAL, domainPrefix + '\\' + username);

            ldapEnvironment.put("com.sun.jndi.ldap.read.timeout", timeout);
            ldapEnvironment.put("com.sun.jndi.ldap.connect.timeout", timeout);

            ctx = new InitialLdapContext(ldapEnvironment, null);

            final SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            constraints.setReturningAttributes(new String[]{"memberOf"});
            constraints.setReturningObjFlag(true);
            results = ctx.search("", "(sAMAccountName=" + username + ")", constraints);

            if (!results.hasMore()) {
                log.warn(".authenticate(" + ldapHost + "," + username + "): unable to locate " + username);
                return null;
            }

            final Set<String> adGroups = new TreeSet<>();
            final SearchResult entry = results.next();
            for (NamingEnumeration valEnum = entry.getAttributes().get("memberOf").getAll(); valEnum.hasMoreElements(); ) {
                String dn = (String) valEnum.nextElement();
                int i = dn.indexOf(",");
                if (i != -1) {
                    dn = dn.substring(0, i);
                }

                if (dn.startsWith("CN=")) {
                    dn = dn.substring("CN=".length());
                }
                adGroups.add(dn);
            }
            return adGroups;
        }
        finally {
            try {
                if (null != results)
                    results.close();
            }
            catch (Throwable ignored) {
            }
            try {
                if (null != ctx)
                    ctx.close();
            }
            catch (Throwable ignored) {
            }
        }
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(
                UsernamePasswordAuthenticationToken.class);
    }

    private Attributes authinicate(String userName, String userPassword) {
        Attributes attrs = null;
        Hashtable<String, String> env = new Hashtable<String, String>();


        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://" + ldapHost + ":" + ldapPort + "/" + baseDomainName);

        // To get rid of the PartialResultException when using Active Directory
        env.put(Context.REFERRAL, "follow");
        String LDAP_BIND_DN="CN=dash,CN=Users,DC=daralshifa,DC=com";
        String LDAP_BIND_PASSWORD="dash123";
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
