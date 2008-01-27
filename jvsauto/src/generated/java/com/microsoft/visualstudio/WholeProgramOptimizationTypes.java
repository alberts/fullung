package com.microsoft.visualstudio  ;

import com4j.*;

public enum WholeProgramOptimizationTypes {
    WholeProgramOptimizationNone, // 0
    WholeProgramOptimizationLinkTimeCodeGen, // 1
    WholeProgramOptimizationPGOInstrument, // 2
    WholeProgramOptimizationPGOOptimize, // 3
    WholeProgramOptimizationPGOUpdate, // 4
}
