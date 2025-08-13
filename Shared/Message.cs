namespace PokerProject.Shared
{
    public class Message
    {
        public string Header { get; set; } = "";
        public object Payload { get; set; } = new object();

        public Message() { }

        public Message(string header, object payload)
        {
            Header = header;
            Payload = payload;
        }
    }
}
