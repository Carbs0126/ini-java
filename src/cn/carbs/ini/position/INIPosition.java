package cn.carbs.ini.position;

public class INIPosition {

    // [charBegin, charEnd)
    public String fileLocation;
    public int lineNumber;
    public int charBegin;
    public int charEnd;

    public INIPosition(String fileLocation, int lineNumber, int charBegin, int charEnd) {
        this.fileLocation = fileLocation;
        this.lineNumber = lineNumber;
        this.charBegin = charBegin;
        this.charEnd = charEnd;
    }

    @Override
    public String toString() {
        return "INIPosition{" +
                "fileLocation='" + fileLocation + '\'' +
                ", lineNumber=" + lineNumber +
                ", charBegin=" + charBegin +
                ", charEnd=" + charEnd +
                '}';
    }
}
