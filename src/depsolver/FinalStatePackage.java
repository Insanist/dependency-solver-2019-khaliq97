package depsolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class FinalStatePackage {

    private String packageName;

    private String packageVersionNumber;


    public List<HashSet<FinalStatePackage>> getDeps() {
        return deps;
    }

    private List<HashSet<FinalStatePackage>> deps;
    private HashSet<FinalStatePackage> dependents;
    private List<String> cons;

    public FinalStatePackage (String packageName, String packageVersionNumber)
    {
        this.packageName = packageName;
        this.packageVersionNumber = packageVersionNumber;

        deps = new ArrayList<>();
        cons = new ArrayList<>();

        dependents = new HashSet<>();
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

    public HashSet<FinalStatePackage> getDependents()
    {
        return dependents;
    }

    public void setDeps(List<FinalStatePackage> finaleStatePackages)
    {
        Package p = Resolver.getPackage(packageName, packageVersionNumber);

        cons = p.getConflicts();

        for(List<String> dep: p.getDepends())
        {
            FinalStatePackage finalStatePackage = null;

            HashSet<FinalStatePackage> innerDep = new HashSet<>();

            for(String innerDeps: dep)
            {
                String[] innerDepSplit = innerDeps.split(Resolver.getPackageComparator(innerDeps));

               // System.out.println(innerDep);

                for(FinalStatePackage fsp: finaleStatePackages)
                {

                    if(innerDepSplit.length == 1 && fsp.getPackageName().equals(innerDepSplit[0]))
                    {
                        innerDep.add(fsp);
                        fsp.getDependents().add(fsp);
                        break;
                    }else if(fsp.getPackageName().equals(innerDepSplit[0]) && Main.haveCorrectVersion(innerDepSplit[1], fsp.getPackageVersionNumber(), Resolver.getPackageComparator(innerDeps)))
                    {
                        innerDep.add(fsp);
                        fsp.getDependents().add(fsp);
                        break;
                    }
                }

                if(!innerDep.isEmpty())
                {
                    break;
                }


            }

            deps.add(innerDep);

        }
    }


    @Override
    public String toString(){
        String toPrint = "Package Name: " + this.packageName + " Package Version: " + this.packageVersionNumber + " Dependencies: {\n";

        for(HashSet<FinalStatePackage> outer: deps)
        {
            for(FinalStatePackage sat : outer){
                String temp = "\t Package Name: " + sat.getPackageName() + " Version: " + sat.getPackageVersionNumber() + "\n";
                toPrint = toPrint + temp;
            }
        }

        toPrint = toPrint + "}";
        return toPrint;
    }
}
