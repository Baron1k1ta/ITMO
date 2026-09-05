package info.kgeorgiy.ja.baronov.implementor;
import info.kgeorgiy.java.advanced.implementor.*;
import info.kgeorgiy.java.advanced.implementor.tools.JarImpler;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.Arrays;
import java.util.Comparator;
import java.util.jar.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

/**
 * <p>
 * The {@code Implementor} class is responsible for generating implementations
 * for Java interfaces or creating JAR files containing those implementations.
 * </p>
 * @author Nikita Baronov
 * @see Impler
 * @see JarImpler
 */


public class Implementor implements JarImpler {
    /**
     * A constant used for separating strings.
     */
    private static final String SPACE = " ";


    /**
     * The main method to launch the program.
     * It accepts command-line arguments to generate the class code for an interface
     * or a JAR file containing the class implementation.
     *
     * @param args Command-line arguments:
     *             - optionally, -jar output JAR file for creating a JAR file.
     *             - class name - name of the interface.
     *             - output directory - directory to save the generated code.
     */
    public static void main(String[] args) {
        boolean isJar = false;
        int index = 0;
        if (args.length < 2) {
            System.err.println("Usage: java Implementor <class name> <output directory>");
            return;
        } else if (args[0].equals("-jar") && args.length == 3) {
            isJar = true;
            index++;
        }
        try {
            Class<?> token = Class.forName(args[index]);
            Path output = Path.of(args[index + 1]);

            Implementor implementor = new Implementor();

            if (isJar) {
                implementor.implementJar(token, output);
            } else {
                implementor.implement(token, output);
            }

        } catch (ImplerException e) {
            System.err.println("Implementor error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found: " + e.getMessage());
        }
    }

    /**
     * Default constructor to create an instance of Implementor.
     */
    public Implementor() {
    }

