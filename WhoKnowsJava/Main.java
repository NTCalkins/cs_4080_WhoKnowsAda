import java.util.Scanner;
import java.util.LinkedList;
import java.io.IOException;

public class Main
{
    /*================================================
      To swap between static/dynamic strings,
      uncomment one of the following definitions
      ================================================*/
    static private void inputMode(String input, LinkedList<DynamicNode> buff) /* Enter input mode for dynamic buffer */
    {
        Scanner scan = new Scanner(System.in);
        input = scan.nextLine();
        while (!input.equals("."))
        {
            buff.add(new DynamicNode(input));
            input = scan.nextLine();
        }
    }

    // static private void inputMode(String input, LinkedList<StaticNode> buff) /* Enter input mode for static buffer */
    // {
    //     Scanner scan = new Scanner(System.in);
    //     input = scan.nextLine();
    //     while (!input.equals("."))
    //     {
    //         buff.add(new DynamicNode(input));
    //         input = scan.nextLine();
    //     }
    // }
    
    public static void main(String[] args) throws IOException
    { /* Main driver for editor. Does not support command suffixing */
        boolean done = false;
        Scanner scan = new Scanner(System.in);
        String input = ""; /* Input from user */
        String defaultFile = "file"; /* Default file name */
        Parser p = new Parser();
        /*================================================
          To swap between static/dynamic strings,
          comment/uncomment the following.
          ================================================*/
        /* Using dynamic strings for text buffer */
        DynamicTextBuffer textBuffer = new DynamicTextBuffer();
        LinkedList<DynamicNode> inputBuffer = new LinkedList<DynamicNode>();
        /* Using static strings for text buffer */
        // StaticTextBuffer textBuffer = new StaticTextBuffer();
        // LinkedList<StaticNode> inputBuffer = new LinkedList<StaticNode>();
        if (args.length > 1)
        {
            System.out.println("Error: Too many arguments.");
            return;
        }
        else if (args.length == 1) /* File name passed as command line argument */
        {
            defaultFile = args[0]; /* Set argument as default file name */
            textBuffer.edit(defaultFile); /* Load file contents into buffer */
        }
        while (!done)
        {
            input = scan.nextLine();
            p.parse(textBuffer.getAddr(), textBuffer.size(), input);
            if (p.command == Parser.INVALID_CMD && p.addr1 != Parser.INVALID_ADDR && p.addr2 == Parser.INVALID_ADDR) /* Only an address was entered */
                textBuffer.setAddr(p.addr1);
            else
                switch (p.command)
                {
                case 'a': /* Enter input mode and insert after addressed line */
                    if (p.addr2 != Parser.INVALID_ADDR) /* Received 2 address arguments */
                    {
                        System.out.println("Error: This command does not take an address range.");
                        break;
                    }
                    else if (p.addr1 == Parser.INVALID_ADDR && p.addr2 == Parser.INVALID_ADDR) /* Received no address arguments */
                        p.addr1 = textBuffer.getAddr(); /* Use current address */
                    if (p.addr1 >= 0 && p.addr1 <= textBuffer.size())
                    {
                        inputMode(input, inputBuffer); /* Enter input mode and fill input buffer */
                        textBuffer.append(p.addr1, inputBuffer); /* Insert input buffer after inputted address */
                    }
                    else
                        System.out.println("Error: Invalid address.");
                    break;
                case 'i': /* Enter input mode and insert before addressed line */
                    if (p.addr2 != Parser.INVALID_ADDR) /* Received 2 address arguments */
                    {
                        System.out.println("Error: This command does not take an address range.");
                        break;
                    }
                    else if (p.addr1 == Parser.INVALID_ADDR && p.addr2 == Parser.INVALID_ADDR) /* Received no address arguments */
                        p.addr1 = textBuffer.getAddr(); /* Use current address */
                    if (p.addr1 >= 0 && p.addr1 <= textBuffer.size())
                    {
                        inputMode(input, inputBuffer); /* Enter input mode and fill input buffer */
                        textBuffer.append((p.addr1 == 0) ? 0 : (p.addr1 - 1), inputBuffer); /* Insert input buffer after inputted address */
                    }
                    else
                        System.out.println("Error: Invalid address.");
                    break;
                case 'c': /* Enter input mode and change out addressed lines with input buffer */
                    if (p.addr1 == Parser.INVALID_ADDR && p.addr2 == Parser.INVALID_ADDR) /* Received no address */
                        p.addr1 = p.addr2 = textBuffer.getAddr(); /* Use current address */
                    else if (p.addr2 == Parser.INVALID_ADDR) /* Received only one address */
                        p.addr2 = p.addr1; /* Affect only address 1 */
                    else if (p.addr1 == Parser.INVALID_ADDR) /* Received only address two */
                        p.addr1 = textBuffer.getAddr();
                    if (p.addr2 >= p.addr1 && p.addr1 > 0 && p.addr2 <= textBuffer.size())
                    {
                        inputMode(input, inputBuffer); /* Enter input mode and fill input buffer */
                        textBuffer.change(p.addr1, p.addr2, inputBuffer);
                    }
                    else
                        System.out.println("Error: Invalid address.");
                    break;
                case 'd': /* Delete addressed lines from buffer */
                    if (p.addr1 == Parser.INVALID_ADDR && p.addr2 == Parser.INVALID_ADDR) /* Received no address */
                        p.addr1 = p.addr2 = textBuffer.getAddr(); /* Use current address */
                    else if (p.addr2 == Parser.INVALID_ADDR) /* Received only one address */
                        p.addr2 = p.addr1; /* Affect only address 1 */
                    else if (p.addr1 == Parser.INVALID_ADDR) /* Received only address two */
                        p.addr1 = textBuffer.getAddr();
                    if (p.addr2 >= p.addr1 && p.addr1 > 0 && p.addr2 <= textBuffer.size())
                        textBuffer.delete(p.addr1, p.addr2);
                    else
                        System.out.println("Error: Invalid address.");
                    break;
                case 'j': /* Replace addressed lines with their concatenation */
                    if (p.addr1 == Parser.INVALID_ADDR && p.addr2 == Parser.INVALID_ADDR) /* Received no address */
                        p.addr1 = p.addr2 = textBuffer.getAddr(); /* Use current address */
                    else if (p.addr2 == Parser.INVALID_ADDR) /* Received only one address */
                        p.addr2 = p.addr1; /* Affect only address 1 */
                    else if (p.addr1 == Parser.INVALID_ADDR) /* Received only address two */
                        p.addr1 = textBuffer.getAddr();
                    if (p.addr2 >= p.addr1 && p.addr1 > 0 && p.addr2 <= textBuffer.size())
                        textBuffer.join(p.addr1, p.addr2);
                    else
                        System.out.println("Error: Invalid address.");
                    break;
                case 'p': /* Print out addressed lines */
                    if (p.addr1 == Parser.INVALID_ADDR && p.addr2 == Parser.INVALID_ADDR) /* Received no address */
                        p.addr1 = p.addr2 = textBuffer.getAddr(); /* Use current address */
                    else if (p.addr2 == Parser.INVALID_ADDR) /* Received only one address */
                        p.addr2 = p.addr1; /* Affect only address 1 */
                    else if (p.addr1 == Parser.INVALID_ADDR) /* Received only address two */
                        p.addr1 = textBuffer.getAddr();
                    if (p.addr2 >= p.addr1 && p.addr1 > 0 && p.addr2 <= textBuffer.size())
                        textBuffer.print(p.addr1, p.addr2);
                    else
                        System.out.println("Error: Invalid address.");
                    break;
                case 'l': /* Print out addressed lines unambiguously */
                    if (p.addr1 == Parser.INVALID_ADDR && p.addr2 == Parser.INVALID_ADDR) /* Received no address */
                        p.addr1 = p.addr2 = textBuffer.getAddr(); /* Use current address */
                    else if (p.addr2 == Parser.INVALID_ADDR) /* Received only one address */
                        p.addr2 = p.addr1; /* Affect only address 1 */
                    else if (p.addr1 == Parser.INVALID_ADDR) /* Received only address two */
                        p.addr1 = textBuffer.getAddr();
                    if (p.addr2 >= p.addr1 && p.addr1 > 0 && p.addr2 <= textBuffer.size())
                        textBuffer.list(p.addr1, p.addr2);
                    else
                        System.out.println("Error: Invalid address.");
                    break;
                case 'n': /* Print and number addressed lines */
                    if (p.addr1 == Parser.INVALID_ADDR && p.addr2 == Parser.INVALID_ADDR) /* Received no address */
                        p.addr1 = p.addr2 = textBuffer.getAddr(); /* Use current address */
                    else if (p.addr2 == Parser.INVALID_ADDR) /* Received only one address */
                        p.addr2 = p.addr1; /* Affect only address 1 */
                    else if (p.addr1 == Parser.INVALID_ADDR) /* Received only address two */
                        p.addr1 = textBuffer.getAddr();
                    if (p.addr2 >= p.addr1 && p.addr1 > 0 && p.addr2 <= textBuffer.size())
                        textBuffer.number(p.addr1, p.addr2);
                    else
                        System.out.println("Error: Invalid address.");
                    break;
                case 'm':
                    if (p.addr1 == Parser.INVALID_ADDR && p.addr2 == Parser.INVALID_ADDR) /* Received no address */
                        p.addr1 = p.addr2 = textBuffer.getAddr(); /* Use current address */
                    else if (p.addr2 == Parser.INVALID_ADDR) /* Received only one address */
                        p.addr2 = p.addr1; /* Affect only address 1 */
                    else if (p.addr1 == Parser.INVALID_ADDR) /* Received only address two */
                        p.addr1 = textBuffer.getAddr();
                    if (p.addr2 >= p.addr1 && p.addr1 > 0 && p.addr2 <= textBuffer.size() && p.addr3 != Parser.INVALID_ADDR && (p.addr3 < p.addr1 || p.addr3 > p.addr2) && p.addr3 <= textBuffer.size())
                        textBuffer.move(p.addr1, p.addr2, p.addr3);
                    else
                        System.out.println("Error: Invalid address.");
                    break;
                case 't': /* Copies addressed lines to after the destination address */
                    if (p.addr1 == Parser.INVALID_ADDR && p.addr2 == Parser.INVALID_ADDR) /* Received no address */
                        p.addr1 = p.addr2 = textBuffer.getAddr(); /* Use current address */
                    else if (p.addr2 == Parser.INVALID_ADDR) /* Received only one address */
                        p.addr2 = p.addr1; /* Affect only address 1 */
                    else if (p.addr1 == Parser.INVALID_ADDR) /* Received only address two */
                        p.addr1 = textBuffer.getAddr();
                    if (p.addr2 >= p.addr1 && p.addr1 > 0 && p.addr2 <= textBuffer.size() && p.addr3 != Parser.INVALID_ADDR && (p.addr3 < p.addr1 || p.addr3 > p.addr2) && p.addr3 <= textBuffer.size())
                        textBuffer.transfer(p.addr1, p.addr2, p.addr3);
                    else
                        System.out.println("Error: Invalid address.");
                    break;
                case 'w': /* Write to a file */
                    if (p.addr1 == Parser.INVALID_ADDR && p.addr2 == Parser.INVALID_ADDR) /* Received no address arguments */
                    {
                        /* Use whole buffer */
                        p.addr1 = 1;
                        p.addr2 = textBuffer.size();
                    }
                    else if (p.addr2 == Parser.INVALID_ADDR) /* Received only one address */
                        p.addr2 = p.addr1;
                    else if (p.addr1 == Parser.INVALID_ADDR) /* Received only address two */
                        p.addr1 = textBuffer.getAddr();
                    if (p.param.length() == 0) /* Use default file name if none is specified */
                        p.param = defaultFile;
                    else /* Set default file name to given name */
                        defaultFile = p.param;
                    if (p.addr2 >= p.addr1 && p.addr1 > 0 && p.addr2 <= textBuffer.size())
                        textBuffer.write(p.addr1, p.addr2, p.param);
                    else
                        System.out.println("Error: Invalid address.");
                    break;
                case 'e': /* Load contents from file */
                    if (p.param.length() == 0) /* Use default file name if none is specified */
                        p.param = defaultFile;
                    else /* Set default file name to given name */
                        defaultFile = p.param;
                    textBuffer.edit(p.param);
                    break;
                case 'q':
                    if (textBuffer.changes())
                    {
                        input = "";
                        while (!input.equals("yes") && !input.equals("no"))
                        {
                            System.out.print("Unsaved changes in buffer. Still quit? (yes/no): ");
                            input = scan.nextLine();
                        }
                        if (input.equals("yes"))
                            done = true;
                    }
                    else
                        done = true;
                    break;
                default:
                    System.out.println("Error: Unknown command.");
                }
        }
    }
}
