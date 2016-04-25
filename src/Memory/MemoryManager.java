package Memory;

import Misc.Util;
import OS.OSDriver;
import OS.ProcessControlBlock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by David McFall on 2/15/16.
 */
public class MemoryManager
{

    OSDriver osDriver;

    /**
     * Memory:
     *
     * This method is the only module of your simulator by which RAM can be accessed. A known absolute/physical
     * address must always be passed to this method. The Memory simply fetches an instruction or datum or writes
     * datum into RAM (or cache – more on this later!).
     *
     *
     */

    /**
     *  Default Constructor
     */
    public MemoryManager(OSDriver osDriver)
    {
        this.osDriver = osDriver;
    }

    /***************************************************************************************
     *
     *                          Page Table Methods
     *
     ***************************************************************************************/
    private Page[] pageList = new Page[Page.NUMBER_OF_PAGES];       // 512 Pages
    private Page[] frameList = new Page[Page.NUMBER_OF_FRAMES];    // 256 Frames

    private ConcurrentLinkedDeque<Page> pageListQueue = new ConcurrentLinkedDeque<>();
    private ConcurrentLinkedDeque<Integer> freeFrameNumberList = new ConcurrentLinkedDeque<>();
    private ConcurrentLinkedDeque<Integer> pageFaults = new ConcurrentLinkedDeque<>();

    public void setupPageTable()
    {
        setupPages();
        //setupFrames();
        updateFreeFrameNumberList();
    }

    /*************************************
     *
     *           FRAME METHODS
     *
     *************************************/
    private ReadWriteLock frameLock = new ReentrantReadWriteLock(true);
    private void setupFrames()
    {
        /*Integer count = 0, frameCount = 0;
        for(Page frame : frameList)
        {
            frame = new Page(frameCount);
            frameCount++;
            frame.setLineNumber(0,count);
            count++;
            frame.setLineNumber(1,count);
            count++;
            frame.setLineNumber(2,count);
            count++;
            frame.setLineNumber(3,count);
            count++;
            freeFrameList.add(frame);
        }*/
    }

    public Page getFrame(Integer pageNumber)
    {
       return null;
    }

    public Integer findFrame(Integer pageNumber)
    {
        frameLock.readLock().lock();
        try{
            for(int i=0; i<frameList.length; i++)
            {
                if(frameList[i].getID().equals(pageNumber))
                {
                    return i;
                }
            }
            return -1;
        }finally {
            frameLock.readLock().unlock();
        }
    }

    //TODO: Finish page faults
    public void handlePageFault(ProcessControlBlock process)
    {
        try{
            Integer faultNumber = process.getFaultNumbers().pollFirst();

            if(faultNumber != null)
            {
                pageFault(faultNumber);
                handlePageFault(process);
            }
        }catch (Exception ex)
        {

        }
    }

    public void pageFault(Integer pageNumber)
    {
        writePageToMemory(getPage(pageNumber));
    }

    public void writePageToMemory(Page page)
    {
        frameLock.readLock().lock();
        try{
            //Integer freeFrameNumber = getFreeFrameNumber();
            Integer freeFrameNumber = freeFrameNumberList.poll();
            if(freeFrameNumber != -1)
            {
                page.setFrameID(freeFrameNumber);
                frameList[freeFrameNumber] = page;

                writeToMemory(getFreeMemoryLine(),getFromDisk(page.getPageLineNumber(0)));
                writeToMemory(getFreeMemoryLine(),getFromDisk(page.getPageLineNumber(1)));
                writeToMemory(getFreeMemoryLine(),getFromDisk(page.getPageLineNumber(2)));
                writeToMemory(getFreeMemoryLine(),getFromDisk(page.getPageLineNumber(3)));

                page.setInMemory(true);
            }
            else{
                return;
            }
        }finally {
            frameLock.readLock().unlock();
        }
    }

    private void clearFrameMemory(Page frame)
    {
        writeToMemory(frame.getFrameLineNumber(0),null);
        writeToMemory(frame.getFrameLineNumber(1),null);
        writeToMemory(frame.getFrameLineNumber(2),null);
        writeToMemory(frame.getFrameLineNumber(3),null);
    }

    public void clearFrame(Page frame)
    {
        if(frame.getHasBeenModified())
        {
            writeFrameToDisk(frame);
        }
        clearFrameMemory(frame);
        freeFrameNumberList.add(frame.getFrameID());
        frameList[frame.getFrameID()] = null;
        frame.setInMemory(false);
    }

