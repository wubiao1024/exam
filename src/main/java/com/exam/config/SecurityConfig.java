package com.exam.config;

import com.exam.filter.TokenAuthenticationFilter;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Resource
    private TokenAuthenticationFilter filter;

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String[] permitAll = RequestPathMatchRules.getPermitAll();
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 权限配置
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(permitAll).permitAll()
                        .requestMatchers(RequestPathMatchRules.getAnonymous()).anonymous()
                        .anyRequest().permitAll()
                )
                .cors(withDefaults());
        // 登录页的配置
                /*.formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                )*/
        // 把token 的过滤器加入到这个filter 之前
//                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        return http.build();


    }

   /* @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withDefaultPasswordEncoder().username("admin").password("admin123").roles("USER").build());
        return manager;
    }*/

    // 跨域的配置
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*")); // 允许所有来源
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 允许所有方法
        configuration.setAllowedHeaders(List.of("*")); // 允许所有头部
        configuration.setMaxAge(7200L); // 允许跨域的时间
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // 创建BCryptPasswordEncoder 注入容器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }




/*    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 关闭 csrf
                .csrf().disable()
                // 不通过session 获取Security Context
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // 允许匿名访问
                .antMatchers(RequestPathMatchRules.getAnonymous()).anonymous()// 不登陆才能访问（不带token）
                .antMatchers(RequestPathMatchRules.getPermitAll()).permitAll() // 一律允许访问
                // 除上面接口以外的所有接口都要认证
                .anyRequest().authenticated();// 一律拦截检查

        // 把Token 过滤器加到 UsernamePasswordAuthenticationFilter  过滤器之前
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

    }*/

/*    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }*/
}
