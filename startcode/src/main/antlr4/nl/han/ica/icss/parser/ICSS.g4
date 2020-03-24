grammar ICSS;


//--- PARSER: ---

stylesheet: declarationRule * | EOF;


declarationRule: variabledeclaration | stylerule ;


stylerule: selector
            OPEN_BRACE
                declaration *
            CLOSE_BRACE
            ;

declaration: styledeclaration | variabledeclaration;

styledeclaration: propertyname COLON expression * SEMICOLON;
variabledeclaration: variablereference ASSIGNMENT_OPERATOR value SEMICOLON;


variablereference: CAPITAL_IDENT;
propertyname: LOWER_IDENT;


value: colorLiteral
    | pixelLiteral
    | percentageLiteral
    | scalarLiteral
    | boolLiteral
    | variablereference;


boolLiteral: TRUE | FALSE;
colorLiteral: COLOR;
percentageLiteral: PERCENTAGE;
pixelLiteral: PIXELSIZE;
scalarLiteral: SCALAR;

operators:PLUS
          | MIN
          | MUL;
expression: value (operators? value)*;


selector: selectortag
    | selectorid
    | selectorclass;

selectortag: LOWER_IDENT;
selectorid: ID_IDENT;
selectorclass: CLASS_IDENT;


//--- LEXER: ---
// IF support:
IF: 'if';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;

//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';
