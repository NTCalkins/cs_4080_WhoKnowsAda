interface StaticLinkedListImplementation {
    public class StaticDoubleLinkedList{
        StaticNode head;
    }

    public class StaticNode{
        private int maxCapacity = 1024;
        StringBuilder sb;

        StaticNode prev;
        StaticNode next;

        public StaticNode(String input){
            sb = new StringBuilder(maxCapacity);
            if(input.length()<maxCapacity)
                sb.append(input);
            else
                System.out.println("Reached max capacity");

            prev = null;
            next = null;
        }

        public void copy(String input){
            if(input.length()<maxCapacity)
                sb = new StringBuilder(input);
            else
                System.out.println("Reached max capacity");
        }

        public void append(String input){
            //prevents going over max capacity
            if(sb.length()<maxCapacity && (sb.length()+input.length())<maxCapacity)
                sb.append(input);
            else
                System.out.println("Reached max capacity");
        }

        public void insert(int position, String input){
            if((sb.length()+input.length())<maxCapacity)
                sb.insert(position,input);
            else
                System.out.println("Reached max capacity");
        }
    }

    public class StaticDoublyLinked{
        private int index = 0;

        public void insertFront(StaticDoubleLinkedList doubleLinkedList, String data){
            StaticNode newNode = new StaticNode(data);
            newNode.next = doubleLinkedList.head;
            newNode.prev = null;
            if(doubleLinkedList.head!=null){
                doubleLinkedList.head.prev = newNode;
                index++;
            }
            doubleLinkedList.head = newNode;
        }

        public void insertAfter(StaticNode prevNode, String data){
            if(prevNode==null){
                System.out.println("Previous node is null.");
                return;
            }

            StaticNode newNode = new StaticNode(data);
            newNode.next = prevNode.next;
            prevNode.next = newNode;
            newNode.prev = prevNode;

            if(newNode.next!= null){
                newNode.next.prev = newNode;
                index++;
            }
        }

        public void insertLast(StaticDoubleLinkedList doubleLinkedList, String data){
            StaticNode newNode = new StaticNode(data);
            if(doubleLinkedList.head==null){
                newNode.prev = null;
                doubleLinkedList.head = newNode;
                index++;
                return;
            }
            StaticNode lastNode = getLastNode(doubleLinkedList);
            lastNode.next = newNode;
            newNode.prev = lastNode;
            index++;
        }

        public StaticNode getLastNode(StaticDoubleLinkedList doubleLinkedList){
            StaticNode temp = doubleLinkedList.head;
            while(temp.next!=null)
                temp = temp.next;
            return temp;
        }

        public void deleteNodeByLine(StaticDoubleLinkedList doubleLinkedList, int lineNumber){
            StaticNode tempHead = doubleLinkedList.head;
            if(tempHead!=null && lineNumber==1){
                doubleLinkedList.head = tempHead.next;
                doubleLinkedList.head.prev = null;
                index--;
                return;
            }

            lineNumber--;
            while(tempHead!=null && lineNumber!=0){
                lineNumber--;
                tempHead = tempHead.next;
            }

            if(tempHead==null)
                return;
            if(tempHead.next!=null)
                tempHead.next.prev = tempHead.prev;
            if(tempHead.prev!=null)
                tempHead.prev.next = tempHead.next;
            index--;
        }

        public void printList(StaticDoubleLinkedList doubleLinkedList, int line1, int line2, int token){
            if(line1==0 && line2==0 && token==0){
                StaticNode n = doubleLinkedList.head;
                int numberLine = 1;
                while (n!=null){
                    System.out.println(numberLine + " >> " + n.sb + " ");
                    numberLine++;
                    n = n.next;
                }
            }
            else if(token==1){
                StaticNode n = doubleLinkedList.head;
                int numberLine = line1;
                int tempIndex = line1-1;
                while(n!=null && tempIndex!=0){
                    tempIndex--;
                    n = n.next;
                }
                for(int i=0; i<(line2-line1+1); i++){
                    System.out.println(numberLine + " > " + n.sb + "$");
                    numberLine++;
                    n = n.next;
                }
            }
            else if(token==2){
                StaticNode n = doubleLinkedList.head;
                int numberLine = line1;
                int tempIndex = line1-1;
                while(n!=null && tempIndex!=0){
                    tempIndex--;
                    n = n.next;
                }
                for(int i=0; i<(line2-line1+1); i++){
                    System.out.println("> " + numberLine + "\t" + n.sb);
                    numberLine++;
                    n = n.next;
                }
            }
            else if(token==3){
                StaticNode n = doubleLinkedList.head;
                int numberLine = line1;
                int tempIndex = line1-1;
                while(n!=null && tempIndex!=0){
                    tempIndex--;
                    n = n.next;
                }
                for(int i=0; i<(line2-line1+1); i++){
                    System.out.println("> " + n.sb);
                    numberLine++;
                    n = n.next;
                }
            }
        }

        public int getIndex(){
            return index;
        }

        public void resetIndex(){
            index = 0;
        }
    }
}