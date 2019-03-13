package depsolver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Main {

  static List<Package> repo;
  static  List<String> initial;

  public static void main(String[] args) throws IOException {
    TypeReference<List<Package>> repoType = new TypeReference<List<Package>>() {};
    repo = JSON.parseObject(readFile(args[0]), repoType);
    TypeReference<List<String>> strListType = new TypeReference<List<String>>() {};
    initial = JSON.parseObject(readFile(args[1]), strListType);
    List<String> constraints = JSON.parseObject(readFile(args[2]), strListType);

    // CHANGE CODE BELOW:
    // using repo, initial and constraints, compute a solution and print the answer
   /* for (Package p : repo) {
      System.out.printf("package %s version %s\n", p.getName(), p.getVersion());
      for (List<String> clause : p.getDepends()) {
        System.out.printf("  dep:");
        for (String q : clause) {
          System.out.printf(" %s", q);
        }
        System.out.printf("\n");
      }
    }*/

    Resolver resolver = new Resolver(constraints, repo);

    List<String> finalState = resolver.resolve();

    List<FinalStatePackage> finalStatePackages = getFinalStatePackageList(finalState);


    HashMap<String, FinalStatePackage> hashedRepo = getHashMapRepo();

    Commands commands = new Commands(hashedRepo, finalStatePackages, constraints);

   /* finalState.forEach(f -> {
      System.out.println(f);
    });
    finalStatePackages.forEach(f -> {
      System.out.println(f.getPackageName());
    });*/

    JSONArray jsonArray = new JSONArray();
    jsonArray.addAll(commands.createCommandsList());

    System.out.println(jsonArray.toJSONString());




  }

  static String readFile(String filename) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(filename));
    StringBuilder sb = new StringBuilder();
    br.lines().forEach(line -> sb.append(line));
    return sb.toString();
  }


  public static List<FinalStatePackage> getFinalStatePackageList(List<String> finalState)
  {
    List<FinalStatePackage> fsps = new ArrayList<>();

    for(String p: finalState)
    {
      String[] packageInfo = p.split("=");
      fsps.add(new FinalStatePackage(packageInfo[0], packageInfo[1]));
    }

    for(FinalStatePackage fsp: fsps)
    {
      fsp.setDeps(fsps);
    }

    return fsps;
  }

  public static HashMap<String, FinalStatePackage> getHashMapRepo()
  {
    List<FinalStatePackage> fsps = new ArrayList<>();
    HashMap<String, FinalStatePackage> repoHashMap = new HashMap<>();

    for(String initialP: initial)
    {
      String temp = initialP.replace("+", "");
      String[] packageInfo = temp.split("=");
      fsps.add(new FinalStatePackage(packageInfo[0], packageInfo[1]));
    }

    fsps.forEach(f -> {
      f.getPackageName();
    });

    HashMap<String, FinalStatePackage> hashedRepo = new HashMap<>();


      for(FinalStatePackage fsp: fsps)
      {
        fsp.setDeps(fsps);
        hashedRepo.put(fsp.getPackageName() + "=" + fsp.getPackageVersionNumber(), fsp);
      }





    return hashedRepo;
  }

  /**
   * Checks if the currentVersion is the "Operator" of the requiredVersion
   * @param requiredVersion
   * @param currentVersion
   * @param comparatorConstraint
   * @return
   */
  public static boolean haveCorrectVersion(String requiredVersion, String currentVersion, String comparatorConstraint)
  {
    String[] requiredVersionSplit = requiredVersion.split("\\.");
    String[] currentVersionSplit = currentVersion.split("\\.");



    boolean returnState = false;
    for(int i = 0; i < currentVersionSplit.length; i++)
    {

      int e = i < currentVersionSplit.length ? Integer.parseInt(currentVersionSplit[i]): 0;
      int j = i < requiredVersionSplit.length ? Integer.parseInt(requiredVersionSplit[i]): 0;

      switch (comparatorConstraint)
      {
        case "=":
          if (e == j)
          {
            returnState = true;
          }else
          {
            returnState = false;
          }
          break;

        case ">=":
          if(e >= j)
          {
            returnState = true;
          }else
          {
            returnState = false;
          }
          break;

        case "<=":
          if (e <= j)
          {
            returnState = true;
          }else
          {
            returnState = false;
          }
          break;

        case ">":
          if (e > j)
          {
            returnState = true;
          }else
          {
            returnState = false;
          }
          break;

        case "<":
          if (e < j)
          {
            returnState = true;
          }else
          {
            returnState = false;
          }
          break;

        default:
          System.out.println("Unmatched constraint: " + comparatorConstraint);
          returnState = false;


      }
    }



    return returnState;
  }
}
