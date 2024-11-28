package codigo.Traductor;

import codigo.Expression;
import codigo.Simbolo;
import codigo.TablaSimbolos;
import codigo.Variable;
import codigo.TipoDato;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;

public class Generador {
    private static int contadorTemp = 0;
    private static int contadorEtiq = 1;
    private static String etiqAnterior = "L0";
    private static String etiqSiguiente = "";
    private static PrintStream out = System.out;
    private static FileWriter fileWriter;
    
    private static String startLabel = "";
    private static String exitLabel = "";
    private static Deque<String> pilaEnd = new LinkedList<>();
    private static TablaSimbolos tablaSimbolos;
    private static boolean inMain = false;

    public Generador(TablaSimbolos tablaSimbolos, String outputFile) {
        Generador.tablaSimbolos = tablaSimbolos;
        try {
            fileWriter = new FileWriter(outputFile);
            generarEncabezado();
        } catch (IOException e) {
            System.err.println("Error al crear archivo de salida: " + e.getMessage());
        }
    }

    private void generarEncabezado() throws IOException {
        fileWriter.write(".386\n");
        fileWriter.write(".model flat, stdcall\n");
        fileWriter.write("option casemap:none\n\n");
        fileWriter.write("include \\masm32\\include\\windows.inc\n");
        fileWriter.write("include \\masm32\\include\\kernel32.inc\n");
        fileWriter.write("include \\masm32\\include\\user32.inc\n");
        fileWriter.write("includelib \\masm32\\lib\\kernel32.lib\n");
        fileWriter.write("includelib \\masm32\\lib\\user32.lib\n\n");
        fileWriter.write(".data\n");
    }

    public void generarVariablesGlobales() throws IOException {
        for (Simbolo simbolo : tablaSimbolos.getTablaSimbolos()) {
            if (simbolo instanceof Variable) {
                Variable var = (Variable) simbolo;
                System.out.println("Generando variable: " + var.getIdentificador() + " en ámbito: " + var.getAmbito());
                switch (var.getTipo()) {
                    case INT:
                        fileWriter.write(var.getIdentificador() + " dd 0\n");
                        break;
                    case CHAR:
                        fileWriter.write(var.getIdentificador() + " db 0\n");
                        break;
                }
            }
        }
        fileWriter.write("\n.code\n");
        fileWriter.write("start:\n");
    }

    public void procesarInstruccion(Instruccion instruccion) throws IOException {
        System.out.println("Procesando instrucción: " + instruccion.getTipo() + 
                         " (" + instruccion.getOperandoIzq() + " " + 
                         instruccion.getOperador() + " " + 
                         instruccion.getOperandoDer() + ")");
                         
        switch (instruccion.getTipo()) {
            case DECLARACION:
                // Las declaraciones ya se manejan en generarVariablesGlobales
                break;
                
            case ASIGNACION:
                if (instruccion.getOperador() != null && instruccion.getOperandoDer() != null) {
                    generarAsignacionConOperacion(
                        instruccion.getOperandoIzq(),
                        instruccion.getOperador(),
                        instruccion.getOperandoDer()
                    );
                } else {
                    generarAsignacion(instruccion.getOperandoIzq(), instruccion.getOperandoDer());
                }
                break;

            case OPERACION_ARITMETICA:
                generarExpresionAritmetica(
                    instruccion.getResultado(),
                    instruccion.getOperandoIzq(),
                    instruccion.getOperador(),
                    instruccion.getOperandoDer()
                );
                break;

            case IF:
                generarIf(instruccion.getOperandoIzq(), 
                         instruccion.getOperador(), 
                         instruccion.getOperandoDer());
                break;

            case ELSE:
                generarElse();
                break;

            case WHILE:
                generarWhile(instruccion.getOperandoIzq(), 
                           instruccion.getOperador(), 
                           instruccion.getOperandoDer());
                break;

            case INCREMENTO:
                generarIncremento(instruccion.getOperandoIzq());
                break;

            case DECREMENTO:
                generarDecremento(instruccion.getOperandoIzq());
                break;
        }
    }

    private void generarAsignacionConOperacion(String destino, String operador, String valor) throws IOException {
        fileWriter.write("    mov eax, " + destino + "\n");
        switch (operador) {
            case "+=":
                fileWriter.write("    add eax, " + valor + "\n");
                break;
            case "-=":
                fileWriter.write("    sub eax, " + valor + "\n");
                break;
            case "*=":
                fileWriter.write("    imul eax, " + valor + "\n");
                break;
            case "/=":
                fileWriter.write("    xor edx, edx\n");
                fileWriter.write("    mov ebx, " + valor + "\n");
                fileWriter.write("    idiv ebx\n");
                break;
        }
        fileWriter.write("    mov " + destino + ", eax\n");
    }

