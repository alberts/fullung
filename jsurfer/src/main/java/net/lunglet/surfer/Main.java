package net.lunglet.surfer;

import net.lunglet.com4j.ComUtils;

import com.goldensoftware.surfer.ClassFactory;
import com.goldensoftware.surfer.IApplication;
import com.goldensoftware.surfer.IContourMap;
import com.goldensoftware.surfer.ILevel;
import com.goldensoftware.surfer.ILevels;
import com.goldensoftware.surfer.IMapFrame;
import com.goldensoftware.surfer.IPlotDocument;
import com.goldensoftware.surfer.IVectorMap;
import com.goldensoftware.surfer.SrfDocTypes;
import com.goldensoftware.surfer.SrfGridFormat;
import com.goldensoftware.surfer.SrfSaveFormat;
import com.goldensoftware.surfer.SrfSaveTypes;
import com.goldensoftware.surfer.SrfVecAngleSys;
import com.goldensoftware.surfer.SrfVecAngleUnits;
import com.goldensoftware.surfer.SrfVecColorMethod;
import com.goldensoftware.surfer.SrfVecCoordSys;
import com.goldensoftware.surfer.srfColor;

public final class Main {
    private static int rgb(final int r, final int g, final int b) {
        if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
            throw new IllegalArgumentException();
        }
        return b << 16 | g << 8 | r;
    }

    private static void gridData(final IApplication app, final String dataFile, final int xCol, final int yCol,
            final int zCol, final String outGrid, final SrfGridFormat outFmt) {
        boolean showReport = false;
        app.gridData(dataFile, xCol, yCol, zCol, null, null, null, null, null, null, null, null, null, null, null,
            showReport, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            outGrid, outFmt, null, null, null, null, null, null);
    }

    public static void main(final String[] args) {
        String infilename = "C:\\home\\albert\\work7\\jsurfer\\input.dat";
        String filename1 = "C:\\home\\albert\\work7\\jsurfer\\Hsig.grd";
        String filename2 = "C:\\home\\albert\\work7\\jsurfer\\PkDir.grd";
        String filename3 = "C:\\home\\albert\\work7\\jsurfer\\Hsig2.grd";
        String outfilename = "C:\\home\\albert\\work7\\jsurfer\\out.srf";
        String exportfile = "C:\\home\\albert\\work7\\jsurfer\\out.emf";

        System.out.println("running Surfer");
        IApplication app = ClassFactory.createApplication();
        app.visible(true);

        app.documents().closeAll(SrfSaveTypes.srfSaveChangesNo);

//        gridData(app, infilename, 1, 2, 4, filename1, SrfGridFormat.srfGridFmtBinary);
//        gridData(app, infilename, 1, 2, 5, filename2, SrfGridFormat.srfGridFmtBinary);
//        app.gridMath("C=max(A,0)", filename1, filename2, filename3, SrfGridFormat.srfGridFmtBinary);

        IPlotDocument plot = app.documents().add(SrfDocTypes.srfDocPlot).queryInterface(IPlotDocument.class);
        IMapFrame mapFrame = plot.shapes().addContourMap(filename1);
        mapFrame.backgroundFill().foreColor(srfColor.srfColorArmyGreen);
        mapFrame.backgroundFill().pattern("solid");
        IContourMap contourMap = mapFrame.overlays(1).queryInterface(IContourMap.class);
        contourMap.fillContours(true);
        contourMap.showColorScale(true);
        ILevels levels = contourMap.levels();
        levels.autoGenerate(0.0, 2.5, 0.1);
        
        int i = 0;
        for (ILevel level : ComUtils.queryIterable(levels, ILevel.class)) {
            int r = (int) (255.0f * (((float) i) / levels.count()));
            int g = 0;
            int b = (int) (255.0f - 255.0f * (float) i / levels.count());
            level.fill().foreColor(rgb(r, g, b));
            level.fill().pattern("solid");
            if (level.value() > 0.0) {
                level.showLabel(true);
            }
            i++;
        }
        
        mapFrame = plot.shapes().addVectorMap(filename2, filename3, SrfVecCoordSys.srfVecPolar,
            SrfVecAngleSys.srfVecAngle, SrfVecAngleUnits.srfVecDegrees);
        IVectorMap vectorMap = mapFrame.overlays(1).queryInterface(IVectorMap.class);
        int orange = rgb(255, 102, 0);
        vectorMap.colorMap().setNodes(new double[]{0.0, 1.0}, new int[]{orange, orange});
        vectorMap.colorScaleMethod(SrfVecColorMethod.srfVecMagnitude);

        plot.shapes().selectAll();
        plot.selection().overlayMaps();
        plot.saveAs(outfilename, "", SrfSaveFormat.srfSaveFormatUnknown);
        plot.export(exportfile, false, "");

//        app.quit();
    }
}
