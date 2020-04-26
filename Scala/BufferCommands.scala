import scala.io.Source
import scala.collection.mutable.ArrayBuffer
import scala.io.StdIn.{readLine, readInt}

class BufferCommands() {

  var buff: DynamicBuffer = new DynamicBuffer()
  var ui: ArrayBuffer[StringBuilder] = ArrayBuffer.empty[StringBuilder]
  var ad1: Int = 0
  var ad2: Int = -1
  var ad3: Int = -1
  var com: Char = 'p'
  var cur: Int = 0

  def isCommand(c: Char) : Boolean = {
    return c match {
      case 'a' => true;
      case 'i' => true;
      case 'c' => true;
      case 'd' => true;
      case 'j' => true;
      case 'l' => true;
      case 'm' => true;
      case 'n' => true;
      case 'p' => true;
      case 't' => true;
      case 'w' => true;
      case 'e' => true;
      case 'q' => true;
      case _ => false;
    }
  }

  def readFile(filename: String) {
    val bufferedSource = Source.fromFile(filename)
    for (line <- bufferedSource.getLines)
      buff.append(new StringBuilder(line))
  }

  def getCur() : Int = {
    return cur
  }

  def setCur(i: Int) {
    cur = i
  }

  def getAd(ad: Int) : Int = {
    if (ad == 1)
      return ad1
    else if (ad == 2)
      return ad2
    else
      return ad3
  }

  def setAd(ad: Int, value: Int) {
    if (ad == 1) {
      ad1 = value
    }
    else if (ad == 2) {
      ad2 = value
    }
    else {
      ad3 = value
    }
  }


  def extractAddress(ad: Int, s: StringBuilder) : Boolean = { 
    //check for double address getters
    if (s.charAt(0) == ',') {
      setAd(ad, 1)
      setAd(ad+1, buff.getTail+1)
      s.delete(0,1)
      return true
    }
    if (s.charAt(0) == ';') {
      setAd(ad,getCur)
      setAd(ad+1,buff.getTail+1)
      s.delete(0,1)
      return true
    }
    //move onto processing a single address
    if (s.charAt(0) == '.') {
      setAd(ad,getCur);
      s.delete(0,1)
    }
    else if (s.charAt(0) == '$') {
      setAd(ad,buff.getTail+1);
      s.delete(0,1)
    }
    else if (s.charAt(0) == '+') {
      var addition = 1;
      s.delete(0,1)
      var i = 0;
      while (i < s.length && s.charAt(i).isDigit) {
        i += 1
      }
      println(s.substring(0,i));
      if (i == 0)
        setAd(ad, getCur + 1)
      else
        setAd(ad, getCur + s.substring(0,i).toInt)
      s.delete(0,i);
    }
    else if (s.charAt(0) == '-') {
      var subtraction = 1;
      s.delete(0,1)
      var i = 0
      while (i < s.length && s.charAt(i).isDigit) {
        i += 1
      }
      println(s.substring(0,i))
      if (i == 0)
        setAd(ad, getCur - 1)
      else
        setAd(ad, getCur - s.substring(0,i).toInt)
      s.delete(0,i);
    }
    else if (s.charAt(0).isDigit) {
      var i = 0
      while (i < s.length() && s.charAt(i).isDigit) {
        i += 1
      }

      setAd(ad, s.substring(0,i).toInt)
      s.delete(0,i)
    }
    else
      setAd(ad, getCur)
    return true
  }

  def extractCommand(s: StringBuilder) {
    if (s.length() > 0) {
      com = s.charAt(0)
      s.delete(0,1)
    }
  }

  def setCom(c: Char) {
    com = c
  }

  def getCom() : Char = {
    return com
  }

  def getUserCommand() : StringBuilder = {
    var userString = readLine("Enter an ed command: ")
    return new StringBuilder(userString)
  }

  def getUserInput() {
    var userString = readLine()
    while (userString.charAt(0) != '.') {
      ui.append(new StringBuilder(userString))
      userString = readLine()
    }
  }

