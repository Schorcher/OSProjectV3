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
    private Integer frameID = 0;
    private Integer[] pageLineNumbers = new Integer[PAGE_SIZE];
    private Integer[] frameLineNumbers = new Integer[PAGE_SIZE];
    private Boolean isAvailable = true;
    private Boolean isInMemory = false;
    private Boolean hasBeenModified = false;
    private Boolean isShared = false;

    public Page()
    {

    }

    public Page(Integer ID)
    {
        this.ID = ID;
    }

    public Integer getPageLineNumber(Integer offset)
    {
        return pageLineNumbers[offset];
    }

    public void setPageLineNumber(Integer offset, Integer newValue)
    {
        pageLineNumbers[offset] = newValue;
    }

    public Integer getFrameLineNumber(Integer offset)
    {
        return frameLineNumbers[offset];
    }

    public void setFrameLineNumber(Integer offset, Integer newValue)
    {
        pageLineNumbers[offset] = newValue;
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

    public Integer[] getPageLineNumbers() {
        return pageLineNumbers;
    }

    public Integer[] getFrameLineNumbers() {
        return frameLineNumbers;
    }

    public Integer getFrameID() {
        return frameID;
    }

    public void setFrameID(Integer frameID) {
        this.frameID = frameID;
    }

    public Boolean getInMemory() {
        return isInMemory;
    }

    public void setInMemory(Boolean inMemory) {
        isInMemory = inMemory;
    }

    public Boolean getHasBeenModified() {
        return hasBeenModified;
    }

    public void setHasBeenModified(Boolean hasBeenModified) {
        this.hasBeenModified = hasBeenModified;
    }

    public Boolean getShared() {
        return isShared;
    }

    public void setShared(Boolean shared) {
        isShared = shared;
    }
}
