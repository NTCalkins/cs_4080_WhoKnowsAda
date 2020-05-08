interface DynamicLinkedListImplementation {
    public class DynamicDoubleLinkedList{
        public DynamicNode head;

		public DynamicDoubleLinkedList(){
			head = new DynamicNode();
		}
    }

    public class DynamicNode{
        private int capacity = 10;
        public StringBuilder sb;

        public DynamicNode prev;
        public DynamicNode next;

        public DynamicNode(String input){
            sb = new StringBuilder(capacity);
            sb.append(input);

            prev = null;
            next = null;
        }

		public DynamicNode(){
			sb = new StringBuilder(capacity);
			prev = null;
			next = null;
		}

        public void copy(String input){
            sb = new StringBuilder(input);
        }

        public void append(String input){
            sb.append(input);
        }

        public void insert(int position, String input){
            sb.insert(position,input);
        }
    }

    public class DynamicDoublyLinked{
        private int index = 0;

        public void insertFront(DynamicDoubleLinkedList doubleLinkedList, String data){
            DynamicNode newNode = new DynamicNode(data);
            newNode.next = doubleLinkedList.head;
            newNode.prev = null;
            if(doubleLinkedList.head!=null){
                doubleLinkedList.head.prev = newNode;
                index++;
            }
            doubleLinkedList.head = newNode;
        }

        public void insertAfter(DynamicNode prevNode, String data){
            if(prevNode==null){
                System.out.println("Previous node is null.");
                return;
            }

            DynamicNode newNode = new DynamicNode(data);
            newNode.next = prevNode.next;
            prevNode.next = newNode;
            newNode.prev = prevNode;

            if(newNode.next!= null){
                newNode.next.prev = newNode;
                index++;
            }
        }

        public void insertLast(DynamicDoubleLinkedList doubleLinkedList, String data){
            DynamicNode newNode = new DynamicNode(data);
            if(doubleLinkedList.head==null){
                newNode.prev = null;
                doubleLinkedList.head = newNode;
                index++;
                return;
            }
            DynamicNode lastNode = getLastNode(doubleLinkedList);
            lastNode.next = newNode;
            newNode.prev = lastNode;
            index++;
        }

        public DynamicNode getLastNode(DynamicDoubleLinkedList doubleLinkedList){
            DynamicNode temp = doubleLinkedList.head;
            while(temp.next!=null)
                temp = temp.next;
            return temp;
        }

        public void deleteNodeByLine(DynamicDoubleLinkedList doubleLinkedList, int lineNumber){
            DynamicNode tempHead = doubleLinkedList.head;
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

        public void printList(DynamicDoubleLinkedList doubleLinkedList, int line1, int line2, int token){
            if(line1==0 && line2==0 && token==0){
                DynamicNode n = doubleLinkedList.head;
                int numberLine = 1;
                while (n!=null){
                    System.out.println(numberLine + " >> " + n.sb + " ");
                    numberLine++;
                    n = n.next;
                }
            }
            else if(token==1){
                DynamicNode n = doubleLinkedList.head;
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
                DynamicNode n = doubleLinkedList.head;
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
                DynamicNode n = doubleLinkedList.head;
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