  def getLines(ad1: Int, ad2: Int) : Boolean = {
    var i = ad1
    while (i <= ad2) {
      ui.append(buff.getLine(i))
      i += 1
    }
    return true
  }

  def resetUserInput() {
    ui.clear
  }

  def append(ad: Int) : Boolean = {
    getUserInput()
    println(buff.insertAtLoc(ad, ui))
    setCur(ad+ui.size)
    resetUserInput()
    return true
  }

  def insert(ad: Int) : Boolean = {
    getUserInput()
    buff.insertBeforeLoc(ad,ui)
    setCur(ad+ui.size-1)
    resetUserInput()
    return true
  }

  def transfer(ad1: Int, ad2: Int, ad3: Int) : Boolean = {
    getLines(ad1,ad2)
    buff.insertAtLoc(ad3,ui)
    setCur(ad2)
    resetUserInput()
    return true
  }

  def printWithNum(ad1: Int, ad2: Int) : Boolean = {
    if (ad2 == -1) {
      buff.printLine(ad1)
      setCur(ad1)
    }
    else {
      var i = ad1
      while (i <= ad2) {
        buff.printLineWithNum(i)
        i+=1
      }
      setCur(ad2)
    }
    return true
  }

  def print(ad1: Int, ad2: Int) : Boolean = {
    if (ad2 == -1) {
      buff.printLine(ad1)
      setCur(ad1)
    }
    else {
      var i = ad1
      while (i <= ad2){
        buff.printLine(i)
        i += 1
      }
      setCur(ad2)
    }
    return true
  }

  def delete(ad1: Int, ad2: Int) : Boolean = {
    if (ad2 == -1) {
      buff.deleteAtLoc(ad1)
    }
    else {
      var i = 0
      while ( i <= ad2-ad1) {
        buff.deleteAtLoc(ad1)
        i += 1
      }
    }
    buff.resize
    setCur(if (ad1 > buff.getSize)  buff.getSize else ad1)
    return true
  }

  def change(ad1: Int, ad2: Int) : Boolean = {
    delete(ad1, ad2)
    insert(getCur)
    return true
  }

  def join(ad1: Int, ad2: Int) : Boolean = {
    buff.joinLines(ad1,ad2)
    delete(ad2,-1)
    setCur(ad1)
    return true
  }

  def processCommand() {
    if (com == 'a')
      append(getAd(1))
    
    else if (com == 'p')
      print(getAd(1),getAd(2))
    
    else if (com == 'i')
      insert(getAd(1))
    
    else if (com == 'd')
      delete(getAd(1), getAd(2))
    
    else if (com == 'n')
      printWithNum(getAd(1),getAd(2))
    
    else if (com == 'c')
      change(getAd(1),getAd(2))
    
    else if (com == 'j')
      join(getAd(1),getAd(2))

    else if (com == 't')
      transfer(getAd(1), getAd(2), getAd(3))
  }

}
  
object BufferCommands {
  def main(args: Array[String]) = {
    val bc = new BufferCommands
    var filename = "text"
    if (args.length > 0)
      filename = args(0)

    println(filename)
    while (bc.getCom != 'q') {

      bc.setAd(1,-1)
      bc.setAd(2,-1)
      bc.setAd(3,-1)
      bc.setCom('p')

      var userString = bc.getUserCommand
      bc.setCom('p')

      if (!bc.isCommand(userString.charAt(0))) {
        bc.extractAddress(1,userString)
      }

      else {
        bc.setAd(1,bc.getCur)
      }
      println("First address is: " + bc.getAd(1))

      if (userString.length() > 0 && userString.charAt(0) == ',') {
        userString.delete(0,1)
        bc.extractAddress(2,userString)
      }
      println("Second address is: " +bc.getAd(2))

      if (userString.length() > 0 && bc.isCommand(userString.charAt(0))) {
        bc.extractCommand(userString)
      }
      if (userString.length() > 0) {
        bc.extractAddress(3,userString)
      }
      else {
        bc.setAd(3,bc.getCur)
      }
      println("Third address is: " + bc.getAd(3))
      println("Operation is: " + bc.getCom)
      bc.processCommand
      
         
     }
  }
}


