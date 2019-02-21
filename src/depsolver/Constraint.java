package depsolver;

public class Constraint {

    private String packageName;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageVersionNumber() {
        return packageVersionNumber;
    }

    public void setPackageVersionNumber(String packageVersionNumber) {
        this.packageVersionNumber = packageVersionNumber;
    }

    private String packageVersionNumber;

    public Constraint()
    {
    }
}
