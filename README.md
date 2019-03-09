kargv
=====

Minimal command line options parser for Kotlin/Native

## Installation

```sh
$ npm install kargv
```

## Usage

```kotlin
// declare model for options container
data class Opts(var arg: String? = null)
// create container for `parse()`
val opts = Opts()
try {
  // parse args in `argv` into `opts` in the callback
  parse(argv, opts} { o, node ->
    // set `node.value` based on `node.name?`
    // on `o` which points back to `opts`
  }
} catch (err: Error) {
  println("error: ${err.message}")
}
```

## Example

```kotlin
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
```

## API

### `fun <T : Any> parse(argv: Array<String>, out: T, cb: (T, Node) -> Any?): T`

Parse command line arguments in `argv` int `out: T` by calling `cb(out, node)`
for each parsed argument with output `out: T` and `node: Node` (see below)
that contains the `name`, `source`, and `value` of a parsed command line
argument node.

* `argv: Array<String>` - An array of `String` command line arguments
  likely passed in fun `main(argv: Array<String>)`.
* `out: T` - A non-nullable type instance container that stores parsed
  command line values.
* `cb: (T, Node) -> Any?` - A callback function that is called for each
  parsed command line node.

#### Rest Arguments

Command line arguments that fall after the `--` node will be consider
_nameless_ nodes and can be captured by the callback function as such as
`node.name` will be `null`

### `data class Node(val name: String?, val source: String, val value: String?)`

A data class container that represents a parsed command line argument
node. This can be a key-value pair, a flag, or a scalar value.

* `val name: String? = null` - The name of the parsed command line
  argument. A parsed command line argument node may be nameless, and
  therefore this value will be `null`.
* `val source: String` - The literal source of the parsed command line
  argument.
* `val value: String? = null` - The value of the parsed command line
  argument. A parsed command line argument node may be valueless, and
  therefore this value will be `null`.

## License

MIT
