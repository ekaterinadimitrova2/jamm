package org.github.jamm.testedclasses;

import org.github.jamm.MemoryMeter;

public class PublicClassWithPackageProtectedClassFields
{
    public static String publicStaticField = "publicStaticField";

    private static String privateStaticField = "privateStaticField";

    public String publicField;

    String packageProtectedField;

    protected String protectedField;

    private String privateField;

    private PackageProtectedClass packageProtectedClass; 

    public PublicClassWithPackageProtectedClassFields(String publicField,
                                                      String packageProtectedField,
                                                      String protectedField,
                                                      String privateField) {

        this.publicField = publicField;
        this.packageProtectedField = packageProtectedField;
        this.protectedField = protectedField;
        this.privateField = privateField;
        this.packageProtectedClass = new PackageProtectedClass("packageProtectedClassPublicField",
                                                               "packageProtectedClassPackageProtectedField", 
                                                               "packageProtectedClassProtectedField",
                                                               "packageProtectedClassPrivateField");
    }

    public long measure(MemoryMeter meter) {
        return meter.measure(this) 
                + meter.measureDeep(publicField)
                + meter.measureDeep(packageProtectedField)
                + meter.measureDeep(protectedField)
                + meter.measureDeep(privateField) 
                + packageProtectedClass.measure(meter);
    }
}
