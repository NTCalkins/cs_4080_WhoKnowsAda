import java.lang.StringBuilder;

public class DynamicNode extends Node
{
    private static final int INITIAL_CAPACITY = 8;
    private StringBuilder data;

    public DynamicNode()
    {
        data = new StringBuilder(INITIAL_CAPACITY);
    }

    public DynamicNode(String s)
    {
        data = new StringBuilder(INITIAL_CAPACITY);
        data.append(s);
    }

    public void copy(String s)
    {
        data.delete(0, data.length());
        data.append(s);
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
