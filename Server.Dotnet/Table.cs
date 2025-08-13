using PokerProject.Shared;
using System;
using System.Collections.Generic;
using System.Linq;

namespace PokerProject.Server
{
    public class Table
    {
        private readonly Stack cardStack = new Stack();
        private readonly List<Player> players = new List<Player>();
        private readonly Stack openCards = new Stack();

        // private HashSet<VarObserver> observers = new HashSet<>(); // To be replaced with events

        public int MaxBet { get; set; } = 0;
        private int roundCounter = 0;
        private int turnCounter = 0;
        private int dealerPos;

        public bool AllPlayersFinished()
        {
            return players.Any(player => player.Name == "ERROR");
        }

        public int PlayerAmount()
        {
            return players.Count;
        }

        public Stack GetCardStack()
        {
            return cardStack;
        }

        public int GetRoundCounter()
        {
            return roundCounter;
        }

        private Stack GetOpenCards()
        {
            return openCards;
        }

        public List<Player> GetPlayers()
        {
            return players;
        }

        public void AddPlayer(Player p)
        {
            players.Add(p);
            // VarObserver obs = new VarObserver(this);
            // p.addObserver(obs);
            // observers.add(obs);
        }

        private void RemovePlayer(Player p)
        {
            if (p.CurrentRole == Role.DEALER)
                dealerPos--;

            // sendToHandler(p, "REMOVE", null);
            players.Remove(p);
        }

        private List<Player> DecideWinner()
        {
            var comparePlayers = players.Where(p => p.IsInRound).ToList();
            if (comparePlayers.Count <= 1)
            {
                return comparePlayers;
            }

            // Get hand results for all players
            foreach (var p in comparePlayers)
            {
                p.GetHandResult(openCards.Cards);
            }

            var bestHandRank = comparePlayers.Max(p => p.GetHandResult(openCards.Cards).HandRank);
            var potentialWinners = comparePlayers.Where(p => p.GetHandResult(openCards.Cards).HandRank == bestHandRank).ToList();

            if (potentialWinners.Count == 1)
            {
                return potentialWinners;
            }

            // Handle ties by comparing high cards
            // This is a simplified tie-breaking rule
            var maxCardValue = potentialWinners.Max(p => p.GetHandResult(openCards.Cards).WinningCards.Max(c => c.Value));
            return potentialWinners.Where(p => p.GetHandResult(openCards.Cards).WinningCards.Max(c => c.Value) == maxCardValue).ToList();
        }

        private void DistributePot(List<Player> winners)
        {
            if (winners == null || !winners.Any()) return;

            int totalPot = players.Sum(p => p.PlayerPot);
            int potPerWinner = totalPot / winners.Count;

            foreach(var winner in winners)
            {
                winner.AddChips(potPerWinner);
            }

            foreach(var player in players)
            {
                player.SubtractPlayerPot(player.PlayerPot); // Clear player pots
            }
        }

        public async Task NextTurnAsync()
        {
            // turnMessage();

            if (turnCounter == 0)
            {
                DistributeCards();
                AssignRole();
                // roleMessage();
                await PreFlopAsync();
            }
            else if (turnCounter > 3 || CountActivePlayers() == 1)
            {
                List<Player> winners = DecideWinner();
                if(winners != null && winners.Any())
                {
                    Console.WriteLine($"Winner is {string.Join(", ", winners.Select(w => w.Name))}");
                    DistributePot(winners);
                }
                NextRound();
                return;
            }
            else
            {
                DistributeCards();
                await PlayerBetAsync();
            }
            turnCounter++;

            Console.WriteLine("-----------------------------------------------------------");
        }

        private void NextRound()
        {
            openCards.Clear();

            players.Where(player => player.Chips == 0).ToList().ForEach(RemovePlayer);
            players.ForEach(p => p.ResetForNextRound());

            MaxBet = 0;
            turnCounter = 0;
            roundCounter++;
            // broadcastToPlayers("NEXTROUND", null);
            // inRoundMessage();

            // Dictionary<string, int> playerBet = new Dictionary<string, int>();
            // foreach (Player p in players)
            // {
            //     playerBet.Add(p.Name, p.PlayerPot);
            // }
            // broadcastToPlayers("BET", playerBet);
        }

