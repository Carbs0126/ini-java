package cn.carbs.ini.atom;

import cn.carbs.ini.atom.interfaces.IINIContent;
import cn.carbs.ini.position.INIPosition;

public class INISectionHeader implements IINIContent {

    public String name;
    public INIPosition iniPosition;

    public INISectionHeader(String name, INIPosition iniPosition) {
        this.name = name;
        this.iniPosition = iniPosition;
    }

    @Override
    public INIPosition getPosition() {
        return this.iniPosition;
    }

    @Override
    public String toINIOutputString() {
        if (this.name == null || this.name.length() == 0) {
            throw new IllegalStateException("Key of INISectionHeader should not be empty");
        }
        return this.name;
    }

    @Override
    public String toString() {
        return "INISectionHeader{name='" + name + "\', iniPosition=" + iniPosition + '}';
    }

}
