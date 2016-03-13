package CPU;

import Misc.Util;
import OS.OSDriver;
import OS.ProcessControlBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by davidmcfall on 3/9/16.
 */
public class CPU extends Thread implements Runnable
{

    OSDriver osDriver;
    Util util = new Util();
    Decoder decoder = new Decoder();

    ConcurrentHashMap<String,String> decodedInstruction = new ConcurrentHashMap<>();
    static String FORMAT_CODE = "FORMAT";
    static String OP_CODE = "OPCode";
    static String S_REG_1 = "S_REG_1";
    static String S_REG_2 = "S_REG_2";
    static String D_REG = "D_REG";
    static String B_REG = "B_REG";
    static String REG_1 = "REG_1";
    static String REG_2 = "REG_2";
    static String ADDRESS = "ADDRESS";
    static String UNUSED = "UNUSED";

    long PROCESS_START_TIME = 0;

    List<String> cache = new ArrayList<>();
    ProcessControlBlock processInfo;

    Integer programCounter = 0;  // Current execution point of the program

    Register register0 = new Register(0);      // #0  (0000) is the accumulator
    Register register1 = new Register(1,0);      // #1  (0001) is the zero register (contains the value zero)
    Register register2 = new Register(2);      // #2  (0010) General Purpose Register - 1
    Register register3 = new Register(3);      // #3  (0011) General Purpose Register - 2
    Register register4 = new Register(4);      // #4  (0100) General Purpose Register - 3
    Register register5 = new Register(5);      // #5  (0101) General Purpose Register - 4
    Register register6 = new Register(6);      // #6  (0110) General Purpose Register - 5
    Register register7 = new Register(7);      // #7  (0111) General Purpose Register - 6
    Register register8 = new Register(8);      // #8  (1000) General Purpose Register - 7
    Register register9 = new Register(9);      // #9  (1001) General Purpose Register - 8
    Register register10 = new Register(10);     // #10 (1010) General Purpose Register - 9
    Register register11 = new Register(11);     // #11 (1011) General Purpose Register - 10
    Register register12 = new Register(12);     // #12 (1100) General Purpose Register - 11
    Register register13 = new Register(13);     // #13 (1101) General Purpose Register - 12
    Register register14 = new Register(14);     // #14 (1110) General Purpose Register - 13
    Register register15 = new Register(15);      // #15 (1111) General Purpose Register - 14

    boolean doCompute = false;

    Register dReg, bReg, sReg1, sReg2, reg1, reg2;

    public CPU(OSDriver osDriver)
    {
        this.osDriver = osDriver;
    }

    @Override
    public void run()
    {
        while (osDriver.isNotDone() && !osDriver.getReadyQueue().isEmpty())
        {
            try{

                this.processInfo = osDriver.getReadyQueue().pollFirst();

                int linePointer = processInfo.getMemoryPointer();

                programCounter = processInfo.getProgramCounter();
                cache.clear();
                setDoCompute(true);

                for (int i=0;  i < processInfo.fullCodeSizeInDecimal(); i++)
                {
                    cache.add(osDriver.getMemoryManager().getMemoryLine(linePointer));
                    linePointer++;
                }

                if(processInfo != null)
                {
                    processInfo.setCpuID(this.getName());
                }

            }
            catch (Exception ex)
            {

            }

            compute();

        }
    }

    private void compute()
    {
        try {
            Util.p("\n[CPU]: Attempting execution of program " + processInfo.getProcessID() + "...");
            PROCESS_START_TIME = System.nanoTime();
            processInfo.setStartTime(PROCESS_START_TIME);
            processInfo.setStatus(ProcessControlBlock.STATUS_RUNNING);
        }catch (NullPointerException ex)
        {

        }

        while (doCompute)
        {
            String instruction = fetch();

            decode(instruction);

            programCounter++;

            try {
                execute();
            }catch (Exception ex)
            {
                printError("Error happened");
                ex.printStackTrace();
                //programCounter--;
                System.exit(1);
            }

            // Halt compute if at end of program
            if(programCounter.intValue() == util.parseHexToDecimal(processInfo.getInstructionCount()))
            {
                doCompute = false;
                terminateProcess();
            }

        }
    }

