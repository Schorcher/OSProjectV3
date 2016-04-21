package Memory;

/**
 * Created by davidmcfall on 3/29/16.
 */
public class Page
{
    static Integer PAGE_SIZE = 4; // in words
    static Integer FRAME_SIZE = 4;

    static Integer NUMBER_OF_PAGES = (2048/4);
    static Integer NUMBER_OF_FRAMES = (1024/4);

    private Integer ID = 0;

    private String[] lines = new String[PAGE_SIZE];

    private Integer[] lineNumbers = new Integer[PAGE_SIZE];

    private Boolean isAvailable = true;


    public Page()
    {

    }

    public Page(Integer ID)
    {
        this.ID = ID;
    }

    public String getLine(Integer offset)
    {
        return lines[offset];
    }

    public Integer getLineNumber(Integer offset)
    {
        return lineNumbers[offset];
    }

    public void setLineNumber(Integer offset, Integer newValue)
    {
        lineNumbers[offset] = newValue;
    }

    public String getFreeLine()
    {
        for(String line : lines)
        {
            if(line == null)
            {
                return line;
            }
            else
            {
                // Do nothing
            }
        }

        return null;
    }

    public Boolean hasFreeLine()
    {
        for (String line : lines)
        {
            if(line != null)
            {
                //return false;
            }
            else
            {
                return true;
            }
        }
        return false;
    }

    public Integer getID()
    {
        return ID;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }

    public Integer[] getLineNumbers() {
        return lineNumbers;
    }

    public void setLineNumbers(Integer[] lineNumbers) {
        this.lineNumbers = lineNumbers;
    }

    public String[] getLines() {
        return lines;
    }

    public void setLines(String[] lines) {
        this.lines = lines;
    }
}
