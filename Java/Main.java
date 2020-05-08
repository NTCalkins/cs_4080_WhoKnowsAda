import java.util.Scanner;

public class Main implements DynamicLinkedListImplementation, StaticLinkedListImplementation{
    public static void main(String[] args){
        Scanner kb = new Scanner(System.in);
        int input = -1;
        while(input!=0){
            System.out.print("(0) Exit program.\n"
                    + "(1) Dynamic-length string text editor.\n"
                    + "(2) Static-length string text editor\n"
                    + "Enter input: ");
            input = kb.nextInt();
            kb.nextLine();

            if(input==1)
               inputBufferDynamic();
            else if(input==2)
                System.out.println("woot");
                //inputBufferStatic();
        }
    }

    public static void inputBufferDynamic(){
        Scanner kb = new Scanner(System.in);

        String input = "";
        int currentAddress = 0;
        int line1 = 0;
        int line2 = 0;
        int line3 = 0;

        Stopwatch stopwatch = new Stopwatch();

        Parser parser = new Parser("",0);
        DynamicTextBuffer tempTextBuffer = new DynamicTextBuffer();
        DynamicDoublyLinked inputList = new DynamicDoublyLinked();
        DynamicDoubleLinkedList headOfList = new DynamicDoubleLinkedList();

        while(!input.equalsIgnoreCase("q")){
            inputList.resetIndex();
            System.out.print("");
            input = kb.nextLine();
            input = input.replace(" ", "");

            parser = new Parser(input,currentAddress);
            if(!parser.isAccept() && parser.getLine1()!=0)
                line1 = parser.getLine1();
            else if(parser.isAccept()){
                if(line1!=0)
                    line2 = parser.getLine1();
                else{
                    line1 = parser.getLine1();
                    line2 = parser.getLine2();
                    line3 = parser.getLine3();
                }
            }

            if(parser.isAccept()){
                switch (parser.getCommand()){
                    case 'a':
                        while(input.charAt(0)!='.'){
                            input = kb.nextLine();
                            if(input.charAt(0)=='.'){
                                int totalLine = inputList.getIndex();
                                tempTextBuffer.append(line1,headOfList,totalLine,currentAddress);
                                headOfList.head = null;
                                break;
                            }
                        }
                        inputList.insertLast(headOfList, input);
                        break;
                    case 'c':
                        while(input.charAt(0)!='.'){
                            input = kb.nextLine();
                            if(input.charAt(0)=='.'){
                                int totalLine = inputList.getIndex();
                                tempTextBuffer.change(line1,line2,headOfList,totalLine,currentAddress);
                                headOfList.head = null;
                                break;
                            }
                        }
                        inputList.insertLast(headOfList, input);
                        break;
                    case 'd':
                        tempTextBuffer.delete(line1,line2,currentAddress);
                        headOfList.head = null;
                        break;
                    case 'j':
                        tempTextBuffer.join(line1,line2,currentAddress);
                        headOfList.head = null;
                        break;
                    case 'l':
                        tempTextBuffer.list(line1,line2,currentAddress);
                        headOfList.head = null;
                        break;
                    case 'm':
                        tempTextBuffer.move(line1,line2,line3,currentAddress);
                        headOfList.head = null;
                        break;
                    case 'n':
                        tempTextBuffer.number(line1,line2,currentAddress);
                        headOfList.head = null;
                        break;
                    case 'p':
                        if(line1!=0 && line2!=0){
                            tempTextBuffer.printLine(line1,line2,headOfList);
                            headOfList.head = null;
                        }
                        break;
                    case 't':
                        tempTextBuffer.transfer(line1,line2,line3,currentAddress);
                        headOfList.head = null;
                        break;
                    case 'w':
                        tempTextBuffer.write(parser.getParsed());
                        headOfList.head = null;
                        break;
                    case 'e':
                        stopwatch.start();
                        tempTextBuffer.edit(parser.getParsed());
                        stopwatch.start();
                        System.out.println("File load time is " + stopwatch.getElapsedTime() + "ms.");
                        headOfList.head = null;
                        break;
                    default:
                        System.out.println("Invalid command");
                        break;
                }
                line1 = 0;
                line2 = 0;
                line3 = 0;
            }
        }
    }

    public static void inputBufferStatic(){
        Scanner kb = new Scanner(System.in);

        String input = "";
        int currentAddress = 0;
        int line1 = 0;
        int line2 = 3;
        int line3 = 0;

        Stopwatch stopwatch = new Stopwatch();

        Parser parser = new Parser("",0);
        StaticTextBuffer tempTextBuffer = new StaticTextBuffer();
        StaticDoublyLinked inputList = new StaticDoublyLinked();
        StaticDoubleLinkedList headOfList = new StaticDoubleLinkedList();

        while(!input.equalsIgnoreCase("q")){
            inputList.resetIndex();
            System.out.print("");
            input = kb.nextLine();
            input = input.replace(" ", "");

            parser = new Parser(input,currentAddress);
            if(!parser.isAccept() && parser.getLine1()!=0)
                line1 = parser.getLine1();
            else if(parser.isAccept()){
                if(line1!=0)
                    line2 = parser.getLine1();
                else{
                    line1 = parser.getLine1();
                    line2 = parser.getLine2();
                    line3 = parser.getLine3();
                }
            }

            if(parser.isAccept()){
                switch (parser.getCommand()){
                    case 'a':
                        while(input.charAt(0)!='.'){
                            input = kb.nextLine();
                            if(input.charAt(0)=='.'){
                                int totalLine = inputList.getIndex();
                                tempTextBuffer.append(line1,headOfList,totalLine,currentAddress);
                                headOfList.head = null;
                                break;
                            }
                        }
                        inputList.insertLast(headOfList, input);
                        break;
                    case 'c':
                        while(input.charAt(0)!='.'){
                            input = kb.nextLine();
                            if(input.charAt(0)=='.'){
                                int totalLine = inputList.getIndex();
                                tempTextBuffer.change(line1,line2,headOfList,totalLine,currentAddress);
                                headOfList.head = null;
                                break;
                            }
                        }
                        inputList.insertLast(headOfList, input);
                        break;
                    case 'd':
                        tempTextBuffer.delete(line1,line2,currentAddress);
                        headOfList.head = null;
                        break;
                    case 'j':
                        tempTextBuffer.join(line1,line2,currentAddress);
                        headOfList.head = null;
                        break;
                    case 'l':
                        tempTextBuffer.list(line1,line2,currentAddress);
                        headOfList.head = null;
                        break;
                    case 'm':
                        tempTextBuffer.move(line1,line2,line3,currentAddress);
                        headOfList.head = null;
                        break;
                    case 'n':
                        tempTextBuffer.number(line1,line2,currentAddress);
                        headOfList.head = null;
                        break;
                    case 'p':
                        if(line1!=0 && line2!=0){
                            tempTextBuffer.printLine(line1,line2,headOfList);
                            headOfList.head = null;
                        }
                        break;
                    case 't':
                        tempTextBuffer.transfer(line1,line2,line3,currentAddress);
                        headOfList.head = null;
                        break;
                    case 'w':
                        tempTextBuffer.write(parser.getParsed());
                        headOfList.head = null;
                        break;
                    case 'e':
                        stopwatch.start();
                        tempTextBuffer.edit(parser.getParsed());
                        stopwatch.start();
                        System.out.println("File load time is " + stopwatch.getElapsedTime() + "ms.");
                        headOfList.head = null;
                        break;
                    default:
                        System.out.println("Invalid command");
                        break;
                }
                line1 = 0;
                line2 = 0;
                line3 = 0;
            }
        }
    }
}
