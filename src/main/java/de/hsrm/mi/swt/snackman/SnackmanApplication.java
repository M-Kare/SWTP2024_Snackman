package de.hsrm.mi.swt.snackman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.*;
import java.util.logging.Logger;


@SpringBootApplication
public class SnackmanApplication {
    static Logger log = Logger.getLogger(SnackmanApplication.class.getName());

    public static void main(String[] args) {
        System.setProperty("python.import.site", "false");
        checkAndCopyResources();
        SpringApplication.run(SnackmanApplication.class, args);
    }

    public static void checkAndCopyResources() {
        Path workFolder = Paths.get("extensions").toAbsolutePath();
        String[] foldersToCopy = {"maze", "ghost", "chicken"};
        String folderToCreate = "map";
        Path srcFolder = Paths.get("src", "main", "resources");

        if (!Files.exists(workFolder)) {
            createWorkFolderAndCopyResources(workFolder, foldersToCopy, folderToCreate, srcFolder);
        } else {
            copyResourcesIfNotExist(workFolder, foldersToCopy, folderToCreate, srcFolder);
        }
    }

    private static void createWorkFolderAndCopyResources(Path workFolder, String[] foldersToCopy, String folderToCreate, Path srcFolder) {
        try {
            Files.createDirectories(workFolder);
            for (String folder : foldersToCopy) {
                Path srcPath = srcFolder.resolve(folder);
                Path targetPath = workFolder.resolve(folder);
                copyFolder(srcPath, targetPath);
            }
            Path mapFolderPath = workFolder.resolve(folderToCreate);
            Files.createDirectories(mapFolderPath);
        } catch (IOException e) {
            log.severe("Failed to create extensions directory or copy folders: " + e.getMessage()+ " Stacktrace: ");
            e.printStackTrace();
        }
    }

    private static void copyResourcesIfNotExist(Path workFolder, String[] foldersToCopy, String folderToCreate, Path srcFolder) {
        for (String folder : foldersToCopy) {
            Path targetPath = workFolder.resolve(folder);
            Path srcPath = srcFolder.resolve(folder);

            if (!Files.exists(targetPath)) {
                createDirectoryAndCopyFiles(srcPath, targetPath);
            } else {
                copyFilesIfNotExist(srcPath, targetPath);
            }
        }
        Path mapFolderPath = workFolder.resolve(folderToCreate);
        if (!Files.exists(mapFolderPath)) {
            try {
                Files.createDirectories(mapFolderPath);
            } catch (IOException e) {
                log.severe("Failed to create Map directory: " + e.getMessage()+ " Stacktrace: ");
                e.printStackTrace();
            }
        }
    }

    private static void createDirectoryAndCopyFiles(Path srcPath, Path targetPath) {
        try {
            Files.createDirectories(targetPath);
            copyFolder(srcPath, targetPath);
        } catch (IOException e) {
            log.severe("Failed to create directory or copy files: " + e.getMessage()+ " Stacktrace: ");
            e.printStackTrace();
        }
    }

    private static void copyFilesIfNotExist(Path srcPath, Path targetPath) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(srcPath)) {
            for (Path entry : stream) {
                Path targetFile = targetPath.resolve(entry.getFileName());
                if (!Files.exists(targetFile)) {
                    Files.copy(entry, targetFile, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (IOException e) {
            log.severe("Failed to copy files: " + e.getMessage());
        }
    }

    private static void copyFolder(Path src, Path dest) throws IOException {
        Files.walk(src).forEach(source -> {
            try {
                Path destination = dest.resolve(src.relativize(source));
                Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                log.severe("Failed to copy file from " + source + " to " + dest.resolve(src.relativize(source)) + ": " + e.getMessage());
            }
        });
    }
}