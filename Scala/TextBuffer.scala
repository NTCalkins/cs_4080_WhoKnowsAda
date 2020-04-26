import scala.io.Source
import scala.collection.mutable.ArrayBuffer

abstract class TextBuffer() {
  var current: Int = 0
  var buffer: ArrayBuffer[StringBuilder] = ArrayBuffer.empty[StringBuilder]

  def clearBuffer() {
    buffer.clear()
    current = 0
  }

  def isValidLoc(loc: Int) : Boolean = {
    return (loc >= 0 && loc <= buffer.size)
  }

  def joinLines(loc1: Int, loc2: Int) : Boolean = {
    buffer.update(loc1-1, buffer.apply(loc1-1).append(buffer.apply(loc2-1).toString))
    return true
  }

  def insertAtLoc(loc: Int = current, s: ArrayBuffer[StringBuilder]) : Boolean = {
    if (!isValidLoc(loc)) {
      return false
    }
    if (loc == 0)
      buffer.prependAll(s)
    else
      buffer.insertAll(loc,s)
    return true
  }

  def insertBeforeLoc(loc: Int = current, s: ArrayBuffer[StringBuilder]) : Boolean = {
    if (!isValidLoc(loc))
      return false
    if (loc == 0)
      buffer.prependAll(s)
    else
      buffer.insertAll(loc-1,s)
    return true
  }

  def deleteAtLoc(loc: Int) : Boolean = {
    if (loc < 0)
      buffer.remove(0)
    else 
      buffer.remove(loc-1)
    return true
  }

  def resize() : Boolean = {
    //buffer.trimToSize
    return true
  }

  def append(s: StringBuilder) {
    buffer.append(s)
  }

  def move(loc: Int) : Boolean = {
    if (!isValidLoc(loc)) 
      return false
    current = loc
    return true
  }

  def moveToHead() {
    current = 0
  }

  def moveToTail() {
    move(buffer.size-1)
  }

  def moveToNext() {
    move(current+1)
  }

  def moveToPrev() {
    move(current-1)
  }

  def getTail() : Int = {
    return buffer.size-1

  }

  def getSize() : Int = {
    return buffer.size
  }

  def getCurrent() : Int = {
    return current
  }

  def getLine(loc: Int) : StringBuilder = {
    return buffer.apply(loc-1)
  }

  def readFromFile(filename: String) {
    val bufferedSource = Source.fromFile(filename)
    for (line <- bufferedSource.getLines) {
      buffer.append(new StringBuilder(line))
    }
    bufferedSource.close()
  }

  def printLine(lineNum: Int) {
    println(buffer.apply(lineNum-1))
  }

  def printLineWithNum(lineNum: Int) {
    println(lineNum + " " + buffer.apply(lineNum-1))
  }

  def printBuffer() {
    buffer.foreach {
      println
    }
  }

}
