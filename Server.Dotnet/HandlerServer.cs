using PokerProject.Shared;
using System;
using System.IO;
using System.Net.Sockets;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;
using System.Linq;
using System.Collections.Generic;

namespace PokerProject.Server
{
    public class HandlerServer
    {
        private readonly Socket socket;
        private readonly Player player;
        private readonly NetworkStream stream;
        private readonly StreamReader reader;
        private readonly StreamWriter writer;

        private TaskCompletionSource<int> betTaskCompletionSource;

        public HandlerServer(Socket socket, Player player)
        {
            this.socket = socket;
            this.player = player;
            stream = new NetworkStream(socket);
            reader = new StreamReader(stream, Encoding.UTF8);
            writer = new StreamWriter(stream, Encoding.UTF8) { AutoFlush = true };
        }

        public async Task StartListeningAsync()
        {
            try
            {
                while (socket.Connected)
                {
                    var line = await reader.ReadLineAsync();
                    if (line != null)
                    {
                        var message = JsonSerializer.Deserialize<Message>(line);
                        await ProcessMessageAsync(message);
                    }
                }
            }
            catch (IOException)
            {
                // Client disconnected
            }
            finally
            {
                Close();
            }
        }

        private async Task ProcessMessageAsync(Message message)
        {
            switch (message.Header)
            {
                case "NAME":
                    // Payload is a string
                    var name = message.Payload.ToString();
                    if (!string.IsNullOrEmpty(name))
                    {
                        await SendDataAsync("NAMEACCEPT", true);
                        player.SetName(name);
                        Console.WriteLine($"Name changed to {name}");
                    }
                    break;

                case "NAMEPASS":
                    // Payload is a string "name:pass"
                    var namePass = message.Payload.ToString();
                    var parts = namePass.Split(':');
                    if (parts.Length == 2)
                    {
                        var username = parts[0];
                        var password = parts[1];
                        if (await IsNameAcceptedAsync(username))
                        {
                            await SendDataAsync("NAMEACCEPT", true);
                            if (await IsPassAcceptedAsync(password, username))
                            {
                                await SendDataAsync("PASSACCEPT", true);
                                player.SetNamePass(username, password);
                                Console.WriteLine($"Name changed to {username}");
                            }
                            else
                            {
                                await SendDataAsync("PASSACCEPT", false);
                            }
                        }
                        else
                        {
                            await SendDataAsync("NAMEACCEPT", false);
                        }
                    }
                    else
                    {
                        await SendDataAsync("NAMEACCEPT", false);
                    }
                    break;

                case "BETGIVEN":
                    // Payload is a string representing an int
                    if (int.TryParse(message.Payload.ToString(), out int bet))
                    {
                        betTaskCompletionSource?.TrySetResult(bet);
                    }
                    break;

                default:
                    await SendDataAsync("UNKNOWN", null);
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
                // Handle error
            }
        }

        public Task<int> GetBetAsync()
        {
            betTaskCompletionSource = new TaskCompletionSource<int>();
            return betTaskCompletionSource.Task;
        }

        public void Close()
        {
            socket.Close();
        }

        private async Task<bool> IsNameAcceptedAsync(string name)
        {
            return await UserDataManager.IsNameAcceptedAsync(name, player.GetTable().GetPlayers());
        }

        private async Task<bool> IsPassAcceptedAsync(string pass, string name)
        {
            return await UserDataManager.IsPassAcceptedAsync(name, pass);
        }
    }
}
