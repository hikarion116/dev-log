import java.time.LocalDate

object Main:

  def main(args: Array[String]): Unit =

    try
      println("Starting")

      val entry = DevLogEntry.create(
        title = "",
        description = "Understanding exception handling",
        date = LocalDate.now()
      )

      println(s"Entry created: ${entry.title}")

    catch
      case e: IllegalArgumentException =>
        println(s"Validation failed: ${e.getMessage}")

    finally
      println("Final block")

    println("Program continues")
