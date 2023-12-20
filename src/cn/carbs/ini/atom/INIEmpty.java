package cn.carbs.ini.atom;

import cn.carbs.ini.atom.interfaces.IINIContent;
import cn.carbs.ini.position.INIPosition;

public class INIEmpty implements IINIContent {

    public INIPosition iniPosition;

    public INIEmpty(INIPosition iniPosition) {
        this.iniPosition = iniPosition;
    }

    @Override
    public INIPosition getPosition() {
        return this.iniPosition;
    }

    @Override
    public String toINIOutputString() {
        return "";
    }

    @Override
    public String toString() {
        return "INIEmpty{iniPosition=" + iniPosition + '}';
    }
}
