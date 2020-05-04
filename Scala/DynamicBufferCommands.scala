import scala.io.Source
import scala.collection.mutable.ArrayBuffer
import scala.io.StdIn.{readLine, readInt}
import java.nio.file.{ Files, FileSystems }
import java.io._
import scala.util.control.Breaks._

class DynamicBufferCommands() {

  var buff: DynamicBuffer = new DynamicBuffer()

  //ArrayBuffer to hold input from the user
  var ui: ArrayBuffer[String] = ArrayBuffer.empty[String]

  //variables to hold the addresses of the current command
  var ad1: Int = 0
  var ad2: Int = -1
  var ad3: Int = -1

  //variables that hold the current command
  var com: Char = 'p'

  //variable to hold the current location in the buffer
  var cur: Int = 0

  //variable to hold the name of the file
  var defaultFile: String = "text"

  //variable to hold the name of the last non-default file written to
  var wfile: String = "text"

  //boolean to know if there is a default file set
  var hasDefault: Boolean = false

  def getDefaultFile() : String = {
    return defaultFile
  }

  def setDefaultFile(s: String) {
    defaultFile = s
    hasDefault = true
  }

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

  def edit() : Boolean = {
    if (!hasDefault)
      return false
    buff.clearBuffer
    readFile(getDefaultFile)
    return true
  }

  def readFile(filename: String) {
    
    //this gets the contents of filename
    val bufferedSource = Source.fromFile(filename)

    //for each line, read it into the buffer
    for (line <- bufferedSource.getLines)
      buff.append(line)

    //put the current location in the buffer at the end
    setCur(buff.getSize)
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
    //check for a special case in address
    if (s.charAt(0) == ';') {
      //set ad1 to current and ad2 to the end of thebuffer
      setAd(ad,getCur)
      setAd(ad+1,buff.getTail+1)
      s.delete(0,1)
      return true
    }
    //move onto processing a single address
    if (s.charAt(0) == '.') {
      // '.' means that this address should be the 
      // current location of the buffer
      setAd(ad,getCur);
      s.delete(0,1)
    }
    //$ means that this address should be the last in the buffer
    else if (s.charAt(0) == '$') {
      setAd(ad,buff.getTail+1);
      s.delete(0,1)
    }
    //logic to do a positive offset from the current location
    else if (s.charAt(0) == '+') {
      var addition = 1;
      s.delete(0,1)
      var i = 0;
      while (i < s.length && s.charAt(i).isDigit) {
        i += 1
      }
      //(s.substring(0,i));
      if (i == 0)
        setAd(ad, getCur + 1)
      else
        setAd(ad, getCur + s.substring(0,i).toInt)
      s.delete(0,i);
    }
    //logic to do a negative offset from the current location
    else if (s.charAt(0) == '-') {
      var subtraction = 1;
      s.delete(0,1)
      var i = 0
      while (i < s.length && s.charAt(i).isDigit) {
        i += 1
      }
      //println(s.substring(0,i))
      if (i == 0)
        setAd(ad, getCur - 1)
      else
        setAd(ad, getCur - s.substring(0,i).toInt)
      s.delete(0,i);
    }
    //logic to take a digit string from the address
    else if (s.charAt(0).isDigit) {
      var i = 0
      while (i < s.length() && s.charAt(i).isDigit) {
        i += 1
      }

      setAd(ad, s.substring(0,i).toInt)
      s.delete(0,i)
    }
    //otherwise, set address to current value
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
      ui.append(userString)
      userString = readLine()
    }
  }

  def getLines(ad1: Int, ad2: Int) : Boolean = {
    var i = ad1
    if (ad2 == -1) {
      ui.append(buff.getLine(ad1))
      return true
    }
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
    buff.insertAtLoc(ad, ui)
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
    setCur(ad3+(ad2-ad1+1))
    resetUserInput()
    return true
  }

  def move(ad1: Int, ad2: Int, ad3: Int) : Boolean = {
    if ( ad3 <= ad2 && ad3 >= ad1) {
      return false
    }
    else {
      transfer(ad1,ad2,ad3)
      if (ad3 > ad1 && ad2 != -1) {
        delete(ad1,ad2)
        setCur(ad3)
      }
      else {
        if (ad2 == -1) {
          delete(ad1+1,-1)
          setCur(ad3+1)
        }
        else {
          val diff = ad2-ad1+1
          delete(ad1+diff,ad2+diff)
          setCur(ad3+diff)
        }
      }
    }
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
    var i = ad1

    breakable {
      while (i < ad2) {
        if (!buff.joinLines(ad1,ad1+1)) {
          break
        }
        delete(ad1+1,-1)
        i += 1
      }
    }
    setCur(ad1)
    return true
  }

  def write(ad1: Int, ad2: Int, wrifile: String) : Boolean = {
    //check if the files exists, if it doesn't then make it.
    var file = new File(wrifile)
    if (!file.exists)
      //println("creating a new file")
      file.createNewFile

    val fw = new FileWriter(wrifile, false)
    var i = ad1
    for (i <- ad1 to ad2) {
      //println("writing to file")
      fw.write(buff.getLine(i) + "\n")
    }
    fw.close
    return true
  }

  def printMemoryStatistics() {

    val mb = 1024*1024
    val runtime = Runtime.getRuntime
    val totalMemory = runtime.totalMemory
    val freeMemory = runtime.freeMemory
    val usedMemory = totalMemory - freeMemory
    
    println()
    println("Used Memory: " + usedMemory / mb + " MB")
    println("Total Memory: " + totalMemory / mb + " MB")
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
    
    else if (com == 'm')
      move(getAd(1), getAd(2), getAd(3))

    else if (com == 'e')
      edit()

    else if (com == 'w')
      write(getAd(1),getAd(2), wfile)
  }

}
  
