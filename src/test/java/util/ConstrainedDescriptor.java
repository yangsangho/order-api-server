package util;

import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.util.StringUtils;

import static org.springframework.restdocs.snippet.Attributes.key;

public class ConstrainedDescriptor {
    public ConstrainedDescriptor(Class<?> clazz) {
        constraintDescriptions = new ConstraintDescriptions(clazz);
    }

    private final ConstraintDescriptions constraintDescriptions;

    public FieldDescriptor fieldWithPath(String path) {
        return fieldWithPath(path, path);
    }

    public FieldDescriptor fieldWithPath(String path, String property) {
        return PayloadDocumentation.fieldWithPath(path).attributes(
                key("constraints").value(
                        StringUtils.collectionToDelimitedString(constraintDescriptions.descriptionsForProperty(property), ". ")
                )
        );
    }
}
