using System;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Threading.Tasks;

namespace PokerProject.Server
{
    public class Server
    {
        private const int Port = 25565;
        private const int RequiredPlayers = 2;
        private const int MaxRounds = 5;

        public static async Task Main(string[] args)
        {
            var server = new Server();
            await server.StartAsync();
        }

        public async Task StartAsync()
        {
            Console.WriteLine("Starting server...");
            var listener = new TcpListener(IPAddress.Any, Port);
            listener.Start();
            Console.WriteLine($"Server started on port {Port}");

            try
            {
                var table = new Table();
                table.GetCardStack().FillStack();

                while (table.PlayerAmount() < RequiredPlayers)
                {
                    Console.WriteLine("Waiting for a new player...");
                    var clientSocket = await listener.AcceptSocketAsync();
                    Console.WriteLine("Player connected.");
                    var player = new Player(clientSocket, table);
                    table.AddPlayer(player);
                }

                Console.WriteLine("All players connected. Waiting for names...");

                while (table.AllPlayersFinished())
                {
                    await Task.Delay(1000);
                }

                Console.WriteLine("All players have names. Saving user data...");
                foreach (var p in table.GetPlayers())
                {
                    await UserDataManager.AddUserAsync(p.Name, p.Pass);
                }

                Console.WriteLine("Starting game...");
                while (table.GetRoundCounter() < MaxRounds)
                {
                    await table.NextTurnAsync();
                }
            }
            finally
            {
                listener.Stop();
                Console.WriteLine("Server stopped.");
            }
        }
    }
}
