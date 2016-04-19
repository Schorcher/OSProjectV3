package OS;

import CPU.Register;
import Memory.Page;
import Misc.Util;

import java.util.ArrayList;

/**
 * Created by David McFall on 2/24/16.
 */
public class ProcessControlBlock
{

    /**
     *  PROCESS CONTROL BLOCK:
     *
     * The CPU is supported by a PCB, which may have the following (suggested) structure:
     *
     * typedef struct PCB
     * {
     *      cpuid:                  // information the assigned CPU (for multiprocessor system)
     *      program-counter         // the job’s pc holds the address of the instruction to fetch
     *      struct state:           // record of environment that is saved on interrupt
     *                              // including the pc, registers, permissions, buffers, caches, active
     *                              // pages/blocks
     *      code-size;              // extracted from the //JOB control line
     *      struct registers:       // accumulators, index, general
     *      struct sched:           // burst-time, priority, queue-type, time-slice, remain-time
     *      struct accounts:        // cpu-time, time-limit, time-delays, start/end times, io-times
     *      struct memories:        // page-table-base, pages, page-size
     *                              // base-registers – logical/physical map, limit-reg
     *      struct progeny:         // child-procid, child-code-pointers
     *      parent: ptr;            // pointer to parent (if this process is spawned, else ‘null’)
     *      struct resources:       // file-pointers, io-devices – unitclass, unit#, open-file-tables
     *      status;                 // {running, ready, blocked, new}
     *      status_info:            // pointer to ‘ready-list of active processes’ or
     *                              // ‘resource-list on blocked processes’
     *      priority: integer;      // of the process, extracted from the //JOB control line
     * }
     */


    final public static String STATUS_NEW="NEW",STATUS_WAITING="WAITING",STATUS_TERMINATED="TERMINATED",
            STATUS_BLOCKED="BLOCKED", STATUS_RUNNING="RUNNING", STATUS_READY="READY";

    Register[] registers = new Register[16];
    Util util = new Util();

    String processID, priority, status, statusInfo, cpuID;
    String instructionCount, inputBufferCount, outputBufferCount, tempBufferCount;

    Integer pageTableBaseRegister=0, pageTableLimitRegister=0;

    Integer programCounter, diskPointer, memoryPointer;

    Integer numberOfIO_Operations=0;

    long startTime, comlpetionTime, creationTime;

    ArrayList<Page> pageList = new ArrayList<>();

    public ProcessControlBlock()
    {

    }


    public String toString()
    {
        return "\nProcessID:\t " + processID + "\tJob Priority:\t " + priority;
    }

    public Integer fullCodeSizeInDecimal()
    {
        return util.parseHexToDecimal(instructionCount) +
                util.parseHexToDecimal(inputBufferCount) +
                util.parseHexToDecimal(outputBufferCount) +
                util.parseHexToDecimal(tempBufferCount);
    }

    public Integer getRelativeInputBufferStartPoint()
    {
        return util.parseHexToDecimal(instructionCount);
    }

    public Integer getRelativeOutputBufferStartPoint()
    {
        return util.parseHexToDecimal(instructionCount) +
                util.parseHexToDecimal(inputBufferCount);
    }

    public Integer getRelativeTempBufferStartPoint()
    {
        return util.parseHexToDecimal(instructionCount) +
                util.parseHexToDecimal(inputBufferCount) +
                util.parseHexToDecimal(outputBufferCount);
    }

    public Integer getInputBufferCountInDecimal()
    {
        return util.parseHexToDecimal(inputBufferCount);
    }

    public Integer getOutputBufferCountInDecimal()
    {
        return util.parseHexToDecimal(outputBufferCount);
    }

    public Integer getTempBufferCountInDecimal()
    {
        return util.parseHexToDecimal(tempBufferCount);
    }

    public void incrementIOOpsCount()
    {
        numberOfIO_Operations++;
    }

    public long getProcessRunTime()
    {
        return getComlpetionTime() - getStartTime();
    }

    public long getWaitingTime()
    {
        return getStartTime() - getCreationTime();
    }

    public Double getRamPercentageUsed()
    {
        double num = ((double)fullCodeSizeInDecimal() / 1024.0)*100.0;
        return util.round(num,2);
    }

    public Double getDiskPercentageUsed()
    {
        double num = ((double)fullCodeSizeInDecimal() / 2048.0)*100.0;
        return util.round(num,2);
    }

    public Register[] getRegisters() {
        return registers;
    }

    public void setRegisters(Register[] registers) {
        this.registers = registers;
    }

    public String getProcessID() {
        return processID;
    }

    public void setProcessID(String processID) {
        this.processID = processID;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusInfo() {
        return statusInfo;
    }

    public void setStatusInfo(String statusInfo) {
        this.statusInfo = statusInfo;
    }

    public String getInstructionCount() {
        return instructionCount;
    }

    public void setInstructionCount(String instructionCount) {
        this.instructionCount = instructionCount;
    }

    public String getInputBufferCount() {
        return inputBufferCount;
    }

    public void setInputBufferCount(String inputBufferCount) {
        this.inputBufferCount = inputBufferCount;
    }

    public String getOutputBufferCount() {
        return outputBufferCount;
    }

    public void setOutputBufferCount(String outputBufferCount) {
        this.outputBufferCount = outputBufferCount;
    }

    public String getTempBufferCount() {
        return tempBufferCount;
    }

    public void setTempBufferCount(String tempBufferCount) {
        this.tempBufferCount = tempBufferCount;
    }

    public Integer getProgramCounter() {
        return programCounter;
    }

    public void setProgramCounter(Integer programCounter) {
        this.programCounter = programCounter;
    }

    public String getCpuID() {
        return cpuID;
    }

    public void setCpuID(String cpuID) {
        this.cpuID = cpuID;
    }

    public Integer getDiskPointer() {
        return diskPointer;
    }

    public void setDiskPointer(Integer diskPointer) {
        this.diskPointer = diskPointer;
    }

    public Integer getMemoryPointer() {
        return memoryPointer;
    }

    public void setMemoryPointer(Integer memoryPointer) {
        this.memoryPointer = memoryPointer;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public long getComlpetionTime() {
        return comlpetionTime;
    }

    public void setComlpetionTime(long comlpetionTime) {
        this.comlpetionTime = comlpetionTime;
    }

    public Integer getNumberOfIO_Operations() {
        return numberOfIO_Operations;
    }

    public ArrayList<Page> getPageList()
    {
        return pageList;
    }
}
