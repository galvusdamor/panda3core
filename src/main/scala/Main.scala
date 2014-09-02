/**
 * Created by gregor on 02.09.14.
 */
object Main {

  def main(args: Array[String]) {

    println("Hello World")

    args map {x => "Argument : " + x} foreach println

    println("test")
  }
}