        private async Task PreFlopAsync()
        {
            int firstDefault = dealerPos;
            int blind = 100;

            if (players.Count > 2)
            {
                players[(dealerPos + 1) % players.Count].PlaceBet(blind / 2, this);
                players[(dealerPos + 2) % players.Count].PlaceBet(blind, this);
                firstDefault = (dealerPos + 3) % players.Count;
            }
            else
            {
                players[dealerPos % players.Count].PlaceBet(blind / 2, this);
                players[(dealerPos + 1) % players.Count].PlaceBet(blind, this);
            }

            // foreach (Player p in players)
            // {
            //     betMessage(p.Name, p.PlayerPot);
            // }

            for (int j = firstDefault, i = 0; i < players.Count; i++, j = (j + 1) % players.Count)
            {
                while (players[j].IsInRound && CountActivePlayers() > 1)
                {
                    int bet = await GetUserInputAsync(players[j]);
                    if (players[j].PlaceBet(bet, this))
                    {
                        // foreach (Player p in players)
                        // {
                        //     betMessage(p.Name, p.PlayerPot);
                        // }
                        break;
                    }
                }
            }

            if (FixBets())
                await PlayerBetAsync();
        }

        private async Task<int> GetUserInputAsync(Player p)
        {
            await p.HandlerServer.SendDataAsync("BETSET", $"Please enter your bet ({MaxBet})");
            return await p.HandlerServer.GetBetAsync();
        }

        private async Task PlayerBetAsync()
        {
            int posSmall = dealerPos;
            if (players.Count > 2)
                posSmall = (dealerPos + 1) % players.Count;

            for (int j = posSmall, i = 0; i < players.Count && CountActivePlayers() > 1; i++, j = (j + 1) % players.Count)
            {
                while (players[j].IsInRound && CountActivePlayers() > 1)
                {
                    int bet = await GetUserInputAsync(players[j]);
                    if (players[j].PlaceBet(bet, this))
                    {
                        // foreach (Player p in players)
                        // {
                        //     betMessage(p.Name, p.PlayerPot);
                        // }
                        break;
                    }
                }
            }

            if (FixBets())
                await PlayerBetAsync();
        }

        private bool FixBets()
        {
            return players.Any(p => p.IsInRound && (p.PlayerPot != MaxBet ^ p.Chips == 0));
        }

        private int CountActivePlayers()
        {
            return players.Count(p => p.IsInRound);
        }

        private void DistributeCards()
        {
            switch (turnCounter)
            {
                case 0:
                    players.Where(p => p.IsInRound).ToList().ForEach(player =>
                    {
                        player.AddCardToHand(cardStack.Remove());
                        player.AddCardToHand(cardStack.Remove());
                    });
                    // handCardMessage();
                    break;
                case 1:
                    for (int i = 0; i < 3; i++)
                    {
                        Card newCard = cardStack.Remove();
                        openCards.Add(newCard);
                        // broadcastToPlayers("OPENCARDS", newCard);
                    }
                    break;
                default:
                    Card newCard_ = cardStack.Remove();
                    openCards.Add(newCard_);
                    // broadcastToPlayers("OPENCARDS", newCard_);
                    break;
            }
        }

        private void AssignRole()
        {
            if (roundCounter == 0)
                dealerPos = new Random().Next(players.Count);
            else
                dealerPos++;

            if (players.Count == 2)
            {
                players[dealerPos % players.Count].CurrentRole = Role.DEALERSPECIAL;
                players[(dealerPos + 1) % players.Count].CurrentRole = Role.BIG;
            }
            else
            {
                players[dealerPos % players.Count].CurrentRole = Role.DEALER;
                players[(dealerPos + 1) % players.Count].CurrentRole = Role.SMALL;
                players[(dealerPos + 2) % players.Count].CurrentRole = Role.BIG;
            }
        }
    }
}
