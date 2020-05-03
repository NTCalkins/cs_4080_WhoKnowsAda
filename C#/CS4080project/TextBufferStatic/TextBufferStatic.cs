using StaticLinkedListImplementation;
using DynamicLinkedListImplementation;
using System;
using System.IO;
using System.Collections.Generic;
using System.Text;

namespace CS4080project.TextBufferStatic
{
    class TextBufferStatic
    {
        doublylinked2 textBufferList = new doublylinked2(); // The text of the current document (DynamicString)
        DoubleLinkedList2 headOfList = new DoubleLinkedList2();  // The first line of the current document (DynamicString)
        uint currentAddress; /* The address used as the default single input for commands when no parameters are passed. Check the doc for which commands change this. */
        uint firstline = 1; // Use for file save starting position.
        uint numberLine = 1; /* How many lines are currently in the buffer if the DoubleLinkedList does not store its own size */

        /* Appends contents of input buffer after the input line */
        public unsafe void Append(uint line, DoubleLinkedList2 inputBuffer, uint totalLine, uint* mainbufferLine)
        {
            if (line == 9999)
            {
                StaticStringLinkNode temp = inputBuffer.head;

                if (temp != null)
                {
                    textBufferList.InsertFront(headOfList, temp.memory.ToString()); // copy first line from input buffer to text buffer
                }
                StaticStringLinkNode temp2 = headOfList.head;
                temp = temp.next;

                while (temp != null)
                {
                    textBufferList.InsertAfter(temp2, temp.memory.ToString());
                    temp = temp.next;
                    temp2 = temp2.next;
                }
                numberLine = textBufferList.GetIndex(); // Get the current line number
                currentAddress = textBufferList.GetIndex(); //Current Address is the last line entered
                *mainbufferLine = textBufferList.GetIndex();
            }
            else if (line == 0 || line == textBufferList.GetIndex()) //append at the end
            {

                StaticStringLinkNode temp = inputBuffer.head;

                if (temp != null)
                {
                    textBufferList.InsertLast(headOfList, temp.memory.ToString()); // copy first line from input buffer to text buffer
                }

                temp = temp.next;

                while (temp != null)
                {
                    textBufferList.InsertLast(headOfList, temp.memory.ToString());
                    temp = temp.next;
                }
                numberLine = textBufferList.GetIndex(); // Get the current line number
                currentAddress = textBufferList.GetIndex(); //Current Address is the last line entered
                *mainbufferLine = textBufferList.GetIndex();
            }
            else //append after a selecte line
            {
                StaticStringLinkNode temp = inputBuffer.head;
                StaticStringLinkNode temp2 = headOfList.head;

                if (line == 1) // Insert after the head
                {
                    if (temp != null)
                    {
                        textBufferList.InsertAfter(headOfList.head, temp.memory.ToString());
                    }

                    temp = temp.next;
                    temp2 = temp2.next;

                    while (temp != null)
                    {
                        textBufferList.InsertAfter(temp2, temp.memory.ToString());
                        temp = temp.next;
                        temp2 = temp2.next;
                    }
                    numberLine = textBufferList.GetIndex(); // Get the current line number
                    currentAddress = totalLine + 1; //Current Address is the last line entered
                    *mainbufferLine = textBufferList.GetIndex();
                }
                else if (textBufferList.GetIndex() >= line)
                {
                    for (int i = 0; i < line - 1; i++)
                    {
                        temp2 = temp2.next;
                    }

                    if (temp != null)
                    {
                        textBufferList.InsertAfter(temp2, temp.memory.ToString());
                    }

                    temp = temp.next;
                    temp2 = temp2.next;

                    while (temp != null)
                    {
                        textBufferList.InsertAfter(temp2, temp.memory.ToString());
                        temp = temp.next;
                        temp2 = temp2.next;
                    }
                    *mainbufferLine = textBufferList.GetIndex();
                    numberLine = textBufferList.GetIndex(); // Get the current line number
                    currentAddress = totalLine + numberLine; //Current Address is the last line entered
                }

                else
                {
                    Console.WriteLine("Erro wrong input lines number ");
                }
            }
        }

