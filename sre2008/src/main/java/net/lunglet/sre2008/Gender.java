package net.lunglet.sre2008;

public enum Gender {
    FEMALE, MALE;

    public static Gender easyValueOf(String gender) {
        if (gender.toLowerCase().startsWith("m")) {
            return MALE;
        } else if (gender.toLowerCase().startsWith("f")) {
            return FEMALE;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
