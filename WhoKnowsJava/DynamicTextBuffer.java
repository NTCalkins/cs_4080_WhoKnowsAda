import java.util.LinkedList;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

/* Text buffer ADT that provides implementations for the supported commands */
public class DynamicTextBuffer
{
    private LinkedList<DynamicNode> text;
    private boolean changesMade;
    private int currAddr;
    private void disambiguousPrint(String s) /* Print a string disambiguously for use in list command */
    {
        for (char c : s.toCharArray())
        {
            switch (c)
            {
            case '$':
                System.out.print("\\$");
                break;
            case '\t':
                System.out.print("\\t");
                break;
            case '\\':
                System.out.print("\\\\");
                break;
            case '\'':
                System.out.print("\\\'");
                break;
            case '\"':
                System.out.print("\\\"");
                break;
            default:
                System.out.print(c);
            }
        }
        System.out.println('$');
    }

    public DynamicTextBuffer()
    {
        text = new LinkedList<DynamicNode>();
        changesMade = false;
        currAddr = 0;
    }
	
    public int size()
    {
        return text.size();
    }

    public int getAddr()
    {
        return currAddr;
    }

    public void append(int line, LinkedList<DynamicNode> buff)
    {
        assert line >= 0 && line <= text.size();
        text.addAll(line, buff);
        currAddr += buff.size();
        buff.clear();
        changesMade = true;
    }

    public void delete(int line1, int line2)
    {
        assert line2 >= line1 && line1 > 0 && line2 <= text.size();
        for (int i = line1 - 1; i < line2; ++i)
            text.remove(line1 - 1);
        currAddr = line1 + 1;
        changesMade = true;
    }

    public void change(int line1, int line2, LinkedList<DynamicNode> buff)
    {
        assert line2 >= line1 && line1 > 0 && line2 <= text.size();
        this.delete(line1, line2);
        this.append(line1 - 1, buff);
        changesMade = true;
    }

    public void join(int line1, int line2)
    {
        assert line2 >= line1 && line1 > 0 && line2 <= text.size();
        DynamicNode node = text.get(line1 - 1);
        for (int i = line1; i < line2; ++i)
        {
            node.append(text.get(line1).get());
            text.remove(line1);
        }
        currAddr = line1;
        changesMade = true;
    }

    public void transfer(int line1, int line2, int line3)
    {
        assert line2 >= line1 && line1 > 0 && line2 <= text.size() && line3 <= text.size();
        assert line3 < line1 || line3 > line2;
        LinkedList<DynamicNode> temp = new LinkedList<DynamicNode>();
        for (int i = line1 - 1; i < line2; ++i)
            temp.add(text.get(i));
        text.addAll(line3, temp);
        currAddr = line3 + temp.size();
        changesMade = true;
    }

    public void move(int line1, int line2, int line3)
    {
        assert line2 >= line1 && line1 > 0 && line2 <= text.size() && line3 <= text.size();
        assert line3 < line1 || line3 > line2;
        this.transfer(line1, line2, line3);
        if (line3 > line2)
        {
            this.delete(line1, line2);
            currAddr = line3;
        }
        else
        {
            this.delete(line1 + (line2 - line1 + 1), line2 + (line2 - line1 + 1));
            currAddr = line3 + (line2 - line1 + 1);
        }
        changesMade = true;
    }

    public void print(int line1, int line2)
    {
        for (int i = line1 - 1; i < line2; ++i)
            System.out.println(text.get(i).get());
        currAddr = line2;
    }

    public void list(int line1, int line2)
    {
        for (int i = line1 - 1; i < line2; ++i)
            disambiguousPrint(text.get(i).get());
        currAddr = line2;
    }

    public void number(int line1, int line2)
    {
        for (int i = line1 - 1; i < line2; ++i)
            System.out.printf("%d\t%s\n", i + 1, text.get(i).get());
        currAddr = line2;
    }

    public void write(int line1, int line2, String file) throws IOException
    {
        FileWriter outFile = new FileWriter(file, false);
        for (int i = line1 - 1; i < line2; ++i)
            outFile.write(text.get(i).get() + "\n");
        outFile.close();
    }

    public void edit(String file) throws IOException
    {
        BufferedReader inFile = new BufferedReader(new FileReader(file));
        String line;
        text.clear();
        while ((line = inFile.readLine()) != null)
        {
            DynamicNode temp = new DynamicNode(line);
            text.add(temp);
        }
        inFile.close();
    }

    public boolean changes()
    {
        return changesMade;
    }
}