    public void writeFrameToDisk(Page frame)
    {
        frameLock.readLock().lock();
        try {
            storeInDisk(getMemoryLine(frame.getFrameLineNumber(0)),frame.getPageLineNumber(0));
            storeInDisk(getMemoryLine(frame.getFrameLineNumber(1)),frame.getPageLineNumber(1));
            storeInDisk(getMemoryLine(frame.getFrameLineNumber(2)),frame.getPageLineNumber(2));
            storeInDisk(getMemoryLine(frame.getFrameLineNumber(3)),frame.getPageLineNumber(3));
        }finally {
            frameLock.readLock().unlock();
        }
    }

    public void updateFreeFrameNumberList()
    {
        frameLock.readLock().lock();
        try {
            for(Integer i=0; i<frameList.length; i++)
            {
                if( (frameList[i]== null) && (!freeFrameNumberList.contains(i)) )
                {
                    freeFrameNumberList.add(i);
                }
            }
        }finally {
            frameLock.readLock().unlock();
        }
    }

    public ConcurrentLinkedDeque<Integer> getFreeFrameNumberList() {
        return freeFrameNumberList;
    }
    public ConcurrentLinkedDeque<Integer> getPageFaults() {
        return pageFaults;
    }
    public Page[] getFrameList() {
        return frameList;
    }
    public Integer getFreeFrameNumber()
    {
        for(Integer i=0; i<frameList.length; i++)
        {
            if(frameList[i] == null)
            {
                return i;
            }
        }
        return -1;
    }


    /*************************************
     *
     *           PAGE METHODS
     *
     *************************************/
    private ReentrantReadWriteLock pageLock = new ReentrantReadWriteLock();
    private void setupPages()
    {
        pageLock.writeLock().lock();
        try {
            Integer count = 0, pageCount = 0;
            for(int i=0; i<pageList.length; i++)
            {
                Page page = new Page(pageCount);
                pageCount++;
                page.setPageLineNumber(0,count);
                count++;
                page.setPageLineNumber(1,count);
                count++;
                page.setPageLineNumber(2,count);
                count++;
                page.setPageLineNumber(3,count);
                count++;

                pageList[i] = page;
            }
        }finally {
            pageLock.writeLock().unlock();
        }
    }

    public Boolean pageHasFreeLine(Page page)
    {
        pageLock.readLock().lock();
        try {
            for(Integer lineNumber : page.getPageLineNumbers())
            {
                if(getFromDisk(lineNumber) == null)
                {
                    return true;
                }
            }
            return false;
        }finally {
            pageLock.readLock().unlock();
        }
    }

    public Page getFirstFreePage()
    {
        pageLock.readLock().lock();
        try {
            for(Page page : pageList)
            {
                if(pageHasFreeLine(page))
                {
                    return page;
                }
            }
            return null;
        }finally {
            pageLock.readLock().unlock();
        }
    }

    public void storeInFreePageLine(Page page, String lineToStore)
    {
        pageLock.writeLock().lock();
        try{
            for(Integer lineNumber : page.getPageLineNumbers())
            {
                if(getFromDisk(lineNumber) != null)
                {
                    storeInDisk(lineToStore,lineNumber);
                    return;
                }
            }
        }finally {
            pageLock.writeLock().unlock();
        }
    }

    public Page[] getPageList() {
        return pageList;
    }

    public Page getPage(Integer pageNumber)
    {
        pageLock.readLock().lock();
        try {
            return pageList[pageNumber];
        }finally {
            pageLock.readLock().unlock();
        }
    }


    /***************************************************************************************
     *
     *                          Memory Methods
     *
     ***************************************************************************************/
    private ReadWriteLock memoryLock = new ReentrantReadWriteLock(true);
    private Integer MEMORY_SIZE = 1024;           // size of available ram (1024 words)
    private String[] memory = new String[MEMORY_SIZE];

    public String getMemoryLine(int location)
    {
        memoryLock.readLock().lock();
        try{
            return memory[location];
        }finally {
            memoryLock.readLock().unlock();
        }
    }

    public void writeToMemory(int location, String data)
    {
        memoryLock.writeLock().lock();
        try {
            memory[location] = data;
        }finally {
            memoryLock.writeLock().unlock();
        }
    }

