package OS;

import CPU.Register;
import Misc.Util;

/**
 * Created by davidmcfall on 3/9/16.
 */
public class ProcessControlBlock
{
    public static String STATUS_NEW="NEW",STATUS_WAITING="WAITING",STATUS_TERMINATED="TERMINATED",
            STATUS_BLOCKED="BLOCKED", STATUS_RUNNING="RUNNING", STATUS_READY="READY";

    Register[] registers = new Register[16];
    Util util = new Util();

    String processID, priority, status, statusInfo, cpuID;
    String instructionCount, inputBufferCount, outputBufferCount, tempBufferCount;

    Integer programCounter, diskPointer, memoryPointer;

    Integer numberOfIO_Operations=0;

    long startTime, comlpetionTime, creationTime;


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

    public String getRamPercentageUsed()
    {
        double num = (fullCodeSizeInDecimal() / 1024)*100;
        return util.round(num,2) + "%";
    }

    public String getDiskPercentageUsed()
    {
        double num = (fullCodeSizeInDecimal() / 2048)*100;
        return util.round(num,2) + "%";
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
}
