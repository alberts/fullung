package com.goldensoftware.surfer;

import com4j.ComEnum;

public enum wksPaper implements ComEnum {
    wksPaperLetter(1),
    wksPaperLegal(2),
    wksPaperA4(3),
    wksPaperCSheet(4),
    wksPaperDSheet(5),
    wksPaperESheet(6),
    wksPaperLetterSmall(7),
    wksPaperTabloid(8),
    wksPaperLedger(9),
    wksPaperStatement(10),
    wksPaperExecutive(11),
    wksPaperA3(12),
    wksPaperA4Small(13),
    wksPaperA5(14),
    wksPaperB4(15),
    wksPaperB5(16),
    wksPaperFolio(17),
    wksPaperQuarto(18),
    wksPaper10x14(19),
    wksPaper11x17(20),
    wksPaperNote(21),
    wksPaperEnv_9(22),
    wksPaperEnv_10(23),
    wksPaperEnv_11(24),
    wksPaperEnv_12(25),
    wksPaperEnv_14(26),
    wksPaperEnv_DL(27),
    wksPaperEnv_C5(28),
    wksPaperEnv_C3(29),
    wksPaperEnv_C4(30),
    wksPaperEnv_C6(31),
    wksPaperEnv_C65(32),
    wksPaperEnv_B4(33),
    wksPaperEnv_B5(34),
    wksPaperEnv_B6(35),
    wksPaperEnv_Italy(36),
    wksPaperEnv_Monarch(37),
    wksPaperEnv_Personal(38),
    wksPaperFanfold_US(39),
    wksPaperFanfold_Std_German(40),
    wksPaperFanfold_Lgl_German(41),
    wksPaperISO_B4(42),
    wksPaperJapanese_Postcard(43),
    wksPaper9x11(44),
    wksPaper10x11(45),
    wksPaper15x11(46),
    wksPaperEnv_Invite(47),
    wksPaperLetter_Extra(50),
    wksPaperLegal_Extra(51),
    wksPaperTabloid_Extra(52),
    wksPaperA4_Extra(53),
    wksPaperLetter_Transverse(54),
    wksPaperA4_Transverse(55),
    wksPaperLetter_Extra_Transverse(56),
    wksPaperA_Plus(57),
    wksPaperB_Plus(58),
    wksPaperLetter_Plus(59),
    wksPaperA4_Plus(60),
    wksPaperA5_Transverse(61),
    wksPaperB5_Transverse(62),
    wksPaperA3_Extra(63),
    wksPaperA5_Extra(64),
    wksPaperB5_Extra(65),
    wksPaperA2(66),
    wksPaperA3_Transverse(67),
    wksPaperA3_Extra_Transverse(68), ;

    private final int value;

    wksPaper(int value) {
        this.value = value;
    }

    public int comEnumValue() {
        return value;
    }
}
