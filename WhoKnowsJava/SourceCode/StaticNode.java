import java.lang.StringBuilder;

public class StaticNode extends Node
{
    private static final int MAX_CAPACITY = 1024;
    private StringBuilder data;

    public StaticNode()
    {
        data = new StringBuilder(MAX_CAPACITY);
    }

    public StaticNode(String s)
    {
        String temp = s;
        if (s.length() > MAX_CAPACITY)
            temp = s.substring(0, MAX_CAPACITY);
        data = new StringBuilder(MAX_CAPACITY);
        data.append(temp);
    }

    public void copy(String s)
    {
        String temp = s;
        if (s.length() > MAX_CAPACITY)
            temp = s.substring(0, MAX_CAPACITY);
        data.delete(0, data.length());
        data.append(temp);
    }

    public void append(String s)
    {
        data.append(s);
    }

    public String get()
    {
        return data.toString();
    }
}
