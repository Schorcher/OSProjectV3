package OS;

import CPU.CPU;
import Memory.MemoryManager;
import Misc.Util;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by David McFall on 2/10/16.
 */
public class OSDriver
{
    Util util = new Util();

    Loader loader = new Loader(this);
    MemoryManager memoryManager = new MemoryManager(this);

    CPU cpu1=new CPU(this),cpu2=new CPU(this),cpu3=new CPU(this),cpu4=new CPU(this);

    ConcurrentLinkedDeque<ProcessControlBlock> newQueue = new ConcurrentLinkedDeque<>();
    ConcurrentLinkedDeque<ProcessControlBlock> readyQueue = new ConcurrentLinkedDeque<>();
    ConcurrentLinkedDeque<ProcessControlBlock> terminatedQueue = new ConcurrentLinkedDeque<>();
    ConcurrentLinkedDeque<ProcessControlBlock> waitQueue = new ConcurrentLinkedDeque<>();
    ConcurrentLinkedDeque<ProcessControlBlock> blockedQueue = new ConcurrentLinkedDeque<>();

    volatile boolean notDone = true;
    long SYSTEM_START_TIME = System.nanoTime();
    long SYSTEM_END_TIME;


    /**
     * Driver
     * {
     *      loader();
     *      loop
     *      {
     *          longScheduler();
     *          dispatcher();
     *          CPU();
     *          waitforinterrupt();
     *      }
     * }
     */

    public OSDriver()
    {
        cpu1.setName("CPU-1");
        cpu2.setName("CPU-2");
        cpu3.setName("CPU-3");
        cpu4.setName("CPU-4");
    }

    public void runOS()
    {

        loader.loadFile();

        scheduler();
        scheduler();
        scheduler();
        scheduler();

        cpu1.start();
        cpu2.start();
        cpu3.start();
        cpu4.start();

        while (notDone)
        {
            scheduler();
        }


        shutdownOS();

    }

    /**
     * The Scheduler
     *
     * The Scheduler loads programs from the disk into RAM according to the given scheduling policy. The scheduler
     * must note in the PCB, which physical addresses in RAM each program/job begins, and ends. This ‘start’ address
     * must be stored in the base-register (or program-counter) of the job). The Scheduler module must also use the
     * Input/Output buffer size information (extracted from the control cards) for allocating spaces in RAM for the
     * input and output data. It may be instructive to store the start addresses of the input-buffer and output-buffer
     * spaces allocated in RAM as well. (Note that a job’s program-counter, which is a component of the PCB, is
     * different from the virtual CPU’s program-counter – see below). The Scheduler module either loads one program
     * or multiple programs at a time (in a multiprogramming system). Thus, the Scheduler works closely with the
     * Memory manager and the Effective-Address method to load jobs into RAM.
     *
     *
     */

    //TODO: Change this to use pages in memory
    public void scheduler()
    {
        if(!newQueue.isEmpty())
        {
            try {
                ProcessControlBlock processInfo = newQueue.getFirst();

                if(memoryManager.checkAvailableMemoryForJob(processInfo.fullCodeSizeInDecimal()))
                {
                    int availableMemorySlot = memoryManager.findBestFitSpaceInMemory(processInfo
                            .fullCodeSizeInDecimal());

                    if(availableMemorySlot != -1)
                    {
                        Integer filePointer = processInfo.diskPointer;
                        Integer codeSize = processInfo.fullCodeSizeInDecimal();
                        Integer memStart = availableMemorySlot;

                        loader.loadProgramToRam(filePointer,codeSize,memStart);

                        processInfo.setMemoryPointer(memStart);
                    }
                    else {
                        return;
                    }
                    readyQueue.add(newQueue.pollFirst());
                }
            } catch (Exception ex)
            {
                print("Cant");
                ex.printStackTrace();
            }
        }
    }

    public void shortScheduler()
    {

    }

    private void orderBy_SJF()
    {

    }

    private void orderBy_Priority()
    {

    }

    private void orderBy_FIFO()
    {

    }

    public void shutdownOS()
    {
        SYSTEM_END_TIME = System.nanoTime();

        try {
            Thread.sleep(500);
        } catch (Exception ex)
        {

        }

        //printProcessOutputs();

        //Util.p("");

        //printPhase1Part1Info();

        print("OS Finished after: " + (SYSTEM_END_TIME-SYSTEM_START_TIME) + " (ns)");

    }

    private void printProcessOutputs()
    {
        ArrayList<ProcessControlBlock> tempList = new ArrayList<>();
        for(int i=0; i<30; i++)
        {
            ProcessControlBlock process = terminatedQueue.poll();
            print("Printing Output Buffer for program: " + process.getProcessID());
            for(int j=0; j<util.parseHexToDecimal(process.getOutputBufferCount()); j++)
            {
                print(memoryManager.getFromDisk(process.getDiskPointer()+process.getRelativeOutputBufferStartPoint()
                        +j));
            }
            print("End Output Buffer");
        }
    }

