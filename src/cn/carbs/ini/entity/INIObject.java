package cn.carbs.ini.entity;

import cn.carbs.ini.atom.interfaces.IINIContent;
import cn.carbs.ini.position.INIPosition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;

// 代表一个文件的内容
public class INIObject {

    private ConcurrentHashMap<String, INISectionObject> sectionsMap = new ConcurrentHashMap<>();

    private ArrayList<String> orderedSectionsName = new ArrayList<>(8);

    public ArrayList<String> generateStringLines() {
        ArrayList<IINIContent> iniContentLines = new ArrayList<>(8);
        for (String sectionName : this.orderedSectionsName) {
            if (sectionName != null && this.sectionsMap.containsKey(sectionName)) {
                INISectionObject iniSectionObject = this.sectionsMap.get(sectionName);
                ArrayList<IINIContent> oneSectionLines = iniSectionObject.generateContentLines();
                if (oneSectionLines != null && oneSectionLines.size() > 0) {
                    iniContentLines.addAll(oneSectionLines);
                }
            }
        }
        // 排序  先 line number，后 start position
        Collections.sort(iniContentLines, new Comparator<IINIContent>() {
            @Override
            public int compare(IINIContent a, IINIContent b) {
                if (a == null || b == null) {
                    return 0;
                }
                INIPosition iniPositionA = a.getPosition();
                INIPosition iniPositionB = b.getPosition();
                // 将 position 为空的元素排到最后
                if (iniPositionA == null) {
                    return 1;
                }
                if (iniPositionB == null) {
                    return -1;
                }
                int lineNumber = iniPositionA.lineNumber - iniPositionB.lineNumber;
                if (lineNumber != 0) {
                    return lineNumber;
                }
                return iniPositionA.charBegin - iniPositionB.charBegin;
            }
        });

        ArrayList<String> stringLines = new ArrayList<>(8);
        StringBuilder sbOneLine = new StringBuilder();
        int preLineNumber = -1;
        int curLineNumber = -1;
        for (IINIContent iiniContent : iniContentLines) {
            if (iiniContent == null) {
                continue;
            }
            INIPosition curINIPosition = iiniContent.getPosition();
            if (curINIPosition == null) {
                if (sbOneLine.length() > 0) {
                    stringLines.add(sbOneLine.toString());
                    sbOneLine.delete(0, sbOneLine.length());
                }
                stringLines.add(iiniContent.toINIOutputString());
                continue;
            }
            curLineNumber = curINIPosition.lineNumber;
            if (preLineNumber != curLineNumber) {
                if (preLineNumber > -1) {
                    stringLines.add(sbOneLine.toString());
                    sbOneLine.delete(0, sbOneLine.length());
                }
                int lineDelta = curLineNumber - preLineNumber;
                if (lineDelta > 1) {
                    // 中间有空行
                    for (int i = 0; i < lineDelta - 1; i++) {
                        stringLines.add("");
                    }
                }
                sbOneLine.append(iiniContent.toINIOutputString());
            } else {
                sbOneLine.append(iiniContent.toINIOutputString());
            }
            preLineNumber = curLineNumber;
        }
        stringLines.add(sbOneLine.toString());
        return stringLines;
    }

    public void addSection(INISectionObject section) {
        this.orderedSectionsName.add(section.getName());
        this.sectionsMap.put(section.getName(), section);
    }

    public INISectionObject getSection(String sectionName) {
        if (sectionName == null || sectionName.length() == 0 || !this.sectionsMap.contains(sectionName)) {
            return null;
        }
        return this.sectionsMap.get(sectionName);
    }

    public ConcurrentHashMap getSectionsMap() {
        return this.sectionsMap;
    }

    public ArrayList<String> getOrderedSectionsName() {
        return this.orderedSectionsName;
    }
}
