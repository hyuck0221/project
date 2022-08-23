package com.hyuck.model.board

import org.jetbrains.annotations.NotNull
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Board(
    var nick:String,
    var title:String,
    var des:String,
    var userid:Long,
    var look:Boolean,
    @Id @GeneratedValue var crudid:Long
)