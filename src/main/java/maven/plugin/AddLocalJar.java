package maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;
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

        getLog().info("files  name is> " + files);

        files.forEach(this::runCommandOnFile);

        List<String> newNames = mapFileName(files, fileName -> getBasename(fileName) + "-1.0.jar");
        List<String> newFilePre = mapFileName(files, this::getBasename);

        useMvnCommand(newNames, newFilePre); // 说实话我还是不知道这两个参数啥意思，建议起个更有意义的名字
        printDependency(newFilePre);
    }

    private List<String> mapFileName(List<String> originalFileNames, UnaryOperator<String> function) {
        return originalFileNames.stream().map(function).collect(Collectors.toList());
    }

    private void runCommandOnFile(String file) {
        run(Arrays.asList(
                "cmd.exe", // 我不太明白为啥要cmd /c，直接rename不好么？我猜你直接fork子进程的时候碰到了问题没有解决。
                "/c",
                String.format("rename \"%s\" \"%s\"", file, getBasename(file) + "-1.0.jar")
        ));
    }

    private void printDependency(List<String> newNamesPre) {
        newNamesPre.stream().map(this::createDependency).forEach(getLog()::info);
    }

    private String createDependency(String name) {
        return " <dependency>  \n" +
                "      <groupId>" + name + "</groupId>  \n" +
                "      <artifactId>" + name + "</artifactId>  \n" +
                "      <version>1.0</version>  \n" +
                "    </dependency>  ";
    }

    private void useMvnCommand(List<String> newNames, List<String> newNamesPre) {
        for (int i = 0; i < newNames.size(); i++) {
            String cmd = String.format("mvn install:install-file -Dfile=%s -DgroupId=%s -DartifactId=%s -Dversion=1.0 -Dpackaging=jar",
                    newNames.get(i), newNamesPre.get(i), newNamesPre.get(i));
            getLog().info(cmd);

            run(Arrays.asList("cmd.exe", "/c", cmd));
        }
    }

    private void run(List<String> commands) {
        try {
            ProcessBuilder pb = new ProcessBuilder(commands);
            pb.directory(new File(path));
            Process process = pb.start();
            process.waitFor();
            InputStream in = process.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line = br.readLine();
            while (line != null) {
                System.out.println(line);
                line = br.readLine();
            }
        } catch (Exception e) {
            getLog().error(e);
        }
    }

    private List<String> getFiles() {
        File[] tempList = new File(path).listFiles();
        assert tempList != null;

        return Stream.of(tempList).filter(File::isFile).map(File::getName).collect(Collectors.toList());
    }

    private String getBasename(String fileName) {
        return fileName.split("\\.")[0];
    }
}
