package pl.training.bank;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @NonNull
    private DataSource dataSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth//.inMemoryAuthentication()
            //    .withUser("admin").password("admin").roles("Admin");
            .jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery("select login,password,enabled from client where login = ?")
                .authoritiesByUsernameQuery("select login,role from client where login = ?");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                    .antMatchers("/resources/**").permitAll()
                    .antMatchers("/**").hasRole("ADMIN")
                .and()
                    //.httpBasic();
                    .formLogin()
                        .loginPage("/login.html")
                        .permitAll()
                .and()
                    .logout()
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout.html"))
                        .logoutSuccessUrl("/login.html");
    }

}
