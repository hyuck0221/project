package com.hyuck.interceptor

import com.hyuck.dtos.Message
import com.hyuck.model.service.Auth
import org.springframework.http.ResponseEntity
import org.springframework.web.method.HandlerMethod
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class TokenInterceptor : HandlerInterceptor{

    private fun checkAnnotation(handler: Any): Boolean{
        val handlerMethod: HandlerMethod = handler as HandlerMethod
        if(handlerMethod.getMethodAnnotation(Auth::class.java) != null){
            return true
        }
        return false
    }



    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val check: Boolean = checkAnnotation(handler)
        if(check) return true
        if(request.cookies == null){
            return false
        } else {
            return true
        }
    }
}