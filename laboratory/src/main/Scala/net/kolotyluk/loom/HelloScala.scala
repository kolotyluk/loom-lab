// @main def hello = println("Hello, world!")

package net.kolotyluk.loom

import java.time.Instant
import java.util.concurrent.StructuredExecutor
import scala.util.Using

object HelloScala {
  def main(args: Array[String]): Unit = {
    Context.printHeader(HelloScala.getClass)

    val results =
      Using(StructuredExecutor.open("HelloScala")) { structuredExecutor =>
        val futureResults = (0 to 15).map { item =>
          println(s"item = $item, Thread ID = ${Thread.currentThread}")
          structuredExecutor.fork { () =>
            println(s"\ttask = $item, Thread ID = ${Thread.currentThread}")
            item
          }
        }
        structuredExecutor.join
        futureResults.map(_.get)
      }

    println(results)
  }
}