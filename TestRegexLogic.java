import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class TestRegexLogic {
    public static void main(String[] args) {
        String cleanSrcPath = "js-project/src";
        String projectName = "js-project";
        String srcPathTemplate = "${srcProject}/src";
        String SRC_PROJECT_VARIABLE = "${srcProject}";

        // original logic:
        String tpl = srcPathTemplate.replaceFirst(Pattern.quote(SRC_PROJECT_VARIABLE), projectName);
        String srcPathTpl = tpl.replace("**", "\0").replace("*", "[^/]*").replace("\0", ".*");
        System.out.println("srcPathTpl = " + srcPathTpl);

        // Let's print what original logic did:
        String codePathWithinSrcFolder = cleanSrcPath.replaceFirst(srcPathTpl, "");
        System.out.println("codePathWithinSrcFolder = " + codePathWithinSrcFolder);

        // Wait! The original logic was:
        // getSrcPathTemplateForSrcProject(quoteReplacement(quote(projectName)))
        // projectName = "js-project"
        // quote(projectName) = "\\Qjs-project\\E"
        // quoteReplacement("\\Qjs-project\\E") = "\\\\Qjs-project\\\\E"
        // srcPathTemplate.replaceFirst(quote(SRC_PROJECT_VARIABLE), projectName)
        // with projectName = "\\\\Qjs-project\\\\E"
        // outputs -> tpl = "\\Qjs-project\\E/src"

        String tplOld = srcPathTemplate.replaceFirst(Pattern.quote(SRC_PROJECT_VARIABLE), Matcher.quoteReplacement(Pattern.quote(projectName)));
        String srcPathTplOld = tplOld.replace("**", "\0").replace("*", "[^/]*").replace("\0", ".*");
        System.out.println("srcPathTplOld = " + srcPathTplOld);

        String codePathWithinSrcFolderOld = cleanSrcPath.replaceFirst(srcPathTplOld, "");
        System.out.println("codePathWithinSrcFolderOld = '" + codePathWithinSrcFolderOld + "'");


        // MY logic:
        // getSrcPathTemplateForSrcProject(Pattern.quote(projectName))
        // So projectName = "\\Qjs-project\\E"
        // tpl = srcPathTemplate.replace(SRC_PROJECT_VARIABLE, projectName)
        // tpl = "\\Qjs-project\\E/src"
        // srcPathTpl = "\\Qjs-project\\E/src"

        // Then:
        // if(cleanSrcPath.startsWith(srcPathTpl)) -> cleanSrcPath="js-project/src", srcPathTpl="\\Qjs-project\\E/src"
        // DOES "js-project/src" START WITH "\\Qjs-project\\E/src"? NO!
        // That's why it was failing!
        // It failed because my startsWith check was comparing literal strings, but `srcPathTpl` was built using `Pattern.quote()`!

        // Let's verify:
        String srcPathTplNew = srcPathTemplate.replace(SRC_PROJECT_VARIABLE, Pattern.quote(projectName)).replace("**", "\0").replace("*", "[^/]*").replace("\0", ".*");
        System.out.println("srcPathTplNew = " + srcPathTplNew);
        System.out.println("cleanSrcPath.startsWith(srcPathTplNew) = " + cleanSrcPath.startsWith(srcPathTplNew));
    }
}