    private String fetch()
    {
        return cache.get(programCounter);
    }

    private void decode(String str)
    {
        decodedInstruction = decoder.decode(str);
    }

    private void execute() throws Exception
    {
        switch ( util.parseBinaryToDecimal(decodedInstruction.get(FORMAT_CODE)) )
        {
            case 0:
                executeOP(decodedInstruction.get(OP_CODE));
                break;
            case 1:
                executeOP(decodedInstruction.get(OP_CODE));
                break;
            case 2:
                executeOP(decodedInstruction.get(OP_CODE));
                break;
            case 3:
                executeOP(decodedInstruction.get(OP_CODE));
                break;
            default:
                throw new Exception("Error in execute method");
        }
    }

    private void executeOP(String opCode)
    {
        Integer addressData,oldValue, newValue;

        switch (Integer.parseUnsignedInt(opCode,2))
        {
            case 0:     //Reads content of I/P buffer into a accumulator (Read the no. of integers to be added from the input buffer)
                //printYellow("Operation = RD (Read from input buffer)");

                reg1 = getRegister(decodedInstruction.get(REG_1));
                reg2 = getRegister(decodedInstruction.get(REG_2));
                addressData = util.binaryWordAddressToByteAddress(decodedInstruction.get(ADDRESS));

                if(addressData != 0)
                {
                    for (int i=addressData, count=1; i<=(addressData + processInfo.getInputBufferCountInDecimal()); i++,count++)
                    {
                        if(util.parseHexToDecimal(cache.get(i)) != 0)
                        {
                            reg1.setValue(count);
                        }
                    }
                    if (reg1.getValue() == 1)
                    {
                        reg1.setValue(util.parseHexToDecimal(cache.get(processInfo.getRelativeInputBufferStartPoint())));
                    }
                }
                else
                {
                    reg1.setValue(util.parseHexToDecimal(cache.get(reg2.getValue()-1)));
                }

                //printYellow("Destination Register \"" + reg1.getRegisterID() + "\" value set to \"" + reg1.getValue() + "\"");
                //Util.p("\n\n");
                break;
            case 1:     //Writes the content of accumulator into O/P buffer
                //printYellow("Operation = WR (Write to output buffer)");

                reg1 = getRegister(decodedInstruction.get(REG_1));
                reg2 = getRegister(decodedInstruction.get(REG_2));
                addressData = util.binaryWordAddressToByteAddress(decodedInstruction.get(ADDRESS));

                if(addressData != 0)
                {
                    //printYellow("Destination Address \"" + addressData + "\" old value is \"" + cache.get(addressData) + "\"");
                    cache.set(addressData, util.decimalToHex(reg1.getValue().toString()));
                    //printYellow("Destination Address \"" + addressData + "\" new value is \"" + cache.get(addressData) + "\"");
                }
                else
                {
                    //printYellow("Destination Address \"" + reg2.getValue() + "\" old value is \"" + cache.get(reg2.getValue()) + "\"");
                    cache.set(reg2.getValue(), util.decimalToHex(reg1.getValue().toString()));
                    //printYellow("Destination Address \"" + reg2.getValue() + "\" new value is \"" + cache.get(reg2.getValue()) + "\"");
                }

                //Util.p("\n\n");
                break;
            case 2:     //Stores content of a reg.  into an address (Stores the data of bReg in address pointed to by dReg)
                //printYellow("Operation = ST (Store)");

                bReg = getRegister(decodedInstruction.get(B_REG));
                dReg = getRegister(decodedInstruction.get(D_REG));


                //printYellow("Destination Address \"" + dReg.getValue() + "\" old value is \"" + cache.get(dReg.getValue()) + "\"");
                cache.set( dReg.getValue() , util.decimalToHex(bReg.getValue().toString()) );
                //printYellow("Destination Address \"" + dReg.getValue() + "\" new value is \"" + cache.get(dReg.getValue()) + "\"");

                //Util.p("\n\n");
                break;
            case 3:     //Loads the content of an address into a reg.
                //printYellow("Operation = LW (Load Word)");

                bReg = getRegister(decodedInstruction.get(B_REG));
                dReg = getRegister(decodedInstruction.get(D_REG));
                addressData = util.binaryWordAddressToByteAddress(decodedInstruction.get(ADDRESS));

                //printYellow("Destination Register \"" + dReg.getRegisterID() + "\" old value is \"" + dReg.getValue() + "\"");

                dReg.setValue( util.parseHexToDecimal(cache.get(bReg.getValue() + addressData)) );

                //printYellow("Destination Register \"" + dReg.getRegisterID() + "\" value set to \"" + dReg.getValue() + "\"");

                //Util.p("\n\n");
                break;
            case 4:     //Transfers the content of one register into another
                //printYellow("Operation = MOV");

                sReg1 = getRegister(decodedInstruction.get(S_REG_1));
                sReg2 = getRegister(decodedInstruction.get(S_REG_2));
                dReg = getRegister(decodedInstruction.get(D_REG));

                //printYellow("Source Register 1 value is \"" + sReg1.getValue() + "\"");
                //printYellow("Source Register 2 value is \"" + sReg2.getValue() + "\"");

                sReg1.setValue(sReg2.getValue());

                //printYellow("Destination Register \"" + dReg.getRegisterID() + "\" value set to \"" + dReg.getValue() + "\"");
                //Util.p("\n\n");
                break;
            case 5:     //Adds content of two S-regs into D-reg
                //printYellow("Operation = ADD");

                sReg1 = getRegister(decodedInstruction.get(S_REG_1));
                sReg2 = getRegister(decodedInstruction.get(S_REG_2));
                dReg = getRegister(decodedInstruction.get(D_REG));

                //printYellow("Source Register 1 value is \"" + sReg1.getValue() + "\"");
                //printYellow("Source Register 2 value is \"" + sReg2.getValue() + "\"");

                dReg.setValue(sReg1.getValue() + sReg2.getValue());

                //printYellow("Destination Register \"" + dReg.getRegisterID() + "\" value set to \"" + dReg.getValue() + "\"");
                //Util.p("\n\n");
                break;
            case 6:     //Subtracts content of two S-regs into D-reg

                System.exit(1);

                //Util.p("\n\n");
                break;
            case 7:     //Multiplies content of two S-regs into D-reg

                System.exit(1);

                //Util.p("\n\n");
                break;
            case 8:     //Divides content of two S-regs into D-reg
                //printYellow("Operation = DIV");

                sReg1 = getRegister(decodedInstruction.get(S_REG_1));
                sReg2 = getRegister(decodedInstruction.get(S_REG_2));
                dReg = getRegister(decodedInstruction.get(D_REG));

                //printYellow("Source Register 1 value is \"" + sReg1.getValue() + "\"");
                //printYellow("Source Register 2 value is \"" + sReg2.getValue() + "\"");

                dReg.setValue(sReg1.getValue() / sReg2.getValue());

                //printYellow("Destination Register \"" + dReg.getRegisterID() + "\" value set to \"" + dReg.getValue() + "\"");
                //Util.p("\n\n");
                break;
            case 9:     //Logical AND of two S-regs into D-reg


                System.exit(1);

                //Util.p("\n\n");
                break;
            case 10:    //Logical OR of two S-regs into D-reg


                System.exit(1);

                Util.p("\n\n");
                break;
            case 11:    //Transfers address/data directly into a register
                //printYellow("Operation = MOVI");

                bReg = getRegister(decodedInstruction.get(B_REG));
                dReg = getRegister(decodedInstruction.get(D_REG));
                addressData = util.binaryWordAddressToByteAddress(decodedInstruction.get(ADDRESS));

                if(addressData == 0)
                {
                    addressData = util.parseBinaryToDecimal(decodedInstruction.get(ADDRESS));
                    newValue = addressData;
                }
                else {
                    newValue = effectiveAddress(bReg.getRegisterID().toString(), addressData.toString());
                }

                dReg.setValue( newValue );

                //printYellow("Destination Register \"" + dReg.getRegisterID() + "\" value set to \"" + dReg.getValue() + "\"");
                //Util.p("\n\n");
                break;
            case 12:    //Adds a data directly to the content of a register
                //printYellow("Operation = ADDI");

                bReg = getRegister(decodedInstruction.get(B_REG));
                dReg = getRegister(decodedInstruction.get(D_REG));
                addressData = util.binaryWordAddressToByteAddress(decodedInstruction.get(ADDRESS));

                if( util.parseBinaryToDecimal(decodedInstruction.get(ADDRESS)) < 4)
                {
                    addressData = util.parseBinaryToDecimal(decodedInstruction.get(ADDRESS));
                }

                oldValue = dReg.getValue();
                //printYellow("Destination Register \"" + dReg.getRegisterID() + "\" old value is \"" + oldValue + "\"");


                //printYellow("Adding \"" + addressData + "\" to \"" + oldValue + "\"" );

                newValue = oldValue + addressData;
                //printYellow("Calculated value is \"" + newValue + "\"");

                dReg.setValue( newValue );

                //printYellow("Destination Register \"" + dReg.getRegisterID() + "\" value set to \"" + dReg.getValue() + "\"");
                //Util.p("\n\n");
                break;
            case 13:    //Multiplies a data directly to the content of a register


                System.exit(1);

                //Util.p("\n\n");
                break;
            case 14:    //Divides a data directly to the content of a register


                System.exit(1);

                //Util.p("\n\n");
                break;
            case 15:    //Loads a data/address directly to the content of a register
                //printYellow("Operation = LDI");

                bReg = getRegister(decodedInstruction.get(B_REG));
                dReg = getRegister(decodedInstruction.get(D_REG));
                addressData = util.binaryWordAddressToByteAddress(decodedInstruction.get(ADDRESS));

                dReg.setValue(addressData);

                //printYellow("Destination Register \"" + dReg.getRegisterID() + "\" value set to \"" + addressData + "\"");
                //Util.p("\n\n");
                break;
            case 16:    //Sets the D-reg to 1 if  first S-reg is less than second B-reg, and 0 otherwise
                //printYellow("Operation = SLT");

                sReg1 = getRegister(decodedInstruction.get(S_REG_1));
                sReg2 = getRegister(decodedInstruction.get(S_REG_2));
                dReg = getRegister(decodedInstruction.get(D_REG));

                //printYellow("Comparing register \"" + sReg1.getRegisterID() + "\" to register \"" + sReg2.getRegisterID() + "\"");
                //printYellow("Register \"" + sReg1.getRegisterID() + "\" value is \"" + sReg1.getValue() + "\"");
                //printYellow("Register \"" + sReg2.getRegisterID() + "\" value is \"" + sReg2.getValue() + "\"");
                if( sReg1.getValue() < sReg2.getValue())
                {
                    dReg.setValue(1);
                }
                else
                {
                    dReg.setValue(0);
                }

                //printYellow("Destination Register \"" + dReg.getRegisterID() + "\" value set to \"" + dReg.getValue() + "\"");
                //Util.p("\n\n");
                break;
            case 17:    //Sets the D-reg to 1 if  first S-reg is less than a data, and 0 otherwise


                System.exit(1);

                //Util.p("\n\n");
                break;
            case 18:    //Logical end of program
                //printYellow("END OF PROGRAM");

                //Util.p("\n\n");
                break;
            case 19:    //Does nothing and moves to next instruction


                System.exit(1);

                //Util.p("\n\n");
                break;
            case 20:    //Jumps to a specified location


                System.exit(1);

                //Util.p("\n\n");
                break;
            case 21:    //Branches to an address when content of B-reg = D-reg
                //printYellow("Operation = BEQ (Branch if Equal)");

                bReg = getRegister(decodedInstruction.get(B_REG));
                dReg = getRegister(decodedInstruction.get(D_REG));
                addressData = util.binaryWordAddressToByteAddress(decodedInstruction.get(ADDRESS));

                if( bReg.getValue().intValue() == dReg.getValue().intValue())
                {
                    programCounter = addressData;
                    //printYellow("Branching to address " + programCounter);
                }
                else
                {
                    // Do nothing
                    //printYellow("Not branching");
                }
                //Util.p("\n\n");
                break;
            case 22:    //Branches to an address when content of B-reg <> D-reg (Not Equal to)
                //printYellow("Operation = BNE (Branch if Not Equal)");

                bReg = getRegister(decodedInstruction.get(B_REG));
                dReg = getRegister(decodedInstruction.get(D_REG));
                addressData = util.binaryWordAddressToByteAddress(decodedInstruction.get(ADDRESS));

                if( bReg.getValue().intValue() != dReg.getValue().intValue())
                {
                    programCounter = addressData;
                    //printYellow("Branching to address " + programCounter);
                }
                else
                {
                    // Do nothing
                    //printYellow("Not branching");
                }

                //Util.p("\n\n");
                break;
            case 23:    //Branches to an address when content of B-reg = 0

                System.exit(1);


                //Util.p("\n\n");
                break;
            case 24:    //Branches to an address when content of B-reg <> 0 (Not Equal to)


                System.exit(1);

                //Util.p("\n\n");
                break;
            case 25:    //Branches to an address when content of B-reg > 0


                System.exit(1);

                //Util.p("\n\n");
                break;
            case 26:    //Branches to an address when content of B-reg < 0


                System.exit(1);

                //Util.p("\n\n");
                break;
            default:
                break;
        }
    }

