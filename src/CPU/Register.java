package CPU;

/**
 * Created by David McFall on 3/4/2016.
 */
public class Register
{
    private Integer registerID = null;
    private Integer value = null;

    public Register(Integer regID)
    {
        setRegisterID(regID);
    }

    public Register(Integer regID, Integer startingValue)
    {
        setRegisterID(regID);
        setValue(startingValue);
    }

    public void reset()
    {
        setValue(null);
    }

    /**
     * Getter for property 'value'.
     *
     * @return Value for property 'value'.
     */
    public Integer getValue() {
        return value;
    }

    /**
     * Setter for property 'value'.
     *
     * @param value Value to set for property 'value'.
     */
    public void setValue(Integer value) {
        this.value = value;
    }

    /**
     * Getter for property 'registerID'.
     *
     * @return Value for property 'registerID'.
     */
    public Integer getRegisterID() {
        return registerID;
    }

    /**
     * Setter for property 'registerID'.
     *
     * @param registerID Value to set for property 'registerID'.
     */
    private void setRegisterID(Integer registerID) {
        this.registerID = registerID;
    }

    public Register clone()
    {
        Register newReg = this;
        return newReg;
    }


}
