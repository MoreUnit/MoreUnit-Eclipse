import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class TestRegex3 {
    public static void main(String[] args) {
        String cleanSrcPath = "myproject/src/code";
        String projectName = "myproject";
        String srcPathTemplate = "${srcProject}/src";
        String SRC_PROJECT_VARIABLE = "${srcProject}";

        // 1. Build regex
        // we pass quote(projectName) which is "\Qmyproject\E"
        String quotedProject = Pattern.quote(projectName);

        // getSrcPathTemplateForSrcProject:
        String tpl = srcPathTemplate.replace(SRC_PROJECT_VARIABLE, quotedProject);
        String srcPathTpl = tpl.replace("**", "\0").replace("*", "[^/]*").replace("\0", ".*");

        System.out.println("srcPathTpl = " + srcPathTpl);

        // 2. replaceFirst
        String codePathWithinSrcFolder = cleanSrcPath.replaceFirst(srcPathTpl, "");
        System.out.println("codePathWithinSrcFolder = " + codePathWithinSrcFolder);

        // 3. For getTestPathTemplateForSrcProject, we don't pass quoted project name. We just pass projectName.
        // wait, let's look at getTestPathFor logic
    }
}