        /* Replace lines from line1 through line2 with the lines in the input buffer. For single argument, change that one line. */
        public unsafe void Change(uint line1, uint line2, DoubleLinkedList2 inputBuffer, uint totalLine, uint* mainbufferLine)
        {
            if (line1.Equals(1) && line2.Equals(0)) //if loaction is at 1 and second number is 0
            {
                Append(line1, inputBuffer, 0, mainbufferLine);
                Delete(1, 0, mainbufferLine);
                numberLine = textBufferList.GetIndex(); // Get the current line number
                currentAddress = totalLine + 1; //Current Address is the last line entered
                *mainbufferLine = textBufferList.GetIndex();
            }
            else if (line1 > 1 && line2.Equals(0)) //if loaction is great then 1 and second number is 0
            {
                Delete(line1, 0, mainbufferLine);
                Append(line1 - 1, inputBuffer, 0, mainbufferLine);
                numberLine = textBufferList.GetIndex(); // Get the current line number
                currentAddress = totalLine + line1; //Current Address is the last line entered
                *mainbufferLine = textBufferList.GetIndex();
            }

            else if (line2 >= line1)
            {
                Delete(line1, line2, mainbufferLine);
                Append(line1 - 1, inputBuffer, 0, mainbufferLine);
                numberLine = textBufferList.GetIndex(); // Get the current line number
                currentAddress = totalLine + line1; //Current Address is the last line entered
                *mainbufferLine = textBufferList.GetIndex();
            }

        }

        /* Delete lines from line1 through line2. Also provide a single argument version acting on one line. */
        public unsafe void Delete(uint line1, uint line2, uint* mainbufferLine)
        {
            if (line1 >= 1 && line2.Equals(0))
            {
                textBufferList.DeleteNodeByLine(headOfList, line1);
                numberLine = textBufferList.GetIndex(); // Get the current line number
                currentAddress = numberLine - 1; //Current Address is the last line entered
                *mainbufferLine = textBufferList.GetIndex();
            }
            else if (line1 == line2) //if same number then only remove that line
            {
                textBufferList.DeleteNodeByLine(headOfList, line1);
                numberLine = textBufferList.GetIndex(); // Get the current line number
                currentAddress = numberLine - 1; //Current Address is the last line entered
                *mainbufferLine = textBufferList.GetIndex();
            }
            else if (line2 > line1) //line1 needs to be greate then line 2
            {
                for (int i = 0; i < (line2 - line1) + 1; i++)
                {
                    textBufferList.DeleteNodeByLine(headOfList, line1);
                }
                numberLine = textBufferList.GetIndex(); // Get the current line number
                currentAddress = line1 - 1; //Current Address is the last line entered
                *mainbufferLine = textBufferList.GetIndex();
            }
            else
            {
                Console.WriteLine("Error lines number");
            }

        }

