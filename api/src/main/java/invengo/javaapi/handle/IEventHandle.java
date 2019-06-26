package invengo.javaapi.handle;

public interface IEventHandle {

	void eventHandle_executed(Object sender,EventArgs e);

	void eventHandle_executing(Object sender,EventArgs e);
}
