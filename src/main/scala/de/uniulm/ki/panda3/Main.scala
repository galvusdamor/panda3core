package de.uniulm.ki.panda3

/**
 * Main object of PANDA 3
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
object Main {

  def main(args: Array[String]) {

    println("PANDA3 says \"Hello World!\".")

    println()
    println("List of Arguments")
    println("=================")
    args.zipWithIndex map (_.swap) map { case (i, x) => "Argument #" + i + ": " + x} foreach println


    println()
    println()
    println("EXITING PANDA3")
  }
}
