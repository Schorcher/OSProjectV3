package Memory;

/**
 * Created by davidmcfall on 3/29/16.
 */
public class Frame
{

    static Integer FRAME_SIZE = 4; // in words
    static Integer NUMBER_OF_FRAMES = (1028/4);

    private String[] lines = new String[FRAME_SIZE];




    public Frame()
    {

    }

    public String getLine(Integer offset)
    {
        return lines[offset];
    }

}
