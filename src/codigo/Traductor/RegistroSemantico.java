package codigo.Traductor;

import codigo.sym;
import codigo.TipoDato;
import codigo.TablaSimbolos;

public class RegistroSemantico {
    private int operacion;
    private String valor;
    private TipoDato tipo;
    private boolean esConstante;
    private Object valorConstante;

    /**
     * Constructor para valores simples
     */
    public RegistroSemantico(String valor) {
        this.operacion = -1;
        this.valor = valor;
        this.tipo = null;
        this.esConstante = false;
        this.valorConstante = null;
    }

    /**
     * Constructor para operaciones
     */
    public RegistroSemantico(int operacion) {
        this.operacion = operacion;
        this.valor = null;
        this.tipo = null;
        this.esConstante = false;
        this.valorConstante = null;
    }

    /**
     * Constructor completo
     */
    public RegistroSemantico(String valor, TipoDato tipo, boolean esConstante, Object valorConstante) {
        this.operacion = -1;
        this.valor = valor;
        this.tipo = tipo;
        this.esConstante = esConstante;
        this.valorConstante = valorConstante;
    }

    // Getters y setters
    public int getOperacion() {
        return operacion;
    }
    
    public Instruccion generarInstruccion(int linea) {
        if (esOperacionAritmetica()) {
            return new Instruccion(
                TipoInstruccion.OPERACION_ARITMETICA,
                valor,
                getNombreOperacion(),
                null,
                null,
                linea
            );
        } else if (esAsignacion()) {
            return new Instruccion(
                TipoInstruccion.ASIGNACION,
                valor,
                "=",
                null,
                null,
                linea
            );
        } else if (esIncrementoDecremento()) {
            return new Instruccion(
                operacion == sym.PLUS_PLUS ? TipoInstruccion.INCREMENTO : TipoInstruccion.DECREMENTO,
                valor,
                linea
            );
        }
        return null;
    }

    public void setOperacion(int operacion) {
        this.operacion = operacion;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public TipoDato getTipo() {
        return tipo;
    }

    public void setTipo(TipoDato tipo) {
        this.tipo = tipo;
    }

    public boolean esConstante() {
        return esConstante;
    }

    public void setEsConstante(boolean esConstante) {
        this.esConstante = esConstante;
    }

    public Object getValorConstante() {
        return valorConstante;
    }

    public void setValorConstante(Object valorConstante) {
        this.valorConstante = valorConstante;
        if (valorConstante != null) {
            this.esConstante = true;
        }
    }

    /**
     * Obtiene el nombre de la operación según sym
     */
    public String getNombreOperacion() {
        if (operacion == -1) {
            return "VALOR";
        }
        return sym.terminalNames[operacion];
    }

    /**
     * Verifica si el registro representa una operación aritmética
     */
    public boolean esOperacionAritmetica() {
        return operacion == sym.PLUS || operacion == sym.MINUS || 
               operacion == sym.TIMES || operacion == sym.DIVIDE || 
               operacion == sym.MOD;
    }

    /**
     * Verifica si el registro representa una operación de comparación
     */
    public boolean esOperacionComparacion() {
        return operacion == sym.GT || operacion == sym.LT || 
               operacion == sym.GTEQ || operacion == sym.LTEQ || 
               operacion == sym.EQUALS || operacion == sym.NOT_EQUALS;
    }

    /**
     * Verifica si el registro representa una operación lógica
     */
    public boolean esOperacionLogica() {
        return operacion == sym.AND || operacion == sym.OR || operacion == sym.NOT;
    }

    /**
     * Verifica si el registro representa una asignación
     */
    public boolean esAsignacion() {
        return operacion == sym.ASSIGN || operacion == sym.PLUS_ASSIGN || 
               operacion == sym.MINUS_ASSIGN || operacion == sym.TIMES_ASSIGN || 
               operacion == sym.DIVIDE_ASSIGN;
    }

    /**
     * Verifica si el registro representa un incremento o decremento
     */
    public boolean esIncrementoDecremento() {
        return operacion == sym.PLUS_PLUS || operacion == sym.MINUS_MINUS;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("RegistroSemantico{");
        
        if (operacion == -1) {
            sb.append("valor='").append(valor).append('\'');
        } else {
            sb.append("operacion='").append(getNombreOperacion()).append('\'');
            if (valor != null) {
                sb.append(", valor='").append(valor).append('\'');
            }
        }
        
        if (tipo != null) {
            sb.append(", tipo=").append(tipo);
        }
        
        if (esConstante) {
            sb.append(", constante=").append(valorConstante);
        }
        
        sb.append('}');
        return sb.toString();
    }
}