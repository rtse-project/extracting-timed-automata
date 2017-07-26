/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (C) 2001 Gerwin Klein <lsf@jflex.de>                          *
 * All rights reserved.                                                    *
 *                                                                         *
 * This is a modified version of the example from                          *
 *   http://www.lincom-asg.com/~rjamison/byacc/                            *
 *                                                                         *
 * Thanks to Larry Bell and Bob Jamison for suggestions and comments.      *
 *                                                                         *
 * License: BSD                                                            *
 *                                                                         *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

%{
import timeannotation.definition.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
%}
      
%token NL          /* newline  */
%token <dval> NUM  /* a number */
%token <sval> TEXT /* a text */
%token CLOCK /* a text */
%token NAME /* a text */
%token VIR /* a text */
%type <dval> exp

%left '-' '+'
%left '*' '/'
%left NEG          /* negation--unary minus */
%right '^'         /* exponentiation        */
      
%%

annotation:   /* empty string */
       | clock
       ;
      
clock: '@' CLOCK '(' NAME '=' VIR TEXT VIR ')'   {clockAnn = new ClockAnnotation($7);}
     | '@' CLOCK                                 {clockAnn = new ClockAnnotation();}

%%

  private Yylex lexer;

  private ClockAnnotation clockAnn;

  private int yylex () {
    int yyl_return = -1;
    try {
      yylval = new ParserVal(0);
      yyl_return = lexer.yylex();
    }
    catch (IOException e) {
      System.err.println("IO error :"+e);
    }
    return yyl_return;
  }


  public void yyerror (String error) {
    System.err.println ("Error: " + error);
  }


  public Parser(Reader r) {
    lexer = new Yylex(r, this);
  }

  public ClockAnnotation getResult(){
    return clockAnn;
  }

  public static ClockAnnotation getAnnotation(String annotation){
    InputStream stream = new ByteArrayInputStream(annotation.getBytes(StandardCharsets.UTF_8));
    Parser yyparser = new Parser(new InputStreamReader(stream));
    yyparser.yyparse();
    return yyparser.getResult();
  }