    public Register getRegister(String binaryRegisterValue)
    {
        switch (util.parseBinaryToDecimal(binaryRegisterValue))
        {
            case 0:
                return register0;
            case 1:
                return register1;
            case 2:
                return register2;
            case 3:
                return register3;
            case 4:
                return register4;
            case 5:
                return register5;
            case 6:
                return register6;
            case 7:
                return register7;
            case 8:
                return register8;
            case 9:
                return register9;
            case 10:
                return register10;
            case 11:
                return register11;
            case 12:
                return register12;
            case 13:
                return register13;
            case 14:
                return register14;
            case 15:
                return register15;
            default:
                return null;
        }
    }

    private Integer effectiveAddress(String bReg, String offset)
    {
        Integer base = util.parseBinaryToDecimal(bReg);
        Integer address  = util.binaryWordAddressToByteAddress(offset);

        return base + address;
    }

    private void updatePCB()
    {

    }

    private void clearCPU()
    {

        decodedInstruction.clear();

        register0.reset();
        register1.reset();
        register2.reset();
        register3.reset();
        register4.reset();
        register5.reset();
        register6.reset();
        register7.reset();
        register8.reset();
        register9.reset();
        register10.reset();
        register11.reset();
        register12.reset();
        register13.reset();
        register14.reset();
        register15.reset();


    }

