package codigo.Traductor;

public class Instruccion {
    private TipoInstruccion tipo;
    private String operandoIzq;
    private String operador;
    private String operandoDer;
    private String resultado;
    private int linea;

    public Instruccion(TipoInstruccion tipo, String operandoIzq, String operador, String operandoDer, String resultado, int linea) {
        this.tipo = tipo;
        this.operandoIzq = operandoIzq;
        this.operador = operador;
        this.operandoDer = operandoDer;
        this.resultado = resultado;
        this.linea = linea;
    }

    // Constructor para instrucciones simples (como incremento/decremento)
    public Instruccion(TipoInstruccion tipo, String operando, int linea) {
        this.tipo = tipo;
        this.operandoIzq = operando;
        this.linea = linea;
    }

    // Getters
    public TipoInstruccion getTipo() { return tipo; }
    public String getOperandoIzq() { return operandoIzq; }
    public String getOperador() { return operador; }
    public String getOperandoDer() { return operandoDer; }
    public String getResultado() { return resultado; }
    public int getLinea() { return linea; }
}