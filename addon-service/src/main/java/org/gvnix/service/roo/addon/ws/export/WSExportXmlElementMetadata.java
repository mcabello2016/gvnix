/*
 * gvNIX. Spring Roo based RAD tool for Conselleria d'Infraestructures
 * i Transport - Generalitat Valenciana
 * Copyright (C) 2010 CIT - Generalitat Valenciana
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gvnix.service.roo.addon.ws.export;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.gvnix.service.roo.addon.annotations.GvNIXXmlElement;
import org.gvnix.service.roo.addon.annotations.GvNIXXmlElementField;
import org.springframework.roo.classpath.PhysicalTypeCategory;
import org.springframework.roo.classpath.PhysicalTypeIdentifierNamingUtils;
import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.details.DeclaredFieldAnnotationDetails;
import org.springframework.roo.classpath.details.FieldMetadata;
import org.springframework.roo.classpath.details.FieldMetadataBuilder;
import org.springframework.roo.classpath.details.MemberFindingUtils;
import org.springframework.roo.classpath.details.MethodMetadataBuilder;
import org.springframework.roo.classpath.details.annotations.AnnotatedJavaType;
import org.springframework.roo.classpath.details.annotations.AnnotationAttributeValue;
import org.springframework.roo.classpath.details.annotations.AnnotationMetadata;
import org.springframework.roo.classpath.details.annotations.AnnotationMetadataBuilder;
import org.springframework.roo.classpath.details.annotations.ArrayAttributeValue;
import org.springframework.roo.classpath.details.annotations.EnumAttributeValue;
import org.springframework.roo.classpath.details.annotations.StringAttributeValue;
import org.springframework.roo.classpath.itd.AbstractItdTypeDetailsProvidingMetadataItem;
import org.springframework.roo.classpath.itd.InvocableMemberBodyBuilder;
import org.springframework.roo.metadata.MetadataIdentificationUtils;
import org.springframework.roo.model.EnumDetails;
import org.springframework.roo.model.JavaSymbolName;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.project.Path;
import org.springframework.roo.support.util.Assert;

/**
 * <p>
 * gvNix Xml Element Marsharlling generation.
 * </p>
 * 
 * @author Ricardo García Fernández at <a href="http://www.disid.com">DiSiD
 *         Technologies S.L.</a> made for <a
 *         href="http://www.cit.gva.es">Conselleria d'Infraestructures i
 *         Transport</a>
 */