        /* Replace lines from line1 through line2 with a single line that is their concatenation. When only one argument is passed, the command does nothing. */
        public unsafe void Join(uint line1, uint line2, uint* mainbufferLine)
        {
            uint index = line1; // final result will be inser at line one
            uint index2 = line2;

            string copy;

            if (line2 > line1 && line2 != line1)
            {

                StaticStringLinkNode temp = headOfList.head; // use to locate line 1 location                  
                StaticStringLinkNode temp2 = headOfList.head; // use to copy node                  
                                                               //(COPY)

                if (line1.Equals(1)) //if start at first line
                {
                    copy = headOfList.head.memory.ToString();
                    index2--;

                    while (temp != null && index2 != 0)
                    {
                        temp = temp.next;

                        copy = copy.Insert(copy.Length, " " + temp.memory);
                        index2--;
                    }

                    textBufferList.InsertAfter(headOfList.head, copy);
                    Delete(line1, 0, mainbufferLine); //after copy then delete line 1 through line2 
                    Delete(2, line2, mainbufferLine); //after copy then delete line 1 through line2 
                    numberLine = textBufferList.GetIndex(); // Get the current line number
                    currentAddress = line1; //Current Address is the last line entered
                    *mainbufferLine = textBufferList.GetIndex();
                }

                else
                {
                    index = line1 - 1;
                    index2 = (line2 - line1 + 1);

                    while (temp != null && index != 0) //locate the line1
                    {
                        temp = temp.next;
                        temp2 = temp2.next;
                        index--;
                    }

                    copy = temp.memory.ToString();
                    index2--;
                    while (temp != null && index2 != 0) //copy from line 1 to line2
                    {
                        temp = temp.next;
                        copy = copy.Insert(copy.Length, " " + temp.memory);
                        index2--;
                    }
                    Delete(line1, line2, mainbufferLine); //delete line 1 through line2  
                    textBufferList.InsertAfter(temp.prev, copy);
                    numberLine = textBufferList.GetIndex(); // Get the current line number
                    currentAddress = line1; //Current Address is the last line entered
                    *mainbufferLine = textBufferList.GetIndex();
                }
            }
            else
                return;
        }

