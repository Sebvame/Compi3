package codigo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.*;
import codigo.Traductor.Instruccion;
import codigo.Traductor.TipoInstruccion;
import codigo.Traductor.PilaSemantica;
import codigo.Traductor.RegistroSemantico;

public class SemanticAnalyzer {
    private TablaSimbolos tablaSimbolos;
    private List<String> erroresSemanticos;
    private Map<String, Expression> constantValues;
    private boolean hasError;
    private PilaSemantica pilaSemantica;
    private ArrayList<Instruccion> instrucciones;
    
    public SemanticAnalyzer(TablaSimbolos tablaSimbolos) {
        this.tablaSimbolos = tablaSimbolos;
        this.erroresSemanticos = new ArrayList<>();
        this.constantValues = new HashMap<>();
        this.hasError = false;
        this.pilaSemantica = new PilaSemantica();
        this.instrucciones = new ArrayList<>();
    }
    
    public ArrayList<Instruccion> getInstrucciones() {
        return instrucciones;
    }

    public void addInstruccion(Instruccion instruccion) {
        instrucciones.add(instruccion);
    }
    
    public boolean hasError() {
        return hasError;
    }
    
        public void checkFunctionDeclaration(Funcion funcion) {
        // Verifica si ya existe una función con el mismo nombre
        if (tablaSimbolos.existeFuncion(funcion.getIdentificador())) {
            addError(String.format("Función '%s' ya está definida", 
                    funcion.getIdentificador()), funcion.getFila());
            return;
        }

        // Verifica parámetros duplicados
        Set<String> paramNames = new HashSet<>();
        for (Variable param : funcion.getParametros()) {
            if (!paramNames.add(param.getIdentificador())) {
                addError(String.format("Parámetro '%s' duplicado en la función '%s'", 
                        param.getIdentificador(), funcion.getIdentificador()), 
                        param.getFila());
            }
        }
    }
        
    public void checkBreakStatement(int line) {
        if (!tablaSimbolos.isInLoop()) {
            addError("Sentencia 'break' fuera de un ciclo", line);
        }
    }
    
    public void checkVariableInFunction(Variable var, String functionName) {
        // Verifica si la variable ya está definida en el ámbito de la función
        if (tablaSimbolos.existeSimboloEnAmbito(var.getIdentificador(), functionName)) {
            addError(String.format("Variable '%s' ya está definida en la función '%s'", 
                    var.getIdentificador(), functionName), var.getFila());
        }
    }
    
    public void checkUndefinedVariable(String identifier, String scope, int line) {
        Variable var = tablaSimbolos.buscarVariable(identifier, scope);
        if (var == null) {
            addError(String.format("Variable '%s' no está definida en el ámbito '%s'", 
                    identifier, scope), line);
        }
    }
    
public void checkDoubleDefinition(Variable variable) {
    if (tablaSimbolos.existeSimboloEnAmbito(variable.getIdentificador(), variable.getAmbito())) {
        addError(String.format("Variable '%s' ya declarada en el ámbito '%s'", 
            variable.getIdentificador(), variable.getAmbito()), variable.getFila());
    }
}
    
    public Expression foldConstants(Expression left, String operator, Expression right) {
        if (left.isConstant() && right.isConstant()) {
            try {
                int leftValue = (Integer) left.getValor();
                int rightValue = (Integer) right.getValor();
                int result;
                
                switch (operator) {
                    case "+": result = leftValue + rightValue; break;
                    case "-": result = leftValue - rightValue; break;
                    case "*": result = leftValue * rightValue; break;
                    case "/": 
                        if (rightValue == 0) {
                            addError("División por cero", -1);
                            return new Expression(TipoDato.INT);
                        }
                        result = leftValue / rightValue; 
                        break;
                    default: return new Expression(TipoDato.INT);
                }
                
                return new Expression(TipoDato.INT, result);
            } catch (Exception e) {
                return new Expression(TipoDato.INT);
            }
        }
        return new Expression(TipoDato.INT);
    }
    
    public Expression checkAndPropagateConstants(String identifier, String scope, int line) {
    if (!tablaSimbolos.existeVariable(identifier, scope)) {
        addError(String.format("Variable '%s' no definida en el ámbito actual", identifier), line);
        return new Expression(TipoDato.INT); // Default to INT to prevent further propagation errors.
    }
    return propagateConstants(identifier, scope);
    }

    public Expression propagateConstants(String identifier, String scope) {
        Expression constValue = constantValues.get(identifier);
        if (constValue != null && constValue.isConstant()) {
            return constValue;
        }
        return null;
    }
    
public void checkLoopControl(String statement, int line) {
    if (!tablaSimbolos.isInLoop()) {
        addError(String.format("'%s' solo puede usarse dentro de ciclos", statement), line);
    }
}
    
    public void checkTypeCompatibility(TipoDato leftType, TipoDato rightType, String operator, int line) {
        if (leftType != rightType) {
            addError(String.format("Tipos incompatibles en operación '%s': %s y %s", 
                    operator, leftType, rightType), line);
        }
    }
    
private void addError(String message, int line) {
    hasError = true;
    if (line > 0) {
        erroresSemanticos.add(String.format("Línea %d: Error semántico - %s", line, message));
    } else {
        erroresSemanticos.add(String.format("Error semántico - %s (línea desconocida)", message));
    }
}
    
    public List<String> getErrores() {
        return erroresSemanticos;
    }
    
    public void clearErrors() {
        erroresSemanticos.clear();
        hasError = false;
    }
}