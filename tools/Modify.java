import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Modify {

    public static void modify(String filePath) {
        try (
                BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                Pattern prereqPattern = Pattern.compile("Prerequisite: (.*?);?(?: Never Taken:? (.*?)| Course Corequisite: (.*?)| Not Registered: (.*?))?$"
                        , Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
                Pattern coreqPattern = Pattern.compile("Corequisite: (.+) (?: Never Taken:? (.*?))?$");
                Pattern neverTaken = Pattern.compile(
                        "Not Registered: (.+);(?: Never Taken: (.*)| Course Corequisite: (.*))?$");

                Matcher m;

                m = prereqPattern.matcher(line);
                if (m.find()) {
                    System.out.println("Prerequisites: " + m.group(1).trim());

                    if (m.group(2) != null) {
                        System.out.println("Restriction: " + m.group(2).trim());
                    } else if (m.group(3) != null) {
                        System.out.println("Corequisite: " + m.group(3).trim());
                    } else if (m.group(4) != null){
                        System.out.println("Restriction: " + m.group(3).trim());
                    }
                }

                m = coreqPattern.matcher(line);
                    if (m.find()) {
                        System.out.println("Corequisite: " + m.group(1).trim());

                        if (m.group(2) != null) {
                            System.out.println("Restriction: " + m.group(2).trim());
                        }
                    }

                m = neverTaken.matcher(line);
                    if (m.find()) {
                        System.out.println("Restriction: " + m.group(1).trim());
                        if (m.group(2) != null) {
                            System.out.println("Restriction: " + m.group(2).trim());
                        } else if (m.group(3) != null) {
                            System.out.println("Course Corequisite: " + m.group(3).trim());
                        }
                    }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        String filePath = "~/prereqAnalysis.json";
        modify(filePath);
    }

}
