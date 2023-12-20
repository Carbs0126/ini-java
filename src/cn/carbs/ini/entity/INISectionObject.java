package cn.carbs.ini.entity;

import cn.carbs.ini.atom.INISectionHeader;
import cn.carbs.ini.atom.interfaces.IINIContent;
import cn.carbs.ini.atom.INIComment;

import java.util.ArrayList;

public class INISectionObject {

    // section 名称
    private INISectionHeader iniSectionHeader;

    // 属于section的注释
    private ArrayList<INIComment> comments;

    // Key Value
    private ArrayList<INIEntryObject> entryObjects;

    public void addComment(INIComment comment) {
        if (this.comments == null) {
            this.comments = new ArrayList<>(4);
        }
        this.comments.add(comment);
    }

    public void addComments(ArrayList<INIComment> comments) {
        if (comments == null || comments.size() == 0) {
            return;
        }
        if (this.comments == null) {
            this.comments = new ArrayList<>(comments.size() * 2);
        }
        this.comments.addAll(comments);
    }

    public ArrayList<INIComment> getComments() {
        return this.comments;
    }

    public void addEntryObject(INIEntryObject entryObject) {
        if (this.entryObjects == null) {
            this.entryObjects = new ArrayList<>(8);
        }
        this.entryObjects.add(entryObject);
    }

    public String getName() {
        return this.iniSectionHeader.name;
    }

    public void setSectionHeader(INISectionHeader sectionHeader) {
        this.iniSectionHeader = sectionHeader;
    }

    public INISectionHeader getSectionHeader() {
        return this.iniSectionHeader;
    }

    public ArrayList<IINIContent> generateContentLines() {
        ArrayList<IINIContent> lines = new ArrayList<>(8);
        if (this.comments != null) {
            lines.addAll(this.comments);
        }
        if (this.iniSectionHeader != null) {
            lines.add(this.iniSectionHeader);
        }
        for (INIEntryObject iniEntryObject : this.entryObjects) {
            if (iniEntryObject != null) {
                ArrayList<IINIContent> entryLines = iniEntryObject.generateContentLines();
                if (entryLines != null && entryLines.size() > 0) {
                    lines.addAll(entryLines);
                }
            }
        }
        return lines;
    }

}
