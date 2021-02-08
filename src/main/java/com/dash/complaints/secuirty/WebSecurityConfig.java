package com.dash.complaints.secuirty;



import com.dash.complaints.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService myUserDetailsService;
/*
    @Value("${ldap.urls}")
    private String ldapUrls;

    @Value("${ldap.base.dn}")
    private String ldapBaseDn;

    @Value("${ldap.username}")
    private String ldapSecurityPrincipal;

    @Value("${ldap.password}")
    private String ldapPrincipalPassword;

    @Value("${ldap.user.dn.pattern}")
    private String ldapUserDnPattern;
    */

    /*
    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("admin")

                .password(passwordEncoder().encode("admin"))
                .roles("USER")
                .build();
        System.out.println("user pass: "+user.getPassword());
        return new InMemoryUserDetailsManager(user);

    }
*/
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      //  LdapAuthenticationProviderConfigurer<AuthenticationManagerBuilder> authenticationManagerBuilderLdapAuthenticationProviderConfigurer = auth.ldapAuthentication();
        auth.userDetailsService(myUserDetailsService);

    }

    @Override

    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/c/home").permitAll()
                .antMatchers("/c/add").permitAll()
                .anyRequest().fullyAuthenticated()
                .and()
                .formLogin();

/*
        http.authorizeRequests()

                .antMatchers("/c/home").permitAll()
                .anyRequest().authenticated()
                .and().formLogin();
*/
    }


    @Bean
    public PasswordEncoder getPasswordEncoder() {
      //  return new BCryptPasswordEncoder();
        PasswordEncoder ps=     NoOpPasswordEncoder.getInstance();
        System.out.println("ps encode = "+ps.encode("MS@123123"));
        return ps;
    }

}
