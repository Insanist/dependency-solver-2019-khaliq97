package depsolver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;


public class Main {



  public static void main(String[] args) throws IOException {
    TypeReference<List<Package>> repoType = new TypeReference<List<Package>>() {};
    List<Package> repo = JSON.parseObject(readFile(args[0]), repoType);
    TypeReference<List<String>> strListType = new TypeReference<List<String>>() {};
    List<String> initial = JSON.parseObject(readFile(args[1]), strListType);
    List<String> constraints = JSON.parseObject(readFile(args[2]), strListType);

    // CHANGE CODE BELOW:
    // using repo, initial and constraints, compute a solution and print the answer
    for (Package p : repo) {
      System.out.printf("package %s version %s\n", p.getName(), p.getVersion());
      for (List<String> clause : p.getDepends()) {
        System.out.printf("  dep:");
        for (String q : clause) {
          System.out.printf(" %s", q);
        }
        System.out.printf("\n");
      }
    }

    Resolver resolver = new Resolver(constraints, repo);




  }

  static String readFile(String filename) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(filename));
    StringBuilder sb = new StringBuilder();
    br.lines().forEach(line -> sb.append(line));
    return sb.toString();
  }

  static HashMap<FinalStatePackage, Boolean> getConstraintsMap(List<String> constraints)
  {
    HashMap<FinalStatePackage, Boolean> returnConstraints = new HashMap<>();
    for(String constraint: constraints)
    {
      FinalStatePackage newCons = new FinalStatePackage(null, null);
      Boolean state = false;
      String packageName = "";
      String packageVersion = "";
      for(int i =0; i < constraint.length(); i++)
      {
        char currentChar = constraint.charAt(i);

        switch (currentChar)
        {
          case '+':
            state = true;
            break;

          case '-':
            state = false;
            break;

          case '=':
            packageVersion = packageVersion +  constraint.charAt(i + 1);

            //Break out of for loop
            i = constraint.length();

            break;

          default:
            packageName = packageName + currentChar;
            break;
        }
      }

      newCons.setPackageVersionNumber(packageVersion);
      newCons.setPackageName(packageName);
      returnConstraints.put(newCons, state);
    }

    return returnConstraints;
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
