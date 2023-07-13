package ch.clip.security6.simple.taskmanager.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static ch.clip.security6.simple.taskmanager.security.WebSecurityConstants.*;


//@Component
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    static Logger log = Logger.getLogger("JwtAuthorizationFilter");
    private AuthenticationManager authenticationManager;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    /*  public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
          log.info("authProvider loaded"+authenticationManager.toString());
          Assert.notNull(authenticationManager, "AuthenticationManager cannot be null");
          this.authenticationManager = authenticationManager;
      }*/
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(HEADER_STRING);
        log.info("JwtAuthorization.doFilterInternal:authorization");
        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
        //super.doFilter(request, response, chain);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        if (token != null) {
            // parse the token.
            String user = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                    .build()
                    .verify(token.replace(TOKEN_PREFIX, ""))
                    .getSubject();

            log.info("getAuthentication: "+user);
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
    }

}
