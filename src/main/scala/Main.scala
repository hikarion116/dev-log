import java.time.LocalDate

object Main:
  def main(args: Array[String]): Unit =

    val entry = DevLogEntry.create(
      "Learning Scala",
      "Testing validation",
      LocalDate.now()
    )

    // 1. Valid entry
    assert(entry.title == "Learning Scala")

    // 2. Empty title
    val emptyTitleThrows = try
      DevLogEntry.create("", "Testing", LocalDate.now())
      false
    catch
      case _: IllegalArgumentException => true

    assert(emptyTitleThrows)

    // 3. Whitespace-only title
    val whitespaceThrows = try
      DevLogEntry.create("   ", "Testing", LocalDate.now())
      false
    catch
      case _: IllegalArgumentException => true

    assert(whitespaceThrows)

    // 4. Invalid copy
    val copyThrows = try
      entry.copy(title = "")
      false
    catch
      case _: IllegalArgumentException => true

    assert(copyThrows)

    // Original entry remains unchanged
    assert(entry.title == "Learning Scala")

    println("All assertions passed!")