    private void printWaitingTimes()
    {
        print("Printing Waiting Times for programs: ");
        for(int i=0; i<30; i++)
        {
            ProcessControlBlock process = terminatedQueue.poll();
            print("Process: " + process.getProcessID() + "\t\tWaiting Time: " + process.getWaitingTime() + " (ns)");
        }
        print("End Waiting Times");
    }

    private void printCompletionTimes()
    {
        print("Printing Completion Times for programs: ");
        for(int i=0; i<30; i++)
        {
            ProcessControlBlock process = terminatedQueue.poll();
            print("Process: " + process.getProcessID() + "\t\tCompletion Time: " + process.getComlpetionTime() + " (ns)");
        }
        print("End Completion Times");
    }

    private void printWaitingAndCompletionTimesForEachCPU()
    {
        ArrayList<ProcessControlBlock> list1 = new ArrayList<>(),
                list2 = new ArrayList<>(),list3 = new ArrayList<>(),list4 = new ArrayList<>();

        for(int i=0; i<30; i++)
        {

        }
    }

    private void printPhase1Part1Info()
    {
        ArrayList<Long> completionTimes = new ArrayList<>();
        ArrayList<Long> waitTimes = new ArrayList<>();
        ArrayList<Integer> IOCounts = new ArrayList<>();
        ArrayList<Double> percentRAMUsed = new ArrayList<>();
        ArrayList<ProcessControlBlock> cpu1Programs = new ArrayList<>();
        ArrayList<ProcessControlBlock> cpu2Programs = new ArrayList<>();
        ArrayList<ProcessControlBlock> cpu3Programs = new ArrayList<>();
        ArrayList<ProcessControlBlock> cpu4Programs = new ArrayList<>();

        print("Printing Phase 1 Part 1 info for programs: ");
        for(int i=0; i<30; i++)
        {
            ProcessControlBlock process = terminatedQueue.poll();

            switch (process.getCpuID())
            {
                case "CPU-1":
                    cpu1Programs.add(process);
                    break;
                case "CPU-2":
                    cpu2Programs.add(process);
                    break;
                case "CPU-3":
                    cpu3Programs.add(process);
                    break;
                case "CPU-4":
                    cpu4Programs.add(process);
                    break;
            }

            print("------------ Process " + process.getProcessID() + " --------------");
            print("CPU ID = " + process.getCpuID());
            completionTimes.add(process.getProcessRunTime());
            print("Completion Time: " + process.getProcessRunTime() + " (ns)");
            waitTimes.add(process.getWaitingTime());
            print("Waiting Time: " + process.getWaitingTime() + " (ns)");
            IOCounts.add(process.getNumberOfIO_Operations());
            print("IO Operations made: " + process.getNumberOfIO_Operations());
            percentRAMUsed.add(process.getRamPercentageUsed());
            print("Percent RAM used: " + process.getRamPercentageUsed() + "%");
            print("---------------- END -----------------");
        }
        long averageCompletionTime = 0;
        for(Long time : completionTimes) {
            averageCompletionTime += time;
        }
        printYellow("Average Completion Time: " + averageCompletionTime/30 + " (ns)");
        long averageWaitTime = 0;
        for(Long time : waitTimes) {
            averageWaitTime += time;
        }
        printYellow("Average Wait Time: " + averageWaitTime/30 + " (ns)");

        printYellow("CPU-1 number of programs ---> " + cpu1Programs.size());
        printYellow("CPU-2 number of programs ---> " + cpu2Programs.size());
        printYellow("CPU-3 number of programs ---> " + cpu3Programs.size());
        printYellow("CPU-4 number of programs ---> " + cpu4Programs.size());

        print("End Phase 1 Part 1 Info");
    }

    public Loader getLoader() {
        return loader;
    }

    public MemoryManager getMemoryManager() {
        return memoryManager;
    }

    public ConcurrentLinkedDeque<ProcessControlBlock> getReadyQueue() {
        return readyQueue;
    }

    public ConcurrentLinkedDeque<ProcessControlBlock> getNewQueue() {
        return newQueue;
    }

    public ConcurrentLinkedDeque<ProcessControlBlock> getTerminatedQueue() {
        return terminatedQueue;
    }

    public ConcurrentLinkedDeque<ProcessControlBlock> getWaitQueue() {
        return waitQueue;
    }

    public ConcurrentLinkedDeque<ProcessControlBlock> getBlockedQueue() {
        return blockedQueue;
    }

    public boolean isNotDone() {
        return notDone;
    }

    public void setNotDone(boolean notDone) {
        this.notDone = notDone;
    }

    /**
     *  Static short print method
     *
     * @param s
     * @param <T>
     */
    public static <T> void print(T s)
    {
        System.out.println("[OS Driver]: " + s);
    }

    public <T> void printYellow(T s)
    {
        System.out.println(Util.ANSI_GREEN + "[OS Driver]: " + s + Util.ANSI_RESET);
    }
}
