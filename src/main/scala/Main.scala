/**
 * Main object of PANDA 3
 *
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
object Main {

  def main(args: Array[String]) {

    println("PANDA3 says \"Hello World!\".")

    args map {x => "Argument : " + x} foreach println

    println("test")
  }
}
