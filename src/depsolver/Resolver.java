package depsolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.sat4j.pb.tools.WeightedObject;

import com.microsoft.z3.*;


public class Resolver {


    private List<String> constraints;
    private List<Package> repo;

    private Context context;
    private Solver solver;

    BoolExpr finalExpr;


    public Resolver(List<String> constraintsFile, List<Package> packagesFile)
    {

        finalExpr
        constraints = constraintsFile;
        repo = packagesFile;
        context = new Context();
        solver = context.mkSolver();


        resolve();

    }


    public void resolve()
    {
        for(String con: constraints)
        {

            List<Package> allCurrentPackages = new ArrayList<>();
            //Only for INCLUDE CASE

            BoolExpr implies = null;
            for(Package p: repo)
            {
                System.out.println("-------------------" + p.getName() + "-------------------");
                List<BoolExpr> deps= getAllDependencyConstraints(p.getDepends());
                BoolExpr conflicts = getAllConflictConstraints(p.getConflicts());

                deps.add(conflicts);

                BoolExpr depsAndCons = context.mkAnd(deps.toArray(new BoolExpr[deps.size()]));

                BoolExpr packageBoolConst = context.mkBoolConst(p.getName() + "=" + p.getVersion());

                implies = context.mkImplies(packageBoolConst, context.mkAnd(depsAndCons));

                System.out.println(implies.toString());

            }
        }




      /*  BoolExpr constraintBoolExpr = getConstraintsBooleanExpression(constraints);

        BoolExpr finalCNF = context.mkAnd(implies, constraintBoolExpr);

        System.out.println("FINAL CNF: ");
        System.out.println(finalCNF);*/



    }

    public BoolExpr getConstraintsBooleanExpression(List<String> constraints)
    {
        ArrayList<Package> allPackagesToBeInstalled = new ArrayList<>();


        for(String cons: constraints)
        {
            String[] splitOperation = cons.split("\\+");


            allPackagesToBeInstalled.addAll(getAllPackageVersions(splitOperation[1]));
        }

        ArrayList<BoolExpr> constraintBoolExprs = new ArrayList<>();
        BoolExpr b = null;
        for(Package p: allPackagesToBeInstalled)
        {
            b = context.mkBoolConst(p.getName() + "=" + p.getVersion());
            constraintBoolExprs.add(context.mkAnd(b));
        }


        BoolExpr implies = context.mkImplies(b, context.mkAnd(constraintBoolExprs.toArray(new BoolExpr[constraintBoolExprs.size()])));

        //System.out.println(implies.toString());

        return context.mkImplies(b, context.mkAnd(constraintBoolExprs.toArray(new BoolExpr[constraintBoolExprs.size()])));
    }

    public BoolExpr getAllConflictConstraints(List<String> conflictsList)
    {

        ArrayList<BoolExpr> conflicts = new ArrayList<>();

        ArrayList<Package> conflictPackages = new ArrayList<>();

        if(!(conflictsList.isEmpty()))
        {
            for(String c: conflictsList)
            {
                conflictPackages.addAll(getAllPackageVersions(c));
            }


            for(Package p: conflictPackages)
            {
                BoolExpr b = context.mkBoolConst(p.getName() + "=" + p.getVersion());
                conflicts.add(context.mkNot(b));
            }
        }


        //System.out.println(context.mkAnd(conflicts.toArray(new BoolExpr[conflicts.size()])).toString());
        return  context.mkAnd(conflicts.toArray(new BoolExpr[conflicts.size()]));
    }

    //List: Optional packages <List<String>> Must be installed
    public List<BoolExpr> getAllDependencyConstraints(List<List<String>> packages)
    {
        ArrayList<BoolExpr> constraints = new ArrayList<>();


        ArrayList<BoolExpr> ors = null;
        int index = 0;



        for(List<String> listS: packages)
        {

            ors = new ArrayList<>();

            for(String s: listS)
            {
                ors.add(getConstraintOrs(getAllPackageVersions(s)));
            }


            constraints.add(context.mkOr(ors.toArray(new BoolExpr[ors.size()])));

        }

       // System.out.println(constraints.toString());
        return constraints;
    }

    public BoolExpr getConstraintOrs(List<Package> packages)
    {
        BoolExpr[] dps = new BoolExpr[packages.size()];


        for(int i = 0; i < dps.length; i++)
        {
            dps[i] = context.mkBoolConst(packages.get(i).getName() + "=" + packages.get(i).getVersion());
        }

        //System.out.println(context.mkOr(dps).toString());
        return context.mkOr(dps);
    }

    public List<Package> getAllPackageVersions(String _package)
    {

        String packageComparator = "";
        if(_package.contains(">="))
        {
            packageComparator = ">=";
        }else if(_package.contains("<="))
        {
            packageComparator = "<=";
        }else if (_package.contains("="))
        {
            packageComparator = "=";
        }else if(_package.contains(">"))
        {
            packageComparator = ">";
        }else if(_package.contains("<"))
        {
            packageComparator = "<";
        }



        ArrayList<Package> packages = new ArrayList<>();


        if(packageComparator.isEmpty())
        {
            for(Package p: repo)
            {
                if(_package.equals(p.getName()))
                {
                    packages.add(p);
                }
            }
        }else
        {
            String[] versionList = _package.split(packageComparator);

            for(Package p: repo)
            {
                if(versionList[0].equals(p.getName()))
                {
                    if(Main.haveCorrectVersion(versionList[1], p.getVersion(), packageComparator)) {
                        packages.add(p);
                    }
                }
            }
        }

        return packages;
    }



}
