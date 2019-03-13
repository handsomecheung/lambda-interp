import java.util.Arrays;

class Match {
    public ExprS expr;
    public Token[] tokens;
    public Match(ExprS expr, Token[] tokens) {
        this.expr = expr;
        this.tokens = tokens;
    }
}

public class Parser {
    // Term  => Atom
    //        | Term Atom
    private static Match parseTerm(Token[] tokens) {
        Match matchedAtom = parseAtom(tokens);
        return parseTerm(matchedAtom.expr, matchedAtom.tokens);
    }

    private static Match parseTerm(ExprS expr, Token[] tokens) {
        try {
            Match matchedAtom = parseAtom(tokens);
            return parseTerm(new ApplyS(expr, matchedAtom.expr), matchedAtom.tokens);
        } catch (IllSyntaxException e) {
            return new Match(expr, tokens);
        }
    }

    // Atom  => % Args . Term
    //        | ( Term )
    //        | Id
    private static Match parseAtom(Token[] tokens) {
        if (tokens.length == 0) {
            throw new IllSyntaxException("expect a atom here");
        }
        if (tokens[0].value.equals("%")) {
            Match matchedArgsS = parseArgsS(Arrays.copyOfRange(tokens, 1, tokens.length));
            if (matchedArgsS.tokens.length == 0 || !matchedArgsS.tokens[0].value.equals(".")) {
                throw new IllSyntaxException("expect symbol '.' here");
            }
            Match matchedTerm = parseTerm(Arrays.copyOfRange(matchedArgsS.tokens, 1, matchedArgsS.tokens.length));

            LambdaS lambda;
            if (matchedArgsS.expr instanceof EmptyS) {
                lambda = new LambdaS(matchedTerm.expr);
            } else {
                lambda = new LambdaS(matchedArgsS.expr, matchedTerm.expr);
            }
            return new Match(lambda, matchedTerm.tokens);
        } else if (tokens[0].value.equals("(")) {
            Match matchedTerm = parseTerm(Arrays.copyOfRange(tokens, 1, tokens.length));
            if (matchedTerm.tokens.length == 0 || !matchedTerm.tokens[0].value.equals(")")) {
                throw new IllSyntaxException("expect symbol ')' here");
            }
            return new Match(matchedTerm.expr, Arrays.copyOfRange(matchedTerm.tokens, 1, matchedTerm.tokens.length));
        } else {
            return parseId(tokens);
        }
    }

    // Args  => Id Args
    //        | Empty
    private static Match parseArgsS(Token[] tokens) {
        try {
            ArgsS expr;
            Match matchedId = parseId(tokens);
            Match matchedArgsS = parseArgsS(matchedId.tokens);
            if (matchedArgsS.expr instanceof EmptyS) {
                expr = new ArgsS(matchedId.expr);
            } else {
                expr = new ArgsS(matchedId.expr, matchedArgsS.expr);
            }
            return new Match(expr, matchedArgsS.tokens);
        } catch (IllSyntaxException e) {
            return new Match(new EmptyS(), tokens);
        }
    }

    private static Match parseId(Token[] tokens) {
        if (tokens.length > 0 && tokens[0] instanceof Id) {
            VarS var = new VarS(tokens[0].value);
            return new Match(var, Arrays.copyOfRange(tokens, 1, tokens.length));
        } else {
            throw new IllSyntaxException("expect Id here");
        }
    }

    public static ExprS parser(Token[] tokens) {
        Match matched = parseTerm(tokens);
        if (matched.tokens.length > 0) {
            String remain = "";
            for(Token t: matched.tokens){
                remain = remain + t.value;
            }
            throw new IllSyntaxException("invalid string: " + remain);
        }
        return matched.expr;
    }
}
