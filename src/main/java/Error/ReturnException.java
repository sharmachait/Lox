package Error;

public class ReturnException extends RuntimeException {
    public final Object val;
    public ReturnException(Object val){
        super(null, null, false, false);
        this.val=val;
    }
}
