package com.goldensoftware.surfer;

import com4j.Com4jObject;
import com4j.DefaultMethod;
import com4j.DefaultValue;
import com4j.IID;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * IDocument Interface
 */
@IID("{B2933403-9788-11D2-9780-00104B6D9C80}")
public interface IDocument extends Com4jObject {
    /**
     * Returns the Application object
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
     * Returns the name of the document
     */
    @VTID(9)
    @DefaultMethod
    java.lang.String name();

    /**
     * Returns the name of the document including the path
     */
    @VTID(10)
    java.lang.String fullName();

    /**
     * Returns the pathname of the document
     */
    @VTID(11)
    java.lang.String path();

    /**
     * Returns False if document has changed since it was last saved
     */
    @VTID(12)
    boolean saved();

    /**
     * Returns the type of this document
     */
    @VTID(13)
    com.goldensoftware.surfer.SrfDocTypes type();

    /**
     * Returns a Windows collection containing the windows associated with this
     * document
     */
    @VTID(14)
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject windows();

    /**
     * Returns this document's ordinal index in the Documents collection
     */
    @VTID(15)
    int index();

    /**
     * Activates the first window associated with this document
     */
    @VTID(16)
    void activate();

    /**
     * Saves the document to disk using the current filename
     */
    @VTID(17)
    boolean save();

    /**
     * Saves the document to disk
     */
    @VTID(18)
    boolean saveAs(@DefaultValue("")
    java.lang.String fileName, @DefaultValue("")
    java.lang.String options, @DefaultValue("-3")
    com.goldensoftware.surfer.SrfSaveFormat fileFormat);

    /**
     * Closes the document and all associated windows
     */
    @VTID(19)
    boolean close(@DefaultValue("1")
    com.goldensoftware.surfer.SrfSaveTypes saveChanges, @DefaultValue("")
    java.lang.String fileName);

    /**
     * Creates a new window for this document
     */
    @VTID(20)
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject newWindow();

}
