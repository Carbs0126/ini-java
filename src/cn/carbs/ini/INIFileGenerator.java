package cn.carbs.ini;

import cn.carbs.ini.entity.INIObject;

import java.io.*;
import java.util.ArrayList;

public class INIFileGenerator {

    public static File generateFileFromINIObject(INIObject iniObject, String fileAbsolutePath) {
        if (iniObject == null) {
            throw new IllegalStateException("IniObject should not be null");
        }
        ArrayList<String> lines = iniObject.generateStringLines();
        // 写入文件
        if (lines == null) {
            return null;
        }
        File outputFile = null;
        try {
            outputFile = writeFile(lines, fileAbsolutePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputFile;
    }

    private static File writeFile(ArrayList<String> lines, String fileAbsolutePath) throws IOException {
        File outputFile = new File(fileAbsolutePath);
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
        if (lines != null) {
            int length = lines.size();
            int index = 0;
            for (String line : lines) {
                bufferedWriter.write(line);
                if (index < length - 1) {
                    bufferedWriter.newLine();
                }
                index++;
            }
        }
        bufferedWriter.close();
        return outputFile;
    }

}
