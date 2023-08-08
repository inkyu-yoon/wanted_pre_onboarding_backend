package com.wanted.global.util;


import com.wanted.global.constants.JwtConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;


public class JwtUtil {


    public static String createToken(String email, String secretKey) {

        Claims claims = Jwts.claims()
                .setSubject(email);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JwtConstants.TOKEN_VALID_MILLIS))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

    }

    public static String getUserEmail(String token, String secretKey) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public static boolean isExpired(String token, String secretKey) {
        Jws<Claims> claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token);

        return claims.getBody()
                .getExpiration()
                .before(new Date());
    }

}
