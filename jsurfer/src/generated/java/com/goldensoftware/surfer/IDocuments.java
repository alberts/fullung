package com.goldensoftware.surfer;

import com4j.Com4jObject;
import com4j.DefaultMethod;
import com4j.DefaultValue;
import com4j.IID;
import com4j.MarshalAs;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * IDocuments Interface
 */
@IID("{B2933402-9788-11D2-9780-00104B6D9C80}")
public interface IDocuments extends Com4jObject, Iterable<Com4jObject> {
    /**
     * Returns the application object
     */
    @VTID(7)
    com.goldensoftware.surfer.IApplication application();

    /**
     * Returns the parent object
     */
    @VTID(8)
    com.goldensoftware.surfer.IApplication parent();

    /**
     * Returns the number of documents
     */
    @VTID(9)
    int count();

    @VTID(10)
    java.util.Iterator<Com4jObject> iterator();

    /**
     * Returns an individual document
     */
    @VTID(11)
    @DefaultMethod
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject item(@MarshalAs(NativeType.VARIANT)
    java.lang.Object index);

    /**
     * Adds a new document to the collection
     */
    @VTID(12)
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject add(@DefaultValue("1")
    com.goldensoftware.surfer.SrfDocTypes docType);

    /**
     * Open an existing document
     */
    @VTID(13)
    @ReturnValue(type = NativeType.Dispatch)
    com4j.Com4jObject open(java.lang.String fileName, @DefaultValue("")
    java.lang.String options);

    /**
     * Saves all documents in the collection
     */
    @VTID(14)
    boolean saveAll(@DefaultValue("0")
    boolean prompt);

    /**
     * Closes all documents in the collection
     */
    @VTID(15)
    boolean closeAll(@DefaultValue("1")
    com.goldensoftware.surfer.SrfSaveTypes saveChanges);

}
