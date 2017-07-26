package timeannotation.parser;

%%

%byaccj

%{
  private Parser yyparser;

  public Yylex(java.io.Reader r, Parser yyparser) {
    this(r);
    this.yyparser = yyparser;
  }
%}

NUM = [0-9]+ ("." [0-9]+)?
TEXT = [aA-zZ]([aA-zZ_-]+)?
NL  = \n | \r | \r\n

%%

/* operators */
"+" | 
"-" | 
"*" | 
"/" | 
"^" | 
"(" |
"=" |
"," |
"@" |
")"    { return (int) yycharat(0); }

"\"" |
"'"    {return Parser.VIR; }

/* newline */
{NL}   { return Parser.NL; }

"Clock" { return Parser.CLOCK; }
"name" | "Name" { return Parser.NAME; }

/* float */
{NUM}  { yyparser.yylval = new ParserVal(Double.parseDouble(yytext()));
         return Parser.NUM; }

{TEXT} {
         yyparser.yylval = new ParserVal(yytext());
         return Parser.TEXT;
        }

/* whitespace */
[ \t]+ { }

/* error fallback */
[^]    { System.err.println("Error: unexpected character '"+yytext()+"'"); return -1; }
