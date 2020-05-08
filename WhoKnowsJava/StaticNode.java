import java.lang.StringBuilder

public class StaticNode implements Node
{
	private static const int MAX_CAPACITY;
	private StringBuilder data;

	public DynamicNode()
	{
		data = new StringBuilder(MAX_CAPACITY);
	}

	public DynamicNode(String s)
	{
		if (s.length() > MAX_CAPACITY)
			s = s.substring(0, MAX_CAPACITY);
		data = new StringBuilder(MAX_CAPACITY);
		data.append(s);
	}

	public void copy(String s)
	{
		if (s.length() > MAX_CAPACITY)
			s = s.substring(0, MAX_CAPACITY);
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
