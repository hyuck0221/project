package com.hyuck

import com.hyuck.entites.board.Board
import com.hyuck.entites.board.BoardRepository
import com.hyuck.entites.user.User
import com.hyuck.entites.user.UserRepository
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
    lateinit var UserRepository: UserRepository
    @Autowired
    lateinit var BoardRepository: BoardRepository


    var login_user = ""
    var read_board:Long = 0

    @RequestMapping("/")
    fun index(model: Model): String {
        if(login_user == ""){
            model.addAttribute("title", "Home")
            read_board = 0
            return "index"
        } else{
            val db_user = UserRepository.findByUserId(login_user)
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
        } else if(formType.equals("logout")){
            login_user = ""
            response = "index"
        } else if(formType.equals("create_board")){
            if(login_user != ""){
                response = "create_board"
            } else{
                response = "login"
            }
        } else if(formType.equals("delete_board")){
            if(login_user != ""){
                response = "delete_board"
            } else{
                response = "login"
            }
        } else if(formType.equals("read_board")){
            if(login_user != ""){
                if(read_board > 0){
                    response = "read_board"
                    model.addAttribute("title", "read "+read_board)
                    if(BoardRepository.findBycrudid(read_board).userid == UserRepository.findByUserId(login_user).id){
                        model.addAttribute("EDIT", "EDIT")
                    } else{
                        model.addAttribute("EDIT", "")
                    }
                    model.addAttribute("nick", BoardRepository.findBycrudid(read_board).nick)
                    model.addAttribute("b_title", BoardRepository.findBycrudid(read_board).title)
                    model.addAttribute("des", BoardRepository.findBycrudid(read_board).des)
                } else{
                    response = "board_list"
                }
            } else{
                response = "login"
            }
        } else if(formType.equals("edit_board")){
            if(login_user != ""){
                if(read_board > 0){
                    response = "edit_board"
                    model.addAttribute("edit_title", BoardRepository.findBycrudid(read_board).title)
                    model.addAttribute("edit_des", BoardRepository.findBycrudid(read_board).des)
                    if(BoardRepository.findBycrudid(read_board).look){
                        model.addAttribute("select_1","selected=\"selected\"")
                        model.addAttribute("select_2","")
                    } else {
                        model.addAttribute("select_1","")
                        model.addAttribute("select_2","selected=\"selected\"")
                    }
                } else{
                    response = "board_list"
                }
            } else{
                response = "login"
            }
        }


        if(response == "board_list"){
            read_board = 0
            val db_user = UserRepository.findByUserId(login_user)
            model.addAttribute("nick", db_user.nick)
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
        var pageName = ""
        try {
            val db_user = UserRepository.findByUserId(userId)
            if(db_user.userId != null){
                pageName = "sign"
                model.addAttribute("title", "sign failed")
            }
        } catch (e: Exception) {
            val cryptoPass = crypto(password)
            UserRepository.save(User(userId, cryptoPass, nick))
            model.addAttribute("title", "sign success")
            pageName ="login"
        }

        return pageName
    }
    @PostMapping("/create_board")
    fun postCreateBoard(
        model: Model,
        @RequestParam(value = "title") title: String,
        @RequestParam(value = "des") des: String
    ): String {
        try {
            val db_user = UserRepository.findByUserId(login_user)
            BoardRepository.save(Board(db_user.nick, title, des, db_user.id, true))
        } catch (e:Exception){
            e.printStackTrace()
        }
        val db_user = UserRepository.findByUserId(login_user)
        model.addAttribute("title", "create board success")
        model.addAttribute("nick", db_user.nick)

        return "board_list"
    }
    @PostMapping("/delete_board")
    fun postDeleteBoard(
        model: Model,
        @RequestParam(value = "crud_id") crud_id: Long
    ): String {
        var pageName = ""
        try {
            val db_user = UserRepository.findByUserId(login_user)
            val db_board = BoardRepository.findBycrudid(crud_id)
            if(db_user.id == db_board.userid){
                BoardRepository.deleteById(crud_id)
                model.addAttribute("title", "delete board success")
                model.addAttribute("nick", db_user.nick)
                pageName = "board_list"
            } else{
                model.addAttribute("title", "delete board failed")
                pageName = "delete_board"
            }
        } catch (e:Exception){
            model.addAttribute("title", "delete board failed")
            pageName = "delete_board"
        }
        return pageName
    }
    @PostMapping("/board_list")
    fun postReadBoard(
        model: Model,
        @RequestParam(value = "read_id") read_id:Long
    ): String {
        var pageName=""
        try{
            val db_user = UserRepository.findByUserId(login_user)
            val db_board = BoardRepository.findBycrudid(read_id)
            if(db_board.look == true || db_board.userid == db_user.id){
                pageName = "read_board"
                read_board = read_id
                model.addAttribute("title", "read "+read_id)
                if(db_board.userid == db_user.id){
                    model.addAttribute("EDIT", "EDIT")
                } else{
                    model.addAttribute("EDIT", "")
                }
                model.addAttribute("nick", db_board.nick)
                model.addAttribute("b_title", db_board.title)
                model.addAttribute("des", db_board.des)
            }
            else{
                model.addAttribute("title", "not found read code")
                model.addAttribute("nick", UserRepository.findByUserId(login_user).nick)
                pageName="board_list"
            }
        } catch (e:Exception){
            model.addAttribute("title", "not found read code")
            model.addAttribute("nick", UserRepository.findByUserId(login_user).nick)
            pageName="board_list"
        }
        return pageName
    }
    @PostMapping("/edit_board")
    fun postEditBoard(
        model: Model,
        @RequestParam(value = "title") title: String,
        @RequestParam(value = "des") des: String,
        @RequestParam(value = "look_cheak") look_cheak: String
    ): String{
        try {
            val look: Boolean
            if(look_cheak == "all"){
                look = true
            } else{
                look = false
            }
            val db_user = UserRepository.findByUserId(login_user)
            BoardRepository.deleteById(read_board)
            BoardRepository.save(Board(db_user.nick, title, des, db_user.id, look))
        } catch (e:Exception){
            e.printStackTrace()
        }
        val db_user = UserRepository.findByUserId(login_user)
        model.addAttribute("title", "edit board success")
        model.addAttribute("nick", db_user.nick)

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
            val db_user = UserRepository.findByUserId(userId)

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