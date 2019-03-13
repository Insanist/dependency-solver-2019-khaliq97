package depsolver;

import java.util.*;

public class Commands {

    private List<FinalStatePackage> finalStates;
    private HashMap<String, FinalStatePackage> initial;

    private List<String> commandList;

    private List<String> constraints;

    public Commands( HashMap<String, FinalStatePackage> initial, List<FinalStatePackage> finalStates, List<String> constraints)
    {
        this.initial = initial;
        this.finalStates = finalStates;
        this.constraints = constraints;


        commandList = new ArrayList<>();




    }

    public List<String> createCommandsList()
    {
        for(FinalStatePackage finalStatePackage: finalStates)
        {
           // if(initial.containsKey(createInstallCommand(finalStatePackage)))
            //{
                boolean isRootMissing = checkIfDepsInstalledThenInstall(finalStatePackage);

                if(!isRootMissing)
                {
                    initial.put(finalStatePackage.getPackageName() + "=" + finalStatePackage.getPackageVersionNumber(), finalStatePackage);
                    commandList.add(createInstallCommand(finalStatePackage));
                }
            //}
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

        constraints.forEach(c ->{
            initial.forEach((k, v) -> {

            });
        });

        return commandList;

    }

    public boolean checkIfDepsInstalledThenInstall(FinalStatePackage finalStatePackage)
    {
        for(HashSet<FinalStatePackage> f: finalStatePackage.getDeps())
        {
            for(FinalStatePackage fsp: f)
            {
                if(!initial.containsKey(fsp.getPackageName() + "=" + fsp.getPackageVersionNumber()))
                {
                    uninstallConflicts(fsp);
                    initial.put(fsp.getPackageName() + "=" + fsp.getPackageVersionNumber(), fsp);
                    commandList.add(createInstallCommand(fsp));

                }
            }

        }

        if(initial.containsValue(finalStatePackage))
        {
            return true;
        }else
        {
            return false;
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
        fsp.getCons().forEach(conflict -> {
            String comp = Resolver.getPackageComparator(conflict);
            if(comp.equals(""))
            {
                initial.forEach((k, v) -> {
                    if(v.getPackageName().equals(conflict))
                    {
                        uninstallDeps(v);
                    }
                });
            }else
            {
                String[] compSplit = conflict.split(comp);
                initial.forEach((k, v) -> {

                    if(v.getPackageName().equals(compSplit[0]) &&  Main.haveCorrectVersion(compSplit[1], v.getPackageVersionNumber(), comp))
                    {
                        uninstallDeps(v);
                    }
                });
            }
        });
    }

    public void uninstall(FinalStatePackage fsp, String conflict)

    {

    }

    public void uninstallDeps(FinalStatePackage fsp)
    {
        if(fsp.getDependents().isEmpty())
        {
            commandList.add(createUninstallCommand(fsp));
        }else
        {
            for(FinalStatePackage finalStatePackage: fsp.getDependents())
            {
                if(checkForOnlyDep(finalStatePackage, fsp))
                {
                    uninstallDeps(fsp);

                }

                commandList.add(createUninstallCommand(fsp));

            }
        }
    }


    public String createInstallCommand(FinalStatePackage p)
    {
        return "+" + p.getPackageName() + "=" + p.getPackageVersionNumber();

    }

    public String createUninstallCommand(FinalStatePackage p)
    {
        return "-" + p.getPackageName() + "=" + p.getPackageVersionNumber();

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
