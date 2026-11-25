# Dev Log — Project Progress & Learning Notes

**Project:** `dev-log`  
**Language:** Scala 3  
**Last updated:** 24 November 2026  
**Purpose:** Build a useful development log while learning Scala design and experimenting with human–AI pair programming.

**Reading order:** Newest learning entries first. Insert future learning entries directly above the most recent learning entry, keep the current-position summary and next learning direction at the top, and preserve older entries below. The original section numbers are retained as historical identifiers, not reading order.

**Current position (24 November 2026):** The Scala 3 model, immutable tag operations and companion `create()` factory are implemented. Exercise 4 introduced the title invariant and verified the invalid-title exception flow locally on 23 November. Exercise 5 adds a supplied `Main.scala` example showing how `try/catch` returns a Boolean that `assert` can check, including whitespace and invalid-copy cases. This latest version has been reviewed but **not confirmed executed**. Next concept: `Try` / `Success` / `Failure`; sbt-based unit tests can be studied later. Commit and push status for Exercise 5 remain unconfirmed.

## 11. Next learning direction — not started

Move on to a new concept rather than repeating these checks. A natural candidate is **`Try`, `Success`, and `Failure`**, comparing failures represented as values with the current `try/catch` expression. The dedicated sbt test suite can wait until it becomes useful for the application.

