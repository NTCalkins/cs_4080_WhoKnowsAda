using CS4080project.Parser;
using CS4080project.TextBuffer;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace LinkedListImplementation
{
    class CS4080project
    {       

        static void Main(string[] args)
        { 
            InputBuffer();
        }

        static void InputBuffer()
        {

            String input = " "; //User input
            int length; //Input length
            uint numberLine = 1; // How many line saved in final list
            uint currentAddress = 0;

            TextBuffer tempTextBuffer = new TextBuffer(); //Create temp buffer                   
            doublylinked inputList = new doublylinked();  // My text buffer
            DoubleLinkedList headOfList = new DoubleLinkedList(); //First node
            
            while (!input.Equals("quit", StringComparison.InvariantCultureIgnoreCase))
            {
                numberLine = tempTextBuffer.getNumberOfLines();
                currentAddress = tempTextBuffer.getCurrentAddress();
                inputList.resetIndex();
                Console.Write("");
                input = Console.ReadLine(); //Read input string
                input = input.Replace(" ", ""); //remove space between words
                
                Parser myParser = new Parser(input, tempTextBuffer.getnumberLine(), numberLine, currentAddress); //Sent input token to parser
                //Console.WriteLine("Line 1 is " + myParser.line1);
                //Console.WriteLine("Line 2 is " + myParser.line2);
                //Console.WriteLine("Current Address is " + currentAddress);


                //After parser read the command the switch statement help it choose what to do
                if (myParser.accept)
                {
                    switch (myParser.command)
                    {
                        case 'a':

                            while (input[0] != '.')
                            {
                                input = Console.ReadLine();
                                
                                if (input[0] == '.')
                                {
                                    uint totalLine = inputList.getIndex();
                                    tempTextBuffer.append(myParser.line1, headOfList, totalLine);                                  
                                    headOfList.head = null;
                     
                                    break;
                                }

                                inputList.InsertLast(headOfList, input);
                            }
                            break;

                        case 'c':

                            while (input[0] != '.')
                            {
                                input = Console.ReadLine();

                                if (input[0] == '.')
                                {
                                    uint totalLine = inputList.getIndex();
                                    tempTextBuffer.change(myParser.line1, myParser.line2, headOfList, totalLine);

                                    headOfList.head = null;

                                    break;
                                }

                                inputList.InsertLast(headOfList, input);
                            }
                            break;

                        case 'd':
                                                             
                            tempTextBuffer.delete(myParser.line1, myParser.line2);
                            headOfList.head = null;                                  
                    
                            break;

                        case 'j':
                                   
                            tempTextBuffer.join(myParser.line1, myParser.line2);
                            headOfList.head = null;                                   

                            break;

                        case 'l': 
                                                        
                            tempTextBuffer.list(myParser.line1, myParser.line2);
                            headOfList.head = null;                                   
  
                            break;

                        case 'm':
                                            
                            tempTextBuffer.move(myParser.line1, myParser.line2, myParser.line3);
                            headOfList.head = null;                               
                   
                            break;

                        case 'n':
                                                               
                            tempTextBuffer.number( myParser.line1, myParser.line2);
                            headOfList.head = null;                                 

                            break;

                        case 'p':

                            if (myParser.line1 != 0 && myParser.line2 != 0)
                            {
                                tempTextBuffer.printLine(myParser.line1, myParser.line2, headOfList);
                                headOfList.head = null;
                            }

                            break;

                        case 't':

                            tempTextBuffer.transfer(myParser.line1, myParser.line2, myParser.line3);
                            headOfList.head = null;

                            break;

                        case 'w':

                            tempTextBuffer.write( );
                            headOfList.head = null;

                            break;

                        case 'e':

                            tempTextBuffer.edit();
                            headOfList.head = null;

                            break;

                        default:
                            Console.WriteLine("Wrong command");
                            break;
                    }
                }              
               
            }
            
        }
    }
}
