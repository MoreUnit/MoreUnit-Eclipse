package org.moreunit.mock.templates;

public enum Part
{
    TEST_CLASS_ANNOTATION("test-class-annotation"), TEST_CLASS_FIELDS("test-class-fields"), BEFORE_INSTANCE_METHOD("before-instance-method");

    private final String id;

    private Part(String id)
    {
        this.id = id;
    }

    public static Part fromId(String partId)
    {
        for (Part part : values())
        {
            if(part.id.equals(partId))
            {
                return part;
            }
        }
        throw new IllegalArgumentException("Invalid part ID: " + partId);
    }
}
