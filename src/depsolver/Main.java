package depsolver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    for(Map.Entry<Constraint, Boolean> entry: getConstraintsMap(constraints).entrySet())
    {
      System.out.println(entry.getKey().getPackageName() + " : " + entry.getValue() + " Version = " + entry.getKey().getPackageVersionNumber());
    }
  }

  static String readFile(String filename) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(filename));
    StringBuilder sb = new StringBuilder();
    br.lines().forEach(line -> sb.append(line));
    return sb.toString();
  }

  static HashMap<Constraint, Boolean> getConstraintsMap(List<String> constraints)
  {
    HashMap<Constraint, Boolean> returnConstraints = new HashMap<>();
    for(String constraint: constraints)
    {
      Constraint newCons = new Constraint();
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
}
