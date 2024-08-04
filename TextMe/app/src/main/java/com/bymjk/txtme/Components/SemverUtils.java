package com.bymjk.txtme.Components;

public class SemverUtils {

    public static Integer getSemverStringToInteger(String strSemver) {
        Integer semverInt = 0;

        String[] splittedSmver = strSemver.split("\\.");
        String x = splittedSmver[0];
        String y = splittedSmver[1];
        String z = splittedSmver[2];

        semverInt = Integer.parseInt(x) * 1000 * 1000 + Integer.parseInt(y) * 1000 + Integer.parseInt(z);
        return semverInt;
    }

    public static String getSemverIntegerToString(int intSemver) {
        int x,y,z;
        String version = " ";
        // Logic for Converting xxxyyy   zzz to x.y.z  001(x) 002 003   y = 001002 %1000
        x = intSemver / 1000000;
        y = intSemver / 1000;
        y = y % 1000;
        z = intSemver % 1000;

        version = x+"."+y+"."+z;
        return version;
    }

}
