package cn.carbs.ini.atom;

import cn.carbs.ini.atom.interfaces.IINIContent;
import cn.carbs.ini.position.INIPosition;

public class INIComment implements IINIContent {

    public String comment;
    public INIPosition iniPosition;

    public INIComment(String comment, INIPosition iniPosition) {
        this.comment = comment;
        this.iniPosition = iniPosition;
    }

    @Override
    public INIPosition getPosition() {
        return this.iniPosition;
    }

    @Override
    public String toINIOutputString() {
        return this.comment;
    }

    @Override
    public String toString() {
        return "INIComment{comment='" + comment + '\'' + ", iniPosition=" + iniPosition + '}';
    }
}
