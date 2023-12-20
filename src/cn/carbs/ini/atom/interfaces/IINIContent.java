package cn.carbs.ini.atom.interfaces;

import cn.carbs.ini.position.INIPosition;

public interface IINIContent {

    INIPosition getPosition();

    String toINIOutputString();

}