public class WSExportXmlElementMetadata extends
        AbstractItdTypeDetailsProvidingMetadataItem {

    private static final String XML_ELEMENT_STRING = WSExportXmlElementMetadata.class
            .getName();

    private static final String XML_ELEMENT_TYPE = MetadataIdentificationUtils
            .create(XML_ELEMENT_STRING);

    public WSExportXmlElementMetadata(String id, JavaType aspectName,
            PhysicalTypeMetadata physicalType, List<FieldMetadata> fields) {

        super(id, aspectName, physicalType);

        // Validate metadata identifier is of type gvNIX xml element metadata
        Assert.isTrue(isValid(id), "Metadata identification string '" + id
                + "' does not appear to be valid");
        if (!isValid()) {
            return;
        }

        // Get the gvNIX xml element annotation
        AnnotationMetadata annotation = MemberFindingUtils.getTypeAnnotation(
                governorTypeDetails,
                new JavaType(GvNIXXmlElement.class.getName()));
        if (annotation != null) {

            // Add to class XmlRoot, XmlType and XmlEnum or XmlAccessorType
            List<AnnotationMetadata> annotationTypeList = getAnnotations(
                    annotation, fields);
            for (AnnotationMetadata annotationMetadata : annotationTypeList) {
                builder.addAnnotation(annotationMetadata);
            }

            // If is not a enumeration type
            if (!governorTypeDetails.getPhysicalTypeCategory().equals(
                    PhysicalTypeCategory.ENUMERATION)) {

                // Add XmlElement annotation for each field
                List<DeclaredFieldAnnotationDetails> declaredFieldXmlElementFieldList = getXmlElementFieldAnnotations(fields);
                for (DeclaredFieldAnnotationDetails declaredFieldAnnotationDetails : declaredFieldXmlElementFieldList) {
                    builder.addFieldAnnotation(declaredFieldAnnotationDetails);
                }

                // Avoid if abstract class or interface (can't add method)
                if (!Modifier.isAbstract(governorTypeDetails.getModifier())
                        && !Modifier.isInterface(governorTypeDetails
                                .getModifier())) {

                    // Add annotation and method to avoid cycles convert to XML
                    addCycleDetection(id);
                }
            }
        }

        // Build the aspect Java defined into builder
        itdTypeDetails = builder.build();
    }

    /**
     * Add annotation and method to avoid cycles converting to XML.
     * 
     * @param id
     *            Declared by metadata id
     */
    protected void addCycleDetection(String id) {

        // Implements class and create method to avoid XML cycles
        builder.addImplementsType(new JavaType(
                "com.sun.xml.bind.CycleRecoverable"));

        // Create method executed when cycle detected
        JavaSymbolName methodName = new JavaSymbolName("onCycleDetected");
        JavaType returnType = new JavaType(Object.class.getName());
        List<AnnotatedJavaType> paramTypes = new ArrayList<AnnotatedJavaType>(1);
        paramTypes.add(new AnnotatedJavaType(new JavaType(
                "com.sun.xml.bind.CycleRecoverable.Context"), null));
        List<JavaSymbolName> paramNames = new ArrayList<JavaSymbolName>(1);
        paramNames.add(new JavaSymbolName("context"));
        InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
        bodyBuilder.appendFormalLine("return new "
                + governorTypeDetails.getName() + " ();");

        builder.addMethod(new MethodMetadataBuilder(id, Modifier.PUBLIC,
                methodName, returnType, paramTypes, paramNames, bodyBuilder));
    }

    /**
     * Create XmlElement annotation for each field.
     * 
     * <p>
     * Field XmlElement annotation has same attributes as GvNIXXmlElementField
     * or only name attribute with field name if GvNIXXmlElementField not
     * exists.
     * </p>
     * 
     * @param fields
     *            Fields to be exported
     * @return All the annotated @XmlElement fields (may be empty)
     */
    public List<DeclaredFieldAnnotationDetails> getXmlElementFieldAnnotations(
            List<FieldMetadata> fields) {

        // Result list of annotations for fields
        List<DeclaredFieldAnnotationDetails> result = new ArrayList<DeclaredFieldAnnotationDetails>();

        for (FieldMetadata field : fields) {

            // Get field annotation of GvNIXXmlElementField type
            AnnotationMetadata annotation = MemberFindingUtils
                    .getAnnotationOfType(field.getAnnotations(), new JavaType(
                            GvNIXXmlElementField.class.getName()));

            List<AnnotationAttributeValue<?>> attrs = new ArrayList<AnnotationAttributeValue<?>>();

            if (annotation != null) {

                // Annotation exists, duplicate all annotation attributes
                for (JavaSymbolName javaSymbolName : annotation
                        .getAttributeNames()) {
                    attrs.add(annotation.getAttribute(javaSymbolName));
                }

            } else {

                // Annotation not exists, create name attr with field name
                attrs.add(new StringAttributeValue(new JavaSymbolName("name"),
                        field.getFieldName().getSymbolName()));
            }

            // Create XmlElement annotation for field with attributes
            result.add(new DeclaredFieldAnnotationDetails(
                    new FieldMetadataBuilder(governorPhysicalTypeMetadata
                            .getId(), field).build(),
                    new AnnotationMetadataBuilder(new JavaType(
                            "javax.xml.bind.annotation.XmlElement"), attrs)
                            .build()));
        }

        return result;
    }

    /**
     * Class annotations: XmlRoot, XmlType and XmlEnum or XmlAccessorType.
     * 
     * <ul>
     * <li>XmlRoot: with name and namespace if annotation has name</li>
     * <li>XmlType: with name, propOrder and namespace</li>
     * <li>XmlEnum: without attributes</li>
     * <li>XmlAccessorType: with always field access type.</li>
     * </ul>
     * 
     * @param annotation
     *            Original gvNIX xml element annotation
     * @param fields
     *            Declared fields to be exported
     * @return {@link List} of {@link AnnotationMetadata} to build the ITD
     */
    protected List<AnnotationMetadata> getAnnotations(
            AnnotationMetadata annotation, List<FieldMetadata> fields) {

        // Generated result annotations list
        List<AnnotationMetadata> result = new ArrayList<AnnotationMetadata>();

        AnnotationMetadata xmlRootAnnotation = getXmlRootAnnotation(annotation);
        if (xmlRootAnnotation != null) {
            result.add(getXmlRootAnnotation(annotation));
        }

        // Add XmlType with name, propOrder and namespace for each field
        result.add(getXmlTypeAnnotation(annotation, fields));

        if (governorTypeDetails.getPhysicalTypeCategory().equals(
                PhysicalTypeCategory.ENUMERATION)) {

            // Is an enumeration: add XmlEnum annotation without attributes.
            result.add(getXmlEnumAnnotation());

        } else {

            // Is not an enumeration: add XmlAccessorType with field access type
            result.add(getXmlAccesorType());
        }

        return result;
    }

    /**
     * Get XmlRootElement with name and namespace if annotation has name.
     * 
     * @param annotation
     *            Annotation to get name and namespace
     * @return XmlRootElement annotation
     */
    protected AnnotationMetadata getXmlRootAnnotation(
            AnnotationMetadata annotation) {

        // Get gvNIX xml element annotation name
        AnnotationAttributeValue<?> nameAttr = annotation
                .getAttribute(new JavaSymbolName("name"));

        // If name exists, create XmlRootElement annotation
        if (nameAttr != null) {

            // @XmlRootElement with name and namespace from gvNIX annotation
            List<AnnotationAttributeValue<?>> xmlRootElementAttrs = new ArrayList<AnnotationAttributeValue<?>>();
            xmlRootElementAttrs.add(nameAttr);
            xmlRootElementAttrs.add(annotation.getAttribute(new JavaSymbolName(
                    "namespace")));
            return new AnnotationMetadataBuilder(new JavaType(
                    "javax.xml.bind.annotation.XmlRootElement"),
                    xmlRootElementAttrs).build();
        }

        return null;
    }

    /**
     * Get XmlType with name, propOrder and namespace.
     * 
     * <ul>
     * <li>name from annotation if annotation has xmlTypeName, else blank</li>
     * <li>propOrder from fields names</li>
     * <li>namespace from annotation</li>
     * </ul>
     * 
     * @param annotation
     * @param fields
     * @return
     */
    protected AnnotationMetadata getXmlTypeAnnotation(
            AnnotationMetadata annotation, List<FieldMetadata> fields) {

        List<AnnotationAttributeValue<?>> xmlTypeAttrs = new ArrayList<AnnotationAttributeValue<?>>();
        if (annotation.getAttribute(new JavaSymbolName("xmlTypeName")) != null) {

            // @XmlType name from gvNIX annotation if xml type name exists
            xmlTypeAttrs.add(new StringAttributeValue(
                    new JavaSymbolName("name"),
                    ((StringAttributeValue) annotation
                            .getAttribute(new JavaSymbolName("name")))
                            .getValue()));
        } else {

            // @XmlType without name if xml type name not exists
            xmlTypeAttrs.add(new StringAttributeValue(
                    new JavaSymbolName("name"), ""));
        }

        // @XmlType prop order from declared fields
        List<StringAttributeValue> propOrderList = new ArrayList<StringAttributeValue>();
        for (FieldMetadata field : fields) {
            if (field != null) {
                propOrderList.add(new StringAttributeValue(new JavaSymbolName(
                        "ignored"), field.getFieldName()
                        .getSymbolName()));
            }
        }
        xmlTypeAttrs.add(new ArrayAttributeValue<StringAttributeValue>(
                new JavaSymbolName("propOrder"), propOrderList));

        // @XmlType prop order from gvNIX annotation
        xmlTypeAttrs.add(annotation
                .getAttribute(new JavaSymbolName("namespace")));

        // @XmlType annotation
        return new AnnotationMetadataBuilder(new JavaType(
                "javax.xml.bind.annotation.XmlType"), xmlTypeAttrs).build();
    }

    /**
     * Get XmlEnum annotation without attributes.
     * 
     * @return XmlEnum annotation
     */
    protected AnnotationMetadata getXmlEnumAnnotation() {

        // @XmlEnum
        return new AnnotationMetadataBuilder(new JavaType(
                "javax.xml.bind.annotation.XmlEnum"),
                new ArrayList<AnnotationAttributeValue<?>>()).build();
    }

    /**
     * Get XmlAccessorType with always field access type.
     * 
     * @return XmlAccessorType annotation
     */
    protected AnnotationMetadata getXmlAccesorType() {

        // @XmlAccessorType always is field access type
        List<AnnotationAttributeValue<?>> xmlAccessorTypeAttrs = new ArrayList<AnnotationAttributeValue<?>>();
        xmlAccessorTypeAttrs.add(new EnumAttributeValue(new JavaSymbolName(
                "value"), new EnumDetails(new JavaType(
                "javax.xml.bind.annotation.XmlAccessType"), new JavaSymbolName(
                "FIELD"))));

        // @XmlAccessorType
        return new AnnotationMetadataBuilder(new JavaType(
                "javax.xml.bind.annotation.XmlAccessorType"),
                xmlAccessorTypeAttrs).build();
    }

    public static String getMetadataIdentiferType() {

        // Get metadata identifier for this annotation
        return XML_ELEMENT_TYPE;
    }

    public static boolean isValid(String metadataIdentificationString) {

        // Is a valid gvNIX xml element physical type idenfifier ?
        return PhysicalTypeIdentifierNamingUtils.isValid(XML_ELEMENT_STRING,
                metadataIdentificationString);
    }

    public static final JavaType getJavaType(String metadataIdentificationString) {

        // Get java type related to gvNIX xml element physical type idenfifier
        return PhysicalTypeIdentifierNamingUtils.getJavaType(
                XML_ELEMENT_STRING, metadataIdentificationString);
    }

    public static final Path getPath(String metadataIdentificationString) {

        // Get path related to gvNIX xml element physical type idenfifier
        return PhysicalTypeIdentifierNamingUtils.getPath(XML_ELEMENT_STRING,
                metadataIdentificationString);
    }

    public static final String createIdentifier(JavaType javaType, Path path) {

        // Get gvNIX xml element physical type idenfifier for java type in path
        return PhysicalTypeIdentifierNamingUtils.createIdentifier(
                XML_ELEMENT_STRING, javaType, path);
    }

}
