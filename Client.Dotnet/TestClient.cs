using System;
using System.Threading.Tasks;

namespace PokerProject.Client
{
    public class TestClient : IGameClient
    {
        private HandlerClient handlerClient;
        private readonly string name;
        private readonly string pass;
        private readonly int betAmount;

        public TestClient(string name, string pass, int betAmount)
        {
            this.name = name;
            this.pass = pass;
            this.betAmount = betAmount;
            handlerClient = new HandlerClient(this);
        }

        public async Task RunAsync()
        {
            try
            {
                await handlerClient.ConnectAsync("127.0.0.1", 25565);
                Console.WriteLine($"[TestClient {name}] Connected to server.");
            }
            catch (Exception e)
            {
                Console.WriteLine($"[TestClient {name}] Failed to connect: {e.Message}");
                return;
            }

            await handlerClient.StartListeningAsync();
        }

        public void DisplayMessage(string message)
        {
            Console.WriteLine($"[TestClient {name}] Message: {message}");
        }

        public void SetBetRequest(string message)
        {
            Console.WriteLine($"[TestClient {name}] Bet requested: {message}");
            _ = handlerClient.SendDataAsync("BETGIVEN", betAmount.ToString());
        }

        public void RequestNameAndPassword()
        {
            Console.WriteLine($"[TestClient {name}] Name and password requested.");
            _ = handlerClient.SendDataAsync("NAMEPASS", $"{name}:{pass}");
        }

        public void ShowNameTakenError() => Console.WriteLine($"[TestClient {name}] Name taken.");
        public void ShowWrongPasswordError() => Console.WriteLine($"[TestClient {name}] Wrong password.");
        public void Exit() => Console.WriteLine($"[TestClient {name}] Server requested disconnect.");

        public void UpdatePlayerChips(string playerName, int chips) {}
        public void UpdatePot(int pot) {}
        public void UpdatePlayerRole(string playerName, string role) {}
        public void AddHandCard(string card) {}
        public void AddOpenCard(string card) {}
        public void UpdatePlayerBet(string playerName, int bet) {}
        public void UpdatePlayerInRound(string playerName, bool inRound) {}
        public void StartNewRound() {}
    }
}
