package net.kolotyluk.loom

import java.util.concurrent.ExecutorService

object LoomUtilities {

    fun ExecutorService.use(block: (executorService: ExecutorService) -> Unit) = block(this)

}

