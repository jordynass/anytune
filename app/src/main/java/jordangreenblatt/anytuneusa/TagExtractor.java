package jordangreenblatt.anytuneusa;

public class TagExtractor
{
	public static String extractTag(String line, String tag)
	{
        tag = "<" + tag + ">";
        if (!line.contains(tag)) return "";

        int beginIndex = -1;
        int endIndex = 0;
        while (endIndex < line.length())
        {
            if (line.charAt(endIndex) == '>')
                beginIndex = endIndex+1;
            if (beginIndex >= 0 && line.charAt(endIndex) == '<')
                break;
            endIndex++;
        }
        return line.substring(beginIndex, endIndex);
	}
}

