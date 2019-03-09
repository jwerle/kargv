package kargv

data class Node(
  val name: String? = null,
  val source: String,
  val value: String? = null
)

fun <T : Any> parse(argv: Array<String>, out: T, cb: (T, Node) -> Any?): T {
  var pending: Array<String> = emptyArray()
  var index = 0
  var error: Error? = null
  val max = argv.count()
  var rest = false

  fun peek(): String? {
    if (pending.count() > 0) {
      return pending[0]
    } else if (index < max) {
      return argv[index]
    } else {
      return null
    }
  }

  fun read(): String? {
    if (null != peek()) {
      if (pending.count() > 0) {
        val value = pending[0]
        pending = pending.sliceArray(1..pending.count() - 1)
        return value
      }
      return argv[index++]
    } else {
      return null
    }
  }

  fun visit(): Node? {
    var node: Node? = null
    if (null != peek() && null == error) {
      var opt = read()
      var value: String? = null

      if (null == opt) {
        return null
      }

      if ('-' == opt[0]) {
        val off = if ('-' == opt[0] && '-' == opt[1]) 2 else 1

        if (2 == off && "--" == opt) {
          rest = true
          return visit()
        }

        if ('-' == opt[0] && '-' != opt[1] && opt.length > 2) {
          val flags = opt.substring(1).split("").filter { c -> c.length > 0 }
          opt = "-${flags[0]}"
          for (i in 1..flags.count() - 1) {
            pending += flags[i]
          }
        } else {
          val parts = opt.split('=')
          val next = peek()

          if (parts.count() > 1) {
            value = parts[1]
            opt = parts[0]
          } else if (null != next && '-' != next[0]) {
            value = read()
          }
        }

        if (true == rest) {
          return Node(null, opt, opt)
        }

        if (null != value && '-' != value[0]) {
          node = Node(opt.substring(off), opt, value)
        } else {
          node = Node(opt.substring(off), opt)
        }
      } else {
        node = Node(null, opt, opt)
      }
    }

    return node
  }

  do {
    val node = visit()

    if (null == node) {
      break
    }

    cb(out, node)

  } while (index < max)

  return out
}
