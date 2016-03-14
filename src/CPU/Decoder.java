package CPU;

import Misc.Util;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by David McFall on 3/3/16.
 */
public class Decoder
{
    Util util = new Util();
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

    public Decoder()
    {

    }

    public ConcurrentHashMap<String, String> decode(String instruction)
    {
        String sREG1,sREG2,dREG,unused,bREG,address,reg1,reg2;
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        //print("Decoding \"" + instruction + "\"...");
        //String fixedHex = instruction.replaceAll("x", "");

        String binaryInstruction = util.hexToBinary(instruction);
        //print("Decoded hex instruction to binary \"" + binaryInstruction + "\" ");

        String formatCode = getFormatFromInstruction(binaryInstruction);
        //print("Format = " + formatCode + " \t\t\t\t\t\t\t\t\t---> " + Util.binaryToDecimal(formatCode));

        String opCode = getOPCodeFromInstruction(binaryInstruction);
        //print("OPCode = " + opCode + " \t\t\t\t\t\t\t\t---> " + Util.binaryToDecimal(opCode));

        map.put(FORMAT_CODE,formatCode);
        map.put(OP_CODE,opCode);

        switch (Integer.parseUnsignedInt(formatCode,2))
        {
            case 0: //Arithmetic Instruction
                sREG1 = binaryInstruction.substring(8,12);
                //print("Source Register 1 = " + sREG1 + " \t\t\t\t\t---> " + Util.binaryToDecimal(sREG1));
                sREG2 = binaryInstruction.substring(12,16);
                //print("Source Register 2 = " + sREG2 + " \t\t\t\t\t---> " + Util.binaryToDecimal(sREG2));
                dREG = binaryInstruction.substring(16,20);
                //print("Destination Register = " + dREG + " \t\t\t\t\t---> " + Util.binaryToDecimal(dREG));
                unused = binaryInstruction.substring(20,32);
                //print("Unused space = " + unused + " \t\t\t\t\t---> " + Util.binaryToDecimal(unused));

                map.put(S_REG_1,sREG1);
                map.put(S_REG_2,sREG2);
                map.put(D_REG,dREG);
                map.put(UNUSED,unused);
                break;

            case 1: //Conditional Branch and Immediate format
                bREG = binaryInstruction.substring(8,12);
                //print("Base Register = " + bREG + " \t\t\t\t\t\t---> " + Util.binaryToDecimal(bREG));
                dREG = binaryInstruction.substring(12,16);
                //print("Destination Register = " + dREG + " \t\t\t\t\t---> " + Util.binaryToDecimal(dREG));
                address = binaryInstruction.substring(16,32);
                //print("Address = " + address);
                //print("Address = " + address + " \t\t\t\t\t---> " + Util.binaryWordAddressToByteAddress(address));

                map.put(B_REG,bREG);
                map.put(D_REG,dREG);
                map.put(ADDRESS,address);
                break;

            case 2: //Unconditional Jump format
                address = binaryInstruction.substring(8,32);
                //print("Address = " + address);
                //print("Address = " + address + " \t\t\t---> " + Util.binaryWordAddressToByteAddress(address));

                map.put(ADDRESS,address);
                break;

            case 3: // Input and Output format
                reg1 = binaryInstruction.substring(8,12);
                //print("Register 1 = " + reg1 + " \t\t\t\t\t\t\t---> " + Util.binaryToDecimal(reg1));
                reg2 = binaryInstruction.substring(12,16);
                //print("Register 2 = " + reg2 + " \t\t\t\t\t\t\t---> " + Util.binaryToDecimal(reg2));
                address = binaryInstruction.substring(16,32);
                //print("Address = " + address + " \t\t\t\t\t---> " + Util.binaryWordAddressToByteAddress(address));

                map.put(REG_1,reg1);
                map.put(REG_2,reg2);
                map.put(ADDRESS,address);
                break;

            default:
                print("FAILED: Testing format integer " + Integer.parseUnsignedInt(formatCode,2));
                break;
        }



        //String dataCode = getDataFromInstruction(binaryInstruction);
        //print("Data Code = " + dataCode);


        return map;
    }

    String getFormatFromInstruction(String instruction)
    {
        return instruction.substring(0,2);
    }

    String getOPCodeFromInstruction(String instruction)
    {
        return instruction.substring(2,8);
    }

    String getDataFromInstruction(String instruction)
    {
        return instruction.substring(8);
    }

    /**
     *  Static short print method
     *
     * @param s
     * @param <T>
     */
    public static <T> void print(T s)
    {
        System.out.println("[Decoder]: " + s);
    }

}
