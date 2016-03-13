package OS;

import Misc.Util;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by davidmcfall on 3/9/16.
 */
public class Loader
{

    OSDriver osDriver;
    String usrDir = System.getProperty("user.home");

    public Loader(OSDriver osDriver)
    {
        this.osDriver=osDriver;
    }

    public void loadFile()
    {
        parseFile(new File(usrDir + "/Desktop/Program-File.txt"));
    }

    public void parseFile(File file)
    {
        BufferedReader bufferedReader;
        print("Starting parse File\n");
        int lineCount = 0;
        String lastPID = "";
        try{

            bufferedReader = Files.newBufferedReader(Paths.get(file.getPath()));
            String nextLine;

            while ( (nextLine = bufferedReader.readLine()) != null )
            {

                String line = nextLine;

                if(line.isEmpty())
                {
                    //print("line empty");
                }
                else if(line.contains("//"))
                {
                    String[] splitLine = line.split(" ");

                    // Get special control "info" from line and load to PCB
                    if( line.toUpperCase().contains("END") )
                    {
                        //print("Program END\n\n" );
                    }
                    else if( splitLine[1].toUpperCase().equals("JOB") )
                    {
                        // print("JOB");
                        //print("Program ID: " + splitLine[2] + "\t\t\t -> " + hexToDecimal(splitLine[2]));
                        //print("Number of Words: " + splitLine[3] + "\t -> " + hexToDecimal(splitLine[3]) );
                        //print("Priority: " + splitLine[4] );

                        lastPID = splitLine[2];
                        sendJobPropertiesToPCB(splitLine[2],splitLine[3],splitLine[4]);
                    }
                    else if( splitLine[1].toUpperCase().equals("DATA") )
                    {
                        // print("DATA");
                        // print("Input buffer size: "+ splitLine[2] + "\t -> " + hexToDecimal(splitLine[2]));
                        // print("Output buffer size: " + splitLine[3] + "\t -> " + hexToDecimal(splitLine[3]));
                        // print("Temp buffer size: " + splitLine[4] + "\t -> " + hexToDecimal(splitLine[4]));

                        sendDataPropertiesToPCB(lastPID,splitLine[2],splitLine[3],splitLine[4]);
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

        processInfo.setCreationTime(System.nanoTime());

        osDriver.getNewQueue().add(processInfo);

    }

    void sendDataPropertiesToPCB(String pID, String inputBufferSize, String outputBufferSize, String tempBufferSize)
    {

        osDriver.getNewQueue().getLast().setInputBufferCount(inputBufferSize);

        osDriver.getNewQueue().getLast().setOutputBufferCount(outputBufferSize);

        osDriver.getNewQueue().getLast().setTempBufferCount(tempBufferSize);

    }

    public void loadProgramToRam(int diskStartIndex, int jobSize, int memStartIndex)
    {
        Util.p("");
        print("Loading job...");
        //print("From Disk at location "+ diskStartIndex);
        //print("Of size " + jobSize + " lines");
        //print("To Memory at location " + memStartIndex);

        for(int i=0; i<jobSize; i++)
        {
            osDriver.getMemoryManager().writeToMemory(memStartIndex,osDriver.getMemoryManager().getFromDisk(diskStartIndex));

            memStartIndex++;
            diskStartIndex++;
        }
        print("Done...\n");
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
