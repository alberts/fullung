package com.microsoft.visualstudio  ;

import com4j.*;

/**
 * VCDebugSettings
 */
@IID("{238B5172-2429-11D7-8BF6-00B0D03DAA06}")
public interface VCDebugSettings extends Com4jObject {
    @VTID(7)
    java.lang.String command();

    @VTID(8)
    void command(
        java.lang.String val);

    @VTID(9)
    java.lang.String commandArguments();

    @VTID(10)
    void commandArguments(
        java.lang.String val);

    @VTID(11)
    java.lang.String workingDirectory();

    @VTID(12)
    void workingDirectory(
        java.lang.String val);

    @VTID(13)
    boolean attach();

    @VTID(14)
    void attach(
        boolean val);

    @VTID(15)
    java.lang.String pdbPath();

    @VTID(16)
    void pdbPath(
        java.lang.String val);

    @VTID(17)
    com.microsoft.visualstudio.TypeOfDebugger debuggerType();

    @VTID(18)
    void debuggerType(
        com.microsoft.visualstudio.TypeOfDebugger val);

    @VTID(19)
    java.lang.String environment();

    @VTID(20)
    void environment(
        java.lang.String val);

    @VTID(21)
    boolean environmentMerge();

    @VTID(22)
    void environmentMerge(
        boolean val);

    @VTID(23)
    boolean sqlDebugging();

    @VTID(24)
    void sqlDebugging(
        boolean val);

    @VTID(25)
    java.lang.String httpUrl();

    @VTID(26)
    void httpUrl(
        java.lang.String val);

    @VTID(27)
    com.microsoft.visualstudio.RemoteDebuggerType remote();

    @VTID(28)
    void remote(
        com.microsoft.visualstudio.RemoteDebuggerType val);

    @VTID(29)
    java.lang.String remoteMachine();

    @VTID(30)
    void remoteMachine(
        java.lang.String val);

    @VTID(31)
    java.lang.String remoteCommand();

    @VTID(32)
    void remoteCommand(
        java.lang.String val);

    @VTID(33)
    com.microsoft.visualstudio.eDebuggerTypes debuggerFlavor();

    @VTID(34)
    void debuggerFlavor(
        com.microsoft.visualstudio.eDebuggerTypes val);

    @VTID(35)
    java.lang.String mpiRunCommand();

    @VTID(36)
    void mpiRunCommand(
        java.lang.String val);

    @VTID(37)
    java.lang.String mpiRunArguments();

    @VTID(38)
    void mpiRunArguments(
        java.lang.String val);

    @VTID(39)
    java.lang.String mpiRunWorkingDirectory();

    @VTID(40)
    void mpiRunWorkingDirectory(
        java.lang.String val);

    @VTID(41)
    java.lang.String applicationCommand();

    @VTID(42)
    void applicationCommand(
        java.lang.String val);

    @VTID(43)
    java.lang.String applicationArguments();

    @VTID(44)
    void applicationArguments(
        java.lang.String val);

    @VTID(45)
    java.lang.String shimCommand();

    @VTID(46)
    void shimCommand(
        java.lang.String val);

    @VTID(47)
    com.microsoft.visualstudio.enumMPIAcceptModes mpiAcceptMode();

    @VTID(48)
    void mpiAcceptMode(
        com.microsoft.visualstudio.enumMPIAcceptModes val);

    @VTID(49)
    java.lang.String mpiAcceptFilter();

    @VTID(50)
    void mpiAcceptFilter(
        java.lang.String val);

}