    public void generarExpresionAritmetica(String resultado, String op1, String operador, String op2) throws IOException {
        Expression expr1 = tablaSimbolos.getConstantValue(op1);
        Expression expr2 = tablaSimbolos.getConstantValue(op2);
        
        if (expr1 != null && expr2 != null && expr1.isConstant() && expr2.isConstant()) {
            // Constant folding
            int val1 = (Integer) expr1.getValor();
            int val2 = (Integer) expr2.getValor();
            int resultadoConstante = 0;
            
            switch (operador) {
                case "+": resultadoConstante = val1 + val2; break;
                case "-": resultadoConstante = val1 - val2; break;
                case "*": resultadoConstante = val1 * val2; break;
                case "/": 
                    if (val2 != 0) resultadoConstante = val1 / val2;
                    break;
            }
            
            fileWriter.write("    mov eax, " + resultadoConstante + "\n");
            fileWriter.write("    mov " + resultado + ", eax\n");
        } else {
            // Generación normal
            fileWriter.write("    mov eax, " + op1 + "\n");
            switch (operador) {
                case "+":
                    fileWriter.write("    add eax, " + op2 + "\n");
                    break;
                case "-":
                    fileWriter.write("    sub eax, " + op2 + "\n");
                    break;
                case "*":
                    fileWriter.write("    imul eax, " + op2 + "\n");
                    break;
                case "/":
                    fileWriter.write("    xor edx, edx\n");
                    fileWriter.write("    mov ebx, " + op2 + "\n");
                    fileWriter.write("    idiv ebx\n");
                    break;
            }
            fileWriter.write("    mov " + resultado + ", eax\n");
        }
    }

    public void generarIf(String condicion, String operador, String valor) throws IOException {
        String etiquetaFalso = nuevaEtiq();
        String etiquetaFin = nuevaEtiq();

        fileWriter.write("    mov eax, " + condicion + "\n");
        fileWriter.write("    cmp eax, " + valor + "\n");
        
        switch (operador) {
            case ">":
                fileWriter.write("    jle " + etiquetaFalso + "\n");
                break;
            case "<":
                fileWriter.write("    jge " + etiquetaFalso + "\n");
                break;
            case ">=":
                fileWriter.write("    jl " + etiquetaFalso + "\n");
                break;
            case "<=":
                fileWriter.write("    jg " + etiquetaFalso + "\n");
                break;
            case "==":
                fileWriter.write("    jne " + etiquetaFalso + "\n");
                break;
            case "!=":
                fileWriter.write("    je " + etiquetaFalso + "\n");
                break;
        }

        pilaEnd.push(etiquetaFalso);
        pilaEnd.push(etiquetaFin);
    }

    public void generarElse() throws IOException {
        String etiquetaFin = pilaEnd.pop();
        String etiquetaFalso = pilaEnd.pop();
        
        fileWriter.write("    jmp " + etiquetaFin + "\n");
        fileWriter.write(etiquetaFalso + ":\n");
        
        pilaEnd.push(etiquetaFin);
    }

    public void cerrarIf() throws IOException {
        String etiqueta = pilaEnd.pop();
        fileWriter.write(etiqueta + ":\n");
    }

    public void generarWhile(String condicion, String operador, String valor) throws IOException {
        String etiquetaInicio = nuevaEtiq();
        String etiquetaFin = nuevaEtiq();

        fileWriter.write(etiquetaInicio + ":\n");
        fileWriter.write("    mov eax, " + condicion + "\n");
        fileWriter.write("    cmp eax, " + valor + "\n");
        
        switch (operador) {
            case ">":
                fileWriter.write("    jle " + etiquetaFin + "\n");
                break;
            case "<":
                fileWriter.write("    jge " + etiquetaFin + "\n");
                break;
            case ">=":
                fileWriter.write("    jl " + etiquetaFin + "\n");
                break;
            case "<=":
                fileWriter.write("    jg " + etiquetaFin + "\n");
                break;
            case "==":
                fileWriter.write("    jne " + etiquetaFin + "\n");
                break;
            case "!=":
                fileWriter.write("    je " + etiquetaFin + "\n");
                break;
        }

        pilaEnd.push(etiquetaInicio);
        pilaEnd.push(etiquetaFin);
    }

    public void cerrarWhile() throws IOException {
        String etiquetaFin = pilaEnd.pop();
        String etiquetaInicio = pilaEnd.pop();
        
        fileWriter.write("    jmp " + etiquetaInicio + "\n");
        fileWriter.write(etiquetaFin + ":\n");
    }

    public void generarIncremento(String variable) throws IOException {
        fileWriter.write("    inc " + variable + "\n");
    }

    public void generarDecremento(String variable) throws IOException {
        fileWriter.write("    dec " + variable + "\n");
    }

    public void generarAsignacion(String destino, String fuente) throws IOException {
        if (fuente == null) return;
        
        Expression exprFuente = tablaSimbolos.getConstantValue(fuente);
        if (exprFuente != null && exprFuente.isConstant()) {
            fileWriter.write("    mov " + destino + ", " + exprFuente.getValor() + "\n");
        } else {
            fileWriter.write("    mov eax, " + fuente + "\n");
            fileWriter.write("    mov " + destino + ", eax\n");
        }
    }

    public void cerrarArchivo() throws IOException {
        fileWriter.write("\n    invoke ExitProcess, 0\n");
        fileWriter.write("end start\n");
        fileWriter.close();
    }

    private static String nuevaTemp() {
        return "t" + contadorTemp++;
    }

    private static String nuevaEtiq() {
        return "L" + contadorEtiq++;
    }
}