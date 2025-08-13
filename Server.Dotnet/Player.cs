using PokerProject.Shared;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Sockets;

namespace PokerProject.Server
{
    public class Player
    {
        private readonly Stack hand = new Stack();
        public HandlerServer HandlerServer { get; }
        public Role CurrentRole { get; set; } = Role.DEFAULT;
        private HandResult handResult;
        public bool IsInRound { get; private set; } = true;
        public int PlayerPot { get; private set; } = 0;
        private Table table;

        public string Name { get; private set; } = "ERROR";
        public string Pass { get; private set; } = "ERROR";
        public int Chips { get; private set; } = 10000;

        public event Action<string> PlayerNameChanged;

        public Player(Socket socket, Table table)
        {
            this.table = table;
            HandlerServer = new HandlerServer(socket, this);
            _ = HandlerServer.StartListeningAsync(); // Fire and forget
            _ = HandlerServer.SendDataAsync("NAMEADD", null);
        }

        public void Close()
        {
            Console.WriteLine($"Player {Name} is being removed.");
            _ = HandlerServer.SendDataAsync("REMOVE", null);
            HandlerServer.Close();
        }

        public void SetName(string name)
        {
            this.Name = name;
            PlayerNameChanged?.Invoke(name);
        }

        public void SetNamePass(string name, string pass)
        {
            this.Name = name;
            this.Pass = pass;
            PlayerNameChanged?.Invoke(name);
        }

        public Table GetTable()
        {
            return table;
        }

        public void AddChips(int amount)
        {
            Chips += amount;
        }

        public int SubtractPlayerPot(int amount)
        {
            if (PlayerPot >= amount)
            {
                PlayerPot -= amount;
                return amount;
            }
            else
            {
                int outVal = PlayerPot;
                PlayerPot = 0;
                return outVal;
            }
        }

        public void ClearHand()
        {
            hand.Clear();
        }

        public void AddCardToHand(Card card)
        {
            hand.Add(card);
        }

        public bool PlaceBet(int amount, Table table)
        {
            //fold
            if (amount < 0)
            {
                IsInRound = false;
                return true;
            }
            //enough chips and raise/call/check
            if (amount <= Chips)
            {
                if (table.MaxBet <= amount + PlayerPot)
                {
                    PlayerPot += amount;
                    Chips -= amount;
                    table.MaxBet = PlayerPot;
                } //Allin
                else if (Chips == amount)
                {
                    PlayerPot += amount;
                    Chips -= amount;
                }
                else
                    return false;
                return true;
            }
            else
                return false;
        }

        private List<Card> GetCompleteHand(List<Card> openCards)
        {
            return hand.Cards.Concat(openCards).ToList();
        }

        public HandResult GetHandResult(List<Card> openCards)
        {
            if (handResult == null)
            {
                var completeHand = GetCompleteHand(openCards);
                handResult = HandEvaluator.Evaluate(completeHand);
            }
            return handResult;
        }

        public void ResetForNextRound()
        {
            handResult = null;
            IsInRound = true;
            hand.Clear();
            CurrentRole = Role.DEFAULT;
        }

        public Stack GetHand()
        {
            return hand;
        }

        public void LeaveRound()
        {
            IsInRound = false;
        }
    }
}
