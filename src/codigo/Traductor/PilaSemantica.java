package codigo.Traductor;

import java.util.ArrayList;
import java.util.List;
import codigo.TipoDato;

public class PilaSemantica {
    private List<RegistroSemantico> pilaSemantica;
    private int maxSize;

    public PilaSemantica() {
        pilaSemantica = new ArrayList<>();
        maxSize = 1000; // Tamaño máximo por defecto
    }

    public PilaSemantica(int maxSize) {
        pilaSemantica = new ArrayList<>();
        this.maxSize = maxSize;
    }

    /**
     * Inserta un registro al inicio de la pila
     */
    public void push_init(String valor) {
        if (pilaSemantica.size() >= maxSize) {
            throw new RuntimeException("Error: Pila semántica llena");
        }
        pilaSemantica.add(0, new RegistroSemantico(valor));
    }

    /**
     * Inserta un registro con valor y tipo al inicio de la pila
     */
    public void push_init(String valor, TipoDato tipo) {
        if (pilaSemantica.size() >= maxSize) {
            throw new RuntimeException("Error: Pila semántica llena");
        }
        RegistroSemantico registro = new RegistroSemantico(valor);
        registro.setTipo(tipo);
        pilaSemantica.add(0, registro);
    }

    /**
     * Inserta un registro al final de la pila
     */
    public void push_end(String valor) {
        if (pilaSemantica.size() >= maxSize) {
            throw new RuntimeException("Error: Pila semántica llena");
        }
        pilaSemantica.add(new RegistroSemantico(valor));
    }

    /**
     * Inserta un registro con valor y tipo al final de la pila
     */
    public void push_end(String valor, TipoDato tipo) {
        if (pilaSemantica.size() >= maxSize) {
            throw new RuntimeException("Error: Pila semántica llena");
        }
        RegistroSemantico registro = new RegistroSemantico(valor);
        registro.setTipo(tipo);
        pilaSemantica.add(registro);
    }

    /**
     * Extrae el primer elemento de la pila
     */
    public RegistroSemantico pop_init() {
        if (pilaSemantica.isEmpty()) {
            throw new RuntimeException("Error: Pila semántica vacía");
        }
        RegistroSemantico temp = pilaSemantica.get(0);
        pilaSemantica.remove(0);
        return temp;
    }

    /**
     * Extrae el último elemento de la pila
     */
    public RegistroSemantico pop_end() {
        if (pilaSemantica.isEmpty()) {
            throw new RuntimeException("Error: Pila semántica vacía");
        }
        int lastIndex = pilaSemantica.size() - 1;
        RegistroSemantico temp = pilaSemantica.get(lastIndex);
        pilaSemantica.remove(lastIndex);
        return temp;
    }

    /**
     * Observa el primer elemento sin extraerlo
     */
    public RegistroSemantico peek_init() {
        if (pilaSemantica.isEmpty()) {
            throw new RuntimeException("Error: Pila semántica vacía");
        }
        return pilaSemantica.get(0);
    }

    /**
     * Observa el último elemento sin extraerlo
     */
    public RegistroSemantico peek_end() {
        if (pilaSemantica.isEmpty()) {
            throw new RuntimeException("Error: Pila semántica vacía");
        }
        return pilaSemantica.get(pilaSemantica.size() - 1);
    }

    /**
     * Verifica si la pila está vacía
     */
    public boolean isEmpty() {
        return pilaSemantica.isEmpty();
    }

    /**
     * Limpia todos los elementos de la pila
     */
    public void clear() {
        pilaSemantica.clear();
    }

    /**
     * Retorna el número de elementos en la pila
     */
    public int size() {
        return pilaSemantica.size();
    }

    /**
     * Busca un valor específico en la pila
     */
    public boolean contains(String valor) {
        for (RegistroSemantico registro : pilaSemantica) {
            if (registro.getValor().equals(valor)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtiene el tipo de dato del último registro en la pila
     */
    public TipoDato getCurrentType() {
        if (!pilaSemantica.isEmpty()) {
            return pilaSemantica.get(pilaSemantica.size() - 1).getTipo();
        }
        return null;
    }

    /**
     * Imprime el contenido de la pila para depuración
     */
    public void print() {
        System.out.println("Contenido de la pila semántica:");
        for (int i = 0; i < pilaSemantica.size(); i++) {
            System.out.printf("[%d] %s%n", i, pilaSemantica.get(i).toString());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("PilaSemantica{\n");
        for (int i = 0; i < pilaSemantica.size(); i++) {
            sb.append("  [").append(i).append("] ")
              .append(pilaSemantica.get(i).toString())
              .append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}