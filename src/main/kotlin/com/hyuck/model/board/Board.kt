package com.hyuck.model.board

import org.jetbrains.annotations.NotNull
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Board(
    var title:String = "",
    var des:String = "",
    var userid:Long = 0,
    var look:Boolean = true,
    @Id @GeneratedValue var id:Long = 0
)