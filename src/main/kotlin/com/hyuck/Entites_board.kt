package com.hyuck

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Board(
    var nick:String,
    var title:String,
    var des:String,
    var user_id:Int,
    @Id @GeneratedValue var crud_id:Long?=null
)