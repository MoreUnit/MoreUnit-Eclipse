import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class TestRegex4 {
    public static void main(String[] args) {
        String testProjTemplate = "prefix-${srcProject}-suffix";
        String SRC_PROJECT_VARIABLE = "${srcProject}";

        String projectName = "myproject";
        // Old replaceFirst:
        // quoteReplacement(quote(projectName)) = Matcher.quoteReplacement("\\Qmyproject\\E") = "\\\\Qmyproject\\\\E"
        String replacement = Matcher.quoteReplacement(Pattern.quote(projectName));
        String tplOld = testProjTemplate.replaceFirst(Pattern.quote(SRC_PROJECT_VARIABLE), replacement);
        System.out.println("tplOld = " + tplOld);

        // New String.replace:
        String tplNew = testProjTemplate.replace(SRC_PROJECT_VARIABLE, replacement);
        System.out.println("tplNew = " + tplNew);

        // BUT wait! `String.replace` DOES NOT NEED regex escaping for backslashes!
        // `replaceFirst` consumed backslashes!
        // `String.replace` replaces the literal string!
        // So `testProjTemplate.replace(..., "\\\\Qmyproject\\\\E")` results in `\\Qmyproject\\E` which is TWO backslashes!
        // That's exactly why we reverted `quoteReplacement(quote(projectName))` to just `quote(projectName)`!
        // But wait! Did I revert it in getSrcPathFor and getTestPathFor when I reverted the code back to `replaceFirst`???
        // No! In the latest push, I reverted the string replacement but left `quoteReplacement(quote(projectName))`?!
        // Let's check line 203 of TestFolderPathPattern.java:
        // `String srcPathTpl = getSrcPathTemplateForSrcProject(quoteReplacement(quote(projectName)));`
        // Wait, if I am passing `quoteReplacement` to `getSrcPathTemplateForSrcProject`, which uses `String.replace`...
        // IT WILL KEEP DOUBLE BACKSLASHES!
    }
}
