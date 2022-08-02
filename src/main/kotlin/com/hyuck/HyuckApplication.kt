package com.hyuck

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HyuckApplication

fun main(args: Array<String>) {
	runApplication<HyuckApplication>(*args)
}
