package cn.carbs.ini;

import cn.carbs.ini.atom.INIComment;
import cn.carbs.ini.atom.INIEmpty;
import cn.carbs.ini.atom.INIKVPair;
import cn.carbs.ini.atom.INISectionHeader;
import cn.carbs.ini.atom.interfaces.IINIContent;
import cn.carbs.ini.entity.INIEntryObject;
import cn.carbs.ini.entity.INIObject;
import cn.carbs.ini.entity.INISectionObject;
import cn.carbs.ini.position.INIPosition;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class INIFileParser {

    public static INIObject parseFileToINIObject(File iniFile) {

        final String CHARSET_NAME = "UTF-8";

        List<String> content = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(iniFile), CHARSET_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<IINIContent> iniLines = new ArrayList<>(content.size());
        int lineNumber = 0;
        String fileName = iniFile.getAbsolutePath();
        for (String strLine : content) {
            String originLine = (strLine == null) ? "" : strLine;
            String trimmedLine = originLine.trim();
            if (trimmedLine.startsWith(";")) {
                // comment
                INIComment iniComment = new INIComment(originLine, new INIPosition(fileName, lineNumber, 0, originLine.length()));
                appendLineContentIntoLineList(iniComment, iniLines);
            } else if (trimmedLine.startsWith("[")) {
                // section header
                int rightSquareBracketsPosition = trimmedLine.indexOf(']');
                if (rightSquareBracketsPosition < 2) {
                    throw new IllegalStateException("Right square bracket's position should be greater than 1, now it is " + rightSquareBracketsPosition);
                }
                String sectionName = trimmedLine.substring(0, rightSquareBracketsPosition + 1);
                if (sectionName.contains(";")) {
                    throw new IllegalStateException("Section's name should not contain ';' symbol");
                }
                int charBegin = originLine.indexOf('[');
                int charEnd = originLine.indexOf(']');
                INISectionHeader sectionHeader = new INISectionHeader(sectionName, new INIPosition(fileName, lineNumber, charBegin, charEnd));
                appendLineContentIntoLineList(sectionHeader, iniLines);
                checkSemicolon(originLine, charEnd + 1, iniLines, fileName, lineNumber);
            } else if (trimmedLine.length() == 0) {
                INIEmpty iniEmpty = new INIEmpty(new INIPosition(fileName, lineNumber, 0, 0));
                appendLineContentIntoLineList(iniEmpty, iniLines);
            } else {
                // kv
                int indexOfEqualInTrimmedString = trimmedLine.indexOf('=');
                if (indexOfEqualInTrimmedString < 1) {
                    throw new IllegalStateException("Equal's position should be greater than 0");
                }
                int indexOfEqualInOriginString = originLine.indexOf('=');
                String keyName = trimmedLine.substring(0, indexOfEqualInTrimmedString).trim();
                String rightStringOfEqual = trimmedLine.substring(indexOfEqualInTrimmedString + 1);
                StringBuilder valueNameSB = new StringBuilder("");
                int length = rightStringOfEqual.length();
                if (length > 0) {
                    // 0: 过滤前面的空格，还未找到value
                    // 1: 正在记录value
                    // 2: value结束
                    int stat = 0;
                    int i = 0;
                    for (; i < length; i++) {
                        char c = rightStringOfEqual.charAt(i);
                        if (stat == 0) {
                            // 过滤前面的空格
                            if (c == ' ' || c == '\t') {
                                continue;
                            } else {
                                stat = 1;
                                valueNameSB.append(c);
                            }
                        } else if (stat == 1) {
                            // 正在记录value
                            // value中允许有空格
                            if (c == ';') {
                                // 记录 value 结束
                                stat = 2;
                                break;
                            } else {
                                stat = 1;
                                valueNameSB.append(c);
                            }
                        }
                    }
                    String valueName = valueNameSB.toString();
                    int charBegin = originLine.indexOf(keyName);
                    int charEnd = indexOfEqualInOriginString + 1 + i;
                    INIKVPair inikvPair = new INIKVPair(keyName, valueName, new INIPosition(fileName, lineNumber, charBegin, charEnd));
                    appendLineContentIntoLineList(inikvPair, iniLines);
                    if (i != length) {
                        // 没有到结尾，检测是不是有分号
                        checkSemicolon(originLine, indexOfEqualInOriginString + 1 + i, iniLines, fileName, lineNumber);
                    }
                }
            }
            lineNumber++;
        }

        // 最终解析为一个实体
        INIObject iniObject = new INIObject();
        // 收集 section 或者 kv 的 comments
        ArrayList<INIComment> commentsCache = new ArrayList<>(8);
        // 解析完当前的 section ，一次存入
        INISectionObject currentSectionObject = null;
        // 解析当前的 kvPair
        INIEntryObject currentEntryObject = null;

        // 0 解析 section 阶段，还没有解析到 section
        // 1 已经解析出 sectionName 阶段，(刚刚解析完 sectionHeader ) 还没有解析到下一个 section
        int parseState = 0;
        IINIContent preINIContent = null;
        IINIContent curINIContent = null;
        for (IINIContent iniContent : iniLines) {
            if (iniContent instanceof INIEmpty) {
                continue;
            }
            curINIContent = iniContent;
            if (curINIContent instanceof INIComment) {
                INIComment iniComment = (INIComment) curINIContent;
                if (parseState == 0) {
                    // 还没解析到 section
                    commentsCache.add(iniComment);
                } else {
                    if (preINIContent instanceof INISectionHeader) {
                        if (checkSameLine(preINIContent, curINIContent)) {
                            // 当前 comment 属于 section
                            commentsCache.add(iniComment);
                            if (currentSectionObject == null) {
                                currentSectionObject = new INISectionObject();
                            }
                            currentSectionObject.addComments(commentsCache);
                            commentsCache.clear();
                            // 当前 section 的所有 comment 已经结束
                        } else {
                            // 当前 comment 属于当前 section 的 kv 或者下一个 section 的 section
                            if (currentSectionObject == null) {
                                currentSectionObject = new INISectionObject();
                            }
                            currentSectionObject.addComments(commentsCache);
                            commentsCache.clear();
                            commentsCache.add(iniComment);
                        }
                    } else if (preINIContent instanceof INIComment) {
                        // comment 累加
                        commentsCache.add(iniComment);
                    } else if (preINIContent instanceof INIKVPair) {
                        if (checkSameLine(preINIContent, curINIContent)) {
                            // 当前 comment 属于 kv
                            commentsCache.add(iniComment);
                            if (currentEntryObject == null) {
                                // 不走这里
                                currentEntryObject = new INIEntryObject();
                            }
                            currentEntryObject.addComments(commentsCache);
                            if (currentSectionObject == null) {
                                currentSectionObject = new INISectionObject();
                            }
                            currentSectionObject.addEntryObject(currentEntryObject);
                            currentEntryObject = null;
                            commentsCache.clear();
                            // 当前 kv 收尾
                        } else {
                            // 当前 comment 属于当前 section 的下一个 kv 或者下一个 section 的 section
                            commentsCache.clear();
                            commentsCache.add(iniComment);
                        }
                    }
                }
            } else if (curINIContent instanceof INISectionHeader) {
                INISectionHeader iniSectionHeader = (INISectionHeader) curINIContent;
                if (parseState == 0) {
                    // 解析到第一个 section
                    parseState = 1;
                    currentSectionObject = new INISectionObject();
                    currentSectionObject.setSectionHeader(iniSectionHeader);
                } else {
                    if (preINIContent instanceof INISectionHeader) {
                        // 连着两个 section header
                        // 收尾上一个 section header
                        if (currentSectionObject != null) {
                            currentSectionObject.addComments(commentsCache);
                            commentsCache.clear();
                            iniObject.addSection(currentSectionObject);
                        }
                        // 新建 section header
                        currentSectionObject = new INISectionObject();
                        currentSectionObject.setSectionHeader(iniSectionHeader);
                    } else if (preINIContent instanceof INIComment) {
                        if (commentsCache.size() == 0) {
                            // 说明上一个 comment 和其之前的元素是一行，需要收尾上一个 section
                            if (currentSectionObject != null) {
                                iniObject.addSection(currentSectionObject);
                            }
                            currentSectionObject = new INISectionObject();
                            currentSectionObject.setSectionHeader(iniSectionHeader);
                        } else {
                            currentSectionObject = new INISectionObject();
                            currentSectionObject.setSectionHeader(iniSectionHeader);
                            currentSectionObject.addComments(commentsCache);
                            commentsCache.clear();
                        }
                    } else if (preINIContent instanceof INIKVPair) {
                        // 说明上一个 section 结束了，需要收尾
                        if (currentSectionObject != null) {
                            if (currentEntryObject != null) {
                                currentSectionObject.addEntryObject(currentEntryObject);
                                currentEntryObject = null;
                            }
                            iniObject.addSection(currentSectionObject);
                        }
                        currentSectionObject = new INISectionObject();
                        currentSectionObject.setSectionHeader(iniSectionHeader);
                    }
                }
            } else if (curINIContent instanceof INIKVPair) {
                INIKVPair inikvPair = (INIKVPair) curINIContent;
                if (parseState == 0) {
                    // 没有 section，就出现了 kv，说明格式出错
                    throw new IllegalStateException("There should be a section header before key-value pairs");
                } else {
                    if (preINIContent instanceof INISectionHeader) {
                        currentEntryObject = new INIEntryObject();
                        currentEntryObject.setKVPair(inikvPair);
                    } else if (preINIContent instanceof INIComment) {
                        if (commentsCache.size() == 0) {
                            // 说明上一行中，comment 是右边的注释，还包含左边的元素
                            // 当上一行的左侧是 section 时，不需要关心 section
                            // 当上一行的左侧是 kv 时，不需要关心当前 section 或者上一个 kv
                            currentEntryObject = new INIEntryObject();
                            currentEntryObject.setKVPair(inikvPair);
                        } else {
                            currentEntryObject = new INIEntryObject();
                            currentEntryObject.setKVPair(inikvPair);
                        }
                    } else if (preINIContent instanceof INIKVPair) {
                        // 把前一个 kv 收尾到 section 中
                        if (currentEntryObject != null) {
                            currentEntryObject.addComments(commentsCache);
                            commentsCache.clear();
                            if (currentSectionObject != null) {
                                currentSectionObject.addEntryObject(currentEntryObject);
                            }
                        }
                        currentEntryObject = new INIEntryObject();
                        currentEntryObject.setKVPair(inikvPair);
                    }
                }
            }
            preINIContent = curINIContent;
        }

        // 最后一个元素
        if (currentEntryObject != null) {
            currentEntryObject.addComments(commentsCache);
            commentsCache.clear();
        }
        if (currentSectionObject != null) {
            currentSectionObject.addComments(commentsCache);
            commentsCache.clear();
            if (currentEntryObject != null) {
                currentSectionObject.addEntryObject(currentEntryObject);
                currentEntryObject = null;
            }
            iniObject.addSection(currentSectionObject);
        }
        return iniObject;
    }

    public static boolean checkSameLine(IINIContent preINIContent, IINIContent curINIContent) {
        if (preINIContent != null && curINIContent != null) {
            INIPosition prePosition = preINIContent.getPosition();
            INIPosition curPosition = curINIContent.getPosition();
            return prePosition.lineNumber == curPosition.lineNumber;
        }
        return false;
    }

    public static void appendLineContentIntoLineList(IINIContent iINIContent, ArrayList<IINIContent> iniLines) {
        iniLines.add(iINIContent);
    }

    public static INIComment checkSemicolon(String originString,
                                            int charBegin,
                                            ArrayList<IINIContent> iniLines,
                                            String fileLocation,
                                            int lineNumber) {
        String remainStr = originString.substring(charBegin);
        String trimmedRemainStr = remainStr.trim();
        if (trimmedRemainStr.length() > 0) {
            if (trimmedRemainStr.startsWith(";")) {
                INIComment iniComment = new INIComment(trimmedRemainStr,
                        new INIPosition(fileLocation, lineNumber, originString.indexOf(';'), originString.length()));
                appendLineContentIntoLineList(iniComment, iniLines);
                return iniComment;
            } else {
                throw new IllegalStateException("Need ';' symbol, but find " + trimmedRemainStr.charAt(0) + " instead");
            }
        }
        return null;
    }

}
