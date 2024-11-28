

package codigo;

public class Expression {
    private TipoDato tipo;
    private Object valor;  // Para constant folding
    private boolean isConstant;
    
    public Expression(TipoDato tipo) {
        this.tipo = tipo;
        this.valor = null;
        this.isConstant = false;
    }
    
    public Expression(TipoDato tipo, Object valor) {
        this.tipo = tipo;
        this.valor = valor;
        this.isConstant = true;
    }
    
    public TipoDato getTipo() {
        return tipo;
    }
    
    public Object getValor() {
        return valor;
    }
    
    public boolean isConstant() {
        return isConstant;
    }
    
    // Método para evaluar operaciones aritméticas con constant folding
    public static Expression evaluateArithmetic(Expression left, String operator, Expression right) {
        // Si ambos operandos son constantes, podemos hacer constant folding
        if (left.isConstant && right.isConstant) {
            switch (operator) {
                case "+":
                    if (left.tipo == TipoDato.INT && right.tipo == TipoDato.INT) {
                        int result = (Integer)left.valor + (Integer)right.valor;
                        return new Expression(TipoDato.INT, result);
                    }
                    break;
                case "*":
                    if (left.tipo == TipoDato.INT && right.tipo == TipoDato.INT) {
                        int result = (Integer)left.valor * (Integer)right.valor;
                        return new Expression(TipoDato.INT, result);
                    }
                    break;
                // Agregar otros operadores según sea necesario
            }
        }
        
        // Si no se puede hacer constant folding, retornar una expresión normal
        return new Expression(determineResultType(left.tipo, right.tipo));
    }
    
    // Determina el tipo resultante de una operación
    private static TipoDato determineResultType(TipoDato left, TipoDato right) {
        if (left == TipoDato.INT && right == TipoDato.INT) {
            return TipoDato.INT;
        }
        // Agregar más reglas según sea necesario
        return TipoDato.INT; // default
    }
}
