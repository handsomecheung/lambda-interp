public class LambdaInterpException extends RuntimeException {
    private static final long serialVersionUID = -8654699250481524590L;
    public LambdaInterpException() {
        super();
    }
    public LambdaInterpException(String msg) {
        super(msg);
    }
}

class IllInterpException extends LambdaInterpException {
    private static final long serialVersionUID = -8674501232897615142L;
    public IllInterpException() {
        super();
    }
    public IllInterpException(String msg) {
        super(msg);
    }
}

class IllTokenException extends LambdaInterpException {
    private static final long serialVersionUID = 1415981412922061572L;
    public IllTokenException() {
        super();
    }
    public IllTokenException(String msg) {
        super(msg);
    }
}

class IllSyntaxException extends LambdaInterpException {
    private static final long serialVersionUID = 9053892733742415984L;
    public IllSyntaxException() {
        super();
    }
    public IllSyntaxException(String msg){
        super(msg);
    }
}
