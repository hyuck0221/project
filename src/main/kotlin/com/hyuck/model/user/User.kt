package com.hyuck.model.user

import org.jetbrains.annotations.NotNull
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class User(
    var userId:String = "",
    var password:String = "",
    var nick:String = "",
    @Id @GeneratedValue var id:Long = 0
)