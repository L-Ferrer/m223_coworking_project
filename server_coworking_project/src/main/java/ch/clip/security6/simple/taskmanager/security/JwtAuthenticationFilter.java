package ch.clip.security6.simple.taskmanager.security;


import ch.clip.security6.simple.taskmanager.security.model.TasksUser;
import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import static ch.clip.security6.simple.taskmanager.security.WebSecurityConstants.*;
import static com.auth0.jwt.algorithms.Algorithm.HMAC512;


//@Component
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    static Logger log = Logger.getLogger("JwtAuthenticationFilter");
    private JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        log.info("authProvider loaded"+authenticationManager);

       // Assert.notNull(daoAuthenticationProvider, "DaoAuthenticationProvider cannot be null");
        Assert.notNull(authenticationManager, "AuthenticationManager cannot be null");
        this.authenticationManager = authenticationManager;
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

      log.info("JwtAuthentication.doFilter:authorization");


      /*  logger.info("==>>  DoFilter ... Is Authentication required? ... ");

        //Here we will attempt JWT AUTHORIZATION
        HttpServletRequest req = (HttpServletRequest) request;

        String header = req.getHeader(HEADER_STRING);

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);*/
        //chain.doFilter(request, response);
        super.doFilter(request, response, chain);

    }
 /*   private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        if (token != null) {
            // parse the token.
            String user = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                    .build()
                    .verify(token.replace(TOKEN_PREFIX, ""))
                    .getSubject();

            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            }
            return null;
        }
        return null;
    }

    private List<SimpleGrantedAuthority> getAuthorities(Claims claims) {
        List<String> authorities = (List<String>) claims.get("authorities");
        if (authorities != null) {
            return authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }*/


    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            log.info("attemptAuthentication");
        //    AuthenticationManager authenticationManager1= SecurityContextHolder.getContext().getAuthentication();
            TasksUser user = new ObjectMapper().readValue(req.getInputStream(), TasksUser.class);
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            user.getPassword(),
                            new ArrayList<>()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        log.info("successfulAuthentication");
        User user = ((User) auth.getPrincipal());
        logger.debug("user : " + user);

        String token = JWT.create()
                .withSubject(((User) auth.getPrincipal()).getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .sign(HMAC512(SECRET.getBytes()));
        // token in body
        PrintWriter out = res.getWriter();
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        out.print(new ObjectMapper().writeValueAsString(new TokenResponse(token)));
        // token in header
  //      res.setHeader(HEADER_STRING, TOKEN_PREFIX+token);
        res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
    }
    // POJO to represent the response body
    @Data
    @AllArgsConstructor
    private class TokenResponse {
        private String token;
    }

}
