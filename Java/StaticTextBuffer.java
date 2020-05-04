import java.io.*;
import java.util.Scanner;

public class StaticTextBuffer implements StaticLinkedListImplementation {
    StaticDoublyLinked textBuffer = new StaticDoublyLinked();
    StaticDoubleLinkedList headOfList = new StaticDoubleLinkedList();
    int currentAddress;
    int firstLine = 1;
    int numberLine = 1;

    public void append(int line, StaticDoubleLinkedList inputBuffer, int totalLine, int mainBufferLine){
        if(line==9999){
            StaticNode temp = inputBuffer.head;
            if(temp!=null)
                textBuffer.insertFront(headOfList, temp.sb.toString());
            StaticNode temp2 = headOfList.head;
            temp = temp.next;
            while(temp!=null){
                textBuffer.insertAfter(temp2, temp.sb.toString());
                temp = temp.next;
                temp2 = temp2.next;
            }
            numberLine = textBuffer.getIndex();
            currentAddress = textBuffer.getIndex();
            mainBufferLine = textBuffer.getIndex();
        }
        else if (line==0 || line==textBuffer.getIndex())
        {
            StaticNode temp = inputBuffer.head;
            if (temp != null)
                textBuffer.insertLast(headOfList, temp.sb.toString());
            temp = temp.next;
            while (temp != null) {
                textBuffer.insertLast(headOfList, temp.sb.toString());
                temp = temp.next;
            }
            numberLine = textBuffer.getIndex(); // Get the current line number
            currentAddress = textBuffer.getIndex(); //Current Address is the last line entered
            mainBufferLine = textBuffer.getIndex();
        }
        else{
            StaticNode temp = inputBuffer.head;
            StaticNode temp2 = headOfList.head;
            if(line==1){
                if(temp!=null)
                    textBuffer.insertAfter(headOfList.head, temp.sb.toString());
                temp = temp.next;
                temp2 = temp2.next;
                while(temp!=null){
                    textBuffer.insertAfter(temp2,temp.sb.toString());
                    temp = temp.next;
                    temp2 = temp2.next;
                }
                numberLine = textBuffer.getIndex();
                currentAddress = totalLine+1;
                mainBufferLine = textBuffer.getIndex();
            }
            else if(textBuffer.getIndex()>=line){
                for(int i=0; i<line-1; i++)
                    temp2 = temp2.next;
                if(temp!=null)
                    textBuffer.insertAfter(temp2,temp.sb.toString());
                temp = temp.next;
                temp2 = temp2.next;
                while(temp!=null){
                    textBuffer.insertAfter(temp2,temp.sb.toString());
                    temp = temp.next;
                    temp2 = temp2.next;
                }
                mainBufferLine = textBuffer.getIndex();
                numberLine = textBuffer.getIndex();
                currentAddress = totalLine+numberLine;
            }
            else
                System.out.println("Wrong input line number.");
        }
    }

    public void change(int line1, int line2, StaticDoubleLinkedList inputBuffer, int totalLine, int mainBufferLine){
        if(line1==1 && line2==0){
            append(line1,inputBuffer,0,mainBufferLine);
            delete(1,0,mainBufferLine);
            numberLine = textBuffer.getIndex();
            currentAddress = totalLine+1;
            mainBufferLine = textBuffer.getIndex();
        }
        else if(line1 > 1 && line2==0){
            delete(line1,0,mainBufferLine);
            append(line1-1,inputBuffer,0,mainBufferLine);
            numberLine = textBuffer.getIndex();
            currentAddress = totalLine + line1;
            mainBufferLine = textBuffer.getIndex();
        }
        else if(line2>=line1){
            delete(line1,line2,mainBufferLine);
            append(line1-1,inputBuffer,0,mainBufferLine);
            numberLine = textBuffer.getIndex();
            currentAddress = totalLine+line1;
            mainBufferLine = textBuffer.getIndex();
        }
    }

    public void delete(int line1, int line2, int mainBufferLine){
        if(line1>=1 && line2==0){
            textBuffer.deleteNodeByLine(headOfList,line1);
            numberLine = textBuffer.getIndex();
            currentAddress = numberLine-1;
            mainBufferLine = textBuffer.getIndex();
        }
        else if(line1==line2){
            textBuffer.deleteNodeByLine(headOfList,line1);
            numberLine = textBuffer.getIndex();
            currentAddress = numberLine-1;
            mainBufferLine = textBuffer.getIndex();
        }
        else if(line2>line1){
            for(int i=0; i<line2-line1+1; i++)
                textBuffer.deleteNodeByLine(headOfList,line1);
            numberLine = textBuffer.getIndex();
            currentAddress = numberLine-1;
            mainBufferLine = textBuffer.getIndex();
        }
        else
            System.out.println("Wrong line number");
    }

