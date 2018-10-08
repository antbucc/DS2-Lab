package noname.epidemic.one;

public class Update{
	
    private int value;
    private String message;
    
    public Update(int _value, String _message ) {
        this.value = _value;
        this.message = _message;
    }
    
    public int getValue() {
    	return value;
    }
    
    public String getMessage() {
    	return message;
    }
}