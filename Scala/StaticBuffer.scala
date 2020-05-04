import scala.io.Source
import scala.collection.mutable.ArrayBuffer

class StaticBuffer extends DynamicBuffer {
  val maxLength = 512

  override def joinLines(loc1: Int, loc2: Int) : Boolean = {
    val l1_length = buffer.apply(loc1-1).length
    val l2_length = buffer.apply(loc2-1).length
    
    if (l1_length + l2_length > maxLength) {
      println("Length too long: cannot complete join")
      return false
    }
    else {
      return super.joinLines(loc1, loc2)
    }
  }

  override def append(s: String) {
    if (s.length <= maxLength)
      buffer.append(new StringBuilder(maxLength, s))
  }

  override def getStringBuilders(s: ArrayBuffer[String]) : ArrayBuffer[StringBuilder] = {
    var temp: ArrayBuffer[StringBuilder] = ArrayBuffer.empty[StringBuilder]
    for (str <- s) {
      temp.append(new StringBuilder(maxLength, str))
    }
    return temp
  }

  override def insertAtLoc(loc: Int, s: ArrayBuffer[String]) : Boolean = {

    if (!isValidLoc(loc))
      return false

    for (str <- s) {
      if (str.length > maxLength) {
        println("Length of one or more lines too long: unable add lines")
        return false
      }
    }
    if (loc == 0)
      buffer.prependAll(getStringBuilders(s))
    else
      buffer.insertAll(loc,getStringBuilders(s))
    return true
  }

  override def insertBeforeLoc(loc: Int, s: ArrayBuffer[String]) : Boolean = {

    if (!isValidLoc(loc))
      return false

    for (str <- s) {
      if (str.length > maxLength) {
        println("Length of one or more lines is too long: unable to add lines")
        return false
      }
    }
    
    if (loc == 0)
      buffer.prependAll(getStringBuilders(s))
    else
      buffer.insertAll(loc,getStringBuilders(s))
    return true
  }

  override def readFromFile(filename: String) {
    val bufferedSource = Source.fromFile(filename)
    for (line <- bufferedSource.getLines) {
      if (line.length() > maxLength)
        println("buffer's max length of " + maxLength + " prevented a line from being read")
      else {
        var sb = new StringBuilder(maxLength, line)
        buffer.append(sb)
      }
    }
    bufferedSource.close()
  }
}
