package com.alissonbk.pilacoin.security;

import com.alissonbk.pilacoin.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UsuarioRepository userRepo;
    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(UsuarioRepository userRepo,
                          JwtAuthenticationFilter jwtFilter) {
        this.userRepo  = userRepo;
        this.jwtFilter = jwtFilter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(
                email ->
                        this.userRepo.findByEmailIgnoreCase(email)
                                .map(UserPrincipal::new)
                                .orElseThrow(
                                        () -> new UsernameNotFoundException(
                                                "Usuário " + email + " não encontrado"
                                        )
                                )
        ).passwordEncoder(this.passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
                .and()
                .csrf()
                .disable() // desabilitar CSRF(não se aplica pois não temos sessão/cookies)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                // Filtro JWT que irá fazer a autenticação de usuários
                // com base no token enviado na requisição
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                // Rotas e suas configurações de acesso
                .authorizeRequests()
                .antMatchers("/v1/login").permitAll()
                .antMatchers("/v1/transferencia").authenticated()
                .antMatchers("/v1/mineracao").authenticated()
                .antMatchers("/v1/transacao").authenticated();
    }

    @Bean
    public CorsFilter corsFilter() {
        final var config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        final var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Necessário expor o AuthenticationManager como um bean, para que seja
     * possivel utiliza-lo para o login.
     *
     * @return AuthenticationManager
     * @throws Exception
     */
    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }
}
