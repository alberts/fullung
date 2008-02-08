package net.lunglet.htk;

public final class HTKConstants {
    public static final short ANON = 12;

    /** Vector quantised codebook. */
    public static final short DISCRETE = 10;

    /** Log Fliter bank energies. */
    public static final short FBANK = 7;

    /** LPC Reflection coefficients (16 bit fixed point). */
    public static final short IREFC = 5;

    /** Linear prediction coefficients. */
    public static final short LPC = 1;

    /** LPC Cepstral coefficients. */
    public static final short LPCEPSTRA = 3;

    /** LPC cepstral+delta coefficients (obsolete). */
    public static final short LPDELCEP = 4;

    /** LPC Reflection coefficients. */
    public static final short LPREFC = 2;

    /** Linear Mel-scaled spectrum. */
    public static final short MELSPEC = 8;

    /** Mel frequency cepstral coefficients. */
    public static final short MFCC = 6;

    /** Include delta coefficients. */
    public static final int MODIFIER_D = 0x100;

    /** Includes energy terms. */
    public static final int MODIFIER_E = 0x40;

    /** Suppress absolute energy. */
    public static final int MODIFIER_N = 0x80;

    /** Zero mean static coefficients. */
    public static final int MODIFIER_Z = 0x800;

    /** Perceptual Linear prediction. */
    public static final short PLP = 11;

    /** User defined features. */
    public static final short USER = 9;

    /** Acoustic waveform. */
    public static final short WAVEFORM = 0;

    private HTKConstants() {
    }
}
