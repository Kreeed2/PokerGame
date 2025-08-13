using System;
using System.Threading.Tasks;

namespace PokerProject.Client
{
    public class GameClient : IGameClient
    {
        private HandlerClient? handlerClient;
        private bool isBetRequested = false;

        public static async Task Main(string[] args)
        {
            if (args.Length > 0 && args[0] == "test")
            {
                var testClient = new TestClient($"TestUser{DateTime.Now.Ticks}", "pass", 10);
                await testClient.RunAsync();
            }
            else
            {
                var gameClient = new GameClient();
                await gameClient.RunAsync();
            }
        }

        public async Task RunAsync()
        {
            handlerClient = new HandlerClient(this);

            Console.Write("Enter server IP address: ");
            var ip = Console.ReadLine();
            if (string.IsNullOrEmpty(ip))
            {
                ip = "127.0.0.1";
            }

            try
            {
                await handlerClient.ConnectAsync(ip, 25565);
                Console.WriteLine("Connected to server.");
            }
            catch (Exception e)
            {
                Console.WriteLine($"Failed to connect: {e.Message}");
                return;
            }

            _ = handlerClient.StartListeningAsync();

            await HandleUserInputAsync();
        }

        private async Task HandleUserInputAsync()
        {
            while (true)
            {
                var input = await Task.Run(() => Console.ReadLine());
                if (isBetRequested)
                {
                    await handlerClient.SendDataAsync("BETGIVEN", input);
                    isBetRequested = false;
                }
            }
        }

        public void DisplayMessage(string message)
        {
            Console.WriteLine(message);
        }

        public void SetBetRequest(string message)
        {
            Console.WriteLine(message);
            isBetRequested = true;
        }

        public void RequestNameAndPassword()
        {
            Console.Write("Enter name: ");
            var name = Console.ReadLine();
            Console.Write("Enter password: ");
            var pass = Console.ReadLine();
            _ = handlerClient.SendDataAsync("NAMEPASS", $"{name}:{pass}");
        }

        public void ShowNameTakenError()
        {
            Console.WriteLine("Name is already taken. Please try again.");
            RequestNameAndPassword();
        }

        public void ShowWrongPasswordError()
        {
            Console.WriteLine("Wrong password. Please try again.");
            RequestNameAndPassword();
        }

        public void Exit()
        {
            Console.WriteLine("Server requested disconnect. Exiting.");
            Environment.Exit(0);
        }

        // Implement other IGameClient methods to display game state
        public void UpdatePlayerChips(string playerName, int chips) => Console.WriteLine($"[State] {playerName} has {chips} chips.");
        public void UpdatePot(int pot) => Console.WriteLine($"[State] Pot is now {pot}.");
        public void UpdatePlayerRole(string playerName, string role) => Console.WriteLine($"[State] {playerName}'s role is {role}.");
        public void AddHandCard(string card) => Console.WriteLine($"[Your Hand] You received: {card}");
        public void AddOpenCard(string card) => Console.WriteLine($"[Table] Open card: {card}");
        public void UpdatePlayerBet(string playerName, int bet) => Console.WriteLine($"[State] {playerName} bets {bet}.");
        public void UpdatePlayerInRound(string playerName, bool inRound) => Console.WriteLine($"[State] {playerName} is {(inRound ? "in" : "out of")} the round.");
        public void StartNewRound() => Console.WriteLine("\n--- NEW ROUND ---");
    }
}
