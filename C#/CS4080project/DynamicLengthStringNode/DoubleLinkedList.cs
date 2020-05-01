using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DynamicLinkedListImplementation
{   
    internal class DoubleLinkedList
    {
        internal DynamicStringLinkNode head;
    }
    //internal node for double linked list
    internal class DynamicStringLinkNode
    {
        internal string memory;
        internal int capacity = 10;
        internal int maxCapacity = 1024;

        internal DynamicStringLinkNode prev;
        internal DynamicStringLinkNode next;

        //Dynamic lenght string node
        public DynamicStringLinkNode(string input)
        {
            StringBuilder stringBuilder = new StringBuilder(capacity, maxCapacity); //initial size is 10
            stringBuilder.Append(input);

            memory = stringBuilder.ToString();

            prev = null;
            next = null;
        }

        public void Copy (string input) { memory = input; }

        public void Append (string input ) { memory = memory + input; }

        public void Insert(int position, string input) { memory.Insert(position, input); }
    }

    internal class doublylinked
    {
        private uint index = 0;
        #region Insert_into_DoubleLinkedList

        internal void InsertFront(DoubleLinkedList doubleLinkedList, string data)
        {
            DynamicStringLinkNode newNode = new DynamicStringLinkNode(data);
            newNode.next = doubleLinkedList.head;
            newNode.prev = null;
            if (doubleLinkedList.head != null)
            {
                doubleLinkedList.head.prev = newNode;
                index++;
            }
            doubleLinkedList.head = newNode;
        }

        internal void InsertAfter(DynamicStringLinkNode prev_node, string data)
        {
            if (prev_node == null)
            {
                Console.WriteLine("Error preoius node is nullT");
                return;
            }
            DynamicStringLinkNode new_Node = new DynamicStringLinkNode(data);

            new_Node.next = prev_node.next;
            prev_node.next = new_Node;
            new_Node.prev = prev_node;

            if (new_Node.next != null)
            {
                new_Node.next.prev = new_Node;
                index++;
            }
        }

        internal void InsertLast(DoubleLinkedList doubleLinkedList, string data)
        {
            DynamicStringLinkNode new_Node = new DynamicStringLinkNode(data);
            if (doubleLinkedList.head == null)
            {
                new_Node.prev = null;
                doubleLinkedList.head = new_Node;
                index++;
                return;
            }
            DynamicStringLinkNode lastNode = GetLastNode(doubleLinkedList);
            lastNode.next = new_Node;
            new_Node.prev = lastNode;
            index++;
        }
        #endregion Insert_into_DoubleLinkedList

        internal DynamicStringLinkNode GetLastNode(DoubleLinkedList doubleLinkedList)
        {
            DynamicStringLinkNode temp = doubleLinkedList.head;
            while (temp.next != null)
            {
                temp = temp.next;
            }
            return temp;
        }
       

        internal void DeleteNodeByLine( DoubleLinkedList doubleLinkedList, uint lineNumber)
        {
            DynamicStringLinkNode temp_head = doubleLinkedList.head; //this is the head of linklist
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
            if (temp_head.next != null) // if current node has next value then set next node's pointer to one before current node.
            {
                temp_head.next.prev = temp_head.prev;               
            }
            if (temp_head.prev != null) // if current node prev has value then set last node point to one after current node.
            {
                temp_head.prev.next = temp_head.next;
            }
            index--;
        }


        public void PrintList(DoubleLinkedList doubleLinkedList, uint line1, uint line2, int token)
        {
            if (line1.Equals(0) && line2.Equals(0) && token == 0)
            {
                DynamicStringLinkNode n = doubleLinkedList.head;
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
                DynamicStringLinkNode n = doubleLinkedList.head;
                uint numberLine = line1;
                uint tempIndex = line1 - 1;


                while (n != null && tempIndex != 0) //This while loop is use to locate line 1
                {
                    tempIndex--;
                    n = n.next; // line1 loaction node, we will copy this node's tring to temp                      
                }

                for(int i = 0; i < (line2 - line1 + 1); i++)
                {
                    Console.WriteLine(numberLine + " > " + n.memory + " $");
                    numberLine++;
                    n = n.next;
                }
             }

            else if (token == 2)
            {
                DynamicStringLinkNode n = doubleLinkedList.head;
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
                DynamicStringLinkNode n = doubleLinkedList.head;
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

        public uint GetIndex() { return index; }

        public void ResetIndex() { index = 0; }
    }
}