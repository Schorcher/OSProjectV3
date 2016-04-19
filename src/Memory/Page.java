package Memory;

/**
 * Created by davidmcfall on 3/29/16.
 */
public class Page
{

    static Integer PAGE_SIZE = 4; // in words
    static Integer NUMBER_OF_PAGES = (2048/4);

    private String[] lines = new String[PAGE_SIZE];




    public Page()
    {

    }

    public String getLine(Integer offset)
    {
        return lines[offset];
    }

}
