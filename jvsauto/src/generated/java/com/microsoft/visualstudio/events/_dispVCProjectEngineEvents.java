package com.microsoft.visualstudio.events;

import com4j.*;

@IID("{FBBF3C63-2428-11D7-8BF6-00B0D03DAA06}")
public abstract class _dispVCProjectEngineEvents {
    @DISPID(275)
    public void itemAdded(
        com4j.Com4jObject item,
        com4j.Com4jObject itemParent) {
            throw new UnsupportedOperationException();
    }

    @DISPID(276)
    public void itemRemoved(
        com4j.Com4jObject item,
        com4j.Com4jObject itemParent) {
            throw new UnsupportedOperationException();
    }

    @DISPID(277)
    public void itemRenamed(
        com4j.Com4jObject item,
        com4j.Com4jObject itemParent,
        java.lang.String oldName) {
            throw new UnsupportedOperationException();
    }

    @DISPID(278)
    public void itemMoved(
        com4j.Com4jObject item,
        com4j.Com4jObject newParent,
        com4j.Com4jObject oldParent) {
            throw new UnsupportedOperationException();
    }

    @DISPID(279)
    public void itemPropertyChange(
        com4j.Com4jObject item,
        com4j.Com4jObject tool,
        int propertyID) {
            throw new UnsupportedOperationException();
    }

    @DISPID(280)
    public void sccEvent(
        com4j.Com4jObject item,
        com.microsoft.visualstudio.enumSccEvent eventID) {
            throw new UnsupportedOperationException();
    }

        @DISPID(282)
        public void projectBuildStarted(
            com4j.Com4jObject cfg) {
                throw new UnsupportedOperationException();
        }

        @DISPID(283)
        public void projectBuildFinished(
            com4j.Com4jObject cfg,
            int warnings,
            int errors,
            boolean cancelled) {
                throw new UnsupportedOperationException();
        }

        @DISPID(284)
        public void solutionLoaded() {
                throw new UnsupportedOperationException();
        }

    }
