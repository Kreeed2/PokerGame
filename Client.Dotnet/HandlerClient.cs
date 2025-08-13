using PokerProject.Shared;
using System;
using System.IO;
using System.Net.Sockets;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;
using System.Text.Json.Nodes;

namespace PokerProject.Client
{
    public class HandlerClient
    {
        private readonly IGameClient gameClient;
        private readonly TcpClient tcpClient;
        private NetworkStream? stream;
        private StreamReader? reader;
        private StreamWriter? writer;

        public HandlerClient(IGameClient gameClient)
        {
            this.gameClient = gameClient;
            tcpClient = new TcpClient();
        }

        public async Task ConnectAsync(string serverAddress, int port)
        {
            await tcpClient.ConnectAsync(serverAddress, port);
            stream = tcpClient.GetStream();
            reader = new StreamReader(stream, Encoding.UTF8);
            writer = new StreamWriter(stream, Encoding.UTF8) { AutoFlush = true };
        }

        public async Task StartListeningAsync()
        {
            try
            {
                while (tcpClient.Connected)
                {
                    var line = await reader.ReadLineAsync();
                    if (line != null)
                    {
                        var message = JsonSerializer.Deserialize<Message>(line);
                        ProcessMessage(message);
                    }
                }
            }
            catch (IOException)
            {
                gameClient.DisplayMessage("Disconnected from server.");
            }
            finally
            {
                Close();
            }
        }

        private void ProcessMessage(Message message)
        {
            switch (message.Header)
            {
                case "NAMEADD":
                    gameClient.RequestNameAndPassword();
                    break;
                case "NAMEACCEPT":
                    if (!(message.Payload is JsonElement je && je.ValueKind == JsonValueKind.True))
                    {
                        gameClient.ShowNameTakenError();
                    }
                    break;
                case "PASSACCEPT":
                    if (!(message.Payload is JsonElement je2 && je2.ValueKind == JsonValueKind.True))
                    {
                        gameClient.ShowWrongPasswordError();
                    }
                    break;
                case "REMOVE":
                    gameClient.Exit();
                    break;
                case "PLAYERCHIPS":
                    // ... and so on for all message types
                    gameClient.DisplayMessage($"Received PLAYERCHIPS: {message.Payload}");
                    break;
                case "POT":
                    if (message.Payload is JsonElement jePot && jePot.TryGetInt32(out int pot))
                    {
                        gameClient.UpdatePot(pot);
                    }
                    break;
                case "BETSET":
                    gameClient.SetBetRequest(message.Payload.ToString());
                    break;
                case "MESSAGE":
                    gameClient.DisplayMessage(message.Payload.ToString());
                    break;
                // Add cases for all other message types from the original HandlerClient.java
                default:
                    gameClient.DisplayMessage($"Unknown message header: {message.Header}");
                    break;
            }
        }

        public async Task SendDataAsync(string header, object payload)
        {
            try
            {
                var message = new Message(header, payload);
                var json = JsonSerializer.Serialize(message);
                await writer.WriteLineAsync(json);
            }
            catch (IOException)
            {
                gameClient.DisplayMessage("Error sending data to server.");
            }
        }

        public void Close()
        {
            tcpClient.Close();
        }
    }
}
