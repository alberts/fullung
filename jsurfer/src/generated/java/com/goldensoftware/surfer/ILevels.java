package com.goldensoftware.surfer;

import com4j.Com4jObject;
import com4j.DefaultMethod;
import com4j.IID;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * ILevels Interface
 */
@IID("{B2933427-9788-11D2-9780-00104B6D9C80}")
public interface ILevels extends Com4jObject, Iterable<Com4jObject> {
    /**
     * Returns the application object
     */
    @VTID(7)
    com.goldensoftware.surfer.IApplication application();

    /**
     * Returns the parent object
     */
    @VTID(8)
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject parent();

    /**
     * Returns the number of levels in the collection
     */
    @VTID(9)
    int count();

    @VTID(10)
    java.util.Iterator<Com4jObject> iterator();

    /**
     * Returns an individual level
     */
    @VTID(11)
    @DefaultMethod
    com.goldensoftware.surfer.ILevel item(int index);

    /**
     * Creates a level from each element of the specified array
     */
    @VTID(12)
    void set(double[] levels);

    /**
     * Generates an evenly spaced series of levels
     */
    @VTID(13)
    void autoGenerate(double minLevel, double maxLevel, double interval);

    /**
     * Loads the specified level file
     */
    @VTID(14)
    void loadFile(java.lang.String fileName);

    /**
     * Saves the current levels in a level file
     */
    @VTID(15)
    void saveFile(java.lang.String fileName);

    /**
     * Turns on/off a range of labels
     */
    @VTID(16)
    void setLabelFrequency(int firstIndex, int numberToSet, int numberToSkip);

    /**
     * Turns on/off a range of hachures
     */
    @VTID(17)
    void setHachFrequency(int firstIndex, int numberToSet, int numberToSkip);

}