        /* Move lines from line1 through line2 to after line3. For single argument, move that one line. */
        public unsafe void Move(uint line1, uint line2, uint line3, uint* mainbufferLine)
        {
            uint index = line1;
            uint index2 = line2;
            uint index3 = line3;
            doublylinked2 tempList = new doublylinked2(); // Temp helper list 
            DoubleLinkedList2 tempheadOfList = new DoubleLinkedList2();  // Temp helper list's first node

            if (line1 >= 1 && line2.Equals(0) && line3 >= 0 && line3 != line1) //if line 2 is 0 then only move one line
            {

                if (headOfList.head != null && line1.Equals(1)) //If location is line 1
                {
                    tempList.InsertLast(tempheadOfList, headOfList.head.memory.ToString());
                    Delete(1, 0, mainbufferLine);
                    Append(line3 - 1, tempheadOfList, 0, mainbufferLine); //need to rest set memory each time or creat 
                    numberLine = textBufferList.GetIndex(); // Get the current line number
                    currentAddress = line3; //Current Address is the last line entered
                    *mainbufferLine = textBufferList.GetIndex();
                }
                else if (line3 >= 1)
                {
                    StaticStringLinkNode temp = headOfList.head; // use to locate line 1 location
                    StaticStringLinkNode temp2 = headOfList.head;
                    index--; //for line 1 location
                    index3--;
                    while (temp != null && index != 0) //This while loop is use to locate line 1 string in text buffer
                    {
                        index--;
                        temp = temp.next; // line1 loaction node, we will copy this node's tring to temp                      
                    }
                    while (temp2 != null && index3 != 0) //This while loop is use to locate line 3
                    {
                        index3--;
                        temp2 = temp2.next; // line 3 loaction node string
                    }
                    Delete(line1, 0, mainbufferLine); // now we will reomove line 1 from text buffer
                    textBufferList.InsertAfter(temp2, temp.memory.ToString());  // Insert line 1 string after line 3
                    numberLine = textBufferList.GetIndex(); // Get the current line number
                    currentAddress = line3;
                    *mainbufferLine = textBufferList.GetIndex();
                }
                else if (line3.Equals(0))
                {
                    StaticStringLinkNode temp = headOfList.head; // use to locate line 1 location
                    index--; //for line 1 location
                    index3--;
                    while (temp != null && index != 0) //This while loop is use to locate line 1 string in text buffer
                    {
                        index--;
                        temp = temp.next; // line1 loaction node, we will copy this node's tring to temp                      
                    }
                    Delete(line1, 0, mainbufferLine); // now we will reomove line 1 from text buffer
                    textBufferList.InsertFront(headOfList, temp.memory.ToString());  // Insert line 1 string after line 3
                    numberLine = textBufferList.GetIndex(); // Get the current line number
                    currentAddress = line3;
                    *mainbufferLine = textBufferList.GetIndex();
                }
            }
            else if (line1 >= 1 && line2.Equals(line1) && line3 >= 1 && line3 != line1) // if line 1 = line2
            {

                if (headOfList.head != null && line1.Equals(1)) //If location is line 1
                {
                    tempList.InsertLast(tempheadOfList, headOfList.head.memory.ToString());
                    Delete(1, 0, mainbufferLine);
                    Append(line3 - 1, tempheadOfList, 0, mainbufferLine); //need to rest set memory each time or creat 
                    numberLine = textBufferList.GetIndex(); // Get the current line number
                    currentAddress = line3;
                    *mainbufferLine = textBufferList.GetIndex();
                }
                else if (line3 >= 1)
                {
                    StaticStringLinkNode temp = headOfList.head; // use to locate line 1 location
                    StaticStringLinkNode temp2 = headOfList.head; // use to locate line 3 string 
                    index--; //for line 1 location
                    index3--;
                    while (temp != null && index != 0) //This while loop is use to locate line 1 string in text buffer
                    {
                        index--;
                        temp = temp.next; // line1 loaction node, we will copy this node's tring to temp                      
                    }
                    while (temp2 != null && index3 != 0) //Thi1s while loop is use to locate line 3
                    {
                        index3--;
                        temp2 = temp2.next; // line 3 loaction node string
                    }
                    Delete(line1, 0, mainbufferLine); // now we will reomove line 1 from text buffer
                    textBufferList.InsertAfter(temp2, temp.memory.ToString());  // Insert line 1 string after line 3
                    numberLine = textBufferList.GetIndex(); // Get the current line number
                    currentAddress = line3;
                    *mainbufferLine = textBufferList.GetIndex();
                }
                else if (line3.Equals(0))
                {
                    StaticStringLinkNode temp = headOfList.head; // use to locate line 1 location
                    StaticStringLinkNode temp2 = headOfList.head;
                    index--; //for line 1 location
                    index3--;
                    while (temp != null && index != 0) //This while loop is use to locate line 1 string in text buffer
                    {
                        index--;
                        temp = temp.next; // line1 loaction node, we will copy this node's tring to temp                      
                    }
                    while (temp2 != null && index3 != 0) //This while loop is use to locate line 3
                    {
                        index3--;
                        temp2 = temp2.next; // line 3 loaction node string
                    }
                    Delete(line1, 0, mainbufferLine); // now we will reomove line 1 from text buffer
                    textBufferList.InsertFront(headOfList, temp.memory.ToString());  // Insert line 1 string after line 3
                    numberLine = textBufferList.GetIndex(); // Get the current line number
                    currentAddress = line3;
                    *mainbufferLine = textBufferList.GetIndex();
                }
            }
            else if (line2 > line1 && !(line1 <= line3 && line3 <= line2)) // copy line 1 throut line 2 and inser after line 3
            {

                index = line1 - 1;
                index2 = (line2 - line1 + 1);
                StaticStringLinkNode temp = headOfList.head; // use to locate line 1 location                  
                StaticStringLinkNode temp2 = headOfList.head; // use to locate line 3 string                   

                while (temp != null && index != 0) //If location is line 1. Copy line 1 thorough line 2
                {
                    temp = temp.next;
                    index--;
                }

                while (temp != null && index2 != 0) //If location is line 1. Copy line 1 thorough line 2                   
                {
                    tempList.InsertLast(tempheadOfList, temp.memory.ToString());
                    temp = temp.next; // line1 loaction node, we will copy this node's tring to temp                      
                    index2--;
                }
                if (line3.Equals(0))
                {
                    Delete(line1, line2, mainbufferLine); //after copy then delete line 1 through line2     
                    Append(9999, tempheadOfList, 0, mainbufferLine);
                    numberLine = textBufferList.GetIndex(); // Get the current line number
                    currentAddress = line3 + (line2 - line1 + 1);
                    *mainbufferLine = textBufferList.GetIndex();
                }
                else if (line3 > line2) // if line 3 is after line 2                   
                {

                    Delete(line1, line2, mainbufferLine); //after copy then delete line 1 through line2                        
                    Append(line3 - (line2 - line1 + 1), tempheadOfList, 0, mainbufferLine);
                    numberLine = textBufferList.GetIndex(); // Get the current line number
                    currentAddress = line3;
                    *mainbufferLine = textBufferList.GetIndex();
                }

                else if (line3 < line1)  // if line 3 is before line 1                  
                {
                    Delete(line1, line2, mainbufferLine); //after copy then delete line 1 through line2                       
                    Append(line3, tempheadOfList, 0, mainbufferLine);
                    numberLine = textBufferList.GetIndex(); // Get the current line number
                    currentAddress = line3 + (line2 - line1 + 1);
                    *mainbufferLine = textBufferList.GetIndex();
                }

            }
            else
            {
                Console.WriteLine("Line index error");
            }


        }

