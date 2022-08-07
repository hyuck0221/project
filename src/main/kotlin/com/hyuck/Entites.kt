package com.hyuck

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class User(
    var userId:String,
    var password:String,
    var nick:String,
    @Id @GeneratedValue var id:Long?=null
)

@Entity
class Board(
    var nick:String,
    var title:String,
    var des:String,
    var user_id:Long?=null,
    @Id @GeneratedValue var crud_id:Long?=null
)