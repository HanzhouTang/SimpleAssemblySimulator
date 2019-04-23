package assembler;

/**
 * Enum type for different lexemes.
 */
public enum Token {
    String, Comma, Number, DotString, Colon, LeftSquareBracket,
    RightSquareBracket, Add, Sub, Mul, Div, NewLine, Invalid,
    LeftParent, RightParent, DollarSign,EndofContent,Quote
}