        /* Print out lines from line1 through line2. For single argument, list that one line. */
        public unsafe void List(uint line1, uint line2, uint* mainbufferLine)
        {
            if (line2 >= line1)
            {
                Console.WriteLine("* Print line  *");
                textBufferList.PrintList(headOfList, line1, line2, 1);
                firstline = line1;
                currentAddress = line2; //Current Address is the last line entered
                *mainbufferLine = textBufferList.GetIndex();
            }
            else
            {
                Console.WriteLine("Line index error");
            }
        }

        /* Number out lines from line1 through line2. For single argument, number that one line. */
        public unsafe void Number(uint line1, uint line2, uint* mainbufferLine)
        {
            if (line2 >= line1)
            {
                Console.WriteLine("* Print line  *");
                textBufferList.PrintList(headOfList, line1, line2, 2);
                currentAddress = line2; //Current Address is the last line entered
                *mainbufferLine = textBufferList.GetIndex();
            }
            else
            {
                Console.WriteLine("Line index error");
            }
        }

        /* Copies lines from line1 through line2 and inserts them after line3. For single argument, transfer that one line. */
        public unsafe void Transfer(uint line1, uint line2, uint line3, uint* mainbufferLine)
        {
            {
                uint index = line1;
                uint index2 = line2;
                uint index3 = line3;
                doublylinked2 tempList = new doublylinked2(); // Temp helper list 
                DoubleLinkedList2 tempheadOfList = new DoubleLinkedList2();  // Temp helper list's first node

                if (line1 >= 1 && line2.Equals(0) && line3 >= 0 && line3 != line1) //is line 2 is 0 then only transfer one line
                {

                    if (headOfList.head != null && line1.Equals(1)) //If location is line 1
                    {
                        tempList.InsertLast(tempheadOfList, headOfList.head.memory.ToString());
                        Append(line3 - 1, tempheadOfList, 0, mainbufferLine); //need to rest set memory each time or creat 
                        numberLine = textBufferList.GetIndex(); // Get the current line number
                        currentAddress = line3 + 1;
                        *mainbufferLine = textBufferList.GetIndex();
                    }
                    else if (line3 >= 1)
                    {
                        StaticStringLinkNode temp = headOfList.head; // use to locate line 1 location
                        StaticStringLinkNode temp2 = headOfList.head;
                        index--; //for line 1 location
                        index3--;
                        while (temp != null && index != 0) //This while loop is use to locate line 1 string in text buffer
                        {
                            index--;
                            temp = temp.next; // line1 loaction node, we will copy this node's tring to temp                      
                        }
                        while (temp2 != null && index3 != 0) //This while loop is use to locate line 3
                        {
                            index3--;
                            temp2 = temp2.next; // line 3 loaction node string
                        }
                        textBufferList.InsertAfter(temp2, temp.memory.ToString());  // Insert line 1 string after line 3
                        numberLine = textBufferList.GetIndex(); // Get the current line number
                        currentAddress = line3 + 1;
                        *mainbufferLine = textBufferList.GetIndex();
                    }
                    else if (line3.Equals(0))
                    {
                        StaticStringLinkNode temp = headOfList.head; // use to locate line 1 location
                        index--; //for line 1 location
                        index3--;
                        while (temp != null && index != 0) //This while loop is use to locate line 1 string in text buffer
                        {
                            index--;
                            temp = temp.next; // line1 loaction node, we will copy this node's tring to temp                      
                        }
                        textBufferList.InsertFront(headOfList, temp.memory.ToString());  // Insert line 1 string after line 3
                        numberLine = textBufferList.GetIndex(); // Get the current line number
                        currentAddress = line3;
                        *mainbufferLine = textBufferList.GetIndex();
                    }
                }
                else if (line1 >= 1 && line2.Equals(line1) && line3 >= 1 && line3 != line1) // if line 1 = line2
                {

                    if (headOfList.head != null && line1.Equals(1)) //If location is line 1
                    {
                        tempList.InsertLast(tempheadOfList, headOfList.head.memory.ToString());
                        Append(line3 - 1, tempheadOfList, 0, mainbufferLine); //need to rest set memory each time or creat 
                        numberLine = textBufferList.GetIndex(); // Get the current line number
                        currentAddress = line3 + 1;
                        *mainbufferLine = textBufferList.GetIndex();
                    }
                    else
                    {
                        StaticStringLinkNode temp = headOfList.head; // use to locate line 1 location
                        StaticStringLinkNode temp2 = headOfList.head; // use to locate line 3 string 
                        index--; //for line 1 location
                        index3--;
                        while (temp != null && index != 0) //This while loop is use to locate line 1 string in text buffer
                        {
                            index--;
                            temp = temp.next; // line1 loaction node, we will copy this node's tring to temp                      
                        }
                        while (temp2 != null && index3 != 0) //Thi1s while loop is use to locate line 3
                        {
                            index3--;
                            temp2 = temp2.next; // line 3 loaction node string
                        }
                        textBufferList.InsertAfter(temp2, temp.memory.ToString());  // Insert line 1 string after line 3
                        numberLine = textBufferList.GetIndex(); // Get the current line number
                        currentAddress = line3 + 1;
                        *mainbufferLine = textBufferList.GetIndex();
                    }
                }
                else if (line2 > line1) // copy line 1 throut line 2 and inser after line 3
                {
                    if (line3.Equals(0))
                    {
                        index = line1 - 1;
                        index2 = (line2 - line1 + 1);
                        StaticStringLinkNode temp = headOfList.head; // use to locate line 1 location                  
                        StaticStringLinkNode temp2 = headOfList.head; // use to locate line 3 string                   

                        index3--;

                        while (temp != null && index != 0) //If location is line 1. Copy line 1 thorough line 2
                        {
                            temp = temp.next;
                            index--;
                        }

                        while (temp != null && index2 != 0) //If location is line 1. Copy line 1 thorough line 2                   
                        {
                            tempList.InsertLast(tempheadOfList, temp.memory.ToString());
                            temp = temp.next; // line1 loaction node, we will copy this node's tring to temp                      
                            index2--;
                        }
                        Append(9999, tempheadOfList, 0, mainbufferLine);
                        numberLine = textBufferList.GetIndex(); // Get the current line number
                        currentAddress = line3 + (line2 - line1 + 1);
                        *mainbufferLine = textBufferList.GetIndex();
                    }
                    else
                    {
                        index = line1 - 1;
                        index2 = (line2 - line1 + 1);
                        StaticStringLinkNode temp = headOfList.head; // use to locate line 1 location                  
                        StaticStringLinkNode temp2 = headOfList.head; // use to locate line 3 string                   

                        index3--;

                        while (temp != null && index != 0) //If location is line 1. Copy line 1 thorough line 2
                        {
                            temp = temp.next;
                            index--;
                        }

                        while (temp != null && index2 != 0) //If location is line 1. Copy line 1 thorough line 2                   
                        {
                            tempList.InsertLast(tempheadOfList, temp.memory.ToString());
                            temp = temp.next; // line1 loaction node, we will copy this node's tring to temp                      
                            index2--;
                        }

                        Append(line3, tempheadOfList, 0, mainbufferLine);
                        numberLine = textBufferList.GetIndex(); // Get the current line number
                        currentAddress = line3 + (line2 - line1 + 1);
                        *mainbufferLine = textBufferList.GetIndex();
                    }

                }
                else
                {
                    Console.WriteLine("Line index error");
                }
            }
        }

