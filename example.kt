import kotlin.system.exitProcess
import kargv.parse

data class Options(
  var help: Boolean = false,
  var port: Int? = 3000,
  var config: String? = null,
  var version: Boolean = false,
  var `--`: Array<String?> = emptyArray()
)

val VERSION = "0.0.1"
val USAGE = "usage: example.kexe [-hV] [options]"
val OPTIONS = """
where options can be:
  -h, --help          Show this message
  -V, --version       Output program version
  -p, --port <port>   Port that program should listen on
  -c, --config <path> Path to a configuration file
"""

fun main(argv: Array<String>) {
  val opts = Options()

  try {
    parse(argv, opts) { o, node ->
      val value = node.value
      when (node.name) {
        "h", "help" -> o.help = true
        "p", "port" -> o.port = value?.toInt()
        "c", "config" -> o.config = value
        "V", "version" -> o.version = true
        // handle rest arguments and args after `--`
        else ->
          if (null == node.name) {
            o.`--` += value
          } else {
            throw Error("unknown option: -${node.name}")
          }
      }
    }
  } catch (err: Error) {
    println("error: ${err.message}")
    println(USAGE)
    exitProcess(1)
  }

  if (true == opts.help) {
    println(USAGE)
    println(OPTIONS)
    exitProcess(0)
  }

  if (true == opts.version) {
    println(VERSION)
    exitProcess(0)
  }

  println("""
    port = ${opts.port}
    config = ${opts.config}
    arguments = ${opts.`--`.joinToString(" ")}
  """)
}
