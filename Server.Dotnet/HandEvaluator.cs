using PokerProject.Shared;
using System.Collections.Generic;
using System.Linq;

namespace PokerProject.Server
{
    public static class HandEvaluator
    {
        public static HandResult Evaluate(List<Card> cards)
        {
            // There must be 5 to 7 cards to evaluate
            if (cards == null || cards.Count < 5 || cards.Count > 7)
            {
                return null;
            }

            // Generate all 5-card combinations
            var combinations = GetCombinations(cards, 5);
            var bestHand = combinations.Select(c => EvaluateHand(c.ToList()))
                                       .OrderByDescending(h => h.HandRank)
                                       .ThenByDescending(h => h.WinningCards.Max(c => c.Value))
                                       .First();

            return bestHand;
        }

        private static HandResult EvaluateHand(List<Card> hand)
        {
            // Must be a 5-card hand
            if (hand.Count != 5) return null;

            bool isFlush = hand.Select(c => c.Color).Distinct().Count() == 1;
            bool isStraight = IsStraight(hand);

            if (isStraight && isFlush)
                return new HandResult { HandRank = HandRank.StraightFlush, WinningCards = hand };

            var groups = hand.GroupBy(c => c.Value).OrderByDescending(g => g.Count()).ThenByDescending(g => g.Key).ToList();

            if (groups[0].Count() == 4)
                return new HandResult { HandRank = HandRank.FourOfAKind, WinningCards = groups[0].ToList() };

            if (groups[0].Count() == 3 && groups[1].Count() == 2)
                return new HandResult { HandRank = HandRank.FullHouse, WinningCards = hand };

            if (isFlush)
                return new HandResult { HandRank = HandRank.Flush, WinningCards = hand };

            if (isStraight)
                return new HandResult { HandRank = HandRank.Straight, WinningCards = hand };

            if (groups[0].Count() == 3)
                return new HandResult { HandRank = HandRank.ThreeOfAKind, WinningCards = groups[0].ToList() };

            if (groups[0].Count() == 2 && groups[1].Count() == 2)
                return new HandResult { HandRank = HandRank.TwoPair, WinningCards = groups[0].Concat(groups[1]).ToList() };

            if (groups[0].Count() == 2)
                return new HandResult { HandRank = HandRank.Pair, WinningCards = groups[0].ToList() };

            return new HandResult { HandRank = HandRank.HighCard, WinningCards = new List<Card> { hand.OrderByDescending(c => c.Value).First() } };
        }

        private static bool IsStraight(List<Card> hand)
        {
            var ordered = hand.OrderBy(c => c.Value).ToList();
            // Ace-low straight (A, 2, 3, 4, 5)
            if (ordered.Last().Value == CardValue.ACE &&
                ordered[0].Value == CardValue.TWO &&
                ordered[1].Value == CardValue.THREE &&
                ordered[2].Value == CardValue.FOUR &&
                ordered[3].Value == CardValue.FIVE)
                return true;

            for (int i = 0; i < ordered.Count - 1; i++)
            {
                if ((int)ordered[i + 1].Value != (int)ordered[i].Value + 1)
                {
                    return false;
                }
            }
            return true;
        }

        private static IEnumerable<IEnumerable<T>> GetCombinations<T>(IEnumerable<T> list, int length)
        {
            if (length < 0) yield break;
            if (length == 0)
            {
                yield return Enumerable.Empty<T>();
                yield break;
            }
            int i = 0;
            foreach (var item in list)
            {
                foreach (var combination in GetCombinations(list.Skip(i + 1), length - 1))
                {
                    yield return new[] { item }.Concat(combination);
                }
                i++;
            }
        }
    }
}
