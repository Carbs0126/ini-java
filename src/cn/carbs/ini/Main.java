package cn.carbs.ini;

import cn.carbs.ini.atom.INIKVPair;
import cn.carbs.ini.atom.INISectionHeader;
import cn.carbs.ini.entity.INIEntryObject;
import cn.carbs.ini.entity.INIObject;
import cn.carbs.ini.entity.INISectionObject;
import java.util.function.Predicate;

import java.io.File;

public class Main {

    public static void main(String[] args) {

        Predicate p = (x) -> {return true;};

//        INIObject iniObject = INIFileParser.parseFileToINIObject(getINITestFile());
//        // 动态增加section
//        /*INISectionObject sectionObject = new INISectionObject();
//        INISectionHeader sectionHeader = new INISectionHeader("[new_section]", null);
//        sectionObject.setSectionHeader(sectionHeader);
//        INIEntryObject entryObject0 = new INIEntryObject(new INIKVPair("new_key", "new_value", null));
//        INIEntryObject entryObject1 = new INIEntryObject(new INIKVPair("new_key1", "new_value1", null));
//        sectionObject.addEntryObject(entryObject0);
//        sectionObject.addEntryObject(entryObject1);
//        iniObject.addSection(sectionObject);*/
//
//        INIFileGenerator.generateFileFromINIObject(iniObject, new File("test-output.ini").getAbsolutePath());
    }

    public static File getINITestFile() {
        return new File("test-input.ini");
    }

}
