package org.myec3.socle.webapp.config;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.myec3.socle.core.constants.MyEc3Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.j2ee.J2eePreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("userDetailsService")
    private UserDetailsService userDetailsService;

    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/health", "/resources/errorAccess.html", "/static/**", "/assets/**", "/modules.gz/t5/core/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .addFilterAt(customFilter(), BasicAuthenticationFilter.class)
                .exceptionHandling().accessDeniedPage("/errorAccess")
                .and().authorizeRequests()

                .antMatchers("/*").permitAll()
                .antMatchers("/Init").hasAuthority("ROLE_SUPER_ADMIN")

                .antMatchers("/sdmInit/**").hasAuthority("ROLE_SUPER_ADMIN")

                .antMatchers("/customer/**").hasAuthority("ROLE_SUPER_ADMIN")

                .antMatchers("/structure/relation/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN")

                .antMatchers("/user/admin/**").hasAuthority("ROLE_SUPER_ADMIN")
                .antMatchers("/user/superadmin/**").hasAuthority("ROLE_SUPER_ADMIN")
                .antMatchers("/user/search/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER_AGENT","ROLE_MANAGER_EMPLOYEE")
                .antMatchers("/user/regeneratepassword").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER_EMPLOYEE", "ROLE_DEFAULT", "ROLE_ANONYMOUS")

                .antMatchers("/synchroman/**").hasAuthority("ROLE_SUPER_ADMIN")

                .antMatchers("/organism/create/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN")
                .antMatchers("/organism/search/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN")
                .antMatchers("/organism/apiManagement/**").hasAnyAuthority("ROLE_SUPER_ADMIN")
                .antMatchers("/organism/appmanager/**").hasAnyAuthority("ROLE_SUPER_ADMIN")

                .antMatchers("/organism/agent/modify/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER_AGENT", "ROLE_DEFAULT")
                .antMatchers("/organism/agent/modify.agent_form.modification_form").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER_AGENT", "ROLE_DEFAULT")
                .antMatchers("/organism/agent/modifyroles/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER_AGENT","ROLE_APPLICATION_MANAGER_AGENT")
                .antMatchers("/organism/agent/modifyroles.modification_form").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER_AGENT","ROLE_APPLICATION_MANAGER_AGENT")
                .antMatchers("/organism/agent/view/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER_AGENT","ROLE_APPLICATION_MANAGER_AGENT","ROLE_DEFAULT")
                .antMatchers("/organism/agent/listagents/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER_AGENT","ROLE_APPLICATION_MANAGER_AGENT","ROLE_DEFAULT")
                .antMatchers("/organism/agent/listagents.agentprofilegrid.pager/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER_AGENT","ROLE_APPLICATION_MANAGER_AGENT","ROLE_DEFAULT")
                .antMatchers("/organism/agent/listagents.agentprofilegrid.columns:sort/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER_AGENT","ROLE_APPLICATION_MANAGER_AGENT","ROLE_DEFAULT")
                .antMatchers("/organism/agent/viewroles/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER_AGENT","ROLE_APPLICATION_MANAGER_AGENT","ROLE_DEFAULT")
                .antMatchers("/organism/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER_AGENT")

                .antMatchers("/company/modify/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER_EMPLOYEE")
                .antMatchers("/company/search/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER_EMPLOYEE")
                .antMatchers("/company/department/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER_EMPLOYEE")
                .antMatchers("/company/establishment/view*").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER_EMPLOYEE", "ROLE_DEFAULT")
                .antMatchers("/company/establishment/view/*").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER_EMPLOYEE", "ROLE_DEFAULT")
                .antMatchers("/company/establishment/listestablishments/*").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER_EMPLOYEE", "ROLE_DEFAULT")
                .antMatchers("/company/establishment/modify/*").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER_EMPLOYEE", "ROLE_DEFAULT")
                .antMatchers("/company/establishment/listemployees/*").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER_EMPLOYEE", "ROLE_DEFAULT")
                // Creating establishment can be accessed for everyone ! Code will handle rigths
                .antMatchers("/company/establishment/create*").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER_EMPLOYEE", "ROLE_DEFAULT", "ROLE_ANONYMOUS")
                // The next one is needed for tapestry pager (*/*)
                .antMatchers("/company/establishment/create*/*").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER_EMPLOYEE", "ROLE_DEFAULT", "ROLE_ANONYMOUS")
                .antMatchers("/company/establishment/create/*").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER_EMPLOYEE", "ROLE_DEFAULT", "ROLE_ANONYMOUS")
                .antMatchers("/company/employee/modify/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER_EMPLOYEE", "ROLE_DEFAULT")
                .antMatchers("/company/employee/modify.employee_form.modification_form").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER_EMPLOYEE", "ROLE_DEFAULT")
                .antMatchers("/company/employee/view/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER_EMPLOYEE", "ROLE_DEFAULT")
                .antMatchers("/company/employee/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER_EMPLOYEE")

                .antMatchers("/company/siren/").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER_EMPLOYEE", "ROLE_DEFAULT", "ROLE_ANONYMOUS")

                .and().logout()
                .logoutRequestMatcher(new OrRequestMatcher(new KeycloakLogoutRequestMatcher(),new AntPathRequestMatcher("/toto", "GET")))
                .invalidateHttpSession(true).logoutSuccessUrl("/Logout");
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(preauthAuthProvider());
    }

    @Bean
    public PreAuthenticatedAuthenticationProvider preauthAuthProvider() {
        PreAuthenticatedAuthenticationProvider preauthAuthProvider = new PreAuthenticatedAuthenticationProvider();
        preauthAuthProvider.setPreAuthenticatedUserDetailsService(userDetailsServiceWrapper());
        return preauthAuthProvider;
    }

    @Bean
    public UserDetailsByNameServiceWrapper<PreAuthenticatedAuthenticationToken> userDetailsServiceWrapper() {
        UserDetailsByNameServiceWrapper<PreAuthenticatedAuthenticationToken> userDetailsServiceWrapper = new UserDetailsByNameServiceWrapper<>(
                userDetailsService);
        return userDetailsServiceWrapper;
    }

    @Bean
    public LoginUrlAuthenticationEntryPoint authenticationProcessingFilterEntryPoint() {
        LoginUrlAuthenticationEntryPoint authenticationProcessingFilterEntryPoint = new LoginUrlAuthenticationEntryPoint(
                "/login.html");
        authenticationProcessingFilterEntryPoint.setForceHttps(false);
        return authenticationProcessingFilterEntryPoint;
    }

    @Bean
    public AbstractPreAuthenticatedProcessingFilter customFilter() throws Exception {
        KeycloakPreAuthenticatedProcessingFilter filter = new KeycloakPreAuthenticatedProcessingFilter();
		filter.setAuthenticationManager(authenticationManager());
		filter.setCheckForPrincipalChanges(true);
		filter.setInvalidateSessionOnPrincipalChange(false);
        return filter;
    }
}

/**
 * AbstractPreAuthenticatedProcessingFilter qui initialise un contexte d'authentification à partir du username recupere de keycloak
 */
class KeycloakPreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter{

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        Principal princ = request.getUserPrincipal();
        if (!(princ instanceof KeycloakPrincipal)){
            return null;
        }
        return ((KeycloakPrincipal<KeycloakSecurityContext>)princ).getKeycloakSecurityContext().getToken().getPreferredUsername();
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "N/A";
    }
}

/**
 * Ajout d'une condition au {@link org.springframework.security.web.authentication.logout.LogoutFilter} pour déconnecter l'utilisateur
 * si le contexte d'authentification de keyclaok n'est plus présent. Ce qui arrive lors d'une déconnextion depuis une autre application comme
 * le portail par exemle. Dans ce cas, keyclaok fait un appel serveur vers une url du socle interceptée par la valve, cette dernière détruisant
 * le contexte d'authentification de keyclaok.
 */
class KeycloakLogoutRequestMatcher implements RequestMatcher{

    @Override
    public boolean matches(HttpServletRequest request) {
        return !(request.getUserPrincipal() instanceof KeycloakPrincipal);
    }
}

