package cn.carbs.ini.atom;

import cn.carbs.ini.atom.interfaces.IINIContent;
import cn.carbs.ini.position.INIPosition;

public class INIKVPair implements IINIContent {
    public String key;
    public String value;
    public INIPosition iniPosition;

    public INIKVPair(String key, String value, INIPosition iniPosition) {
        this.key = key;
        this.value = value;
        this.iniPosition = iniPosition;
    }

    @Override
    public INIPosition getPosition() {
        return this.iniPosition;
    }

    @Override
    public String toINIOutputString() {
        if (this.key == null || this.key.length() == 0) {
            throw new IllegalStateException("Key of INIEntry should not be empty");
        }
        if (this.value == null) {
            this.value = "";
        }
        return this.key + "=" + this.value;
    }

    @Override
    public String toString() {
        return "INIKVPair{key='" + key + "\', value='" + value + "\', iniPosition=" + iniPosition + '}';
    }
}
