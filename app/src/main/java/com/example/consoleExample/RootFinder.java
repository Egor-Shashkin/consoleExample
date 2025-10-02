package com.example.consoleExample;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RootFinder {
    
    public static String findGradleProjectRoot() {
        return findGradleProjectRoot(Paths.get("").toAbsolutePath());
    }
    
    public static String findGradleProjectRoot(Path startPath) {
        Path current = startPath;
        
        while (current != null) {
            // Проверяем наличие файлов Gradle
            if (isGradleProjectRoot(current)) {
                return current.toString();
            }
            
            // Поднимаемся на уровень выше
            Path parent = current.getParent();
            if (parent == null || parent.equals(current)) {
                break; // Достигли корня файловой системы
            }
            current = parent;
        }
        
        throw new RuntimeException("Gradle project root not found");
    }
    
    private static boolean isGradleProjectRoot(Path path) {
        return Files.exists(path.resolve("settings.gradle"));
    }
}