package cn.carbs.ini.entity;

import cn.carbs.ini.atom.INIComment;
import cn.carbs.ini.atom.INIKVPair;
import cn.carbs.ini.atom.interfaces.IINIContent;

import java.util.ArrayList;

public class INIEntryObject {

    private ArrayList<INIComment> comments = null;

    private INIKVPair inikvPair;

    public INIEntryObject() {

    }

    public INIEntryObject(INIKVPair inikvPair) {
        setKVPair(inikvPair);
    }

    public void setKVPair(INIKVPair inikvPair) {
        this.inikvPair = inikvPair;
    }

    public INIKVPair getKVPair() {
        return this.inikvPair;
    }

    public void addComments(ArrayList<INIComment> comments) {
        if (comments == null || comments.size() == 0) {
            return;
        }
        if (this.comments == null) {
            this.comments = new ArrayList<>(comments.size());
        }
        this.comments.addAll(comments);
    }

    public void addComment(INIComment comment) {
        if (this.comments == null) {
            this.comments = new ArrayList<>(2);
        }
        this.comments.add(comment);
    }

    public ArrayList<INIComment> getComments() {
        return this.comments;
    }

    public ArrayList<IINIContent> generateContentLines() {
        ArrayList<IINIContent> lines = new ArrayList<>(8);
        if (this.comments != null) {
            lines.addAll(this.comments);
        }
        if (this.inikvPair != null) {
            lines.add(this.inikvPair);
        }
        return lines;
    }
}