    /**
     * Generates java class that implements the given interface. Saves file in root directory
     *
     * @param token The interface for which the class needs to be generated.
     * @param root The directory where the generated Java file will be saved.
     * @throws ImplerException If an error occurs during code generation:
     *                         <ul>
     *                             <li>Invalid arguments (e.g., {@code token} or {@code root} are {@code null})</li>
     *                             <li>Error creating directories for the file</li>
     *                             <li>Error creating the file itself</li>
     *                             <li>Error writing content to the file</li>
     *                         </ul>
     */
    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        checkArgs(token, root);
        Path filePath = generatePath(token, root, ".java");
        createDirectories(filePath);
        createFile(filePath);

        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {

            generateHeader(token, writer);

            for (Method method : token.getMethods()) {
                generateMethod(method, writer);
            }

            writer.newLine();
            writer.newLine();
            writer.write("}");
            writer.newLine();

        } catch (IOException e) {
            throw new ImplerException("write error" + e.getMessage());
        }

    }

    /**
     * Generates Jar file that implements the given interface.
     *
     * @param token The interface for which the JAR needs to be created.
     * @param jarFile The path to the JAR file.
     * @throws ImplerException If an error occurs during JAR file generation:
     *                         <ul>
     *                             <li>Invalid arguments (e.g., {@code token} or {@code jarFile} are {@code null})</li>
     *                             <li>Error generating source code</li>
     *                             <li>Compilation error of the generated code</li>
     *                             <li>Error creating the JAR file (unable to open or write to the file)</li>
     *                             <li>Error writing classes to the JAR</li>
     *                             <li>Error deleting tempDirectories that was created to save java file and compiled file</li>
     *                         </ul>
     */
    @Override
    public void implementJar(Class<?> token, Path jarFile) throws ImplerException {
        Path currentWorkingDirectory = Paths.get("").toAbsolutePath();

        implement(token, currentWorkingDirectory);

        Path javaFilePath = generatePath(token, currentWorkingDirectory, ".java");
        compile(javaFilePath, token);

        createJar(token, jarFile, currentWorkingDirectory);

        Path pathToDelete = currentWorkingDirectory
                .resolve(token.getPackageName()
                        .substring(0, token.getPackageName().indexOf('.')));

        clean(pathToDelete);

    }

    /**
     * Checks the input arguments for validity.
     *
     * @param token The interface for which the class needs to be generated.
     * @param root  The directory where the generated code will be saved.
     * @throws ImplerException If the arguments are invalid.
     */
    private void checkArgs(Class<?> token, Path root) throws ImplerException {
        if (token == null || root == null) {
            throw new ImplerException("token or root can't be null");
        } else if (Modifier.isPrivate(token.getModifiers())) {
            throw new ImplerException("interface can't be private");
        } else if (!token.isInterface()) {
            throw new ImplerException("token is not an interface");
        }
    }

    /**
     * Generates the file path for saving the code.
     *
     * @param token     The interface for which the class is being generated.
     * @param root      The directory where the code will be saved.
     * @param extension The file extension.
     * @return The path to the generated file.
     */
    private Path generatePath(Class<?> token, Path root, String extension) {
        return root
                .resolve(token.getPackageName().replace('.', '/'))
                .resolve(token.getSimpleName() + "Impl" + extension);
    }

    /**
     * Creates the directories for saving the file.
     *
     * @param path The path for which directories need to be created.
     * @throws ImplerException If an error occurs during directory creation.
     */
    private void createDirectories(Path path) throws ImplerException {
        if (path.getParent() != null) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                throw new ImplerException("Directory creation error" + e.getMessage());
            }
        }
    }

    /**
     * Creates a file for the generated code.
     *
     * @param path The path where the file will be created.
     * @throws ImplerException If an error occurs during file creation.
     */
    private void createFile(Path path) throws ImplerException {
        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
        } catch (IOException e) {
            throw new ImplerException("File creation error" + e.getMessage());
        }
    }

    /**
     * Generates the header for the interface implementation.
     *
     * @param token  The interface for which the implementation is being generated.
     * @param writer The stream for writing to the file.
     * @throws ImplerException If an error occurs while writing the header.
     */
    private void generateHeader(Class<?> token, BufferedWriter writer) throws ImplerException {

        try {
            writer.write("package ");
            writer.write(token.getPackageName());
            writer.write(";");
            writer.newLine();
            writer.newLine();
            writer.write("public class ");
            writer.write(token.getSimpleName());
            writer.write("Impl implements ");
            writer.write(token.getCanonicalName());
            writer.write(SPACE);
            writer.write("{");
        } catch (IOException e) {
            throw new ImplerException("Head write error" + e.getMessage());
        }

    }

    /**
     * Generates a method for the interface implementation.
     * For each method of the interface, it generates its declaration, parameters, and body.
     *
     * @param method The interface method.
     * @param writer The stream for writing to the file.
     * @throws ImplerException If an error occurs while generating the method.
     */
    private void generateMethod(Method method, BufferedWriter writer) throws ImplerException {
        if (Modifier.isStatic(method.getModifiers())) {
            return;
        }

        generateMethodDeclaration(method, writer);
        generateParameters(method, writer);
        generateMethodBody(method, writer);

    }

    /**
     * Generates the method declaration.
     *
     * @param method The interface method.
     * @param writer The stream for writing to the file.
     * @throws ImplerException If an error occurs while writing the method declaration.
     */
    private void generateMethodDeclaration(Method method, BufferedWriter writer) throws ImplerException {
        try {
            writer.newLine();
            writer.newLine();
            writer.write("\t@Override");
            writer.newLine();
            writer.write("\tpublic ");
            writer.write(method.getReturnType().getCanonicalName());
            writer.write(SPACE);
            writer.write(method.getName());
            writer.write("(");
        } catch (IOException e) {
            throw new ImplerException("Method declaration write error" + e.getMessage());
        }
    }

    /**
     * Generates the method parameters.
     *
     * @param method The interface method.
     * @param writer The stream for writing to the file.
     * @throws ImplerException If an error occurs while writing the method parameters.
     */
    private void generateParameters(Method method, BufferedWriter writer) throws ImplerException {
        try {
            writer.write(Arrays.stream(method.getParameters())
                    .map(parameter -> parameter.getType().getCanonicalName() + SPACE + parameter.getName())
                    .collect(Collectors.joining(", ")));
        } catch (IOException e) {
            throw new ImplerException("Method parameters write error" + e.getMessage());
        }

    }

    /**
     * Generates the method body.
     *
     * @param method The interface method.
     * @param writer The stream for writing to the file.
     * @throws ImplerException If an error occurs while writing the method body.
     */
    private void generateMethodBody(Method method, BufferedWriter writer) throws ImplerException {

        try {
            writer.write(")");
            writer.write(method.getExceptionTypes().length > 0
                    ? "\tthrows " + Arrays.stream(method.getExceptionTypes())
                    .map(Class::getName)
                    .collect(Collectors.joining(", "))
                    : "");
            writer.write(SPACE);
            writer.write("{");
            writer.newLine();
            writer.write(generateReturnStatement(method));
            writer.newLine();
            writer.write("\t}");
        } catch (IOException e) {
            throw new ImplerException("Method body write error" + e.getMessage());
        }

    }

    /**
     * Generates the return statement for the method.
     *
     * @param method The interface method.
     * @return The return statement for the method body.
     */
    private String generateReturnStatement(Method method) {
        if (method.getReturnType().isPrimitive()) {
            if (method.getReturnType() == boolean.class) {
                return "\t\treturn false;";
            } else if (method.getReturnType() == void.class) {
                return "";
            } else {
                return "\t\treturn 0;";
            }
        } else {
            return "\t\treturn null;";
        }
    }

    /**
     * Compiles the source file into bytecode.
     *
     * @param file      The path to the source file.
     * @param dependency A dependency for the compilation.

     * @throws ImplerException If an error occurs during compilation.
     */
    public static void compile(
            final Path file,
            final Class<?> dependency
    ) throws ImplerException {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new ImplerException("Could not find java compiler, include tools.jar to classpath");
        }

        final String classpath = getClassPath(dependency).toString();

        final String[] args = {
                "-cp", classpath,
                "-encoding", "UTF8",
                file.toString()
        };

        final int exitCode = compiler.run(null, null, null, args);
        if (exitCode != 0) {
            throw new ImplerException("Compiler exit code: " + exitCode);
        }
    }

    /**
     * Obtains the classpath for a given dependency.
     *
     * @param dependency The class for which the classpath is needed.
     * @return The classpath for the class file.
     * @throws ImplerException If an error occurs while obtaining the classpath.
     */
    private static Path getClassPath(final Class<?> dependency) throws ImplerException {
        try {
            CodeSource codeSource = dependency.getProtectionDomain().getCodeSource();
            if (codeSource == null) {
                return Path.of(System.getProperty("java.class.path"));
            }
            return Path.of(codeSource.getLocation().toURI());
        } catch (final URISyntaxException e) {
            throw new ImplerException("Error obtaining class path", e);
        }
    }

    /**
     * Creates a JAR file with the compiled class.
     *
     * @param token        The interface for which the JAR needs to be created.
     * @param jarFile      The path to the output JAR file.
     * @param tempDirectory The temporary directory for storing files.
     * @throws ImplerException If an error occurs while creating the JAR file.
     */
    private void createJar(Class<?> token, Path jarFile, Path tempDirectory) throws ImplerException {
        Manifest manifest = new Manifest();
        Attributes attributes = manifest.getMainAttributes();
        attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        attributes.put(Attributes.Name.IMPLEMENTATION_VENDOR, "Baronov Nikita");

        try (JarOutputStream jarOutputStream = new JarOutputStream(Files.newOutputStream(jarFile), manifest)) {
            Path classFilePath = generatePath(token, tempDirectory, ".class");
            jarOutputStream.putNextEntry(new ZipEntry(token.getPackageName().replace('.', '/') + "/" + token.getSimpleName() + "Impl.class"));
            Files.copy(classFilePath, jarOutputStream);

        } catch (IOException e) {
            throw new ImplerException("Cannot write Jar file" + e.getMessage());
        }
    }

    /**
     * Recursively deletes directories.
     * If a file or directory cannot be deleted, an error message is printed to {@code System.err}.
     *
     * @param pathToDelete The path to directory that will be deleted.
     * @throws ImplerException If an error occurs while deleting directories.
     */
    private void clean(Path pathToDelete) throws ImplerException{
        try (Stream<Path> paths = Files.walk(pathToDelete)) {
            paths.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(file -> {
                        if (!file.delete()) {
                            System.err.println("Failed to delete: " + file.getAbsolutePath());
                        }
                    });
        } catch (IOException e) {
            throw new ImplerException("Deleting directory error:" + e.getMessage());
        }
    }
}