    public void join(int line1, int line2, int mainBufferLine){
        int index = line1;
        int index2 = line2;
        String copy;
        if(line2>line1 && line2!=line1){
            StaticNode temp = headOfList.head;
            StaticNode temp2 = headOfList.head;

            if(line1==1){
                copy = headOfList.head.sb.toString();
                index2--;
                while(temp!=null && index2!=0){
                    temp = temp.next;
                    copy = copy + " " + temp.sb;
                    index2--;
                }
                textBuffer.insertAfter(headOfList.head, copy);
                delete(line1,0,mainBufferLine);
                delete(2,line2,mainBufferLine);
                numberLine = textBuffer.getIndex();
                currentAddress = line1;
                mainBufferLine = textBuffer.getIndex();
            }
            else{
                index = line1-1;
                index2 = line2-line1+1;
                while(temp!=null && index!=0){
                    temp = temp.next;
                    temp2 = temp2.next;
                    index--;
                }
                copy = temp.sb.toString();
                index2--;
                while(temp!=null && index2!=0){
                    temp = temp.next;
                    copy = copy + " " + temp.sb;
                    index2--;
                }
                delete(line1,line2,mainBufferLine);
                textBuffer.insertAfter(temp.prev,copy);
                numberLine = textBuffer.getIndex();
                currentAddress = line1;
                mainBufferLine = textBuffer.getIndex();
            }
        }
        else return;
    }

    public void move(int line1, int line2, int line3, int mainBufferLine){
        int index = line1;
        int index2 = line2;
        int index3 = line3;
        StaticDoublyLinked tempList = new StaticDoublyLinked();
        StaticDoubleLinkedList tempHeadOfList = new StaticDoubleLinkedList();

        if(line1>=1 && line2==0 && line3>=0 && line3!=line1) {
            if (headOfList.head != null && line1 == 1) {
                tempList.insertLast(tempHeadOfList, headOfList.head.sb.toString());
                delete(1, 0, mainBufferLine);
                append(line3 - 1, tempHeadOfList, 0, mainBufferLine);
                numberLine = textBuffer.getIndex();
                currentAddress = line3;
                mainBufferLine = textBuffer.getIndex();
            } else if (line3 >= 1) {
                StaticNode temp = headOfList.head;
                StaticNode temp2 = headOfList.head;
                index--;
                index3--;
                while (temp != null && index != 0) {
                    index--;
                    temp = temp.next;
                }
                while (temp2 != null && index3 != 0) {
                    index3--;
                    temp2 = temp2.next;
                }
                delete(line1, 0, mainBufferLine);
                textBuffer.insertAfter(temp2, temp.sb.toString());
                numberLine = textBuffer.getIndex();
                currentAddress = line3;
                mainBufferLine = textBuffer.getIndex();
            } else if (line3 == 0) {
                StaticNode temp = headOfList.head;
                index--;
                index3--;
                while (temp != null && index != 0) {
                    index--;
                    temp = temp.next;
                }
                delete(line1, 0, mainBufferLine);
                textBuffer.insertFront(headOfList, temp.sb.toString());
                numberLine = textBuffer.getIndex();
                currentAddress = line3;
                mainBufferLine = textBuffer.getIndex();
            }
        }
        else if(line1>=0 && line2==line1 && line3>=1 && line3!=line1){
            if(headOfList.head!=null && line1==1){
                tempList.insertLast(tempHeadOfList,headOfList.head.sb.toString());
                delete(1,0,mainBufferLine);
                append(line3-1,tempHeadOfList,0,mainBufferLine);
                numberLine = textBuffer.getIndex();
                currentAddress = line3;
                mainBufferLine = textBuffer.getIndex();
            }
            else if(line3>=1){
                StaticNode temp = headOfList.head;
                StaticNode temp2 = headOfList.head;
                index--;
                index3--;
                while(temp!=null && index!=0){
                    index--;
                    temp = temp.next;
                }
                while(temp2!=null && index3!=0){
                    index3--;
                    temp2 = temp2.next;
                }
                delete(line1,0,mainBufferLine);
                textBuffer.insertAfter(temp2,temp.sb.toString());
                numberLine = textBuffer.getIndex();
                currentAddress = line3;
                mainBufferLine = textBuffer.getIndex();
            }
            else if(line3==0){
                StaticNode temp = headOfList.head;
                StaticNode temp2 = headOfList.head;
                index--;
                index3--;
                while(temp!=null && index!=0){
                    index--;
                    temp = temp.next;
                }
                while(temp2!=null && index3!=0){
                    index3--;
                    temp2 = temp2.next;
                }
                delete(line1,0,mainBufferLine);
                textBuffer.insertAfter(temp2,temp.sb.toString());
                numberLine = textBuffer.getIndex();
                currentAddress = line3;
                mainBufferLine = textBuffer.getIndex();
            }
        }
        else if(line2>line1 && !(line1<=line3 && line3<=line2)){
            index = line1-1;
            index2 = line2-line1+1;
            StaticNode temp = headOfList.head;
            StaticNode temp2 = headOfList.head;
            while(temp!=null && index!=0){
                temp = temp.next;
                index--;
            }
            while(temp!=null && index2!=0){
                tempList.insertLast(tempHeadOfList,temp.sb.toString());
                temp = temp.next;
                index2--;
            }
            if(line3==0){
                delete(line1,line2,mainBufferLine);
                append(9999, tempHeadOfList,0,mainBufferLine);
                numberLine = textBuffer.getIndex();
                currentAddress = line3 + (line2-line1+1);
                mainBufferLine = textBuffer.getIndex();
            }
            else if(line3>line2){
                delete(line1,line2,mainBufferLine);
                append(line3-(line2-line1+1), tempHeadOfList,0,mainBufferLine);
                numberLine = textBuffer.getIndex();
                currentAddress = line3;
                mainBufferLine = textBuffer.getIndex();
            }
            else if(line3<line1){
                delete(line1,line2,mainBufferLine);
                append(line3, tempHeadOfList,0,mainBufferLine);
                numberLine = textBuffer.getIndex();
                currentAddress = line3 + (line2-line1+1);
                mainBufferLine = textBuffer.getIndex();
            }
            else
                System.out.println("Wrong line index");
        }
    }

