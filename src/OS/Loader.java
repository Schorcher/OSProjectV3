package OS;

import Memory.Page;
import Misc.Util;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by David McFall on 2/10/16.
 */
public class Loader
{
    /**
     * The Loader
     * The loader module opens (once at the start) the ‘program-file’ and performs the loading process. Programs are
     * loaded into disk according to the format of the batched user programs in the program-file. Ancillary programs
     * would be needed to process (strip off) the special control “cards” – which start with ‘//’.
     * For example,
     * the ‘// Job 1 17 2’ control card of Job1 is processed by discarding the ‘//’, noting that the ‘1’ is the ID
     * of the first job, noting that ‘17’ (or 23 in decimal) is the number of words that constitute
     * the instructions of Job 1, and ‘2’ is the priority-number (to be used for scheduling) of Job 1. All the
     * numbers are in hex. Following the Job-control card are the actual instructions – one instruction per line in
     * the program-file, which must also be extracted and stored in disk.
     *
     * Similar logic for processing the data-section, following the instructions and proceeded by ‘// Data ...’
     * control cards, also applies. In the case of Job 1, for example, ‘// Data 14 C C’, means Job 1’s
     * input buffer is 20 (14 in hex), its output buffer size is 12 (C in hex) words, and the size of its
     * temporary buffer is 12 (C in hex) words. (In this simulation, the input buffer comes pre-loaded with the
     * input data, for simplicity.) All the data values on the control cards are attributes of each program,
     * must be extracted and stored in the Process Control Block (PCB) (see below).
     */


    OSDriver osDriver;
    String usrDir = System.getProperty("user.home");

    int instructionStart=0, inputStart=0, outputStart=0, tempStart=0;
    int instructionEnd=0, inputEnd=0, outputEnd=0, tempEnd=0;

    Util util = new Util();

    public Loader(OSDriver osDriver)
    {
        this.osDriver=osDriver;
        //currentPage = osDriver.getMemoryManager().getFirstFreePage();
    }

    /**
     *
     * The basic outline of the loader’s logic looks like the following:
     *
     *  while (not end-of-program-data-file) do
     *  {
     *      Read-File();
     *      Extract program attributes into the PCB
     *      Insert hex-code or instructions into the simulated RAM
     *  }
     *
     *
     */

    public void loadFile()
    {
        parseFile(new File(usrDir + "/Desktop/Program-File.txt"));
    }

    public void parseFile(File file)
    {
        // Creates the reader and other local variables
        BufferedReader bufferedReader;
        print("Starting parse File\n");
        int lineCount = 0;
        String lastPID = "";
        try{

            bufferedReader = Files.newBufferedReader(Paths.get(file.getPath()));
            String nextLine;

            // Reads the next line while its not null
            while ( (nextLine = bufferedReader.readLine()) != null )
            {
                // Required redundant string
                String line = nextLine;

                // Checks if line is empty, if so moves on to next line
                if(line.isEmpty())
                {
                    //print("line empty");
                }
                // Checks if line contains a comment / control info
                else if(line.contains("//"))
                {
                    // Removes the spaces and splits the line into an array
                    String[] splitLine = line.split(" ");

                    // Get special control "info" from line and load to PCB
                    if( line.toUpperCase().contains("END") )
                    {
                        //print("Program END\n\n" );
                    }
                    else if( splitLine[1].toUpperCase().equals("JOB") )
                    {
                        //print("JOB");
                        //print("Program ID: " + splitLine[2] + "\t\t\t -> " + util.parseHexToDecimal(splitLine[2]));
                        //print("Number of Words: " + splitLine[3] + "\t -> " + util.parseHexToDecimal(splitLine[3]) );
                        //print("Priority: " + splitLine[4] );

                        lastPID = splitLine[2];
                        sendJobPropertiesToPCB(splitLine[2],splitLine[3],splitLine[4]);

                        instructionStart = lineCount;
                    }
                    else if( splitLine[1].toUpperCase().equals("DATA") )
                    {
                        //print("DATA");
                        //print("Input buffer size: "+ splitLine[2] + "\t -> " + util.parseHexToDecimal(splitLine[2]));
                        //print("Output buffer size: " + splitLine[3] + "\t -> " + util.parseHexToDecimal(splitLine[3]));
                        //print("Temp buffer size: " + splitLine[4] + "\t -> " + util.parseHexToDecimal(splitLine[4]));
                        instructionEnd = lineCount - 1;

                        sendDataPropertiesToPCB(lastPID,splitLine[2],splitLine[3],splitLine[4]);

                        inputStart = instructionEnd + 1;
                        inputEnd = inputStart + 19;

                        outputStart = inputEnd + 1;
                        outputEnd = outputStart + 11;

                        tempStart = outputEnd + 1;
                        tempEnd = tempStart + 11;

                        sendPageDataToPCB();

                        //print("NUMBERS");

                        //print("Instruction Start: \t\t-> " + instructionStart);
                        //print("Page would be -> " + (instructionStart/4));
                        //print("Instruction End: \t\t\t-> " + instructionEnd);
                        //print("Page would be -> " + (instructionEnd/4));

                        //print("Input Start: \t\t\t\t->" + inputStart);
                        //print("Page would be -> " + (inputStart/4));
                        //print("Input End: \t\t\t\t->" + inputEnd);
                        //print("Page would be -> " + (inputEnd/4));

                        //print("Output Start: \t\t\t->" + outputStart);
                        //print("Page would be -> " + (outputStart/4));
                        //print("Output End: \t\t\t\t->" + outputEnd);
                        //print("Page would be -> " + (outputEnd/4));

                        //print("Temp Start: \t\t\t\t->" + tempStart);
                        //print("Page would be -> " + (tempStart/4));
                        //print("Temp End: \t\t\t\t->" + tempEnd);
                        //print("Page would be -> " + (tempEnd/4));
                    }
                }
                else
                {
                    // Store into disk
                    //print(line);
                    String newline = line.replaceAll(" ", "");
                    osDriver.getMemoryManager().storeInDisk(newline);
                    lineCount++;
                }

            }

            // Finally close the reader
            bufferedReader.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            //print("ParseFile finished... " + (System.nanoTime() - Main.start) + "(ns)");
            print("Line Count: " + lineCount);
            print("Loading to disk...");
            print("Disk max size: " + osDriver.getMemoryManager().getDiskMaxSize());
            print("Disk current size: " + osDriver.getMemoryManager().getDiskCurrentSize());
            print("Percentage full: " + osDriver.getMemoryManager().getDiskPercentageFull() + "%");


        }

    }

