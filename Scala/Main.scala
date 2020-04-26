import scala.collection.mutable.ArrayBuffer
object Main {
  def main(args: Array[String]) {
    var dynbuff = new DynamicBuffer()
    var sblb = new ArrayBuffer[StringBuilder]
    sblb.append(new StringBuilder("This is Line 1"))
    sblb.append(new StringBuilder("This is Line 2"))
    dynbuff.insertAtLoc(0,sblb)
    dynbuff.printBuffer() 
  }
}