    public void list(int line1, int line2, int mainBufferLine){
        if(line2>=line1){
            System.out.println("* Print line *");
            textBuffer.printList(headOfList,line1,line2,1);
            firstLine = line1;
            currentAddress = line2;
            mainBufferLine = textBuffer.getIndex();
        }
        else
            System.out.println("Wrong line index");
    }

    public void number(int line1, int line2, int mainBufferLine){
        if(line2>=line1){
            System.out.println("* Print line *");
            textBuffer.printList(headOfList,line1,line2,2);
            currentAddress = line2;
            mainBufferLine = textBuffer.getIndex();
        }
        else
            System.out.println("Wrong line index");
    }

    public void transfer(int line1, int line2, int line3, int mainBufferLine){
        int index = line1;
        int index2 = line2;
        int index3 = line3;
        StaticDoublyLinked tempList = new StaticDoublyLinked();
        StaticDoubleLinkedList tempHeadOfList = new StaticDoubleLinkedList();

        if(line1>=1 && line2==0 && line3 >=0 && line3!=line1){
            if (headOfList.head != null && line1 == 1) {
                tempList.insertLast(tempHeadOfList, headOfList.head.sb.toString());
                append(line3 - 1, tempHeadOfList, 0, mainBufferLine);
                numberLine = textBuffer.getIndex();
                currentAddress = line3+1;
                mainBufferLine = textBuffer.getIndex();
            }
            else if(line3>=1){
                StaticNode temp = headOfList.head;
                StaticNode temp2 = headOfList.head;
                index--;
                index3--;
                while(temp!=null && index!=0){
                    index--;
                    temp = temp.next;
                }
                while(temp2!=null && index3!=0){
                    index3--;
                    temp2 = temp2.next;
                }
                textBuffer.insertAfter(temp2,temp.sb.toString());
                numberLine = textBuffer.getIndex();
                currentAddress = line3+1;
                mainBufferLine = textBuffer.getIndex();
            }
            else if(line3==0){
                StaticNode temp = headOfList.head;
                index--;
                index3--;
                while(temp!=null && index!=0){
                    index--;
                    temp = temp.next;
                }
                textBuffer.insertFront(headOfList,temp.sb.toString());
                numberLine = textBuffer.getIndex();
                currentAddress = line3;
                mainBufferLine = textBuffer.getIndex();
            }
        }
        else if(line1>=1 && line2==line1 && line3>=1 && line3!=line1){
            if (headOfList.head != null && line1 == 1) {
                tempList.insertLast(tempHeadOfList, headOfList.head.sb.toString());
                append(line3 - 1, tempHeadOfList, 0, mainBufferLine);
                numberLine = textBuffer.getIndex();
                currentAddress = line3+1;
                mainBufferLine = textBuffer.getIndex();
            }
            else{
                StaticNode temp = headOfList.head;
                StaticNode temp2 = headOfList.head;
                index--;
                index3--;
                while(temp!=null && index!=0){
                    index--;
                    temp = temp.next;
                }
                while(temp2!=null && index3!=0){
                    index3--;
                    temp2 = temp2.next;
                }
                textBuffer.insertAfter(temp2,temp.sb.toString());
                numberLine = textBuffer.getIndex();
                currentAddress = line3+1;
                mainBufferLine = textBuffer.getIndex();
            }
        }
        else if(line2>line1){
            if(line3==0){
                index = line1-1;
                index2 = line2-line1+1;
                StaticNode temp = headOfList.head;
                StaticNode temp2 = headOfList.head;
                index3--;
                while(temp!=null && index!=0){
                    index--;
                    temp = temp.next;
                }
                while(temp!=null && index2!=0){
                    tempList.insertLast(tempHeadOfList,temp.sb.toString());
                    temp = temp.next;
                    index2--;
                }
                append(9999,tempHeadOfList,0,mainBufferLine);
                numberLine = textBuffer.getIndex();
                currentAddress = line3+(line2-line1+1);
                mainBufferLine = textBuffer.getIndex();
            }
            else{
                index = line1-1;
                index2 = line2-line1+1;
                StaticNode temp = headOfList.head;
                StaticNode temp2 = headOfList.head;
                index3--;
                while(temp!=null && index!=0){
                    index--;
                    temp = temp.next;
                }
                while(temp!=null && index2!=0){
                    tempList.insertLast(tempHeadOfList,temp.sb.toString());
                    temp = temp.next;
                    index2--;
                }
                append(line3,tempHeadOfList,0,mainBufferLine);
                numberLine = textBuffer.getIndex();
                currentAddress = line3+(line2-line1+1);
                mainBufferLine = textBuffer.getIndex();
            }
        }
        else
            System.out.println("Wrong line index");
    }

