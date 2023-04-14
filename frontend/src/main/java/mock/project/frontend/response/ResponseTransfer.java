package mock.project.frontend.response;

public class ResponseTransfer {
	private String text;
	
	public ResponseTransfer(String text) {
		super();
		this.text = text;
	}

	public ResponseTransfer() {
		super();
	}


	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
}
