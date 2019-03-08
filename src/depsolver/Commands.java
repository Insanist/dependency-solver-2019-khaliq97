package depsolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Commands {

    private List<Package> repo;
    private List<FinalStatePackage> finalStates;
    private HashMap<String, FinalStatePackage> initial;

    private HashSet<String> commandList;

    public Commands(List<FinalStatePackage> finalState, HashMap<String, FinalStatePackage> initial)
    {
        this.repo = repo;
        this.finalStates = finalState;
        this.initial = initial;
        commandList = new HashSet<>();

    }

    public void createCommandsList()
    {

        for(FinalStatePackage finalStatePackage: finalStates)
        {
            if(initial.containsKey(createInstallCommand(finalStatePackage)))
            {
                checkIfDepsInstalled(finalStatePackage);
            }
        }

      /*  for(FinalStatePackage finalStatePackage: finalStates)
        {
            finalStatePackage.setDeps(finalStates);
            for(FinalStatePackage f: finalStatePackage.getDeps())
            {
                commandList = commandList + createInstallCommand(f);
                System.out.println(f.getPackageName());
            }*/
          /*  finalStatePackage.setDeps(finalStates);
            String temp = "\t Package Name: " + finalStatePackage.getPackageName() + " Version: " + finalStatePackage.getPackageVersionNumber() + "\n";
            System.out.println(finalStatePackage.toString());*/
        //}

    }

    public void checkIfDepsInstalled(FinalStatePackage finalStatePackage)
    {
        for(HashSet<FinalStatePackage> f: finalStatePackage.getDeps())
        {
            for(FinalStatePackage fsp: f)
            {
                if(initial.containsKey(fsp.getPackageName() + "=" + fsp.getPackageVersionNumber()))
                {
                    
                }
            }

        }
    }

    public boolean checkForOnlyDep(FinalStatePackage p, FinalStatePackage dep)
    {
        for(HashSet<FinalStatePackage> h: p.getDeps()) {

            if(h.contains(dep) && h.size() > 1)
            {
                return true;
            }
        }

        return false;
    }

    public void uninstallConflicts(FinalStatePackage fsp)
    {

    }

    public void uninstallDeps(FinalStatePackage fsp)
    {
        if(fsp.getDependents().size() < 0)
        {
            commandList.add(createUninstallCommand(fsp));
            initial.remove(fsp.getPackageName() + "=" + fsp.getPackageVersionNumber());
        }else
        {
            for(FinalStatePackage finalStatePackage: fsp.getDependents())
            {
                if(checkForOnlyDep(finalStatePackage, fsp))
                {
                    uninstallDeps(fsp);
                }

                commandList.add(createUninstallCommand(fsp));
                initial.remove(fsp.getPackageName() + "=" + fsp.getPackageVersionNumber());
            }
        }
    }

    public String fixConflicts(FinalStatePackage p)
    {




        return "";
    }



    public String createInstallCommand(FinalStatePackage p)
    {
        return "+" + p.getPackageName() + "=" + p.getPackageVersionNumber();

    }

    public String createUninstallCommand(FinalStatePackage p)
    {
        return "+" + p.getPackageVersionNumber() + "=" + p.getPackageVersionNumber();

    }

/*
    String conflictCommands = "";
        for(String conflict: p.getConflicts())
    {
        String comp = Resolver.getPackageComparator(conflict);
        for(String installed: initial)
        {
            String removeOp = installed.replace("+", "");
            String[] removedOpSplitList = removeOp.split("=");

            if(comp.isEmpty() && conflict.equals(removedOpSplitList[0]))
            {
                conflictCommands = createUninstallCommand(p) + "\n";
                conflictCommands = fixConflicts(Main.getHashMapRepo().get(removedOpSplitList[0] + "=" + removedOpSplitList[1])) + conflictCommands;

            }else
            {
                String[] conflictT = conflict.split(comp);
                if(conflictT[0].equals(removedOpSplitList[0]) && Main.haveCorrectVersion(conflictT[1], removedOpSplitList[1], comp))
                {
                    conflictCommands = createUninstallCommand(p) + "\n";
                    conflictCommands = fixConflicts(Main.getHashMapRepo().get(removedOpSplitList[0] + "=" + removedOpSplitList[1])) + conflictCommands;
                }
            }
        }
    }

        return conflictCommands;*/



}