    public void write(String inputName){
        PrintWriter pw;
        FileWriter fw;
        try{
            if(!inputName.equals("")){
                File fileName = new File(inputName);
                fw = new FileWriter(fileName,false);
                pw = new PrintWriter(fileName);
            }
            else{
                File fileName = new File("file.txt");
                fw = new FileWriter(fileName,false);
                pw = new PrintWriter(fileName);
            }
            StaticNode n = headOfList.head;
            int numberLine = firstLine;
            int tempIndex = textBuffer.getIndex();
            for(int i=0; i<tempIndex; i++){
                fw.write("" + n.sb);
                numberLine++;
                n = n.next;
            }

            pw.close();
            fw.close();
        } catch (IOException e) {
            System.out.println("File not found: " + e);
        }
    }

    public void edit(String inputName){
        File fileName;
        Scanner reader;
        try{
            if(!inputName.equals("")){
                fileName = new File(inputName);
                reader = new Scanner(fileName);
            }
            else{
                fileName = new File("file.txt");
                reader = new Scanner(fileName);
            }

            headOfList.head = null;
            textBuffer.getIndex();

            while(reader.hasNextLine()){
                textBuffer.insertLast(headOfList,reader.nextLine());
            }
            firstLine = 1;
            numberLine = textBuffer.getIndex();
            currentAddress = textBuffer.getIndex();

            reader.close();
        } catch (IOException e) {
            System.out.println("File not found: " + e);
        }
        Scanner kb = new Scanner(System.in);
        kb.nextLine();
    }

    public int getNumberLine(){
        return numberLine;
    }

    public void print(){
        System.out.println("* Text Buffer *");
        textBuffer.printList(headOfList,0,0,0);
    }

    public void printLine(int line1, int line2, StaticDoubleLinkedList inputBuffer){
        if(line2<=textBuffer.getIndex()){
            textBuffer.printList(headOfList,line1,line2,3);
            currentAddress = line2;
        }
        else{
            System.out.println("Wrong line index");
        }
    }

    public int getCurrentAddress(){
        return currentAddress;
    }
}