object DynamicBufferCommands {

  def main(args: Array[String]) = {
    val bc = new DynamicBufferCommands
    var wfile : String = ""

    //check if the user provided a filename
    if (args.length > 0) {

      //set the filename to default
      bc.setDefaultFile(args(0))
      bc.hasDefault = true
      println("set " + args(0) + " to default file")

      //if the file exists, read it in.
      if (Files.exists(FileSystems.getDefault().getPath(bc.getDefaultFile))) {
        bc.readFile(bc.getDefaultFile)
        println(args(0) + " exists, read into buffer")
      }
    }
    //continue while the user doesn't command q
    while (bc.getCom != 'q') {

      //set the addresses to default, and default command to print
      bc.setAd(1,-1)
      bc.setAd(2,-1)
      bc.setAd(3,-1)
      bc.setCom('p')


      //get the command string to be parsed
      var userString = bc.getUserCommand

      //if the first character isn't a command, extract an address
      if (!bc.isCommand(userString.charAt(0))) {
        bc.extractAddress(1,userString)
      }

      //otherwise, use the current address as the destination, except for w
      else {
        bc.extractCommand(userString)
        //no addresses for 'w' means that the default is from 1 to $
        if (bc.getCom == 'w') {
          bc.setAd(1,1)
          bc.setAd(2,bc.buff.getTail+1)
        }
        else {
          bc.setAd(1,bc.getCur)
        }
      }

      //if the command is w or e, we might need a filename
      if (bc.getCom == 'w' || bc.getCom == 'e') {
        //indication there is a filename
        if (userString.length() > 0) {
          //get ride of the whitespace
          userString.delete(0,1)
          //save the string
          bc.wfile = userString.toString
          //if you're trying to edit or there is no default yet, set the default
          if (bc.getCom == 'e' || !bc.hasDefault) {
            bc.setDefaultFile(userString.toString())
            bc.hasDefault = true
          }
        }
        //case for no file name
        else {
          if (!bc.hasDefault) {
            println("NO DEFAULT FILE AND NO FILE PROVIDED: CANNOT WRITE OR EDIT")
          }
          else {
            bc.wfile = bc.getDefaultFile
          }
        }
      }

      //println("First address is: " + bc.getAd(1))

      //check and see if there is a second address, otherwise just move on
      if (userString.length() > 0 && userString.charAt(0) == ',') {
        userString.delete(0,1)
        bc.extractAddress(2,userString)
      }
      //println("Second address is: " +bc.getAd(2))
      
      //get the command from the user, otherwise it will just print
      if (bc.getCom != 'w' && bc.getCom != 'e' && userString.length() > 0 && bc.isCommand(userString.charAt(0))) {
        bc.extractCommand(userString)
      }

      //Check for a third address
      if (userString.length() > 0 && userString.charAt(0) != ' ') {
        bc.extractAddress(3,userString)
      }
      //otherwise, use the current address of the bc
      else {
        bc.setAd(3,bc.getCur)
      }
      //check for a filename
      //println("Third address is: " + bc.getAd(3))
      //println("Operation is: " + bc.getCom)
      bc.processCommand
      
         
     }
     bc.printMemoryStatistics()
  }
}


