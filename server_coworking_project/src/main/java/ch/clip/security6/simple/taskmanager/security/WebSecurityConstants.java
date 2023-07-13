package ch.clip.security6.simple.taskmanager.security;

import org.springframework.beans.factory.annotation.Value;

public class WebSecurityConstants {
    @Value("${jwt.secret}")
    public static final String SECRET = "(G+KaPdSgVkYp3s6v9y$B&E)H@McQeThWmZq4t7w!z%C*F-JaNdRgUkXn2r5u8x/";
    public static final long JWT_TOKEN_VALIDITY = 864_000_000; // 10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/users/sign-up";
}