        /* Writes the contents of the buffer into the inputted file */
        public void Write(string inputName)
        {
            StreamWriter sw;

            try
            {
                if (inputName != " ")
                {
                    //Create a new file called "cs4080.txt"
                    FileInfo fileName = new FileInfo(inputName);
                    //Get a StreamWriter for the file
                    sw = fileName.CreateText();
                }
                else
                {
                    FileInfo fileName = new FileInfo("file.txt");
                    //Get a StreamWriter for the file
                    sw = fileName.CreateText();
                }

                StaticStringLinkNode n = headOfList.head;
                uint numberLine = firstline;
                uint tempIndex = textBufferList.GetIndex();

                for (int i = 0; i < tempIndex; i++)
                {
                    //Write to file
                    sw.WriteLine(n.memory);
                    numberLine++;
                    n = n.next;
                }

                sw.Close();
            }
            catch (IOException e)
            {
                Console.WriteLine("An IO Exception Occurred :" + e);
            }
        }

        /* Loads the contents of the input file and sets currentAddress to the last line */
        public void Edit(string inputName)
        {
            FileInfo fileName;
            StreamReader sr;

            try
            {
                if (inputName != " ")
                {
                    //Open the file
                    fileName = new FileInfo(inputName );
                    //Get the StreamReader
                    sr = fileName.OpenText();
                }
                else
                {
                    ////Open the file
                    fileName = new FileInfo("file.txt");
                    //Get the StreamReader
                    sr = fileName.OpenText();
                }

                //Delete all not saved list
                headOfList.head = null;
                textBufferList.GetIndex();


                //Peek to see if the next character exists
                while (sr.Peek() > -1)
                {
                    textBufferList.InsertLast(headOfList, sr.ReadLine()); //Copy each line from txt file in to the buffer
                }

                firstline = 1; // Reset first line to 1
                numberLine = textBufferList.GetIndex(); // Get the current line number
                currentAddress = textBufferList.GetIndex(); //Current Address is the last line entered

                //Close the file
                sr.Close();
            }
            catch (IOException e)
            {
                Console.WriteLine("A Error Occurred :" + e);
            }
            Console.ReadLine();
        }

        public uint getnumberLine()
        {
            return numberLine;
        }

        public void print() //Print everything from text buffer
        {
            Console.WriteLine("* Text Buffer *");
            textBufferList.PrintList(headOfList, 0, 0, 0);
        }

        public void printLine(uint line1, uint line2, DoubleLinkedList2 inputBuffer) //Print everything from text buffer
        {

            if (line2 <= textBufferList.GetIndex())
            {
                textBufferList.PrintList(headOfList, line1, line2, 3);
                currentAddress = line2;
            }
            else
            {
                Console.WriteLine("Line index error");
            }
        }

        public uint GetNumberOfLines()
        {
            return numberLine;
        }

        public uint GetCurrentAddress()
        {
            return currentAddress;
        }

    }
}
