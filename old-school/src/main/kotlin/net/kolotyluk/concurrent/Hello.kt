package net.kolotyluk.concurrent

import java.util.concurrent.ExecutorService

fun main(args: Array<String>) {
    println("Hello, World")
}

private fun ExecutorService.use(block: (ExecutorService) -> Unit) {

}