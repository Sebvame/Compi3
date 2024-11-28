package codigo;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import javax.swing.JOptionPane;

public class Main {
    public static void main(String[] args) {
        try {
            String rutaLexer = "src/codigo/Lexer.flex";
            String rutaParser = "src/codigo/Parser.cup";
            
            File lexerFile = new File(rutaLexer);
            File parserFile = new File(rutaParser);
            
            // Solo generar si los archivos existen
            if (lexerFile.exists() && parserFile.exists()) {
                try {
                    // Generar parser primero
                    generarParser(rutaParser);
                    // Luego generar lexer
                    generarLexer(rutaLexer);
                } catch (Exception e) {
                    System.err.println("Advertencia: Error al generar Lexer/Parser: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("Nota: Archivos Lexer.flex y/o Parser.cup no encontrados. Usando versión existente.");
            }
            
        } catch (Exception e) {
            System.err.println("Error en la inicialización: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Siempre intentar mostrar la interfaz
            javax.swing.SwingUtilities.invokeLater(() -> {
                try {
                    MainFrame frame = new MainFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    System.err.println("Error al crear la interfaz: " + e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, 
                        "Error al iniciar la aplicación: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }
    
    public static void generarLexer(String ruta) {
        try {
            File archivo = new File(ruta);
            System.out.println("Generando Lexer desde: " + archivo.getAbsolutePath());
            JFlex.Main.generate(archivo);
            System.out.println("Lexer generado exitosamente.");
        } catch (Exception e) {
            throw new RuntimeException("Error al generar lexer: " + e.getMessage(), e);
        }
    }
    
    public static void generarParser(String ruta) {
        try {
            String[] cupArgs = {"-destdir", "src/codigo/", 
                              "-parser", "Parser", 
                              "-symbols", "sym", 
                              ruta};
            System.out.println("Generando Parser desde: " + ruta);
            java_cup.Main.main(cupArgs);
            System.out.println("Parser generado exitosamente.");
            
            // Mover archivos si existen
            if (new File("Parser.java").exists()) {
                moveFile("Parser.java", "src/codigo/Parser.java");
            }
            if (new File("sym.java").exists()) {
                moveFile("sym.java", "src/codigo/sym.java");
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Error al generar parser: " + e.getMessage(), e);
        }
    }
    
    private static void moveFile(String sourceFile, String targetPath) {
        try {
            Path source = Paths.get(sourceFile);
            Path target = Paths.get(targetPath);
            
            // Crear directorios si no existen
            Files.createDirectories(target.getParent());
            
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error moviendo archivo " + sourceFile + ": " + e.getMessage());
        }
    }
}