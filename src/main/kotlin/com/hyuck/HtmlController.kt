package com.hyuck

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.security.MessageDigest
import javax.servlet.http.HttpSession


@Controller
class HtmlController {

    @Autowired
    lateinit var repository: UserRepository
    var login_user = ""

    @RequestMapping("/")
    fun index(model: Model): String {
        if(login_user == ""){
            model.addAttribute("title", "Home")
            return "index"
        } else{
            val db_user = repository.findByUserId(login_user)
            model.addAttribute("title", "board list")
            model.addAttribute("nick", db_user.nick)
            return "board_list"
        }
    }

    fun crypto(ss: String): String {
        val sha = MessageDigest.getInstance("SHA-256")
        val hexa = sha.digest(ss.toByteArray())
        val crypto_str = hexa.fold("", { str, it -> str + "%02x".format(it) })
        return crypto_str
    }

    @GetMapping("/{formType}")
    fun htmlForm(model: Model, @PathVariable formType: String): String {
        var response: String = ""

        if (formType.equals("sign")) {
            response = "sign"
        } else if (formType.equals("login")) {
            response = "login"
        } else if (formType.equals("mylist")){
            if(login_user != ""){
                response = "mylist"
            } else{
                response = "login"
            }
        } else if(formType.equals("logout")){
            login_user = ""
            response = "index"
        }

        model.addAttribute("title", response)
        return response
    }

    @PostMapping("/sign")
    fun postSign(
        model: Model,
        @RequestParam(value = "id") userId: String,
        @RequestParam(value = "password") password: String,
        @RequestParam(value = "nick") nick: String
    ): String {
        try {
            val cryptoPass = crypto(password)
            repository.save(User(userId, cryptoPass, nick))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        model.addAttribute("title", "sign success")

        return "login"  //수정
    }

    @PostMapping("/login")
    fun postlogin(
        model: Model,
        session: HttpSession,
        @RequestParam(value = "id") userId: String,
        @RequestParam(value = "password") password: String): String {

        var pageName=""
        try {
            val cryptoPass = crypto(password)
            val db_user = repository.findByUserId(userId)

            if (db_user != null) {
                val db_pass = db_user.password

                if (cryptoPass.equals(db_pass)) {
                    session.setAttribute("userId", db_user.userId)
                    model.addAttribute("title", "board list")
                    model.addAttribute("nick", db_user.nick)
                    login_user = userId
                    pageName = "board_list"
                }
                else {
                    model.addAttribute("title", "login")
                    pageName = "login"
                }
            }

        } catch (e: Exception) {
            model.addAttribute("title", "login")
            pageName = "login"
        }
        return pageName
    }
}