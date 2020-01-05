package maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * add local jar in you dependencies.
 */
@Mojo(name = "wzn-plugin", defaultPhase = LifecyclePhase.PACKAGE)
public class AddLocalJar extends AbstractMojo {
    @Parameter
    private String path;

    @Override
    public void execute() {
        List<String> files = getFiles();
        System.out.println("files  name is> " + files);
        List<List<String>> newNamesResult = null;
        try {
            newNamesResult = processBuilderCommand(files);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        try {
            assert newNamesResult != null;
            useMvnCommand(newNamesResult.get(0), newNamesResult.get(1));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        printDependency(newNamesResult.get(1));
    }

    private void printDependency(List<String> newNamesPre) {
        for (String newNamePre : newNamesPre) {

            System.out.println(" <dependency>  \n" +
                    "      <groupId>" + newNamePre + "</groupId>  \n" +
                    "      <artifactId>" + newNamePre + "</artifactId>  \n" +
                    "      <version>1.0</version>  \n" +
                    "    </dependency>  ");
        }
    }

    private void useMvnCommand(List<String> newNames, List<String> newNamesPre) throws IOException, InterruptedException {
        for (int i = 0; i < newNames.size(); i++) {
            List<String> commands = new ArrayList<>();
            commands.add("cmd.exe");
            commands.add("/c");
            System.out.println("mvn install:install-file -Dfile=" + newNames.get(i) + " -DgroupId=" + newNamesPre.get(i) +
                    " -DartifactId=" + newNamesPre.get(i) + " -Dversion=1.0 -Dpackaging=jar");
            commands.add("mvn install:install-file -Dfile=" + newNames.get(i) + " -DgroupId=" + newNamesPre.get(i) +
                    " -DartifactId=" + newNamesPre.get(i) + " -Dversion=1.0 -Dpackaging=jar");
            ProcessBuilder pb = new ProcessBuilder(commands);
            pb.directory(new File(path));
            Process process = pb.start();
            int status = process.waitFor();
            InputStream in = process.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line = br.readLine();
            while (line != null) {
                System.out.println(line);
                line = br.readLine();
            }
        }
    }


    private List<String> getFiles() {
        File[] tempList = new File(path).listFiles();
        assert tempList != null;

        return Stream.of(tempList).filter(File::isFile).map(File::getName).collect(Collectors.toList());
    }


    private List<List<String>> processBuilderCommand(List<String> files) throws IOException, InterruptedException {
        List<String> newNames = new ArrayList<>();
        List<String> newFilePre = new ArrayList<>();
        List<List<String>> result = new ArrayList<>();
        for (String file : files) {
            List<String> commands = new ArrayList<>();
            commands.add("cmd.exe");
            commands.add("/c");
            String[] fileName = file.split("\\.");
            String newFileName = fileName[0] + "-1.0.jar";
            newFilePre.add(fileName[0]);
            commands.add("rename " + "\"" + file + "\"" + " " + "\"" + newFileName + "\"");
            newNames.add(newFileName);
            ProcessBuilder pb = new ProcessBuilder(commands);
            pb.directory(new File(path));
            Process process = pb.start();
            int status = process.waitFor();
            InputStream in = process.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line = br.readLine();
            while (line != null) {
                System.out.println(line);
                line = br.readLine();
            }
        }
        result.add(newNames);
        result.add(newFilePre);
        return result;
    }
}
