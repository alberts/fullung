package net.lunglet.features.mfcc;

// load sphere header

// load mlf

// load htk base mfcc

// check last mlf timestamp against sph header to make sure its all there

// check mfcc size against window + length info from sph header

// remove silence, giving us a bunch of blocks

// discard blocks that are too small to work with the amount of deltaing we want to do

// gaussianize across all blocks

// delta and delta-delta, etc. duplicate at boundaries like htk does

// deltawindow = 2, accwindow = 2

// later: cross channel squelch: nuke features where energy differs by less than 3db
// 10log10(e1/e2) > 3dB = 10log10(2) (dubbel die energy)

public class MFCCBuilder {

}
