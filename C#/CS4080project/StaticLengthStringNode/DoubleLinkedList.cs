using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace StaticLinkedListImplementation
{
    
    internal class DoubleLinkedList2
    {
        internal StaticStringLinkNode head;
    }
    //internal node for double linked list
    internal class StaticStringLinkNode
    {
        internal StringBuilder memory;
        internal int maxCapacity = 1024;

        internal StaticStringLinkNode prev;
        internal StaticStringLinkNode next;

        //Dynamic lenght string node
        public StaticStringLinkNode(string input)
        {
            memory = new StringBuilder(maxCapacity, maxCapacity); //initial size is 255
            memory.Append(input);

            prev = null;
            next = null;
        }

        public void Copy(string input) 
        {
            memory.Clear();
            memory.Append(input);         
        }

        public void Append(string input) { memory.Append(input); ; }

        public void Insert(int position, string input) { memory.Insert(position, input); }
    }


    internal class doublylinked2
    {
        private uint index = 0;
        #region Insert_into_DoubleLinkedList2


        internal void InsertFront(DoubleLinkedList2 doubleLinkedList, string data)
        {
            StaticStringLinkNode newNode = new StaticStringLinkNode(data);
            newNode.next = doubleLinkedList.head;
            newNode.prev = null;
            if (doubleLinkedList.head != null)
            {
                doubleLinkedList.head.prev = newNode;
                index++;
            }
            doubleLinkedList.head = newNode;
        }

        internal void InsertAfter(StaticStringLinkNode prev_node, string data)
        {
            if (prev_node == null)
            {
                Console.WriteLine("Error preoius node is nullT");
                return;
            }
            StaticStringLinkNode new_Node = new StaticStringLinkNode(data);

            new_Node.next = prev_node.next;
            prev_node.next = new_Node;
            new_Node.prev = prev_node;

            if (new_Node.next != null)
            {
                new_Node.next.prev = new_Node;
                index++;
            }
        }

        internal void InsertLast(DoubleLinkedList2 doubleLinkedList, string data)
        {
            StaticStringLinkNode new_Node = new StaticStringLinkNode(data);
            if (doubleLinkedList.head == null)
            {
                new_Node.prev = null;
                doubleLinkedList.head = new_Node;
                index++;
                return;
            }
            StaticStringLinkNode lastNode = GetLastNode(doubleLinkedList);
            lastNode.next = new_Node;
            new_Node.prev = lastNode;
            index++;
        }
        #endregion Insert_into_DoubleLinkedList2

        internal StaticStringLinkNode GetLastNode(DoubleLinkedList2 doubleLinkedList)
        {
            StaticStringLinkNode temp = doubleLinkedList.head;
            while (temp.next != null)
            {
                temp = temp.next;
            }
            return temp;
        }
       

        internal void DeleteNodeByLine( DoubleLinkedList2 doubleLinkedList, uint lineNumber)
        {
            StaticStringLinkNode temp_head = doubleLinkedList.head; //this is the head of linklist

            if (temp_head != null && lineNumber.Equals(1)) //remove first node
            {
                doubleLinkedList.head = temp_head.next; // Change first node to second node
                doubleLinkedList.head.prev = null; //Set new first node's pres to null
                index--;
                return;
            }
            lineNumber--;
            while (temp_head != null && lineNumber != 0)
            {
                lineNumber--;
                temp_head = temp_head.next;
            }
            if (temp_head == null)
            {
                return;
            }
            if (temp_head.next != null) // if current node has next value then set next node's pinter to one before current node.
            {
                temp_head.next.prev = temp_head.prev;
            }
            if (temp_head.prev != null) // if current node prev has value then set last node point to one after current node.
            {
                temp_head.prev.next = temp_head.next;
            }
            index--;
        }


        public void PrintList(DoubleLinkedList2 doubleLinkedList, uint line1, uint line2, int token)
        {
            if (line1.Equals(0) && line2.Equals(0) && token == 0)
            {
                StaticStringLinkNode n = doubleLinkedList.head;
                int numberLine = 1;
                while (n != null)
                {
                    Console.WriteLine(numberLine + " >> " + n.memory + " ");
                    numberLine++;
                    n = n.next;
                }
                numberLine = 1;
            }
            else if (token == 1)
            {
                StaticStringLinkNode n = doubleLinkedList.head;
                uint numberLine = line1;
                uint tempIndex = line1 - 1;


                while (n != null && tempIndex != 0) //This while loop is use to locate line 1
                {
                    tempIndex--;
                    n = n.next; // line1 loaction node, we will copy this node's tring to temp                      
                }

                for(int i = 0; i < (line2 - line1 + 1); i++)
                {
                    Console.WriteLine(numberLine + "> " + n.memory + " $");
                    numberLine++;
                    n = n.next;
                }
             }

            else if (token == 2)
            {
                StaticStringLinkNode n = doubleLinkedList.head;
                uint numberLine = line1;
                uint tempIndex = line1 - 1;


                while (n != null && tempIndex != 0) //This while loop is use to locate line 1
                {
                    tempIndex--;
                    n = n.next; // line 1 loaction node, we will copy this node's tring to temp                      
                }             

                for (int i = 0; i < (line2 - line1 + 1)  ; i++)
                {

                    Console.WriteLine("> " + numberLine +"\t" + n.memory);
                    numberLine++;
                    n = n.next;
                }
            }

            else if (token == 3)
            {
                StaticStringLinkNode n = doubleLinkedList.head;
                uint numberLine = line1;
                uint tempIndex = line1 - 1;


                while (n != null && tempIndex != 0) //This while loop is use to locate line 1
                {
                    tempIndex--;
                    n = n.next; // line 1 loaction node, we will copy this node's tring to temp                      
                }

                for (int i = 0; i < (line2 - line1 + 1); i++)
                {

                    Console.WriteLine("> " + n.memory);
                    numberLine++;
                    n = n.next;
                }
            }

        }

        public uint GetIndex( ) { return index; }

        public void ResetIndex( ) { index = 0; }
    }
}