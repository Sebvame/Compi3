package codigo;

public class Token {
    public final String tipo;
    public final String valor;
    public final int linea;
    
    public Token(String tipo, String valor, int linea) {
        this.tipo = tipo;
        this.valor = valor;
        this.linea = linea;
    }
    
    @Override
    public String toString() {
        return String.format("%-20s | %-20s | línea: %d", 
                           tipo, valor, linea);
    }
}