package Memory;

/**
 * Created by davidmcfall on 3/29/16.
 */
public class Frame
{

    static Integer FRAME_SIZE = 4; // in words
    static Integer NUMBER_OF_FRAMES = (1024/4);

    private String[] lines = new String[FRAME_SIZE];

    private Integer frameID = 0;
    private Integer[] lineNumbers = new Integer[FRAME_SIZE];



    public Frame()
    {

    }

    public Frame(Integer frameID)
    {
        this.frameID = frameID;
    }

    public String getLine(Integer offset)
    {
        return lines[offset];
    }

    public Integer getFrameID()
    {
        return frameID;
    }

    public void setFrameEqualToPage(Page page)
    {
        this.lineNumbers[0] = page.getLineNumber(0);
        this.lineNumbers[1] = page.getLineNumber(1);
        this.lineNumbers[2] = page.getLineNumber(2);
        this.lineNumbers[3] = page.getLineNumber(3);
    }


}