    private void storeRegistersToPCB()
    {
        processInfo.getRegisters()[0] = register0.clone();
        processInfo.getRegisters()[1] = register1.clone();
        processInfo.getRegisters()[2] = register2.clone();
        processInfo.getRegisters()[3] = register3.clone();
        processInfo.getRegisters()[4] = register4.clone();
        processInfo.getRegisters()[5] = register5.clone();
        processInfo.getRegisters()[6] = register6.clone();
        processInfo.getRegisters()[7] = register7.clone();
        processInfo.getRegisters()[8] = register8.clone();
        processInfo.getRegisters()[9] = register9.clone();
        processInfo.getRegisters()[10] = register10.clone();
        processInfo.getRegisters()[11] = register11.clone();
        processInfo.getRegisters()[12] = register12.clone();
        processInfo.getRegisters()[13] = register13.clone();
        processInfo.getRegisters()[14] = register14.clone();
        processInfo.getRegisters()[15] = register15.clone();
    }

    private void terminateProcess()
    {
        sendOutputBufferToDisk();

        storeRegistersToPCB();

        //printOutputBuffer();

        print("Process " + processInfo.getProcessID() + " finished...");
        long PROCESS_END_TIME = System.nanoTime();
        processInfo.setComlpetionTime(PROCESS_END_TIME);

        printYellow("Process ended after " + (PROCESS_END_TIME-PROCESS_START_TIME) + " (ns)");

        processInfo.setStatus(ProcessControlBlock.STATUS_TERMINATED);
        osDriver.getTerminatedQueue().add(processInfo);
        //print("Removing process " + processInfo.getProcessID() + " from RAM...");
        //print("Process size = " + processInfo.fullCodeSizeInDecimal());
        osDriver.getMemoryManager().removeFromRam(processInfo.getMemoryPointer(),processInfo.fullCodeSizeInDecimal());

        if(processInfo.getProcessID().equals("1E"))
        {
            osDriver.setNotDone(false);
        }

        clearCPU();
    }

