package com.hyuck.model.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Service
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse


@Service
class JwtService {

    fun createCookie(id: Long, response: HttpServletResponse) {  //jwt 토큰 생성
        val jwt = Jwts.builder()  // JWT 생성 (아래는 부가데이터)
            .setIssuer(id.toString())     // 발행되는 유저(유저 아이디)
            .setExpiration(Date(System.currentTimeMillis()+60*24*1000))
            //토큰 제한시간 (위 숫자는 1일)
            .signWith(SignatureAlgorithm.HS256,"secret")
            .compact()  // 압축

        val cookie = Cookie("jwt", jwt)
        cookie.isHttpOnly = true
        response.addCookie(cookie)
    }
}