package codigo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java_cup.runtime.Symbol;
import codigo.Traductor.Instruccion;
import codigo.Traductor.TipoInstruccion;
import codigo.Traductor.Generador;
import java.util.TreeMap;

public class MainFrame extends JFrame {
    private JTextArea lexicalErrorsArea;
    private JTextArea syntaxErrorsArea;
    private JTextArea semanticErrorsArea;
    private JTextArea symbolTableArea;
    private JTable tokenTable;
    private DefaultTableModel tableModel;

    public MainFrame() {
        setTitle("Analizador Léxico, Sintáctico y Semántico");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 900);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Botón para abrir archivo
        JButton openButton = new JButton("Abrir Archivo");
        openButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                processFile(fileChooser.getSelectedFile());
            }
        });

        JPanel topPanel = new JPanel();
        topPanel.add(openButton);

        // Tabla para tokens
        String[] columnNames = {"Token", "Lexema", "Línea"};
        tableModel = new DefaultTableModel(columnNames, 0);
        tokenTable = new JTable(tableModel);
        tokenTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        tokenTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        tokenTable.getColumnModel().getColumn(2).setPreferredWidth(50);
        JScrollPane tableScrollPane = new JScrollPane(tokenTable);

        // Áreas de errores y tabla de símbolos
        lexicalErrorsArea = new JTextArea();
        lexicalErrorsArea.setEditable(false);
        syntaxErrorsArea = new JTextArea();
        syntaxErrorsArea.setEditable(false);
        semanticErrorsArea = new JTextArea();
        semanticErrorsArea.setEditable(false);
        symbolTableArea = new JTextArea();
        symbolTableArea.setEditable(false);

        // Panel de errores léxicos
        JPanel lexicalErrorsPanel = new JPanel(new BorderLayout());
        lexicalErrorsPanel.add(new JLabel("Errores Léxicos"), BorderLayout.NORTH);
        lexicalErrorsPanel.add(new JScrollPane(lexicalErrorsArea), BorderLayout.CENTER);

        // Panel de errores sintácticos
        JPanel syntaxErrorsPanel = new JPanel(new BorderLayout());
        syntaxErrorsPanel.add(new JLabel("Errores Sintácticos"), BorderLayout.NORTH);
        syntaxErrorsPanel.add(new JScrollPane(syntaxErrorsArea), BorderLayout.CENTER);

        // Panel de errores semánticos
        JPanel semanticErrorsPanel = new JPanel(new BorderLayout());
        semanticErrorsPanel.add(new JLabel("Errores Semánticos"), BorderLayout.NORTH);
        semanticErrorsPanel.add(new JScrollPane(semanticErrorsArea), BorderLayout.CENTER);

        // Panel de tabla de símbolos
        JPanel symbolTablePanel = new JPanel(new BorderLayout());
        symbolTablePanel.add(new JLabel("Tabla de Símbolos"), BorderLayout.NORTH);
        symbolTablePanel.add(new JScrollPane(symbolTableArea), BorderLayout.CENTER);

        // Panel que contiene todos los errores
        JPanel errorsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        errorsPanel.add(lexicalErrorsPanel);
        errorsPanel.add(syntaxErrorsPanel);
        errorsPanel.add(semanticErrorsPanel);
        errorsPanel.add(symbolTablePanel);

        // Split pane principal
        JSplitPane mainSplitPane = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            tableScrollPane,
            errorsPanel
        );
        mainSplitPane.setDividerLocation(300);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(mainSplitPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void processFile(File file) {
        try {
            // Limpiar todas las áreas
            tableModel.setRowCount(0);
            lexicalErrorsArea.setText("");
            syntaxErrorsArea.setText("");
            semanticErrorsArea.setText("");
            symbolTableArea.setText("");

            // Primera pasada: análisis léxico
            Reader reader = new BufferedReader(new FileReader(file));
            Lexer lexer = new Lexer(reader);
            ArrayList<Symbol> tokens = new ArrayList<>();

            Symbol token;
            while ((token = lexer.next_token()) != null && token.sym != sym.EOF) {
                tokens.add(token);
                tableModel.addRow(new Object[]{
                    sym.terminalNames[token.sym],
                    getLexema(token),
                    token.left
                });
            }

            // Segunda pasada: análisis sintáctico y semántico
reader.close();
reader = new BufferedReader(new FileReader(file));
lexer = new Lexer(reader);
Parser parser = new Parser(lexer);

try {
    parser.parse();
    
    // Mostrar errores semánticos
    List<String> semanticErrors = parser.getSemanticErrors();
    if (semanticErrors != null && !semanticErrors.isEmpty()) {
        for (String error : semanticErrors) {
            semanticErrorsArea.append(error + "\n");
        }
    } else {
        semanticErrorsArea.append("No se encontraron errores semánticos\n");
    }

    // Mostrar tabla de símbolos
    TablaSimbolos tablaSimbolos = parser.getTablaSimbolos();
    if (tablaSimbolos != null) {
        String tablaSimbolosStr = tablaSimbolos.verTablaSimbolos();
        if (tablaSimbolosStr != null && !tablaSimbolosStr.isEmpty()) {
            symbolTableArea.append(tablaSimbolosStr);
        } else {
            symbolTableArea.append("Tabla de símbolos vacía\n");
        }
        System.out.println("=== Debug Tabla de Símbolos ===");
        System.out.println(tablaSimbolosStr);
    } else {
        symbolTableArea.append("No se pudo obtener la tabla de símbolos\n");
        System.out.println("ERROR: No se pudo obtener la tabla de símbolos");
    }

    // Generación de código si no hay errores
    if (semanticErrors.isEmpty() && parser.getSyntaxErrors().isEmpty() && lexer.getErrors().isEmpty()) {
        try {
            String outputFileName = file.getName().replaceFirst("[.][^.]+$", "") + ".asm";
            
            // Obtener tabla de símbolos y mostrar debug
            TablaSimbolos tabla = parser.getTablaSimbolos();
            System.out.println("\n=== Debug Tabla de Símbolos antes de generación ===");
            System.out.println(tabla.verTablaSimbolos());

            // Obtener y mostrar debug de instrucciones
            ArrayList<Instruccion> instrucciones = parser.getInstrucciones();
            System.out.println("\n=== Debug Instrucciones ===");
            System.out.println("Número total de instrucciones: " + instrucciones.size());
            for (Instruccion inst : instrucciones) {
                System.out.println("Tipo: " + inst.getTipo() + 
                                 ", Izq: " + inst.getOperandoIzq() + 
                                 ", Op: " + inst.getOperador() + 
                                 ", Der: " + inst.getOperandoDer() +
                                 ", Línea: " + inst.getLinea());
            }
            
            // Iniciar generación de código
            Generador generador = new Generador(tabla, outputFileName);

            // Generar sección de datos
            generador.generarVariablesGlobales();

            // Procesar cada instrucción
            for (Instruccion inst : instrucciones) {
                System.out.println("Procesando instrucción: " + inst.getTipo() + " - " + inst.getOperandoIzq());
                switch(inst.getTipo()) {
                    case DECLARACION:
                        // Las declaraciones ya se manejan en generarVariablesGlobales
                        break;
                        
                    case ASIGNACION:
                        generador.generarAsignacion(
                            inst.getOperandoIzq(),
                            inst.getOperandoDer()
                        );
                        break;

                    case OPERACION_ARITMETICA:
                        generador.generarExpresionAritmetica(
                            inst.getResultado(),
                            inst.getOperandoIzq(),
                            inst.getOperador(),
                            inst.getOperandoDer()
                        );
                        break;

                    case IF:
                        generador.generarIf(
                            inst.getOperandoIzq(),
                            inst.getOperador(),
                            inst.getOperandoDer()
                        );
                        break;

                    case ELSE:
                        generador.generarElse();
                        break;

                    case WHILE:
                        generador.generarWhile(
                            inst.getOperandoIzq(),
                            inst.getOperador(),
                            inst.getOperandoDer()
                        );
                        break;

                    case INCREMENTO:
                        generador.generarIncremento(inst.getOperandoIzq());
                        break;

                    case DECREMENTO:
                        generador.generarDecremento(inst.getOperandoIzq());
                        break;
                }
            }

            generador.cerrarArchivo();

            JOptionPane.showMessageDialog(this,
                "Código ensamblador generado exitosamente en: " + outputFileName,
                "Generación Exitosa",
                JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Error al generar código ensamblador: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

} catch (Exception e) {
    syntaxErrorsArea.append("Error de parsing: " + e.getMessage() + "\n");
    e.printStackTrace();
}

// Mostrar errores sintácticos 
ArrayList<String> syntaxErrors = new ArrayList<>(parser.getSyntaxErrors());
if (!syntaxErrors.isEmpty()) {
    TreeMap<Integer, String> errorMap = new TreeMap<>();
    
    for (String error : syntaxErrors) {
        if (error.startsWith("Línea")) {
            try {
                int lineNum = Integer.parseInt(
                    error.split("Línea ")[1].split(":")[0].trim()
                );
                if (!errorMap.containsKey(lineNum)) {
                    errorMap.put(lineNum, error);
                }
            } catch (Exception e) {
                syntaxErrorsArea.append(error + "\n");
            }
        } else {
            syntaxErrorsArea.append(error + "\n");
        }
    }
    
    for (String error : errorMap.values()) {
        syntaxErrorsArea.append(error + "\n");
    }
} else {
    syntaxErrorsArea.append("No se encontraron errores sintácticos\n");
}

// Mostrar errores léxicos 
List<Lexer.ErrorLexico> lexicalErrors = lexer.getErrors();
if (!lexicalErrors.isEmpty()) {
    for (Lexer.ErrorLexico error : lexicalErrors) {
        lexicalErrorsArea.append(error.toString() + "\n");
    }
} else {
    lexicalErrorsArea.append("No se encontraron errores léxicos\n");
}

reader.close();

        } catch (Exception e) {
            handleError(e);
        }
    }

    private String getLexema(Symbol token) {
        if (token.value != null) {
            return token.value.toString();
        }
        
        switch (token.sym) {
            // Palabras reservadas
            case sym.INT: return "int";
            case sym.CHAR: return "char";
            case sym.VOID: return "void";
            case sym.IF: return "if";
            case sym.ELSE: return "else";
            case sym.WHILE: return "while";
            case sym.FOR: return "for";
            case sym.DO: return "do";
            case sym.BREAK: return "break";
            case sym.CONTINUE: return "continue";
            case sym.RETURN: return "return";
            case sym.SWITCH: return "switch";
            case sym.CASE: return "case";
            case sym.DEFAULT: return "default";
            case sym.READ: return "read";
            case sym.WRITE: return "write";
            case sym.CONST: return "const";
            case sym.SHORT: return "short";
            case sym.LONG: return "long";
            
            // Operadores
            case sym.PLUS: return "+";
            case sym.MINUS: return "-";
            case sym.TIMES: return "*";
            case sym.DIVIDE: return "/";
            case sym.MOD: return "%";
            case sym.ASSIGN: return "=";
            case sym.PLUS_ASSIGN: return "+=";
            case sym.MINUS_ASSIGN: return "-=";
            case sym.TIMES_ASSIGN: return "*=";
            case sym.DIVIDE_ASSIGN: return "/=";
            case sym.EQUALS: return "==";
            case sym.NOT_EQUALS: return "!=";
            case sym.GT: return ">";
            case sym.LT: return "<";
            case sym.GTEQ: return ">=";
            case sym.LTEQ: return "<=";
            case sym.AND: return "&&";
            case sym.OR: return "||";
            case sym.NOT: return "!";
            case sym.PLUS_PLUS: return "++";
            case sym.MINUS_MINUS: return "--";
            
            // Delimitadores
            case sym.LPAREN: return "(";
            case sym.RPAREN: return ")";
            case sym.LBRACE: return "{";
            case sym.RBRACE: return "}";
            case sym.SEMICOLON: return ";";
            case sym.COMMA: return ",";
            case sym.COLON: return ":";
            
            default: return "";
        }
    }

    private void handleError(Exception e) {
        String message = "Error al procesar el archivo: " + e.getMessage();
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        syntaxErrorsArea.append(message + "\n");
        e.printStackTrace();
    }
}