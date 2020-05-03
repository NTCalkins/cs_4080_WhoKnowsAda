using CS4080project.Parser;
using CS4080project.TextBufferDynamic;
using CS4080project.TextBufferStatic;
using DynamicLinkedListImplementation;
using StaticLinkedListImplementation;
using System;
using System.Diagnostics;
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

            String input = " "; //User input
            while (!input.Equals("0"))
            {
                Console.WriteLine("Enter 1 to use Dynamic length string text editor");
                Console.WriteLine("Enter 2 to use static length string text editor");
                Console.WriteLine("Enter 0 to to exit program");


                input = Console.ReadLine(); //Read input string

                if (input.Equals("1"))
                {
                    InputBufferDynamic();
                }
                else if (input.Equals("2"))
                {
                    InputBufferStatic();
                }

            }
        }

        static void InputBufferDynamic()
        {

            String input = ""; //User input1
            uint currentAddress = 0;
            uint line1 = 0;
            uint line2 = 0;
            uint line3 = 0;

            //Stopwatch to test execution time
            Stopwatch stopWatch = Stopwatch.StartNew();

            Parser myParser = new Parser("", 0);
            TextBufferDynamic tempTextBuffer = new TextBufferDynamic(); //Create temp buffer                   
            Doublylinked inputList = new Doublylinked();  // My text buffer
            DoubleLinkedList headOfList = new DoubleLinkedList(); //First node

            while (!input.Equals("q", StringComparison.InvariantCultureIgnoreCase))
            {
                inputList.ResetIndex();
                Console.Write("");
                input = Console.ReadLine(); //Read input string
                input = input.Replace(" ", ""); //remove space between words

                myParser = new Parser(input, currentAddress); //Sent input token to parser
                //Console.WriteLine("Line 1 is " + myParser.line1 + "Line 2 is " + myParser.line2 + "Line 3 is " + myParser.line3);
                //Console.WriteLine("Current Address is " + currentAddress);

                if (myParser.accept != true && myParser.line1 != 0) // if only one address is enter
                {
                    line1 = myParser.line1;
                }
                else if (myParser.accept)
                {
                    if (line1 != 0)
                    {
                        line2 = myParser.line1;
                    }
                    else
                    {
                        line1 = myParser.line1;
                        line2 = myParser.line2;
                        line3 = myParser.line3;
                    }

                }

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
                                    uint totalLine = inputList.GetIndex();
                                    unsafe
                                    {
                                        tempTextBuffer.Append(line1, headOfList, totalLine, &currentAddress);
                                    }
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
                                    uint totalLine = inputList.GetIndex();
                                    unsafe
                                    {
                                        tempTextBuffer.Change(line1, line2, headOfList, totalLine, &currentAddress);
                                    }

                                    headOfList.head = null;

                                    break;
                                }

                                inputList.InsertLast(headOfList, input);
                            }
                            break;

                        case 'd':

                            unsafe
                            {
                                tempTextBuffer.Delete(line1, line2, &currentAddress);
                            }
                            headOfList.head = null;

                            break;

                        case 'j':
                            unsafe
                            {
                                tempTextBuffer.Join(line1, line2, &currentAddress);
                            }
                            headOfList.head = null;

                            break;

                        case 'l':
                            unsafe
                            {
                                tempTextBuffer.List(line1, line2, &currentAddress);
                            }
                            headOfList.head = null;

                            break;

                        case 'm':
                            unsafe
                            {
                                tempTextBuffer.Move(line1, line2, line3, &currentAddress);
                            }
                            headOfList.head = null;

                            break;

                        case 'n':
                            unsafe
                            {
                                tempTextBuffer.number(line1, line2, &currentAddress);
                            }
                            headOfList.head = null;

                            break;

                        case 'p':

                            if (line1 != 0 && line2 != 0)
                            {
                                tempTextBuffer.PrintLine(line1, line2, headOfList);
                                headOfList.head = null;
                            }

                            break;

                        case 't':
                            unsafe
                            {
                                tempTextBuffer.Transfer(line1, line2, line3, &currentAddress);
                            }
                            headOfList.head = null;

                            break;

                        case 'w':

                            tempTextBuffer.Write(myParser.para);
                            headOfList.head = null;

                            break;

                        case 'e':
                            stopWatch.Start();
                            tempTextBuffer.Edit(myParser.para);
                            stopWatch.Stop();
                            Console.WriteLine("File load time is " +stopWatch.ElapsedMilliseconds + "ms");
                            headOfList.head = null;

                            break;

                        default:
                            Console.WriteLine("Wrong command");
                            break;
                    }

                    line1 = 0;
                    line2 = 0;
                    line3 = 0;
                }

            }

        }

        static void InputBufferStatic()
        {

            String input = ""; //User input1
            uint currentAddress = 0;
            uint line1 = 0;
            uint line2 = 0;
            uint line3 = 0;

            //Stopwatch to test execution time
            Stopwatch stopWatch = Stopwatch.StartNew();

            Parser myParser = new Parser("", 0);
            TextBufferStatic tempTextBuffer = new TextBufferStatic(); //Create temp buffer                   
            doublylinked2 inputList = new doublylinked2();  // My text buffer
            DoubleLinkedList2 headOfList = new DoubleLinkedList2(); //First node

            while (!input.Equals("q", StringComparison.InvariantCultureIgnoreCase))
            {
                inputList.ResetIndex();
                Console.Write("");
                input = Console.ReadLine(); //Read input string
                input = input.Replace(" ", ""); //remove space between words

                myParser = new Parser(input, currentAddress); //Sent input token to parser
                //Console.WriteLine("Line 1 is " + myParser.line1 + "Line 2 is " + myParser.line2 + "Line 3 is " + myParser.line3);
                //Console.WriteLine("Current Address is " + currentAddress);

                if (myParser.accept != true && myParser.line1 != 0) // if only one address is enter
                {
                    line1 = myParser.line1;
                }
                else if (myParser.accept)
                {
                    if (line1 != 0)
                    {
                        line2 = myParser.line1;
                    }
                    else
                    {
                        line1 = myParser.line1;
                        line2 = myParser.line2;
                        line3 = myParser.line3;
                    }

                }

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
                                    uint totalLine = inputList.GetIndex();
                                    unsafe
                                    {
                                        tempTextBuffer.Append(line1, headOfList, totalLine, &currentAddress);
                                    }
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
                                    uint totalLine = inputList.GetIndex();
                                    unsafe
                                    {
                                        tempTextBuffer.Change(line1, line2, headOfList, totalLine, &currentAddress);
                                    }

                                    headOfList.head = null;

                                    break;
                                }

                                inputList.InsertLast(headOfList, input);
                            }
                            break;

                        case 'd':

                            unsafe
                            {
                                tempTextBuffer.Delete(line1, line2, &currentAddress);
                            }
                            headOfList.head = null;

                            break;

                        case 'j':
                            unsafe
                            {
                                tempTextBuffer.Join(line1, line2, &currentAddress);
                            }
                            headOfList.head = null;

                            break;

                        case 'l':
                            unsafe
                            {
                                tempTextBuffer.List(line1, line2, &currentAddress);
                            }
                            headOfList.head = null;

                            break;

                        case 'm':
                            unsafe
                            {
                                tempTextBuffer.Move(line1, line2, line3, &currentAddress);
                            }
                            headOfList.head = null;

                            break;

                        case 'n':
                            unsafe
                            {
                                tempTextBuffer.Number(line1, line2, &currentAddress);
                            }
                            headOfList.head = null;

                            break;

                        case 'p':

                            if (line1 != 0 && line2 != 0)
                            {
                                tempTextBuffer.printLine(line1, line2, headOfList);
                                headOfList.head = null;
                            }

                            break;

                        case 't':
                            unsafe
                            {
                                tempTextBuffer.Transfer(line1, line2, line3, &currentAddress);
                            }
                            headOfList.head = null;

                            break;

                        case 'w':

                            tempTextBuffer.Write(myParser.para);
                            headOfList.head = null;

                            break;

                        case 'e':

                            stopWatch.Start();
                            tempTextBuffer.Edit(myParser.para);
                            stopWatch.Stop();
                            Console.WriteLine("File load time is " + stopWatch.ElapsedMilliseconds + "ms");

                            break;

                        default:
                            Console.WriteLine("Wrong command");
                            break;
                    }

                    line1 = 0;
                    line2 = 0;
                    line3 = 0;
                }

            }

        }
    }
}