    private void sendOutputBufferToDisk()
    {
        int lineLocation = processInfo.getDiskPointer() + processInfo.getRelativeOutputBufferStartPoint();
        for(int i=0; i<util.parseHexToDecimal(processInfo.getOutputBufferCount()); i++)
        {
            osDriver.getMemoryManager().storeInDisk(cache.get(processInfo.getRelativeOutputBufferStartPoint()+i),
                    lineLocation+i);
            //print("Storing line: \"" + cache.get(processInfo.getRelativeOutputBufferStartPoint()+i) + "\" back to " +
            //        "Disk.");
        }
    }

    private void printRegisters()
    {
        print("----------------------------------- Printing Registers -------------------------------------");
        print("Register " + register0.getRegisterID() + " = " + register0.getValue());
        print("Register " + register1.getRegisterID() + " = " + register1.getValue());
        print("Register " + register2.getRegisterID() + " = " + register2.getValue());
        print("Register " + register3.getRegisterID() + " = " + register3.getValue());
        print("Register " + register4.getRegisterID() + " = " + register4.getValue());
        print("Register " + register5.getRegisterID() + " = " + register5.getValue());
        print("Register " + register6.getRegisterID() + " = " + register6.getValue());
        print("Register " + register7.getRegisterID() + " = " + register7.getValue());
        print("Register " + register8.getRegisterID() + " = " + register8.getValue());
        print("Register " + register9.getRegisterID() + " = " + register9.getValue());
        print("Register " + register10.getRegisterID() + " = " + register10.getValue());
        print("Register " + register11.getRegisterID() + " = " + register11.getValue());
        print("Register " + register12.getRegisterID() + " = " + register12.getValue());
        print("Register " + register13.getRegisterID() + " = " + register13.getValue());
        print("Register " + register14.getRegisterID() + " = " + register14.getValue());
        print("Register " + register15.getRegisterID() + " = " + register15.getValue());
        print("------------------------------------------- End --------------------------------------------");
    }

