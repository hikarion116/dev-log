import java.time.LocalDate

case class DevLogEntry(
                        title: String,
                        description: String,
                        date: LocalDate,
                        tags: Set[String]
                      ):

  // Validate when a new instance is created
  require(title.trim.nonEmpty, "Title must not be empty")

  // Return a new instance with an additional tag
  def addTag(newTag: String): DevLogEntry =
    copy(tags = tags + newTag)

  // Return a new instance with the tag removed
  def removeTag(tag: String): DevLogEntry =
    copy(tags = tags - tag)


object DevLogEntry:

  def create(
              title: String,
              description: String,
              date: LocalDate
            ): DevLogEntry =
    DevLogEntry(title, description, date, Set.empty[String])