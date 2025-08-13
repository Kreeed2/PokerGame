using System.Collections.Generic;

namespace PokerProject.Shared
{
    public class HandResult
    {
        public HandRank HandRank { get; set; }
        public List<Card> WinningCards { get; set; } = new List<Card>();
    }
}
