import datkt.tape.test

import kargv.parse

fun main(argv: Array<String>) {
  test("""
    fun <T : Any> parse(
      argv: Array<String>,
      model: T,
      handler: (T, Node) -> Any?
    ): T? """.trimMargin().trimIndent().replace("\n", "")) { t ->
    class Model(
      var a: String = "",
      var b: String = "",
      var c: Boolean = false,
      var d: Int = 0,
      var option: String = "",
      var number: Int = 0,
      var key: String = "",
      var list: Array<String> = emptyArray(),
      var args: Array<String> = emptyArray()
    )

    var rest = arrayOf("the", "rest", "of", "the", "arguments")
    val values = Model(
      a = "hello",
      b = "world",
      c = true,
      d = 2,
      option = "value",
      number = 1234,
      key = "value",
      list = arrayOf("x", "y", "z"),
      args = rest
    )

    val args = arrayOf(
      "-a", "hello",
      "-b", "world",
      "-c",
      "-d",
      "-d",
      "--option", "value",
      "--number", "1234",
      "--key=value",
      "--list", "x",
      "--list", "y",
      "--list", "z",
      *rest,
      "--",
      "fefe"
    )

    val model = Model()
    parse(args, model) { m, node ->
      val value = node.value

      when (node.name) {
        // strings
        "a" -> m.a = if (null != value) value else m.a
        "b" -> m.b = if (null != value) value else m.b
        "key" -> m.key = if (null != value) value else m.key
        "option" -> m.option = if (null != value) value else m.option

        // boolean
        "c" -> m.c = true

        // numbers
        "d" -> {
          m.d = if (null != value) value.toInt() else ++m.d
        }

        "number" -> {
          m.number = if (null != value) value.toInt() else ++m.number
        }

        // array
        "list" -> {
          m.list = if (null != value) m.list + value else m.list
        }

        // otherwise/fallback
        else -> {
          m.args = if (null != value) m.args + value else m.args
        }
      }
    }

    t.equal(model.a, values.a, "model.a == values.a")
    t.equal(model.b, values.b, "model.b == values.b")
    t.equal(model.c, values.c, "model.c == values.c")
    t.equal(model.d, values.d, "model.d == values.d")
    t.equal(model.number, values.number, "model.number == values.number")
    t.equal(model.option, values.option, "model.option == values.option")
    t.equal(model.key, values.key, "model.key == values.key")

    rest += "fefe"
    model.args.forEachIndexed { i, arg -> t.equal(arg, rest[i]) }
    model.list.forEachIndexed { i, arg -> t.equal(arg, values.list[i]) }

    t.end()
  }
}
