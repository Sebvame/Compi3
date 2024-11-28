package codigo;
import java_cup.runtime.*;
import static codigo.sym.*;

%%

%class Lexer
%unicode
%line
%cup
%public
%state COMMENT_LINE, COMMENT_BLOCK

%{
    private Symbol symbol(int type) {
        return new Symbol(type, yyline + 1, yycolumn + 1);
    }
    
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline + 1, yycolumn + 1, value);
    }

    private java.util.List<ErrorLexico> errors = new java.util.ArrayList<>();
    
    public static class ErrorLexico {
        public final String mensaje;
        public final int linea;
        
        public ErrorLexico(String mensaje, int linea) {
            this.mensaje = mensaje;
            this.linea = linea;
        }
        
        @Override
        public String toString() {
            return String.format("Línea %d: %s", linea, mensaje);
        }
    }
    
    public java.util.List<ErrorLexico> getErrors() {
        return errors;
    }

    // Método auxiliar para manejar errores sin generar tokens
    private void addError(String message) {
        errors.add(new ErrorLexico(message, yyline + 1));
    }
%}

/* Definiciones regulares */
LineTerminator = \r|\n|\r\n
WhiteSpace = {LineTerminator} | [ \t\f]
Letter = [a-zA-Z]
Digit = [0-9]
Identifier = {Letter}({Letter}|{Digit}|[_])*
Integer = {Digit}+
Decimal = {Integer}\.{Integer}

/* Identificadores y números */
Letter = [a-zA-Z]
Digit = [0-9]
Identifier = {Letter}({Letter}|{Digit}|[_])*
Integer = {Digit}+
Decimal = {Integer}\.{Integer}

/* Patrones de error */
MultiplePoints = {Integer}\.{Integer}(\.{Integer})+
StartWithNumber = {Digit}+{Letter}({Letter}|{Digit}|[_])*

%%

/* Comentarios - usando estados */
<YYINITIAL> {
    "//"    { yybegin(COMMENT_LINE); }
    "/*"    { yybegin(COMMENT_BLOCK); }
}

<COMMENT_LINE> {
    {LineTerminator}    { yybegin(YYINITIAL); }
    .                   { /* ignorar */ }
}

<COMMENT_BLOCK> {
    "*/"                { yybegin(YYINITIAL); }
    {LineTerminator}    { /* ignorar */ }
    .                   { /* ignorar */ }
    <<EOF>>            { 
        addError("Comentario multilínea sin cerrar");
        yybegin(YYINITIAL); 
    }
}

<YYINITIAL> {
    /* Ignorar espacios en blanco */
    {WhiteSpace}        { /* Ignorar */ }

    /* Patrones de error */
    {MultiplePoints}    { 
        addError("Número decimal mal formado: '" + yytext() + "'");
        /* Continuar el análisis sin generar token */
    }

    {StartWithNumber}   { 
        addError("Identificador no puede empezar con número: '" + yytext() + "'");
        /* Continuar el análisis sin generar token */
    }

 /* Palabras reservadas */
    "break"         { return symbol(BREAK); }
    "case"          { return symbol(CASE); }
    "char"          { return symbol(CHAR); }
    "const"         { return symbol(CONST); }
    "continue"      { return symbol(CONTINUE); }
    "default"       { return symbol(DEFAULT); }
    "do"            { return symbol(DO); }
    "else"          { return symbol(ELSE); }
    "for"           { return symbol(FOR); }
    "if"            { return symbol(IF); }
    "int"           { return symbol(INT); }
    "long"          { return symbol(LONG); }
    "return"        { return symbol(RETURN); }
    "short"         { return symbol(SHORT); }
    "switch"        { return symbol(SWITCH); }
    "void"          { return symbol(VOID); }
    "while"         { return symbol(WHILE); }
    "read"          { return symbol(READ); }
    "write"         { return symbol(WRITE); }

    /* Operadores */
    "++"            { return symbol(PLUS_PLUS); }
    "--"            { return symbol(MINUS_MINUS); }
    "+"             { return symbol(PLUS); }
    "-"             { return symbol(MINUS); }
    "*"             { return symbol(TIMES); }
    "/"             { return symbol(DIVIDE); }
    "%"             { return symbol(MOD); }
    "="             { return symbol(ASSIGN); }
    "+="            { return symbol(PLUS_ASSIGN); }
    "-="            { return symbol(MINUS_ASSIGN); }
    "*="            { return symbol(TIMES_ASSIGN); }
    "/="            { return symbol(DIVIDE_ASSIGN); }
    "=="            { return symbol(EQUALS); }
    ">="            { return symbol(GTEQ); }
    ">"             { return symbol(GT); }
    "<="            { return symbol(LTEQ); }
    "<"             { return symbol(LT); }
    "!="            { return symbol(NOT_EQUALS); }
    "||"            { return symbol(OR); }
    "&&"            { return symbol(AND); }
    "!"             { return symbol(NOT); }

    /* Delimitadores */
    "("             { return symbol(LPAREN); }
    ")"             { return symbol(RPAREN); }
    "{"             { return symbol(LBRACE); }
    "}"             { return symbol(RBRACE); }
    ";"             { return symbol(SEMICOLON); }
    ","             { return symbol(COMMA); }
    ":"             { return symbol(COLON); }

    /* Identificadores y números */
    {Identifier}    { return symbol(IDENTIFIER, yytext()); }
    {Integer}       { return symbol(NUMBER, Integer.parseInt(yytext())); }
    {Decimal}       { return symbol(NUMBER, Double.parseDouble(yytext())); }

    /* Error - cualquier otro carácter */
    [^]             { addError("Carácter inválido '" + yytext() + "'"); }
}
