package org.gvnix.weblayer.roo.addon.metadata;

import org.springframework.roo.addon.entity.EntityMetadata;
import org.springframework.roo.classpath.PhysicalTypeIdentifierNamingUtils;
import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.details.ItdTypeDetailsBuilder;
import org.springframework.roo.classpath.itd.AbstractItdTypeDetailsProvidingMetadataItem;
import org.springframework.roo.metadata.MetadataIdentificationUtils;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.project.Path;
import org.springframework.roo.support.style.ToStringCreator;
import org.springframework.roo.support.util.Assert;

/**
 * This type produces metadata for a new ITD. It uses an
 * {@link ItdTypeDetailsBuilder} provided by
 * {@link AbstractItdTypeDetailsProvidingMetadataItem} to register a field in
 * the ITD and a new method.
 * 
 * @author Oscar Rovira (orovira at disid dot com) at <a
 *         href="http://www.disid.com">DiSiD Technologies S.L.</a> made for <a
 *         href="http://www.cit.gva.es">Conselleria d'Infraestructures i
 *         Transport</a>
 * @since 0.9
 */
public class WebLayerShowMetadata extends
    AbstractItdTypeDetailsProvidingMetadataItem {

  private static final String PROVIDES_TYPE_STRING = WebLayerShowMetadata.class
      .getName();

  private static final String PROVIDES_TYPE = MetadataIdentificationUtils
      .create(PROVIDES_TYPE_STRING);

  public WebLayerShowMetadata(String identifier, JavaType aspectName,
      PhysicalTypeMetadata governorPhysicalTypeMetadata,
      EntityMetadata entityMetadata) {
    super(identifier, aspectName, governorPhysicalTypeMetadata);
    Assert.isTrue(isValid(identifier), "Metadata identification string '"
        + identifier + "' does not appear to be a valid");

    // XXX De momento este metadata no hace nada ya que no se genera ITD para la
    // entidad, no es necesario. Pero puede usarse para guardar información
    // interesante sobre el metadato de forma que se pueda usar esta información
    // en lugar de tener que volver a inspeccionar clases
  }

  // Typically, no changes are required beyond this point

  @Override
  public String toString() {
    ToStringCreator tsc = new ToStringCreator(this);
    tsc.append("identifier", getId());
    tsc.append("valid", valid);
    tsc.append("aspectName", aspectName);
    tsc.append("destinationType", destination);
    tsc.append("governor", governorPhysicalTypeMetadata.getId());
    tsc.append("itdTypeDetails", itdTypeDetails);
    return tsc.toString();
  }

  public static final String getMetadataIdentiferType() {
    return PROVIDES_TYPE;
  }

  public static final String createIdentifier(JavaType javaType, Path path) {
    return PhysicalTypeIdentifierNamingUtils.createIdentifier(
        PROVIDES_TYPE_STRING, javaType, path);
  }

  public static final JavaType getJavaType(String metadataIdentificationString) {
    return PhysicalTypeIdentifierNamingUtils.getJavaType(PROVIDES_TYPE_STRING,
        metadataIdentificationString);
  }

  public static final Path getPath(String metadataIdentificationString) {
    return PhysicalTypeIdentifierNamingUtils.getPath(PROVIDES_TYPE_STRING,
        metadataIdentificationString);
  }

  public static boolean isValid(String metadataIdentificationString) {
    return PhysicalTypeIdentifierNamingUtils.isValid(PROVIDES_TYPE_STRING,
        metadataIdentificationString);
  }
}