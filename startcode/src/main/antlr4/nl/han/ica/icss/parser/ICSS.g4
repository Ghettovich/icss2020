grammar ICSS;


//--- PARSER: ---

stylesheet: declarationRule * EOF;


declarationRule: variableassignment | stylerule ;


stylerule: selector
            OPEN_BRACE
                declarations *
            CLOSE_BRACE
            ;

declarations: variableassignment | styledeclaration ;

styledeclaration: propertyname COLON expression * SEMICOLON;

variableassignment: variablereference ASSIGNMENT_OPERATOR expression SEMICOLON;

expression: value (operators? value)*;

value
    : colorLiteral
    | pixelLiteral
    | percentageLiteral
    | scalarLiteral
    | boolLiteral
    | variablereference;

operators
    : addOperator
    | substractOperator
    | multiplyOperator;

selector
    : selectortag
    | selectorid
    | selectorclass;


variablereference: CAPITAL_IDENT;
propertyname: LOWER_IDENT;

//operators
addOperator:PLUS;
substractOperator:MIN;
multiplyOperator:MUL;

//selectors
selectortag: LOWER_IDENT;
selectorid: ID_IDENT;
selectorclass: CLASS_IDENT;

//literals
boolLiteral: TRUE | FALSE;
colorLiteral: COLOR;
percentageLiteral: PERCENTAGE;
pixelLiteral: PIXELSIZE;
scalarLiteral: SCALAR;


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
