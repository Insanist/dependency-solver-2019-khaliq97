package depsolver;

import java.util.ArrayList;
import java.util.List;

public class FinalStatePackage {

    private String packageName;

    private String packageVersionNumber;


    public List<FinalStatePackage> getDeps() {
        return deps;
    }

    private List<FinalStatePackage> deps;
    private List<String> cons;

    public FinalStatePackage (String packageName, String packageVersionNumber)
    {
        this.packageName = packageName;
        this.packageVersionNumber = packageVersionNumber;

        deps = new ArrayList<>();
        cons = new ArrayList<>();
    }

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

    public void setDeps(List<FinalStatePackage> finaleStatePackages)
    {
        Package p = Resolver.getPackage(packageName, packageVersionNumber);

        cons = p.getConflicts();

        for(List<String> dep: p.getDepends())
        {
            FinalStatePackage finalStatePackage = null;

            for(String innerDep: dep)
            {
                String[] innerDepSplit = innerDep.split(Resolver.getPackageComparator(innerDep));

               // System.out.println(innerDep);

                for(FinalStatePackage fsp: finaleStatePackages)
                {

                    if(innerDepSplit.length == 1 && fsp.getPackageName().equals(innerDepSplit[0]))
                    {
                        finalStatePackage = fsp;
                        break;
                    }else if(fsp.getPackageName().equals(innerDepSplit[0]) && Main.haveCorrectVersion(innerDepSplit[1], fsp.getPackageVersionNumber(), Resolver.getPackageComparator(innerDep)))
                    {
                        finalStatePackage = fsp;
                        break;
                    }
                }

                if(finalStatePackage != null)
                {
                    break;
                }


            }

            deps.add(finalStatePackage);

        }
    }

    @Override
    public String toString(){
        String toPrint = "Package Name: " + this.packageName + " Package Version: " + this.packageVersionNumber + " Dependencies: {\n";
        for(FinalStatePackage sat : deps){
            String temp = "\t Package Name: " + sat.getPackageName() + " Version: " + sat.getPackageVersionNumber() + "\n";
            toPrint = toPrint + temp;
        }
        toPrint = toPrint + "}";
        return toPrint;
    }
}
