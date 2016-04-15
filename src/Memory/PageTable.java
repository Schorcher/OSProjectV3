package Memory;

/**
 * Created by davidmcfall on 3/29/16.
 */
public class PageTable
{

    static Integer PAGE_TABLE_SIZE = 0;

    // page number - int
    // frame number - int
    // modified bit - bit
    // page fault test - bit
    //

    /**
     *  Page table size total = 512 pages
     *  Frame table size total = 256 Frames
     *
     *  4 words per page/frame
     *
     *
     *  logical address / 4 is the frame number, remainder is the offset
     */

}