    public int getAmountOfFreeMemory()
    {
        memoryLock.readLock().lock();
        try {
            int count = 0;
            for(String str : memory)
            {
                if(str == null)
                {
                    count++;
                }
            }
            return count;
        }finally {
            memoryLock.readLock().unlock();
        }
    }

    public Boolean checkAvailableMemoryForJob(int jobSize)
    {
        return jobSize <= getAmountOfFreeMemory();
    }

    public Integer findBestFitSpaceInMemory(Integer jobSize)
    {
        memoryLock.readLock().lock();
        try {
            if (!checkAvailableMemoryForJob(jobSize))
            {
                return -1;
            }

            int goodStart = -1;
            int count = 0;

            for(int i=100; i < MEMORY_SIZE; i++)
            {
                if(memory[i] == null)
                {
                    if(goodStart == -1)
                    {
                        goodStart = i;
                        count = 0;
                    }else {
                        count++;
                    }
                    if(count == (jobSize) )
                    {
                        return goodStart;
                    }
                }
                else{
                    goodStart = -1;
                }
            }
            return -1;
        }finally {
            memoryLock.readLock().unlock();
        }
    }

    public void removeFromRam(Integer programStartPoint, Integer programSize)
    {
        //Util.p("");
        //print("Cleaning Ram...");
        memoryLock.writeLock().lock();
        try {
            for(int i=0; i<programSize; i++)
            {
                int currentLine = programStartPoint + i;
                //print("Old RAM value = " + peekAtMemory(currentLine,key));
                memory[currentLine] = null;
                //print("New RAM value = " + peekAtMemory(currentLine,key));
            }
        }finally {
            memoryLock.writeLock().unlock();
        }
        //print("Free memory = " + getAmountOfFreeMemory());
    }

    public Integer getFreeMemoryLine()
    {
        memoryLock.readLock().lock();
        try{
            int count = 0;
            for (String str : memory)
            {
                if(str == null)
                {
                    return count;
                }
                else
                {
                    count++;
                }
            }
            return -1;
        }finally {
            memoryLock.readLock().unlock();
        }
    }

    public Boolean hasAvailableSpace(int spaceRequired)
    {
        memoryLock.readLock().lock();
        try{
            int count = 0;
            for (String str : memory)
            {
                if(str == null)
                {
                    count++;
                }
            }
            if (count >= spaceRequired)
            {
                return true;
            }
            else {
                return false;
            }

        }finally {
            memoryLock.readLock().unlock();
        }
    }

    /***************************************************************************************
     *
     *                          Disk Methods
     *
     ***************************************************************************************/

    private ReadWriteLock diskLock = new ReentrantReadWriteLock(true);
    private Integer DISK_SIZE = 2048;           // size of available ram (1024 words)
    private String[] disk = new String[DISK_SIZE];

    public void storeInDisk(String lineToStore)
    {
        diskLock.writeLock().lock();
        try {
            disk[getDiskCurrentSize()] = lineToStore;
        }finally {
            diskLock.writeLock().unlock();
        }
    }

    public void storeInDisk(String lineToStore, Integer lineNumber)
    {
        diskLock.writeLock().lock();
        try {
            disk[lineNumber] = lineToStore;
        }finally {
            diskLock.writeLock().unlock();
        }
    }

    public  String getFromDisk(Integer index)
    {
        diskLock.readLock().lock();
        try {
            return disk[index];
        }finally {
            diskLock.readLock().unlock();
        }
    }

    public Double getDiskPercentageFull()
    {
        return round((( (double) getDiskCurrentSize() / (double) getDiskMaxSize() ) * 100),2) ;
    }

    public Integer getDiskCurrentSize()
    {
        diskLock.readLock().lock();
        try {
            Integer count = 0;

            for(String str : disk)
            {
                if(str != null)
                {
                    count++;
                }
            }

            return count;
        }finally {
            diskLock.readLock().unlock();
        }
    }

    public Integer getDiskMaxSize()
    {
        return disk.length;
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void printDiskContents()
    {
        diskLock.readLock().lock();
        try {
            int count =0;
            print("-------------------------- Disk Contents ---------------------------------");
            for(String str : disk)
            {
                print("Index:\t" + count + "\tValue:\t" + str);
                count++;
            }
            print("------------------------------- End --------------------------------------");
        }finally {
            diskLock.readLock().unlock();
        }
    }


    /**
     *  Static short print method
     *
     * @param s
     * @param <T>
     */
    public static <T> void print(T s)
    {
        System.out.println("[Memory Manager]: " + s);
    }
}