    private void printOutputBuffer()
    {
        print("------------------------ Output Buffer -------------------------------");
        for(int i=getProcessInfo().getRelativeOutputBufferStartPoint();
            i < getProcessInfo().getRelativeTempBufferStartPoint();
            i++)
        {
            print(cache.get(i));
        }
        print("----------------------------- End ------------------------------------");
    }


    public List<String> getCache() {
        return cache;
    }

    /**
     * Getter for property 'programCounter'.
     *
     * @return Value for property 'programCounter'.
     */
    public Integer getProgramCounter() {
        return programCounter;
    }

    /**
     * Setter for property 'programCounter'.
     *
     * @param programCounter Value to set for property 'programCounter'.
     */
    public void setProgramCounter(Integer programCounter) {
        this.programCounter = programCounter;
    }



    public ProcessControlBlock getProcessInfo() {
        return processInfo;
    }

    public void setProcessInfo(ProcessControlBlock processInfo) {
        this.processInfo = processInfo;
    }

    public boolean isDoCompute() {
        return doCompute;
    }

    public void setDoCompute(boolean doCompute) {
        this.doCompute = doCompute;
    }


    /**
     *  Static short print method
     *
     * @param s
     * @param <T>
     */
    public <T> void print(T s)
    {
        System.out.println("[" + this.getName() + "]: " + s);
    }
    /**
     *  Static short error print method
     *
     * @param s
     * @param <T>
     */
    public <T> void printError(T s)
    {
        System.out.println(Util.ANSI_RED + "[" + this.getName() + "]: " + s + Util.ANSI_RESET);
    }
    /**
     *  Static short error print method
     *
     * @param s
     * @param <T>
     */
    public <T> void printYellow(T s)
    {
        System.out.println(Util.ANSI_GREEN + "[" + this.getName() + "]: " + s + Util.ANSI_RESET);
    }



}
