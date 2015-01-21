package org.moreunit.elements;

import org.eclipse.jdt.core.IMethod;
import org.moreunit.preferences.Preferences.MethodSearchMode;

public class CorrespondingMemberRequest
{
    public static Builder newCorrespondingMemberRequest()
    {
        return new Builder();
    }

    private Builder builder;

    private CorrespondingMemberRequest(Builder builder)
    {
        this.builder = builder;
    }

    public IMethod getCurrentMethod()
    {
        return builder.currentMethod;
    }

    public MethodSearchMode getMethodSearchMode()
    {
        return builder.methodSearchMode;
    }

    public String getPromptText()
    {
        return builder.promptText;
    }

    public boolean shouldCreateClassIfNoResult()
    {
        return builder.createClassIfNoResult;
    }

    public boolean shouldReturn(MemberType memberType)
    {
        return builder.expectedMemberType == memberType;
    }

    public static enum MemberType
    {
        TYPE, TYPE_OR_METHOD
    }

    public static final class Builder
    {
        private boolean createClassIfNoResult;
        private IMethod currentMethod;
        private MethodSearchMode methodSearchMode = MethodSearchMode.DEFAULT;
        private String promptText;
        private MemberType expectedMemberType = MemberType.TYPE_OR_METHOD;

        private Builder()
        {
        }

        /**
         * Will propose the creation of a type if no correspondence is found.
         * 
         * @param promptText the prompt text to display in the dialog asking the
         *            user to choose for a member
         */
        public Builder createClassIfNoResult(String promptText)
        {
            this.createClassIfNoResult = true;
            this.promptText = promptText;
            return this;
        }

        /**
         * How to search for corresponding methods (by call or/and by method
         * name).
         */
        public Builder methodSearchMode(MethodSearchMode methodSearchMode)
        {
            this.methodSearchMode = methodSearchMode;
            return this;
        }

        /**
         * The method to search for correspondence (if not provided, the search
         * will only be based on the type).
         */
        public Builder withCurrentMethod(IMethod method)
        {
            currentMethod = method;
            return this;
        }

        public Builder withExpectedResultType(MemberType memberType)
        {
            this.expectedMemberType = memberType;
            return this;
        }

        public CorrespondingMemberRequest build()
        {
            return new CorrespondingMemberRequest(this);
        }
    }
}