    void sendJobPropertiesToPCB(String pID, String codeSize, String priority)
    {

        ProcessControlBlock processInfo = new ProcessControlBlock();

        // Sets the process id (makes it easy to grab the process later)
        processInfo.setProcessID(pID);

        // Sets the counter for this specific program (next instruction to be executed)
        processInfo.setProgramCounter(0);

        // Sets the size of the instruction code block
        processInfo.setInstructionCount(codeSize);

        // Sets the starting location in "disk" for the instructions
        processInfo.setDiskPointer(osDriver.getMemoryManager().getDiskCurrentSize());

        // Sets the status of the process to "new" (Info set, but not yet created(Ready) )
        processInfo.setStatus(ProcessControlBlock.STATUS_NEW);

        // Sets the priority for the process to be executed
        processInfo.setPriority(priority);

        // Sets the creation time for the process
        processInfo.setCreationTime(System.nanoTime());

        // Adds the process to the new process queue
        osDriver.getNewQueue().add(processInfo);

    }

    void sendDataPropertiesToPCB(String pID, String inputBufferSize, String outputBufferSize, String tempBufferSize)
    {
        // Sets the input buffer size for the process
        osDriver.getNewQueue().getLast().setInputBufferCount(inputBufferSize);

        // Sets the output buffer size for the process
        osDriver.getNewQueue().getLast().setOutputBufferCount(outputBufferSize);

        // Sets the temp buffer size for the process
        osDriver.getNewQueue().getLast().setTempBufferCount(tempBufferSize);

    }

    void sendPageDataToPCB()
    {
        osDriver.getNewQueue().getLast().setInstructionStart(instructionStart);
        osDriver.getNewQueue().getLast().setInstructionEnd(instructionEnd);
        osDriver.getNewQueue().getLast().setInputStart(inputStart);
        osDriver.getNewQueue().getLast().setInputEnd(inputEnd);
        osDriver.getNewQueue().getLast().setOutputStart(outputStart);
        osDriver.getNewQueue().getLast().setOutputEnd(outputEnd);
        osDriver.getNewQueue().getLast().setTempStart(tempStart);
        osDriver.getNewQueue().getLast().setTempEnd(tempEnd);

        for(int i=instructionStart; i<=tempEnd; i++)
        {
            osDriver.getNewQueue().getLast().getLineNumbers().add(i);
        }
    }
    // Sends the program to RAM ( memory )
    public void loadProgramToRam(int diskStartIndex, int jobSize, int memStartIndex)
    {
        //Util.p("");
        //print("Loading job...");
        //print("From Disk at location "+ diskStartIndex);
        //print("Of size " + jobSize + " lines");
        //print("To Memory at location " + memStartIndex);

        for(int i=0; i<jobSize; i++)
        {
            osDriver.getMemoryManager().writeToMemory(memStartIndex,osDriver.getMemoryManager().getFromDisk(diskStartIndex));

            memStartIndex++;
            diskStartIndex++;
        }
        //print("Done...\n");
    }

    public void loadPageToRam()
    {

    }

    /**
     *  Static short print method
     *
     * @param s
     * @param <T>
     */
    public static <T> void print(T s)
    {
        System.out.println("[Loader]: " + s);
    }
}
