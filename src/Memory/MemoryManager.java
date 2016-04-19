package Memory;

import Misc.Util;
import OS.OSDriver;

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
    private Page[] frameList = new Page[Frame.NUMBER_OF_FRAMES];    // 256 Frames

    private ConcurrentLinkedDeque<Page> freeFrameList = new ConcurrentLinkedDeque<>();



    public void setupPageTable()
    {
        setupPages();
        setupFrames();
    }

    private void setupPages()
    {
        Integer count = 0, pageCount = 0;
        for(Page page : pageList)
        {
            page = new Page(pageCount);
            pageCount++;
            page.setLineNumber(0,count);
            count++;
            page.setLineNumber(1,count);
            count++;
            page.setLineNumber(2,count);
            count++;
            page.setLineNumber(3,count);
            count++;
        }
    }

    private void setupFrames()
    {
        Integer frameCount = 0;
        for(Page frame : frameList)
        {
            frame = new Page(frameCount);
            frameCount++;
            freeFrameList.add(frame);
        }
    }

    private void translatePageToFrame()
    {

    }

    private boolean pageFault(Page page)
    {
        return true;
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
