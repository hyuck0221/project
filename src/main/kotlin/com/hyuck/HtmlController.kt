package com.hyuck

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Repository
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
    @Autowired
    lateinit var boardRepository: BoardRepository
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
            model.addAttribute("list", boardRepository.findAll())
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
            if(login_user != ""){
                response = "board_list"
            } else{
                response = "sign"
            }
        } else if (formType.equals("login")) {
            if(login_user != ""){
                response = "board_list"
            } else{
                response = "login"
            }
        } else if (formType.equals("mylist")){
            if(login_user != ""){
                response = "mylist"
            } else{
                response = "login"
            }
        } else if(formType.equals("logout")){
            login_user = ""
            response = "index"
        } else if(formType.equals("create_board")){
            if(login_user != ""){
                response = "create_board"
            } else{
                response = "login"
            }
        }



        if(response == "board_list"){
            model.addAttribute("nick", login_user)
            model.addAttribute("list", boardRepository.findAll())
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

        return "login"
    }
    @PostMapping("/create_board")
    fun postCreateBoard(
        model: Model,
        @RequestParam(value = "title") title: String,
        @RequestParam(value = "des") des: String
    ): String {
        try {
            val db_user = repository.findByUserId(login_user)
            boardRepository.save(Board(db_user.nick, title, des, db_user.id))
        } catch (e:Exception){
            e.printStackTrace()
        }

        model.addAttribute("title", "create board success")
        model.addAttribute("nick", login_user)

        return "board_list"
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
                    model.addAttribute("list", boardRepository.findAll())
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