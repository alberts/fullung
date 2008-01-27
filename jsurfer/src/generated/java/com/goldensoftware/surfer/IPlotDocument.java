package com.goldensoftware.surfer;

import com4j.DefaultValue;
import com4j.IID;
import com4j.MarshalAs;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * IPlotDocument Interface
 */
@IID("{B2933404-9788-11D2-9780-00104B6D9C80}")
public interface IPlotDocument extends com.goldensoftware.surfer.IDocument {
    /**
     * Returns the Shapes collection
     */
    @VTID(21)
    com.goldensoftware.surfer.IShapes shapes();

    @VTID(21)
    @ReturnValue(type = NativeType.Dispatch, defaultPropertyThrough = {com.goldensoftware.surfer.IShapes.class})
    com4j.Com4jObject shapes(@MarshalAs(NativeType.VARIANT)
    java.lang.Object index);

    /**
     * Returns the current selection
     */
    @VTID(22)
    com.goldensoftware.surfer.ISelection selection();

    @VTID(22)
    @ReturnValue(defaultPropertyThrough = {com.goldensoftware.surfer.ISelection.class})
    com.goldensoftware.surfer.IShape selection(@MarshalAs(NativeType.VARIANT)
    java.lang.Object index);

    /**
     * Returns the PageSetup object
     */
    @VTID(23)
    com.goldensoftware.surfer.IPageSetup pageSetup();

    /**
     * Shows/Hides the Object Manager window
     */
    @VTID(24)
    boolean showObjectManager();

    /**
     * Shows/Hides the Object Manager window
     */
    @VTID(25)
    void showObjectManager(boolean pShow);

    /**
     * Imports a graphic file
     */
    @VTID(26)
    com.goldensoftware.surfer.IShape _import(java.lang.String fileName, @DefaultValue("")
    java.lang.String options);

    /**
     * Exports the document to a specified graphics file
     */
    @VTID(27)
    boolean export(java.lang.String fileName, @DefaultValue("0")
    boolean selectionOnly, @DefaultValue("")
    java.lang.String options);

    /**
     * Sends the document to the current printer
     */
    @VTID(28)
    boolean printOut(@MarshalAs(NativeType.VARIANT)
    java.lang.Object method, @MarshalAs(NativeType.VARIANT)
    java.lang.Object selectionOnly, @MarshalAs(NativeType.VARIANT)
    java.lang.Object numCopies, @MarshalAs(NativeType.VARIANT)
    java.lang.Object collate, @MarshalAs(NativeType.VARIANT)
    java.lang.Object xOverlap, @MarshalAs(NativeType.VARIANT)
    java.lang.Object yOverlap, @MarshalAs(NativeType.VARIANT)
    java.lang.Object scale);

    /**
     * Returns the default line properties object for this document
     */
    @VTID(29)
    com.goldensoftware.surfer.ILineFormat defaultLine();

    /**
     * Returns the default fill properties object for this document
     */
    @VTID(30)
    com.goldensoftware.surfer.IFillFormat defaultFill();

    /**
     * Returns the default font properties object for this document
     */
    @VTID(31)
    com.goldensoftware.surfer.IFontFormat defaultFont();

    /**
     * Returns the default symbol properties object for this document
     */
    @VTID(32)
    com.goldensoftware.surfer.IMarkerFormat defaultSymbol();

}