- [Scala API — `Try`](https://www.scala-lang.org/api/3.x/scala/util/Try.html) — **What to look for:** `Try.apply`, `Success`, and `Failure`, and which exceptions are caught.

## 10. Exercise 5 — assert an expected exception using a Boolean `try/catch` expression (code prepared and reviewed)

**Date:** 24 November 2026  
**Status:** The `Main.scala` example was supplied in the 24 November upload and reviewed against the existing title invariant. **A local `sbt run` result was not supplied for this version**, so its execution remains unverified.  
**Scope:** A short learning exercise, not a new production feature or a dedicated test suite.

### The useful discovery

Previously, the exercise used `try/catch` only to print an exception. Today I learned that **Scala's `try/catch` is an expression that returns a value**. That lets me store the outcome as a `Boolean`, then verify the expected exception using `assert`.

```scala
val emptyTitleThrows = try
  DevLogEntry.create("", "Testing", LocalDate.now())
  false
catch
  case _: IllegalArgumentException => true

assert(emptyTitleThrows)
```

- **If construction succeeds**, execution reaches `false`. The assertion then fails: the expected exception did not happen.
- **If construction throws `IllegalArgumentException`**, the matching `catch` evaluates to `true`, so the assertion passes.
- **If another exception occurs**, this `catch` does not handle it; it propagates rather than being mistaken for a successful test.
- Scala infers `emptyTitleThrows: Boolean` because the normal and caught paths both produce a Boolean.
- `_` in the `catch` pattern means the exception is matched by type but its value is not needed.

**Key takeaway:** A `try/catch` can be used to *produce a value*, not just perform side effects. The `false` after the constructor is important: it marks an unexpected success as a failed check.

### Submitted `Main.scala`

The uploaded code checks a valid entry, an empty title, a whitespace-only title, and an invalid `copy`. It also checks that the original entry remains unchanged.

```scala
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
```

**Verification boundary:** The example is consistent with our existing `require(title.trim.nonEmpty, ...)` rule, but the latest uploaded `Main.scala` has not been accompanied by an execution result. It does **not** separately check direct `DevLogEntry(...)` construction, and no new testing library has been introduced.

### Must know / Good to know / Revisit later

- **Must know:** `try/catch` returns the value of the completed `try` block or the matching `catch` branch; `assert` checks the resulting Boolean.
- **Good to know:** An unexpected exception type propagates; `case _: IllegalArgumentException` matches without binding the exception; `assert` can detect when an expected exception never occurred.
- **Revisit later:** `Try`, `Success`, `Failure`; unit-testing frameworks and `sbt test`; direct `apply` checks if they become useful. No need to rewrite near-identical examples just for repetition.

### Learning approach refined today

Because this learning happens after work, spend hands-on time on **genuinely new insights or design questions**. For very similar cases, provide complete code and explain only what is new. Keep the Dev Log moving without treating repetitive typing as progress.

### Documentation

- [Scala 3 Book — `try` / `catch` / `finally`](https://docs.scala-lang.org/scala3/book/control-structures.html#try-catch-finally) — **What to look for:** how `try/catch` forms an expression whose branches return values.
- [Scala API — `Predef.assert`](https://www.scala-lang.org/api/3.x/scala/Predef$.html) — **What to look for:** what happens when an assertion condition is false.
- [Scala 3 Book — Building and Testing with sbt](https://docs.scala-lang.org/scala3/book/tools-sbt.html) — **Read later:** how to organise tests outside `Main.scala` and run them using `sbt test`.

### Suggested Git milestone

```text
test: check title validation with try-catch assertions
```

Commit the updated `../src/main/scala/Main.scala` and `docs/dev-log-progress.md` after local review/verification. Do not record a push or successful run until it is confirmed.

## 9. Exercise 4 — class invariants with `require` and exception handling (implemented; invalid-title path verified)

**Date reviewed:** 23 November 2026  
**Code review:** The supplied `DevLogEntry.scala` and `Main.scala` were reviewed in chat.  
**Local verification:** **Confirmed by my `sbt run` output** for an empty title through `DevLogEntry.create`.  
**Not yet verified by execution in this exercise:** valid titles, whitespace-only titles, invalid `copy()` and direct `apply()` calls.  
**Commit/push:** Suggested below; not confirmed.

### Goal and rule

Prevent successfully constructing a `DevLogEntry` whose `title` is empty or contains only ordinary spaces. The rule should apply across construction routes, not just the custom companion factory.

My implementation places this expression **inside the case-class body**, outside any method:

```scala
require(title.trim.nonEmpty, "Title must not be empty")
```

The chain works as follows:

| Part | Purpose |
| --- | --- |
| `title` | The input field being checked |
| `.trim` | Removes leading/trailing whitespace (including normal spaces) |
| `.nonEmpty` | Produces `Boolean`; `false` for an empty trimmed result |
| `require(condition, message)` | Allows construction to continue if `true`; throws `IllegalArgumentException` if `false` |

**Design decision:** This is a **class invariant** — every successfully constructed `DevLogEntry` has a title satisfying the rule. Other fields have **not** been given new validation rules. In particular, an empty `tags` set remains intentional because our `create()` factory starts entries without tags.

### Submitted implementation — `DevLogEntry.scala`

```scala
import java.time.LocalDate

case class DevLogEntry(
    title: String,
    description: String,
    date: LocalDate,
    tags: Set[String]
):
  // This is class-body code, executed during construction.
  require(title.trim.nonEmpty, "Title must not be empty")

  def addTag(newTag: String): DevLogEntry =
    copy(tags = tags + newTag)

  def removeTag(tag: String): DevLogEntry =
    copy(tags = tags - tag)

object DevLogEntry:
  def create(
      title: String,
      description: String,
      date: LocalDate
  ): DevLogEntry =
    DevLogEntry(title, description, date, Set.empty[String])
```

This snippet reformats indentation for the note; it does not change the behaviour of my submitted code.

### Why the class body, not `apply()`?

I initially considered overriding or redefining the companion object's generated `apply()`. The key distinction discovered through a `println` experiment was:

- `DevLogEntry(...)` calls the case-class construction path.
- `DevLogEntry.create(...)` delegates to `DevLogEntry(...)` in my factory.
- `entry.copy(...)` constructs a **new** case-class instance rather than modifying the original; it does not have to call the companion object's `apply()`.
- Code in the **class body** executes as each new instance is initialised, so the invariant covers these construction paths.

**Important:** `apply()` is a way to construct the value, but it is not the single validation boundary for all new instances. We did **not** override `apply()`.

### `Predef`, overloaded `require`, and `final def`

I read [Scala 3 API — `Predef`](https://www.scala-lang.org/api/3.x/scala/Predef$.html) and learned:

- `Predef` is a Scala standard-library singleton object whose members are automatically imported in normal Scala source files. `println(...)` and `Predef.println(...)` normally call the same method; likewise for `require(...)`.
- There is an overload `require(requirement: Boolean)` and another that takes a message, `require(requirement: Boolean, message: => Any)`. I used the **two-argument** form.
- `final def` means the method cannot be overridden. It does **not** mean it must be called last, and it is not the reason the message overload exists.
- The `=> Any` message parameter is **by-name**. Its expression need not be evaluated unless it is needed for a failed requirement; deeper details can wait.

### Submitted experiment — `Main.scala`

```scala
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
```

### Verified local output — `sbt run` (23 November 2026)

```text
Starting
Validation failed: requirement failed: Title must not be empty
Final block
Program continues
```

**What this confirms:**

1. `Starting` prints before constructing the entry.
2. The empty title fails `require(...)` and throws `IllegalArgumentException`.
3. `catch` matches that exception and prints its `.getMessage`; `require`'s standard exception message includes the prefix `requirement failed:`.
4. `println(s"Entry created: ...")` does **not** run because the value was not successfully constructed.
5. `finally` runs when leaving the `try`/`catch`.
6. Because the exception was handled, execution continues to `Program continues`.

**A key distinction:** `finally` is not the same thing as `catch`. Catching handles a matching exception; `finally` is useful for cleanup that should occur when leaving the `try`/`catch` whether the operation succeeds or fails. For this learning example, it simply prints. Production code generally should not add a `try/catch` around every `require()` or use `finally` for normal success messages.

### Predictions made during the exercise

- An invalid `entry.copy(title = "")` should throw `IllegalArgumentException`; the original immutable entry remains unchanged. **Reasoned through, but not separately executed in the latest submitted `Main.scala`.**
- An unhandled exception would stop normal execution at the failed line; later statements would not run.
- A valid `DevLogEntry.create(title = "Learning Scala", ...)` should skip `catch`, execute `finally`, and continue. **Predicted, not shown in the verified output above.**

### Must know / Good to know / Revisit later

**Must know**

- `require()` checks a **Boolean** and throws `IllegalArgumentException` on failure.
- Case-class **class-body** code runs during instance construction; it is the right location for this rule.
- `copy()` creates a new instance and leaves the original unchanged.
- `try` contains code that might throw, `catch` handles matching exceptions, and `finally` executes as control leaves `try`/`catch` under normal exception handling.
- If a matching `catch` handles the exception, execution can continue after the `try`/`catch`/`finally` statement.

**Good to know**

- `Predef` supplies commonly used automatically imported methods, including `require`, `assert` and `println`.
- `require(condition, message)` has a message overload; Scala documentation marks that overload `final def` (not overridable).
- A helpful error message is better than a vague message such as `"empty string"`.
- `e.getMessage` can show the specific reason for a caught exception.
- An invariant inside a class is different from validation performed only in an optional factory method.

**Revisit later**

- A dedicated test suite for invalid titles created by `apply()`, `create()` and `copy()`, plus a successful valid-title case.
- Difference between `require` (invalid arguments), `assert` (internal assumptions), and `ensuring` (postconditions).
- `Try` / `Success` / `Failure`, `Either`, and modelling expected input-validation failures without exceptions.
- When catching `IllegalArgumentException` is appropriate at an application boundary.
- By-name parameters (`=> Any`) in more depth, and why `finally` is typically used for resource cleanup.

### Related official documentation

- [Scala API — `Predef` (`require`, `assert`, `println`)](https://www.scala-lang.org/api/3.x/scala/Predef$.html) — method signatures and error behaviour.  
  **What to look for:** Two `require` overloads, their parameter lists, `final def`, and the return type `Unit`.
- [Scala 3 Book — Classes: fields and methods](https://docs.scala-lang.org/scala3/book/domain-modeling-tools.html#fields-and-methods) — class-body statements and initialisation.  
  **What to look for:** The `println("initialization begins")` example, and when it executes.
- [Scala 3 Book — Case classes](https://docs.scala-lang.org/scala3/book/domain-modeling-tools.html#case-classes) — generated methods and `copy()`.  
  **What to look for:** Why `copy()` creates a new case-class instance rather than changing an existing one.
- [Scala 3 Book — `try` / `catch` / `finally`](https://docs.scala-lang.org/scala3/book/control-structures.html#try-catch-finally) — exception handling.  
  **What to look for:** The order of statements when an exception is thrown and caught, and what `finally` does.

### Commit milestone

Suggested commit message (do not mark as committed/pushed until Git confirms it):

```text
feat: validate dev log titles with require
```

Stage `../src/main/scala/DevLogEntry.scala`, `src/main/scala/Main.scala`, and `docs/dev-log-progress.md` for this milestone. Earlier Exercises 2 and 3 have historical commit suggestions in this note; their Git status still needs checking against actual repository history.

## 8. Exercise 3 — companion-object factory and assertions (implemented and reviewed)

**Date reviewed:** 18 November 2026  
**Code review:** Passed for the submitted implementation and updated assertions.  
**Local verification:** Not confirmed in chat; run `sbt run` and check for `All assertions passed!`.  
**Commit/push:** Proposed below; not yet confirmed.

### Problem

Create a new Dev Log entry with an empty tag set without making every caller supply `Set.empty[String]`. This lets us compare methods on an existing instance with a method on its companion object.

### Implementation — `DevLogEntry.scala`

```scala
import java.time.LocalDate

case class DevLogEntry(
    title: String,
    description: String,
    date: LocalDate,
    tags: Set[String]
):
  def addTag(newTag: String): DevLogEntry =
    this.copy(tags = this.tags + newTag)

  def removeTag(tag: String): DevLogEntry =
    this.copy(tags = this.tags - tag)

object DevLogEntry:
  def create(title: String, description: String, date: LocalDate): DevLogEntry =
    DevLogEntry(title, description, date, Set.empty[String])
```

The implementation has an explicit `: DevLogEntry` result type. `create` is on the **companion object** (`object DevLogEntry`), while `addTag` and `removeTag` remain **instance methods**. The factory adds a convenient creation path; it does not disable the case class's generated `apply` constructor.

### Checks — `Main.scala`

My latest exercise solution uses:

```scala
import java.time.LocalDate

object Main {
  def main(args: Array[String]): Unit = {
    val entry = DevLogEntry.create(
      "Learning Scala",
      "Understanding companion objects",
      LocalDate.now()
    )

    val updated = entry.addTag("Scala")

    assert(entry.title == "Learning Scala")
    assert(entry.description == "Understanding companion objects")
    assert(entry.tags.isEmpty)

    assert(updated.tags == Set("Scala"))
    assert(entry.tags.isEmpty)

    println("All assertions passed!")
  }
}
```

These checks cover the title, description, initial empty tags, successful addition of a tag, and the original entry remaining unchanged. **They do not yet explicitly assert the date**; if that becomes important, capture `LocalDate.now()` in a variable, pass it to `create`, and assert `entry.date == thatDate`. No local execution result has been reported.

### Design discussion — the trade-off I noticed

> If we know a new Dev Log should start with no tags, the factory saves us from repeatedly passing an empty Set. But our `create` method cannot accept initial tags, so it is less flexible for that use case.

That is correct **for the current `create` signature**. Callers may still use the generated case-class constructor to supply tags directly. An alternative would be to give `tags` a default value in the case-class constructor:

```scala
case class DevLogEntry(
    title: String,
    description: String,
    date: LocalDate,
    tags: Set[String] = Set.empty[String]
)
```

Default arguments can make the simple creation case more flexible, without a separate custom `create` method. **This was discussed as an alternative; it has not been implemented in the submitted solution.**

### Design discussion — where should validation live?

My answer to the follow-up design question was:

> Put a rule such as "the title must not be empty" in the case class, because a caller can bypass validation in `create` by using the generated case-class `apply` method directly.

This reasoning is sound. With a public case-class constructor, a check only in the factory does not protect every construction path. A possible **future exercise** is a class-body invariant using `require`, such as `require(title.trim.nonEmpty, "Title must not be empty")`, which would run when instances are constructed through the public constructor, generated `apply`, or `copy`. **No validation rule has been added yet.** We should also discuss when throwing an exception is appropriate versus returning a validation result.

### Must know / Good to know / Revisit later

- **Must know:** An instance method works on one entry; a companion object is a singleton with the same name as its class, declared alongside it; the case-class constructor remains available even after adding `create`.
- **Good to know:** A companion factory can apply standard initial values; constructor default parameters may be simpler; public methods can state their return type explicitly.
- **Revisit later:** Invariants with `require`, validation errors as `Either`, controlled constructors, proper automated unit tests, and record identity.

### Documentation

- [Scala 3 Book — Companion Objects](https://docs.scala-lang.org/scala3/book/domain-modeling-tools.html#companion-objects) — companion object naming and the relationship to the class.
- [Scala 3 Book — Singleton Objects](https://docs.scala-lang.org/scala3/book/taste-objects.html) — why `object` methods do not require a separate instance.
- [Scala 3 Book — Methods](https://docs.scala-lang.org/scala3/book/taste-methods.html) — methods on classes and objects.
- [Scala 3 Book — Default Parameter Values](https://docs.scala-lang.org/scala3/book/methods-most.html#default-parameter-values) — an alternative to a factory for an optional initial tag set.
- [Scala 3 Book — Case Classes](https://docs.scala-lang.org/scala3/book/domain-modeling-tools.html#case-classes) — generated construction and `copy()` behaviour.

**What to look for:** Identify which call uses an existing `entry`, which call creates a new entry using `DevLogEntry.create(...)`, and which call uses the case class's generated `DevLogEntry(...)`. Notice that a convenient factory does not automatically enforce a rule on every construction path.

### Suggested Git milestone

After verifying the code locally, commit the companion factory, updated `Main.scala` assertions, and the progress notes with:

```text
feat: add companion factory for dev log entries
```

Only record this as committed and pushed after Git confirms it. The earlier Exercise 2 commit is still unconfirmed in the supplied notes; verify Git history rather than assuming it was pushed.

## 7. Exercise 2 — immutable `removeTag` and `assert` checks (completed and reviewed)

**Date reviewed:** 17 November 2026  
**Code review:** Passed; the following was supplied in chat after the ZIP upload.  
**Verification note:** The assertions are written and reviewed, but a successful local `sbt run` has not been confirmed in chat.  
**Commit:** Ready to make; not yet confirmed as pushed.

### Problem and solution

Remove a tag while preserving the original entry. A nonexistent tag should be harmless; it should leave the set's elements unchanged.

```scala
def removeTag(tag: String): DevLogEntry =
  this.copy(tags = this.tags - tag)
```

- `tags - tag` creates a Set without the specified element; it does not modify the original immutable Set.
- `copy(tags = ...)` creates a new `DevLogEntry` and preserves `title`, `description` and `date`.
- Removing a missing tag returns a Set with the same elements; no `if`, `Option` or exception handling is needed for this simple case.
- Removing the last remaining tag produces `Set.empty[String]`.

`addTag` remains:

```scala
def addTag(newTag: String): DevLogEntry =
  this.copy(tags = this.tags + newTag)
```

### Checking behaviour with `assert` in `Main.scala`

The following is the test example I wrote. It checks the expected results automatically rather than relying only on printed output:

```scala
import java.time.LocalDate

object Main {
  def main(args: Array[String]): Unit = {
    val entry = DevLogEntry(
      title = "My First Dev Log",
      description = "Testing development across two Macs",
      date = LocalDate.now(),
      tags = Set("Scala", "Git")
    )

    val updatedEntry = entry.addTag("GitHub")
    val updated = entry.removeTag("Git")
    val empty = updated.removeTag("Scala")
    val unchanged = entry.removeTag("new")

    // Original entry
    assert(entry.tags == Set("Scala", "Git"))

    // Add a new tag
    assert(updatedEntry.tags == Set("Scala", "Git", "GitHub"))

    // Remove an existing tag
    assert(updated.tags == Set("Scala"))

    // Remove the last tag
    assert(empty.tags == Set.empty[String])

    // Removing a missing tag preserves the value
    assert(unchanged == entry)

    // Original entry is still unchanged
    assert(entry.tags == Set("Scala", "Git"))

    println("All assertions passed!")
  }
}
```

### What is `assert`?

`assert(condition)` verifies that a Boolean expression is `true`:

```scala
assert(updated.tags == Set("Scala"))
```

- If the condition is `true`, the program continues.
- If it is `false`, `assert` throws an `AssertionError`; the success message at the end won't be reached.
- These are useful **manual application checks**, not yet a dedicated automated test suite.
- The two checks of `entry.tags` are intentionally redundant for learning: they reinforce that operations on copies have not changed the original.

**Local verification to do before committing:** run `sbt run` and confirm the message `All assertions passed!` appears without errors. The chat review alone does not prove the code was executed.

### Explanation in my own words

> `entry.removeTag("Git")` returns a new copy with one fewer tag. The original `entry` is not modified. `val` prevents reassignment, while the immutable Set and the `copy()` operation are what give this example its immutable update behaviour.

### Documentation

- [Scala 3 Book — Deleting elements from a Set](https://docs.scala-lang.org/scala3/book/collections-classes.html#deleting-elements-from-a-set) — the `-` operation and Set behaviour.
- [Scala 3 Book — Immutable Values](https://docs.scala-lang.org/scala3/book/fp-immutable-values.html) — creating updated copies without modifying the original.
- [Scala 3 Book — Building and Testing with sbt](https://docs.scala-lang.org/scala3/book/tools-sbt.html) — examples of assertions in a test workflow and the next step beyond checks in `Main`.

**What to look for:** Why `-` does not mutate an immutable Set; why `copy()` returns a new case-class instance; what `assert` does if a condition is false.

### Commit milestone

Planned commit message:

```text
feat: add immutable tag removal to DevLogEntry
```

Include the implementation, `Main.scala` checks and this progress note in the commit. Do not mark the commit as pushed until Git confirms it.

## 6. What to learn now vs later

### Must know

- What a `case class` provides automatically.
- How value equality (`==`) works for case classes.
- Why `copy()` plus immutable collections leaves the original unchanged.
- How `Set` operators `+` (add) and `-` (remove) produce updated immutable collections.
- The distinction between `val` and an immutable object or collection.
- How simple `assert` checks verify expected behaviour and fail with `AssertionError` when a condition is false.
- The difference between an **instance method** (`entry.addTag`) and a method on an **object** (`DevLogService.addTag`).
- What `this` means inside an instance method.
- The distinction between an instance method, singleton object, and companion object.
- Why validation only inside a factory can be bypassed by another public construction path.

### Good to know

- Why `Set` is useful when duplicate tags are not wanted.
- Why explicit return types improve method readability.
- How methods can delegate to one another without duplicating logic.
- Why behaviour working solely on an entry's data may belong on the entry itself.
- Why a companion factory can supply default values and why constructor defaults may sometimes be simpler.
- How Scala 3 infers a return type, and why an explicit return type on a public method can help readers.

### Revisit later

- Value equality vs persistent record identity and unique IDs.
- Factories that enforce construction rules, and alternative ways of designing creation APIs.
- Services, repositories and dependency injection.
- Traits, composition and abstraction boundaries.
- A dedicated test suite (for example, using a Scala testing framework) and validation rules (for example, empty titles).

## 5. Moving behaviour into the model — implemented

I implemented `addTag` inside `DevLogEntry`:

```scala
import java.time.LocalDate

case class DevLogEntry(
    title: String,
    description: String,
    date: LocalDate,
    tags: Set[String]
):
  def addTag(newTag: String): DevLogEntry =
    this.copy(tags = this.tags + newTag)
```

The explicit `: DevLogEntry` return type is a clarity improvement suggested during review; my original implementation relied on Scala's return-type inference.

Usage:

```scala
val withGitHub = entry.addTag("GitHub")
val stillOneGitHub = withGitHub.addTag("GitHub")
```

Because `tags` is a `Set`, adding the same string twice does not create a duplicate. The original `entry` is unchanged.

### Case-class method vs service object

We considered two designs:

```scala
// Approach A: behaviour on an instance
entry.addTag("GitHub")

// Approach B: behaviour in a separate singleton object
DevLogService.addTag(entry, "GitHub")
```

An example implementation of Approach B is:

```scala
object DevLogService {
  def addTag(entry: DevLogEntry, newTag: String): DevLogEntry =
    entry.addTag(newTag)
}
```

**Decision for now:** Keep simple behaviour that only transforms the entry's own data in `DevLogEntry`. A service becomes more useful when an operation coordinates external dependencies or other responsibilities, for example saving an entry to a database. Just needing a `DevLogEntry` argument does *not*, by itself, prove that the method must be on the class.

**Status clarification:** `DevLogService.scala` is present in the uploaded project ZIP, and it delegates to `entry.addTag(newTag)`. We can keep it as an example of a separate singleton object, but it is not required for this simple operation.

### Related documentation

- [Scala 3 Book — Methods](https://docs.scala-lang.org/scala3/book/taste-methods.html) — methods inside case classes and other types.
- [Scala 3 Book — Fields and methods](https://docs.scala-lang.org/scala3/book/domain-modeling-tools.html#fields-and-methods) — instance behaviour in a class body.
- [Scala 3 Book — Singleton Objects](https://docs.scala-lang.org/scala3/book/taste-objects.html) — the alternative of placing methods in an `object`.

**What to look for:** Where a method can be declared and how an instance method differs from a method in a singleton object. Keeping `addTag` on `DevLogEntry` is **our design decision**, not a rule imposed by these docs.

## 4. Immutability and `copy()` — completed experiment

We experimented with:

```scala
val updatedEntry = entry.copy(
  tags = entry.tags + "GitHub"
)
```

Correct predictions:

- `entry.tags` stays unchanged.
- `updatedEntry.tags` includes the extra tag.
- `entry == updatedEntry` is `false` when their tags differ.

**Lesson:** Immutability does not mean the application cannot represent changes. Instead of changing an existing value, we create a new value with the updated state.

**`val` clarification (17 November 2026):** `val entry` prevents assigning a different value to the variable `entry`, but `val` alone does not make a referenced object deeply immutable. Here, case-class parameters are `val` by default, `tags` is an immutable `Set[String]`, and `copy()` creates a new entry rather than changing the existing one.

### Related documentation

- [Scala 3 Book — Immutable Values](https://docs.scala-lang.org/scala3/book/fp-immutable-values.html) — case classes and the “update as you copy” pattern.
- [Scala 3 Book — Adding elements to a Set](https://docs.scala-lang.org/scala3/book/collections-classes.html#adding-elements-to-a-set) — returning a new immutable collection with `+`.

**What to look for:** Which value is unchanged after `copy()`, and which operation produces a new `Set`?

## 3. First data model — completed

Created `DevLogEntry.scala`:

```scala
import java.time.LocalDate

case class DevLogEntry(
    title: String,
    description: String,
    date: LocalDate,
    tags: Set[String]
)
```

This describes a development-log entry using four fields:

- `title` and `description`: `String`
- `date`: `LocalDate`, rather than an arbitrary date string
- `tags`: immutable `Set[String]`, which prevents duplicate values

We created entries in `Main.scala`, printed them, and used Git to push the initial model. The commit was:

```text
feat: add initial dev log entry model
```

We also tested working with the repository across two Macs.

### Why a case class?

A case class is a useful default for this value-oriented model because it provides:

1. Construction without `new`.
2. Equality based on constructor field values.
3. Immutable constructor fields (`val`) by default.
4. `copy()` to create modified instances.
5. Readable `toString` output.

A regular `class` can be appropriate for other kinds of objects, but these features are not all supplied automatically in the same way.

**Important future distinction:** Two entries with identical fields compare equal as case-class values, but they might eventually be two distinct saved records. We will revisit **value equality vs record identity** when introducing IDs and persistence.

### Related documentation

- [Scala 3 Book — Case Classes](https://docs.scala-lang.org/scala3/book/domain-modeling-tools.html#case-classes) — generated methods, equality, `copy()` and constructor fields.
- [Scala 3 Book — Working with Sets](https://docs.scala-lang.org/scala3/book/collections-classes.html#working-with-sets) — why `Set` stores unique elements.

**What to look for:** Identify which case-class features Scala generates for us, and why `tags` is a `Set[String]` rather than a `List[String]`.

## 2. Project setup — completed

- Created the GitHub repository `dev-log` and opened the local directory with IntelliJ IDEA.
- Learned that `../.idea` is IntelliJ's project configuration directory, not application source code.
- Set up a simple **sbt + Scala 3** console application. Our original setup used `scalaVersion := "3.9.0"` in `../build.sbt` and `sbt.version=2.0.11` in `../project/build.properties` (these were the versions used in the setup exercise; check the files for their current values).
- Created `../src/main/scala/Main.scala` and successfully ran `sbt run`, which printed `Dev Log started`.
- Learned that sbt / IntelliJ may create generated directories such as `../project/project`, `../target` and `.bsp/`. They are not where application code goes.
- Added `../.gitignore` to exclude local IDE settings and generated build outputs.
- Committed and pushed the initial setup:

```text
chore: initialise Scala dev log project
```

Key files:

```text
dev-log/
├── .gitignore
├── build.sbt
├── project/
│   └── build.properties
└── src/
    └── main/
        └── scala/
            ├── Main.scala
            ├── DevLogEntry.scala
            └── DevLogService.scala
```

The `../src/test/scala` directory can be added or used when we begin unit testing. Empty directories do not appear in Git commits.

### Related documentation

- [The Book of sbt — Getting Started](https://www.scala-sbt.org/2.x/docs/en/guide/index.html) — sbt project structure, build definitions and commands.
- [Scala 3 Book — Main Methods](https://docs.scala-lang.org/scala3/book/methods-main-methods.html) — how a Scala program starts.

**What to look for:** How `../build.sbt`, `project/build.properties` and the application's entry point have different jobs.

## 1. Our development approach

This project is both an application and a learning experiment. I write and run the code; AI helps me reason about design, review decisions and explore alternatives. Initially, AI does not need terminal access.

For each small feature, follow this cycle:

1. Define the problem and its requirements.
2. Think about the design before coding.
3. Implement it myself.
4. Test and review it, comparing alternatives.
5. Commit a meaningful milestone and record what I learned.

For **every exercise**, keep the relevant documentation links next to the exercise (not only in a reference list at the end). Include a brief **“What to look for”** note so I can return to the source and find the concept independently. Prefer the official Scala 3 Book and link to a specific section whenever possible; use official tool documentation for setup exercises. Only record an implementation as complete after I have written and checked it. Distinguish a code-review pass from a build or test run that has been verified locally.

We use four recurring design questions:

| Foundation | Question |
| --- | --- |
| **Case class vs class** | How should we model data? |
| **Object vs class** | Where should behaviour live? |
| **Trait vs inheritance/composition** | How do we design for change? |
| **Collections** | How should we organise data? |

We will return to these questions as the application develops rather than treating them as isolated lessons.

## 12. Official Scala documentation — quick reference

- [Scala 3 Book — Case Classes](https://docs.scala-lang.org/scala3/book/domain-modeling-tools.html#case-classes)
- [Scala 3 Book — Methods](https://docs.scala-lang.org/scala3/book/taste-methods.html)
- [Scala 3 Book — Singleton Objects](https://docs.scala-lang.org/scala3/book/taste-objects.html)
- [Scala 3 Book — Collections](https://docs.scala-lang.org/scala3/book/collections-classes.html)
- [Scala 3 Book — Control Structures](https://docs.scala-lang.org/scala3/book/control-structures.html)
- [Scala 3 API — `Predef`](https://www.scala-lang.org/api/3.x/scala/Predef$.html)

**How to use these docs:** Begin with the direct documentation links and “What to look for” prompts for the current exercise. Return to this quick list for broader reference.
